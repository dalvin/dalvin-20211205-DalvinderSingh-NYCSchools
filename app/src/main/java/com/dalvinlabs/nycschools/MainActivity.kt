package com.dalvinlabs.nycschools

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.dalvinlabs.nycschools.view.MainFragment
import com.dalvinlabs.nycschools.view.MainFragmentModule
import dagger.Binds
import dagger.Module
import dagger.Subcomponent
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import dagger.multibindings.ClassKey
import dagger.multibindings.IntoMap
import javax.inject.Inject

@Subcomponent(modules = [MainFragmentModule::class])
interface MainActivitySubcomponent : AndroidInjector<MainActivity> {
    @Subcomponent.Factory
    interface Factory : AndroidInjector.Factory<MainActivity>
}

@Module(subcomponents = [MainActivitySubcomponent::class])
internal interface MainActivityModule {
    @Binds
    @IntoMap
    @ClassKey(MainActivity::class)
    fun bindAndroidInjectorFactory(factory: MainActivitySubcomponent.Factory?): AndroidInjector.Factory<*>?
}

class MainActivity : AppCompatActivity(), HasAndroidInjector {

    @Inject
    lateinit var fragmentInjector: DispatchingAndroidInjector<Any>

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun androidInjector(): AndroidInjector<Any> = fragmentInjector
}