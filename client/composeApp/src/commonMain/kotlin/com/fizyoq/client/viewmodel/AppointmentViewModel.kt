package com.fizyoq.client.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.fizyoq.client.model.Appointment
import com.fizyoq.client.model.AppointmentRequest
import com.fizyoq.client.network.FizyoApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch

class AppointmentViewModel {
    var appointments by mutableStateOf<List<Appointment>>(emptyList())
        private set

    private val scope = CoroutineScope(Dispatchers.IO)

    init {
        fetchAppointments()
    }

    fun fetchAppointments() {
        scope.launch {
            try {
                appointments = FizyoApi.getPatients()
            }
            catch (e: Exception) {
                println("API Hatası: ${e.message}")
                e.printStackTrace()
            }
        }
    }

    fun getPatientDisplayList(): List<Appointment> {
        return appointments.map { randevu ->
            randevu.copy(
                patientName = maskName(randevu.patientName)
            )
        }
    }

    fun addAppointment(name: String, fzt: String, time: String) {
        scope.launch {
            try {
                val newAppointment = AppointmentRequest(
                    patientName = name,
                    physiotherapist = fzt,
                    timeSlot = time
                )

                FizyoApi.addPatient(newAppointment)
                fetchAppointments()
            }
            catch (e: Exception) {
                println("Ekleme Hatası: ${e.message}")
            }
        }
    }

    fun deleteAppointment(id: Int) {
        scope.launch {
            try {
                FizyoApi.deletePatient(id)
                fetchAppointments()
            }
            catch (e: Exception) {
                println("Silme hatası: ${e.message}")
            }
        }
    }

    fun updateAppointment(id: Int, name: String, fzt: String, time: String, status: String) {
        scope.launch {
            try {
                val updateRequest = AppointmentRequest(
                    patientName = name,
                    physiotherapist = fzt,
                    timeSlot = time,
                    status = status
                )
                FizyoApi.updatePatient(id, updateRequest)
                fetchAppointments()
            } catch (e: Exception) {
                println("Güncelleme Hatası: ${e.message}")
            }
        }
    }

    private fun maskName(fullName: String): String {
        return fullName.split(" ").joinToString(" ") { word ->
            if (word.isNotEmpty()) "${word.first()}****" else ""
        }
    }
}