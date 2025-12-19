from sqlalchemy import Column, Integer, String
from app.core.database import Base

class PatientDB(Base):
    __tablename__ = "patients"

    id = Column(Integer, primary_key=True, index=True)
    full_name = Column(String, index=False)
    physiotherapist = Column(String)
    reservation_time = Column(String)
    date = Column(String)
    status = Column(String, default="Bekliyor")