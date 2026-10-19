package org.company.app.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kmplocalize.composeapp.generated.resources.Res
import kmplocalize.composeapp.generated.resources.welcome_to_pilloo_ai
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.meenagopal24.localization.local.getLocalString

data class HomeUiState(
    val message: String? = null,
    val isLoading: Boolean = false
)

class HomeViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    fun onAddClicked() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            // Using the suspend getLocalString function from localize library inside ViewModel
            val localizedString = getLocalString(Res.string.welcome_to_pilloo_ai)
            _uiState.update {
                it.copy(
                    message = "Added: $localizedString",
                    isLoading = false
                )
            }
        }
    }

    fun clearMessage() {
        _uiState.update { it.copy(message = null) }
    }
}
