package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.local.ConversationEntity
import com.example.ui.theme.*

@Composable
fun LeftNavigationDrawer(
    conversations: List<ConversationEntity>,
    currentConversationId: String?,
    onNewChat: () -> Unit,
    onSelectConversation: (String) -> Unit,
    onOpenLibrary: () -> Unit,
    onOpenMemory: () -> Unit,
    onOpenProjects: () -> Unit,
    onOpenSettings: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }

    Surface(
        color = Color(0xFFFBFBFE),
        modifier = modifier
            .fillMaxHeight()
            .width(300.dp)
            .testTag("left_navigation_drawer")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            // Header with Brand and Close Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.lazaynova_emblem),
                            contentDescription = "Lazaynova Logo",
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Lazaynova",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = LazaynovaTextPrimary
                    )
                }

                IconButton(
                    onClick = onClose,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "إغلاق القائمة",
                        tint = LazaynovaTextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Search Bar ("بحث")
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = Color.White,
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "بحث",
                        tint = LazaynovaTextTertiary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "بحث",
                        color = LazaynovaTextTertiary,
                        fontSize = 14.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Scrollable Menu Section
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
                // Main Navigation Items
                DrawerItemRow(
                    title = "دردشة جديدة",
                    icon = Icons.Default.AddComment,
                    onClick = onNewChat,
                    testTag = "drawer_new_chat"
                )

                DrawerItemRow(
                    title = "المحادثات",
                    icon = Icons.Default.AccessTime,
                    hasActiveDot = true,
                    onClick = {},
                    testTag = "drawer_conversations"
                )

                // Recent Conversations Sub-list
                if (conversations.isNotEmpty()) {
                    Column(modifier = Modifier.padding(start = 28.dp, bottom = 6.dp)) {
                        conversations.take(4).forEach { conv ->
                            val isSelected = conv.id == currentConversationId
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) Color(0xFFEEF2FF) else Color.Transparent)
                                    .clickable { onSelectConversation(conv.id) }
                                    .padding(horizontal = 8.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(if (isSelected) LazaynovaPrimaryLight else Color(0xFFCBD5E1))
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = conv.title,
                                    fontSize = 12.sp,
                                    color = if (isSelected) LazaynovaPrimary else LazaynovaTextSecondary,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }

                DrawerItemRow(
                    title = "المكتبة",
                    icon = Icons.Default.MenuBook,
                    onClick = onOpenLibrary,
                    testTag = "drawer_library"
                )

                DrawerItemRow(
                    title = "الوسائط",
                    icon = Icons.Default.PermMedia,
                    hasActiveDot = true,
                    onClick = onOpenProjects,
                    testTag = "drawer_media"
                )

                DrawerItemRow(
                    title = "المستندات",
                    icon = Icons.Default.Article,
                    onClick = onOpenLibrary,
                    testTag = "drawer_documents"
                )

                DrawerItemRow(
                    title = "جدولة",
                    icon = Icons.Default.CalendarToday,
                    onClick = {},
                    testTag = "drawer_schedule"
                )

                Divider(
                    color = Color(0xFFF1F5F9),
                    thickness = 1.dp,
                    modifier = Modifier.padding(vertical = 10.dp)
                )

                // Basic Settings
                Text(
                    text = "الإعدادات الأساسية ⚙",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = LazaynovaTextTertiary,
                    modifier = Modifier.padding(vertical = 4.dp)
                )

                DrawerItemRow(title = "المظهر", icon = Icons.Default.LightMode, onClick = onOpenSettings)
                DrawerItemRow(title = "اللغة", icon = Icons.Default.Language, onClick = onOpenSettings)
                DrawerItemRow(title = "صوت الوكيل", icon = Icons.Default.GraphicEq, onClick = onOpenSettings)
                DrawerItemRow(title = "الإشعارات", icon = Icons.Default.Notifications, onClick = onOpenSettings)

                Divider(
                    color = Color(0xFFF1F5F9),
                    thickness = 1.dp,
                    modifier = Modifier.padding(vertical = 10.dp)
                )

                // Advanced Features
                Text(
                    text = "الميزات المتقدمة ⚙",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = LazaynovaTextTertiary,
                    modifier = Modifier.padding(vertical = 4.dp)
                )

                DrawerItemRow(title = "الوكلاء", icon = Icons.Default.SmartToy, onClick = onOpenProjects)
                DrawerItemRow(title = "الذاكرة", icon = Icons.Default.Psychology, onClick = onOpenMemory)
                DrawerItemRow(title = "التكاملات", icon = Icons.Default.Link, onClick = onOpenSettings)
                DrawerItemRow(title = "الأمان والخصوصية", icon = Icons.Default.Shield, onClick = onOpenSettings)
                DrawerItemRow(title = "السجلات والتحليلات", icon = Icons.Default.Analytics, onClick = onOpenProjects)
            }

            // Footer Section matching screenshot
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp)
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.lazaynova_emblem),
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "Lazaynova Core",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = LazaynovaTextPrimary
                                )
                                Text(
                                    text = "الوكيل الرئيسي",
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

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.PersonOutline,
                            contentDescription = null,
                            tint = LazaynovaTextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "الحساب والاشتراكات",
                            fontSize = 12.sp,
                            color = LazaynovaTextSecondary
                        )
                    }
                }
            }

            Text(
                text = "الإصدار 0.1",
                fontSize = 10.sp,
                color = LazaynovaTextTertiary,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 8.dp)
            )
        }
    }
}

@Composable
fun DrawerItemRow(
    title: String,
    icon: ImageVector,
    hasActiveDot: Boolean = false,
    onClick: () -> Unit,
    testTag: String = ""
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 9.dp)
            .then(if (testTag.isNotEmpty()) Modifier.testTag(testTag) else Modifier),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = LazaynovaTextSecondary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = LazaynovaTextPrimary
            )
        }

        if (hasActiveDot) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF3B82F6))
            )
        }
    }
}
