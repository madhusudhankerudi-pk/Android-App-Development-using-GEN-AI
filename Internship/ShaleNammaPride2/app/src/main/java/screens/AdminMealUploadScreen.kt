package com.example.nammashale.screens
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun AdminMealUploadScreen() {

    Button(onClick = {

        val todayDate = "2026-05-11"

        FirebaseFirestore.getInstance()
            .collection("Meals")
            .document(todayDate)
            .get()
            .addOnSuccessListener {

                if (it.exists()) {
                    println("Meal already uploaded")
                } else {
                    println("Upload allowed")
                }
            }

    }) {

        Text(text = "Upload Meal")
    }
}