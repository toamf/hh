import SwiftUI

// Определение перечисления для операций калькулятора
enum CalculatorOperation {
    case addition, subtraction, multiplication, division, squareRoot, percentage
    
    // Инициализатор для символьного представления операций
    init?(symbol: String) {
        switch symbol {
        case "+": self = .addition
        case "-": self = .subtraction
        case "×": self = .multiplication
        case "÷": self = .division
        case "√": self = .squareRoot
        case "%": self = .percentage
        default: return nil
        }
    }
}

struct ContentView: View {
    // Состояния для отображения, текущей операции, сохраненного значения, флага нового ввода и отображения кнопки "С"
    @State private var displayValue = "0"
    @State private var currentOperation: CalculatorOperation? = nil
    @State private var storedValue: Double? = nil
    @State private var isNewInput = true
    @State private var showsClear = false

    var body: some View {
        GeometryReader { geometry in
            let topButtons: [[String]] = [
                [showsClear ? "C" : "AC", "√", "%", "÷"],
                ["7", "8", "9", "×"],
                ["4", "5", "6", "-"],
                ["1", "2", "3", "+"],
            ]

            let bottomButtons: [String] = ["0", ".", "="]

            VStack(spacing: 12) {
                Spacer()
                
                // Отображение значения калькулятора
                Text(displayValue)
                    .font(.system(size: displayFontSize(for: geometry.size)))
                    .fontWeight(.light)
                    .lineLimit(1)
                    .padding(.trailing)
                    .frame(minWidth: 0, maxWidth: .infinity, minHeight: 100, alignment: .trailing)
                    .foregroundColor(.white)
                    .accessibilityIdentifier("display")

                // Формирование верхних кнопок
                ForEach(topButtons, id: \.self) { row in
                    HStack(spacing: 12) {
                        ForEach(row, id: \.self) { button in
                            Button(action: {
                                self.buttonTapped(button)
                            }) {
                                Text(button)
                                    .font(.system(size: buttonFontSize(for: geometry.size, isHorizontal: geometry.size.width > geometry.size.height)))
                                    .frame(width: buttonWidth(for: geometry.size, isHorizontal: geometry.size.width > geometry.size.height), height: buttonWidth(for: geometry.size, isHorizontal: geometry.size.width > geometry.size.height))
                                    .background(buttonColor(button: button))
                                    .foregroundColor(.white)
                                    .cornerRadius(buttonWidth(for: geometry.size, isHorizontal: geometry.size.width > geometry.size.height) / 2)
                            }
                            .accessibilityIdentifier(button)
                        }
                    }
                }
                
                // Формирование нижних кнопок
                HStack(spacing: 12) {
                    ForEach(bottomButtons, id: \.self) { button in
                        Button(action: {
                            self.buttonTapped(button)
                        }) {
                            Text(button)
                                .font(.system(size: buttonFontSize(for: geometry.size, isHorizontal: geometry.size.width > geometry.size.height)))
                                .frame(width: button == "0" ? buttonWidth(for: geometry.size, isHorizontal: geometry.size.width > geometry.size.height) * 2 + 12 : buttonWidth(for: geometry.size, isHorizontal: geometry.size.width > geometry.size.height), height: buttonWidth(for: geometry.size, isHorizontal: geometry.size.width > geometry.size.height))
                                .background(buttonColor(button: button))
                                .foregroundColor(.white)
                                .cornerRadius(button == "0" ? (buttonWidth(for: geometry.size, isHorizontal: geometry.size.width > geometry.size.height) * 2 + 12) / 2 : buttonWidth(for: geometry.size, isHorizontal: geometry.size.width > geometry.size.height) / 2)
                        }
                        .accessibilityIdentifier(button)
                    }
                }
                
                Spacer()
            }
            .background(Color.black)
            .edgesIgnoringSafeArea(.bottom)
        }
    }

    // Обработка нажатия на кнопку
    private func buttonTapped(_ button: String) {
        switch button {
        case "0"..."9", ".":
            handleNumberInput(button)
        case "+", "-", "×", "÷":
            if isNewInput, let operation = CalculatorOperation(symbol: button) {
                currentOperation = operation
            } else if let operation = CalculatorOperation(symbol: button) {
                executeCurrentOperation()
                currentOperation = operation
                storedValue = Double(displayValue)
                isNewInput = true
            }
        case "√", "%":
            applyUnaryOperation(button)
        case "=":
            executeCurrentOperation()
        case "AC":
            clearAll()
        case "C":
            removeLastDigit()
        default:
            break
        }
    }

    // Применение унарной операции (корень, проценты)
    private func applyUnaryOperation(_ operation: String) {
        if let value = Double(displayValue) {
            switch operation {
            case "√":
                displayValue = value >= 0 ? formatNumber(sqrt(value)) : "Error"
            case "%":
                displayValue = formatNumber(value / 100)
            default:
                break
            }
        }
    }
    
    // Выполнение текущей бинарной операции
    private func executeCurrentOperation() {
        guard let operation = currentOperation, let stored = storedValue, let current = Double(displayValue) else {
            if currentOperation == nil, let currentValue = Double(displayValue) {
                storedValue = currentValue
            }
            return
        }
        
        var result: Double?
        switch operation {
        case .addition:
            result = stored + current
        case .subtraction:
            result = stored - current
        case .multiplication:
            result = stored * current
        case .division:
            result = current != 0 ? stored / current : nil
        default:
            break
        }
        
        if let resultValue = result {
            displayValue = formatNumber(resultValue)
            storedValue = resultValue
        } else {
            displayValue = "Error"
        }
        
        isNewInput = true
    }

    // Очистка после выполнения операции
    private func clearAfterOperation() {
        currentOperation = nil
        storedValue = nil
        isNewInput = true
    }

    // Полная очистка калькулятора
    private func clearAll() {
        displayValue = "0"
        currentOperation = nil
        storedValue = nil
        isNewInput = true
        showsClear = false
    }

    // Обработка ввода чисел
    private func handleNumberInput(_ input: String) {
        if isNewInput || displayValue == "0" {
            displayValue = input != "." ? input : "0."
            isNewInput = false
        } else if displayValue.count < 9, !(input == "." && displayValue.contains(".")) {
            displayValue += input
        }
        showsClear = true
    }

    private func formatNumber(_ value: Double) -> String {
        let maxDisplayLength = 9
        let formatter = NumberFormatter()
        formatter.locale = Locale(identifier: "en_US_POSIX")
        formatter.usesGroupingSeparator = false
        
        // Попытка форматирования числа в десятичном формате
        formatter.numberStyle = .decimal
        formatter.maximumFractionDigits = 5
        formatter.minimumFractionDigits = 0
        var formattedString = formatter.string(from: NSNumber(value: value)) ?? "Error"
        
        // Проверка на превышение максимальной длины
        if formattedString.count > maxDisplayLength {
            // Переключение на научный формат, если число слишком большое
            formatter.numberStyle = .scientific
            formatter.maximumFractionDigits = maxDisplayLength - 5 // Адаптация количества знаков после запятой
            formattedString = formatter.string(from: NSNumber(value: value)) ?? "Error"
        }
        
        return formattedString
    }

    // Вычисление размера шрифта для отображения
    private func displayFontSize(for size: CGSize) -> CGFloat {
        let minDimension = min(size.width, size.height)
        return minDimension * 0.16
    }

    // Вычисление размера кнопки
    private func buttonFontSize(for size: CGSize, isHorizontal: Bool) -> CGFloat {
        let buttonWidth = buttonWidth(for: size, isHorizontal: isHorizontal)
        return buttonWidth * 0.4
    }

    // Вычисление ширины кнопки
    private func buttonWidth(for size: CGSize, isHorizontal: Bool) -> CGFloat {
        let screenWidth = isHorizontal ? size.height / 2 : size.width
        return screenWidth / 5
    }

    // Определение цвета кнопки в зависимости от типа
    private func buttonColor(button: String) -> Color {
        switch button {
        case "AC", "C", "√", "%":
            return Color.gray
        case "÷", "×", "-", "+", "=":
            return Color.orange
        default:
            return Color.gray.opacity(0.5)
        }
    }
    
    // Удаление последней введенной цифры
    private func removeLastDigit() {
        guard !displayValue.isEmpty && displayValue != "0" else { return }

        displayValue = "0"
        isNewInput = true
        showsClear = false
    }
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}
