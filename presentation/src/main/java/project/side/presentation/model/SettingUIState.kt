package project.side.presentation.model

sealed class SettingUIState {
    data object Init : SettingUIState()
    data object Loading : SettingUIState()
    data object LogoutSuccess : SettingUIState()
    data class Error(val message: String) : SettingUIState()
}
