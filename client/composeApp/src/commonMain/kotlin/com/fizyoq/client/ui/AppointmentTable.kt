package com.fizyoq.client.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.fizyoq.client.model.Appointment


@Composable
fun AppointmentTable(
    appointments: List<Appointment>,
    isAdmin: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // --- BAŞLIK SATIRI (HEADER) ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primary)
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TableCell(text = "Saat", weight = 1f, isHeader = true)
            TableCell(text = "Hasta Adı", weight = 2f, isHeader = true)
            TableCell(text = "Tedavi", weight = 2f, isHeader = true)

            if (isAdmin) {
                TableCell(text = "Notlar", weight = 2f, isHeader = true)
            }
        }

        // --- VERİ LİSTESİ ---
        LazyColumn {
            items(appointments) { appointment ->
                AppointmentRow(appointment = appointment, isAdmin = isAdmin)
                HorizontalDivider()
            }
        }
    }
}

@Composable
fun AppointmentRow(appointment: Appointment, isAdmin: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TableCell(text = appointment.timeSlot, weight = 1f)
        TableCell(text = appointment.patientName, weight = 2f)
        TableCell(text = appointment.treatmentType, weight = 2f)

        if (isAdmin) {
            TableCell(text = appointment.notes ?: "-", weight = 2f)
        }
    }
}

@Composable
fun RowScope.TableCell(
    text: String,
    weight: Float,
    isHeader: Boolean = false
) {
    Text(
        text = text,
        modifier = Modifier.weight(weight),
        color = if (isHeader) Color.White else Color.Black,
        fontWeight = if (isHeader) FontWeight.Bold else FontWeight.Normal,
        style = if (isHeader) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyMedium
    )
}