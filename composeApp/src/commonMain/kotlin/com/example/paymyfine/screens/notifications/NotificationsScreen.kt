package com.example.paymyfine.screens.notifications

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.paymyfine.data.notification.NotificationDto
import com.example.paymyfine.data.notification.NotificationsState
import com.example.paymyfine.data.notification.NotificationsViewModel
import com.example.paymyfine.util.DateFormatter
import kotlinx.coroutines.launch

@Composable
fun NotificationsScreen(
    vm: NotificationsViewModel
) {
    val state by vm.state.collectAsState()
    val scope = rememberCoroutineScope()

    var selectedItem by remember { mutableStateOf<NotificationDto?>(null) }

    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        SegmentedToggle(
            onInbox = { scope.launch { vm.switchTab(true) } },
            onHistory = { scope.launch { vm.switchTab(false) } }
        )

        Spacer(Modifier.height(12.dp))

        when (val s = state) {

            is NotificationsState.Loading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            is NotificationsState.Error -> {
                Text("Error loading notifications")
            }

            is NotificationsState.Data -> {

                if (s.items.isEmpty()) {
                    EmptyState(s.isInbox)
                } else {
                    NotificationsList(
                        items = s.items,
                        onClick = { selectedItem = it }
                    )
                }
            }
        }
    }

    // Dialog
    selectedItem?.let { item ->
        NotificationDialog(
            item = item,
            onMarkRead = {
                scope.launch { vm.markRead(item.id) }
                selectedItem = null
            },
            onClose = { selectedItem = null }
        )
    }
}

@Composable
fun SegmentedToggle(
    onInbox: () -> Unit,
    onHistory: () -> Unit
) {
    var selected by remember { mutableStateOf(0) }

    Row(
        Modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(Color(0xFFE0E0E0))
            .padding(4.dp)
    ) {

        SegmentTab(
            text = "Notifications",
            selected = selected == 0
        ) {
            selected = 0
            onInbox()
        }

        SegmentTab(
            text = "History",
            selected = selected == 1
        ) {
            selected = 1
            onHistory()
        }
    }
}

@Composable
private fun RowScope.SegmentTab(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Box(
        Modifier
            .weight(1f)
            .fillMaxHeight()
            .clip(RoundedCornerShape(20.dp))
            .background(if (selected) MaterialTheme.colorScheme.primary else Color.Transparent)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text,
            color = if (selected) Color.White else Color.Gray,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
fun NotificationsList(
    items: List<NotificationDto>,
    onClick: (NotificationDto) -> Unit
) {
    LazyColumn {
        items(items) { item ->
            NotificationCard(
                item = item,
                onClick = { onClick(item) }
            )
        }
    }
}

@Composable
fun EmptyState(isInbox: Boolean) {
    Box(
        Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            if (isInbox) "No notifications"
            else "No payment history yet"
        )
    }
}

@Composable
fun NotificationCard(
    item: NotificationDto,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        elevation = CardDefaults.cardElevation(3.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        Row(Modifier.padding(14.dp)) {

            // 🔴 Unread dot
            if (!item.isRead) {
                Box(
                    Modifier
                        .size(8.dp)
                        .background(Color.Red, CircleShape)
                )
                Spacer(Modifier.width(8.dp))
            }

            Column {

                // ⭐ Title
                Text(
                    item.title,
                    fontWeight =
                        if (!item.isRead) FontWeight.Bold
                        else FontWeight.Normal
                )

                Spacer(Modifier.height(4.dp))

                // ⭐ Message
                Text(item.message)

                Spacer(Modifier.height(4.dp))

                // ⭐ Notice number
                item.noticeNumber?.let {
                    Text(
                        it,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF0A4EFF)

                    )
                    Spacer(Modifier.height(4.dp))
                }

                // ⭐ Date
                Text(
                    DateFormatter.format(item.createdAt),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )

                // ⭐ Paid label for history
                if (item.type == "FINE_PAID") {
                    Spacer(Modifier.height(4.dp))

                    Text(
                        "Paid on ${DateFormatter.format(item.createdAt)}",
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF0A4EFF)
                    )
                }
            }
        }
    }
}


@Composable
fun NotificationDialog(
    item: NotificationDto,
    onMarkRead: () -> Unit,
    onClose: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onClose,
        confirmButton = {
            TextButton(onClick = onMarkRead) {
                Text("Mark as read")
            }
        },
        dismissButton = {
            TextButton(onClick = onClose) {
                Text("Close")
            }
        },
        title = { Text(item.title) },
        text = {
            Column {
                Text(item.message)
                item.noticeNumber?.let {
                    Text("Fine: $it")
                }
                Text(item.createdAt)
            }
        }
    )
}


