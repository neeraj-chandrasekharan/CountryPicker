package com.njtech.countrypicker

import android.content.res.Configuration
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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
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
                var showPicker by rememberSaveable { mutableStateOf(false) }
                var pickerMode by rememberSaveable { mutableStateOf(CountryPickerMode.Fullscreen) }
                var showDetailPane by rememberSaveable { mutableStateOf(false) }
                var useCustomItem by rememberSaveable { mutableStateOf(false) }
                var useCustomSearch by rememberSaveable { mutableStateOf(false) }
                var useCustomContainer by rememberSaveable { mutableStateOf(false) }

                val configuration = LocalConfiguration.current
                val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

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
                    ) {
                        Text(
                            text = "CountryPicker Demo",
                            style = MaterialTheme.typography.headlineLarge,
                            modifier = Modifier
                                .padding(16.dp)
                                .align(Alignment.CenterHorizontally)
                        )

                        if (isLandscape) {
                            Row(
                                modifier = Modifier.fillMaxSize(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Left side: Configuration (60% weight)
                                Column(
                                    modifier = Modifier
                                        .weight(0.6f)
                                        .fillMaxHeight()
                                        .verticalScroll(rememberScrollState())
                                        .padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    ConfigurationSwitches(
                                        pickerMode = pickerMode,
                                        onPickerModeChange = { pickerMode = it },
                                        showDetailPane = showDetailPane,
                                        onShowDetailPaneChange = { showDetailPane = it },
                                        useCustomItem = useCustomItem,
                                        onUseCustomItemChange = { useCustomItem = it },
                                        useCustomSearch = useCustomSearch,
                                        onUseCustomSearchChange = { useCustomSearch = it },
                                        useCustomContainer = useCustomContainer,
                                        onUseCustomContainerChange = { useCustomContainer = it }
                                    )
                                }

                                VerticalDivider()

                                // Right side: Action Button (40% weight)
                                Box(
                                    modifier = Modifier
                                        .weight(0.4f)
                                        .fillMaxHeight(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Button(onClick = { showPicker = true }) {
                                        Text("Open Country Picker")
                                    }
                                }
                            }
                        } else {
                            // Portrait Mode
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Column(
                                    modifier = Modifier
                                        .weight(1f)
                                        .verticalScroll(rememberScrollState())
                                        .padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    ConfigurationSwitches(
                                        pickerMode = pickerMode,
                                        onPickerModeChange = { pickerMode = it },
                                        showDetailPane = showDetailPane,
                                        onShowDetailPaneChange = { showDetailPane = it },
                                        useCustomItem = useCustomItem,
                                        onUseCustomItemChange = { useCustomItem = it },
                                        useCustomSearch = useCustomSearch,
                                        onUseCustomSearchChange = { useCustomSearch = it },
                                        useCustomContainer = useCustomContainer,
                                        onUseCustomContainerChange = { useCustomContainer = it }
                                    )
                                }

                                HorizontalDivider()
                                
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(32.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Button(onClick = { showPicker = true }) {
                                        Text("Open Country Picker")
                                    }
                                }
                            }
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

@Composable
fun ConfigurationSwitches(
    pickerMode: CountryPickerMode,
    onPickerModeChange: (CountryPickerMode) -> Unit,
    showDetailPane: Boolean,
    onShowDetailPaneChange: (Boolean) -> Unit,
    useCustomItem: Boolean,
    onUseCustomItemChange: (Boolean) -> Unit,
    useCustomSearch: Boolean,
    onUseCustomSearchChange: (Boolean) -> Unit,
    useCustomContainer: Boolean,
    onUseCustomContainerChange: (Boolean) -> Unit
) {
    ConfigSwitch(
        label = "BottomSheet Mode",
        checked = pickerMode == CountryPickerMode.BottomSheet,
        onCheckedChange = { 
            onPickerModeChange(if (it) CountryPickerMode.BottomSheet else CountryPickerMode.Fullscreen) 
        }
    )
    ConfigSwitch(
        label = "Show Detail Pane",
        checked = showDetailPane,
        onCheckedChange = onShowDetailPaneChange
    )
    ConfigSwitch(
        label = "Use Custom Item Slot",
        checked = useCustomItem,
        onCheckedChange = onUseCustomItemChange
    )
    ConfigSwitch(
        label = "Use Custom Search Slot",
        checked = useCustomSearch,
        onCheckedChange = onUseCustomSearchChange
    )
    ConfigSwitch(
        label = "Use Custom List Container",
        checked = useCustomContainer,
        onCheckedChange = onUseCustomContainerChange
    )
}

@Composable
fun ConfigSwitch(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyLarge)
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}
