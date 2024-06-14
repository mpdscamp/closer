<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Create Group</title>
    <link rel="stylesheet" href="css/styles.css">
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>
    <script>
        $(document).ready(function() {
            const userEmail = '<%= request.getParameter("USER_EMAIL") %>';

            $('#createGroupButton').click(function() {
                const groupName = $('#groupName').val();
                const groupTheme = $('#groupTheme').val();
                const translatedTheme = translateTheme(groupTheme);

                createGroup(userEmail, groupName, translatedTheme);
            });
        });

        function translateTheme(theme) {
            switch (theme.toLowerCase()) {
                case 'família':
                    return 'family';
                case 'amigos':
                    return 'friends';
                case 'romântico':
                    return 'romantic';
                default:
                    return theme;
            }
        }

        function createGroup(email, groupName, groupTheme) {
            $.ajax({
                url: 'create-group',
                method: 'POST',
                data: {
                    email: email,
                    groupName: groupName,
                    groupTheme: groupTheme
                },
                success: function(response) {
                    alert('Group created successfully');
                    window.location.href = 'groupList.jsp?USER_EMAIL=' + encodeURIComponent(email);
                },
                error: function(error) {
                    console.error('Error creating group:', error);
                    alert('Error creating group');
                }
            });
        }
    </script>
</head>
<body>
<div class="create-group-container">
    <h2>Criar Grupo</h2>
    <input type="text" id="groupName" placeholder="Nome do Grupo" required>
    <select id="groupTheme">
        <option value="família">Família</option>
        <option value="amigos">Amigos</option>
        <option value="romântico">Romântico</option>
    </select>
    <button id="createGroupButton">Criar Grupo</button>
</div>
</body>
</html>
