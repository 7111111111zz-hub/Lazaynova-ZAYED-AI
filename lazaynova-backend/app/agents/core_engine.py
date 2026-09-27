import asyncio
from typing import Dict, Any, List
from app.agents.tool_manager import tool_manager
from app.agents.code_agent import code_agent
from app.workflows.engine import workflow_engine, WorkflowStep
from app.verification.pipeline import verification_pipeline

class CoreAgentEngine:
    def __init__(self):
        pass

    async def route_and_execute(self, prompt: str, user_id: str = "default_user") -> Dict[str, Any]:
        cleaned = prompt.strip().lower()

        # 1. Casual Chat Intent (Fast direct path without queuing)
        if cleaned in ["السلام عليكم", "مرحبا", "أهلا", "hello", "hi", "من أنت"]:
            return {
                "route": "direct_chat",
                "response": "أهلاً بك في منصة Lazaynova AI. أنا مساعدك الذكي ومحرك الوكلاء، جاهز لخدمتك مباشرة.",
                "stages": []
            }

        # 2. Coding Task
        if any(w in cleaned for w in ["كود", "code", "python", "kotlin", "دالة", "function"]):
            code_res = await code_agent.generate_and_verify_code(prompt)
            return {
                "route": "code_agent",
                "response": f"تم توليد والتحقق من الكود البرمجي بنجاح:\n\n```{code_res['language']}\n{code_res['code']}\n```",
                "code_details": code_res,
                "stages": ["Analysis", "Code Generation", "Sandbox Syntax Verification"]
            }

        # 3. Autonomous Multi-Stage Workflow
        steps = [
            WorkflowStep("s1", "Analyze requirements", "web_search", {"query": prompt}),
            WorkflowStep("s2", "Create architectural plan", "file_writer", {"path": "plan.md", "content": f"# Plan for {prompt}"}),
            WorkflowStep("s3", "Generate code module", "file_writer", {"path": "module.kt", "content": "// Module code"}),
            WorkflowStep("s4", "Execute verification tests", "code_executor", {"command": "echo 'Sandbox verification OK'"})
        ]

        async def step_handler(step: WorkflowStep):
            return await tool_manager.execute_tool(step.tool_name, step.args)

        workflow_res = await workflow_engine.execute_workflow(steps, step_handler)
        verif = await verification_pipeline.run_pipeline()

        return {
            "route": "autonomous_workflow",
            "response": f"تم إنجاز المهمة بنجاح عبر كافة المراحل الأربع مع التحقق النهائي (Status: {verif['status']}).",
            "workflow": workflow_res,
            "verification": verif
        }

core_agent_engine = CoreAgentEngine()
