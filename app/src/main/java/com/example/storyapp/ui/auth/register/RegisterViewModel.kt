package com.example.storyapp.ui.auth.register

import android.util.Log
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

class RegisterViewModel(private val authRepository: AuthRepository) : ViewModel() {

    private val _registerForm = MutableLiveData<AuthFormState>()
    val registerFormState: LiveData<AuthFormState> = _registerForm

    private val _registerResult = MutableLiveData<Event<AuthResult>>()
    val registerResult: LiveData<Event<AuthResult>> = _registerResult

    fun register(name: String, email: String, password: String) {
        viewModelScope.launch {
            val result = authRepository.register(name, email, password)

            if (result is Result.Success) {
                val successResult = AuthResult(successRegister = RegisteredUserView(message = result.data.message))
                _registerResult.value = Event(successResult)
                Log.d(TAG, "register function called : ${registerResult.value}")
            } else if (result is Result.Error){
                val errorResult = AuthResult(failedRegister = RegisteredUserView(message = result.exception))
                _registerResult.value = Event(errorResult)
                Log.e(TAG, "register function called : ${registerResult.value}")
            }
        }
    }

    fun registerDataChanged(editText: MyCustomEditText, name: String, email: String, password: String) {
        if (!editText.isNameValid(name)){
            _registerForm.value = AuthFormState(nameError = R.string.invalid_name)
        } else if (!editText.isEmailValid(email)){
            _registerForm.value = AuthFormState(emailError = R.string.invalid_email)
        } else if (!editText.isPasswordValid(password)){
            _registerForm.value = AuthFormState(passwordError = R.string.invalid_password)
        } else {
            _registerForm.value = AuthFormState(isDataValid = true)
        }
    }

    companion object{
        private const val TAG = "RegisterViewModel"
    }

}