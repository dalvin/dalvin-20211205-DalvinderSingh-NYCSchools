package viewmodel

import com.dalvinlabs.nycschools.model.Repository
import com.dalvinlabs.nycschools.model.SchoolWithDetails
import com.dalvinlabs.nycschools.viewmodel.MainViewModel
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class MainViewModelTest {

    @ExperimentalCoroutinesApi
    private var testDispatcher: TestCoroutineDispatcher? = null

    @ExperimentalCoroutinesApi
    @Before
    fun setup() {
        testDispatcher = TestCoroutineDispatcher()
        Dispatchers.setMain(testDispatcher!!)
    }

    @ExperimentalCoroutinesApi
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `Given repository provides data when get stream then receive stream events`() {

        runBlocking {
            // Given
            val mockRepository = mockk<Repository>()
            val schoolWithDetailsFoo = SchoolWithDetails(
                name = "Foo",
                reading = "1",
                writing = "2",
                math = "3"
            )
            val schoolWithDetailsBar = SchoolWithDetails(
                name = "Bar",
                reading = "5",
                writing = "10",
                math = "15"
            )
            coEvery { mockRepository.getSchools() } returns Result.success(
                listOf(
                    schoolWithDetailsFoo,
                    schoolWithDetailsBar
                )
            )

            // When
            val mainViewModel = MainViewModel(mockRepository, testDispatcher!!)
            val job = launch {
                mainViewModel.getSchoolsStream().collect {
                    println(it)
                    Assert.assertNotNull(it)
                    it!!
                    Assert.assertTrue(it.isSuccess)
                    val schoolWithDetails = it.getOrNull()
                    Assert.assertNotNull(schoolWithDetails)
                    schoolWithDetails!!
                    Assert.assertEquals(schoolWithDetails.size, 2)

                    Assert.assertEquals(schoolWithDetails[0].name, "Foo")
                    Assert.assertEquals(schoolWithDetails[0].reading, "1")
                    Assert.assertEquals(schoolWithDetails[0].writing, "2")
                    Assert.assertEquals(schoolWithDetails[0].math, "3")

                    Assert.assertEquals(schoolWithDetails[1].name, "Bar")
                    Assert.assertEquals(schoolWithDetails[1].reading, "5")
                    Assert.assertEquals(schoolWithDetails[1].writing, "10")
                    Assert.assertEquals(schoolWithDetails[1].math, "15")
                    cancel()
                }
            }
            job.join()
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `Given repository provides error when get stream then receive stream events`() {

        runBlocking {
            // Given
            val mockRepository = mockk<Repository>()
            coEvery { mockRepository.getSchools() } returns Result.failure(Exception("Foo exception encountered"))

            // When
            val mainViewModel = MainViewModel(mockRepository, testDispatcher!!)
            val job = launch {
                mainViewModel.getSchoolsStream().collect {
                    println(it)
                    Assert.assertNotNull(it)
                    it!!
                    Assert.assertTrue(it.isFailure)
                    val exception = it.exceptionOrNull()
                    Assert.assertEquals("Foo exception encountered", exception!!.message)
                    cancel()
                }
            }
            job.join()
        }
    }
}