package com.njtech.countrypicker.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.njtech.countrypicker.data.model.Country
import com.njtech.countrypicker.ui.viewmodel.CountryUiState

@Composable
fun CountryListScreen(
    uiState: CountryUiState,
    onSearchQueryChange: (String) -> Unit,
    onCountryClick: (Country) -> Unit,
    modifier: Modifier = Modifier,
    itemContent: (@Composable (Country) -> Unit)? = null
) {
    val topPadding = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()

    Column(modifier = modifier.fillMaxSize()) {
        SearchBar(
            query = uiState.searchQuery,
            onQueryChange = onSearchQueryChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(top = topPadding + 16.dp, bottom = 16.dp)
        )

        Box(modifier = Modifier.fillMaxSize()) {
            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (uiState.error != null) {
                Text(
                    text = uiState.error,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(16.dp)
                )
            } else {
                LazyColumn {
                    items(uiState.countries, key = { it.code }) { country ->
                        if (itemContent != null) {
                            Box(modifier = Modifier.clickable { onCountryClick(country) }) {
                                itemContent(country)
                            }
                        } else {
                            DefaultCountryItem(
                                country = country,
                                onClick = { onCountryClick(country) }
                            )
                        }
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            thickness = 0.5.dp,
                            color = MaterialTheme.colorScheme.outlineVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier,
        placeholder = { Text("Search by name or code") },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
        singleLine = true,
        shape = RoundedCornerShape(12.dp)
    )
}

@Composable
fun DefaultCountryItem(
    country: Country,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ListItem(
        headlineContent = { Text(country.name["en"] ?: "") },
        supportingContent = { Text("${country.code} (${country.dialCode})") },
        leadingContent = {
            Text(
                text = country.flag,
                fontSize = 28.sp
            )
        },
        modifier = modifier.clickable { onClick() }
    )
}
