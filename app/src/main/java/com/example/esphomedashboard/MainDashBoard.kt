package com.example.esphomedashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.time.Duration.Companion.seconds


@Composable
fun MainDashBoard(innerPadding: PaddingValues, mainDashBoardViewmodel: ViewModelDashBoard) {

    Column(
        modifier = Modifier
            .padding(16.dp)

    ) {
        TopBarInfo(mainDashBoardViewmodel)
        Spacer(modifier = Modifier.padding(top = 16.dp))
        HorizontalDivider(
            color = MaterialTheme.colorScheme.onSecondary,
            modifier = Modifier.fillMaxWidth(1f)
        )
    }
}

@Composable
fun TopBarInfo(mainDashBoardViewmodel: ViewModelDashBoard) {
    val weatherResponse by mainDashBoardViewmodel.weatherState.collectAsStateWithLifecycle()
    val localeLang = Locale("ru")

    var currentTime by remember { mutableStateOf("") }
    var currentDate by remember { mutableStateOf("") }
    LaunchedEffect(Unit) {
        while (true) {
            currentTime = SimpleDateFormat("HH:mm",localeLang).format(Date())
            currentDate = SimpleDateFormat("EEEE, d MMMM", localeLang)
                .format(Date())
                .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale("ru")) else it.toString() }
            delay(10.seconds)
        }

    }


    val currentTemperatureText = remember(weatherResponse) {
        val response = weatherResponse ?: return@remember "Загрузка..."

        val currentHourString = SimpleDateFormat("yyyy-MM-dd'T'HH:00", Locale.getDefault()).format(Date())
        val index = response.hourly.time.indexOf(currentHourString)

        val temp = if (index != -1) {
            response.hourly.temperature_2m[index]
        } else {
            response.hourly.temperature_2m.firstOrNull() ?: 0.0
        }

        if (temp > 0) "На улице +$temp °C" else "На улице $temp °C"
    }

    BoxWithConstraints() {
        val isTablet = maxWidth > 900.dp

        val timeFontSize = if (isTablet) 64.sp else 32.sp
        val dateFontSize = if (isTablet) 32.sp else 15.sp

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.2f)
        ) {
            Text(
                text = currentTime,
                style = TextStyle(
                    fontSize = timeFontSize,
                    color = MaterialTheme.colorScheme.tertiary,
                    fontFamily = FontFamily(Font(R.font.bold))
                )
            )
            VerticalDivider(
                color = MaterialTheme.colorScheme.onSecondary,
                modifier = Modifier.fillMaxHeight(0.5f)
            )
            Text(
                text = currentDate,
                style = TextStyle(
                    fontSize = dateFontSize,
                    color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.9f),
                    fontFamily = FontFamily(Font(R.font.medium))
                )
            )

            VerticalDivider(
                color = MaterialTheme.colorScheme.onSecondary,
                modifier = Modifier.fillMaxHeight(0.5f)
            )
            Text(
                text = currentTemperatureText,
                style = TextStyle(
                    fontSize = dateFontSize,
                    color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.9f),
                    fontFamily = FontFamily(Font(R.font.medium))
                )
            )
        }
    }
}