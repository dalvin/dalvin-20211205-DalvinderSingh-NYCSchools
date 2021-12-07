package com.dalvinlabs.nycschools.model

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import retrofit2.Response
import java.lang.Exception

class RepositoryTest {

    @Test
    fun `Given valid response when get schools then get schools successfully`() {
        runBlocking {
            // Given
            val schools = listOf(
                School(dbn = "foo", name = "abc"),
                School(dbn = "bar", name = "xyz")
            )
            val details = listOf(
                Details(dbn = "foo", reading = "1", writing = "2", math = "3"),
                Details(dbn = "bar", reading = "5", writing = "10", math = "15")
            )
            val mockApi = mockk<Api>(relaxed = true) {
                coEvery { getSchools() } returns Response.success(schools)
                coEvery { getDetails() } returns Response.success(details)
            }

            // When
            val repository = Repository(mockApi)
            val response = repository.getSchools()

            // Then
            coVerify(exactly = 1) { mockApi.getSchools() }
            coVerify(exactly = 1) { mockApi.getDetails() }

            Assert.assertTrue(response.isSuccess)
            val schoolWithDetails = response.getOrNull()
            Assert.assertNotNull(schoolWithDetails)
            schoolWithDetails!!
            Assert.assertEquals(schoolWithDetails.size, 2)

            Assert.assertEquals(schoolWithDetails[0].name, "abc")
            Assert.assertEquals(schoolWithDetails[0].reading, "1")
            Assert.assertEquals(schoolWithDetails[0].writing, "2")
            Assert.assertEquals(schoolWithDetails[0].math, "3")

            Assert.assertEquals(schoolWithDetails[1].name, "xyz")
            Assert.assertEquals(schoolWithDetails[1].reading, "5")
            Assert.assertEquals(schoolWithDetails[1].writing, "10")
            Assert.assertEquals(schoolWithDetails[1].math, "15")
        }
    }

    @Test
    fun `Given schools api fails when get schools then get error`() {
        runBlocking {
            // Given
            val details = listOf(
                Details(dbn = "foo", reading = "1", writing = "2", math = "3"),
                Details(dbn = "bar", reading = "5", writing = "10", math = "15")
            )
            val mockApi = mockk<Api>(relaxed = true) {
                coEvery { getSchools() } throws Exception("Foo exception encountered")
                coEvery { getDetails() } returns Response.success(details)
            }

            // When
            val repository = Repository(mockApi)
            val response = repository.getSchools()

            // Then
            coVerify(exactly = 1) { mockApi.getSchools() }
            coVerify(exactly = 0) { mockApi.getDetails() }

            Assert.assertTrue(response.isFailure)
            val exception = response.exceptionOrNull()
            Assert.assertEquals("Foo exception encountered", exception!!.message)
        }
    }

    @Test
    fun `Given details api fails when get schools then get error`() {
        runBlocking {
            // Given
            val schools = listOf(
                School(dbn = "foo", name = "abc"),
                School(dbn = "bar", name = "xyz")
            )
            val mockApi = mockk<Api>(relaxed = true) {
                coEvery { getSchools() } returns Response.success(schools)
                coEvery { getDetails() } throws Exception("Bar exception encountered")
            }

            // When
            val repository = Repository(mockApi)
            val response = repository.getSchools()

            // Then
            coVerify(exactly = 1) { mockApi.getSchools() }
            coVerify(exactly = 1) { mockApi.getDetails() }

            Assert.assertTrue(response.isFailure)
            val exception = response.exceptionOrNull()
            Assert.assertEquals("Bar exception encountered", exception!!.message)
        }
    }
}