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
val TIME_SLOTS = listOf("09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00", "18:00")

@Composable
fun AppointmentTable(
    viewModel: AppointmentViewModel,
    isAdmin: Boolean
) {
    val appointments = if (isAdmin) viewModel.appointments else viewModel.getPatientDisplayList()

    var showAddDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var selectedFzt by remember { mutableStateOf("") }
    var selectedTime by remember { mutableStateOf("") }
    var selectedAppointment by remember { mutableStateOf<Appointment?>(null) }

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

    if (showEditDialog && selectedAppointment != null) {
        EditPatientDialog(
            appointment = selectedAppointment!!,
            onDismiss = { showEditDialog = false },
            onUpdate = { newName ->
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
                viewModel.deleteAppointment(selectedAppointment!!.id)
                showEditDialog = false
            }
        )
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // TARİH PANELİ
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(onClick = { viewModel.previousDay() }) { Text("< Önceki") }
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = viewModel.formattedDate, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.width(16.dp))
            Button(onClick = { viewModel.nextDay() }) { Text("Sonraki >") }
            Spacer(modifier = Modifier.width(16.dp))
            Button(onClick = { viewModel.goToday() }, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)) { Text("Bugün") }
        }

        // TABLO BAŞLIKLARI
        Row(modifier = Modifier.fillMaxWidth()) {
            Box(modifier = Modifier.width(150.dp).height(50.dp).border(1.dp, Color.Gray).background(MaterialTheme.colorScheme.primaryContainer), contentAlignment = Alignment.Center) {
                Text("Fizyoterapist", fontWeight = FontWeight.Bold)
            }
            TIME_SLOTS.forEach { time ->
                Box(modifier = Modifier.weight(1f).height(50.dp).border(1.dp, Color.Gray).background(MaterialTheme.colorScheme.surfaceVariant), contentAlignment = Alignment.Center) {
                    Text(time, fontWeight = FontWeight.Bold)
                }
            }
        }

        // TABLO SATIRLARI
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(PHYSIOTHERAPISTS) { fzt ->
                Row(modifier = Modifier.fillMaxWidth()) {
                    Box(modifier = Modifier.width(150.dp).height(80.dp).border(1.dp, Color.LightGray), contentAlignment = Alignment.CenterStart) {
                        Text("  $fzt", fontWeight = FontWeight.Bold)
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
                                        selectedFzt = fzt; selectedTime = time; showAddDialog = true
                                    } else {
                                        selectedAppointment = appointment; showEditDialog = true
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
            Card(colors = CardDefaults.cardColors(containerColor = if (appointment.status == "Tamamlandı") Color(0xFFE8F5E9) else MaterialTheme.colorScheme.primary), modifier = Modifier.fillMaxSize()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = appointment.patientName, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = if (appointment.status == "Tamamlandı") Color.Black else Color.White)
                }
            }
        } else if (isAdmin) {
            Text("+", color = Color.LightGray)
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

@Composable
fun EditPatientDialog(appointment: Appointment, onDismiss: () -> Unit, onUpdate: (String) -> Unit, onDelete: () -> Unit) {
    var name by remember { mutableStateOf(appointment.patientName) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Randevu Düzenle") },
        text = {
            Column {
                Text("${appointment.physiotherapist} - ${appointment.timeSlot}")
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Hasta Adı") }, singleLine = true, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = onDelete, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error), modifier = Modifier.weight(1f)) { Text("Sil") }
                Button(onClick = { if (name.isNotBlank()) onUpdate(name) }, modifier = Modifier.weight(1f)) { Text("Güncelle") }
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Kapat") } }
    )
}