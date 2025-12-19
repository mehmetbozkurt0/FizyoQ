from sqlalchemy.orm import Session
from app.models import patient as models
from app.schemas import patient as schemas

def get_patients(db: Session, date: str):
    return db.query(models.PatientDB).filter(models.PatientDB.date == date).all()

def create_patient(db: Session, patient: schemas.PatientCreate):
    db_patient = models.PatientDB(
        full_name=patient.full_name,
        physiotherapist=patient.physiotherapist,
        reservation_time=patient.reservation_time,
        date = patient.date,
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

def delete_patient (db: Session, patient_id: int):
    db.query(models.PatientDB).filter(models.PatientDB.id == patient_id).delete()
    db.commit()

def update_patient(db: Session, patient_id: int, patient_data: schemas.PatientUpdate):
    db_patient = db.query(models.PatientDB).filter(models.PatientDB.id == patient_id).first()
    if db_patient:
        db_patient.full_name = patient_data.full_name
        db_patient.physiotherapist = patient_data.physiotherapist
        db_patient.reservation_time = patient_data.reservation_time
        db_patient.date = patient_data.date
        db_patient.status = patient_data.status
        db.commit()
        db.refresh(db_patient)
    return db_patient

def get_appointment_by_fzt(db:Session, fzt: str, time:str, date: str):
    return db.query(models.PatientDB).filter(
        models.PatientDB.physiotherapist == fzt,
        models.PatientDB.reservation_time == time,
        models.PatientDB.date == date
    ).first()
