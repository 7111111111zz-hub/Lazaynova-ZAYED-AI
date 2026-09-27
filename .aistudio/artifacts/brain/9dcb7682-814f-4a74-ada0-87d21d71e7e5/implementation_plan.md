# Lazaynova AI - Intelligent Neural Platform & Autonomous Agent

A production-grade Android application for **Lazaynova AI** ("Private. Autonomous. Yours.") under the leadership of Zayed Al-Jubaiji, precisely faithfully replicating the provided light futuristic interface design, featuring full Arabic RTL support, an intelligent multi-agent orchestration engine powered by Gemini, an interactive task selector modal, and complete conversation and memory persistence.

---

### User Review & Critical Decisions

> [!IMPORTANT]
> The following architectural and design parameters were confirmed during clarification and will govern the implementation:

- **Confirmed Aesthetic Direction**: Light futuristic theme matching the screenshot (`فاتح مثل الصورة تماماً`), utilizing clean high-tech surfaces (`#F8FAFC` to `#FFFFFF`), soft ambient cyan/violet glows, glowing neon pill borders, and 3D iridescent metallic badges.
- **Confirmed Language & Layout**: Full Arabic localization as the primary language with native RTL (Right-to-Left) direction, preserving the brand name "Lazaynova AI" in English/transliteration as shown in the design.
- **Confirmed Agent Architecture**: Dual-path routing (Intent Classifier) to prevent repetitive processing messages on greetings/casual chat, routing casual inquiries to instantaneous streaming responses while dispatching complex coding and multi-step workflows to the Autonomous Task Planner & Verifier pipeline.
- **AI Engine**: Gemini API (`gemini-3.5-flash` for high-speed chat, `gemini-3.1-pro-preview` with High Thinking Level for complex reasoning & coding tasks, and multimodal vision for image/document analysis).

---

### 1. Overview & Core Concept

- **What It Does**: Lazaynova AI serves as an autonomous AI agent platform and personal computing environment. It accepts user prompts, decomposes complex objectives into verifiable milestones (planning, code generation, sandboxed execution, verification), and provides specialized modal tools for Image Generation, Video (Veo), Music, Code Canvas, Deep Research, Guided Learning, and Custom Memory.
- **Target Audience**: Developers, creators, researchers, and professionals seeking a private, autonomous, and intuitive AI workspace that can plan, execute, and verify tasks without repetitive conversational loops.
- **Key Value**: Direct execution without "echo loops" or fake progress messages; genuine Gemini API connectivity; faithful light futuristic aesthetic; seamless Arabic RTL interface matching the reference specification.

---

### 2. User Experience & Visual Design

#### Key User Flows
1. **Zero-State Greeting & Central Hub**:
   - The user opens the app to find the illuminated 3D Lazaynova "L" emblem with orbital ring lighting.
   - The greeting reads: *"مرحباً بك في Lazaynova 👋"* with the subtext *"أنا مساعدك الذكي، جاهز لمساعدتك في أي شيء تتخيله أو تحتاجه."*
   - Interactive prompt suggestions allow one-tap exploration.
2. **Instant Message & Task Input**:
   - The user writes in the glowing rounded input capsule: *"اسأل Lazaynova..."*.
   - Simple prompts and greetings immediately stream an answer directly with no artificial delays or boilerplate loops.
   - Tapping the `+` action button opens the **Task Selector Sheet ("اختيار المهام")**.
3. **Task Selector Modal ("اختيار المهام")**:
   - Top action pills: **ملفات** (Files), **كاميرا** (Camera), **صورة** (Gallery Image).
   - 8-grid feature cards matching the screenshot:
     - 🎨 **إنشاء صور** (Image Generation)
     - 🎬 **فيديو (مع Veo)** (Video Generation)
     - 🎵 **موسيقى** (Music)
     - 💻 **Canvas** (Code & Canvas Workspace)
     - 🔍 **Deep Research** (Comprehensive Research Agent)
     - 🎓 **التعلم الموجه** (Guided Learning)
     - ✨ **الذكاء المخصص** (Custom AI & Personas)
     - 🎛️ **المزيد** (Additional Utilities)
   - Descriptive hint footer explaining tool accessibility.
4. **Autonomous Agent Execution & Verification Flow**:
   - When a complex task or coding project is requested, the Autonomous Agent triggers its multi-stage lifecycle:
     - 1. Requirement Analysis (تحليل المتطلبات)
     - 2. Plan Formulation (إنشاء خطة العمل)
     - 3. Code Generation (كتابة الشفرة)
     - 4. Sandboxed Build & Test (تشغيل الاختبارات والتحقق)
     - 5. Verification & Delivery (الاعتماد والتسليم)
   - Real-time step progress indicator with collapsible log console.
5. **Left Navigation Drawer ("القائمة الجانبية")**:
   - Lazaynova 3D brand header + Search bar (*"بحث"*).
   - Core tabs: دردشة جديدة (New Chat), المحادثات (Conversations), المكتبة (Library), الوسائط (Media), المستندات (Documents), جدولة (Schedule).
   - Basic settings: المظهر (Theme), اللغة (Language), صوت الوكيل (Voice), الإشعارات (Notifications).
   - Advanced features: الوكلاء (Agents), الذاكرة (Memory), التكاملات (Integrations), الأمان والخصوصية (Privacy), السجلات والتحليلات (Logs).
   - Footer: Lazaynova Core (الوكيل الرئيسي) and User Account profile.
6. **Right Drawer / Profile Inspector ("زايد الجبيجي")**:
   - User card: *زايد الجبيجي - حساب مجاني*.
   - Quick component access (الوكيل الذكي: تفكير • تخطيط • تنفيذ, رفع الصور, رفع الملفات).
   - Quick feature launch list.
   - Bottom 3D robotic agent avatar card: *"وكيلك الذكي Lazaynova - يفكر. يخطط. ينفذ."*

#### Visual Identity & Theme
- **Color Palette**:
  - `Background`: Pristine soft off-white/sky-tinted surface (`#F8FAFD`).
  - `Surface`: Pure white card surfaces (`#FFFFFF`) with subtle soft shadows (`rgba(99, 102, 241, 0.08)`).
  - `Primary / Accent`: Gradient Violet & Electric Indigo (`#6366F1` to `#4F46E5`).
  - `Secondary Accent`: Cyan & Aqua glow (`#06B6D4` / `#0EA5E9`).
  - `Border / Glow`: Soft ambient gradient pill border (`#818CF8` to `#38BDF8`).
  - `Text Primary`: Deep Slate (`#0F172A`).
  - `Text Secondary`: Muted Slate (`#64748B`).
- **Typography**: Clean, readable Arabic typography with balanced line heights and strong hierarchical weights.
- **RTL Support**: CompositionLocalProvider enforcing `LayoutDirection.Rtl` for authentic Arabic layout.

---

### 3. Key Product Decisions & Trade-Offs

- **Decision 1: Intent-Based Fast Streaming vs. Task Pipeline**
  - *Chosen Approach*: Lightweight heuristic & classifier routing. Greetings, conversational inquiries, and direct answers stream immediately without queued messages. Only explicitly complex tasks, multi-file generation, or deep research invoke the multi-stage planner.
  - *Why*: Eliminates the frustrating user experience where casual greetings trigger *"جاري معالجة المهمة..."*.
- **Decision 2: Direct Gemini API Architecture**
  - *Chosen Approach*: Retrofit + OkHttpClient with Kotlinx Serialization and direct streaming for chat and tool execution, supporting `gemini-3.5-flash` and `gemini-3.1-pro-preview` with High Thinking level.
  - *Why*: Reliable, immediate execution within the Android container with no fragile external dependencies, allowing local offline fallbacks when API keys are not supplied.
- **Decision 3: Local Persistence via Room Database**
  - *Chosen Approach*: Offline-first Room Database storing conversations, messages, agent tasks, memory entries, and project files.
  - *Why*: Ensures user chats, custom instructions, and media records persist reliably between app launches.

---

### 4. Technical Architecture & Data Strategy

```
┌────────────────────────────────────────────────────────────────────────┐
│                          Lazaynova AI App                              │
│                                                                        │
│ ┌──────────────────────┐  ┌──────────────────────┐  ┌────────────────┐ │
│ │ Left Nav Drawer      │  │ Main Screen & Chat   │  │ Right Inspector│ │
│ │ - Library & Media    │  │ - 3D Glow Emblem     │  │ - User Profile │ │
│ │ - Agents & Memory    │  │ - Message Thread     │  │ - Agent Card   │ │
│ │ - Settings & Privacy │  │ - Neon Pill Input    │  │ - Quick Tools  │ │
│ └──────────┬───────────┘  └──────────┬───────────┘  └───────┬────────┘ │
│            │                         │                      │          │
│            ▼                         ▼                      ▼          │
│ ┌────────────────────────────────────────────────────────────────────┐ │
│ │                  Task Selector Modal Sheet                         │ │
│ │   [Files | Camera | Images]  [8-Grid Specialized Tools]            │ │
│ └────────────────────────────────────┬───────────────────────────────┘ │
└──────────────────────────────────────┼─────────────────────────────────┘
                                       │
                                       ▼
┌────────────────────────────────────────────────────────────────────────┐
│                        Lazaynova Engine Core                           │
│                                                                        │
│   ┌────────────────────┐   Fast Stream   ┌─────────────────────────┐   │
│   │  Intent Router /   ├────────────────►│ Direct Gemini Streamer  │   │
│   │  Classifier        │                 │ (gemini-3.5-flash)      │   │
│   └────────┬───────────┘                 └─────────────────────────┘   │
│            │ Complex Task                                              │
│            ▼                                                           │
│   ┌────────────────────────────────────────────────────────────────┐   │
│   │ Autonomous Multi-Agent Pipeline                                │   │
│   │  [Planner] ➔ [Executor / Code Sandbox] ➔ [Verifier & Checker]  │   │
│   │  (gemini-3.1-pro-preview + High Thinking Level)                │   │
│   └────────────────────────────────┬───────────────────────────────┘   │
│                                    │                                   │
│   ┌────────────────────────────────┴───────────────────────────────┐   │
│   │ Local Room DB & Memory Manager (Chats, Tasks, Projects, Media) │   │
│   └────────────────────────────────────────────────────────────────┘   │
└────────────────────────────────────────────────────────────────────────┘
```

#### Entities and State Models
- `ConversationEntity`: ID, title, timestamp, mode (Chat, Code, Task, Vision, Memory).
- `MessageEntity`: ID, conversationId, role (user/model/system), content, timestamp, toolType, taskStatus (Idle, Planning, Executing, Verified, Error).
- `TaskStepEntity`: ID, taskId, stepIndex, title, status (Pending, Running, Completed, Failed), logs.
- `MemoryEntity`: ID, key, category, content, isSensitive, updatedAt.
- `ProjectEntity`: ID, name, description, fileCount, status.

---

### Implementation Stages

1. **Foundational Setup & Platform Identity**:
   - Update `metadata.json` and `strings.xml` to match "Lazaynova AI".
   - Configure Gradle dependencies: Room, Retrofit, Serialization, Coil, Material 3 Icons.
   - Configure `.env.example` with `GEMINI_API_KEY`.
2. **Visual Assets & Icons**:
   - Generate custom high-resolution iridescent 3D "L" emblem and cute 3D AI agent mascot matching the screenshot using `generate_image`.
   - Update adaptive launcher icon.
3. **Design System & Theme**:
   - Implement `Theme.kt` and `Color.kt` for the bright light futuristic palette with gradient brushes and glowing ambient pills.
   - Configure Arabic font styling and RTL layout containers.
4. **Data Layer & Room Persistence**:
   - Implement Room entities, DAOs, and repository for chat histories, agent execution logs, and custom memory.
5. **Agent Engine & Gemini Integration**:
   - Implement Retrofit service for Gemini API with streaming and thinking level support.
   - Implement Intent Classifier & Router to separate casual dialogue from deep agent workflows.
   - Implement Task Execution Pipeline (Planner, Sandbox Runner, Verifier).
6. **Compose UI Implementation**:
   - Left Navigation Drawer (Conversations, Library, Settings, Advanced).
   - Right Profile & Component Inspector (Zayed Al-Jubaiji profile & agent card).
   - Central Home View (3D glowing emblem, greeting, quick prompts, message list).
   - Glowing Neon Pill Input with voice, attachments, and instant send.
   - Task Selector Modal Bottom Sheet (Files, Camera, Image + 8 tool grid).
   - Code Canvas & Task Execution Progress viewer.
7. **Verification & Testing**:
   - Compile applet with `compile_applet` and verify spotless build.
   - Test CUJs: instant greeting response, task modal opening, task execution step advancement, drawer navigation, and memory management.
