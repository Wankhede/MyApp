package com.example.myapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.example.myapp.databinding.ActivityMainBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

import androidx.fragment.app.FragmentTransaction


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var googleSignInClient: GoogleSignInClient

    private lateinit var firebaseAuth: FirebaseAuth

    var SignInbutton: LinearLayout? = null
    var cardview2: CardView? = null
    var cardview1: View? = null

    var frmtxtv : ConstraintLayout? = null

    companion object {
        private const val RC_SIGN = 100

        private const val TAG = "SIGN_IN_TAG"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)




        SignInbutton = findViewById(R.id.SiginInButtonn)
        cardview2 = findViewById(R.id.cardView2)
        cardview1 = findViewById(R.id.view)
        frmtxtv = findViewById(R.id.frametextview)
        firebaseAuth = FirebaseAuth.getInstance()

        checkUser()
        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_idd))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions)




        binding.SiginInButtonn.setOnClickListener {
            Log.d(TAG, "onCreate: Google Sign In")
            val intent = googleSignInClient.signInIntent
            Log.d(TAG, "onCreate: Google Sign Iyyyyn")

            cardview2!!.isVisible = false
            cardview1!!.isVisible = false
            startActivityForResult(intent, RC_SIGN)
            Log.d(TAG, "onCreate: Google Sign Innnnn")


        }
    }

    private fun checkUser() {

        val fireBaseUser = firebaseAuth.currentUser
        if (fireBaseUser != null) {
            val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, NotesFragment())
            transaction.addToBackStack(null)
            transaction.commit()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d(TAG, "onActivityResult: Google SignIn Intent result")
        val accountTask = GoogleSignIn.getSignedInAccountFromIntent(data)
        try {
            val account = accountTask.getResult(ApiException::class.java)
            firebaseAuthWithGoogle(account)
        } catch (e: Exception) {
            Log.d(TAG, "onActivityResult:  ${e.message} ")
        }
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount?) {
        Log.d(TAG, "firebaseAuthWithGoogle: Begin Firebase auth with Google")

        val crediential = GoogleAuthProvider.getCredential(account!!.idToken, null)
        firebaseAuth.signInWithCredential(crediential)
            .addOnSuccessListener { authResult ->
                Log.d(TAG, "firebaseAuthWithGoogle: Loginsuccess")
                val fireBaseUser = firebaseAuth.currentUser
                val fireBaseUID = fireBaseUser!!.uid
                val fireBaseEmail = fireBaseUser!!.email
                Log.d(TAG, "firebaseAuthWithGoogle: Email - $fireBaseEmail")
                Log.d(TAG, "firebaseAuthWithGoogle: UID - $fireBaseUID")

                if (authResult.additionalUserInfo!!.isNewUser) {
                    Log.d(TAG, "firebaseAuthWithGoogle: Account Created...")
                    Toast.makeText(this, "Account Created...", Toast.LENGTH_SHORT).show()
                } else {
                    Log.d(TAG, "firebaseAuthWithGoogle: Existing User")
                    Toast.makeText(this, "Account Existing...", Toast.LENGTH_SHORT).show()
                }
                val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
                transaction.replace(R.id.fragment_container, NotesFragment())
                transaction.addToBackStack(null)
                transaction.commit()
            }
            .addOnFailureListener { e ->
                Log.d(TAG, "firebaseAuthWithGoogle: Login Not Success - ${e.message}")
                Toast.makeText(this, "Login Not Success...", Toast.LENGTH_SHORT).show()

            }
    }
}