<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Invite to Group</title>
    <link rel="stylesheet" href="css/styles.css">
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>
    <script>
        $(document).ready(function() {
            const groupId = '<%= request.getParameter("GROUP_ID") %>';
            const userEmail = '<%= request.getParameter("USER_EMAIL") %>';
            if (!groupId || !userEmail) {
                console.error('GROUP_ID or USER_EMAIL parameter is missing.');
                return;
            }

            fetchGroupMembers(groupId);
            fetchUsers(userEmail, groupId);

            $('#inviteToGroupButton').click(function() {
                inviteToGroup();
            });
        });

        function fetchGroupMembers(groupId) {
            $.ajax({
                url: 'get-group-members?groupId=' + encodeURIComponent(groupId),
                method: 'GET',
                success: function(response) {
                    console.log('Group members response:', response);
                    if (Array.isArray(response)) {
                        window.groupMembers = response; // Store group members globally
                    } else {
                        console.error('Expected an array but got:', response);
                    }
                },
                error: function(error) {
                    console.error('Error fetching group members:', error);
                }
            });
        }

        function fetchUsers(userEmail, groupId) {
            $.ajax({
                url: 'get-friends?email=' + encodeURIComponent(userEmail),
                method: 'GET',
                success: function(response) {
                    console.log('User list response:', response);
                    const userList = $('#userList');
                    userList.empty();

                    if (Array.isArray(response)) {
                        response.forEach(function(user) {
                            if (user.email !== userEmail && !window.groupMembers.includes(user.email)) {
                                let userItem = '<div class="user-item">' +
                                    '<p>' + user.username + ' (' + user.email + ')</p>' +
                                    '<button onclick="inviteToGroup(\'' + user.email + '\', ' + groupId + ')" class="invite-button">Invite</button>' +
                                    '</div>';
                                userList.append(userItem);
                            }
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

        function inviteToGroup(friendEmail, groupId) {
            $.ajax({
                url: 'invite-to-group',
                method: 'POST',
                data: {
                    groupId: groupId,
                    email: '<%= request.getParameter("USER_EMAIL") %>',
                    friendEmail: friendEmail
                },
                success: function(response) {
                    alert(response);
                    window.location.reload(); // Reload the page to update the list of users
                },
                error: function(error) {
                    console.error('Error sending invite:', error);
                    alert('Error sending invite');
                }
            });
        }
    </script>
</head>
<body>
<div class="invite-to-group-container">
    <h2>Invite Friends to Group</h2>
    <div id="userList" class="user-list"></div>
</div>
</body>
</html>
