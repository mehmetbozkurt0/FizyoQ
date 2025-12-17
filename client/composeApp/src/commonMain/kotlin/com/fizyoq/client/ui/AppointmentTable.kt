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

// Sabit Tanımlar
val PHYSIOTHERAPISTS = listOf("Fzt. Ahmet", "Fzt. Ayşe", "Fzt. Mehmet", "Fzt. Zeynep")
val TIME_SLOTS = listOf("09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00", "18:00")

@Composable
fun AppointmentTable(
    viewModel: AppointmentViewModel,
    isAdmin: Boolean
) {
    val appointments = if (isAdmin) viewModel.appointments else viewModel.getPatientDisplayList()

    // --- STATE ---
    var showAddDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }

    // Seçilen hücre bilgileri
    var selectedFzt by remember { mutableStateOf("") }
    var selectedTime by remember { mutableStateOf("") }
    var selectedAppointment by remember { mutableStateOf<Appointment?>(null) }

    // 1. EKLEME PENCERESİ (Boş yere tıklayınca)
    if (showAddDialog) {
        AddPatientDialog(
            physiotherapist = selectedFzt,
            timeSlot = selectedTime,
            onDismiss = { showAddDialog = false },
            onConfirm = { patientName ->
                viewModel.addAppointment(patientName, selectedFzt, selectedTime)
                showAddDialog = false
            }
        )
    }

    // 2. DÜZENLEME PENCERESİ (Dolu yere tıklayınca) - YENİ!
    if (showEditDialog && selectedAppointment != null) {
        EditPatientDialog(
            appointment = selectedAppointment!!,
            onDismiss = { showEditDialog = false },
            onUpdate = { newName ->
                // İsmi güncelle
                viewModel.updateAppointment(
                    id = selectedAppointment!!.id,
                    name = newName,
                    fzt = selectedAppointment!!.physiotherapist,
                    time = selectedAppointment!!.timeSlot,
                    status = selectedAppointment!!.status
                )
                showEditDialog = false
            },
            onDelete = {
                // Sil
                viewModel.deleteAppointment(selectedAppointment!!.id)
                showEditDialog = false
            }
        )
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // Başlıklar
        Row(modifier = Modifier.fillMaxWidth()) {
            Box(modifier = Modifier.width(150.dp).height(50.dp).border(1.dp, Color.Gray).background(MaterialTheme.colorScheme.primaryContainer).padding(8.dp), contentAlignment = Alignment.Center) {
                Text("Fizyoterapist", fontWeight = FontWeight.Bold)
            }
            TIME_SLOTS.forEach { time ->
                Box(modifier = Modifier.weight(1f).height(50.dp).border(1.dp, Color.Gray).background(MaterialTheme.colorScheme.surfaceVariant).padding(8.dp), contentAlignment = Alignment.Center) {
                    Text(time, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Tablo
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(PHYSIOTHERAPISTS) { fzt ->
                Row(modifier = Modifier.fillMaxWidth()) {
                    Box(modifier = Modifier.width(150.dp).height(80.dp).border(1.dp, Color.LightGray).padding(8.dp), contentAlignment = Alignment.CenterStart) {
                        Text(fzt, fontWeight = FontWeight.Bold)
                    }

                    TIME_SLOTS.forEach { time ->
                        val appointment = appointments.find { it.physiotherapist == fzt && it.timeSlot == time }

                        AppointmentCell(
                            appointment = appointment,
                            isAdmin = isAdmin,
                            modifier = Modifier.weight(1f).height(80.dp),
                            onClick = {
                                if (isAdmin) {
                                    if (appointment == null) {
                                        // BOŞ -> EKLE
                                        selectedFzt = fzt
                                        selectedTime = time
                                        showAddDialog = true
                                    } else {
                                        // DOLU -> DÜZENLE/SİL
                                        selectedAppointment = appointment
                                        showEditDialog = true
                                    }
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
fun AppointmentCell(appointment: Appointment?, isAdmin: Boolean, modifier: Modifier, onClick: () -> Unit) {
    Box(modifier = modifier.border(0.5.dp, Color.LightGray).clickable(enabled = isAdmin) { onClick() }.padding(4.dp), contentAlignment = Alignment.Center) {
        if (appointment != null) {
            Card(
                colors = CardDefaults.cardColors(containerColor = if (appointment.status == "Tamamlandı") Color(0xFFE8F5E9) else MaterialTheme.colorScheme.primary),
                modifier = Modifier.fillMaxSize()
            ) {
                Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = appointment.patientName, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = if (appointment.status == "Tamamlandı") Color.Black else Color.White)
                }
            }
        } else if (isAdmin) {
            Text("+", color = Color.LightGray, style = MaterialTheme.typography.headlineSmall)
        }
    }
}

@Composable
fun AddPatientDialog(physiotherapist: String, timeSlot: String, onDismiss: () -> Unit, onConfirm: (String) -> Unit) {
    var name by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Yeni Randevu") },
        text = {
            Column {
                Text("$physiotherapist - $timeSlot")
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Hasta Adı") }, singleLine = true)
            }
        },
        confirmButton = { Button(onClick = { if (name.isNotBlank()) onConfirm(name) }) { Text("Kaydet") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("İptal") } }
    )
}

// --- YENİ DÜZENLEME PENCERESİ ---
@Composable
fun EditPatientDialog(
    appointment: Appointment,
    onDismiss: () -> Unit,
    onUpdate: (String) -> Unit,
    onDelete: () -> Unit
) {
    var name by remember { mutableStateOf(appointment.patientName) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Randevu Düzenle") },
        text = {
            Column {
                Text("${appointment.physiotherapist} - ${appointment.timeSlot}")
                Spacer(modifier = Modifier.height(16.dp))

                // İsim Düzenleme Alanı
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Hasta Adı") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Sil Butonu (Solda ve Kırmızı)
                Button(
                    onClick = onDelete,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Sil")
                }

                // Güncelle Butonu (Sağda ve Mavi)
                Button(
                    onClick = { if (name.isNotBlank()) onUpdate(name) },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Güncelle")
                }
            }
        },
        dismissButton = {
            // İptal butonu gerekirse buraya eklenebilir ama yukarıdaki yapı yeterli
        }
    )
}