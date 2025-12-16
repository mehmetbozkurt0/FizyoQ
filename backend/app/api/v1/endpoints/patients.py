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
async def update_patient_status(patient_id: int, status: str, db: Session = Depends(get_db)):
    updated_patient = crud_patient.update_status(db, patient_id=patient_id, new_status=status)
    if updated_patient is None:
        raise HTTPException(status_code=404, detail=f"Cannot find patient with id {patient_id}")
    return updated_patient

@router.delete("/clean")
def clean_db(db: Session = Depends(get_db)):
    crud_patient.delete_all(db)
    return {"message": "Whole list has deleted."}