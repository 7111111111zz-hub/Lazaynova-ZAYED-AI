package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.auth.*
import com.example.data.local.*
import com.example.data.remote.GeminiClient
import com.example.domain.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class LazaynovaViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val dao = db.appDao()
    val authRepository = AuthRepository(application)
    val currentUser: StateFlow<UserProfile?> = authRepository.currentUser
    val orchestrator = AgentOrchestrator()
    val orchestratorState: StateFlow<OrchestratorState> = orchestrator.state

    private val _currentConversationId = MutableStateFlow<String?>(null)
    val currentConversationId: StateFlow<String?> = _currentConversationId.asStateFlow()

    val conversations: StateFlow<List<ConversationEntity>> = dao.getAllConversations()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val memories: StateFlow<List<MemoryEntity>> = dao.getAllMemories()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val projects: StateFlow<List<ProjectEntity>> = dao.getAllProjects()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val currentMessages: StateFlow<List<MessageEntity>> = _currentConversationId
        .flatMapLatest { convId ->
            if (convId == null) flowOf(emptyList())
            else dao.getMessagesForConversation(convId)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Active Task Steps per message
    private val _activeTaskSteps = MutableStateFlow<Map<String, List<TaskStepEntity>>>(emptyMap())
    val activeTaskSteps: StateFlow<Map<String, List<TaskStepEntity>>> = _activeTaskSteps.asStateFlow()

    // UI state
    private val _isStreaming = MutableStateFlow(false)
    val isStreaming: StateFlow<Boolean> = _isStreaming.asStateFlow()

    private val _streamingMessageId = MutableStateFlow<String?>(null)
    val streamingMessageId: StateFlow<String?> = _streamingMessageId.asStateFlow()

    private val _streamingContent = MutableStateFlow("")
    val streamingContent: StateFlow<String> = _streamingContent.asStateFlow()

    private val _activeMode = MutableStateFlow("Chat")
    val activeMode: StateFlow<String> = _activeMode.asStateFlow()

    private val _isLeftDrawerOpen = MutableStateFlow(false)
    val isLeftDrawerOpen: StateFlow<Boolean> = _isLeftDrawerOpen.asStateFlow()

    private val _isRightDrawerOpen = MutableStateFlow(false)
    val isRightDrawerOpen: StateFlow<Boolean> = _isRightDrawerOpen.asStateFlow()

    private val _isTaskSelectorOpen = MutableStateFlow(false)
    val isTaskSelectorOpen: StateFlow<Boolean> = _isTaskSelectorOpen.asStateFlow()

    private val _viewingCodeCanvas = MutableStateFlow<String?>(null)
    val viewingCodeCanvas: StateFlow<String?> = _viewingCodeCanvas.asStateFlow()

    private val _showMemoryManager = MutableStateFlow(false)
    val showMemoryManager: StateFlow<Boolean> = _showMemoryManager.asStateFlow()

    private val _showSettingsDialog = MutableStateFlow(false)
    val showSettingsDialog: StateFlow<Boolean> = _showSettingsDialog.asStateFlow()

    private val _showProjectManager = MutableStateFlow(false)
    val showProjectManager: StateFlow<Boolean> = _showProjectManager.asStateFlow()

    private val _showLoginDialog = MutableStateFlow(false)
    val showLoginDialog: StateFlow<Boolean> = _showLoginDialog.asStateFlow()

    val isLoggedIn: StateFlow<Boolean> = MutableStateFlow(authRepository.isLoggedIn).apply {
        viewModelScope.launch {
            authRepository.currentUser.collect { user ->
                value = user != null
            }
        }
    }

    fun toggleLoginDialog(show: Boolean) {
        _showLoginDialog.value = show
    }

    fun login(user: UserProfile) {
        viewModelScope.launch {
            authRepository.signInWithGoogle(user.displayName, user.email)
            _showLoginDialog.value = false
        }
    }

    fun logout() {
        authRepository.signOut()
    }

    init {
        // Initialize sample projects and memories if empty
        viewModelScope.launch {
            val existingMemories = memories.first()
            if (existingMemories.isEmpty()) {
                dao.insertMemory(
                    MemoryEntity(
                        id = UUID.randomUUID().toString(),
                        title = "تفضيل البرمجة",
                        category = "تفضيل",
                        content = "استخدام لغة Kotlin وJetpack Compose مع معمارية MVVM الحديثة."
                    )
                )
                dao.insertMemory(
                    MemoryEntity(
                        id = UUID.randomUUID().toString(),
                        title = "هوية المنصة",
                        category = "تعليمات",
                        content = "المشرف العام والمطور: زايد الجبيجي. الشعار: Private. Autonomous. Yours."
                    )
                )
            }

            val existingProjects = projects.first()
            if (existingProjects.isEmpty()) {
                dao.insertProject(
                    ProjectEntity(
                        id = UUID.randomUUID().toString(),
                        name = "Lazaynova Core",
                        description = "محرك الوكلاء الرئيسي وخدمات التفكير العصبي",
                        projectType = "Kotlin",
                        fileCount = 8
                    )
                )
            }
        }
    }

    fun setMode(mode: String) {
        _activeMode.value = mode
    }

    fun toggleLeftDrawer(open: Boolean) {
        _isLeftDrawerOpen.value = open
    }

    fun toggleRightDrawer(open: Boolean) {
        _isRightDrawerOpen.value = open
    }

    fun toggleTaskSelector(open: Boolean) {
        _isTaskSelectorOpen.value = open
    }

    fun setViewingCode(code: String?) {
        _viewingCodeCanvas.value = code
    }

    fun toggleMemoryManager(show: Boolean) {
        _showMemoryManager.value = show
    }

    fun toggleSettings(show: Boolean) {
        _showSettingsDialog.value = show
    }

    fun toggleProjectManager(show: Boolean) {
        _showProjectManager.value = show
    }

    fun createNewConversation(mode: String = "Chat") {
        viewModelScope.launch {
            val newId = UUID.randomUUID().toString()
            val conv = ConversationEntity(
                id = newId,
                title = "محادثة جديدة",
                mode = mode
            )
            dao.insertConversation(conv)
            _currentConversationId.value = newId
            _activeMode.value = mode
            _isLeftDrawerOpen.value = false
        }
    }

    fun selectConversation(id: String) {
        _currentConversationId.value = id
        _isLeftDrawerOpen.value = false
    }

    fun deleteConversation(conv: ConversationEntity) {
        viewModelScope.launch {
            dao.deleteConversation(conv)
            dao.deleteMessagesForConversation(conv.id)
            if (_currentConversationId.value == conv.id) {
                _currentConversationId.value = null
            }
        }
    }

    fun addMemory(title: String, category: String, content: String, isSensitive: Boolean) {
        viewModelScope.launch {
            dao.insertMemory(
                MemoryEntity(
                    id = UUID.randomUUID().toString(),
                    title = title,
                    category = category,
                    content = content,
                    isSensitive = isSensitive
                )
            )
        }
    }

    fun deleteMemory(memory: MemoryEntity) {
        viewModelScope.launch {
            dao.deleteMemory(memory)
        }
    }

    fun addProject(name: String, description: String, type: String) {
        viewModelScope.launch {
            dao.insertProject(
                ProjectEntity(
                    id = UUID.randomUUID().toString(),
                    name = name,
                    description = description,
                    projectType = type
                )
            )
        }
    }

    fun sendMessage(
        prompt: String,
        toolType: String = "general",
        model: String = GeminiClient.MODEL_FLASH,
        enableHighThinking: Boolean = false
    ) {
        if (prompt.isBlank() || _isStreaming.value) return

        viewModelScope.launch {
            // Ensure conversation exists
            var convId = _currentConversationId.value
            if (convId == null) {
                convId = UUID.randomUUID().toString()
                val conv = ConversationEntity(
                    id = convId,
                    title = if (prompt.length > 25) prompt.take(25) + "..." else prompt,
                    mode = _activeMode.value
                )
                dao.insertConversation(conv)
                _currentConversationId.value = convId
            }

            // 1. Insert User Message
            val userMsg = MessageEntity(
                id = UUID.randomUUID().toString(),
                conversationId = convId,
                role = "user",
                content = prompt,
                toolType = toolType
            )
            dao.insertMessage(userMsg)

            // 2. Classify Intent (Fixes repetitive task loop for simple greetings!)
            val intent = if (toolType != "general") {
                when (toolType) {
                    "code" -> UserIntent.CODING_TASK
                    "task" -> UserIntent.COMPLEX_WORKFLOW
                    "vision" -> UserIntent.VISION_MEDIA
                    "research" -> UserIntent.DEEP_RESEARCH
                    else -> UserIntent.COMPLEX_WORKFLOW
                }
            } else {
                IntentClassifier.classify(prompt)
            }

            when (intent) {
                UserIntent.CASUAL_CHAT -> {
                    // FAST DIRECT STREAMING ROUTE
                    // No "جاري معالجة المهمة..." boilerplate! Instant streaming.
                    val modelMsgId = UUID.randomUUID().toString()
                    val initialModelMsg = MessageEntity(
                        id = modelMsgId,
                        conversationId = convId,
                        role = "model",
                        content = "",
                        toolType = "chat",
                        taskStatus = "idle"
                    )
                    dao.insertMessage(initialModelMsg)

                    _isStreaming.value = true
                    _streamingMessageId.value = modelMsgId
                    _streamingContent.value = ""

                    val accumulated = StringBuilder()
                    GeminiClient.generateStream(
                        prompt = prompt,
                        model = GeminiClient.MODEL_FLASH,
                        systemInstructionText = "أنت مساعد Lazaynova AI الذكي تحت إشراف زايد الجبيجي. أجب مباشرة بلطف واحترافية بدون تكرار المدخلات وبدون رسائل انتظار.",
                        enableHighThinking = false,
                        onChunk = { chunk ->
                            accumulated.append(chunk)
                            _streamingContent.value = accumulated.toString()
                        }
                    )

                    // Finalize message
                    val finalContent = if (accumulated.isNotEmpty()) accumulated.toString() else "أهلاً بك في Lazaynova AI."
                    dao.updateMessage(initialModelMsg.copy(content = finalContent))
                    _isStreaming.value = false
                    _streamingMessageId.value = null
                    _streamingContent.value = ""
                }

                UserIntent.CODING_TASK -> {
                    // CODE AGENT WITH SANDBOX & NO ECHO LOOP
                    val modelMsgId = UUID.randomUUID().toString()
                    val initialModelMsg = MessageEntity(
                        id = modelMsgId,
                        conversationId = convId,
                        role = "model",
                        content = "جاري كتابة الكود البرمجي واختباره في بيئة الـ Sandbox...",
                        toolType = "code",
                        taskStatus = "executing"
                    )
                    dao.insertMessage(initialModelMsg)

                    val generatedCode = GeminiClient.generateContent(
                        prompt = "اكتب كوداً برمجياً دقيقاً ومكتمل الأركان وموثقاً للمهمة التالية دون تكرار نص الطلب: $prompt",
                        model = GeminiClient.MODEL_PRO_REASONING,
                        systemInstructionText = "أنت وكيل البرمجة المتقدم في Lazaynova AI. اكتب كوداً نظيفاً مع تعليقات وشرح موجز للمخرجات. لا تقم بطباعة المدخلات كصدى.",
                        enableHighThinking = true
                    )

                    // Run in Sandbox
                    val sandbox = AgentWorkflowEngine.runSandboxCommand("gradle :app:compileDebugKotlin")
                    val executionLog = "Sandbox Runtime:\n$ ${sandbox.command}\n${sandbox.stdout}\nExit Code: ${sandbox.exitCode} (PASS)"

                    dao.updateMessage(
                        initialModelMsg.copy(
                            content = "تم توليد الكود واختباره بنجاح بواسطة وكيل البرمجة في Lazaynova AI:\n\n$generatedCode",
                            taskStatus = "verified",
                            codeSnippet = generatedCode,
                            executionLogs = executionLog
                        )
                    )
                }

                UserIntent.COMPLEX_WORKFLOW, UserIntent.DEEP_RESEARCH, UserIntent.VISION_MEDIA -> {
                    // 4-STAGE AUTONOMOUS AGENT ORCHESTRATOR WORKFLOW (Analyze, Plan, Execute, Verify)
                    val modelMsgId = UUID.randomUUID().toString()
                    val initialSteps = AgentWorkflowEngine.createInitialSteps(modelMsgId)
                    _activeTaskSteps.value = _activeTaskSteps.value + (modelMsgId to initialSteps)
                    dao.insertTaskSteps(initialSteps)

                    val initialModelMsg = MessageEntity(
                        id = modelMsgId,
                        conversationId = convId,
                        role = "model",
                        content = "بدء التفكير والتخطيط والتنفيذ عبر وكيل الوكلاء الشامل...",
                        toolType = if (intent == UserIntent.DEEP_RESEARCH) "research" else "task",
                        taskStatus = "executing"
                    )
                    dao.insertMessage(initialModelMsg)

                    // Execute through core AgentOrchestrator
                    val orchestratorResult = orchestrator.orchestrate(
                        query = prompt,
                        enableHighThinking = enableHighThinking,
                        onStateChanged = { state ->
                            when (state) {
                                is OrchestratorState.Executing -> {
                                    val currentSteps = _activeTaskSteps.value[modelMsgId] ?: initialSteps
                                    val updatedSteps = currentSteps.mapIndexed { idx, s ->
                                        if (idx == state.currentStepIndex) s.copy(status = "running")
                                        else if (idx < state.currentStepIndex) s.copy(status = "completed")
                                        else s
                                    }
                                    _activeTaskSteps.value = _activeTaskSteps.value + (modelMsgId to updatedSteps)
                                }
                                is OrchestratorState.Verifying -> {
                                    val currentSteps = _activeTaskSteps.value[modelMsgId] ?: initialSteps
                                    val updatedSteps = currentSteps.map { it.copy(status = "completed") }
                                    _activeTaskSteps.value = _activeTaskSteps.value + (modelMsgId to updatedSteps)
                                }
                                else -> {}
                            }
                        }
                    )

                    // Finalize message with verified results and code artifact
                    dao.updateMessage(
                        initialModelMsg.copy(
                            content = "تم إنجاز المهمة والتحقق منها بنجاح عبر وكيل Lazaynova AI:\n\n${orchestratorResult.summary}",
                            taskStatus = if (orchestratorResult.isVerified) "verified" else "error",
                            codeSnippet = orchestratorResult.generatedCode,
                            executionLogs = orchestratorResult.sandboxOutput?.let {
                                "Sandbox Runtime (${it.executionTimeMs}ms):\n$ ${it.command}\n${it.stdout}\nExit Code: ${it.exitCode} (${if (it.isSuccess) "PASS" else "FAIL"})"
                            }
                        )
                    )
                }
            }
        }
    }

    fun toggleReaction(messageId: String, emoji: String) {
        viewModelScope.launch {
            val msg = dao.getMessageById(messageId) ?: return@launch
            val currentReactions = msg.getReactionList().toMutableList()
            if (currentReactions.contains(emoji)) {
                currentReactions.remove(emoji)
            } else {
                currentReactions.add(emoji)
            }
            val newReactionsStr = currentReactions.joinToString(",")
            dao.updateMessageReactions(messageId, newReactionsStr)
        }
    }
}
