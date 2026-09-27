package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.*

@Composable
fun RightInspectorDrawer(
    onToolClick: (String) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = Color(0xFFFBFBFE),
        modifier = modifier
            .fillMaxHeight()
            .width(310.dp)
            .testTag("right_inspector_drawer")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            // User Profile Header Card matching screenshot
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = Color.White,
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { }
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF6366F1).copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = Color(0xFF6366F1),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "زايد الجبيجي",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = LazaynovaTextPrimary
                            )
                            Text(
                                text = "حساب مجاني",
                                fontSize = 11.sp,
                                color = LazaynovaTextTertiary
                            )
                        }
                    }

                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = null,
                        tint = LazaynovaTextTertiary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Scrollable Content
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
                // Section: المكونات الإضافية
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ViewInAr,
                        contentDescription = null,
                        tint = Color(0xFF6366F1),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "المكونات الإضافية",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = LazaynovaTextPrimary
                    )
                }

                InspectorComponentCard(
                    title = "الوكيل الذكي",
                    subtitle = "تفكير • تخطيط • تنفيذ",
                    icon = Icons.Default.SmartToy,
                    iconColor = Color(0xFF6366F1),
                    onClick = { onToolClick("task") }
                )

                InspectorComponentCard(
                    title = "رفع الصور",
                    subtitle = "تحليل • استفسار",
                    icon = Icons.Default.Image,
                    iconColor = Color(0xFF3B82F6),
                    onClick = { onToolClick("vision") }
                )

                InspectorComponentCard(
                    title = "رفع الملفات",
                    subtitle = "ملفات • مستندات",
                    icon = Icons.Default.Description,
                    iconColor = Color(0xFF8B5CF6),
                    onClick = { onToolClick("files") }
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Section: الميزات والمهام الرئيسية
                Text(
                    text = "الميزات والمهام الرئيسية",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = LazaynovaTextPrimary,
                    modifier = Modifier.padding(vertical = 6.dp)
                )

                val mainFeatures = listOf(
                    Triple("إنشاء صور", Icons.Default.Palette, Color(0xFF8B5CF6)),
                    Triple("فيديو (مع Veo)", Icons.Default.PlayCircle, Color(0xFF3B82F6)),
                    Triple("موسيقى", Icons.Default.MusicNote, Color(0xFF06B6D4)),
                    Triple("Canvas", Icons.Default.Code, Color(0xFF4F46E5)),
                    Triple("Deep Research", Icons.Default.Search, Color(0xFF6366F1)),
                    Triple("التعلم الموجه", Icons.Default.School, Color(0xFF10B981)),
                    Triple("الذكاء المخصص Lazaynova", Icons.Default.AutoAwesome, Color(0xFFEC4899))
                )

                mainFeatures.forEach { (title, icon, color) ->
                    InspectorFeatureRow(
                        title = title,
                        icon = icon,
                        iconColor = color,
                        onClick = { onToolClick(title) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Bottom Robot Mascot Card matching screenshot
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color.White,
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(
                                        Color(0xFFE0E7FF),
                                        Color(0xFFC7D2FE)
                                    )
                                )
                            )
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.lazaynova_agent),
                            contentDescription = "Lazaynova Agent",
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = "وكيلك الذكي",
                            fontSize = 12.sp,
                            color = LazaynovaTextSecondary
                        )
                        Text(
                            text = "Lazaynova",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF4F46E5)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "يفكر . يخطط . ينفذ.",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = LazaynovaTextTertiary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun InspectorComponentCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconColor: Color,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        color = Color.White,
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF1F5F9)),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(iconColor.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconColor,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = title,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = LazaynovaTextPrimary
                    )
                    Text(
                        text = subtitle,
                        fontSize = 10.sp,
                        color = LazaynovaTextTertiary
                    )
                }
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = null,
                tint = LazaynovaTextTertiary,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
private fun InspectorFeatureRow(
    title: String,
    icon: ImageVector,
    iconColor: Color,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = Color.Transparent,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(iconColor.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconColor,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = title,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = LazaynovaTextPrimary
                )
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = null,
                tint = LazaynovaTextTertiary,
                modifier = Modifier.size(14.dp)
            )
        }
    }
}
