from sqlalchemy.orm import Session
from app.models.physiotherapist import PhysiotherapistDB
from app.schemas.physiotherapist import PhysiotherapistCreate


def get_physiotherapists(db:Session):
    return db.query(PhysiotherapistDB).all()

def create_physiotherapist(db: Session, fzt: PhysiotherapistCreate):
    db_fzt = PhysiotherapistDB(name=fzt.name)
    db.add(db_fzt)
    db.commit()
    db.refresh(db_fzt)
    return db_fzt

def delete_physiotherapist(db:Session, fzt_id: int):
    db.query(PhysiotherapistDB).filter(PhysiotherapistDB.id == fzt_id).delete()
    db.commit()