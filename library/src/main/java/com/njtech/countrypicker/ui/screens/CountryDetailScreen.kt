package com.njtech.countrypicker.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.njtech.countrypicker.data.model.Country

@Composable
fun CountryDetailScreen(
    country: Country?,
    onSelectClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (country == null) {
        Text(
            text = "Select a country to see details",
            modifier = modifier
                .fillMaxSize()
                .padding(32.dp),
            style = MaterialTheme.typography.bodyLarge
        )
        return
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = country.flag,
                fontSize = 120.sp
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        val countryName = country.name["en"] ?: ""
        Text(
            text = countryName,
            style = MaterialTheme.typography.headlineLarge
        )

        Spacer(modifier = Modifier.height(24.dp))

        DetailRow(label = "ISO Code", value = country.code)
        DetailRow(label = "Dialing Code", value = country.dialCode)
        
        Spacer(modifier = Modifier.weight(1f))
        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onSelectClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Select $countryName")
        }
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}
