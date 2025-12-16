package com.fizyoq.client.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.fizyoq.client.model.Appointment
import com.fizyoq.client.viewmodel.AppointmentViewModel


val PHYSIOTHERAPISTS = listOf("Fzt. Ahmet", "Fzt. Ayşe", "Fzt. Mehmet", "Fzt. Zeynep")
val TIME_SLOTS = listOf("09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00")

@Composable
fun AppointmentTable(
    viewModel: AppointmentViewModel,
    isAdmin: Boolean
) {
    val appointments = if (isAdmin) {
        viewModel.appointments
    }
    else {
        viewModel.getPatientDisplayList()
    }

    var showDialog by remember { mutableStateOf(false) }
    var selectedFzt by remember { mutableStateOf("") }
    var selectedTime by remember { mutableStateOf("") }

    if (showDialog) {
        AddPatientDialog(
            physiotherapist = selectedFzt,
            timeSlot = selectedTime,
            onDismiss = { showDialog = false },
            onConfirm = { patientName ->
                // ViewModel'e kaydet emri ver
                viewModel.addAppointment(patientName, selectedFzt, selectedTime)
                showDialog = false
            }
        )
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {

        // --- BAŞLIK SATIRI ---
        Row(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .width(150.dp)
                    .height(50.dp)
                    .border(1.dp, Color.Gray)
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("Fizyoterapist", fontWeight = FontWeight.Bold)
            }

            // Saat Başlıkları
            TIME_SLOTS.forEach { time ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                        .border(1.dp, Color.Gray)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(time, fontWeight = FontWeight.Bold)
                }
            }
        }

        // --- TABLO GÖVDESİ ---
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(PHYSIOTHERAPISTS) { fzt ->
                Row(modifier = Modifier.fillMaxWidth()) {

                    Box(
                        modifier = Modifier
                            .width(150.dp)
                            .height(80.dp)
                            .border(1.dp, Color.LightGray)
                            .padding(8.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Text(fzt, fontWeight = FontWeight.Bold)
                    }

                    // Randevu Hücreleri
                    TIME_SLOTS.forEach { time ->
                        val appointment = appointments.find { it.physiotherapist == fzt && it.timeSlot == time }

                        AppointmentCell(
                            appointment = appointment,
                            isAdmin = isAdmin,
                            modifier = Modifier
                                .weight(1f)
                                .height(80.dp),
                            onClick = {
                                if (isAdmin && appointment == null) {
                                    selectedFzt = fzt
                                    selectedTime = time
                                    showDialog = true
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AppointmentCell(
    appointment: Appointment?,
    isAdmin: Boolean,
    modifier: Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .border(0.5.dp, Color.LightGray)
            .clickable(enabled = isAdmin) { onClick() }
            .padding(4.dp),
        contentAlignment = Alignment.Center
    ) {
        if (appointment != null) {
            // Dolu Randevu Kartı
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (appointment.status == "Tamamlandı") Color(0xFFE8F5E9) else MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier.fillMaxSize()
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = appointment.patientName,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = if (appointment.status == "Tamamlandı") Color.Black else Color.White
                    )
                }
            }
        } else {
            // Boş Hücre (Sadece Admin'e + göster)
            if (isAdmin) {
                Text("+", color = Color.LightGray, style = MaterialTheme.typography.headlineSmall)
            }
        }
    }
}

@Composable
fun AddPatientDialog(
    physiotherapist: String,
    timeSlot: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var name by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        onConfirm(name)
                    }
                }
            ) {
                Text("Kaydet")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("İptal")
            }
        },
        title = { Text("Yeni Randevu") },
        text = {
            Column {
                Text("$physiotherapist için saat $timeSlot randevusu.")
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Hasta Adı Soyadı") },
                    singleLine = true
                )
            }
        }
    )
}