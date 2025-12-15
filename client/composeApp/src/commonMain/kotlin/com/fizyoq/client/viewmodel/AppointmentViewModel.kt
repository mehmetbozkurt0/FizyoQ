package com.fizyoq.client.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.fizyoq.client.model.Appointment
import com.fizyoq.client.model.AppointmentStatus

class AppointmentViewModel {
    var appointments by mutableStateOf<List<Appointment>>(emptyList())
        private set

    init {
        loadMockData()
    }

    private fun loadMockData() {
        appointments = listOf(
            Appointment("1", "Ahmet Yılmaz", "Manuel Terapi", "2023-10-27", "14:00"),
            Appointment("2", "Ayşe Demir", "Kuru İğneleme", "2023-10-27", "15:00", AppointmentStatus.COMPLETED),
            Appointment("3", "Mehmet Kaya", "Kupa Terapisi", "2023-10-27", "16:00", AppointmentStatus.CANCELLED)
        )
    }

    fun getPatientDisplayList(): List<Appointment> {
        return appointments.map { randevu ->
            randevu.copy(
                patientName = maskName(randevu.patientName)
            )
        }
    }

    private fun maskName(fullName: String): String {
        return fullName.split(" ").joinToString(" ") { word ->
            if (word.isNotEmpty()) "${word.first()}****" else ""
        }
    }
}