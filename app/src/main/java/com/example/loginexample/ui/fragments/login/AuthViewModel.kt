package com.example.loginexample.ui.fragments.login

import android.util.Log
import android.view.View
import androidx.databinding.Bindable
import androidx.databinding.Observable
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.example.loginexample.models.User
import com.example.loginexample.repository.AuthRepository
import com.example.loginexample.util.StateListener
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class AuthViewModel @ViewModelInject constructor(private val authRepository: AuthRepository) :
    ViewModel(), Observable {

    var stateListener: StateListener? = null

    @Bindable
    val firstName = MutableLiveData<String>()

    @Bindable
    val lastName = MutableLiveData<String>()

    @Bindable
    val emailAddress = MutableLiveData<String>()

    @Bindable
    val phoneNumber = MutableLiveData<String>()

    @Bindable
    val password = MutableLiveData<String>()

    fun loginUser(view: View) {
        stateListener?.onLoading()

        if (emailAddress.value.isNullOrEmpty()) {
            stateListener?.onError("Enter email address")
            return
        } else if (password.value.isNullOrEmpty()) {
            stateListener?.onError("Enter password")
            return
        }


        viewModelScope.launch {
            try {
                val isUserExist = authRepository.checkUser(emailAddress.value!!)
                isUserExist.collect { user ->
                    when {
                        (emailAddress.value != user?.email) -> {
                            stateListener?.onError("User account not found")
                            return@collect
                        }
                        else -> {
                            val loginResponse =
                                authRepository.loginUser(emailAddress.value!!, password.value!!)

                            loginResponse.collect { user ->
                                when {
                                    (password.value != user?.password) -> {
                                        stateListener?.onError("Incorrect password")
                                        return@collect
                                    }
                                    else -> {
                                        stateListener?.onSuccess("Welcome, ${user?.firstName} ${user?.lastName}")

                                        authRepository.setUserLoggedIn(emailAddress.value!!)

                                        return@collect
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                stateListener?.onError(e.message!!)
                return@launch
            }
        }
    }

    fun registerUser(view: View) {
        when {
            firstName.value.isNullOrEmpty() -> {
                stateListener?.onError("Enter first name")
                return
            }
            lastName.value.isNullOrEmpty() -> {
                stateListener?.onError("Enter last name")
                return
            }
            emailAddress.value.isNullOrEmpty() -> {
                stateListener?.onError("Enter email address")
                return
            }
            phoneNumber.value.isNullOrEmpty() -> {
                stateListener?.onError("Enter phone number")
                return
            }
            password.value.isNullOrEmpty() -> {
                stateListener?.onError("Enter password")
                return
            }
        }

        viewModelScope.launch {
            try {
                val user = User(
                    null,
                    firstName.value!!,
                    lastName.value!!,
                    emailAddress.value!!,
                    phoneNumber.value!!,
                    password.value!!,
                    isUserLogged = false
                )

                authRepository.registerUser(user)

                stateListener?.onSuccess("Welcome, ${firstName.value} ${lastName.value}")

                authRepository.setUserLoggedIn(emailAddress.value!!)

                return@launch
            } catch (e: Exception) {
                stateListener?.onError(e.message!!)
                return@launch
            }
        }

    }

    private val loggedInUser = MutableLiveData<User>()

    fun isUserLoggedIn() = liveData {
        try {
            val isUserLoggedIn = authRepository.getLoggedInUser()
            isUserLoggedIn.collect {user ->
                loggedInUser.value = user
                if (user.isUserLogged) {
                    emit(true)
                }
            }
        } catch  (e: Exception) {
            emit(false)
        }
        return@liveData
    }

    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {}

    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {}
}