<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="pt-br">
<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Group List</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet"
          integrity="sha384-1BmE4kWBq78iYhFldvKuhfTAU6auU8tT94WrHftjDbrCEXSU1oBoqyl2QvZ6jIW3" crossorigin="anonymous">
    <link rel="stylesheet" href="css/groupsStyle.css">
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
                    const groupList = $('#groupList');
                    groupList.empty();

                    if (Array.isArray(response)) {
                        response.forEach(function(group) {
                            let imageUrl = group.imageUrl.replace('10.0.2.2', 'localhost');

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

                            fetchGroupMembers(group.groupId, function(members) {
                                var groupItem = '<div class="col-md-6 col-lg-4 col-xl-3 mb-4">' +
                                    '<div class="card h-100">' +
                                    '<img src="' + imageUrl + '" alt="' + group.groupName + '" class="card-img-top">' +
                                    '<div class="card-body">' +
                                    '<h5 class="card-title">' + group.groupName + '</h5>' +
                                    '<p class="card-text">' + theme + '</p>' +
                                    '<p class="card-text">Membros: ' + members.join(', ') + '</p>' +
                                    '<button onclick="enterGroup(' + group.groupId + ', \'' + group.groupName + '\', \'' + email + '\')" class="btn btn-primary">Entrar</button>' +
                                    '</div>' +
                                    '</div>' +
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
<header>
    <nav class="container">
        <h1>Closer</h1>
        <div class="Closer-contact-container">
            <span id="userNameDisplay" style="font-weight: bold; font-size: 18px;"></span>
            <button id="friendsListButton" class="btn btn-secondary">Amigos</button>
            <button id="createGroupButton" class="btn btn-primary">Criar Grupo</button>
        </div>
    </nav>
</header>
<main class="container mt-4">
    <div id="groupList" class="row"></div>
</main>
</body>
</html>
