package com.dalvinlabs.nycschools.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.dalvinlabs.nycschools.model.Repository
import com.dalvinlabs.nycschools.model.SchoolWithDetails
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import javax.inject.Singleton

@Module
class MainViewModelFactoryModule {

    @Singleton
    @Provides
    fun providesFactory(repository: Repository): MainViewModelFactory {
        return MainViewModelFactory(repository)
    }
}

class MainViewModelFactory constructor(
    private val repository: Repository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(repository::class.java, CoroutineDispatcher::class.java)
            .newInstance(repository, Dispatchers.IO)
    }
}

class MainViewModel constructor(
    private val repository: Repository,
    private val dispatcher: CoroutineDispatcher
) : ViewModel() {
    private val schools: MutableStateFlow<Result<List<SchoolWithDetails>>?> = MutableStateFlow(null)

    init {
        loadSchools()
    }

    fun getSchoolsStream() = schools.asStateFlow().filterNotNull()

    fun loadSchools() {
        viewModelScope.launch(dispatcher) {
            repository.getSchools().let {
                schools.value = it
            }
        }
    }
}