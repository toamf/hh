#include <LiquidCrystal_I2C.h>
#include <Wire.h>

// Определение пинов
const int tmpSensorPin = A0;       // Пин для подключения датчика температуры TMP36
const int gasSensorPin = A1;       // Пин для подключения газового датчика
const int redLEDPin = 2;           // Пин для красного светодиода (сигнал тревоги)
const int greenLEDPin = 3;         // Пин для зеленого светодиода (нормальное состояние)
const int buttonSwitchPin = 4;     // Пин для кнопки переключения между датчиками
const int buttonPowerPin = 5;      // Пин для кнопки включения устройства
const int buzzerPin = 6;           // Пин для подключения пьезоизлучателя
const int tiltSensorPin = 7;       // Пин для подключения датчика наклона SW200D
const int trigPin = 8;             // Пин для подачи сигнала на датчик
const int echoPin = 9;             // Пин для приема сигнала от датчика

// Создаем объект LCD
LiquidCrystal_I2C lcd(0x27, 16, 2);

bool deviceOn = false;             // Состояние устройства (включено/выключено)
bool displayTemperature = true;    // Отображение температуры или газа
bool displayDistance = false;      // Отображение расстояния
bool tiltAlarm = false;            // Состояние тревоги от датчика наклона
bool displayGas = false; 		   // Отображение уровня газа

void setup() {
  pinMode(tmpSensorPin, INPUT);
  pinMode(gasSensorPin, INPUT);
  pinMode(redLEDPin, OUTPUT);
  pinMode(greenLEDPin, OUTPUT);
  pinMode(buzzerPin, OUTPUT);
  pinMode(buttonSwitchPin, INPUT_PULLUP);
  pinMode(buttonPowerPin, INPUT_PULLUP);
  pinMode(tiltSensorPin, INPUT_PULLUP);   // Подключение датчика наклона
  pinMode(trigPin, OUTPUT);               // Устанавливаем пины датчика HC-SR04
  pinMode(echoPin, INPUT);

  while (!deviceOn) {
    if (digitalRead(buttonPowerPin) == LOW) {
      deviceOn = true;                    // Включаем устройство
      break;
    }
  }

  if (deviceOn) {
    lcd.init();                           // Инициализируем LCD
    lcd.backlight();                      // Включаем подсветку
  }

  digitalWrite(redLEDPin, LOW);
  digitalWrite(greenLEDPin, LOW);         // Начальное состояние индикаторов выключено
  noTone(buzzerPin);                      // Выключаем звуковой сигнал
}

void loop() {
  if (!deviceOn) {
    // Если устройство выключено, выключаем все индикаторы и прекращаем выполнение кода
    digitalWrite(redLEDPin, LOW);
    digitalWrite(greenLEDPin, LOW);
    noTone(buzzerPin);
    return;
  }

  if (digitalRead(buttonSwitchPin) == LOW) {
    if (displayTemperature) {
      displayTemperature = false;
      displayGas = true; // Переключаемся на отображение газа
      displayDistance = false;
    } else if (displayGas) {
      displayTemperature = false;
      displayGas = false;
      displayDistance = true; // Переключаемся на отображение расстояния
    } else if (displayDistance) {
      displayTemperature = true; // Возвращаемся к отображению температуры
      displayGas = false;
      displayDistance = false;
    }
    delay(500); // Задержка для предотвращения дребезга
  }

  float temperature = readTemperature();
  int gasLevel = analogRead(gasSensorPin);
  float distance = readDistance();

  lcd.clear();
  if (displayTemperature) {
    lcd.print("Temp: ");
    lcd.print(temperature);
    lcd.print(" C");
  } else if (displayDistance) {
    lcd.print("Dist: ");
    lcd.print(distance);
    lcd.print(" cm");
  } else {
    lcd.print("Gas: ");
    lcd.print(gasLevel);
    lcd.print(" ");
  }

  // Проверяем состояние датчика наклона
  if (digitalRead(tiltSensorPin) == LOW) {
    tiltAlarm = true;
  } else {
    tiltAlarm = false;
  }

  // Условия для срабатывания тревоги
  if (temperature > 50 || temperature < 20 || gasLevel > 150 || tiltAlarm || distance < 100) { 
    digitalWrite(redLEDPin, HIGH);
    digitalWrite(greenLEDPin, LOW);
    tone(buzzerPin, 1000);               // Включаем звук
  } else {
    digitalWrite(redLEDPin, LOW);
    digitalWrite(greenLEDPin, HIGH);
    noTone(buzzerPin);                   // Выключаем звук
  }

  delay(1000);                           // Задержка между измерениями
}

// Преобразование напряжения в температуру
float readTemperature() {
  int sensorValue = analogRead(tmpSensorPin);
  float voltage = sensorValue * (5.0 / 1023.0);
  return (voltage - 0.5) * 100;          // Преобразование напряжения в температуру
}

// Функция для чтения расстояния с датчика HC-SR04
float readDistance() {
  digitalWrite(trigPin, LOW);
  delayMicroseconds(2);
  digitalWrite(trigPin, HIGH);
  delayMicroseconds(10);
  digitalWrite(trigPin, LOW);
  float duration = pulseIn(echoPin, HIGH);
  float distance = (duration * 0.0343) / 2;
  return distance;
}
