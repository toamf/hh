import requests
from bs4 import BeautifulSoup


def read_vulnerable_tags(filename):
    # Чтение уязвимых тегов из файла и возвращение их в виде списка
    with open(filename, 'r') as file:
        return [line.strip() for line in file.readlines()]


def read_vulnerable_attributes(filename):
    # Чтение уязвимых атрибутов из файла и возвращение их в виде списка кортежей
    with open(filename, 'r') as file:
        return [tuple(line.strip().split(':')) for line in file.readlines()]


def find_vulnerable_tags(soup, vulnerable_tags):
    # Поиск уязвимых тегов в HTML-разметке и возвращение их в виде списка кортежей
    vulnerabilities = []

    for tag in vulnerable_tags:
        for occurrence in soup.find_all(tag):
            vulnerabilities.append((tag, occurrence))

    return vulnerabilities


def find_vulnerable_attributes(soup, vulnerable_attributes):
    # Определение списка обработчиков событий
    event_handlers = [
        'onclick',
        'ondblclick',
        'onmousedown',
        'onmouseup',
        'onmouseover',
        'onmousemove',
        'onmouseout',
        'onkeypress',
        'onkeydown',
        'onkeyup',
        'onload',
        'onunload',
        'onfocus',
        'onblur',
        'onsubmit',
        'onreset',
        'onselect',
        'onchange',
    ]

    # Поиск уязвимых атрибутов в HTML-разметке и возвращение их в виде списка кортежей
    vulnerabilities = []

    for tag, attribute in vulnerable_attributes:
        for occurrence in soup.find_all(tag):
            if attribute:
                value = occurrence.get(attribute)
                if value:
                    vulnerabilities.append((tag, attribute, value))
            else:
                for event_handler in event_handlers:
                    value = occurrence.get(event_handler)
                    if value:
                        vulnerabilities.append((tag, event_handler, value))

    return vulnerabilities


def scan_url(url, vulnerable_tags, vulnerable_attributes):
    # Получение HTML-контента с указанного URL
    response = requests.get(url)
    if response.status_code == 200:
        # Разбор HTML-контента и поиск уязвимых тегов и атрибутов
        soup = BeautifulSoup(response.text, 'html.parser')
        tag_vulnerabilities = find_vulnerable_tags(soup, vulnerable_tags)
        attribute_vulnerabilities = find_vulnerable_attributes(soup, vulnerable_attributes)
        return tag_vulnerabilities, attribute_vulnerabilities
    else:
        return None, None


def main():
    # Чтение уязвимых тегов и атрибутов из файлов
    vulnerable_tags = read_vulnerable_tags('vulnerable_tags.txt')
    vulnerable_attributes = read_vulnerable_attributes('vulnerable_attributes.txt')

    # Вывод информации о возможных уязвимостях
    info = '''
    Список возможных уязвимостей.

    <script>: Возможны JavaScript-инъекции (Cross-Site Scripting, XSS).
    <iframe>: Возможны межсайтовые инъекции и нежелательное встраивание стороннего контента.
    <object> и <embed>: Возможны инъекции Flash, Java, и других объектов, что может привести к выполнению произвольного кода.
    <img> с атрибутом src: Может содержать вредоносный код или ведущую на вредоносный сайт ссылку.
    <a> с атрибутом href: Может содержать ссылку на вредоносный сайт или использоваться для межсайтовой подделки запросов (CSRF).
    <form> с атрибутами action и method: Неправильная настройка может привести к утечке данных или CSRF.
    <input> и <textarea>: Возможны инъекции данных, если не используется должным образом проверка и фильтрация вводимых пользователем данных.
    Атрибут style и тег <style>: Возможны инъекции CSS, что может привести к изменению внешнего вида страницы или к краже данных с помощью фишинга.
    Теги с атрибутами, содержащими обработчики событий, например onclick и onload: Могут содержать вредоносный JavaScript-код.
    '''

    print(info)

    # Запрос URL для сканирования от пользователя
    url = input("Введите URL для сканирования: ")

    # Сканирование URL на уязвимые теги и атрибуты
    tag_vulnerabilities, attribute_vulnerabilities = scan_url(url, vulnerable_tags, vulnerable_attributes)

    # Вывод найденных уязвимостей
    if tag_vulnerabilities or attribute_vulnerabilities:
        print("Найденные уязвимости:")
        for tag, occurrence in tag_vulnerabilities:
            print(f"Тег {tag}: {occurrence}")

        for tag, attribute, value in attribute_vulnerabilities:
            print(f"Атрибут {attribute} в теге {tag}: {value}")
    else:
        print("Уязвимости не найдены")


if __name__ == "__main__":
    main()
