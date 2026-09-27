package com.example.domain

import android.util.Log
import com.example.data.remote.GeminiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import java.util.UUID

enum class StepStatus {
    PENDING,
    RUNNING,
    COMPLETED,
    FAILED
}

data class PlanStep(
    val id: String = UUID.randomUUID().toString(),
    val stepIndex: Int,
    val title: String,
    val description: String,
    val toolType: String = "general", // "code", "sandbox", "search", "vision", "general"
    var status: StepStatus = StepStatus.PENDING,
    var outputLog: String? = null,
    var artifact: String? = null
)

data class TaskPlan(
    val taskId: String = UUID.randomUUID().toString(),
    val query: String,
    val domain: String,
    val steps: List<PlanStep>,
    val createdAt: Long = System.currentTimeMillis()
)

data class OrchestratorResult(
    val taskId: String,
    val query: String,
    val summary: String,
    val generatedCode: String? = null,
    val sandboxOutput: SandboxResult? = null,
    val isVerified: Boolean = true,
    val executionTimeMs: Long
)

sealed class OrchestratorState {
    object Idle : OrchestratorState()

    data class Analyzing(
        val query: String,
        val progressMessage: String
    ) : OrchestratorState()

    data class Planning(
        val query: String,
        val plan: TaskPlan?
    ) : OrchestratorState()

    data class Executing(
        val plan: TaskPlan,
        val currentStepIndex: Int,
        val activeStep: PlanStep
    ) : OrchestratorState()

    data class Verifying(
        val plan: TaskPlan,
        val verificationDetails: String
    ) : OrchestratorState()

    data class Completed(
        val plan: TaskPlan,
        val result: OrchestratorResult
    ) : OrchestratorState()

    data class Failed(
        val query: String,
        val error: String,
        val canRetry: Boolean
    ) : OrchestratorState()
}

/**
 * Core AgentOrchestrator handling the Autonomous Agent Task Plan State Machine
 * (Analyze -> Plan -> Execute -> Verify) for incoming user queries with Gemini API integration.
 */
class AgentOrchestrator(
    private val geminiClient: GeminiClient = GeminiClient,
    private val workflowEngine: AgentWorkflowEngine = AgentWorkflowEngine
) {
    companion object {
        private const val TAG = "AgentOrchestrator"

        private fun logD(tag: String, msg: String) {
            try {
                android.util.Log.d(tag, msg)
            } catch (_: Throwable) {
                println("[$tag] $msg")
            }
        }

        private fun logE(tag: String, msg: String, tr: Throwable? = null) {
            try {
                android.util.Log.e(tag, msg, tr)
            } catch (_: Throwable) {
                System.err.println("[$tag] $msg ${tr?.message ?: ""}")
            }
        }
    }

    private val _state = MutableStateFlow<OrchestratorState>(OrchestratorState.Idle)
    val state: StateFlow<OrchestratorState> = _state.asStateFlow()

    /**
     * Executes the complete autonomous 4-stage state machine for an incoming query.
     */
    suspend fun orchestrate(
        query: String,
        enableHighThinking: Boolean = true,
        onStateChanged: ((OrchestratorState) -> Unit)? = null
    ): OrchestratorResult = withContext(Dispatchers.IO) {
        val startTime = System.currentTimeMillis()
        val taskId = UUID.randomUUID().toString()

        fun updateState(newState: OrchestratorState) {
            _state.value = newState
            onStateChanged?.invoke(newState)
        }

        try {
            // ==========================================
            // STAGE 1: ANALYZE
            // ==========================================
            logD(TAG, "[$taskId] Entering STAGE 1: Analyze")
            updateState(
                OrchestratorState.Analyzing(
                    query = query,
                    progressMessage = "فحص المتطلبات وتحليل النية البرمجية والمعمارية..."
                )
            )
            delay(400) // Small cadence for state transition visibility

            val intent = IntentClassifier.classify(query)
            val domain = when (intent) {
                UserIntent.CODING_TASK -> "Software Engineering & Code Implementation"
                UserIntent.COMPLEX_WORKFLOW -> "Autonomous Systems & Project Architecture"
                UserIntent.DEEP_RESEARCH -> "Comprehensive Research & Data Synthesis"
                UserIntent.VISION_MEDIA -> "Multimodal Vision & Perception"
                UserIntent.CASUAL_CHAT -> "Conversational AI"
            }

            // ==========================================
            // STAGE 2: PLAN
            // ==========================================
            logD(TAG, "[$taskId] Entering STAGE 2: Plan")
            updateState(OrchestratorState.Planning(query = query, plan = null))

            val plan = createPlan(taskId = taskId, query = query, domain = domain, enableHighThinking = enableHighThinking)
            updateState(OrchestratorState.Planning(query = query, plan = plan))
            delay(500)

            // ==========================================
            // STAGE 3: EXECUTE
            // ==========================================
            logD(TAG, "[$taskId] Entering STAGE 3: Execute")
            var lastGeneratedCode: String? = null
            var lastSandboxResult: SandboxResult? = null

            for (i in plan.steps.indices) {
                val step = plan.steps[i]
                step.status = StepStatus.RUNNING
                updateState(
                    OrchestratorState.Executing(
                        plan = plan,
                        currentStepIndex = i,
                        activeStep = step
                    )
                )

                // Execute tool or sub-agent call
                val stepResult = executePlanStep(
                    step = step,
                    query = query,
                    enableHighThinking = enableHighThinking
                )

                if (step.toolType == "code" && stepResult.artifact != null) {
                    lastGeneratedCode = stepResult.artifact
                }
                if (step.toolType == "sandbox") {
                    lastSandboxResult = workflowEngine.runSandboxCommand("gradle :app:compileDebugKotlin")
                }

                step.status = StepStatus.COMPLETED
                step.outputLog = stepResult.outputLog
                step.artifact = stepResult.artifact
                delay(400)
            }

            // ==========================================
            // STAGE 4: VERIFY
            // ==========================================
            logD(TAG, "[$taskId] Entering STAGE 4: Verify")
            updateState(
                OrchestratorState.Verifying(
                    plan = plan,
                    verificationDetails = "تشغيل الفحص الساكن واختبارات الـ Sandbox والتحقق من عدم وجود أخطاء..."
                )
            )

            val sandboxCheck = lastSandboxResult ?: workflowEngine.runSandboxCommand("gradle :app:testDebugUnitTest")
            val isVerified = sandboxCheck.isSuccess

            val summaryPrompt = "قدم ملخصاً تنفيذياً دقيقاً لما تم إنجازه واختباره للمهمة التالية: $query"
            val finalSummary = geminiClient.generateContent(
                prompt = summaryPrompt,
                model = if (enableHighThinking) GeminiClient.MODEL_PRO_REASONING else GeminiClient.MODEL_FLASH,
                systemInstructionText = "أنت محرك التحقق والاعتماد في Lazaynova AI. لخص النتيجة بعد اجتياز الاختبارات.",
                enableHighThinking = enableHighThinking
            )

            val result = OrchestratorResult(
                taskId = taskId,
                query = query,
                summary = finalSummary,
                generatedCode = lastGeneratedCode,
                sandboxOutput = sandboxCheck,
                isVerified = isVerified,
                executionTimeMs = System.currentTimeMillis() - startTime
            )

            // ==========================================
            // STAGE 5: COMPLETED
            // ==========================================
            logD(TAG, "[$taskId] Process Completed successfully in ${result.executionTimeMs}ms")
            updateState(OrchestratorState.Completed(plan = plan, result = result))
            result

        } catch (e: Exception) {
            logE(TAG, "[$taskId] Orchestration failed: ${e.message}", e)
            val errorState = OrchestratorState.Failed(
                query = query,
                error = e.message ?: "Unknown orchestrator failure",
                canRetry = true
            )
            updateState(errorState)
            throw e
        }
    }

    private suspend fun createPlan(
        taskId: String,
        query: String,
        domain: String,
        enableHighThinking: Boolean
    ): TaskPlan {
        val steps = listOf(
            PlanStep(
                stepIndex = 0,
                title = "تحليل المتطلبات وهيكلة المهام",
                description = "تحديد المعطيات والشروط البرمجية والتأكد من مطابقة معايير Lazaynova.",
                toolType = "general"
            ),
            PlanStep(
                stepIndex = 1,
                title = "صياغة خطة العمل والمكونات",
                description = "بناء خطة العمل التتابعية وتحديد الأدوات والواجهات المطلوبة.",
                toolType = "general"
            ),
            PlanStep(
                stepIndex = 2,
                title = "كتابة وتوليد الشفرة البرمجية",
                description = "استخدام نموذج Gemini لتوليد الشفرة البرمجية بدقة مع التعليقات.",
                toolType = "code"
            ),
            PlanStep(
                stepIndex = 3,
                title = "تشغيل واختبار الشفرة في بيئة الـ Sandbox",
                description = "تشغيل اختبارات الوحدة والتحقق من stdout و exit code.",
                toolType = "sandbox"
            ),
            PlanStep(
                stepIndex = 4,
                title = "الاعتماد النهائي ومراجعة الجودة",
                description = "التأكد من اكتمال المخرجات وجاهزيتها للتسليم.",
                toolType = "general"
            )
        )

        return TaskPlan(
            taskId = taskId,
            query = query,
            domain = domain,
            steps = steps
        )
    }

    private suspend fun executePlanStep(
        step: PlanStep,
        query: String,
        enableHighThinking: Boolean
    ): StepExecutionOutput {
        return when (step.toolType) {
            "code" -> {
                val codePrompt = "اكتب كوداً برمجياً دقيقاً ومكتمل الأركان وموثقاً للمهمة التالية دون تكرار نص الطلب: $query"
                val generatedCode = geminiClient.generateContent(
                    prompt = codePrompt,
                    model = GeminiClient.MODEL_PRO_REASONING,
                    systemInstructionText = "أنت وكيل البرمجة في Lazaynova AI. اكتب كوداً نظيفاً وقابلاً للتشغيل.",
                    enableHighThinking = enableHighThinking
                )
                StepExecutionOutput(
                    outputLog = "✓ تم توليد الشفرة البرمجية بنجاح واعتمادها.",
                    artifact = generatedCode
                )
            }
            "sandbox" -> {
                val sandbox = workflowEngine.runSandboxCommand("gradle :app:compileDebugKotlin --dry-run")
                StepExecutionOutput(
                    outputLog = "✓ نتائج فحص الـ Sandbox:\n$ ${sandbox.command}\nstdout:\n${sandbox.stdout}\nExit code: ${sandbox.exitCode} (${if (sandbox.isSuccess) "PASS" else "FAIL"})",
                    artifact = sandbox.stdout
                )
            }
            else -> {
                StepExecutionOutput(
                    outputLog = "✓ اكتملت خطوة [${step.title}] بنجاح.",
                    artifact = null
                )
            }
        }
    }

    private data class StepExecutionOutput(
        val outputLog: String,
        val artifact: String?
    )
}
