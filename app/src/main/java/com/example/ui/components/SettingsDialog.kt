package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.remote.GeminiClient
import com.example.ui.theme.*

@Composable
fun SettingsDialog(
    onDismiss: () -> Unit
) {
    val isKeyConfigured = GeminiClient.hasValidApiKey()

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .padding(16.dp)
                .testTag("settings_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = null,
                            tint = LazaynovaPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "إعدادات منصة Lazaynova AI",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = LazaynovaTextPrimary
                        )
                    }

                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "إغلاق")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // API Status Card
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = if (isKeyConfigured) Color(0xFFECFDF5) else Color(0xFFFFFBEB),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isKeyConfigured) Color(0xFFA7F3D0) else Color(0xFFFDE68A)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .clip(CircleShape)
                                .background(if (isKeyConfigured) LazaynovaSuccess else LazaynovaWarning)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = if (isKeyConfigured) "اتصال Gemini API: نشط ومفعّل" else "وضع التشغيل: المحرك المستقل الذاتي (Local Autonomous)",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isKeyConfigured) Color(0xFF065F46) else Color(0xFF92400E)
                            )
                            Text(
                                text = if (isKeyConfigured) "جاهز للنماذج gemini-3.5-flash و gemini-3.1-pro-preview" else "يعمل بنظام الاستجابة المباشرة ومعالجة الوكلاء بدون انقطاع",
                                fontSize = 11.sp,
                                color = if (isKeyConfigured) Color(0xFF047857) else Color(0xFFB45309)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Platform Info
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFFF8FAFD),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "معلومات المنصة والهوية",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = LazaynovaTextPrimary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(text = "• المنصة: Lazaynova AI Neural Engine", fontSize = 11.sp, color = LazaynovaTextSecondary)
                        Text(text = "• المشرف العام: زايد الجبيجي", fontSize = 11.sp, color = LazaynovaTextSecondary)
                        Text(text = "• الشعار: Private. Autonomous. Yours.", fontSize = 11.sp, color = LazaynovaTextSecondary)
                        Text(text = "• المعمارية: Multi-Agent Orchestration & Sandbox Verifier", fontSize = 11.sp, color = LazaynovaTextSecondary)
                        Text(text = "• التوجيه الذكي: منع التكرار ومعالجة التحيات المباشرة", fontSize = 11.sp, color = LazaynovaTextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = LazaynovaPrimary)
                ) {
                    Text("تم وحفظ")
                }
            }
        }
    }
}
