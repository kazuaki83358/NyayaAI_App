package com.example.nyayaai.ui.screens.signup

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(
    onSignUpSuccess: () -> Unit,
    onSignInClick: () -> Unit
) {
    val brandOrange = Color(0xFFD97706)
    val backgroundCream = Color(0xFFFDF8F6)
    val darkBlueText = Color(0xFF0F172A)

    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var agreedToTerms by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundCream)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.Start
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // 1. Top Logo and Skip Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        modifier = Modifier.size(48.dp),
                        color = brandOrange,
                        shape = RoundedCornerShape(12.dp),
                        shadowElevation = 4.dp
                    ) {
                        Icon(
                            painter = painterResource(id = android.R.drawable.ic_menu_sort_by_size),
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "NyayaAI",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = brandOrange,
                        fontFamily = FontFamily.Serif
                    )
                }
                Text(
                    text = "Skip for now →",
                    color = Color.Gray,
                    fontSize = 14.sp,
                    modifier = Modifier.clickable { }
                )
            }

            Spacer(modifier = Modifier.height(30.dp))

            // 2. Main Container Card
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(20.dp, RoundedCornerShape(32.dp)),
                shape = RoundedCornerShape(32.dp),
                color = Color.White
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text(
                        text = "Create Account",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = darkBlueText,
                        fontFamily = FontFamily.Serif
                    )
                    Text(
                        text = "Start your journey with NyayaAI today",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    if (errorMessage != null) {
                        Text(
                            text = errorMessage!!,
                            color = Color.Red,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                    }

                    // Input Fields
                    SignUpInputField(
                        label = "Full Name",
                        placeholder = "Your full name",
                        value = fullName,
                        onValueChange = { fullName = it }
                    )
                    SignUpInputField(
                        label = "Email Address",
                        placeholder = "you@example.com",
                        value = email,
                        onValueChange = { email = it }
                    )
                    SignUpInputField(
                        label = "Phone Number",
                        placeholder = "+91 98765 43210",
                        value = phone,
                        onValueChange = { phone = it }
                    )
                    SignUpInputField(
                        label = "Password",
                        placeholder = "Create a strong password",
                        value = password,
                        onValueChange = { password = it },
                        isPassword = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // 3. Terms and Conditions Checkbox
                    Row(verticalAlignment = Alignment.Top) {
                        Checkbox(
                            checked = agreedToTerms,
                            onCheckedChange = { agreedToTerms = it },
                            modifier = Modifier.offset(y = (-12).dp)
                        )
                        Text(
                            text = buildAnnotatedString {
                                append("I agree to the ")
                                withStyle(style = SpanStyle(color = brandOrange, fontWeight = FontWeight.Bold)) {
                                    append("Terms of Service")
                                }
                                append(" and ")
                                withStyle(style = SpanStyle(color = brandOrange, fontWeight = FontWeight.Bold)) {
                                    append("Privacy Policy")
                                }
                            },
                            fontSize = 13.sp,
                            lineHeight = 18.sp,
                            color = Color.DarkGray
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // 4. Create Account Button
                    Button(
                        onClick = {
                            if (fullName.isBlank() || email.isBlank() || phone.isBlank() || password.isBlank()) {
                                errorMessage = "Please fill in all fields"
                                return@Button
                            }
                            if (!agreedToTerms) {
                                errorMessage = "You must agree to the Terms & Conditions"
                                return@Button
                            }
                            isLoading = true
                            errorMessage = null

                            auth.createUserWithEmailAndPassword(email, password)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        val uid = task.result?.user?.uid
                                        if (uid != null) {
                                            val userMap = hashMapOf(
                                                "uid" to uid,
                                                "fullName" to fullName,
                                                "email" to email,
                                                "phone" to phone,
                                                "role" to "pending"
                                            )
                                            firestore.collection("users").document(uid)
                                                .set(userMap)
                                                .addOnSuccessListener {
                                                    isLoading = false
                                                    onSignUpSuccess()
                                                }
                                                .addOnFailureListener { e ->
                                                    isLoading = false
                                                    errorMessage = "Account created, but profile failed: ${e.localizedMessage}"
                                                }
                                        } else {
                                            isLoading = false
                                            errorMessage = "User ID was not generated."
                                        }
                                    } else {
                                        isLoading = false
                                        errorMessage = task.exception?.localizedMessage ?: "Registration failed."
                                    }
                                }
                        },
                        enabled = !isLoading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = brandOrange)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                        } else {
                            Text("Create Account", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // 5. Sign In Link
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text("Already have an account? ", color = Color.Gray)
                        Text(
                            text = "Sign in",
                            color = brandOrange,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable { onSignInClick() }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpInputField(
    label: String,
    placeholder: String,
    value: String,
    onValueChange: (String) -> Unit,
    isPassword: Boolean = false
) {
    var passwordVisible by remember { mutableStateOf(false) }

    Column(modifier = Modifier.padding(bottom = 20.dp)) {
        Text(
            text = label,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = Color(0xFF374151)
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = Color.LightGray) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            visualTransformation = if (isPassword && !passwordVisible) PasswordVisualTransformation() else VisualTransformation.None,
            trailingIcon = if (isPassword) {
                {
                    val image = if (passwordVisible) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(image, contentDescription = if (passwordVisible) "Hide password" else "Show password")
                    }
                }
            } else null,
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Color(0xFFE5E7EB),
                focusedBorderColor = Color(0xFFD97706),
                unfocusedContainerColor = Color.White,
                focusedContainerColor = Color.White,
                unfocusedTextColor = Color.Black,
                focusedTextColor = Color.Black
            )
        )
    }
}