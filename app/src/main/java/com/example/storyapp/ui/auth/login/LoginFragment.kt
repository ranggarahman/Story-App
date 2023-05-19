package com.example.storyapp.ui.auth.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.storyapp.R
import com.example.storyapp.data.viewmodelfactory.AuthViewModelFactory
import com.example.storyapp.databinding.FragmentLoginBinding
import com.example.storyapp.ui.auth.validator.ResultListener

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private lateinit var loginViewModel: LoginViewModel
    private var resultListener: ResultListener? = null


    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentLoginBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val email = binding.loginEmail
        val password = binding.loginPassword
        val login = binding.btnLogin

        loginViewModel = ViewModelProvider(this, AuthViewModelFactory(requireContext()))[LoginViewModel::class.java]

        loginViewModel.loginFormState.observe(viewLifecycleOwner, Observer {
            //make sure observer handles once
            val loginState = it ?: return@Observer

            // disable login button unless both username / password is valid
            login.isEnabled = loginState.isDataValid

            if (loginState.emailError != null) {
                email.error = getString(loginState.emailError)
            }
            if (loginState.passwordError != null) {
                password.error = getString(loginState.passwordError)
            }
        })

        loginViewModel.loginResult.observe(viewLifecycleOwner, Observer {event ->
            //make sure observer handles once
            val loginResult = event.getContentIfNotHandled() ?: return@Observer

            resultListener?.onResult(LOGIN_RESULT_OK)

            if (loginResult.failedLogin != null) {
                Toast.makeText(activity,
                    loginResult.failedLogin.message,
                    Toast.LENGTH_SHORT
                ).show()
            }
            if (loginResult.successLogin != null) {

                val bundle = bundleOf(EXTRA_BUNDLE to loginResult.successLogin.token)
                findNavController().navigate(
                    R.id.action_navigation_login_to_storyListActivity,
                    bundle
                )

                activity?.finish()
            }
        })

        email.afterTextChanged {
            loginViewModel.loginDataChanged(
                email,
                email.text.toString(),
                password.text.toString()
            )
        }

        password.apply {
            afterTextChanged {
                loginViewModel.loginDataChanged(
                    password,
                    email.text.toString(),
                    password.text.toString()
                )
            }

            setOnEditorActionListener { _, actionId, _ ->
                when (actionId){
                    EditorInfo.IME_ACTION_DONE ->
                        loginViewModel.login(
                            email.text.toString(),
                            password.text.toString()
                        )
                }
                false
            }

            login.setOnClickListener {

                resultListener?.onResult(LOGIN_RESULT_ONGOING)

                loginViewModel.login(
                    email.text.toString(),
                    password.text.toString())
            }

        }

        playAnimation()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is ResultListener) {
            resultListener = context
        }
    }

    private fun playAnimation() {

        val email = ObjectAnimator.ofFloat(binding.loginEmail, View.ALPHA, 1f).setDuration(500)
        val password = ObjectAnimator.ofFloat(binding.loginPassword, View.ALPHA, 1f).setDuration(500)
        val login = ObjectAnimator.ofFloat(binding.btnLogin, View.ALPHA, 1f).setDuration(500)

        AnimatorSet().apply {
            playSequentially(email, password, login)
            startDelay = 500
        }.start()
    }

    companion object {
        const val EXTRA_BUNDLE = "extra_bundle"
        const val LOGIN_RESULT_OK = 135
        const val LOGIN_RESULT_ONGOING = 133
    }

}