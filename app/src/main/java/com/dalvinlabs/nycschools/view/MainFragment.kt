package com.dalvinlabs.nycschools.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.dalvinlabs.nycschools.R
import com.dalvinlabs.nycschools.databinding.FragmentMainBinding
import com.dalvinlabs.nycschools.model.SchoolWithDetails
import com.dalvinlabs.nycschools.viewmodel.MainViewModel
import com.dalvinlabs.nycschools.viewmodel.MainViewModelFactory
import dagger.Binds
import dagger.Module
import dagger.Subcomponent
import dagger.android.AndroidInjector
import dagger.multibindings.ClassKey
import dagger.multibindings.IntoMap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@Subcomponent
interface MainFragmentSubcomponent : AndroidInjector<MainFragment> {
    @Subcomponent.Factory
    interface Factory : AndroidInjector.Factory<MainFragment>
}

@Module(subcomponents = [MainFragmentSubcomponent::class])
internal interface MainFragmentModule {
    @Binds
    @IntoMap
    @ClassKey(MainFragment::class)
    fun bindAndroidInjectorFactory(factory: MainFragmentSubcomponent.Factory?): AndroidInjector.Factory<*>?
}

class MainFragment : BaseFragment() {

    @Inject
    lateinit var mainViewModelFactory: MainViewModelFactory
    private lateinit var binding: FragmentMainBinding

    private fun showProgress() {
        binding.swipeRefreshView.isRefreshing = true
        binding.swipeRefreshView.isEnabled = true
    }

    private fun hideProgress() {
        binding.swipeRefreshView.isRefreshing = false
        binding.swipeRefreshView.isEnabled = false
    }

    private fun handleSuccess(success: List<SchoolWithDetails>?) {
        success?.takeIf { it.isNotEmpty() }
            ?.let {
                binding.recyclerView.adapter = Adapter(it.toMutableList())
            }
    }

    private fun handleError(error: Throwable?) {
        AlertDialog.Builder(requireContext())
            .setMessage(error?.message.orEmpty())
            .setPositiveButton(R.string.button_retry) { dialog, _ ->
                run {
                    dialog.dismiss()
                    retry()
                }
            }.setNegativeButton(R.string.button_exit) { dialog, _ ->
                run {
                    dialog.dismiss()
                    requireActivity().finish()
                }
            }.show()
    }

    private fun retry() {
        binding.viewModel?.loadSchools()
        showProgress()
    }

    private fun observeViewModel() {
        lifecycleScope.launch(Dispatchers.Main) {
            binding.viewModel?.getSchoolsStream()?.collect { result ->
                hideProgress()
                if (result.isSuccess) {
                    handleSuccess(result.getOrNull())
                } else if (result.isFailure) {
                    handleError(result.exceptionOrNull())
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(inflater)
        showProgress()
        binding.apply {
            recyclerView.layoutManager = LinearLayoutManager(activity)
            viewModel = ViewModelProvider(
                this@MainFragment,
                mainViewModelFactory
            )[MainViewModel::class.java]
        }
        observeViewModel()
        return binding.root
    }
}