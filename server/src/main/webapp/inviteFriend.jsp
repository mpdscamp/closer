<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Invite Friends</title>
    <link rel="stylesheet" href="css/styles.css">
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>
    <script>
        $(document).ready(function() {
            const userEmail = '<%= request.getParameter("USER_EMAIL") %>';

            if (!userEmail) {
                console.error('USER_EMAIL parameter is missing.');
                return;
            }

            fetchUsers(userEmail);

            function fetchUsers(email) {
                $.ajax({
                    url: 'get-non-friends?email=' + encodeURIComponent(email),
                    method: 'GET',
                    success: function(response) {
                        console.log('Response:', response); // Log the response to check its structure
                        const usersList = $('#usersList');
                        usersList.empty();

                        if (Array.isArray(response)) {
                            response.forEach(function(user) {
                                console.log('User:', user); // Log each user to debug

                                // Using string concatenation instead of template literals
                                var userItem = '<div class="user-item">' +
                                    '<div class="user-info">' +
                                    '<h3>' + user.username + '</h3>' +
                                    '<p>' + user.email + '</p>' +
                                    '</div>' +
                                    '<button onclick="inviteFriend(\'' + user.email + '\')" class="invite-button">Convidar</button>' +
                                    '</div>';

                                usersList.append(userItem);
                            });
                        } else {
                            console.error('Expected an array but got:', response);
                        }
                    },
                    error: function(error) {
                        console.error('Error fetching users:', error);
                    }
                });
            }

            window.inviteFriend = function(friendEmail) {
                $.ajax({
                    url: 'invite-friend',
                    method: 'POST',
                    data: {
                        email: userEmail,
                        friendEmail: friendEmail
                    },
                    success: function(response) {
                        alert('Convite enviado com sucesso!');
                        fetchUsers(userEmail); // Refresh the list after inviting a friend
                    },
                    error: function(error) {
                        console.error('Error inviting friend:', error);
                        alert('Erro ao enviar convite.');
                    }
                });
            }
        });
    </script>
</head>
<body>
<div class="invite-friend-container">
    <h2>Convidar Amigos</h2>
    <div id="usersList" class="users-list"></div>
</div>
</body>
</html>
