#include "cylogram_lib.h"
#include <iostream>
#include <fstream>
#include <limits>

// Функция отображения меню
void displayMenu() {
    std::cout << "Меню:\n";
    std::cout << "1. Загрузить циклограмму из текстового файла\n";
    std::cout << "2. Сохранить циклограмму в текстовый файл\n";
    std::cout << "3. Загрузить циклограмму из бинарного файла\n";
    std::cout << "4. Сохранить циклограмму в бинарный файл\n";
    std::cout << "5. Добавить точку\n";
    std::cout << "6. Редактировать точку\n";
    std::cout << "7. Удалить точку\n";
    std::cout << "8. Визуализировать циклограмму\n";
    std::cout << "0. Выход\n";
}

// Функция визуализации циклограммы и сохранения графика в формате SVG
void visualizeCylogram(const Cylogram& cylogram) {
    std::ofstream svgFile("cylogram.svg");

    if (!svgFile.is_open()) {
        std::cerr << "Ошибка при открытии файла для записи SVG.\n";
        return;
    }

    const double scale = 10.0;

    // Начало SVG документа
    svgFile << "<svg height=\"500\" width=\"500\" xmlns=\"http://www.w3.org/2000/svg\">\n";

    // Оси координат
    svgFile << "<line x1=\"0\" y1=\"250\" x2=\"500\" y2=\"250\" stroke=\"black\" />\n"; // X-ось
    svgFile << "<line x1=\"250\" y1=\"0\" x2=\"250\" y2=\"500\" stroke=\"black\" />\n"; // Y-ось
    svgFile << "<line x1=\"500\" y1=\"0\" x2=\"0\" y2=\"500\" stroke=\"black\" stroke-dasharray=\"2,2\" />\n"; // Z-ось (мелкий пунктир)

    // Подписи осей
    svgFile << "<text x=\"490\" y=\"260\" font-family=\"Verdana\" font-size=\"12\" fill=\"black\">X</text>\n";
    svgFile << "<text x=\"260\" y=\"10\" font-family=\"Verdana\" font-size=\"12\" fill=\"black\">Y</text>\n";
    svgFile << "<text x=\"10\" y=\"490\" font-family=\"Verdana\" font-size=\"12\" fill=\"black\">Z</text>\n";

    // Разметка по оси X
    for (int i = 0; i <= 500; i += 100) {
        int value = static_cast<int>((i - 250) / scale);
        svgFile << "<line x1=\"" << i << "\" y1=\"245\" x2=\"" << i << "\" y2=\"255\" stroke=\"black\" />\n";
        svgFile << "<text x=\"" << i << "\" y=\"265\" font-family=\"Verdana\" font-size=\"10\" fill=\"black\">" << value << "</text>\n";
    }

    // Разметка по оси Y
    for (int i = 0; i <= 500; i += 100) {
        int value = static_cast<int>((250 - i) / scale);
        svgFile << "<line x1=\"245\" y1=\"" << i << "\" x2=\"255\" y2=\"" << i << "\" stroke=\"black\" />\n";
        svgFile << "<text x=\"260\" y=\"" << i << "\" font-family=\"Verdana\" font-size=\"10\" fill=\"black\">" << value << "</text>\n";
    }

    // Разметка по оси Z
    for (int i = 0; i <= 500; i += 100) {
        int value = static_cast<int>((250 - i) / scale);
        svgFile << "<line x1=\"" << (500 - i) << "\" y1=\"" << i << "\" x2=\"" << (490 - i) << "\" y2=\"" << (i + 10) << "\" stroke=\"black\" />\n";
        svgFile << "<text x=\"" << (500 - i) << "\" y=\"" << (i + 20) << "\" font-family=\"Verdana\" font-size=\"10\" fill=\"black\">" << value << "</text>\n";
    }

    // Сетка
    for (int i = 0; i <= 500; i += 100) {
        svgFile << "<line x1=\"" << i << "\" y1=\"0\" x2=\"" << i << "\" y2=\"500\" stroke=\"lightgray\" stroke-dasharray=\"2\" />\n";
        svgFile << "<line x1=\"0\" y1=\"" << i << "\" x2=\"500\" y2=\"" << i << "\" stroke=\"lightgray\" stroke-dasharray=\"2\" />\n";
    }

    // Отображение точек и их координат
    const auto& points = cylogram.getPoints();
    for (size_t i = 0; i < points.size(); ++i) {
        const auto& point = points[i];
        double x = 250 + point.x * scale; // Масштабирование и сдвиг к центру
        double y = 250 - point.y * scale; // Масштабирование и инвертирование оси Y

        svgFile << "<circle cx=\"" << x << "\" cy=\"" << y << "\" r=\"3\" fill=\"blue\" />\n";
        svgFile << "<text x=\"" << (x + 5) << "\" y=\"" << (y - 5) << "\" font-family=\"Verdana\" font-size=\"10\" fill=\"black\">"
                << (i + 1) << ": (" << point.x << "," << point.y << "," << point.z << ")</text>\n";
    }

    // Завершение SVG документа
    svgFile << "</svg>\n";
    svgFile.close();
    std::cout << "Циклограмма визуализирована и сохранена в файл cylogram.svg\n";
}

void displayPoints(const Cylogram& cylogram) {
    const auto& points = cylogram.getPoints();
    if (points.empty()) {
        std::cout << "Циклограмма пуста.\n";
    } else {
        std::cout << "Текущие точки циклограммы:\n";
        for (size_t i = 0; i < points.size(); ++i) {
            const auto& point = points[i];
            std::cout << i + 1 << ": (" << point.x << ", " << point.y << ", " << point.z << ")\n";
        }
    }
}

int main() {
    Cylogram cylogram;
    int choice;

    // Основной цикл программы
    do {
        displayMenu();
        while (!(std::cin >> choice)) {
            std::cin.clear(); // Очистка флага ошибки ввода
            std::cin.ignore(std::numeric_limits<std::streamsize>::max(), '\n'); // Игнорирование некорректного ввода
            std::cout << "Некорректный ввод. Пожалуйста, введите номер действия из меню.\n";
            displayMenu();
        }

        switch (choice) {
            case 1: {
                // Загрузка циклограммы из текстового файла
                std::string filename;
                std::cout << "Введите имя текстового файла для загрузки: ";
                std::cin >> filename;
                if (cylogram.loadFromFile(filename)) {
                    std::cout << "Циклограмма успешно загружена.\n";
                } else {
                    std::cerr << "Ошибка при загрузке циклограммы. Проверьте имя файла и его содержимое.\n";
                }
                break;
            }
            case 2: {
                // Сохранение циклограммы в текстовый файл
                std::string filename;
                std::cout << "Введите имя текстового файла для сохранения: ";
                std::cin >> filename;
                if (cylogram.saveToFile(filename)) {
                    std::cout << "Циклограмма успешно сохранена.\n";
                } else {
                    std::cerr << "Ошибка при сохранении циклограммы. Проверьте доступность файла для записи.\n";
                }
                break;
            }
            case 3: {
                // Загрузка циклограммы из бинарного файла
                std::string filename;
                std::cout << "Введите имя бинарного файла для загрузки: ";
                std::cin >> filename;
                if (cylogram.loadFromFile(filename, true)) {
                    std::cout << "Циклограмма успешно загружена.\n";
                } else {
                    std::cerr << "Ошибка при загрузке циклограммы. Проверьте имя файла и его содержимое.\n";
                }
                break;
            }
            case 4: {
                // Сохранение циклограммы в бинарный файл
                std::string filename;
                std::cout << "Введите имя бинарного файла для сохранения: ";
                std::cin >> filename;
                if (cylogram.saveToFile(filename, true)) {
                    std::cout << "Циклограмма успешно сохранена.\n";
                } else {
                    std::cerr << "Ошибка при сохранении циклограммы. Проверьте доступность файла для записи.\n";
                }
                break;
            }
            case 5: {
                // Добавление новой точки
                displayPoints(cylogram);
                Point point;
                std::cout << "Введите координаты точки (x y z): ";
                if (!(std::cin >> point.x >> point.y >> point.z)) {
                    std::cin.clear(); // Очистка флага ошибки ввода
                    std::cin.ignore(std::numeric_limits<std::streamsize>::max(), '\n'); // Игнорирование некорректного ввода
                    std::cerr << "Некорректный ввод координат. Пожалуйста, введите три числа через пробел.\n";
                } else {
                    cylogram.addPoint(point);
                    std::cout << "Точка добавлена.\n";
                }
                break;
            }
            case 6: {
                // Редактирование существующей точки
                displayPoints(cylogram);
                size_t index;
                Point point;
                std::cout << "Введите индекс точки для редактирования (начиная с 1): ";
                if (!(std::cin >> index) || index == 0 || index > cylogram.getPoints().size()) {
                    std::cin.clear(); // Очистка флага ошибки ввода
                    std::cin.ignore(std::numeric_limits<std::streamsize>::max(), '\n'); // Игнорирование некорректного ввода
                    std::cerr << "Некорректный индекс. Пожалуйста, введите корректный индекс существующей точки.\n";
                } else {
                    std::cout << "Введите новые координаты точки (x y z): ";
                    if (!(std::cin >> point.x >> point.y >> point.z)) {
                        std::cin.clear(); // Очистка флага ошибки ввода
                        std::cin.ignore(std::numeric_limits<std::streamsize>::max(), '\n'); // Игнорирование некорректного ввода
                        std::cerr << "Некорректный ввод координат. Пожалуйста, введите три числа через пробел.\n";
                    } else {
                        cylogram.editPoint(index - 1, point); // Вычитание 1 для преобразования в индекс с нуля
                        std::cout << "Точка отредактирована.\n";
                    }
                }
                break;
            }
            case 7: {
                // Удаление точки
                displayPoints(cylogram);
                size_t index;
                std::cout << "Введите индекс точки для удаления (начиная с 1): ";
                if (!(std::cin >> index) || index == 0 || index > cylogram.getPoints().size()) {
                    std::cin.clear(); // Очистка флага ошибки ввода
                    std::cin.ignore(std::numeric_limits<std::streamsize>::max(), '\n'); // Игнорирование некорректного ввода
                    std::cerr << "Некорректный индекс. Пожалуйста, введите корректный индекс существующей точки.\n";
                } else {
                    cylogram.removePoint(index - 1); // Вычитание 1 для преобразования в индекс с нуля
                    std::cout << "Точка удалена.\n";
                }
                break;
            }
            case 8: {
                // Визуализация циклограммы
                visualizeCylogram(cylogram);
                break;
            }
            case 0:
                std::cout << "Выход из программы.\n";
                break;
            default:
                std::cerr << "Некорректный выбор. Пожалуйста, выберите действие из меню.\n";
                break;
        }
    } while (choice != 0);

    return 0;
}
