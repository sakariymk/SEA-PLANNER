package no.uio.ifi.in2000.gruppe40.prosjekt.ui.boat


import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.gruppe40.prosjekt.data.boat.Boat
import no.uio.ifi.in2000.gruppe40.prosjekt.data.boat.BoatRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class BoatViewModel : ViewModel() {

    private val repository = BoatRepository()

    // below are the types of boats
    private val _allBoats = MutableStateFlow<List<Boat>>(emptyList())
    val allBoats: StateFlow<List<Boat>> = _allBoats

    private val _fishingBoats = MutableStateFlow<List<Boat>>(emptyList())
    val fishingBoats: StateFlow<List<Boat>> = _fishingBoats

    private val _towingBoats = MutableStateFlow<List<Boat>>(emptyList())
    val towingBoats: StateFlow<List<Boat>> = _towingBoats

    private val _sailingBoats = MutableStateFlow<List<Boat>>(emptyList())
    val sailingBoats: StateFlow<List<Boat>> = _sailingBoats

    private val _commercialBoats = MutableStateFlow<List<Boat>>(emptyList())
    val commercialBoats: StateFlow<List<Boat>> = _commercialBoats

    private val _enforcementBoats = MutableStateFlow<List<Boat>>(emptyList())
    val enforcementBoats: StateFlow<List<Boat>> = _enforcementBoats

    private val _divingBoats = MutableStateFlow<List<Boat>>(emptyList())
    val divingBoats: StateFlow<List<Boat>> = _divingBoats

    private val _medicalBoats = MutableStateFlow<List<Boat>>(emptyList())
    val medicalBoats: StateFlow<List<Boat>> = _medicalBoats

    // a loading state
    private val _isLoading = MutableStateFlow(false)

    init {
        fetchBoats()
    }

    fun fetchBoats() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.createRepository()
                // would eventually move this on init if it is sufficient to initialize this viewmodel once


                _allBoats.value = repository.getBoatList()
                _fishingBoats.value = repository.getFishingBoats()
                _towingBoats.value = repository.getTowingBoats()
                _sailingBoats.value = repository.getSailingBoats()
                _commercialBoats.value = repository.getCommercialBoats()
                _enforcementBoats.value = repository.getLawEnforcmentBoats()
                _divingBoats.value = repository.getDivingBoats()
                _medicalBoats.value = repository.getMedicalBoats()

                _allBoats.value.forEach {
                    Log.d("BoatDebug", "Båt: ${it.name} (${it.latitude}, ${it.longitude})")
                }
            } catch (e: Exception) {
                Log.e("BoatViewModel", "Exception at loading boats: ${e.message}", e)
            } finally {
                _isLoading.value = false
            }
        }
    }
}
