package cd.ghost.weatherapp.screens

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cd.ghost.weatherapp.core.Container
import cd.ghost.weatherapp.data.Repository
import cd.ghost.weatherapp.data.model.WhetherModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainViewModel(
    private val repository: Repository,
) : ViewModel() {

    private val _state = MutableStateFlow<Container<WhetherModel>>(Container.Pending)
    val state = _state.asStateFlow()

    private val TAG = "HomeViewModel"

    fun fetchData() {
        viewModelScope.launch {
            repository.getDailyForecast()
                .catch {
                    Log.d(TAG, "Error from viewModel: $it")
                    _state.value = Container.Error(it)
                }
                .collectLatest {
                    _state.value = it
                }
        }
    }
}