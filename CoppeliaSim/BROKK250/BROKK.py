from coppeliasim_zmqremoteapi_client import RemoteAPIClient
import math
import time
import random

# Инициализация клиента удаленного API
client = RemoteAPIClient()
sim = client.require('sim')

# Отключение пошагового режима симуляции
sim.setStepping(False)

print("Ожидание 5 секунд перед началом симуляции...")
time.sleep(5)

# Получение дескрипторов объектов
myObjectHandle = sim.getObjectHandle('povorotkabini')
secondObjectHandle = sim.getObjectHandle('podemstreli')
thirdObjectHandle = sim.getObjectHandle('podemstreli2')
frameHandle = sim.getObjectHandle('rama')

# Проверка на успешное получение дескрипторов
if myObjectHandle != -1 and secondObjectHandle != -1 and thirdObjectHandle != -1:
    sim.startSimulation()
    print("Симуляция началась...")

    # Поворот первого объекта (кабины)
    initial_orientation = sim.getObjectOrientation(myObjectHandle, sim.handle_world)
    target_angle = math.radians(2.79253)
    final_orientation_z = initial_orientation[2] + target_angle

    current_orientation_z = initial_orientation[2]
    rotation_speed = math.radians(0.0174533)

    start_time = sim.getSimulationTime()
    while abs(final_orientation_z - current_orientation_z) > 0.01:
        current_time = sim.getSimulationTime()
        time_delta = current_time - start_time
        rotation_angle = rotation_speed * time_delta
        if rotation_angle > target_angle:
            rotation_angle = target_angle
        current_orientation_z = initial_orientation[2] + rotation_angle
        if current_orientation_z > final_orientation_z:
            current_orientation_z = final_orientation_z
        sim.setObjectOrientation(myObjectHandle, sim.handle_world,
                                 [initial_orientation[0], initial_orientation[1], current_orientation_z])

        # Поворот второго объекта (стрелы)
        initial_orientation_second = sim.getObjectOrientation(secondObjectHandle, sim.handle_world)
        target_angle_second = math.radians(-0.0174533)
        final_orientation_z_second = initial_orientation_second[2] + target_angle_second

        current_orientation_z_second = initial_orientation_second[2]
        rotation_speed_second = math.radians(0.05)

        rotation_angle_second = rotation_speed_second * time_delta
        if rotation_angle_second > target_angle_second:
            rotation_angle_second = target_angle_second
        current_orientation_z_second = initial_orientation_second[2] + rotation_angle_second
        if current_orientation_z_second < final_orientation_z_second:
            current_orientation_z_second = final_orientation_z_second
        sim.setObjectOrientation(secondObjectHandle, sim.handle_world,
                                 [initial_orientation_second[0], initial_orientation_second[1],
                                  current_orientation_z_second])

        if current_orientation_z >= final_orientation_z:
            break

        time.sleep(0.02)

    print("Первый поворот завершен. Начинаем второй поворот...")

    # Поворот второго объекта (подъем или опускание стрелы)
    initial_orientation_second = sim.getObjectOrientation(secondObjectHandle, sim.handle_world)
    target_angle_second = math.radians(15)  # Целевой угол в радианах для подъема/опускания
    # Изменяем ориентацию вокруг оси Z
    final_orientation_z_second = initial_orientation_second[2] + target_angle_second

    current_orientation_z_second = initial_orientation_second[2]
    rotation_speed_second = math.radians(0.5)  # Скорость поворота в радианах в секунду для второго объекта

    start_time_second = sim.getSimulationTime()
    while abs(final_orientation_z_second - current_orientation_z_second) > 0.01:
        current_time_second = sim.getSimulationTime()
        time_delta_second = current_time_second - start_time_second
        rotation_angle_second = rotation_speed_second * time_delta_second
        if rotation_angle_second > target_angle_second:
            rotation_angle_second = target_angle_second
        current_orientation_z_second = initial_orientation_second[2] + rotation_angle_second
        if current_orientation_z_second > final_orientation_z_second:
            current_orientation_z_second = final_orientation_z_second
        sim.setObjectOrientation(secondObjectHandle, sim.handle_world,
                                 [initial_orientation_second[0], initial_orientation_second[1],
                                  current_orientation_z_second])

        if current_orientation_z_second >= final_orientation_z_second:
            break

        time.sleep(0.02)

    print("Второй поворот завершен. Начинаем третий поворот...")

    print("Начинаем третий поворот и имитацию взаимодействия бура с землей...")

    # Третий объект (подъем или опускание стрелы)
    initial_orientation_third = sim.getObjectOrientation(thirdObjectHandle, sim.handle_world)
    target_angle_third = math.radians(10)  # Целевой угол для подъема/опускания

    # Ориентация рамы для имитации взаимодействия
    initial_orientation_frame = sim.getObjectOrientation(frameHandle, sim.handle_world)
    tilt_angle = math.radians(1)  # Наклон на 3 градуса
    lift_angle = math.radians(1)  # Приподнятие на 1 градус

    # Рассчитываем целевую ориентацию рамы
    target_orientation_frame = [
        initial_orientation_frame[0] + lift_angle,
        initial_orientation_frame[1] + tilt_angle,
        initial_orientation_frame[2]
    ]

    rotation_speed_third = math.radians(0.35)  # Скорость поворота третьего объекта
    change_rate_frame = math.radians(1)  # Скорость изменения ориентации рамы

    start_time_third = sim.getSimulationTime()
    progress_frame = 0
    while True:
        current_time_third = sim.getSimulationTime()
        time_delta_third = current_time_third - start_time_third

        # Поворот третьего объекта
        rotation_angle_third = rotation_speed_third * time_delta_third
        if rotation_angle_third > target_angle_third:
            rotation_angle_third = target_angle_third
        sim.setObjectOrientation(thirdObjectHandle, sim.handle_world,
                                 [initial_orientation_third[0], initial_orientation_third[1],
                                  initial_orientation_third[2] + rotation_angle_third])

        # Изменение ориентации рамы
        if progress_frame < 1:
            progress_frame = change_rate_frame * time_delta_third
            if progress_frame > 1:
                progress_frame = 1
            current_orientation_frame = [
                initial_orientation_frame[0] + (
                            target_orientation_frame[0] - initial_orientation_frame[0]) * progress_frame,
                initial_orientation_frame[1] + (
                            target_orientation_frame[1] - initial_orientation_frame[1]) * progress_frame,
                initial_orientation_frame[2]
            ]
            sim.setObjectOrientation(frameHandle, sim.handle_world, current_orientation_frame)

        if rotation_angle_third == target_angle_third and progress_frame == 1:
            break

        time.sleep(0.02)

    print("Третий поворот и имитация взаимодействия бура с землей завершены.")

    print("Начало вибрации рамы и плавного возвращения...")

    start_time = sim.getSimulationTime()
    duration_of_vibration = 20.0  # Продолжительность вибрации в секундах
    return_duration = 10.0  # Время возврата в секундах
    intensity_of_vibration = math.radians(0.009)  # Интенсивность вибрации

    while sim.getSimulationTime() < start_time + max(duration_of_vibration, return_duration):
        current_time = sim.getSimulationTime()
        vibration_time_elapsed = current_time - start_time
        return_time_elapsed = current_time - start_time

        # Применяем случайную вибрацию, если в пределах времени вибрации
        if vibration_time_elapsed <= duration_of_vibration:
            vibration_adjustment = random.uniform(-intensity_of_vibration, intensity_of_vibration)
        else:
            vibration_adjustment = 0  # Вибрация закончена

        # Получаем текущую ориентацию рамы
        current_orientation = sim.getObjectOrientation(frameHandle, sim.handle_world)

        if return_time_elapsed <= return_duration:
            # Рассчитываем прогресс плавного возвращения
            progress = return_time_elapsed / return_duration
            # Вычисляем новую ориентацию для плавного возвращения
            new_orientation = [
                initial_orientation_frame[0] + (current_orientation[0] - initial_orientation_frame[0]) * (
                            1 - progress) + vibration_adjustment,
                initial_orientation_frame[1] + (current_orientation[1] - initial_orientation_frame[1]) * (
                            1 - progress) + vibration_adjustment,
                initial_orientation_frame[2] + (current_orientation[2] - initial_orientation_frame[2]) * (1 - progress)
                # Применяем вибрацию только по X и Y
            ]
        else:
            # Если плавное возвращение завершено, продолжаем только вибрацию (если она еще актуальна)
            new_orientation = [
                current_orientation[0] + vibration_adjustment,
                current_orientation[1] + vibration_adjustment,
                current_orientation[2]
            ]

        # Устанавливаем новую ориентацию рамы
        sim.setObjectOrientation(frameHandle, sim.handle_world, new_orientation)

        # Уменьшаем задержку для более плавного обновления
        time.sleep(0.01)

    print("Вибрация и плавное возвращение завершены.")

    print("Бур погружен в землю. Начало подъема и разворота стрел...")
    # Разворот второго объекта (стрелы) в исходное положение
    start_orientation_second = sim.getObjectOrientation(secondObjectHandle, sim.handle_world)
    target_orientation_second = [start_orientation_second[0], start_orientation_second[1],
                                 initial_orientation_second[2]]

    return_duration_second = 50.0  # Время разворота в секундах
    start_time_return_second = sim.getSimulationTime()

    while sim.getSimulationTime() < start_time_return_second + return_duration_second:
        progress_second = (sim.getSimulationTime() - start_time_return_second) / return_duration_second
        # Вычисляем текущую ориентацию на основе прогресса разворота
        current_orientation_second = sim.getObjectOrientation(secondObjectHandle, sim.handle_world)
        new_orientation_second = [start_orientation_second[0] + (
                    target_orientation_second[0] - start_orientation_second[0]) * progress_second,
                                  start_orientation_second[1] + (
                                              target_orientation_second[1] - start_orientation_second[
                                          1]) * progress_second,
                                  start_orientation_second[2] + (
                                              target_orientation_second[2] - start_orientation_second[
                                          2]) * progress_second]
        sim.setObjectOrientation(secondObjectHandle, sim.handle_world, new_orientation_second)
        time.sleep(0.02)

    print("Разворот стрелы завершен.")

    # Плавный подъем третьего объекта (стрелы)
    start_orientation_third = sim.getObjectOrientation(thirdObjectHandle, sim.handle_world)
    target_orientation_third = [start_orientation_third[0], start_orientation_third[1],
                                start_orientation_third[2] - target_angle_third]

    return_duration_third = 50.0  # Время подъема в секундах
    start_time_return_third = sim.getSimulationTime()

    while sim.getSimulationTime() < start_time_return_third + return_duration_third:
        progress_third = (sim.getSimulationTime() - start_time_return_third) / return_duration_third
        # Вычисляем текущую ориентацию на основе прогресса подъема
        current_orientation_third = sim.getObjectOrientation(thirdObjectHandle, sim.handle_world)
        new_orientation_third = [
            start_orientation_third[0] + (target_orientation_third[0] - start_orientation_third[0]) * progress_third,
            start_orientation_third[1] + (target_orientation_third[1] - start_orientation_third[1]) * progress_third,
            start_orientation_third[2] + (target_orientation_third[2] - start_orientation_third[2]) * progress_third]
        sim.setObjectOrientation(thirdObjectHandle, sim.handle_world, new_orientation_third)
        time.sleep(0.02)

    print("Подъем стрелы завершен.")


    print("Симуляция завершена.")
    time.sleep(5)
    sim.stopSimulation()

else:
    print("Не удалось получить дескриптор для одного из объектов")
