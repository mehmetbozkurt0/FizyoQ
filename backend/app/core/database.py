import os
from pathlib import Path
from sqlalchemy import create_engine
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy.orm import sessionmaker

BASE_DIR = Path(__file__).resolve().parent.parent.parent
DB_FILE = BASE_DIR / "fizyo.db"

SQL_ALCHEMY_URL = f"sqlite:///{str(DB_FILE).replace(os.sep, '/')}"
print(f"Veritabanı Bağlanıyor: {SQL_ALCHEMY_URL}")

engine = create_engine(SQL_ALCHEMY_URL, connect_args={"check_same_thread": False})
SessionLocal = sessionmaker(autocommit=False, autoflush=False, bind=engine)

Base = declarative_base()