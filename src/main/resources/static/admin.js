
// Инициализация приложения. Запуск DOMContentLoaded.
document.addEventListener('DOMContentLoaded', async () => {
    await loadCurrentUser();
    await loadUsers();
    const roles = await loadRoles();
    createRolesDropdown('#newRoles', roles);
    setupEventListeners();
});

// Загрузка данных текущего пользователя
async function loadCurrentUser() {
    const response = await fetch('/api/user');
    if (!response.ok) {
        throw new Error(`HTTP error! Status: ${response.status}`);
    }
    const user = await response.json();
    document.querySelector('#userEmail').textContent = user.email;
    document.querySelector('#userRoles').textContent = user.roles.map(role => role.name.replace('ROLE_', '')).join(' ');
}

// Генерация таблицы пользователей
function createUserTable(users) {
    const tbody = document.querySelector('#userTable tbody');
    tbody.innerHTML = '';
    users.forEach(user => {
        const row = document.createElement('tr');
        row.innerHTML = `
            <td>${user.id}</td>
            <td>${user.firstName}</td>
            <td>${user.lastName}</td>
            <td>${user.age}</td>
            <td>${user.email}</td>
            <td>${user.roles.map(role => role.name.replace('ROLE_', '')).join(' ')}</td>
            <td><button class="btn btn-info edit-btn" data-user-id="${user.id}">Edit</button></td>
            <td><button class="btn btn-danger delete-btn" data-user-id="${user.id}">Delete</button></td>
        `;
        tbody.appendChild(row);
    });
}

// Получение данных всех пользователей
async function loadUsers() {
    const response = await fetch('/api/admin/users');
    if (!response.ok) {
        throw new Error(`HTTP error! Status: ${response.status}`);
    }
    const users = await response.json();
    createUserTable(users);
}

// Асинхронная функция для загрузки списка ролей
async function loadRoles() {
    const response = await fetch('/api/admin/roles');
    if (!response.ok) {
        throw new Error(`HTTP error! Status: ${response.status}`);
    }
    return response.json();
}

// Обработчик событий
function setupEventListeners() {
    // Добавление нового пользователя
    document.querySelector('#newUserForm').addEventListener('submit', async (event) => {
        event.preventDefault();
        const formData = new FormData(event.target);
        const user = Object.fromEntries(formData.entries());

        const response = await fetch('/api/admin/users', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(user),
        });

        if (!response.ok) {
            throw new Error(`HTTP error! Status: ${response.status}`);
        }

        await loadUsers();                                                  // Обновляем список пользователей
        event.target.reset();                                               // Сбрасываем форму
        document.querySelector('#user-table-tab').click();          // Переключаемся на вкладку с отображением списка всех пользователей
    });

    // Обработка кликов по кнопкам Edit и Delete
    document.querySelector('#userTable').addEventListener('click', async (event) => {
        if (event.target.classList.contains('edit-btn')) {
            const userId = event.target.getAttribute('data-user-id');
            await openEditModal(userId);
        } else if (event.target.classList.contains('delete-btn')) {
            const userId = event.target.getAttribute('data-user-id');
            await openDeleteModal(userId);
        }
    });

    // Редактирование пользователя
    document.querySelector('#editUserForm').addEventListener('submit', async (event) => {
        event.preventDefault();
        const formData = new FormData(event.target);
        // const roles = Array.from(formData.getAll('roleIds')).map(id => ({ id: Number(id) })); // Array.from излишен, т.к. formData уже сам возвращает массив !!! Array.from создает копию существующего массива.
        const roles = formData.getAll('roleIds').map(id => ({ id: Number(id) }));
        // Что происходит на самом делев этом коде - const roles = formData.getAll('roleIds').map(id => ({ id: Number(id) }));
        // JavaScript (клиентская часть):
        // - Получает строковые значения roleIds из HTML-формы.
        // - Преобразует каждое строковое значение в число.
        // - Создает массив JavaScript объектов, где каждый объект имеет вид { id: Number(id) }.
        // - Отправляет этот массив объектов в формате JSON на сервер (Spring Boot REST API).
        // - Преобразуем roleIds в объекты Role
        // - Spring Boot (серверная часть):
        // - Получает JSON-данные от клиента.
        // - Использует механизм десериализации JSON (обычно Jackson, который является частью Spring) для преобразования JSON-данных в Java объекты.
        // - Создает List<Role> из полученных JSON объектов. Это происходит автоматически благодаря Spring и Jackson.
        const user = {
            id: formData.get('id'),
            firstName: formData.get('firstName'),
            lastName: formData.get('lastName'),
            age: formData.get('age'),
            email: formData.get('email'),
            password: formData.get('password'),
            roles: roles                              // Передаем объекты Role
        };

        const response = await fetch(`/api/admin/users/${user.id}`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(user),
        });

        if (!response.ok) {
            throw new Error(`HTTP error! Status: ${response.status}`);
        }

        await loadUsers();
        closeModal('#editUserModal');
    });

    // Удаление пользователя
    document.querySelector('#deleteUserForm').addEventListener('submit', async (event) => {
        event.preventDefault();
        const formData = new FormData(event.target);
        const user = {
            id: formData.get('id'),
            firstName: formData.get('firstName'),
            lastName: formData.get('lastName'),
            age: formData.get('age'),
            email: formData.get('email'),
            password: formData.get('password'),
            roleIds: Array.from(formData.getAll('roleIds')).map(Number)
        };

        const response = await fetch(`/api/admin/users/${user.id}`, {
            method: 'DELETE',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(user),
        });

        if (!response.ok) {
            throw new Error(`HTTP error! Status: ${response.status}`);
        }

        await loadUsers();
        closeModal('#deleteUserModal');
    });
}

// Открытие модального окна для редактирования пользователя
async function openEditModal(userId) {
    const response = await fetch(`/api/admin/users/${userId}`);
    const user = await response.json();
    const roles = await loadRoles();

    document.querySelector('#editUserId').value = user.id;
    document.querySelector('#userIdDisplayEdit').value = user.id; // Добавляем ID для отображения
    document.querySelector('#editFirstName').value = user.firstName;
    document.querySelector('#editLastName').value = user.lastName;
    document.querySelector('#editAge').value = user.age;
    document.querySelector('#editEmail').value = user.email;
    document.querySelector('#editPassword').value = '';
    createRolesDropdown('#editRoles', roles, user.roles.map(role => role.id));

    openModal('#editUserModal');
}

// Открытие модального окна для удаления пользователя
async function openDeleteModal(userId) {
    const response = await fetch(`/api/admin/users/${userId}`);
    const user = await response.json();
    const roles = await loadRoles();

    document.querySelector('#deleteUserId').value = user.id;
    document.querySelector('#userIdDisplayDelete').value = user.id; // Добавляем ID для отображения
    document.querySelector('#deleteFirstName').value = user.firstName;
    document.querySelector('#deleteLastName').value = user.lastName;
    document.querySelector('#deleteAge').value = user.age;
    document.querySelector('#deleteEmail').value = user.email;
    createRolesDropdown('#deleteRoles', roles, user.roles.map(role => role.id));

    openModal('#deleteUserModal');
}

// Создание выпадающего списка ролей
function createRolesDropdown(selector, roles, selectedRoleIds = []) {
    const dropdown = document.querySelector(selector);
    dropdown.innerHTML = roles.map(role => `
        <option value="${role.id}" ${selectedRoleIds.includes(role.id) ? 'selected' : ''}>
            ${role.name.replace('ROLE_', '')}
        </option>
    `).join('');
}

// Открытие модального окна
function openModal(selector) {
    const modal = new bootstrap.Modal(document.querySelector(selector));
    modal.show();
}

// Закрытие модального окна
function closeModal(selector) {
    const modal = bootstrap.Modal.getInstance(document.querySelector(selector));
    modal.hide();
}