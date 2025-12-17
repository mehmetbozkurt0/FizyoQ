from typing import List
from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session
from app.core.database import SessionLocal
from app.crud import crud_patient
from app.schemas import patient as schemas

router = APIRouter()

# Dependency
def get_db():
    db = SessionLocal()
    try:
        yield db
    finally:
        db.close()

@router.get("/", response_model=List[schemas.Patient])
async def read_patients(db: Session = Depends(get_db)):
    return crud_patient.get_patients(db)
    
@router.post("/", response_model=schemas.Patient)
async def create_patient(patient: schemas.PatientCreate, db: Session = Depends(get_db)):
    return crud_patient.create_patient(db=db, patient=patient)

@router.put("/{patient_id}", response_model=schemas.Patient)
def update_patient(patient_id: int, patient_data: schemas.PatientUpdate, db: Session = Depends(get_db)):
    updated_patient = crud_patient.update_patient(db, patient_id=patient_id, patient_data=patient_data)
    if updated_patient is None:
        raise HTTPException(status_code=404, detail="Patient not found")
    return updated_patient

@router.delete("/clean")
def clean_db(db: Session = Depends(get_db)):
    crud_patient.delete_all(db)
    return {"message": "Whole list has deleted."}

@router.delete("/{patient_id}")
def delete_patient(patient_id: int, db: Session = Depends(get_db)):
    crud_patient.delete_patient(db, patient_id)
    return {"message":"Kullanıcı silindi!"}