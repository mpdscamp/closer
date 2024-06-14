<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="pt-br">
<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Closer</title>
    <link rel="stylesheet" href="css/loginStyle.css">
    <link href='https://unpkg.com/boxicons@2.1.4/css/boxicons.min.css' rel='stylesheet'>
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>
    <script>
        $(document).ready(function() {
            $('#loginForm').submit(function(event) {
                event.preventDefault(); // Prevent default form submission

                const email = $('#email').val();
                const password = $('#password').val();

                $.ajax({
                    url: 'login',
                    type: 'POST',
                    data: {
                        email: email,
                        password: password
                    },
                    success: function(response) {
                        if (response.trim() === 'Login successful') {
                            window.location.href = 'groupList.jsp?USER_EMAIL=' + encodeURIComponent(email);
                        } else {
                            alert('Invalid credentials!');
                        }
                    },
                    error: function(error) {
                        console.error('Error during login:', error);
                        alert('An error occurred. Please try again.');
                    }
                });
            });
        });
    </script>
</head>
<body>
<div class="wrapper">
    <form id="loginForm">
        <h1>Closer</h1>
        <div class="input-box">
            <input type="email" placeholder="E-mail" name="email" id="email" required>
            <i class='bx bxs-user'></i>
        </div>

        <div class="input-box">
            <input type="password" placeholder="Senha" name="password" id="password" required>
            <i class='bx bxs-lock-alt'></i>
        </div>

        <button type="submit" class="btn">Login</button>

        <div class="register-link">
            <p>Não possui uma conta? <a href="register.jsp">Cadastre-se</a></p>
        </div>
    </form>
</div>
</body>
</html>
