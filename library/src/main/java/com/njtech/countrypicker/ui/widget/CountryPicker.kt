package com.njtech.countrypicker.ui.widget

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffold
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.material3.rememberModalBottomSheetState
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * A reusable CountryPicker widget that provides an adaptive list-detail interface.
 *
 * @param onCountrySelected Callback triggered when a country is selected.
 * @param modifier Modifier for the root container.
 * @param mode The presentation mode (Fullscreen or BottomSheet).
 * @param showDetailPane Whether to show the detail view before selecting a country.
 * @param onDismiss Callback for when the picker is dismissed (especially in BottomSheet mode).
 * @param itemContent Optional custom Composable for rendering individual country items in the list.
 * @param searchContent Optional custom Composable for rendering the search UI.
 * @param listContainer Optional custom Composable for wrapping the list of countries.
 */
@OptIn(ExperimentalMaterial3AdaptiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun CountryPicker(
    onCountrySelected: (Country) -> Unit,
    modifier: Modifier = Modifier,
    mode: CountryPickerMode = CountryPickerMode.Fullscreen,
    showDetailPane: Boolean = false,
    onDismiss: (() -> Unit)? = null,
    itemContent: (@Composable (Country) -> Unit)? = null,
    searchContent: (@Composable (query: String, onQueryChange: (String) -> Unit) -> Unit)? = null,
    listContainer: (@Composable (content: @Composable () -> Unit) -> Unit)? = null
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
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val selectedCountry = uiState.countries.find { it.code == navigator.currentDestination?.contentKey }

    val content = @Composable {
        ListDetailPaneScaffold(
            directive = navigator.scaffoldDirective,
            value = navigator.scaffoldValue,
            listPane = {
                AnimatedPane {
                    CountryListScreen(
                        uiState = uiState,
                        onSearchQueryChange = { viewModel.onSearchQueryChange(it) },
                        onCountryClick = { country ->
                            if (showDetailPane) {
                                scope.launch {
                                    navigator.navigateTo(ListDetailPaneScaffoldRole.Detail, country.code)
                                }
                            } else {
                                onCountrySelected(country)
                                if (mode == CountryPickerMode.BottomSheet) {
                                    scope.launch {
                                        sheetState.hide()
                                        delay(1000)
                                        onDismiss?.invoke()
                                    }
                                }
                            }
                        },
                        itemContent = itemContent,
                        searchContent = searchContent,
                        listContainer = listContainer
                    )
                }
            },
            detailPane = {
                if (showDetailPane) {
                    AnimatedPane {
                        CountryDetailScreen(
                            country = selectedCountry,
                            onSelectClick = {
                                selectedCountry?.let { country ->
                                    onCountrySelected(country)
                                    if (mode == CountryPickerMode.BottomSheet) {
                                        scope.launch {
                                            sheetState.hide()
                                            onDismiss?.invoke()
                                        }
                                    }
                                }
                            }
                        )
                    }
                }
            }
        )
    }

    if (mode == CountryPickerMode.BottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { onDismiss?.invoke() },
            sheetState = sheetState,
            modifier = modifier
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                content()
            }
        }
    } else {
        BackHandler(navigator.canNavigateBack()) {
            scope.launch {
                navigator.navigateBack()
            }
        }
        Scaffold(modifier = modifier.fillMaxSize()) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                content()
            }
        }
    }
}
