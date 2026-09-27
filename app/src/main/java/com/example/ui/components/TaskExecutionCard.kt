package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.TaskStepEntity
import com.example.ui.theme.*

@Composable
fun TaskExecutionCard(
    steps: List<TaskStepEntity>,
    taskStatus: String,
    executionLogs: String?,
    onOpenCanvas: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showLogs by remember { mutableStateOf(false) }

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
        shadowElevation = 2.dp,
        modifier = modifier
            .fillMaxWidth()
            .testTag("task_execution_card")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header: Status title and badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(
                                if (taskStatus == "verified") LazaynovaSuccess.copy(alpha = 0.15f)
                                else LazaynovaPrimary.copy(alpha = 0.15f)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (taskStatus == "verified") Icons.Default.CheckCircle else Icons.Default.Autorenew,
                            contentDescription = null,
                            tint = if (taskStatus == "verified") LazaynovaSuccess else LazaynovaPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "مراحل تنفيذ وكيل Lazaynova AI",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = LazaynovaTextPrimary
                        )
                        Text(
                            text = if (taskStatus == "verified") "✓ اكتملت جميع المراحل بنجاح" else "جاري معالجة المراحل وتدقيق الكود...",
                            fontSize = 11.sp,
                            color = if (taskStatus == "verified") LazaynovaSuccess else LazaynovaTextSecondary
                        )
                    }
                }

                // Copy Action
                IconButton(
                    onClick = {
                        val fullLog = buildString {
                            append("=== Lazaynova AI Task Execution Report ===\n")
                            steps.forEach { step ->
                                append("[${step.status.uppercase()}] ${step.title}\n")
                                if (!step.logOutput.isNullOrEmpty()) {
                                    append("${step.logOutput}\n")
                                }
                            }
                            if (!executionLogs.isNullOrEmpty()) {
                                append("\nExecution Logs:\n$executionLogs\n")
                            }
                        }
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("Lazaynova Logs", fullLog)
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(context, "تم نسخ تقرير المهمة بنجاح ✨", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "نسخ التقرير",
                        tint = LazaynovaTextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Step Progress Timeline
            steps.forEachIndexed { index, step ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Indicator Circle
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(
                                when (step.status) {
                                    "completed" -> LazaynovaSuccess
                                    "running" -> LazaynovaPrimary
                                    else -> Color(0xFFE2E8F0)
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        when (step.status) {
                            "completed" -> Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                            "running" -> CircularProgressIndicator(
                                modifier = Modifier.size(14.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                            else -> Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF94A3B8))
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Text(
                        text = step.title,
                        fontSize = 12.sp,
                        fontWeight = if (step.status == "running") FontWeight.Bold else FontWeight.Medium,
                        color = when (step.status) {
                            "completed" -> LazaynovaTextPrimary
                            "running" -> LazaynovaPrimary
                            else -> LazaynovaTextTertiary
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Expandable Logs & Canvas Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TextButton(
                    onClick = { showLogs = !showLogs },
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = if (showLogs) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = null,
                        tint = LazaynovaPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (showLogs) "إخفاء سجلات الـ Sandbox" else "عرض سجلات الـ Sandbox",
                        fontSize = 11.sp,
                        color = LazaynovaPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                if (onOpenCanvas != null) {
                    FilledTonalButton(
                        onClick = onOpenCanvas,
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = Color(0xFFEEF2FF),
                            contentColor = LazaynovaPrimary
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Code,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "فتح في Canvas", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            AnimatedVisibility(visible = showLogs) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF0F172A))
                        .padding(12.dp)
                ) {
                    Text(
                        text = "LZAINOVA SANDBOX LOGS",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF38BDF8),
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    steps.forEach { step ->
                        if (!step.logOutput.isNullOrEmpty()) {
                            Text(
                                text = step.logOutput,
                                fontSize = 10.sp,
                                color = Color(0xFFE2E8F0),
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier.padding(vertical = 2.dp)
                            )
                        }
                    }
                    if (!executionLogs.isNullOrEmpty()) {
                        Divider(
                            color = Color(0xFF334155),
                            thickness = 1.dp,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                        Text(
                            text = executionLogs,
                            fontSize = 10.sp,
                            color = Color(0xFF4ADE80),
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }
    }
}
