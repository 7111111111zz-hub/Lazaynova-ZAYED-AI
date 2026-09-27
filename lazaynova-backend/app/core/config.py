import os
from pydantic_settings import BaseSettings
from typing import Optional

class Settings(BaseSettings):
    PROJECT_NAME: str = "Lazaynova AI Engine"
    VERSION: str = "1.0.0"
    API_V1_STR: str = "/api/v1"
    
    # Environment
    ENVIRONMENT: str = "development"
    DEBUG: bool = True
    
    # Security
    SECRET_KEY: str = os.getenv("SECRET_KEY", "lazaynova_super_secret_jwt_key_32bytes_long")
    ACCESS_TOKEN_EXPIRE_MINUTES: int = 60 * 24 * 7  # 7 days
    ALGORITHM: str = "HS256"
    
    # Database & Cache
    DATABASE_URL: str = os.getenv(
        "DATABASE_URL",
        "postgresql+asyncpg://lazaynova_user:lazaynova_secure_password_2026@localhost:5432/lazaynova_db"
    )
    REDIS_URL: str = os.getenv("REDIS_URL", "redis://localhost:6379/0")
    
    # AI Engine & Gemini API
    GEMINI_API_KEY: str = os.getenv("GEMINI_API_KEY", "")
    BRAVE_API_KEY: Optional[str] = os.getenv("BRAVE_API_KEY", None)
    
    # Sandbox path
    SANDBOX_BASE_DIR: str = "/var/lazaynova/sandbox"
    
    class Config:
        case_sensitive = True
        env_file = ".env"

settings = Settings()
