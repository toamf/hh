#### Telegram: [@toamf](https://t.me/toamf)




# Object Detection API

## Описание
Это REST API для обнаружения объектов на изображениях с использованием модели YOLO. API принимает изображение и возвращает изображение с наложенной маской и массив с координатами обнаруженных объектов (bounding boxes, bbox).

## Требования
- Python 3.10+
- Docker (для запуска в контейнере)
- Система Linux (разработано для Ubuntu)

## Установка и запуск

### Локальный запуск

1. Клонируйте репозиторий:
    ```bash
    git clone <URL репозитория>
    cd <название директории>
    ```

2. Создайте виртуальное окружение и установите зависимости:
    ```bash
    python3 -m venv venv
    source venv/bin/activate
    pip install -r requirements.txt
    ```

3. Запустите приложение:
    ```bash
    uvicorn app:app --host 0.0.0.0 --port 8000
    ```

Теперь приложение доступно по адресу `http://localhost:8000`.

### Запуск с использованием Docker

1. Постройте Docker-образ:
    ```bash
    docker build -t app .
    ```

2. Запустите контейнер на CPU/CUDA:
    ```bash
    docker run --rm -e DEVICE=cpu -e HOST=0.0.0.0 -e PORT=8000 -p 8000:8000 app
    ```

Приложение будет доступно по адресу `http://localhost:8000`.

## Использование API

- ### Endpoint для предсказания:
    **POST** `/predict/`

    **Параметры**: `image_base64` (Base64-кодированное изображение)

    **Пример запроса**:
    ```json
    {
      "image_base64": "<Base64-код изображения>"
    }
    ```

    **Пример ответа**:
    ```json
    {
      "filename": "image.jpg",
      "image_base64": "<Base64-код изображения с bbox>",
      "bboxes": [
        {"x_min": 50, "y_min": 60, "x_max": 200, "y_max": 250}
      ]
    }
    ```

## Системные требования
- Python 3.10+
- Операционная система: Ubuntu (рекомендуемая версия 20.04)

