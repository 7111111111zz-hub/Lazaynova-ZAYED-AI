from fastapi import FastAPI, HTTPException, Depends
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel
from typing import Dict, Any, List, Optional
from app.core.config import settings
from app.agents.core_engine import core_agent_engine
from app.agents.tool_manager import tool_manager
from app.projects.manager import project_manager
from app.verification.pipeline import verification_pipeline

app = FastAPI(
    title=settings.PROJECT_NAME,
    version=settings.VERSION,
    description="Lazaynova AI Autonomous Agent Backend & Execution Sandbox"
)

# Enable CORS for Android client & development apps
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

class QueryRequest(BaseModel):
    prompt: str
    user_id: Optional[str] = "default_user"
    mode: Optional[str] = "general"

class ToolRequest(BaseModel):
    tool_name: str
    arguments: Dict[str, Any]

class ProjectCreateRequest(BaseModel):
    name: str
    description: str
    project_type: Optional[str] = "Android"

@app.get("/health")
def health_check():
    return {
        "status": "online",
        "service": settings.PROJECT_NAME,
        "version": settings.VERSION,
        "supervisor": "Zayed Al-Jubaiji"
    }

@app.post(f"{settings.API_V1_STR}/agent/query")
async def execute_agent_query(req: QueryRequest):
    try:
        result = await core_agent_engine.route_and_execute(req.prompt, user_id=req.user_id)
        return {"success": True, "data": result}
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

@app.post(f"{settings.API_V1_STR}/tools/execute")
async def execute_tool_endpoint(req: ToolRequest):
    try:
        result = await tool_manager.execute_tool(req.tool_name, req.arguments)
        return {"success": True, "result": result}
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

@app.get(f"{settings.API_V1_STR}/projects")
def list_projects_endpoint():
    return {"success": True, "projects": project_manager.list_projects()}

@app.post(f"{settings.API_V1_STR}/projects")
def create_project_endpoint(req: ProjectCreateRequest):
    project = project_manager.create_project(req.name, req.description, req.project_type)
    return {"success": True, "project": project}

@app.post(f"{settings.API_V1_STR}/verification/run")
async def run_verification_endpoint():
    report = await verification_pipeline.run_pipeline()
    return {"success": True, "verification": report}
