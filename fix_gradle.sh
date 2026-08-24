#!/bin/sh
set -eu

ROOT_DIR=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)
cd "$ROOT_DIR"

mkdir -p gradle/wrapper

echo "Downloading official Gradle 8.13 wrapper..."
curl -fL "https://raw.githubusercontent.com/gradle/gradle/v8.13.0/gradle/wrapper/gradle-wrapper.jar" \
  -o gradle/wrapper/gradle-wrapper.jar

EXPECTED="81a82aaea5abcc8ff68b3dfcb58b3c3c429378efd98e7433460610fecd7ae45f"
ACTUAL=$(shasum -a 256 gradle/wrapper/gradle-wrapper.jar | awk '{print $1}')
if [ "$ACTUAL" != "$EXPECTED" ]; then
  echo "Wrapper checksum mismatch. Expected $EXPECTED, got $ACTUAL" >&2
  rm -f gradle/wrapper/gradle-wrapper.jar
  exit 1
fi

curl -fL "https://raw.githubusercontent.com/gradle/gradle/v8.13.0/gradlew" -o gradlew
curl -fL "https://raw.githubusercontent.com/gradle/gradle/v8.13.0/gradlew.bat" -o gradlew.bat
chmod +x gradlew

echo "Gradle wrapper installed and verified."
echo "Running ./gradlew --version ..."
./gradlew --version
