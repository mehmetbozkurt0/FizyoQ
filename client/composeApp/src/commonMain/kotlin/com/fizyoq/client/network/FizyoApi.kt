package com.fizyoq.client.network

import com.fizyoq.client.getPlatform
import com.fizyoq.client.model.Appointment
import com.fizyoq.client.model.AppointmentRequest
import io.ktor.client.call.*
import io.ktor.client.*
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

object  FizyoApi {
    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                prettyPrint = true
                isLenient = true
                encodeDefaults = true
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

    suspend fun deletePatient(id: Int) {
        client.delete("$BASE_URL/patients/$id")
    }

    suspend fun updatePatient(id: Int, request: AppointmentRequest) {
        client.put("$BASE_URL/patients/$id") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
    }
}