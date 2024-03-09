# Черный космонавт похитил ... 
def find_best_bomb_placement(n, m, game_map):
    def count_enemies(x, y):
        count = 0
        directions = [(0, 1), (0, -1), (1, 0), (-1, 0)]  # Вправо, влево, вниз, вверх
        for dx, dy in directions:
            nx, ny = x, y
            while 0 <= nx < n and 0 <= ny < m:
                if game_map[nx][ny] == 'W':
                    break
                if game_map[nx][ny] == 'B':
                    count += 1
                nx += dx
                ny += dy
        return count

    best_count = 0
    best_position = (1, 1)  # Значение по умолчанию

    for i in range(n):
        for j in range(m):
            if game_map[i][j] == '.':
                enemies_destroyed = count_enemies(i, j)
                if enemies_destroyed > best_count:
                    best_count = enemies_destroyed
                    best_position = (i + 1, j + 1)

    return best_position


# Ввод данных с клавиатуры
n, m = map(int, input().split())
game_map = []
for _ in range(n):
    row = input()
    game_map.append(row)

best_position = find_best_bomb_placement(n, m, game_map)
print(best_position[0], best_position[1])
