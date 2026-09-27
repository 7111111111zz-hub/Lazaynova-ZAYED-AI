package com.example.domain

enum class UserIntent {
    CASUAL_CHAT,     // "السلام عليكم", "مرحبا", "من أنت", "شكراً", أسئلة عامة سريعة
    CODING_TASK,     // كتابة كود، فحص برنامج، إصلاح أخطاء
    COMPLEX_WORKFLOW,// بناء تطبيق كامل، مهام متعددة الخطوات، مشاريع
    VISION_MEDIA,    // تحليل صورة، وسائط
    DEEP_RESEARCH    // بحث معمق، دراسة مقارنة
}

object IntentClassifier {
    fun classify(prompt: String): UserIntent {
        val text = prompt.trim().lowercase()

        // 1. Greetings and simple interactions
        val isGreeting = text in listOf(
            "السلام عليكم", "سلام", "مرحبا", "أهلا", "اهلا", "صباح الخير", "مساء الخير",
            "hello", "hi", "hey", "how are you", "كيف حالك", "شو اخبارك", "من انت", "من أنت",
            "عرف عن نفسك", "شكرا", "شكراً", "تسلم", "يعطيك العافية", "who are you"
        )
        if (isGreeting || text.length <= 15 && (text.startsWith("مرحبا") || text.startsWith("سلام") || text.startsWith("أهلا"))) {
            return UserIntent.CASUAL_CHAT
        }

        // 2. Coding tasks
        val codeKeywords = listOf(
            "كود", "دالة", "function", "class", "برمج", "بايثون", "python", "kotlin",
            "java", "javascript", "typescript", "html", "css", "sql", "bug", "خطأ",
            "صحيح الكود", "fix", "script", "خوارزمية", "algorithm"
        )
        if (codeKeywords.any { text.contains(it) }) {
            return UserIntent.CODING_TASK
        }

        // 3. Complex workflows and projects
        val workflowKeywords = listOf(
            "أنشئ تطبيق", "ابن تطبيق", "مشروع متكامل", "build an app", "create project",
            "workflow", "مهمة معقدة", "تطبيق أندرويد", "تطبيق لإدارة", "نظام إدارة",
            "تطوير منصة", "full app", "to-do app", "todo app"
        )
        if (workflowKeywords.any { text.contains(it) }) {
            return UserIntent.COMPLEX_WORKFLOW
        }

        // 4. Research
        val researchKeywords = listOf(
            "ابحث", "بحث عميق", "deep research", "تقرير شامل", "دراسة تحليلية", "مقارنة مفصلة"
        )
        if (researchKeywords.any { text.contains(it) }) {
            return UserIntent.DEEP_RESEARCH
        }

        // 5. Vision / Image
        val visionKeywords = listOf(
            "حلل الصورة", "صورة", "image", "فيديو", "video", "veo", "رؤية"
        )
        if (visionKeywords.any { text.contains(it) }) {
            return UserIntent.VISION_MEDIA
        }

        // Default to casual chat for simple queries so the user gets fast direct answers
        return if (text.length < 80) UserIntent.CASUAL_CHAT else UserIntent.COMPLEX_WORKFLOW
    }
}
