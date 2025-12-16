package com.fizyoq.client.network

import com.fizyoq.client.getPlatform
import com.fizyoq.client.model.Appointment
import com.fizyoq.client.model.AppointmentRequest
import io.ktor.client.call.*
import io.ktor.client.*
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

object  FizyoApi {
    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                prettyPrint = true
                isLenient = true
            })
        }
    }

    private val BASE_URL = getPlatform().baseUrl

    suspend fun getPatients(): List<Appointment> {
        println("İstek atılıyor: $BASE_URL/patients/")
        return client.get("$BASE_URL/patients/").body()
    }

    suspend fun addPatient(request: AppointmentRequest) {
        client.post("$BASE_URL/patients/") {
            header("Content-Type","application/json")
            setBody(request)
        }
    }
}