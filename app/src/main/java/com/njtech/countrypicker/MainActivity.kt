package com.njtech.countrypicker

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.njtech.countrypicker.ui.screens.CountryListScreen
import com.njtech.countrypicker.ui.theme.CountryPickerTheme
import com.njtech.countrypicker.ui.widget.CountryPicker
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CountryPickerTheme {
                CountryListScreen(
                    uiState = uiState,
                    onSearchQueryChange = { viewModel.onSearchQueryChange(it) },
                    onCountryClick = { country ->
                        scope.launch {
                            navigator.navigateTo(ListDetailPaneScaffoldRole.Detail, country.code)
                        }
                    }
                )
                CountryPicker(
                    modifier = Modifier.background(Color.Red),
                    onCountrySelected = { country ->
                        Toast.makeText(
                            this,
                            "Selected: ${country.name["en"]} (${country.code})",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                )
            }
        }
    }
}
