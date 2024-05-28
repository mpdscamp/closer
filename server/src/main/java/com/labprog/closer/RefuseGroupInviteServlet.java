package com.labprog.closer;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/refuse-group-invite")
public class RefuseGroupInviteServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String inviteId = request.getParameter("inviteId");
        response.setContentType("text/plain");
        PrintWriter out = response.getWriter();

        String jdbcUrl = "jdbc:mysql://localhost:3306/closer";
        String username = "root";
        String password = "password"; // Replace with your actual password

        Connection connection = null;
        PreparedStatement statement = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(jdbcUrl, username, password);

            // Get the groupId from the invite
            String getGroupIdSql = "SELECT group_id FROM GroupInvites WHERE invite_id = ?";
            statement = connection.prepareStatement(getGroupIdSql);
            statement.setInt(1, Integer.parseInt(inviteId));
            ResultSet rs = statement.executeQuery();
            int groupId = -1;
            if (rs.next()) {
                groupId = rs.getInt("group_id");
            }

            if (groupId != -1) {
                // Delete all invites for the group
                String deleteInviteSql = "DELETE FROM GroupInvites WHERE group_id = ?";
                statement = connection.prepareStatement(deleteInviteSql);
                statement.setInt(1, groupId);
                statement.executeUpdate();

                out.print("Convite de grupo recusado!");
            } else {
                out.print("Invite not found");
            }

        } catch (ClassNotFoundException e) {
            e.printStackTrace(out);
        } catch (SQLException e) {
            e.printStackTrace(out);
        } finally {
            try {
                if (statement != null) statement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace(out);
            }
        }
    }
}
