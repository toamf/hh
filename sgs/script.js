let cityWorkshopMapping = {
    "Город1": ["Цех1", "Цех2"],
    "Город2": ["Цех3", "Цех4"]
};

let workshopEmployeeMapping = {
    "Цех1": ["Сотрудник1", "Сотрудник2"],
    "Цех2": ["Сотрудник3", "Сотрудник4"]
};

function updateWorkshop() {
    let citySelect = document.getElementById("city");
    let workshopSelect = document.getElementById("workshop");
    let selectedCity = citySelect.value;

    let workshops = cityWorkshopMapping[selectedCity] || [];
    workshopSelect.innerHTML = workshops.map(w => `<option value="${w}">${w}</option>`).join('');

    updateEmployee();
}

function updateEmployee() {
    let workshopSelect = document.getElementById("workshop");
    let employeeSelect = document.getElementById("employee");
    let selectedWorkshop = workshopSelect.value;

    let employees = workshopEmployeeMapping[selectedWorkshop] || [];
    employeeSelect.innerHTML = employees.map(e => `<option value="${e}">${e}</option>`).join('');

    updateBrigade();
}

function updateBrigade() {
    let brigadeSelect = document.getElementById("brigade");
    let currentHour = new Date().getHours();
    if (currentHour >= 8 && currentHour < 20) {
        brigadeSelect.innerHTML = '<option value="Бригада1">Бригада1</option>';
    } else {
        brigadeSelect.innerHTML = '<option value="Бригада2">Бригада2</option>';
    }
}

window.onload = function() {
    let citySelect = document.getElementById("city");
    citySelect.innerHTML = Object.keys(cityWorkshopMapping).map(c => `<option value="${c}">${c}</option>`).join('');
    updateWorkshop();
}
