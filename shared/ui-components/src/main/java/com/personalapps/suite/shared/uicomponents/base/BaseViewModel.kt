package com.personalapps.suite.shared.uicomponents.base

import androidx.lifecycle.ViewModel
import com.personalapps.suite.shared.common.Result
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow

abstract class BaseViewModel<State, Effect>(initialState: State) : ViewModel() {

    private val _uiState = MutableStateFlow(initialState)
    val uiState: StateFlow<State> = _uiState.asStateFlow()

    private val _effect = Channel<Effect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    protected fun updateState(reducer: State.() -> State) {
        _uiState.value = _uiState.value.reducer()
    }

    protected fun sendEffect(effect: Effect) {
        _effect.trySend(effect)
    }

    protected fun <T> handleResult(
        result: Result<T>,
        onSuccess: (T) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        when (result) {
            is Result.Success -> onSuccess(result.data)
            is Result.Error -> onError(result.exception)
            Result.Loading -> { /* Handle loading if needed */ }
        }
    }
}
