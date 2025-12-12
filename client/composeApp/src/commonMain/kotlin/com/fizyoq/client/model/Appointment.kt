package com.fizyoq.client.model

import kotlinx.serialization.Serializable

@Serializable
data class Appointment (
    val id: String,
    val patientName: String,
    val treatmentType: String,
    val date: String,
    val timeSlot: String,
    val status: AppointmentStatus = AppointmentStatus.PENDING,
    val notes: String? = null

)
@Serializable
enum class AppointmentStatus {
    PENDING,
    COMPLETED,
    CANCELLED
}