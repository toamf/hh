# Однажды Адлет ...
def find_next_happy_number(number):
    # Функция для проверки, является ли билет счастливым
    def is_happy(n):
        s = str(n)
        return sum(map(int, s[:3])) == sum(map(int, s[3:]))

    # Начинаем поиск со следующего числа
    number += 1
    while not is_happy(number):
        number += 1
    return number


# Чтение числа с клавиатуры
number = int(input())
print(find_next_happy_number(number))
