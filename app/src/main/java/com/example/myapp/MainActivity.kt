package com.example.myapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import com.example.myapp.databinding.ActivityMainBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var googleSignInClient: GoogleSignInClient

    private lateinit var firebaseAuth: FirebaseAuth

    companion object{
        private const val RC_SIGN = 100

        private const val TAG = "SIGN_IN_TAG"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN)
            .requestIdToken(R.string.default_web_client_id)
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions)

        firebaseAuth = FirebaseAuth.getInstance()
        checkUser()


        binding.SiginInButton.setOnClickListener {
            Log.d(TAG, "onCreate: Google Sign In")
            val intent = googleSignInClient.signInIntent
            startActivityForResult(intent, RC_SIGN)
        }
    }

    private fun checkUser() {

        val fireBaseUser = firebaseAuth.currentUser
        if(fireBaseUser != null){
            startActivity(Intent(this@MainActivity, ProfileFragment::class.java))
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d(TAG, "onActivityResult: Google SignIn Intent result")
        val accountTask = GoogleSignIn.getSignedInAccountFromIntent(data)
        try {
            val account = accountTask.getResult(ApiException::class.java)
            firebaseAuthWithGoogle(account)
        }catch (e: Exception){
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

                if(authResult.additionalUserInfo!!.isNewUser){
                    Log.d(TAG, "firebaseAuthWithGoogle: Account Created...")
                    Toast.makeText(this, "Account Created..." , Toast.LENGTH_SHORT).show()
                }
                else{
                    Log.d(TAG, "firebaseAuthWithGoogle: Existing User")
                    Toast.makeText(this, "Account Existing..." , Toast.LENGTH_SHORT).show()
                }
                startActivity(Intent(this@MainActivity, ProfileFragment::class.java))
                finish()
            }
            .addOnFailureListener { e ->
                Log.d(TAG, "firebaseAuthWithGoogle: Login Not Success - ${e.message}")
                Toast.makeText(this, "Login Not Success..." , Toast.LENGTH_SHORT).show()

            }
    }
}