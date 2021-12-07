package com.dalvinlabs.nycschools.di

import android.content.Context
import com.dalvinlabs.nycschools.MainActivityModule
import com.dalvinlabs.nycschools.MyApplication
import com.dalvinlabs.nycschools.model.RepositoryModule
import com.dalvinlabs.nycschools.viewmodel.MainViewModelFactoryModule
import dagger.BindsInstance
import dagger.Component
import dagger.Module
import dagger.Provides
import dagger.android.support.AndroidSupportInjectionModule
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AndroidSupportInjectionModule::class,
        MainActivityModule::class,
        RetrofitModule::class,
        RepositoryModule::class,
        MainViewModelFactoryModule::class
    ]
)

interface AppComponent {
    fun inject(application: MyApplication)

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun bindsContext(context: Context): Builder
        fun build(): AppComponent
    }
}

@Module(includes = [HttpModule::class])
class RetrofitModule {

    @Singleton
    @Provides
    fun providesRetrofitBuilder(
        okHttpClient: OkHttpClient
    ): Retrofit.Builder {
        val builder = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
        return builder.client(okHttpClient)
    }
}

@Module
class HttpModule {

    @Singleton
    @Provides
    fun providesOkHttpClient(
    ): OkHttpClient {
        //Configure builder e.g. interceptors, cookie jar, cache etc.
        return OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .build()
    }
}