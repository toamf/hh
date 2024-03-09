# В приложении для звонков ... 
def update_recent_calls(m, calls):
    recent_calls = []  # Список для хранения недавних номеров
    for call in calls:
        if call in recent_calls:
            # Если номер уже в списке, перемещаем его в начало
            recent_calls.remove(call)
            recent_calls.insert(0, call)
        else:
            # Если номера нет в списке
            if len(recent_calls) < m:
                # Если в списке есть место, просто добавляем в начало
                recent_calls.insert(0, call)
            else:
                # Если список полон, удаляем последний номер и добавляем новый в начало
                recent_calls.pop()
                recent_calls.insert(0, call)
    # Если в списке меньше m номеров, заполняем оставшиеся позиции "Empty"
    while len(recent_calls) < m:
        recent_calls.append("<Empty>")
    return recent_calls


# Чтение входных данных с клавиатуры
m, n = map(int, input().split())
calls = []
for _ in range(n):
    call = input()
    calls.append(call)

# Обновление и вывод списка недавних номеров
recent_calls = update_recent_calls(m, calls)
for call in recent_calls:
    print(call)
