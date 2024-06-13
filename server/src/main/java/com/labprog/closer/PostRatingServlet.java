package com.labprog.closer;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/post-rating")
public class PostRatingServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int imageId = Integer.parseInt(request.getParameter("imageId"));
        int userId = Integer.parseInt(request.getParameter("userId"));
        String rating = request.getParameter("rating");
        String userRated = request.getParameter("userRated");
        int groupId = Integer.parseInt(request.getParameter("groupId"));

        response.setContentType("text/plain");
        PrintWriter out = response.getWriter();

        String jdbcUrl = "jdbc:mysql://localhost:3306/closer";
        String username = "root";
        String password = "password";

        Connection connection = null;
        PreparedStatement statement = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(jdbcUrl, username, password);
            System.out.println("Database connection established.");

            String insertRatingSql = "INSERT INTO ratings (image_id, user_id, rating, rated_at, group_id, user_rated) VALUES (?, ?, ?, NOW(), ?, ?)";
            statement = connection.prepareStatement(insertRatingSql);
            statement.setInt(1, imageId);
            statement.setInt(2, userId);
            statement.setString(3, rating);
            statement.setInt(4, groupId);
            statement.setString(5, userRated);

            System.out.println("Executing query: " + statement.toString());
            statement.executeUpdate();

            out.print("Rating posted successfully");

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