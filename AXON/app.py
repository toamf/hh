import io
import base64
import os
from fastapi import FastAPI, HTTPException, Request
from fastapi.responses import HTMLResponse
from fastapi.templating import Jinja2Templates
from ultralytics import YOLO
from PIL import Image
import cv2
import torch

app = FastAPI()

# Настройка шаблонов
templates = Jinja2Templates(directory="templates")

# Установка параметров из переменных окружения
device = os.getenv("DEVICE", "cuda")
host = os.getenv("HOST", "127.0.0.1")
port = int(os.getenv("PORT", 8000))

# Проверка доступности устройства
if device == "cuda" and not torch.cuda.is_available():
    device = "cpu"
elif device == "mps" and not torch.backends.mps.is_available():
    device = "cpu"

# Загрузка модели YOLO
model = YOLO("models/best.pt")
model.to(device)

@app.get("/", response_class=HTMLResponse)
async def main(request: Request):
    return templates.TemplateResponse("index.html", {"request": request})

@app.post("/predict/")
async def predict(request: Request):
    data = await request.json()

    # Декодирование изображения из base64
    try:
        image_data = base64.b64decode(data["image_base64"])
        image = Image.open(io.BytesIO(image_data)).convert("RGB")
    except Exception:
        raise HTTPException(status_code=400, detail="Invalid image data.")

    # Получение предсказания
    results = model(image)
    predicted_image = results[0].plot()

    # Создание полупрозрачной маски для выделения объектов
    overlay = predicted_image.copy()

    # Перебор всех ограничительных рамок и закрашивание областей
    bboxes = []
    for box in results[0].boxes.xyxy.cpu().numpy():
        x_min, y_min, x_max, y_max = map(int, box)
        bboxes.append({"x_min": float(x_min), "y_min": float(y_min), "x_max": float(x_max), "y_max": float(y_max)})
        cv2.rectangle(overlay, (x_min, y_min), (x_max, y_max), (255, 153, 204), -1)

    # Добавление полупрозрачной маски поверх изображения
    alpha = 0.5
    cv2.addWeighted(overlay, alpha, predicted_image, 1 - alpha, 0, predicted_image)

    # Конвертация изображения с предсказанием в RGB
    predicted_image_rgb = cv2.cvtColor(predicted_image, cv2.COLOR_BGR2RGB)

    # Конвертация изображения с предсказанием в base64
    buffered = io.BytesIO()
    Image.fromarray(predicted_image_rgb).save(buffered, format="JPEG")
    img_str = base64.b64encode(buffered.getvalue()).decode("utf-8")

    # Формируем ответ с данными изображения и bbox
    return {"filename": data["filename"], "image_base64": img_str, "bboxes": bboxes}

# Запуск приложения с заданными параметрами
if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host=host, port=port)
