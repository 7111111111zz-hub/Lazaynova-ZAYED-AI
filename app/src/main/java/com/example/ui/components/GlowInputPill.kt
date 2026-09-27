package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun GlowInputPill(
    text: String,
    onTextChanged: (String) -> Unit,
    onSend: () -> Unit,
    onPlusClicked: () -> Unit,
    isStreaming: Boolean,
    modifier: Modifier = Modifier
) {
    var isRecording by remember { mutableStateOf(false) }

    // Pulsing glow animation
    val infiniteTransition = rememberInfiniteTransition(label = "pill_glow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_alpha"
    )

    val neonBorderBrush = Brush.horizontalGradient(
        colors = listOf(
            LazaynovaGlowCyan.copy(alpha = glowAlpha),
            LazaynovaPrimaryLight.copy(alpha = glowAlpha),
            LazaynovaGlowViolet.copy(alpha = glowAlpha)
        )
    )

    val sendButtonBrush = Brush.linearGradient(
        colors = listOf(
            Color(0xFF4F46E5),
            Color(0xFF7C3AED)
        )
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(32.dp),
                ambientColor = LazaynovaPrimaryLight.copy(alpha = 0.15f),
                spotColor = LazaynovaGlowCyan.copy(alpha = 0.25f)
            )
            .clip(RoundedCornerShape(32.dp))
            .background(Color.White)
            .border(
                width = 2.dp,
                brush = neonBorderBrush,
                shape = RoundedCornerShape(32.dp)
            )
            .padding(horizontal = 6.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Plus '+' Button for Task Selector
            IconButton(
                onClick = onPlusClicked,
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFF1F5F9))
                    .testTag("plus_tools_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "أدوات ومهام Lazaynova",
                    tint = LazaynovaTextPrimary,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Main Text Input Area
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 4.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                if (text.isEmpty()) {
                    Text(
                        text = "اسأل Lazaynova...",
                        color = LazaynovaTextTertiary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Normal
                    )
                }

                BasicTextField(
                    value = text,
                    onValueChange = onTextChanged,
                    textStyle = TextStyle(
                        color = LazaynovaTextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    cursorBrush = SolidColor(LazaynovaPrimary),
                    maxLines = 4,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                    keyboardActions = KeyboardActions(onSend = {
                        if (text.isNotBlank() && !isStreaming) onSend()
                    }),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("chat_input_field")
                )
            }

            Spacer(modifier = Modifier.width(6.dp))

            // Voice Dictation Button
            IconButton(
                onClick = {
                    isRecording = !isRecording
                    if (isRecording) {
                        onTextChanged(if (text.isBlank()) "أنشئ تطبيق لإدارة المهام باستخدام Kotlin" else "$text صوتياً")
                    }
                },
                modifier = Modifier
                    .size(38.dp)
                    .testTag("voice_dictation_button")
            ) {
                Icon(
                    imageVector = if (isRecording) Icons.Default.MicOff else Icons.Default.Mic,
                    contentDescription = "التسجيل الصوتي",
                    tint = if (isRecording) LazaynovaError else LazaynovaTextSecondary,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(4.dp))

            // Glowing Send Button
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(if (text.isNotBlank() && !isStreaming) sendButtonBrush else SolidColor(Color(0xFFE2E8F0)))
                    .clickable(
                        enabled = text.isNotBlank() && !isStreaming,
                        onClick = onSend
                    )
                    .testTag("send_message_button"),
                contentAlignment = Alignment.Center
            ) {
                if (isStreaming) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "إرسال",
                        tint = if (text.isNotBlank()) Color.White else Color(0xFF94A3B8),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}
