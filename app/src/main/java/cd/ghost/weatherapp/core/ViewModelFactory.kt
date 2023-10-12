package cd.ghost.weatherapp.core

import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel

class ViewModelFactory<VM : ViewModel>(
    private val viewModel: () -> VM
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return viewModel() as T
    }
}

inline fun <reified VM : ViewModel> ComponentActivity.viewModel(
    noinline viewModel: () -> VM
) = viewModels<VM> {
    ViewModelFactory(viewModel)
}

@Composable
inline fun <reified VM : ViewModel> screenViewModel(
    noinline viewModel: () -> VM
): VM = viewModel(
    factory = ViewModelFactory(viewModel)
)

