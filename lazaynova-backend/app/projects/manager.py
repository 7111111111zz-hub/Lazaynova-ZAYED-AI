from typing import List, Dict, Any, Optional
import os
import uuid
from app.sandbox.engine import sandbox_engine

class ProjectManager:
    def __init__(self):
        self.projects_cache: Dict[str, Dict[str, Any]] = {}

    def create_project(self, name: str, description: str, project_type: str = "Android") -> Dict[str, Any]:
        project_id = str(uuid.uuid4())
        project_data = {
            "id": project_id,
            "name": name,
            "description": description,
            "project_type": project_type,
            "file_count": 1,
            "status": "active"
        }
        self.projects_cache[project_id] = project_data
        
        # Initialize default project structure in sandbox
        sandbox_engine.write_file(f"projects/{name}/README.md", f"# {name}\n\n{description}\nCreated with Lazaynova AI")
        return project_data

    def get_project(self, project_id: str) -> Optional[Dict[str, Any]]:
        return self.projects_cache.get(project_id)

    def list_projects(self) -> List[Dict[str, Any]]:
        return list(self.projects_cache.values())

project_manager = ProjectManager()
