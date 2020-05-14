package com.example.android.trackmysleepquality.sleeptracker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.example.android.trackmysleepquality.R
import com.example.android.trackmysleepquality.database.SleepDatabase
import com.example.android.trackmysleepquality.databinding.FragmentSleepTrackerBinding

class SleepTrackerFragment : Fragment() {

    /**
     * Called when the Fragment is ready to display content to the screen.
     *
     * This function uses DataBindingUtil to inflate R.layout.fragment_sleep_quality.
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {


        val binding: FragmentSleepTrackerBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_sleep_tracker, container, false)

        /*Cria uma instância do ViewModelFactory*/
        val application = requireNotNull(this.activity).application
        val dataSource = SleepDatabase.getInstance(application).sleepDatabaseDao
        val viewModelFactory = SleepTrackerViewModelFactory(dataSource, application)


        /*Referência da ViewModel associada ao Fragment*/
        val sleepTrackerViewModel = ViewModelProviders.of(this, viewModelFactory)
                .get(SleepTrackerViewModel::class.java)


        /*Faz com que o ciclo de vida relacionado ao DataBinding seja o mesmo do Fragment*/
        binding.lifecycleOwner = this


        /*O databinding do viewModel seja o mesmo que nós definimos no Fragment*/
        binding.sleepTrackerViewModel = sleepTrackerViewModel


        return binding.root
    }
}
