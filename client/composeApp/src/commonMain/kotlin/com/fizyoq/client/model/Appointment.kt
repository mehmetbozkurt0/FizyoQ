package com.fizyoq.client.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class Appointment (
    val id: Int,

    @SerialName("full_name")
    val patientName: String,

    @SerialName("physiotherapist")
    val physiotherapist: String,

    @SerialName("reservation_time")
    val timeSlot: String,

    val status: String = "Bekliyor"
)

@Serializable
data class  AppointmentRequest(
    @SerialName("full_name")
    val patientName: String,

    @SerialName("physiotherapist")
    val physiotherapist: String,

    @SerialName("reservation_time")
    val timeSlot: String,

    val status: String = "Bekliyor"
)