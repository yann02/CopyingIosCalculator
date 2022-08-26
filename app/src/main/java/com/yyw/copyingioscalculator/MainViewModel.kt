package com.yyw.copyingioscalculator

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class MainViewModel : ViewModel() {
    private var _mAppState = MutableStateFlow(AppStateUI())
    val mAppState: StateFlow<AppStateUI> = _mAppState
    fun updateAppState(appStateUI: AppStateUI) {
        _mAppState.update { appStateUI }
    }
}