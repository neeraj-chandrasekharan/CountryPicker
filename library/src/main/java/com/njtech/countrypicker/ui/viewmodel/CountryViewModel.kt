package com.njtech.countrypicker.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.njtech.countrypicker.data.model.Country
import com.njtech.countrypicker.data.model.matches
import com.njtech.countrypicker.data.repository.CountryRepository
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class CountryUiState(
    val countries: List<Country> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val searchQuery: String = ""
)

class CountryViewModel(private val repository: CountryRepository) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    private val _allCountries = MutableStateFlow<List<Country>>(emptyList())

    @OptIn(FlowPreview::class)
    val uiState: StateFlow<CountryUiState> = combine(
        _allCountries,
        _searchQuery,
        _isLoading,
        _error
    ) { countries, query, loading, err ->
        val filteredCountries = if (query.isBlank()) {
            countries
        } else {
            countries.filter { it.matches(query) }
        }
        CountryUiState(
            countries = filteredCountries,
            searchQuery = query,
            isLoading = loading,
            error = err
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = CountryUiState(isLoading = true)
    )

    init {
        loadCountries()
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun loadCountries() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            repository.getCountries().fold(
                onSuccess = { countries ->
                    _allCountries.value = countries
                    _isLoading.value = false
                },
                onFailure = { throwable ->
                    _error.value = throwable.message ?: "Unknown error"
                    _isLoading.value = false
                }
            )
        }
    }
}
