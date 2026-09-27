package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.LazaynovaPrimary
import com.example.ui.theme.LazaynovaPrimaryLight
import com.example.ui.theme.LazaynovaTextSecondary

/**
 * Animated dynamic typing indicator shown in the chat list when the AI is processing or streaming.
 */
@Composable
fun AgentTypingIndicator(
    modifier: Modifier = Modifier,
    statusText: String = "Lazaynova يكتب الرد الآن..."
) {
    // Glowing breathing animation for avatar border
    val infiniteTransition = rememberInfiniteTransition(label = "avatar_pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_alpha"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("agent_typing_indicator"),
        horizontalAlignment = Alignment.Start
    ) {
        Surface(
            shape = RoundedCornerShape(
                topStart = 20.dp,
                topEnd = 20.dp,
                bottomStart = 4.dp,
                bottomEnd = 20.dp
            ),
            color = Color.White,
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
            shadowElevation = 3.dp,
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Mini 3D Emblem with pulsing border
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .border(
                            width = 1.5.dp,
                            color = LazaynovaPrimary.copy(alpha = pulseAlpha),
                            shape = CircleShape
                        )
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.lazaynova_emblem),
                        contentDescription = "Lazaynova typing",
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                // Three Dynamic Bouncing Dots
                BouncingDotsAnimation(
                    dotSize = 7.dp,
                    dotSpacing = 4.dp
                )

                Spacer(modifier = Modifier.width(10.dp))

                // Status Text
                Text(
                    text = statusText,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = LazaynovaTextSecondary,
                    modifier = Modifier.testTag("typing_status_text")
                )
            }
        }
    }
}

/**
 * Three bouncing animated dots with staggered delays.
 */
@Composable
fun BouncingDotsAnimation(
    modifier: Modifier = Modifier,
    dotSize: Dp = 7.dp,
    dotSpacing: Dp = 4.dp,
    dotColors: List<Color> = listOf(
        Color(0xFF6366F1), // Primary Indigo
        Color(0xFF06B6D4), // Cyan
        Color(0xFFA855F7)  // Purple
    )
) {
    val infiniteTransition = rememberInfiniteTransition(label = "bouncing_dots")

    // Staggered bounce animations for 3 dots
    val offset1 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -7f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 1100
                0.0f at 0
                -7f at 220 using FastOutSlowInEasing
                0.0f at 440 using FastOutSlowInEasing
                0.0f at 1100
            },
            repeatMode = RepeatMode.Restart
        ),
        label = "dot1_offset"
    )

    val offset2 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -7f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 1100
                0.0f at 180
                -7f at 400 using FastOutSlowInEasing
                0.0f at 620 using FastOutSlowInEasing
                0.0f at 1100
            },
            repeatMode = RepeatMode.Restart
        ),
        label = "dot2_offset"
    )

    val offset3 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -7f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 1100
                0.0f at 360
                -7f at 580 using FastOutSlowInEasing
                0.0f at 800 using FastOutSlowInEasing
                0.0f at 1100
            },
            repeatMode = RepeatMode.Restart
        ),
        label = "dot3_offset"
    )

    val offsets = listOf(offset1, offset2, offset3)

    Row(
        modifier = modifier.testTag("bouncing_dots_container"),
        horizontalArrangement = Arrangement.spacedBy(dotSpacing),
        verticalAlignment = Alignment.CenterVertically
    ) {
        offsets.forEachIndexed { index, offset ->
            val color = dotColors.getOrElse(index) { LazaynovaPrimary }
            Box(
                modifier = Modifier
                    .size(dotSize)
                    .graphicsLayer {
                        translationY = offset * density
                    }
                    .clip(CircleShape)
                    .background(color)
                    .testTag("typing_dot_$index")
            )
        }
    }
}
