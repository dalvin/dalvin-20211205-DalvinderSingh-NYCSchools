package com.dalvinlabs.nycschools.model

import dagger.Module
import dagger.Provides

@Module(includes = [ApiModule::class])
class RepositoryModule {

    @Provides
    fun providesRepository(api: Api): Repository = Repository(api)
}

private fun String?.orNA(): String {
    return this ?: "NA"
}

class Repository constructor(private val api: Api) {

    suspend fun getSchools(): Result<List<SchoolWithDetails>> {
        val schoolsWithDetails = mutableListOf<SchoolWithDetails>()
        return kotlin.runCatching {
            val schools = api.getSchools().body().orEmpty()
            val details = api.getDetails().body().orEmpty()
            schools.forEach { school ->
                details.find { school.dbn == it.dbn }
                    .let {
                        schoolsWithDetails.add(
                            SchoolWithDetails(
                                name = school.name,
                                reading = it?.reading.orNA(),
                                writing = it?.writing.orNA(),
                                math = it?.math.orNA()
                            )
                        )
                    }
            }
            schoolsWithDetails
        }
    }
}