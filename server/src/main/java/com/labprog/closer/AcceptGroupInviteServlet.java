package com.labprog.closer;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/accept-group-invite")
public class AcceptGroupInviteServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String inviteId = request.getParameter("inviteId");
        response.setContentType("text/plain");
        PrintWriter out = response.getWriter();

        Connection connection = null;
        PreparedStatement statement = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DatabaseConnection.getConnection();

            String getInviteSql = "SELECT user_id, group_id FROM GroupInvites WHERE invite_id = ?";
            statement = connection.prepareStatement(getInviteSql);
            statement.setInt(1, Integer.parseInt(inviteId));
            ResultSet rs = statement.executeQuery();
            int userId = -1;
            int groupId = -1;
            if (rs.next()) {
                userId = rs.getInt("user_id");
                groupId = rs.getInt("group_id");
            }

            if (userId != -1 && groupId != -1) {
                String insertMembershipSql = "INSERT INTO GroupMemberships (user_id, group_id) VALUES (?, ?)";
                statement = connection.prepareStatement(insertMembershipSql);
                statement.setInt(1, userId);
                statement.setInt(2, groupId);
                statement.executeUpdate();

                // Delete all invites related to this group
                String deleteInviteSql = "DELETE FROM GroupInvites WHERE group_id = ?";
                statement = connection.prepareStatement(deleteInviteSql);
                statement.setInt(1, groupId);
                statement.executeUpdate();

                out.print("Convite de grupo aceito!");
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
