import os
import sys
import face_recognition
import pickle
import cv2


def train_model_by_img(name):
    # Проверяем наличие директории "faces"
    if not os.path.exists("faces"):
        print("ERROR: Directory 'faces' not found.")
        sys.exit()

    known_encodings = []
    images = os.listdir("faces")

    for i, image in enumerate(images):
        try:
            print(f"[+] Processing image {i + 1}/{len(images)}: {image}")
            # Загружаем изображение с использованием библиотеки face_recognition
            face_img = face_recognition.load_image_file(f"faces/{image}")
            # Извлекаем кодировку лица с изображения
            face_encodings = face_recognition.face_encodings(face_img)

            if face_encodings:
                known_encodings.append(face_encodings[0])
            else:
                print(f"No faces found in image {image}")

        except Exception as e:
            print(f"Error processing image {image}: {e}")

    if known_encodings:
        # Сохраняем данные в файле pickle
        data = {"name": name, "encodings": known_encodings}
        with open(f"{name}_encodings.pickle", "wb") as file:
            file.write(pickle.dumps(data))
        print(f"[INFO] File {name}_encodings.pickle successfully created")
    else:
        print("No face encodings were found.")


def take_screenshot_from_video(output_dir="dataset"):
    cap = cv2.VideoCapture(0)
    count = 0
    frame_skip = 1  # Обработка каждого N кадра
    frame_count = 0

    if not os.path.exists(output_dir):
        os.mkdir(output_dir)

    while True:
        ret, frame = cap.read()
        if not ret:
            print("Error: Can't get the frame.")
            break

        frame_count += 1
        if frame_count % frame_skip != 0:
            continue  # Пропускаем кадры для уменьшения нагрузки

        cv2.imshow("Video", frame)
        k = cv2.waitKey(20)

        if k == ord(" "):
            screenshot_path = os.path.join(output_dir, f"{count}_scr.jpg")
            cv2.imwrite(screenshot_path, frame)
            print(f"Screenshot saved: {screenshot_path}")
            count += 1
        elif k == ord("q"):
            print("Quitting...")
            break

    cap.release()
    cv2.destroyAllWindows()


if __name__ == '__main__':
    # Пример использования функции train_model_by_img
    train_model_by_img("aa")

    # Пример использования функции take_screenshot_from_video
    # take_screenshot_from_video()
