package com.labprog.closer;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/refuse-friend-invite")
public class RefuseFriendInviteServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String userEmail = request.getParameter("email");
        String friendEmail = request.getParameter("friendEmail");
        response.setContentType("text/plain");
        PrintWriter out = response.getWriter();

        String jdbcUrl = "jdbc:mysql://localhost:3306/closer";
        String username = "root";
        String password = "MyNewPass"; // Replace with your actual password

        Connection connection = null;
        PreparedStatement getUserStatement = null;
        PreparedStatement deleteInviteStatement = null;
        ResultSet rs = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(jdbcUrl, username, password);

            // Retrieve user ids
            int userId = getUserId(connection, userEmail);
            int friendId = getUserId(connection, friendEmail);

            if (userId == -1 || friendId == -1) {
                out.print("User not found");
                return;
            }

            // Remove from friend invites
            String deleteInviteSql = "DELETE FROM FriendInvites WHERE (user1_id = ? AND user2_id = ?) OR (user1_id = ? AND user2_id = ?)";
            deleteInviteStatement = connection.prepareStatement(deleteInviteSql);
            deleteInviteStatement.setInt(1, userId);
            deleteInviteStatement.setInt(2, friendId);
            deleteInviteStatement.setInt(3, friendId);
            deleteInviteStatement.setInt(4, userId);
            deleteInviteStatement.executeUpdate();

            out.print("Convite de amizade recusado!");

        } catch (ClassNotFoundException e) {
            e.printStackTrace(out);
        } catch (SQLException e) {
            e.printStackTrace(out);
        } finally {
            try {
                if (rs != null) rs.close();
                if (getUserStatement != null) getUserStatement.close();
                if (deleteInviteStatement != null) deleteInviteStatement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace(out);
            }
        }
    }

    private int getUserId(Connection connection, String email) throws SQLException {
        String getUserIdSql = "SELECT user_id FROM Users WHERE email = ?";
        try (PreparedStatement statement = connection.prepareStatement(getUserIdSql)) {
            statement.setString(1, email);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("user_id");
                }
            }
        }
        return -1;
    }
}
