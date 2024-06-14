<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Friends List</title>
    <link rel="stylesheet" href="css/styles.css">
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>
    <script>
        $(document).ready(function() {
            const userEmail = '<%= request.getParameter("USER_EMAIL") %>';
            if (!userEmail) {
                console.error('USER_EMAIL parameter is missing.');
                return;
            }
            fetchFriends(userEmail);

            $('#inviteFriendButton').click(function() {
                window.location.href = 'inviteFriend.jsp?USER_EMAIL=' + encodeURIComponent(userEmail);
            });

            $('#pendingInvitesButton').click(function() {
                window.location.href = 'pendingInvites.jsp?USER_EMAIL=' + encodeURIComponent(userEmail);
            });

            $('#pendingGroupInvitesButton').click(function() {
                window.location.href = 'pendingGroupInvites.jsp?USER_EMAIL=' + encodeURIComponent(userEmail);
            });
        });

        function fetchFriends(email) {
            $.ajax({
                url: 'get-friends?email=' + encodeURIComponent(email),
                method: 'GET',
                success: function(response) {
                    console.log('Response:', response); // Log the response to check its structure
                    const friendsList = $('#friendsList');
                    friendsList.empty();

                    if (Array.isArray(response)) {
                        response.forEach(function(friend) {
                            console.log('Friend:', friend); // Log each friend to debug

                            // Using string concatenation instead of template literals
                            var friendItem = '<div class="friend-item">' +
                                '<div class="friend-info">' +
                                '<h3>' + friend.username + '</h3>' +
                                '<p>' + friend.email + '</p>' +
                                '</div>' +
                                '</div>';

                            friendsList.append(friendItem);
                        });
                    } else {
                        console.error('Expected an array but got:', response);
                    }
                },
                error: function(error) {
                    console.error('Error fetching friends:', error);
                }
            });
        }
    </script>
</head>
<body>
<div class="friends-list-container">
    <div>
        <h2>Amigos</h2>
        <button id="inviteFriendButton">Convidar Amigos</button>
        <button id="pendingInvitesButton">Convites de Amizade Pendentes</button>
        <button id="pendingGroupInvitesButton">Convites de Grupo Pendentes</button>
    </div>
    <div id="friendsList" class="friends-list"></div>
</div>
</body>
</html>
