package com.abyxcz.googlesso

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.abyxcz.googlesso.ui.theme.GoogleSSOTheme
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class MainActivity : ComponentActivity() {

    companion object {
        private val RC_SIGN_IN = 1234
        private val GOOGLE_SSO_TOKEN = "your_sign_in_token"
        val mFirebaseAuth = FirebaseAuth.getInstance()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val mainViewModel by viewModels<MainViewModel>()

        setContent {
            GoogleSSOTheme {
                // A surface container using the 'background' color from the theme
                
                var user = mainViewModel.state.collectAsStateWithLifecycle()

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting("Android")
                }
            }
        }

        initGoogleSSO()
    }


    fun initGoogleSSO(){

        val mSignInClient: GoogleSignInClient

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(GOOGLE_SSO_TOKEN)
            .requestEmail()
            .build()
        mSignInClient = GoogleSignIn.getClient(this, gso)

        val signInIntent: Intent = mSignInClient.getSignInIntent()
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            println("$resultCode $data")
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                //TODO:
                // This error (nullable field) is removed in at least @tasks.18.0.2, bundled version should be updated
                // And investigate cause of this dependency mismatch
                val account: GoogleSignInAccount? = task.getResult(ApiException::class.java)
                /* continue with Google account */
                firebaseAuthWithGoogle(account)
            } catch (e: ApiException) {
                Log.w("MainActivity", "Google sign in failed", e)
            }
        }
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount?) {
        acct?.let {
            val credential = GoogleAuthProvider.getCredential(it.idToken, null)
            mFirebaseAuth.signInWithCredential(credential)
                .addOnSuccessListener(this) { authResult ->
                    /* continue with Firebase User */

                }
                .addOnFailureListener(this) { e ->
                    Toast.makeText(
                        this@MainActivity, "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    GoogleSSOTheme {
        Greeting("Android")
    }
}