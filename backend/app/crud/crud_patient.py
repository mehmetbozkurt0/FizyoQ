from sqlalchemy.orm import Session
from app.models import patient as models
from app.schemas import patient as schemas

def get_patients(db: Session):
    return db.query(models.PatientDB).order_by(models.PatientDB.reservation_time).all()

def create_patient(db: Session, patient: schemas.PatientCreate):
    db_patient = models.PatientDB(
        full_name=patient.full_name,
        physiotherapist=patient.physiotherapist,
        reservation_time=patient.reservation_time,
        status=patient.status
    )
    db.add(db_patient)
    db.commit()
    db.refresh(db_patient)
    return db_patient

def update_status(db: Session, patient_id: int, new_status: str):
    patient = db.query(models.PatientDB).filter(models.PatientDB.id == patient_id).first()
    if patient:
        patient.status = new_status
        db.commit()
        db.refresh(patient)
    return patient

def delete_all(db: Session):
    db.query(models.PatientDB).delete()
    db.commit()