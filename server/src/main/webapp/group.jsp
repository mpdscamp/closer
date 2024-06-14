<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Group</title>
    <link rel="stylesheet" href="css/styles.css">
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>
    <script>
        $(document).ready(function() {
            const groupId = '<%= request.getParameter("GROUP_ID") %>';
            const groupName = '<%= request.getParameter("GROUP_NAME") %>';
            const userEmail = '<%= request.getParameter("USER_EMAIL") %>';

            if (!groupId || !groupName || !userEmail) {
                console.error('Missing parameters.');
                return;
            }

            $('#groupNameDisplay').text("Grupo " + groupName);
            fetchGroupMembers(groupId);

            $('#inviteToGroupButton').click(function() {
                window.location.href = 'inviteToGroup.jsp?GROUP_ID=' + groupId + '&USER_EMAIL=' + encodeURIComponent(userEmail);
            });
        });

        function fetchGroupMembers(groupId) {
            $.ajax({
                url: 'get-group-members?groupId=' + encodeURIComponent(groupId),
                method: 'GET',
                success: function(response) {
                    console.log('Group members response:', response);
                    const membersList = $('#membersList');
                    membersList.empty();

                    if (Array.isArray(response)) {
                        response.forEach(function(member) {
                            var memberItem = '<div class="member-item">' +
                                '<p>' + member + '</p>' +
                                '</div>';
                            membersList.append(memberItem);
                        });
                    } else {
                        console.error('Expected an array but got:', response);
                    }
                },
                error: function(error) {
                    console.error('Error fetching group members:', error);
                }
            });
        }
    </script>
</head>
<body>
<div class="group-container">
    <h2 id="groupNameDisplay"></h2>
    <button id="inviteToGroupButton" class="invite-button">Convidar para o Grupo</button>
    <div id="membersList" class="members-list"></div>
</div>
</body>
</html>
