package com.example.nyayaai.ui.screens.signup

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Visibility
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
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(
    onSignUpSuccess: () -> Unit,
    onSignInClick: () -> Unit
) {
    val brandOrange = Color(0xFFD97706)
    val backgroundCream = Color(0xFFFDF8F6)
    val darkBlueText = Color(0xFF0F172A)

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
                        // REPLACE with your actual logo
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

                    Spacer(modifier = Modifier.height(32.dp))

                    // Input Fields
                    SignUpInputField(label = "Full Name", placeholder = "Your full name")
                    SignUpInputField(label = "Email Address", placeholder = "you@example.com")
                    SignUpInputField(label = "Phone Number", placeholder = "+91 98765 43210")
                    SignUpInputField(
                        label = "Password",
                        placeholder = "Create a strong password",
                        isPassword = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // 3. Terms and Conditions Checkbox
                    Row(verticalAlignment = Alignment.Top) {
                        Checkbox(
                            checked = false,
                            onCheckedChange = {},
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
                        onClick = { onSignUpSuccess() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .shadow(8.dp, RoundedCornerShape(12.dp)),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = brandOrange)
                    ) {
                        Text("Create Account", fontSize = 16.sp, fontWeight = FontWeight.Bold)
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
fun SignUpInputField(label: String, placeholder: String, isPassword: Boolean = false) {
    Column(modifier = Modifier.padding(bottom = 20.dp)) {
        Text(
            text = label,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = Color(0xFF374151)
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = "",
            onValueChange = {},
            placeholder = { Text(placeholder, color = Color.LightGray) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            trailingIcon = if (isPassword) {
                { Icon(Icons.Outlined.Visibility, contentDescription = null, tint = Color.Gray) }
            } else null,
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Color(0xFFE5E7EB),
                focusedBorderColor = Color(0xFFD97706),
                unfocusedContainerColor = Color.White,
                focusedContainerColor = Color.White
            )
        )
    }
}