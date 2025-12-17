from pydantic import BaseModel

class PatientBase(BaseModel):
    full_name: str
    physiotherapist: str
    reservation_time: str
    status: str = "Bekliyor"

class PatientCreate(PatientBase):
    pass

class Patient(PatientBase):
    id: int
    class Config:
        from_attributes = True

class PatientUpdate(BaseModel):
    full_name: str
    physiotherapist: str
    reservation_time: str
    status: str