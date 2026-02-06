package com.example.paymyfine.screens.home


import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.paymyfine.data.fines.IForceItem

@Composable
fun IndividualFinesList(
    fines: List<IForceItem>,
    onFineClick: (IForceItem) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp)
    ) {
        items(
            items = fines,
            key = { it.noticeNumber ?: it.hashCode() }
        ) { fine ->
            FineRow(
                fine = fine,
                onClick = { onFineClick(fine) }
            )
        }
    }
}
