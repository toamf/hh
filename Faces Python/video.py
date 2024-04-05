import pickle
import cv2
import face_recognition


def load_face_encodings(file_path):
    try:
        with open(file_path, "rb") as file:
            data = pickle.load(file)
            return data["name"], data["encodings"]
    except Exception as e:
        print(f"Error loading face encodings from {file_path}: {e}")
        return None, None


def detect_person_in_video(model="hog", new_width=None, new_height=None):
    name, known_encodings = load_face_encodings("aa_encodings.pickle")
    if known_encodings is None:
        return

    video = cv2.VideoCapture(0)
    if not video.isOpened():
        print("Error opening video stream.")
        return

    if new_width and new_height:
        video.set(cv2.CAP_PROP_FRAME_WIDTH, new_width)
        video.set(cv2.CAP_PROP_FRAME_HEIGHT, new_height)

    frame_skip = 1  # Обработка каждого N кадра
    frame_count = 1

    while True:
        ret, image = video.read()
        if not ret:
            print("Error reading video frame.")
            break

        frame_count += 1
        if frame_count % frame_skip != 0:
            continue  # Пропускаем кадры для уменьшения нагрузки

        small_frame = cv2.resize(image, (0, 0), fx=0.5, fy=0.5)
        locations = face_recognition.face_locations(small_frame, model=model)
        encodings = face_recognition.face_encodings(small_frame, locations)

        for face_encoding, face_location in zip(encodings, locations):
            matches = face_recognition.compare_faces(known_encodings, face_encoding)
            match_name = name if True in matches else "Unknown"

            top, right, bottom, left = [coordinate * 2 for coordinate in face_location]
            cv2.rectangle(image, (left, top), (right, bottom), (0, 255, 0), 2)
            cv2.putText(image, match_name, (left + 10, bottom + 20), cv2.FONT_HERSHEY_SIMPLEX, 1, (255, 255, 255), 2)

        cv2.imshow("Video", image)
        if cv2.waitKey(1) & 0xFF == ord('q'):
            break

    video.release()
    cv2.destroyAllWindows()


if __name__ == '__main__':
    detect_person_in_video(new_width=640, new_height=480)  # Установите желаемое разрешение
