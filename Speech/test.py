import tkinter as tk  # Импортируем модуль для создания GUI
from tkinter import filedialog  # Импортируем необходимые компоненты из модуля tkinter
import os  # Импортируем модуль для работы с операционной системой
import whisper  # Импортируем модуль whisper для распознавания речи
import threading  # Импортируем модуль для работы с потоками
import librosa  # Импортируем модуль librosa для обработки аудио
import joblib  # Импортируем модуль joblib для загрузки моделей
from nltk.tokenize import sent_tokenize  # Импортируем функцию для токенизации текста на предложения

# Функция для загрузки моделей
def load_models():
    try:
        global whisper_model, classification_model, vectorizer, device
        loading_label.config(text="Загрузка моделей, пожалуйста, подождите...")

        # Загрузка модели Whisper для распознавания речи
        whisper_model = whisper.load_model("medium")

        # Загрузка модели для определения содержательности слов
        classification_model = joblib.load('logistic_regression_model.pkl')
        vectorizer = joblib.load('tfidf_vectorizer.pkl')

        loading_label.config(text="Модели загружены")
    except Exception as e:
        loading_label.config(text="Ошибка загрузки моделей")
        print(f"Ошибка при загрузке моделей: {str(e)}")

# Функция для выбора аудио файла
def choose_file():
    try:
        global file_path
        file_path = filedialog.askopenfilename(filetypes=[("Audio Files", "*.wav *.mp3 *.m4a")])
        if file_path:
            file_label.config(text=os.path.basename(file_path))
            analyze_button.config(state=tk.NORMAL)
        else:
            file_label.config(text="Файл не выбран")
            analyze_button.config(state=tk.DISABLED)
    except Exception as e:
        print(f"Ошибка при выборе файла: {str(e)}")

# Функция для анализа аудио файла
def analyze_audio():
    try:
        global stop_analysis
        stop_analysis = False

        if not file_path:
            loading_label.config(text="Ошибка: Файл не выбран")
            return

        analyze_button.config(state=tk.DISABLED)
        stop_button.grid(row=1, column=1, padx=10, pady=10)
        loading_label.config(text="Анализ аудио, пожалуйста, подождите...")

        def transcribe_and_analyze():
            try:
                # Загрузка аудио файла в формате WAV с помощью librosa
                audio_data, _ = librosa.load(file_path, sr=16000)

                # Настройка параметров модели для улучшения распознавания междометий
                options = {
                    "language": "en",
                    "verbose": True,
                    "temperature": 0.8,
                    "best_of": 5,
                    "logprob_threshold": -1.0,
                    "no_speech_threshold": 0.6,
                }

                # Распознавание речи с помощью Whisper
                result = whisper_model.transcribe(audio_data, **options)

                # Постпроцессинг результатов
                transcript = result["text"]

                # Обновление второго текстового поля для полного текста
                full_text.delete(1.0, tk.END)
                full_text.insert(tk.END, transcript)

                # Разделение текста на предложения
                sentences = sent_tokenize(transcript)

                content_sentences = []

                # Определение содержательности предложений и добавление содержательных предложений в список
                for sentence in sentences:
                    if stop_analysis:
                        break
                    if predict_text(sentence) == 'Содержательный':
                        content_sentences.append(sentence)

                if not stop_analysis:
                    content_text.delete(1.0, tk.END)
                    content_text.insert(tk.END, " ".join(content_sentences))

                    loading_label.config(text="Анализ завершен")
                else:
                    loading_label.config(text="Анализ остановлен")

                stop_button.grid_forget()
                analyze_button.config(state=tk.NORMAL)
            except Exception as e:
                loading_label.config(text="Ошибка анализа")
                print(f"Ошибка при анализе аудио: {str(e)}")
                stop_button.grid_forget()
                analyze_button.config(state=tk.NORMAL)

        # Запуск анализа в отдельном потоке
        threading.Thread(target=transcribe_and_analyze).start()

    except Exception as e:
        print(f"Ошибка при анализе аудио: {str(e)}")

# Функция для остановки анализа
def stop_analysis_func():
    global stop_analysis
    stop_analysis = True

# Функция для предсказания содержательности текста
def predict_text(text):
    text_tfidf = vectorizer.transform([text])
    prediction = classification_model.predict(text_tfidf)
    return 'Содержательный' if prediction[0] == 1 else 'Бессодержательный'

# Функция для копирования текста в буфер обмена
def copy_to_clipboard(text_widget):
    root.clipboard_clear()
    root.clipboard_append(text_widget.get(1.0, tk.END).strip())
    notification_label.config(text="Текст скопирован в буфер обмена")
    root.after(3000, clear_notification)  # Удаление уведомления через 3 секунды

def clear_notification():
    notification_label.config(text="")

# Инициализация главного окна
root = tk.Tk()
root.title("Анализатор Аудио")

# Конфигурация макета
frame = tk.Frame(root)
frame.pack(pady=20, padx=20)

# Кнопка для выбора аудио файла
choose_button = tk.Button(frame, text="Выбрать аудио файл", command=choose_file)
choose_button.grid(row=0, column=0, padx=10, pady=10)

# Метка для отображения выбранного файла
file_label = tk.Label(frame, text="Файл не выбран", width=40, anchor="w")
file_label.grid(row=0, column=1, padx=10, pady=10)

# Кнопка для начала анализа аудио файла
analyze_button = tk.Button(frame, text="Анализировать", command=analyze_audio, state=tk.DISABLED)
analyze_button.grid(row=1, column=0, padx=10, pady=10)

# Кнопка для остановки анализа
stop_button = tk.Button(frame, text="Остановить анализ", command=stop_analysis_func, state=tk.NORMAL)

# Метка для отображения состояния загрузки и анализа
loading_label = tk.Label(frame, text="", width=40, anchor="w")
loading_label.grid(row=2, column=0, columnspan=2, padx=10, pady=10)

# Метка для отображения уведомлений
notification_label = tk.Label(frame, text="", width=40, anchor="w")
notification_label.grid(row=3, column=0, columnspan=2, padx=10, pady=10)

# Поля для отображения результатов анализа
results_frame = tk.Frame(frame)
results_frame.grid(row=4, column=0, columnspan=2, pady=10, sticky="w")

# Поле для отображения содержательных предложений (слева)
content_frame = tk.Frame(results_frame)
content_frame.pack(side="left", padx=10)

content_label = tk.Label(content_frame, text="Содержательная часть:")
content_label.pack(anchor="w")

content_text = tk.Text(content_frame, width=40, height=20)
content_text.pack(padx=10, pady=10)

content_copy_button = tk.Button(content_frame, text="Копировать текст", command=lambda: copy_to_clipboard(content_text))
content_copy_button.pack(pady=5)

# Поле для отображения полного текста (справа)
full_text_frame = tk.Frame(results_frame)
full_text_frame.pack(side="right", padx=10)

full_text_label = tk.Label(full_text_frame, text="Полный текст:")
full_text_label.pack(anchor="w")

full_text = tk.Text(full_text_frame, width=40, height=20)
full_text.pack(padx=10, pady=10)

full_text_copy_button = tk.Button(full_text_frame, text="Копировать текст", command=lambda: copy_to_clipboard(full_text))
full_text_copy_button.pack(pady=5)

# Запуск загрузки моделей в отдельном потоке
threading.Thread(target=load_models).start()

# Запуск главного окна приложения
root.mainloop()
