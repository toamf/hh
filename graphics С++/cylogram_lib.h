#ifndef CYCLOGRAM_LIB_H
#define CYCLOGRAM_LIB_H

#include <vector>
#include <string>

// Структура Point представляет точку в 3D пространстве с координатами x, y и z.
struct Point {
    double x;  // Координата X
    double y;  // Координата Y
    double z;  // Координата Z
};

// Класс Cylogram предоставляет методы для работы с циклограммой,
// которая представляет собой последовательность точек (Point) в 3D пространстве.
class Cylogram {
public:
    // Конструктор по умолчанию
    Cylogram();

    // Метод загрузки циклограммы из файла
    // Параметры:
    // - filename: имя файла (std::string)
    // - binary: если true, файл читается как бинарный, иначе как текстовый (по умолчанию false)
    // Возвращает: true, если загрузка успешна, false в противном случае
    bool loadFromFile(const std::string& filename, bool binary = false);

    // Метод сохранения циклограммы в файл
    // Параметры:
    // - filename: имя файла (std::string)
    // - binary: если true, файл сохраняется как бинарный, иначе как текстовый (по умолчанию false)
    // Возвращает: true, если сохранение успешно, false в противном случае
    bool saveToFile(const std::string& filename, bool binary = false);

    // Метод добавления новой точки в циклограмму
    // Параметры: объект Point, представляющий точку
    void addPoint(const Point& point);

    // Метод редактирования точки в циклограмме
    // Параметры: индекс редактируемой точки и объект Point с новыми значениями
    void editPoint(size_t index, const Point& point);

    // Метод удаления точки из циклограммы
    // Параметры: индекс удаляемой точки
    void removePoint(size_t index);

    // Метод получения всех точек циклограммы
    // Возвращает: вектор объектов Point
    std::vector<Point> getPoints() const;

private:
    // Вектор для хранения точек циклограммы
    std::vector<Point> points;
};

#endif // CYCLOGRAM_LIB_H
