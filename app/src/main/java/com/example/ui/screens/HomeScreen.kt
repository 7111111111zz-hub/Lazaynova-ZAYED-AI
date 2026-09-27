package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.data.local.MessageEntity
import com.example.ui.LazaynovaViewModel
import com.example.ui.components.*
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: LazaynovaViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val conversations by viewModel.conversations.collectAsStateWithLifecycle()
    val currentConvId by viewModel.currentConversationId.collectAsStateWithLifecycle()
    val messages by viewModel.currentMessages.collectAsStateWithLifecycle()
    val activeTaskSteps by viewModel.activeTaskSteps.collectAsStateWithLifecycle()
    val memories by viewModel.memories.collectAsStateWithLifecycle()
    val projects by viewModel.projects.collectAsStateWithLifecycle()

    val isStreaming by viewModel.isStreaming.collectAsStateWithLifecycle()
    val streamingMessageId by viewModel.streamingMessageId.collectAsStateWithLifecycle()
    val streamingContent by viewModel.streamingContent.collectAsStateWithLifecycle()

    val isLeftDrawerOpen by viewModel.isLeftDrawerOpen.collectAsStateWithLifecycle()
    val isRightDrawerOpen by viewModel.isRightDrawerOpen.collectAsStateWithLifecycle()
    val isTaskSelectorOpen by viewModel.isTaskSelectorOpen.collectAsStateWithLifecycle()

    val viewingCodeCanvas by viewModel.viewingCodeCanvas.collectAsStateWithLifecycle()
    val showMemoryManager by viewModel.showMemoryManager.collectAsStateWithLifecycle()
    val showSettingsDialog by viewModel.showSettingsDialog.collectAsStateWithLifecycle()
    val showLoginDialog by viewModel.showLoginDialog.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()

    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    // Scroll to bottom when new messages arrive or when streaming/typing
    LaunchedEffect(messages.size, streamingContent.length, isStreaming) {
        val totalCount = messages.size + if (isStreaming) 1 else 0
        if (totalCount > 0) {
            listState.animateScrollToItem(totalCount - 1)
        }
    }

    Box(modifier = modifier.fillMaxSize().background(LazaynovaBackground)) {
        Scaffold(
            containerColor = LazaynovaBackground,
            topBar = {
                Surface(
                    color = Color.White.copy(alpha = 0.95f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Menu Icon (Left Drawer)
                        IconButton(
                            onClick = { viewModel.toggleLeftDrawer(true) },
                            modifier = Modifier.size(40.dp).testTag("menu_hamburger_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "القائمة الجانبية",
                                tint = LazaynovaTextPrimary,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        // Center: Sparkle + Lazaynova Title
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable { viewModel.createNewConversation() }
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = Color(0xFF6366F1),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Lazaynova",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = LazaynovaTextPrimary
                            )
                        }

                        // Right: More Options / Profile
                        IconButton(
                            onClick = { viewModel.toggleRightDrawer(true) },
                            modifier = Modifier.size(40.dp).testTag("more_options_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.MoreHoriz,
                                contentDescription = "المزيد من الخيارات",
                                tint = LazaynovaTextPrimary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            },
            bottomBar = {
                GlowInputPill(
                    text = inputText,
                    onTextChanged = { inputText = it },
                    onSend = {
                        val prompt = inputText.trim()
                        inputText = ""
                        viewModel.sendMessage(prompt = prompt)
                    },
                    onPlusClicked = { viewModel.toggleTaskSelector(true) },
                    isStreaming = isStreaming,
                    modifier = Modifier.navigationBarsPadding()
                )
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                if (messages.isEmpty() && !isStreaming) {
                    // Empty State matching screenshot exactly
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        // Illuminated 3D "L" Emblem
                        Box(
                            modifier = Modifier
                                .size(160.dp)
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

                        Spacer(modifier = Modifier.height(18.dp))

                        // Brand Name
                        Text(
                            text = "Lazaynova",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF4F46E5),
                            letterSpacing = 0.5.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Greeting text matching screenshot
                        Text(
                            text = "مرحباً بك في Lazaynova 👋",
                            fontSize = 20.sp,
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

                        Spacer(modifier = Modifier.height(28.dp))

                        // Quick suggestion pills
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
                                    onClick = {
                                        viewModel.sendMessage(prompt = suggestion)
                                    },
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
                } else {
                    // Chat Message Thread
                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        contentPadding = PaddingValues(vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        items(messages) { message ->
                            val isStreamingThis = isStreaming && message.id == streamingMessageId
                            val displayContent = if (isStreamingThis && streamingContent.isNotEmpty()) {
                                streamingContent
                            } else {
                                message.content
                            }

                            MessageBubble(
                                message = message,
                                displayContent = displayContent,
                                taskSteps = activeTaskSteps[message.id] ?: emptyList(),
                                onOpenCanvas = if (!message.codeSnippet.isNullOrEmpty()) {
                                    { viewModel.setViewingCode(message.codeSnippet) }
                                } else null,
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

        // Left Navigation Drawer Overlay
        AnimatedVisibility(
            visible = isLeftDrawerOpen,
            enter = slideInHorizontally(initialOffsetX = { -it }) + fadeIn(),
            exit = slideOutHorizontally(targetOffsetX = { -it }) + fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.35f))
                    .clickable { viewModel.toggleLeftDrawer(false) }
            ) {
                LeftNavigationDrawer(
                    conversations = conversations,
                    currentConversationId = currentConvId,
                    onNewChat = { viewModel.createNewConversation() },
                    onSelectConversation = { viewModel.selectConversation(it) },
                    onOpenLibrary = {
                        viewModel.toggleLeftDrawer(false)
                        Toast.makeText(context, "المكتبة تضم كافة المشاريع والمستندات المحفوظة.", Toast.LENGTH_SHORT).show()
                    },
                    onOpenMemory = {
                        viewModel.toggleLeftDrawer(false)
                        viewModel.toggleMemoryManager(true)
                    },
                    onOpenProjects = {
                        viewModel.toggleLeftDrawer(false)
                        Toast.makeText(context, "الوسائط والمشاريع النشطة تعمل تحت إشراف زايد الجبيجي.", Toast.LENGTH_SHORT).show()
                    },
                    onOpenSettings = {
                        viewModel.toggleLeftDrawer(false)
                        viewModel.toggleSettings(true)
                    },
                    onOpenAccount = {
                        viewModel.toggleLeftDrawer(false)
                        viewModel.toggleLoginDialog(true)
                    },
                    onClose = { viewModel.toggleLeftDrawer(false) }
                )
            }
        }

        // Right Inspector Drawer Overlay
        AnimatedVisibility(
            visible = isRightDrawerOpen,
            enter = slideInHorizontally(initialOffsetX = { it }) + fadeIn(),
            exit = slideOutHorizontally(targetOffsetX = { it }) + fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.35f))
                    .clickable { viewModel.toggleRightDrawer(false) },
                contentAlignment = Alignment.CenterEnd
            ) {
                RightInspectorDrawer(
                    onToolClick = { toolName ->
                        viewModel.toggleRightDrawer(false)
                        viewModel.sendMessage(
                            prompt = "بدء مهمة: $toolName",
                            toolType = when (toolName) {
                                "task" -> "task"
                                "vision" -> "vision"
                                "Canvas" -> "code"
                                else -> "general"
                            }
                        )
                    },
                    onClose = { viewModel.toggleRightDrawer(false) }
                )
            }
        }

        // Task Selector Bottom Sheet
        if (isTaskSelectorOpen) {
            TaskSelectorSheet(
                onDismiss = { viewModel.toggleTaskSelector(false) },
                onToolSelected = { toolId, defaultPrompt ->
                    viewModel.sendMessage(
                        prompt = defaultPrompt,
                        toolType = when (toolId) {
                            "canvas" -> "code"
                            "video", "image", "music" -> "task"
                            "research" -> "research"
                            "files", "camera", "gallery" -> "vision"
                            else -> "task"
                        }
                    )
                }
            )
        }

        // Code Canvas Dialog
        if (viewingCodeCanvas != null) {
            CodeCanvasDialog(
                code = viewingCodeCanvas!!,
                onDismiss = { viewModel.setViewingCode(null) }
            )
        }

        // Memory Manager Dialog
        if (showMemoryManager) {
            MemoryManagerDialog(
                memories = memories,
                onAddMemory = { title, cat, content, sens ->
                    viewModel.addMemory(title, cat, content, sens)
                },
                onDeleteMemory = { mem ->
                    viewModel.deleteMemory(mem)
                },
                onDismiss = { viewModel.toggleMemoryManager(false) }
            )
        }

        // Settings Dialog
        if (showSettingsDialog) {
            SettingsDialog(
                onDismiss = { viewModel.toggleSettings(false) }
            )
        }

        // Login & Cloud Account Dialog
        if (showLoginDialog) {
            Dialog(
                onDismissRequest = { viewModel.toggleLoginDialog(false) },
                properties = DialogProperties(usePlatformDefaultWidth = false)
            ) {
                LoginScreen(
                    onLoginSuccess = { user ->
                        viewModel.login(user)
                    },
                    onContinueAsGuest = {
                        viewModel.toggleLoginDialog(false)
                    },
                    onDismiss = {
                        viewModel.toggleLoginDialog(false)
                    }
                )
            }
        }
    }
}

@Composable
fun MessageBubble(
    message: MessageEntity,
    displayContent: String,
    taskSteps: List<com.example.data.local.TaskStepEntity>,
    onOpenCanvas: (() -> Unit)? = null,
    onToggleReaction: ((String) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val isUser = message.role == "user"
    val context = LocalContext.current
    var showContextMenu by remember { mutableStateOf(false) }

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
                .widthIn(max = 320.dp)
                .pointerInput(message.id) {
                    detectTapGestures(
                        onLongPress = {
                            showContextMenu = true
                        }
                    )
                }
                .testTag(if (isUser) "home_user_message" else "home_assistant_message")
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                if (!isUser) {
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
                    Text(
                        text = displayContent,
                        fontSize = 14.sp,
                        color = Color.White,
                        lineHeight = 20.sp
                    )
                } else {
                    MarkdownRenderer(
                        content = displayContent,
                        textColor = LazaynovaTextPrimary,
                        onOpenCanvas = { code ->
                            if (onOpenCanvas != null) {
                                onOpenCanvas()
                            }
                        }
                    )
                }
            }
        }

        // Emoji reactions row under message
        if (onToggleReaction != null) {
            MessageReactionsRow(
                reactions = message.getReactionList(),
                onToggleReaction = onToggleReaction,
                onAddMore = { showContextMenu = true },
                modifier = Modifier.padding(top = 2.dp)
            )
        }

        // Context Menu Dialog on Long Press
        if (showContextMenu) {
            MessageContextMenuDialog(
                message = message,
                displayContent = displayContent,
                onDismiss = { showContextMenu = false },
                onSelectEmoji = { emoji ->
                    onToggleReaction?.invoke(emoji)
                },
                onOpenCanvas = if (!message.codeSnippet.isNullOrEmpty() && onOpenCanvas != null) {
                    { onOpenCanvas() }
                } else null
            )
        }

        // Show Task Execution Card if multi-step task
        if (taskSteps.isNotEmpty() || message.taskStatus == "verified" || message.taskStatus == "executing") {
            Spacer(modifier = Modifier.height(8.dp))
            TaskExecutionCard(
                steps = taskSteps,
                taskStatus = message.taskStatus,
                executionLogs = message.executionLogs,
                onOpenCanvas = onOpenCanvas,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}
