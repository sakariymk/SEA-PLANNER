package no.uio.ifi.in2000.gruppe40.prosjekt.ui.home

import androidx.compose.runtime.mutableFloatStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.gruppe40.prosjekt.data.grib.GribRepository

class HomeScreenViewModel : ViewModel() {
    private val _gribRepositoryState = MutableStateFlow(GribRepository())
    val gribRepositoryState: StateFlow<GribRepository> = _gribRepositoryState.asStateFlow()

    private val _searchMode = MutableStateFlow(false)
    val searchMode: StateFlow<Boolean> = _searchMode.asStateFlow()

    var upperValue = mutableFloatStateOf(1f)
        private set

    var maxSliderValue = mutableFloatStateOf(10f)
        private set

    fun toggleSearchMode() {
        _searchMode.value = !_searchMode.value
    }

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val gribRepository = GribRepository()
                gribRepository.createRepository()
                _gribRepositoryState.value = gribRepository
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getGribRepository(): StateFlow<GribRepository> {
        return gribRepositoryState
    }
}
