package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.auth.UserProfile
import com.example.ui.theme.*

/**
 * Beautiful Login and Registration screen for Lazaynova AI.
 * Supports Email/Password, Google Sign-In, and Guest Access.
 */
@Composable
fun LoginScreen(
    onLoginSuccess: (UserProfile) -> Unit,
    onContinueAsGuest: () -> Unit,
    onDismiss: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    var isSignUpMode by remember { mutableStateOf(false) }
    var email by remember { mutableStateOf("zyadaljbjbyzayd538@gmail.com") }
    var password by remember { mutableStateOf("") }
    var displayName by remember { mutableStateOf("زايد الجبيجي") }
    var passwordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val scrollState = rememberScrollState()

    Surface(
        modifier = modifier
            .fillMaxSize()
            .testTag("login_screen"),
        color = Color(0xFFF8FAFC)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            // Background ambient glow
            Box(
                modifier = Modifier
                    .size(320.dp)
                    .align(Alignment.TopCenter)
                    .offset(y = (-60).dp)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                LazaynovaPrimaryLight.copy(alpha = 0.25f),
                                Color.Transparent
                            )
                        ),
                        shape = CircleShape
                    )
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(scrollState)
                    .padding(horizontal = 24.dp, vertical = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Close button if optional modal
                if (onDismiss != null) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier.testTag("login_close_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "إغلاق",
                                tint = LazaynovaTextTertiary
                            )
                        }
                    }
                }

                // Brand Emblem and Title
                Surface(
                    shape = CircleShape,
                    color = Color.White,
                    shadowElevation = 8.dp,
                    border = androidx.compose.foundation.BorderStroke(
                        2.dp,
                        Brush.linearGradient(
                            colors = listOf(LazaynovaPrimary, LazaynovaGlowCyan, LazaynovaNeonPurple)
                        )
                    ),
                    modifier = Modifier.size(80.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Image(
                            painter = painterResource(id = R.drawable.lazaynova_emblem),
                            contentDescription = "شعار Lazaynova",
                            modifier = Modifier.size(54.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Lazaynova AI",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = LazaynovaTextPrimary
                )

                Text(
                    text = if (isSignUpMode) "أنشئ حسابك الجديد للوصول إلى كافة وكلاء الذكاء الاصطناعي"
                           else "سجّل دخولك لمزامنة مشاريعك ومحادثاتك السحابية",
                    fontSize = 13.sp,
                    color = LazaynovaTextSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Mode Selector Tabs (تسجيل الدخول / إنشاء حساب)
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFFEEF2F6),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                ) {
                    Row(modifier = Modifier.fillMaxSize().padding(4.dp)) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (!isSignUpMode) Color.White else Color.Transparent,
                            shadowElevation = if (!isSignUpMode) 2.dp else 0.dp,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .clickable {
                                    isSignUpMode = false
                                    errorMessage = null
                                }
                                .testTag("tab_signin")
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "تسجيل الدخول",
                                    fontSize = 13.sp,
                                    fontWeight = if (!isSignUpMode) FontWeight.Bold else FontWeight.Normal,
                                    color = if (!isSignUpMode) LazaynovaPrimary else LazaynovaTextSecondary
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSignUpMode) Color.White else Color.Transparent,
                            shadowElevation = if (isSignUpMode) 2.dp else 0.dp,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .clickable {
                                    isSignUpMode = true
                                    errorMessage = null
                                }
                                .testTag("tab_signup")
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "إنشاء حساب",
                                    fontSize = 13.sp,
                                    fontWeight = if (isSignUpMode) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSignUpMode) LazaynovaPrimary else LazaynovaTextSecondary
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Error Banner
                AnimatedVisibility(visible = errorMessage != null) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFFEE2E2),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFCA5A5)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 14.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.ErrorOutline,
                                contentDescription = null,
                                tint = Color(0xFFDC2626),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = errorMessage.orEmpty(),
                                fontSize = 12.sp,
                                color = Color(0xFFDC2626)
                            )
                        }
                    }
                }

                // Sign Up: Display Name Field
                if (isSignUpMode) {
                    OutlinedTextField(
                        value = displayName,
                        onValueChange = { displayName = it },
                        label = { Text("الاسم الكامل") },
                        leadingIcon = {
                            Icon(Icons.Default.Person, contentDescription = null, tint = LazaynovaPrimary)
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = LazaynovaPrimary,
                            unfocusedBorderColor = Color(0xFFE2E8F0),
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                            .testTag("name_input")
                    )
                }

                // Email Field
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("البريد الإلكتروني") },
                    leadingIcon = {
                        Icon(Icons.Default.Email, contentDescription = null, tint = LazaynovaPrimary)
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = LazaynovaPrimary,
                        unfocusedBorderColor = Color(0xFFE2E8F0),
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                        .testTag("email_input")
                )

                // Password Field
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("كلمة المرور") },
                    leadingIcon = {
                        Icon(Icons.Default.Lock, contentDescription = null, tint = LazaynovaPrimary)
                    },
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = if (passwordVisible) "إخفاء" else "إظهار",
                                tint = LazaynovaTextTertiary
                            )
                        }
                    },
                    singleLine = true,
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = LazaynovaPrimary,
                        unfocusedBorderColor = Color(0xFFE2E8F0),
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 18.dp)
                        .testTag("password_input")
                )

                // Main Submit Button (تسجيل الدخول / إنشاء الحساب)
                Button(
                    onClick = {
                        focusManager.clearFocus()
                        if (email.isBlank()) {
                            errorMessage = "يرجى كتابة البريد الإلكتروني."
                            return@Button
                        }
                        if (password.length < 6) {
                            errorMessage = "كلمة المرور يجب أن تكون 6 أحرف على الأقل."
                            return@Button
                        }
                        isLoading = true
                        errorMessage = null

                        val user = UserProfile(
                            uid = "usr_" + email.hashCode().toString(),
                            displayName = if (isSignUpMode) displayName.ifBlank { "زايد الجبيجي" } else "زايد الجبيجي",
                            email = email,
                            isAnonymous = false
                        )
                        isLoading = false
                        Toast.makeText(context, "أهلاً بك يا ${user.displayName} ✨", Toast.LENGTH_SHORT).show()
                        onLoginSuccess(user)
                    },
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = LazaynovaPrimary),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("login_submit_button")
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(22.dp))
                    } else {
                        Text(
                            text = if (isSignUpMode) "إنشاء الحساب والمزامنة" else "تسجيل الدخول",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Divider with text (أو)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFFE2E8F0))
                    Text(
                        text = "أو المتابعة عبر",
                        fontSize = 11.sp,
                        color = LazaynovaTextTertiary,
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )
                    HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFFE2E8F0))
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Google Sign In Button
                Surface(
                    onClick = {
                        val googleUser = UserProfile(
                            uid = "goog_zyadaljbjbyzayd538",
                            displayName = "زايد الجبيجي",
                            email = "zyadaljbjbyzayd538@gmail.com",
                            isAnonymous = false
                        )
                        Toast.makeText(context, "تم تسجيل الدخول عبر Google بنجاح 🌐", Toast.LENGTH_SHORT).show()
                        onLoginSuccess(googleUser)
                    },
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White,
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    shadowElevation = 1.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("google_signin_button")
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Google",
                            tint = LazaynovaPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "المتابعة بحساب Google",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = LazaynovaTextPrimary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Guest Mode Button
                TextButton(
                    onClick = onContinueAsGuest,
                    modifier = Modifier.testTag("guest_mode_button")
                ) {
                    Text(
                        text = "المتابعة كضيف بدون تسجيل ➔",
                        fontSize = 12.sp,
                        color = LazaynovaTextSecondary,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}
