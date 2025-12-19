from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from app.core.database import engine, Base
from app.api.v1.endpoints import patients,physiotherapists

# Veritabanı tablolarını oluştur
Base.metadata.create_all(bind=engine)

app = FastAPI(title="FizyoQ API")

# CORS Ayarları (Frontend bağlantısı için kritik)
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],  # Geliştirme aşamasında her yerden erişime izin ver
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# Router'ları ana uygulamaya dahil et
app.include_router(patients.router, prefix="/patients", tags=["patients"])
app.include_router(physiotherapists.router, prefix="/physiotherapists", tags=["physiotherapists"])

@app.get("/")
def root():
    return {"message": "FizyoQ API Çalışıyor"}