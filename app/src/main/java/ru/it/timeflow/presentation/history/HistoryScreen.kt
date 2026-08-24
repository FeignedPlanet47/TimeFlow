package ru.it.timeflow.presentation.history

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ru.it.timeflow.util.formatClockTime
import ru.it.timeflow.util.formatDurationCompact

@Composable
fun HistoryRoute(viewModel: HistoryViewModel = hiltViewModel()) {
    val entries by viewModel.entries.collectAsStateWithLifecycle()
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Text("История", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
            Text("Сегодняшняя временная шкала", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        items(entries, key = { it.id }) { entry ->
            Card(shape = RoundedCornerShape(20.dp), modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        Modifier.size(48.dp).background(Color(entry.categoryColorArgb).copy(alpha = .16f), CircleShape),
                        contentAlignment = Alignment.Center,
                    ) { Text(entry.categoryEmoji, fontSize = 22.sp) }
                    Spacer(Modifier.size(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text(entry.categoryName, fontWeight = FontWeight.SemiBold)
                        Text(
                            "${formatClockTime(entry.startTimeMillis)} – ${entry.endTimeMillis?.let(::formatClockTime) ?: "сейчас"}",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        entry.taskName?.let { Text(it, style = MaterialTheme.typography.bodySmall) }
                    }
                    Text(formatDurationCompact(entry.durationMillis()), fontWeight = FontWeight.Bold)
                }
            }
        }
        if (entries.isEmpty()) {
            item { Text("За сегодня пока ничего не записано.") }
        }
    }
}
