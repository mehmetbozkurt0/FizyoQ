package com.fizyoq.client

import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import androidx.compose.runtime.remember
import com.fizyoq.client.ui.AppointmentTable
import com.fizyoq.client.viewmodel.AppointmentViewModel

fun main() = application {
    val viewModel = remember { AppointmentViewModel() }

    Window(
        onCloseRequest = ::exitApplication,
        title = "FizyoQ - Yönetici Paneli",
        state = rememberWindowState(
            position = WindowPosition(Alignment.Center),
            size = DpSize(1000.dp, 700.dp)
        )
    ) {
        AppointmentTable(
            viewModel = viewModel,
            isAdmin = true
        )
    }

    Window(
        onCloseRequest = ::exitApplication,
        title = "FizyoQ - Hasta Bilgilendirme",
        state = rememberWindowState(

            position = WindowPosition(x = 100.dp, y = 100.dp),
            size = DpSize(800.dp, 600.dp)
        )
    ) {
        val maskedList = viewModel.getPatientDisplayList()

        AppointmentTable(
            viewModel = viewModel,
            isAdmin = false
        )
    }
}