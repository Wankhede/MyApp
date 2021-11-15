package com.example.myapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import com.google.firebase.*
import androidx.fragment.app.FragmentManager
import com.example.myapp.databinding.ActivityMainBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import androidx.fragment.app.FragmentTransaction


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [LoginFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LoginFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var binding: ActivityMainBinding

    private lateinit var googleSignInClient: GoogleSignInClient

    private lateinit var firebaseAuth: FirebaseAuth

    var signInButton: Button? = null

    companion object {

        fun newInstance(param1: String, param2: String) =
            LoginFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)

                }
            }

        private const val RC_SIGN = 100

        private const val TAG = "SIGN_IN_TAG"
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val v= inflater.inflate(R.layout.fragment_profile, container, false)

        signInButton = v.findViewById(R.id.SiginInButtonn)
        

        // Inflate the layout for this fragment
        binding = ActivityMainBinding.inflate(layoutInflater)
        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_idd))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(context, googleSignInOptions)

        firebaseAuth = FirebaseAuth.getInstance()



//        binding.signInButton!!.setOnClickListener {
//            val intent = googleSignInClient.signInIntent
//            startActivityForResult(intent, RC_SIGN)
//        }


//        SignInButton!!.setOnClickListener{
//            val fragment: Fragment = ProfileFragment()
//            val fragmentManager = requireActivity().supportFragmentManager
//            val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
//            fragmentTransaction.replace(R.id.fragment_container, fragment)
//            fragmentTransaction.addToBackStack(null)
//            fragmentTransaction.commit()
//        }
//        SignInButton!!.setOnClickListener {
//            val intent = googleSignInClient.signInIntent
//            startActivityForResult(intent, RC_SIGN)
//        }


        return inflater.inflate(R.layout.fragment_login, container, false)
    }



    private fun setFragment(fragment : Fragment) {
        getFragmentManager()
            ?.beginTransaction()
            ?.replace(R.id.fragment_container,fragment)
            ?.commit()
    }




    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d(LoginFragment.TAG, "onActivityResult: Google SignIn Intent result")
        val accountTask = GoogleSignIn.getSignedInAccountFromIntent(data)
        try {
            val account = accountTask.getResult(ApiException::class.java)
            firebaseAuthWithGoogle(account)
        }catch (e: Exception){
            Log.d(LoginFragment.TAG, "onActivityResult:  ${e.message} ")
        }
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount?) {
        Log.d(LoginFragment.TAG, "firebaseAuthWithGoogle: Begin Firebase auth with Google")

        val crediential = GoogleAuthProvider.getCredential(account!!.idToken, null)
        firebaseAuth.signInWithCredential(crediential)
            .addOnSuccessListener { authResult ->
                Log.d(LoginFragment.TAG, "firebaseAuthWithGoogle: Loginsuccess")
                val fireBaseUser = firebaseAuth.currentUser
                val fireBaseUID = fireBaseUser!!.uid
                val fireBaseEmail = fireBaseUser!!.email
                Log.d(LoginFragment.TAG, "firebaseAuthWithGoogle: Email - $fireBaseEmail")
                Log.d(LoginFragment.TAG, "firebaseAuthWithGoogle: UID - $fireBaseUID")

                if (authResult.additionalUserInfo!!.isNewUser) {
                    Log.d(LoginFragment.TAG, "firebaseAuthWithGoogle: Account Created...")
                    Toast.makeText(context, "Account Created...", Toast.LENGTH_SHORT).show()
                } else {
                    Log.d(LoginFragment.TAG, "firebaseAuthWithGoogle: Existing User")
                    Toast.makeText(context, "Account Existing...", Toast.LENGTH_SHORT).show()
                }

            }
            .addOnFailureListener { e ->
                Log.d(LoginFragment.TAG, "firebaseAuthWithGoogle: Login Not Success - ${e.message}")
                Toast.makeText(context, "Login Not Success...", Toast.LENGTH_SHORT).show()

            }
    }



}