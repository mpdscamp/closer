// GetPendingGroupInvitesServlet.java
package com.labprog.closer;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

@WebServlet("/get-pending-group-invites")
public class GetPendingGroupInvitesServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String userEmail = request.getParameter("email");
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        String jdbcUrl = "jdbc:mysql://localhost:3306/closer";
        String username = "root";
        String password = "password"; // Replace with your actual password

        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        List<GroupInvite> invites = new ArrayList<>();

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(jdbcUrl, username, password);

            String sql = "SELECT gi.invite_id, ug.group_name, u.username as invited_by " +
                    "FROM GroupInvites gi " +
                    "JOIN Users u ON gi.invited_by = u.user_id " +
                    "JOIN Users u2 ON gi.user_id = u2.user_id " +
                    "JOIN UserGroups ug ON gi.group_id = ug.group_id " +
                    "WHERE u2.email = ?";
            statement = connection.prepareStatement(sql);
            statement.setString(1, userEmail);
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int inviteId = resultSet.getInt("invite_id");
                String groupName = resultSet.getString("group_name");
                String invitedBy = resultSet.getString("invited_by");
                invites.add(new GroupInvite(inviteId, groupName, invitedBy));
            }

            Gson gson = new Gson();
            String json = gson.toJson(invites);
            out.print(json);

        } catch (ClassNotFoundException e) {
            e.printStackTrace(out);
        } catch (SQLException e) {
            e.printStackTrace(out);
        } finally {
            try {
                if (resultSet != null) resultSet.close();
                if (statement != null) statement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace(out);
            }
        }
    }

    class GroupInvite {
        int inviteId;
        String groupName;
        String invitedBy;

        GroupInvite(int inviteId, String groupName, String invitedBy) {
            this.inviteId = inviteId;
            this.groupName = groupName;
            this.invitedBy = invitedBy;
        }
    }
}
