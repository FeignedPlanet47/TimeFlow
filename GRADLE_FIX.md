# Gradle fix

This project is pinned to:
- Android Gradle Plugin 8.13.2
- Gradle 8.13
- Kotlin 2.3.21
- JDK 17
- compileSdk 36
- Compose BOM 2026.06.00

On macOS/Linux run once from the project root:

```bash
./fix_gradle.sh
```

Then in Android Studio select JDK 17 for Gradle and Sync Project with Gradle Files.

If Android Studio cached the old AGP 9.3 configuration, close Android Studio and delete only the project's `.gradle` directory, then reopen the project.
