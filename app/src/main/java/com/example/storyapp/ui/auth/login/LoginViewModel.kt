package com.example.storyapp.ui.auth.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.storyapp.R
import com.example.storyapp.data.Result
import com.example.storyapp.data.repository.AuthRepository
import com.example.storyapp.ui.auth.validator.AuthFormState
import com.example.storyapp.ui.auth.validator.AuthResult
import com.example.storyapp.ui.customview.MyCustomEditText
import com.example.storyapp.utils.Event
import kotlinx.coroutines.launch

class LoginViewModel(private val authRepository: AuthRepository) : ViewModel() {

    private val _loginForm = MutableLiveData<AuthFormState>()
    val loginFormState: LiveData<AuthFormState> = _loginForm

    private val _loginResult = MutableLiveData<Event<AuthResult>>()
    val loginResult: LiveData<Event<AuthResult>> = _loginResult

    fun login(username: String, password: String) {
        // can be launched in a separate asynchronous job
        viewModelScope.launch {
            val result = authRepository.login(username, password)

            if (result is Result.Success) {
                _loginResult.value = Event(
                    AuthResult(successLogin = LoggedInUserView(
                        message = result.data.name.toString(),
                        token = result.data.token.toString())))
            } else if (result is Result.Error) {
                _loginResult.value = Event(AuthResult(failedLogin = LoggedInUserView(
                    message = result.exception,
                    token = null
                )))
            }
        }
    }

    fun loginDataChanged(editText: MyCustomEditText, email : String, password: String) {
        if (!editText.isEmailValid(email)) {
            _loginForm.value = AuthFormState(emailError = R.string.invalid_email)
        }
        else if (!editText.isPasswordValid(password)){
            _loginForm.value = AuthFormState(passwordError = R.string.invalid_password)
        } else {
            _loginForm.value = AuthFormState(isDataValid = true)
        }
    }
}