new Vue({
    el: '#app',
    data: {
        selectedCity: '',
        selectedWorkshop: '',
        selectedEmployee: '',
        selectedBrigade: '',
        shift: 1,
        cities: ["Город1", "Город2"],
        cityWorkshopMapping: {
            "Город1": ["Цех1", "Цех2"],
            "Город2": ["Цех3", "Цех4"]
        },
        workshopEmployeeMapping: {
            "Цех1": ["Сотрудник1", "Сотрудник2"],
            "Цех2": ["Сотрудник3", "Сотрудник4"]
        },
        brigades: ["Бригада1", "Бригада2"],
        availableWorkshops: [],
        availableEmployees: []
    },
    methods: {
        updateWorkshop() {
            this.availableWorkshops = this.cityWorkshopMapping[this.selectedCity] || [];
            this.updateEmployee();
        },
        updateEmployee() {
            this.availableEmployees = this.workshopEmployeeMapping[this.selectedWorkshop] || [];
        },
        saveToCookie() {
            Cookies.set('selectedCity', this.selectedCity);
            Cookies.set('selectedWorkshop', this.selectedWorkshop);
            Cookies.set('selectedEmployee', this.selectedEmployee);
            Cookies.set('selectedBrigade', this.selectedBrigade);
            Cookies.set('shift', this.shift);
        }
    },
    mounted() {
        this.updateWorkshop();
    },
    watch: {
        selectedCity() {
            this.updateBrigade();
        }
    }
});
