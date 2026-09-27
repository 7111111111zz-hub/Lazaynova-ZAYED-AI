import asyncio
from typing import List, Dict, Any, Callable, Optional
from datetime import datetime

class WorkflowStep:
    def __init__(self, step_id: str, title: str, tool_name: str, args: Dict[str, Any]):
        self.step_id = step_id
        self.title = title
        self.tool_name = tool_name
        self.args = args
        self.status = "pending" # pending, running, completed, failed
        self.output: Optional[Any] = None
        self.logs: List[str] = []

    def to_dict(self) -> Dict[str, Any]:
        return {
            "step_id": self.step_id,
            "title": self.title,
            "tool_name": self.tool_name,
            "status": self.status,
            "output": self.output,
            "logs": self.logs
        }

class WorkflowEngine:
    def __init__(self):
        self.max_retries = 3

    async def execute_workflow(
        self,
        steps: List[WorkflowStep],
        step_executor: Callable[[WorkflowStep], Any]
    ) -> Dict[str, Any]:
        results = []
        all_passed = True
        
        for step in steps:
            step.status = "running"
            step.logs.append(f"Started at {datetime.utcnow().isoformat()}")
            
            success = False
            for attempt in range(1, self.max_retries + 1):
                try:
                    step.logs.append(f"Executing attempt {attempt}/{self.max_retries}")
                    output = await step_executor(step)
                    step.output = output
                    step.status = "completed"
                    step.logs.append(f"Completed successfully at {datetime.utcnow().isoformat()}")
                    success = True
                    break
                except Exception as e:
                    step.logs.append(f"Attempt {attempt} failed: {str(e)}")
                    await asyncio.sleep(0.5)

            if not success:
                step.status = "failed"
                all_passed = False
                break
            
            results.append(step.to_dict())

        return {
            "workflow_completed": all_passed,
            "total_steps": len(steps),
            "executed_steps": len(results),
            "steps": [s.to_dict() for s in steps]
        }

workflow_engine = WorkflowEngine()
