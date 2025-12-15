from typing import List
from fastapi import FastAPI, Depends, HTTPException
from sqlalchemy.orm import Session
import models
from database import engine, SessionLocal
import crud
import schemas

models.Base.metadata.create_all(bind= engine)

app = FastAPI()

def get_db():
    db = SessionLocal()
    try:
        yield db
    finally:
        db.close()


#Endpoints
@app.get("/patients", response_model=List[schemas.Patient])
async def read_patients(db: Session = Depends(get_db)):
    return crud.get_patients(db)

@app.post("/patients", response_model=schemas.Patient)
async def create_patient( patient: schemas.PatientCreate, db: Session = Depends(get_db)):
    return crud.create_patient(db=db, patient=patient)

@app.put("/patients/{patient_id}", response_model=schemas.Patient)
async def update_patient_status(patinet_id: int, status: str, db: Session = Depends(get_db)):
    updated_patient = crud.update_status(db, patient_id=patinet_id, new_status=status)
    if updated_patient is None:
        raise HTTPException(status_code=404, detail=f"Cannot find patient with id {patinet_id}")

@app.delete("/clean")
def clean_db(db: Session = Depends(get_db)):
    crud.delete_all(db)
    return {"message": "Whole list has deleted."}