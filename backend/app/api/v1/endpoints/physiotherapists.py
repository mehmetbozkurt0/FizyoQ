from typing import List
from fastapi import APIRouter, Depends
from sqlalchemy.orm import Session
from app.schemas import physiotherapist as schemas
from .patients import get_db
from app.crud import crud_therapist

router = APIRouter()

@router.get("/", response_model=List[schemas.Physiotherapist])
def read_therapists(db:Session = Depends(get_db)):
    return crud_therapist.get_physiotherapists(db)

@router.post("/", response_model=schemas.PhysiotherapistCreate)
def create_therapist(fzt: schemas.PhysiotherapistCreate, db:Session = Depends(get_db)):
    return crud_therapist.create_physiotherapist(db,fzt)

@router.delete("/{fzt_id}")
def delete_therapist(fzt_id: int, db:Session = Depends(get_db)):
    crud_therapist.delete_physiotherapist(db,fzt_id)
    return {"message": "Fzt. Silindi"}