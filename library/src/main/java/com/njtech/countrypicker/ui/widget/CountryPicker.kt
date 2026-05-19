package com.njtech.countrypicker.ui.widget

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffold
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.njtech.countrypicker.data.model.Country
import com.njtech.countrypicker.data.repository.CountryRepository
import com.njtech.countrypicker.ui.screens.CountryDetailScreen
import com.njtech.countrypicker.ui.screens.CountryListScreen
import com.njtech.countrypicker.ui.viewmodel.CountryViewModel
import kotlinx.coroutines.launch

/**
 * A reusable CountryPicker widget that provides an adaptive list-detail interface.
 *
 * @param onCountrySelected Callback triggered when a country is selected (e.g., from the detail view or list).
 * @param modifier Modifier for the root container.
 */
@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun CountryPicker(
    onCountrySelected: (Country) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current.applicationContext
    val viewModel: CountryViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return CountryViewModel(CountryRepository(context)) as T
            }
        }
    )

    val uiState by viewModel.uiState.collectAsState()
    val navigator = rememberListDetailPaneScaffoldNavigator<String>()
    val scope = rememberCoroutineScope()

    val selectedCountry = uiState.countries.find { it.code == navigator.currentDestination?.contentKey }

    BackHandler(navigator.canNavigateBack()) {
        scope.launch {
            navigator.navigateBack()
        }
    }

    Scaffold(modifier = modifier.fillMaxSize()) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            ListDetailPaneScaffold(
                directive = navigator.scaffoldDirective,
                value = navigator.scaffoldValue,
                listPane = {
                    AnimatedPane {
                        CountryListScreen(
                            uiState = uiState,
                            onSearchQueryChange = { viewModel.onSearchQueryChange(it) },
                            onCountryClick = { country ->
                                scope.launch {
                                    navigator.navigateTo(ListDetailPaneScaffoldRole.Detail, country.code)
                                }
                            }
                        )
                    }
                },
                detailPane = {
                    AnimatedPane {
                        CountryDetailScreen(
                            country = selectedCountry,
                            onSelectClick = {
                                selectedCountry?.let { onCountrySelected(it) }
                            }
                        )
                    }
                }
            )
        }
    }
}
