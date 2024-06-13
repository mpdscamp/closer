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

@WebServlet("/post-message")
public class PostMessageServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String userEmail = request.getParameter("email");
        int groupId = Integer.parseInt(request.getParameter("groupId"));
        int challengeId = Integer.parseInt(request.getParameter("challengeId"));
        String imageUrl = request.getParameter("imageUrl");
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
            System.out.println("Database connection established.");

            String getUserSql = "SELECT user_id FROM Users WHERE email = ?";
            statement = connection.prepareStatement(getUserSql);
            statement.setString(1, userEmail);
            System.out.println("Executing query: " + statement.toString());
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                int userId = rs.getInt("user_id");

                String insertImageSql = "INSERT INTO images (group_id, user_id, challenge_id, image_url, submitted_at) VALUES (?, ?, ?, ?, NOW())";
                statement = connection.prepareStatement(insertImageSql);
                statement.setInt(1, groupId);
                statement.setInt(2, userId);
                statement.setInt(3, challengeId);
                statement.setString(4, imageUrl);
                System.out.println("Executing query: " + statement.toString());
                statement.executeUpdate();

                out.print("Image posted successfully");
            } else {
                out.print("User not found");
            }

        } catch (ClassNotFoundException e) {
            e.printStackTrace(out);
            System.err.println("JDBC Driver not found.");
        } catch (SQLException e) {
            e.printStackTrace(out);
            System.err.println("SQL Error: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace(out);
            System.err.println("Unknown Error: " + e.getMessage());
        } finally {
            try {
                if (statement != null) statement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace(out);
                System.err.println("Error closing resources: " + e.getMessage());
            }
        }
    }
}
