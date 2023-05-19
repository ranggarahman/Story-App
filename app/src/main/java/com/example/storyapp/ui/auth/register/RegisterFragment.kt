package com.example.storyapp.ui.auth.register

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.storyapp.R
import com.example.storyapp.data.viewmodelfactory.AuthViewModelFactory
import com.example.storyapp.databinding.FragmentRegisterBinding
import com.example.storyapp.ui.auth.validator.ResultListener

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private lateinit var registerViewModel: RegisterViewModel
    private var resultListener: ResultListener? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val name = binding.registerName
        val email = binding.registerEmail
        val password = binding.registerPassword
        val register = binding.btnRegister

        registerViewModel =
            ViewModelProvider(this, AuthViewModelFactory(requireContext()))[RegisterViewModel::class.java]

        registerViewModel.registerFormState.observe(viewLifecycleOwner, Observer {
            val registerState = it ?: return@Observer

            register.isEnabled = registerState.isDataValid

            if(registerState.emailError != null){
                email.error = getString(registerState.emailError)
            }
            if(registerState.nameError != null){
                name.error = getString(registerState.nameError)
            }
            if(registerState.passwordError != null){
                password.error = getString(registerState.passwordError)
            }

        })

        registerViewModel.registerResult.observe(viewLifecycleOwner, Observer { event ->
            val registerResult = event.getContentIfNotHandled() ?: return@Observer

            resultListener?.onResult(REGISTER_RESULT_OK)

            if(registerResult.failedRegister != null){
                Toast.makeText(
                    activity,
                    registerResult.failedRegister.message,
                    Toast.LENGTH_SHORT
                ).show()
            }
            if(registerResult.successRegister != null){
                Toast.makeText(
                    activity,
                    getString(R.string.register_success),
                    Toast.LENGTH_SHORT
                ).show()
                findNavController().navigate(R.id.action_navigation_register_to_navigation_login)
            }
        })

        name.afterTextChanged {
            registerViewModel.registerDataChanged(
                name,
                name.text.toString(),
                email.text.toString(),
                password.text.toString()
            )
        }

        email.afterTextChanged {
            registerViewModel.registerDataChanged(
                email,
                name.text.toString(),
                email.text.toString(),
                password.text.toString()
            )
        }

        password.apply {
            afterTextChanged {
                registerViewModel.registerDataChanged(
                    password,
                    name.text.toString(),
                    email.text.toString(),
                    password.text.toString()
                )
            }

            setOnEditorActionListener { _, actionId, _ ->
                when (actionId){
                    EditorInfo.IME_ACTION_DONE ->
                        registerViewModel.register(
                            name.text.toString(),
                            email.text.toString(),
                            password.text.toString()
                        )
                }
                false
            }

            register.setOnClickListener {

                resultListener?.onResult(REGISTER_RESULT_ONGOING)

                try {
                    registerViewModel.register(
                        name.text.toString(),
                        email.text.toString(),
                        password.text.toString()
                    )
                } catch (e: Exception) {
                    Log.e(TAG, "$e")
                }
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

        val name = ObjectAnimator.ofFloat(binding.registerName, View.ALPHA, 1f).setDuration(500)
        val email = ObjectAnimator.ofFloat(binding.registerEmail, View.ALPHA, 1f).setDuration(500)
        val password = ObjectAnimator.ofFloat(binding.registerPassword, View.ALPHA, 1f).setDuration(500)
        val register = ObjectAnimator.ofFloat(binding.btnRegister, View.ALPHA, 1f).setDuration(500)

        AnimatorSet().apply {
            playSequentially(name, email, password, register)
            startDelay = 500
        }.start()
    }

    companion object{
        private const val TAG = "RegisterFragment"
        const val REGISTER_RESULT_OK = 235
        const val REGISTER_RESULT_ONGOING = 233
    }
}