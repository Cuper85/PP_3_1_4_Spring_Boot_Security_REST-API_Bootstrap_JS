
// Вызов функции для загрузки данных пользователя при загрузке страницы
document.addEventListener('DOMContentLoaded', function () {
    loadUserData();
});

function loadUserData() {
    fetch('/api/user')
        .then(response => response.json())
        .then(user => {
            // Отображение email и роли пользователя
            document.querySelector('#userEmail').textContent = user.email;
            const roles = user.roles.map(role => role.name.replace('ROLE_', '')).join(' ');
            document.querySelector('#userRoles').textContent = roles;

            // Заполнение таблицы данными пользователя
            const userTableBody = document.querySelector('#userTable tbody');
            userTableBody.innerHTML = `
                <tr>
                    <td>${user.id}</td>
                    <td>${user.firstName}</td>
                    <td>${user.lastName}</td>
                    <td>${user.age}</td>
                    <td>${user.email}</td>
                    <td>${roles}</td>
                </tr>
            `;
        })
        .catch(error => console.error('Ошибка при получении данных пользователя:', error));
}


