package screens

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth

@Composable
fun LoginScreen() {

    val context = LocalContext.current

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),

        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(text = "Shale-NammaPride")

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
            },
            label = {
                Text("Email")
            }
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
            },
            label = {
                Text("Password")
            }
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {

                message = "Button Clicked"

                if (email.isEmpty() || password.isEmpty()) {

                    message = "Enter email and password"

                    return@Button
                }

                FirebaseAuth.getInstance()
                    .signInWithEmailAndPassword(email.trim(), password.trim())
                    .addOnSuccessListener {

                        message = "Login Successful"

                        Toast.makeText(
                            context,
                            "Login Successful",
                            Toast.LENGTH_LONG
                        ).show()

                    }
                    .addOnFailureListener {

                        message = it.message.toString()

                        Toast.makeText(
                            context,
                            it.message.toString(),
                            Toast.LENGTH_LONG
                        ).show()

                    }

            }
        ) {
            Text(text = "Login")
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(text = message)
    }
}