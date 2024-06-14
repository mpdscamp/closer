<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Group List</title>
    <link rel="stylesheet" href="css/styles.css">
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>
    <script>
        $(document).ready(function() {
            const userEmail = '<%= request.getParameter("USER_EMAIL") %>';
            if (!userEmail) {
                console.error('USER_EMAIL parameter is missing.');
                return;
            }

            fetchUserName(userEmail);
            fetchGroups(userEmail);

            $('#createGroupButton').click(function() {
                window.location.href = 'createGroup.jsp?USER_EMAIL=' + encodeURIComponent(userEmail);
            });

            $('#friendsListButton').click(function() {
                window.location.href = 'friendsList.jsp?USER_EMAIL=' + encodeURIComponent(userEmail);
            });
        });

        function fetchUserName(email) {
            $.ajax({
                url: 'get-username?email=' + encodeURIComponent(email),
                method: 'GET',
                success: function(response) {
                    $('#userNameDisplay').text(response.username);
                },
                error: function(error) {
                    console.error('Error fetching username:', error);
                }
            });
        }

        function fetchGroups(email) {
            $.ajax({
                url: 'group-list?email=' + encodeURIComponent(email),
                method: 'GET',
                success: function(response) {
                    console.log('Group list response:', response); // Log the response to check its structure
                    const groupList = $('#groupList');
                    groupList.empty();

                    if (Array.isArray(response)) {
                        response.forEach(function(group) {
                            console.log('Group:', group); // Log each group to debug
                            let imageUrl = group.imageUrl.replace('10.0.2.2', 'localhost'); // Remove the domain part if needed

                            // Map themes to Portuguese equivalents
                            let theme = group.theme;
                            switch (theme) {
                                case "family":
                                    theme = "Família";
                                    break;
                                case "friends":
                                    theme = "Amigos";
                                    break;
                                case "romantic":
                                    theme = "Romântico";
                                    break;
                            }

                            // Fetch group members and then append the group item
                            fetchGroupMembers(group.groupId, function(members) {
                                // Using string concatenation instead of template literals
                                var groupItem = '<div class="group-item">' +
                                    '<img src="' + imageUrl + '" alt="' + group.groupName + '" class="group-image">' +
                                    '<div class="group-info">' +
                                    '<h3>' + group.groupName + '</h3>' +
                                    '<p>' + theme + '</p>' +
                                    '<p>Membros: ' + members.join(', ') + '</p>' +
                                    '</div>' +
                                    '<button onclick="enterGroup(' + group.groupId + ', \'' + group.groupName + '\', \'' + email + '\')" class="group-button">Entrar</button>' +
                                    '</div>';

                                $('#groupList').append(groupItem);
                            });
                        });
                    } else {
                        console.error('Expected an array but got:', response);
                    }
                },
                error: function(error) {
                    console.error('Error fetching groups:', error);
                }
            });
        }

        function fetchGroupMembers(groupId, callback) {
            $.ajax({
                url: 'get-group-members?groupId=' + groupId,
                method: 'GET',
                success: function(response) {
                    console.log('Group members response:', response); // Log the response to check its structure
                    if (Array.isArray(response)) {
                        callback(response);
                    } else {
                        console.error('Expected an array but got:', response);
                        callback([]);
                    }
                },
                error: function(error) {
                    console.error('Error fetching group members:', error);
                    callback([]);
                }
            });
        }

        function enterGroup(groupId, groupName, userEmail) {
            window.location.href = 'group.jsp?GROUP_ID=' + groupId + '&GROUP_NAME=' + encodeURIComponent(groupName) + '&USER_EMAIL=' + encodeURIComponent(userEmail);
        }
    </script>

</head>
<body>
<div class="group-list-container">
    <div>
        <span id="userNameDisplay" style="font-weight: bold; font-size: 18px;"></span>
        <button id="friendsListButton">Amigos</button>
        <button id="createGroupButton">Criar Grupo</button>
    </div>
    <div id="groupList" class="group-list"></div>
</div>
</body>
</html>
