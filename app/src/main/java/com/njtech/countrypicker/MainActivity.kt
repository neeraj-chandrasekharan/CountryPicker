package com.njtech.countrypicker

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.njtech.countrypicker.data.repository.CountryRepository
import com.njtech.countrypicker.ui.theme.CountryPickerTheme
import com.njtech.countrypicker.ui.widget.CountryPicker
import com.njtech.countrypicker.ui.widget.CountryPickerMode

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CountryPickerTheme {
                var showPicker by remember { mutableStateOf(false) }
                var pickerMode by remember { mutableStateOf<CountryPickerMode>(CountryPickerMode.Fullscreen) }
                var showDetailPane by remember { mutableStateOf(false) }
                var useCustomItem by remember { mutableStateOf(false) }
                var useCustomSearch by remember { mutableStateOf(false) }
                var useCustomContainer by remember { mutableStateOf(false) }

                BackHandler(showPicker) {
                    showPicker = false
                }
                
                // Demo direct repository access
                val repository = remember { CountryRepository(applicationContext) }
                LaunchedEffect(Unit) {
                    repository.getCountries().onSuccess { countries ->
                        println("MainActivity: Loaded ${countries.size} countries directly from repository.")
                    }
                }

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "CountryPicker Demo", style = MaterialTheme.typography.headlineLarge)
                        
                        Spacer(modifier = Modifier.padding(16.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("BottomSheet Mode")
                            Switch(
                                checked = pickerMode == CountryPickerMode.BottomSheet,
                                onCheckedChange = { 
                                    pickerMode = if (it) CountryPickerMode.BottomSheet else CountryPickerMode.Fullscreen 
                                }
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Show Detail Pane")
                            Switch(
                                checked = showDetailPane,
                                onCheckedChange = { showDetailPane = it }
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Use Custom Item Slot")
                            Switch(
                                checked = useCustomItem,
                                onCheckedChange = { useCustomItem = it }
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Use Custom Search Slot")
                            Switch(
                                checked = useCustomSearch,
                                onCheckedChange = { useCustomSearch = it }
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Use Custom List Container")
                            Switch(
                                checked = useCustomContainer,
                                onCheckedChange = { useCustomContainer = it }
                            )
                        }

                        Spacer(modifier = Modifier.padding(16.dp))

                        HorizontalDivider()
                        Spacer(modifier = Modifier.padding(16.dp))

                        Button(onClick = { showPicker = true }) {
                            Text("Open Country Picker")
                        }
                    }

                    if (showPicker) {
                        CountryPicker(
                            mode = pickerMode,
                            showDetailPane = showDetailPane,
                            onCountrySelected = { country ->
                                Toast.makeText(
                                    this,
                                    "Selected: ${country.name["en"]} (${country.code})",
                                    Toast.LENGTH_LONG
                                ).show()
                                showPicker = false
                            },
                            onDismiss = {
                                showPicker = false
                            },
                            itemContent = if (useCustomItem) {
                                { country ->
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(8.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                                        )
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(16.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(text = country.flag, fontSize = 32.sp)
                                            Spacer(modifier = Modifier.padding(8.dp))
                                            Column {
                                                Text(
                                                    text = country.name["en"] ?: "",
                                                    style = MaterialTheme.typography.titleMedium,
                                                    fontWeight = FontWeight.Bold
                                                )
                                                Text(
                                                    text = "Dial code: ${country.dialCode}",
                                                    style = MaterialTheme.typography.bodySmall
                                                )
                                            }
                                        }
                                    }
                                }
                            } else null,
                            searchContent = if (useCustomSearch) {
                                { query, onQueryChange ->
                                    OutlinedTextField(
                                        value = query,
                                        onValueChange = onQueryChange,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        label = { Text("Search Globally...") },
                                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                                        shape = RoundedCornerShape(24.dp)
                                    )
                                }
                            } else null,
                            listContainer = if (useCustomContainer) {
                                { content ->
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(8.dp)
                                            .border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(8.dp))
                                            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f))
                                    ) {
                                        content()
                                    }
                                }
                            } else null
                        )
                    }
                }
            }
        }
    }
}
