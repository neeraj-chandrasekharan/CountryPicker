package com.njtech.countrypicker.data.repository

import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CountryRepositoryTest {

    private val sampleJson = """
[
    {"name": {"en": "Afghanistan", "es": "Afganistán"}, "dialCode": "+93", "code": "AF", "flag": "🇦🇫"},
    {"name": {"en": "Albania", "es": "Albania"}, "dialCode": "+355", "code": "AL", "flag": "🇦🇱"},
    {"name": {"en": "Algeria", "es": "Argelia"}, "dialCode": "+213", "code": "DZ", "flag": "🇩🇿"}
]
"""
    private val repository = CountryRepository(jsonString = sampleJson)

    @Test
    fun `getCountries returns sorted list from provided json`() = runTest {
        val result = repository.getCountries()
        assertTrue(result.isSuccess)
        val countries = result.getOrNull()!!
        
        assertEquals("Afghanistan", countries[0].name["en"])
        assertEquals("Albania", countries[1].name["en"])
        assertEquals("Algeria", countries[2].name["en"])
    }

    @Test
    fun `searchCountries filters correctly by localized name`() = runTest {
        val result = repository.searchCountries("Argelia")
        assertTrue(result.isSuccess)
        val countries = result.getOrNull()!!
        assertEquals(1, countries.size)
        assertEquals("Algeria", countries[0].name["en"])
    }

    @Test
    fun `searchCountries filters correctly by dial code`() = runTest {
        val result = repository.searchCountries("+93")
        assertTrue(result.isSuccess)
        val countries = result.getOrNull()!!
        assertEquals(1, countries.size)
        assertEquals("Afghanistan", countries[0].name["en"])
    }
}
