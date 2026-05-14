package com.example.shale_nammapride.ui.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.SupervisorAccount
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shale_nammapride.R
import com.example.shale_nammapride.data.model.UserRole
import com.example.shale_nammapride.ui.components.SchoolButton
import com.example.shale_nammapride.ui.theme.AdminColor
import com.example.shale_nammapride.ui.theme.ParentColor
import com.example.shale_nammapride.ui.theme.SchoolPrimary
import com.example.shale_nammapride.ui.theme.TeacherColor

@Composable
fun LoginScreen(
    onLoginClick: (UserRole) -> Unit,
    onRegisterClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Welcome Back!",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = SchoolPrimary
        )
        Text(
            text = "Login to continue",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Placeholder for school building image
        Box(
            modifier = Modifier
                .size(200.dp)
                .background(Color.Transparent),
            contentAlignment = Alignment.Center
        ) {
            // Image(painter = painterResource(id = R.drawable.school_building), contentDescription = null)
            Icon(
                imageVector = Icons.Default.School,
                contentDescription = null,
                modifier = Modifier.size(120.dp),
                tint = SchoolPrimary.copy(alpha = 0.5f)
            )
        }

        Spacer(modifier = Modifier.height(48.dp))

        SchoolButton(
            text = "Admin Login",
            onClick = { onLoginClick(UserRole.ADMIN) },
            containerColor = AdminColor,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        SchoolButton(
            text = "Teacher Login",
            onClick = { onLoginClick(UserRole.TEACHER) },
            containerColor = TeacherColor,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        SchoolButton(
            text = "Parent Login",
            onClick = { onLoginClick(UserRole.PARENT) },
            containerColor = ParentColor,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row {
            Text(text = "Don't have an account? ")
            Text(
                text = "Register",
                color = SchoolPrimary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable { onRegisterClick() }
            )
        }
    }
}
