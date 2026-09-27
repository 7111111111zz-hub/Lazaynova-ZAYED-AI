import asyncio
import os
import time
from typing import Dict, Any, List

class SandboxExecutionResult:
    def __init__(
        self,
        command: str,
        stdout: str,
        stderr: str,
        exit_code: int,
        duration_ms: int,
        is_success: bool
    ):
        self.command = command
        self.stdout = stdout
        self.stderr = stderr
        self.exit_code = exit_code
        self.duration_ms = duration_ms
        self.is_success = is_success

    def to_dict(self) -> Dict[str, Any]:
        return {
            "command": self.command,
            "stdout": self.stdout,
            "stderr": self.stderr,
            "exit_code": self.exit_code,
            "duration_ms": self.duration_ms,
            "is_success": self.is_success
        }

class SandboxEngine:
    DANGEROUS_PATTERNS = [
        "rm -rf /",
        "rm -rf /*",
        ":(){ :|:& };:",
        "curl ",
        "wget ",
        "| sh",
        "| bash",
        "sudo ",
        "chmod -R 777 /",
        "mkfs",
        "dd if=",
        "> /dev/sda",
        "../..",
    ]

    def __init__(self, workspace_path: str = "/tmp/lazaynova_sandbox"):
        self.workspace_path = workspace_path
        os.makedirs(self.workspace_path, exist_ok=True)

    def validate_command(self, cmd: str) -> bool:
        lowered = cmd.lower()
        for pattern in self.DANGEROUS_PATTERNS:
            if pattern in lowered:
                return False
        return True

    async def execute_command(self, cmd: str, timeout_seconds: int = 30) -> SandboxExecutionResult:
        start_time = time.time()
        
        # Security Policy Check
        if not self.validate_command(cmd):
            duration = int((time.time() - start_time) * 1000)
            return SandboxExecutionResult(
                command=cmd,
                stdout="",
                stderr="SECURITY VIOLATION: Command rejected by Lazaynova Sandbox Policy.",
                exit_code=126,
                duration_ms=duration,
                is_success=False
            )

        try:
            process = await asyncio.create_subprocess_shell(
                cmd,
                stdout=asyncio.subprocess.PIPE,
                stderr=asyncio.subprocess.PIPE,
                cwd=self.workspace_path
            )

            try:
                stdout_bytes, stderr_bytes = await asyncio.wait_for(
                    process.communicate(),
                    timeout=timeout_seconds
                )
                duration = int((time.time() - start_time) * 1000)
                exit_code = process.returncode or 0
                return SandboxExecutionResult(
                    command=cmd,
                    stdout=stdout_bytes.decode(errors="replace"),
                    stderr=stderr_bytes.decode(errors="replace"),
                    exit_code=exit_code,
                    duration_ms=duration,
                    is_success=(exit_code == 0)
                )
            except asyncio.TimeoutError:
                process.kill()
                duration = int((time.time() - start_time) * 1000)
                return SandboxExecutionResult(
                    command=cmd,
                    stdout="",
                    stderr=f"EXECUTION TIMEOUT: Terminated after {timeout_seconds}s",
                    exit_code=124,
                    duration_ms=duration,
                    is_success=False
                )
        except Exception as e:
            duration = int((time.time() - start_time) * 1000)
            return SandboxExecutionResult(
                command=cmd,
                stdout="",
                stderr=f"SANDBOX RUNTIME ERROR: {str(e)}",
                exit_code=1,
                duration_ms=duration,
                is_success=False
            )

    def write_file(self, relative_path: str, content: str) -> str:
        # Prevent directory traversal
        clean_path = os.path.normpath(relative_path)
        if clean_path.startswith("..") or os.path.isabs(clean_path):
            raise PermissionError("Path traversal outside sandbox is forbidden")
        
        full_path = os.path.join(self.workspace_path, clean_path)
        os.makedirs(os.path.dirname(full_path), exist_ok=True)
        with open(full_path, "w", encoding="utf-8") as f:
            f.write(content)
        return full_path

    def read_file(self, relative_path: str) -> str:
        clean_path = os.path.normpath(relative_path)
        if clean_path.startswith("..") or os.path.isabs(clean_path):
            raise PermissionError("Path traversal outside sandbox is forbidden")
        
        full_path = os.path.join(self.workspace_path, clean_path)
        if not os.path.exists(full_path):
            raise FileNotFoundError(f"File {relative_path} does not exist in sandbox")
        with open(full_path, "r", encoding="utf-8") as f:
            return f.read()

sandbox_engine = SandboxEngine()
