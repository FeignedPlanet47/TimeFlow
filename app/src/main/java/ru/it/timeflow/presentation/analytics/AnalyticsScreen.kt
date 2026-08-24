package ru.it.timeflow.presentation.analytics

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ru.it.timeflow.util.formatDurationCompact

@Composable
fun AnalyticsRoute(viewModel: AnalyticsViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val max = state.summaries.maxOfOrNull { it.durationMillis }?.coerceAtLeast(1L) ?: 1L

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        item {
            Text("Аналитика", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
            Text("Эта неделя · ${formatDurationCompact(state.totalMillis)}", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        items(state.summaries, key = { it.categoryId }) { item ->
            Card(shape = RoundedCornerShape(20.dp), modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.fillMaxWidth().padding(16.dp)) {
                    Row(Modifier.fillMaxWidth()) {
                        Text("${item.emoji} ${item.name}", modifier = Modifier.weight(1f), fontWeight = FontWeight.SemiBold)
                        Text(formatDurationCompact(item.durationMillis), fontWeight = FontWeight.Bold)
                    }
                    Spacer(Modifier.height(10.dp))
                    val fraction = item.durationMillis.toFloat() / max.toFloat()
                    Box(
                        Modifier
                            .fillMaxWidth(fraction.coerceIn(0.03f, 1f))
                            .height(10.dp)
                            .clip(RoundedCornerShape(99.dp))
                            .background(Color(item.colorArgb))
                    )
                }
            }
        }

        if (state.summaries.isEmpty()) {
            item { Text("Статистика появится после первой завершённой или активной записи.") }
        }
    }
}
