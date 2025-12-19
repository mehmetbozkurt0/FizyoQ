package com.fizyoq.client.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.fizyoq.client.model.Appointment
import com.fizyoq.client.model.AppointmentRequest
import com.fizyoq.client.network.FizyoApi
import com.fizyoq.client.model.Physiotherapist
import com.fizyoq.client.model.PhysiotherapistRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import kotlinx.datetime.*
import kotlinx.datetime.LocalDate
import kotlinx.datetime.number
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class AppointmentViewModel {
    var appointments by mutableStateOf<List<Appointment>>(emptyList())
        private set

    var selectedDate by mutableStateOf<LocalDate>(
        Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
    )
        private set

    val formattedDate: String
        get() = "${selectedDate.day} ${getTurkishMonthName(selectedDate.month.number)} ${selectedDate.year}"

    private val apiDateFormat: String
        get() = selectedDate.toString()

    var physiotherapist by mutableStateOf<List<Physiotherapist>>(emptyList())
        private set

    private val scope = CoroutineScope(Dispatchers.IO)

    init {
        fetchPhysiotherapists()
        fetchAppointments()
    }

    fun fetchPhysiotherapists() {
        scope.launch {
            try {
                physiotherapist = FizyoApi.getPhysiotherapists()
            }
            catch (e: Exception) {
                println("Fzt. Çekilemedi: ${e.message}")
            }
        }
    }

    fun addPhysiotherapist(name: String) {
        scope.launch {
            try {
                FizyoApi.addPhysiotherapist(PhysiotherapistRequest(name))
                fetchPhysiotherapists() // Listeyi tazele
            } catch (e: Exception) {
                println("Fizyoterapist Ekleme Hatası: ${e.message}")
            }
        }
    }

    fun deletePhysiotherapist(id: Int) {
        scope.launch {
            try {
                FizyoApi.deletePhysiotherapist(id)
                fetchPhysiotherapists()
                fetchAppointments()
            } catch (e: Exception) {
                println("Fizyoterapist Silme Hatası: ${e.message}")
            }
        }
    }

    fun nextDay() {
        selectedDate = selectedDate.plus(DatePeriod(days = 1))
        fetchAppointments()
    }

    fun previousDay() {
        selectedDate = selectedDate.minus(DatePeriod(days = 1))
        fetchAppointments()
    }

    fun goToday() {
        selectedDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        fetchAppointments()
    }

    fun fetchAppointments() {
        scope.launch {
            try {
                appointments = FizyoApi.getPatients(apiDateFormat)
            } catch (e: Exception) {
                println("API Hatası: ${e.message}")
            }
        }
    }

    fun addAppointment(name: String, fzt: String, time: String) {
        scope.launch {
            try {
                val newReq = AppointmentRequest(
                    patientName = name,
                    physiotherapist = fzt,
                    timeSlot = time,
                    date = apiDateFormat
                )
                FizyoApi.addPatient(newReq)
                fetchAppointments()
            } catch (e: Exception) {
                println("Ekleme Hatası: ${e.message}")
            }
        }
    }

    fun updateAppointment(id: Int, name: String, fzt: String, time: String, status: String, date: String) {
        scope.launch {
            try {
                val updateRequest = AppointmentRequest(
                    patientName = name,
                    physiotherapist = fzt,
                    timeSlot = time,
                    status = status,
                    date = date
                )
                FizyoApi.updatePatient(id, updateRequest)
                fetchAppointments()
            } catch (e: Exception) {
                println("Güncelleme Hatası: ${e.message}")
            }
        }
    }

    fun deleteAppointment(id: Int) {
        scope.launch {
            try {
                FizyoApi.deletePatient(id)
                fetchAppointments()
            } catch (e: Exception) {
                println("Silme Hatası: ${e.message}")
            }
        }
    }

    fun getPatientDisplayList(): List<Appointment> {
        return appointments.map { it.copy(patientName = maskName(it.patientName)) }
    }

    private fun maskName(fullName: String): String {
        return fullName.split(" ").joinToString(" ") { word ->
            if (word.isNotEmpty()) "${word.first()}****" else ""
        }
    }

    private fun getTurkishMonthName(month: Int): String {
        return when (month) {
            1 -> "Ocak"; 2 -> "Şubat"; 3 -> "Mart"; 4 -> "Nisan"; 5 -> "Mayıs"; 6 -> "Haziran"
            7 -> "Temmuz"; 8 -> "Ağustos"; 9 -> "Eylül"; 10 -> "Ekim"; 11 -> "Kasım"; 12 -> "Aralık"
            else -> ""
        }
    }
}