package com.njtech.countrypicker.data.repository

import android.content.Context
import com.njtech.countrypicker.data.model.Country
import com.njtech.countrypicker.library.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

class CountryRepository(private val context: Context? = null, private val jsonString: String? = null) {

    private val json = Json { ignoreUnknownKeys = true }

    suspend fun getCountries(): Result<List<Country>> = withContext(Dispatchers.IO) {
        try {
            val content = jsonString ?: context?.let {
                it.resources.openRawResource(R.raw.countries)
                    .bufferedReader()
                    .use { it.readText() }
            } ?: throw Exception("No data source provided")
            
            val countries: List<Country> = json.decodeFromString(content)
            Result.success(countries)
        } catch (e: Exception) {
            Result.failure(Exception("Failed to load country data: ${e.message}", e))
        }
    }

    suspend fun searchCountries(query: String): Result<List<Country>> = withContext(Dispatchers.IO) {
        getCountries().map { countries ->
            countries.filter { country ->
                country.name.values.any { it.contains(query, ignoreCase = true) } ||
                        country.code.contains(query, ignoreCase = true) ||
                        country.dialCode.contains(query, ignoreCase = true)
            }.sortedBy { it.name["en"] }
        }
    }
}
