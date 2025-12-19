from sqlalchemy import Column, Integer, String
from app.core.database import Base

class PhysiotherapistDB(Base):
    __tablename__ = "physiotherapists"

    id = Column(Integer, primary_key=True, index= True)
    name = Column(String, index= True, unique=True)