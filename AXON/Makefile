# Сборка Docker-образа
build:
	docker build -t app .

# Запуск на CPU
run-cpu:
	docker run --rm -e DEVICE=cpu -e HOST=0.0.0.0 -e PORT=8000 -p 8000:8000 app

# Запуск на GPU
run-gpu:
	docker run --rm --gpus all -e DEVICE=cuda -e HOST=0.0.0.0 -e PORT=8000 -p 8000:8000 app
