from pydantic import BaseModel

class PhysiotherapistBase(BaseModel):
    name:str

class PhysiotherapistCreate(PhysiotherapistBase):
    pass

class Physiotherapist(PhysiotherapistBase):
    id: int
    class Config:
        from_attributes = True