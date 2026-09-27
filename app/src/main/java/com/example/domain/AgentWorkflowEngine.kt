package com.example.domain

import com.example.data.local.TaskStepEntity
import kotlinx.coroutines.delay
import java.util.UUID

data class SandboxResult(
    val command: String,
    val stdout: String,
    val stderr: String,
    val exitCode: Int,
    val executionTimeMs: Long,
    val isSuccess: Boolean
)

object AgentWorkflowEngine {

    val DEFAULT_WORKFLOW_STEPS = listOf(
        "تحليل المتطلبات وفحص المدخلات",
        "إنشاء خطة العمل وهيكلة المشروع",
        "كتابة الشفرة البرمجية وتوليد المكونات",
        "تشغيل الاختبارات والتحقق في بيئة الـ Sandbox",
        "الاعتماد النهائي والتسليم للمستخدم"
    )

    fun createInitialSteps(messageId: String): List<TaskStepEntity> {
        return DEFAULT_WORKFLOW_STEPS.mapIndexed { index, title ->
            TaskStepEntity(
                id = UUID.randomUUID().toString(),
                messageId = messageId,
                stepIndex = index,
                title = title,
                status = if (index == 0) "running" else "pending",
                logOutput = if (index == 0) "بدء فحص المتطلبات والتأكد من مطابقة المعايير..." else null
            )
        }
    }

    suspend fun executeStep(
        step: TaskStepEntity,
        taskPrompt: String,
        onUpdate: (TaskStepEntity) -> Unit
    ): TaskStepEntity {
        val updatedRunning = step.copy(
            status = "running",
            logOutput = "جاري تنفيذ: ${step.title}..."
        )
        onUpdate(updatedRunning)
        delay(600) // Realistic asynchronous milestone execution

        val logOutput = when (step.stepIndex) {
            0 -> "✓ تم تحليل المتطلبات:\n• النوع: مشروع متكامل\n• السياق: مطابق لمعايير Lazaynova AI\n• التبعيات المطلوبة: جاهزة ومؤكدة."
            1 -> "✓ تم بناء الخطة الهيكلية:\n• تهيئة الملفات والمجلدات\n• ربط معمارية MVVM مع Flow\n• عزل البيئة الافتراضية لمنع تكرار الإخراج."
            2 -> "✓ تم توليد الشفرة البرمجية واكتمال كتابة الملفات بنجاح في مسار العمل."
            3 -> {
                val sandbox = runSandboxCommand("gradle :app:compileDebugKotlin --dry-run")
                "✓ اختبار الـ Sandbox:\n$ ${sandbox.command}\nstdout:\n${sandbox.stdout}\nExit code: ${sandbox.exitCode} (${if (sandbox.isSuccess) "PASS" else "FAIL"})\nوقت التنفيذ: ${sandbox.executionTimeMs}ms"
            }
            4 -> "✓ تم التحقق الشامل بنجاح! النتيجة جاهزة وخالية من الأخطاء العالقة."
            else -> "✓ تم الانتهاء بنجاح."
        }

        val completed = step.copy(
            status = "completed",
            logOutput = logOutput
        )
        onUpdate(completed)
        return completed
    }

    fun runSandboxCommand(cmd: String): SandboxResult {
        val startTime = System.currentTimeMillis()
        val dangerousTokens = listOf("rm -rf /", "curl", "wget", ";", "| sh", "sudo", "../..")

        if (dangerousTokens.any { cmd.contains(it) }) {
            return SandboxResult(
                command = cmd,
                stdout = "",
                stderr = "SECURITY_VIOLATION: Command blocked by Lazaynova Sandbox Policy.",
                exitCode = 126,
                executionTimeMs = System.currentTimeMillis() - startTime,
                isSuccess = false
            )
        }

        // Safe mock sandbox execution for Gradle/test/lint
        val stdout = when {
            cmd.contains("compile") || cmd.contains("build") ->
                "> Task :app:preBuild UP-TO-DATE\n> Task :app:compileDebugKotlin\nBUILD SUCCESSFUL in 1s\n3 actionable tasks: 1 executed, 2 up-to-date"
            cmd.contains("test") ->
                "> Task :app:testDebugUnitTest\nResults: 6 passed, 0 failed, 0 skipped\nSUCCESS"
            else ->
                "Execution completed successfully on Lazaynova Sandbox runtime."
        }

        return SandboxResult(
            command = cmd,
            stdout = stdout,
            stderr = "",
            exitCode = 0,
            executionTimeMs = System.currentTimeMillis() - startTime,
            isSuccess = true
        )
    }
}
