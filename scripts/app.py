from fastapi import FastAPI
from pydantic import BaseModel
from scripts.recognize import recognize

app = FastAPI()

class RequestModel(BaseModel):
    imagePath: str
    topK: int = 5

@app.post("/recognize")
def recognize_api(request: RequestModel):
    try:
        results = recognize(request.imagePath, request.topK)
        return {
            "status": "success",
            "results": results
        }
    except Exception as e:
        return {
            "status": "error",
            "message": str(e)
        }