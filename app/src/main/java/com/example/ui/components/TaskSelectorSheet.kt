package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

data class TaskToolItem(
    val id: String,
    val title: String,
    val icon: ImageVector,
    val iconColor: Color,
    val containerColor: Color = Color.White
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskSelectorSheet(
    onDismiss: () -> Unit,
    onToolSelected: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    val quickUploadPills = listOf(
        Triple("files", "ملفات", Icons.Default.Description),
        Triple("camera", "كاميرا", Icons.Default.PhotoCamera),
        Triple("gallery", "صورة", Icons.Default.Image)
    )

    val toolsList = listOf(
        TaskToolItem("image", "إنشاء صور", Icons.Default.Palette, Color(0xFF8B5CF6)),
        TaskToolItem("video", "فيديو (مع Veo)", Icons.Default.PlayCircle, Color(0xFF3B82F6)),
        TaskToolItem("music", "موسيقى", Icons.Default.MusicNote, Color(0xFF06B6D4)),
        TaskToolItem("canvas", "Canvas", Icons.Default.Code, Color(0xFF4F46E5)),
        TaskToolItem("research", "Deep Research", Icons.Default.Search, Color(0xFF6366F1)),
        TaskToolItem("guided_learning", "التعلم الموجه", Icons.Default.School, Color(0xFF10B981)),
        TaskToolItem("custom_ai", "الذكاء المخصص", Icons.Default.AutoAwesome, Color(0xFFEC4899)),
        TaskToolItem("more", "المزيد", Icons.Default.GridView, Color(0xFF64748B))
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        dragHandle = null,
        modifier = modifier.testTag("task_selector_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header: Title and Close button
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFF1F5F9))
                        .testTag("close_task_selector")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "إغلاق",
                        tint = LazaynovaTextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Text(
                    text = "اختيار المهام",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = LazaynovaTextPrimary
                )

                // Placeholder for symmetry
                Spacer(modifier = Modifier.size(36.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Quick Upload Row (Files, Camera, Image)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                quickUploadPills.forEach { (id, label, icon) ->
                    Surface(
                        onClick = {
                            val prompt = when (id) {
                                "files" -> "قم بفحص المستند المرفق وتلخيص أهم النقاط والنتائج."
                                "camera" -> "تحليل مباشر من الكاميرا والتعرف على العناصر."
                                else -> "تحليل الصورة واستخراج العناصر الفنية والشرح."
                            }
                            onToolSelected(id, prompt)
                            onDismiss()
                        },
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFFF8FAFD),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("quick_pill_$id")
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = label,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = LazaynovaTextPrimary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = icon,
                                contentDescription = label,
                                tint = LazaynovaPrimaryLight,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // 8-Grid of Tools
            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(toolsList) { tool ->
                    Surface(
                        onClick = {
                            val defaultPrompt = when (tool.id) {
                                "image" -> "توليد صورة فنية ثلاثية الأبعاد بأسلوب مستقبلي لـ Lazaynova AI"
                                "video" -> "إنشاء مشهد فيديو عالي الجودة باستخدام نموذج Veo المتقدم"
                                "music" -> "تأليف مقطوعة موسيقية سينمائية هادئة"
                                "canvas" -> "إنشاء تطبيق Android متكامل لحساب المصاريف مع واجهة Jetpack Compose"
                                "research" -> "بحث معمق في شبكات الذكاء الاصطناعي العصبية وهندسة الوكلاء الذاتية"
                                "guided_learning" -> "شرح مبسط وتفاعلي لمعمارية MVVM في أندرويد مع أمثلة عملية"
                                "custom_ai" -> "تخصيص سلوك الوكيل وربط الذاكرة الدائمة بالأهداف الحالية"
                                else -> "استعراض المزيد من أدوات وخدمات منصة Lazaynova AI"
                            }
                            onToolSelected(tool.id, defaultPrompt)
                            onDismiss()
                        },
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFFFAFAFE),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF1F5F9)),
                        modifier = Modifier
                            .height(86.dp)
                            .testTag("tool_card_${tool.id}")
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(6.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(tool.iconColor.copy(alpha = 0.12f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = tool.icon,
                                    contentDescription = tool.title,
                                    tint = tool.iconColor,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = tool.title,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = LazaynovaTextPrimary,
                                textAlign = TextAlign.Center,
                                maxLines = 1
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Informational tip matching screenshot
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFFF0F4FF),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE0E7FF)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = LazaynovaPrimaryLight,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "يمكنك الوصول إلى جميع الأدوات والميزات من هنا.\nاختر ما تحتاجه لبدء مهمتك مع Lazaynova",
                        fontSize = 12.sp,
                        color = LazaynovaTextSecondary,
                        lineHeight = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
