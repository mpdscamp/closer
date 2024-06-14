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

@WebServlet("/get-non-friends")
public class GetNonFriendsServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String userEmail = request.getParameter("email");
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        Connection connection = null;
        PreparedStatement statement = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DatabaseConnection.getConnection();

            // Get user ID
            String getUserIdSql = "SELECT user_id FROM Users WHERE email = ?";
            statement = connection.prepareStatement(getUserIdSql);
            statement.setString(1, userEmail);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                int userId = rs.getInt("user_id");

                // Get non-friends
                String getNonFriendsSql = "SELECT user_id, username, email FROM Users " +
                        "WHERE user_id != ? AND user_id NOT IN " +
                        "(SELECT user2_id FROM Friendships WHERE user1_id = ? " +
                        "UNION SELECT user1_id FROM Friendships WHERE user2_id = ?)";
                statement = connection.prepareStatement(getNonFriendsSql);
                statement.setInt(1, userId);
                statement.setInt(2, userId);
                statement.setInt(3, userId);
                rs = statement.executeQuery();

                // Build JSON response
                out.print("[");
                boolean first = true;
                while (rs.next()) {
                    if (!first) out.print(",");
                    out.print("{\"username\":\"" + rs.getString("username") + "\",");
                    out.print("\"email\":\"" + rs.getString("email") + "\"}");
                    first = false;
                }
                out.print("]");
            } else {
                out.print("[]");
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
