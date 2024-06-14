document.addEventListener('DOMContentLoaded', () => {
    const loginForm = document.getElementById('loginForm');
    const registerForm = document.getElementById('registerForm');

    if (loginForm) {
        loginForm.addEventListener('submit', async (e) => {
            e.preventDefault();
            const formData = new FormData(loginForm);
            const data = new URLSearchParams(formData);

            const response = await fetch('login', {
                method: 'POST',
                body: data
            });

            const result = await response.text();
            if (result === 'Login successful') {
                window.location.href = 'groupList.jsp'; // Adjust as needed
            } else {
                alert('Invalid credentials!');
            }
        });
    }

    if (registerForm) {
        registerForm.addEventListener('submit', async (e) => {
            e.preventDefault();
            const formData = new FormData(registerForm);
            const data = new URLSearchParams(formData);

            const response = await fetch('register', {
                method: 'POST',
                body: data
            });

            const result = await response.text();
            if (result === 'Registration successful') {
                alert('Registration successful!');
                window.location.href = 'login.jsp';
            } else {
                alert('Registration failed!');
            }
        });
    }
});
