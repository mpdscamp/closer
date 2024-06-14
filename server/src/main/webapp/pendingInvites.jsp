<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Pending Invites</title>
    <link rel="stylesheet" href="css/styles.css">
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>
    <script>
        $(document).ready(function() {
            const userEmail = '<%= request.getParameter("USER_EMAIL") %>';

            if (!userEmail) {
                console.error('USER_EMAIL parameter is missing.');
                return;
            }

            fetchPendingInvites(userEmail);

            function fetchPendingInvites(email) {
                $.ajax({
                    url: 'get-pending-invites?email=' + encodeURIComponent(email),
                    method: 'GET',
                    success: function(response) {
                        console.log('Response:', response); // Log the response to check its structure
                        const invitesList = $('#invitesList');
                        invitesList.empty();

                        if (Array.isArray(response)) {
                            response.forEach(function(invite) {
                                console.log('Invite:', invite); // Log each invite to debug

                                // Using string concatenation instead of template literals
                                var inviteItem = '<div class="invite-item">' +
                                    '<div class="invite-info">' +
                                    '<h3>' + invite.username + '</h3>' +
                                    '<p>' + invite.email + '</p>' +
                                    '</div>' +
                                    '<button onclick="handleInviteAction(\'' + invite.email + '\', true)" class="accept-button">Aceitar</button>' +
                                    '<button onclick="handleInviteAction(\'' + invite.email + '\', false)" class="reject-button">Recusar</button>' +
                                    '</div>';

                                invitesList.append(inviteItem);
                            });
                        } else {
                            console.error('Expected an array but got:', response);
                        }
                    },
                    error: function(error) {
                        console.error('Error fetching invites:', error);
                    }
                });
            }

            window.handleInviteAction = function(friendEmail, accept) {
                const actionUrl = accept ? 'accept-friend-invite' : 'refuse-friend-invite';
                $.ajax({
                    url: actionUrl,
                    method: 'POST',
                    data: {
                        email: userEmail,
                        friendEmail: friendEmail
                    },
                    success: function(response) {
                        alert(response);
                        fetchPendingInvites(userEmail); // Refresh the list after handling an invite
                    },
                    error: function(error) {
                        console.error('Error handling invite:', error);
                        alert('Erro ao processar o convite.');
                    }
                });
            }
        });
    </script>
</head>
<body>
<div class="pending-invites-container">
    <h2>Convites Pendentes</h2>
    <div id="invitesList" class="invites-list"></div>
</div>
</body>
</html>
