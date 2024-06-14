<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Login</title>
    <link rel="stylesheet" href="css/styles.css">
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
<div class="container">
    <img src="images/logo.png" alt="Logo" class="logo">
    <form id="loginForm">
        <input type="email" name="email" id="email" placeholder="E-mail" required>
        <input type="password" name="password" id="password" placeholder="Senha" required>
        <button type="submit">Login</button>
    </form>
    <p>Não possui uma conta? <a href="register.jsp">Cadastre-se</a></p>
</div>
</body>
</html>
