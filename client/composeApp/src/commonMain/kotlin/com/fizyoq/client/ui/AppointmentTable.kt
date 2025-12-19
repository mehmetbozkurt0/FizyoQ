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

val TIME_SLOTS = listOf("09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00", "18:00")

@Composable
fun AppointmentTable(
    viewModel: AppointmentViewModel,
    isAdmin: Boolean
) {
    val appointments = if (isAdmin) viewModel.appointments else viewModel.getPatientDisplayList()
    val currentFizyoList = viewModel.physiotherapist

    var showManageStaffDialog by remember {mutableStateOf(false)}
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
            onUpdate = { newName, newStatus ->
                viewModel.updateAppointment(
                    id = selectedAppointment!!.id,
                    name = newName,
                    fzt = selectedAppointment!!.physiotherapist,
                    time = selectedAppointment!!.timeSlot,
                    status = newStatus,
                    date = selectedAppointment!!.date
                )
                showEditDialog = false
            },
            onDelete = {
                viewModel.deleteAppointment(selectedAppointment!!.id)
                showEditDialog = false
            }
        )
    }

    if (showManageStaffDialog) {
        ManageStaffDialog(
            staffList = currentFizyoList,
            onDismiss = {showManageStaffDialog = false},
            onAdd = {viewModel.addPhysiotherapist(it)},
            onDelete = {viewModel.deletePhysiotherapist(it)}
        )
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
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

            if (isAdmin) {
                Spacer(modifier = Modifier.width(16.dp))
                Button(
                    onClick = { showManageStaffDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray)
                ) {
                    Text("Personel Yönetimi")
                }
            }
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
            items(currentFizyoList) { fzt ->
                Row(modifier = Modifier.fillMaxWidth()) {
                    Box(modifier = Modifier.width(150.dp).height(80.dp).border(1.dp, Color.LightGray), contentAlignment = Alignment.CenterStart) {
                        Text("  ${fzt.name}", fontWeight = FontWeight.Bold)
                    }
                    TIME_SLOTS.forEach { time ->
                        val appointment = appointments.find { it.physiotherapist == fzt.name && it.timeSlot == time }
                        AppointmentCell(
                            appointment = appointment,
                            isAdmin = isAdmin,
                            modifier = Modifier.weight(1f).height(80.dp),
                            onClick = {
                                if (isAdmin) {
                                    if (appointment == null) {
                                        selectedFzt = fzt.name; selectedTime = time; showAddDialog = true
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

fun getStatusColor(status: String): Color {
    return when (status) {
        "Bekliyor" -> Color(0xFFBBDEFB) // Açık Mavi
        "Geldi" -> Color(0xFFFFF9C4)    // Açık Sarı
        "Tamamlandı" -> Color(0xFFC8E6C9) // Açık Yeşil
        "İptal" -> Color(0xFFFFCDD2)    // Açık Kırmızı
        else -> Color.White
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
            .border(0.5.dp, Color.LightGray.copy(alpha = 0.5f))
            .clickable(enabled = isAdmin) { onClick() }
            .padding(4.dp),
        contentAlignment = Alignment.Center
    ) {
        if (appointment != null) {
            // Duruma göre İkon ve Renk Belirleme
            val (statusIcon, statusColor) = when (appointment.status) {
                "Tamamlandı" -> "✅" to Color(0xFFE8F5E9)
                "Geldi" -> "🕒" to Color(0xFFFFF9C4)
                "İptal" -> "❌" to Color(0xFFFFEBEE)
                else -> "⏳" to Color(0xFFE3F2FD)
            }

            Card(
                colors = CardDefaults.cardColors(containerColor = statusColor),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = statusIcon,
                        style = MaterialTheme.typography.labelSmall
                    )
                    Text(
                        text = appointment.patientName,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        color = Color.DarkGray,
                        maxLines = 2
                    )
                }
            }
        } else if (isAdmin) {
            Text("+", color = Color.LightGray, style = MaterialTheme.typography.bodyLarge)
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
fun EditPatientDialog(
    appointment: Appointment,
    onDismiss: () -> Unit,
    onUpdate: (String, String) -> Unit,
    onDelete: () -> Unit) {
    var name by remember { mutableStateOf(appointment.patientName) }
    var selectedStatus by remember {mutableStateOf(appointment.status)}
    val stasuses = listOf("Bekliyor", "Geldi", "Tamamlandı", "İptal")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Randevu Düzenle") },
        text = {
            Column {
                Text("${appointment.physiotherapist} - ${appointment.timeSlot}", style = MaterialTheme.typography.labelLarge)
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Hasta Adı") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))

                Text("Durum Güncelle:", style = MaterialTheme.typography.labelMedium)
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    stasuses.forEach { status ->
                        FilterChip(
                            selected = selectedStatus == status,
                            onClick = {selectedStatus = status},
                            label = {Text(status, style = MaterialTheme.typography.labelSmall)}
                        )
                    }
                }
            }
        },
        confirmButton = {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = onDelete,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    modifier = Modifier.weight(1f)
                ) { Text("Sil") }
                Button(
                    onClick = { if (name.isNotBlank()) onUpdate(name, selectedStatus) },
                    modifier = Modifier.weight(1f)
                ) { Text("Güncelle") }
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Kapat") } }
    )
}

@Composable
fun ManageStaffDialog(
    staffList: List<com.fizyoq.client.model.Physiotherapist>,
    onDismiss: () -> Unit,
    onAdd: (String) -> Unit,
    onDelete: (Int) -> Unit
) {
    var newStaffName by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Fizyoterapist Yönetimi") },
        text = {
            Column(modifier = Modifier.width(300.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = newStaffName,
                        onValueChange = { newStaffName = it },
                        label = { Text("Yeni İsim") },
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = {
                        if(newStaffName.isNotBlank()) {
                            onAdd(newStaffName)
                            newStaffName = ""
                        }
                    }) {
                        Text("+", style = MaterialTheme.typography.headlineSmall)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Divider()

                // Liste Kısmı
                LazyColumn(modifier = Modifier.height(200.dp)) {
                    items(staffList) { staff ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(staff.name)
                            IconButton(onClick = { onDelete(staff.id) }) {
                                Text("🗑️", color = Color.Red)
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Kapat") }
        }
    )
}