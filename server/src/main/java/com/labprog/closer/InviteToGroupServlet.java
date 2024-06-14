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

@WebServlet("/invite-to-group")
public class InviteToGroupServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String groupId = request.getParameter("groupId");
        String userEmail = request.getParameter("email");
        String friendEmail = request.getParameter("friendEmail");
        response.setContentType("text/plain");
        PrintWriter out = response.getWriter();

        Connection connection = null;
        PreparedStatement statement = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DatabaseConnection.getConnection();

            // Get user_id of the user who sends the invite
            String getUserSql = "SELECT user_id FROM Users WHERE email = ?";
            statement = connection.prepareStatement(getUserSql);
            statement.setString(1, userEmail);
            ResultSet rs = statement.executeQuery();
            int invitedBy = -1;
            if (rs.next()) {
                invitedBy = rs.getInt("user_id");
            }

            // Get user_id of the friend
            statement = connection.prepareStatement(getUserSql);
            statement.setString(1, friendEmail);
            rs = statement.executeQuery();
            int friendId = -1;
            if (rs.next()) {
                friendId = rs.getInt("user_id");
            }

            if (invitedBy != -1 && friendId != -1) {
                String inviteSql = "INSERT INTO GroupInvites (user_id, group_id, invited_by) VALUES (?, ?, ?)";
                statement = connection.prepareStatement(inviteSql);
                statement.setInt(1, friendId);
                statement.setInt(2, Integer.parseInt(groupId));
                statement.setInt(3, invitedBy);
                statement.executeUpdate();
                out.print("Convite de grupo enviado com sucesso!");
            } else {
                out.print("User not found");
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
