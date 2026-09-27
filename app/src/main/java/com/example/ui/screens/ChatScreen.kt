package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.data.local.MessageEntity
import com.example.ui.LazaynovaViewModel
import com.example.ui.components.*
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    viewModel: LazaynovaViewModel,
    onNavigateBack: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // State from ViewModel
    val messages by viewModel.currentMessages.collectAsStateWithLifecycle()
    val isStreaming by viewModel.isStreaming.collectAsStateWithLifecycle()
    val streamingMessageId by viewModel.streamingMessageId.collectAsStateWithLifecycle()
    val streamingContent by viewModel.streamingContent.collectAsStateWithLifecycle()
    val activeTaskSteps by viewModel.activeTaskSteps.collectAsStateWithLifecycle()
    val viewingCodeCanvas by viewModel.viewingCodeCanvas.collectAsStateWithLifecycle()
    val isTaskSelectorOpen by viewModel.isTaskSelectorOpen.collectAsStateWithLifecycle()

    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    // Auto-scroll to latest message during streaming or when agent is typing
    LaunchedEffect(messages.size, streamingContent.length, isStreaming) {
        val totalCount = messages.size + if (isStreaming) 1 else 0
        if (totalCount > 0) {
            listState.animateScrollToItem(totalCount - 1)
        }
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .testTag("chat_screen"),
        containerColor = LazaynovaBackground,
        topBar = {
            Surface(
                color = Color.White.copy(alpha = 0.95f),
                shadowElevation = 1.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (onNavigateBack != null) {
                            IconButton(
                                onClick = onNavigateBack,
                                modifier = Modifier.testTag("back_button")
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "الرجوع",
                                    tint = LazaynovaTextPrimary
                                )
                            }
                        } else {
                            IconButton(
                                onClick = { viewModel.toggleLeftDrawer(true) },
                                modifier = Modifier.testTag("drawer_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Menu,
                                    contentDescription = "القائمة",
                                    tint = LazaynovaTextPrimary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(4.dp))

                        // Avatar & Title
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.lazaynova_emblem),
                                contentDescription = "Lazaynova Emblem",
                                modifier = Modifier.fillMaxSize()
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Lazaynova AI",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = LazaynovaTextPrimary
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                // Active Model Chip
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = Color(0xFFEEF2FF)
                                ) {
                                    Text(
                                        text = "gemini-3.5",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = LazaynovaPrimary,
                                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(if (isStreaming) LazaynovaSuccess else Color(0xFF10B981))
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (isStreaming) "جاري المعالجة والبث..." else "متصل وجاهز",
                                    fontSize = 11.sp,
                                    color = if (isStreaming) LazaynovaPrimary else LazaynovaTextSecondary
                                )
                            }
                        }
                    }

                    // Top Action Buttons
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = { viewModel.createNewConversation() },
                            modifier = Modifier.testTag("new_chat_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.AddComment,
                                contentDescription = "محادثة جديدة",
                                tint = LazaynovaTextSecondary,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        IconButton(
                            onClick = { viewModel.toggleRightDrawer(true) },
                            modifier = Modifier.testTag("inspector_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Tune,
                                contentDescription = "المكونات والأدوات",
                                tint = LazaynovaTextSecondary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        },
        bottomBar = {
            // Neon-Styled Input Field at the Bottom
            NeonChatInputBar(
                text = inputText,
                onTextChanged = { inputText = it },
                onSend = {
                    val prompt = inputText.trim()
                    if (prompt.isNotEmpty()) {
                        inputText = ""
                        viewModel.sendMessage(prompt = prompt)
                    }
                },
                onPlusClicked = { viewModel.toggleTaskSelector(true) },
                isStreaming = isStreaming,
                modifier = Modifier.navigationBarsPadding()
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (messages.isEmpty() && !isStreaming) {
                // Empty State with Neon Glow Emblem and Quick Starters
                ChatEmptyState(
                    onSuggestionClicked = { suggestion ->
                        viewModel.sendMessage(prompt = suggestion)
                    }
                )
            } else {
                // Scrollable Message List supporting Streaming & Markdown
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                        .testTag("messages_lazy_column"),
                    contentPadding = PaddingValues(vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(messages, key = { it.id }) { message ->
                        val isStreamingThis = isStreaming && message.id == streamingMessageId
                        val displayContent = if (isStreamingThis && streamingContent.isNotEmpty()) {
                            streamingContent
                        } else {
                            message.content
                        }

                        ChatStreamMessageItem(
                            message = message,
                            displayContent = displayContent,
                            isStreaming = isStreamingThis,
                            taskSteps = activeTaskSteps[message.id] ?: emptyList(),
                            onOpenCanvas = { code ->
                                viewModel.setViewingCode(code)
                            },
                            onToggleReaction = { emoji ->
                                viewModel.toggleReaction(message.id, emoji)
                            }
                        )
                    }

                    // Dynamic Agent Typing Indicator when processing or streaming
                    if (isStreaming) {
                        item(key = "agent_typing_indicator_item") {
                            AgentTypingIndicator(
                                statusText = if (streamingContent.isNotEmpty()) "Lazaynova يكتب الرد الآن..." else "Lazaynova يحلل ويهيئ الرد...",
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    // Modal Task Selector Bottom Sheet
    if (isTaskSelectorOpen) {
        TaskSelectorSheet(
            onDismiss = { viewModel.toggleTaskSelector(false) },
            onToolSelected = { toolId, defaultPrompt ->
                viewModel.sendMessage(
                    prompt = defaultPrompt,
                    toolType = when (toolId) {
                        "canvas" -> "code"
                        "research" -> "research"
                        "files", "camera", "gallery" -> "vision"
                        else -> "task"
                    }
                )
            }
        )
    }

    // Code Canvas Dialog when requested
    if (viewingCodeCanvas != null) {
        CodeCanvasDialog(
            code = viewingCodeCanvas!!,
            onDismiss = { viewModel.setViewingCode(null) }
        )
    }
}

/**
 * Neon-Styled Input Bar with glowing border and '+' action button.
 */
@Composable
fun NeonChatInputBar(
    text: String,
    onTextChanged: (String) -> Unit,
    onSend: () -> Unit,
    onPlusClicked: () -> Unit,
    isStreaming: Boolean,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val hasText = text.isNotBlank()

    // Pulsing animation for the neon glow ring
    val infiniteTransition = rememberInfiniteTransition(label = "neon_glow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.25f,
        targetValue = 0.65f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_alpha"
    )

    Surface(
        color = Color.Transparent,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 8.dp)
            .testTag("neon_chat_input_bar")
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                // Neon glow outer shadow
                .shadow(
                    elevation = 14.dp,
                    shape = RoundedCornerShape(28.dp),
                    ambientColor = LazaynovaGlowCyan.copy(alpha = glowAlpha),
                    spotColor = LazaynovaPrimaryLight.copy(alpha = glowAlpha)
                )
                .clip(RoundedCornerShape(28.dp))
                .background(Color.White)
                // Neon gradient border
                .border(
                    width = 1.5.dp,
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFF6366F1).copy(alpha = 0.8f),
                            Color(0xFF06B6D4).copy(alpha = 0.8f),
                            Color(0xFFA855F7).copy(alpha = 0.8f)
                        )
                    ),
                    shape = RoundedCornerShape(28.dp)
                )
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // '+' Action Button
                FilledIconButton(
                    onClick = onPlusClicked,
                    shape = CircleShape,
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = Color(0xFFF1F5F9),
                        contentColor = LazaynovaPrimary
                    ),
                    modifier = Modifier
                        .size(40.dp)
                        .testTag("plus_action_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "إضافة أداة أو مهمة",
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Text Field
                TextField(
                    value = text,
                    onValueChange = onTextChanged,
                    placeholder = {
                        Text(
                            text = "اسأل Lazaynova...",
                            fontSize = 14.sp,
                            color = LazaynovaTextTertiary
                        )
                    },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    maxLines = 4,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("chat_input_text_field")
                )

                // Voice Mic Button
                IconButton(
                    onClick = {
                        Toast.makeText(context, "الاستماع الصوتي المباشر جاهز عبر Live API 🎙️", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier
                        .size(38.dp)
                        .testTag("voice_mic_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = "التسجيل الصوتي",
                        tint = LazaynovaTextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(4.dp))

                // Send Button with Gradient Accent
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(
                            brush = if (hasText) {
                                Brush.linearGradient(
                                    colors = listOf(LazaynovaPrimary, LazaynovaPrimaryLight)
                                )
                            } else {
                                Brush.linearGradient(
                                    colors = listOf(Color(0xFFE2E8F0), Color(0xFFCBD5E1))
                                )
                            }
                        )
                        .clickable(enabled = hasText && !isStreaming) {
                            onSend()
                        }
                        .testTag("send_button"),
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
                            tint = if (hasText) Color.White else Color(0xFF94A3B8),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Message list item with streaming indicator and Markdown rendering.
 */
@Composable
fun ChatStreamMessageItem(
    message: MessageEntity,
    displayContent: String,
    isStreaming: Boolean,
    taskSteps: List<com.example.data.local.TaskStepEntity>,
    onOpenCanvas: (String) -> Unit,
    onToggleReaction: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val isUser = message.role == "user"
    val context = LocalContext.current
    var showContextMenu by remember { mutableStateOf(false) }

    // Pulsing cursor for streaming token effect
    val infiniteTransition = rememberInfiniteTransition(label = "cursor")
    val cursorVisible by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "cursor_blink"
    )

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
    ) {
        // Message Card with Long-Press Context Menu Gesture
        Surface(
            shape = RoundedCornerShape(
                topStart = 20.dp,
                topEnd = 20.dp,
                bottomStart = if (isUser) 20.dp else 4.dp,
                bottomEnd = if (isUser) 4.dp else 20.dp
            ),
            color = if (isUser) LazaynovaPrimary else Color.White,
            border = if (isUser) null else androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
            shadowElevation = if (isUser) 1.dp else 2.dp,
            modifier = Modifier
                .widthIn(max = 340.dp)
                .pointerInput(message.id) {
                    detectTapGestures(
                        onLongPress = {
                            showContextMenu = true
                        }
                    )
                }
                .testTag(if (isUser) "user_message_item" else "assistant_message_item")
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                if (!isUser) {
                    // Assistant Header (Avatar + Name + Copy)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .clip(CircleShape)
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.lazaynova_emblem),
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Lazaynova AI",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = LazaynovaPrimary
                            )
                        }

                        IconButton(
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("Lazaynova", displayContent)
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(context, "تم نسخ النص ✨", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "نسخ",
                                tint = LazaynovaTextTertiary,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                }

                if (isUser) {
                    // Plain text for user
                    Text(
                        text = displayContent,
                        fontSize = 14.sp,
                        color = Color.White,
                        lineHeight = 20.sp
                    )
                } else {
                    // Rich Markdown renderer for Assistant response
                    MarkdownRenderer(
                        content = displayContent,
                        textColor = LazaynovaTextPrimary,
                        onOpenCanvas = onOpenCanvas
                    )

                    // Streaming cursor indicator
                    if (isStreaming) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(top = 4.dp)
                        ) {
                            Text(
                                text = "▋",
                                fontSize = 14.sp,
                                color = if (cursorVisible > 0.5f) LazaynovaPrimary else Color.Transparent
                            )
                        }
                    }
                }
            }
        }

        // Emoji reactions row under the message bubble
        MessageReactionsRow(
            reactions = message.getReactionList(),
            onToggleReaction = onToggleReaction,
            onAddMore = { showContextMenu = true },
            modifier = Modifier.padding(top = 2.dp)
        )

        // Context Menu on Long-Press
        if (showContextMenu) {
            MessageContextMenuDialog(
                message = message,
                displayContent = displayContent,
                onDismiss = { showContextMenu = false },
                onSelectEmoji = { emoji ->
                    onToggleReaction(emoji)
                },
                onOpenCanvas = if (!message.codeSnippet.isNullOrEmpty()) onOpenCanvas else null
            )
        }

        // Multi-Stage Task Progress Card
        if (taskSteps.isNotEmpty() || message.taskStatus == "verified" || message.taskStatus == "executing") {
            Spacer(modifier = Modifier.height(8.dp))
            TaskExecutionCard(
                steps = taskSteps,
                taskStatus = message.taskStatus,
                executionLogs = message.executionLogs,
                onOpenCanvas = if (!message.codeSnippet.isNullOrEmpty()) {
                    { onOpenCanvas(message.codeSnippet) }
                } else null,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

/**
 * Empty state shown when conversation has no messages yet.
 */
@Composable
fun ChatEmptyState(
    onSuggestionClicked: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Glowing 3D Emblem
        Box(
            modifier = Modifier
                .size(150.dp)
                .shadow(
                    elevation = 20.dp,
                    shape = CircleShape,
                    ambientColor = LazaynovaPrimaryLight.copy(alpha = 0.2f),
                    spotColor = LazaynovaGlowCyan.copy(alpha = 0.35f)
                )
                .clip(RoundedCornerShape(32.dp))
        ) {
            Image(
                painter = painterResource(id = R.drawable.lazaynova_emblem),
                contentDescription = "Lazaynova 3D Emblem",
                modifier = Modifier.fillMaxSize()
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Lazaynova",
            fontSize = 30.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFF4F46E5),
            letterSpacing = 0.5.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "مرحباً بك في Lazaynova 👋",
            fontSize = 19.sp,
            fontWeight = FontWeight.Bold,
            color = LazaynovaTextPrimary
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "أنا مساعدك الذكي، جاهز لمساعدتك في أي شيء تتخيله أو تحتاجه.",
            fontSize = 13.sp,
            color = LazaynovaTextSecondary,
            textAlign = TextAlign.Center,
            lineHeight = 18.sp,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        val suggestions = listOf(
            "السلام عليكم",
            "أنشئ تطبيق إدارة مهام (To-Do App)",
            "اكتب دالة Python لمعالجة المهام",
            "ما هو نظام Android ومعمارية MVVM؟"
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth(0.9f)
        ) {
            suggestions.forEach { suggestion ->
                Surface(
                    onClick = { onSuggestionClicked(suggestion) },
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White,
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = LazaynovaPrimaryLight,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = suggestion,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = LazaynovaTextPrimary
                        )
                    }
                }
            }
        }
    }
}
