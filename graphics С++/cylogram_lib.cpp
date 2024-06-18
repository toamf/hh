#include "cylogram_lib.h"
#include <fstream>
#include <sstream>
#include <iostream>

// Конструктор по умолчанию
Cylogram::Cylogram() {}

// Загрузка циклограммы из файла (текстового или бинарного)
bool Cylogram::loadFromFile(const std::string& filename, bool binary) {
    if (binary) {
        // Открытие бинарного файла для чтения
        std::ifstream file(filename, std::ios::binary);
        if (!file.is_open()) return false;

        points.clear();
        Point point;
        // Чтение точек из бинарного файла
        while (file.read(reinterpret_cast<char*>(&point), sizeof(Point))) {
            points.push_back(point);
        }

        file.close();
        return true;
    } else {
        // Открытие текстового файла для чтения
        std::ifstream file(filename);
        if (!file.is_open()) return false;

        points.clear();
        std::string line;
        // Чтение файла построчно
        while (std::getline(file, line)) {
            std::istringstream iss(line);
            Point point;
            // Чтение координат точки из строки
            if (iss >> point.x >> point.y >> point.z) {
                points.push_back(point);
            }
        }

        file.close();
        return true;
    }
}

// Сохранение циклограммы в файл (текстовый или бинарный)
bool Cylogram::saveToFile(const std::string& filename, bool binary) {
    if (binary) {
        // Открытие бинарного файла для записи
        std::ofstream file(filename, std::ios::binary);
        if (!file.is_open()) return false;

        // Запись точек в бинарный файл
        for (const auto& point : points) {
            file.write(reinterpret_cast<const char*>(&point), sizeof(Point));
        }

        file.close();
        return true;
    } else {
        // Открытие текстового файла для записи
        std::ofstream file(filename);
        if (!file.is_open()) return false;

        // Запись каждой точки в файл
        for (const auto& point : points) {
            file << point.x << " " << point.y << " " << point.z << "\n";
        }

        file.close();
        return true;
    }
}

// Добавление новой точки в циклограмму
// Параметры: объект Point, представляющий точку
void Cylogram::addPoint(const Point& point) {
    points.push_back(point);
}

// Редактирование точки в циклограмме
// Параметры: индекс редактируемой точки и объект Point с новыми значениями
void Cylogram::editPoint(size_t index, const Point& point) {
    if (index < points.size()) {
        points[index] = point;
    }
}

// Удаление точки из циклограммы
// Параметры: индекс удаляемой точки
void Cylogram::removePoint(size_t index) {
    if (index < points.size()) {
        points.erase(points.begin() + index);
    }
}

// Получение всех точек циклограммы
// Возвращает: вектор объектов Point
std::vector<Point> Cylogram::getPoints() const {
    return points;
}
