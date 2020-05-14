package com.example.android.trackmysleepquality.sleeptracker

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.example.android.trackmysleepquality.database.SleepDatabaseDao
import com.example.android.trackmysleepquality.database.SleepNight
import com.example.android.trackmysleepquality.formatNights
import kotlinx.coroutines.*

class SleepTrackerViewModel(val database: SleepDatabaseDao, application: Application)
    : AndroidViewModel(application) {

    private var viewModelJob = Job()

    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private val nights = database.getAllNights()

    val nightString = Transformations.map(nights) {nights ->
        formatNights(nights, application.resources)
    }

    private var tonight = MutableLiveData<SleepNight>()

    init {
        initalizeTonight()
    }

    private fun initalizeTonight() {
        uiScope.launch {
            tonight.value = getTonightFromDatabase()
        }
    }

    private suspend fun getTonightFromDatabase(): SleepNight? {
        return withContext(Dispatchers.IO) {
            var night = database.getTonight() //pega a noite mais recente


            /*se o horario de inicio e o horario de fim não forem o mesmo,
                quer dizer que a night já foi completada e retorna nulo,
                senão retorna a night*/
            if (night?.endTimeMilli != night?.startTimeMilli) {
                night = null
            }


            night
        }
    }

    fun onStartTracking() {
        uiScope.launch {
            val newNight = SleepNight()


            insert(newNight)


            tonight.value = getTonightFromDatabase()
        }
    }

    fun onStopTracking() {
        uiScope.launch {
            val oldNight = tonight.value ?: return@launch


            oldNight.endTimeMilli = System.currentTimeMillis()
            update(oldNight)
        }
    }

    fun onClear() {
        uiScope.launch {
            clear()
            tonight.value = null
        }
    }

    private suspend fun insert(night: SleepNight) {
        withContext(Dispatchers.IO) {
            database.insert(night)
        }
    }

    private suspend fun update(night: SleepNight) {
        withContext(Dispatchers.IO) {
            database.update(night)
        }
    }

    private suspend fun clear() {
        withContext(Dispatchers.IO) {
            database.clear()
        }
    }





    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}

