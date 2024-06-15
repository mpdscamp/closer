<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="pt-br">
<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Closer - Register</title>
    <link rel="stylesheet" href="css/loginStyle.css">
    <link href='https://unpkg.com/boxicons@2.1.4/css/boxicons.min.css' rel='stylesheet'>
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>
</head>
<body>
<div class="wrapper">
    <form id="registerForm" action="register" method="post">
        <h1>Closer</h1>
        <div class="input-box">
            <input type="text" placeholder="Usuário" name="username" id="username" required>
            <i class='bx bxs-user'></i>
        </div>
        <div class="input-box">
            <input type="email" placeholder="E-mail" name="email" id="email" required>
            <i class='bx bxs-envelope'></i>
        </div>
        <div class="input-box">
            <input type="password" placeholder="Senha" name="password" id="password" required>
            <i class='bx bxs-lock-alt'></i>
        </div>
        <button type="submit" class="btn">Cadastrar</button>
        <div class="register-link">
            <p>Já possui uma conta? <a href="login.jsp">Login</a></p>
        </div>
    </form>
</div>
<script>
    $(document).ready(function() {
        $('#registerForm').submit(function(event) {
            event.preventDefault(); // Prevent default form submission

            const username = $('#username').val();
            const email = $('#email').val();
            const password = $('#password').val();

            $.ajax({
                url: 'register',
                type: 'POST',
                data: {
                    username: username,
                    email: email,
                    password: password
                },
                success: function(response) {
                    if (response.trim() === 'Registration successful') {
                        alert('Registration successful! Please login.');
                        window.location.href = 'login.jsp';
                    } else {
                        alert('Registration failed: ' + response);
                    }
                },
                error: function(error) {
                    console.error('Error during registration:', error);
                    alert('An error occurred. Please try again.');
                }
            });
        });
    });
</script>
</body>
</html>
