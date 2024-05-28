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

@WebServlet("/create-group")
public class CreateGroupServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String userEmail = request.getParameter("email");
        String groupName = request.getParameter("groupName");
        String groupTheme = request.getParameter("groupTheme");
        response.setContentType("text/plain");
        PrintWriter out = response.getWriter();

        String jdbcUrl = "jdbc:mysql://localhost:3306/closer";
        String username = "root";
        String password = "password"; // Replace with your actual password

        String defaultImageUrl;
        switch (groupTheme.toLowerCase()) {
            case "family":
                defaultImageUrl = "http://10.0.2.2:8080/closer_war_exploded/images/family.jpg";
                break;
            case "friends":
                defaultImageUrl = "http://10.0.2.2:8080/closer_war_exploded/images/friends.jpg";
                break;
            case "romantic":
                defaultImageUrl = "http://10.0.2.2:8080/closer_war_exploded/images/romantic.jpg";
                break;
            default:
                defaultImageUrl = "http://10.0.2.2:8080/closer_war_exploded/images/default.jpg";
                break;
        }

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

                String insertGroupSql = "INSERT INTO UserGroups (group_name, theme, image_url, created_by) VALUES (?, ?, ?, ?)";
                statement = connection.prepareStatement(insertGroupSql, PreparedStatement.RETURN_GENERATED_KEYS);
                statement.setString(1, groupName);
                statement.setString(2, groupTheme);
                statement.setString(3, defaultImageUrl);
                statement.setInt(4, userId);
                System.out.println("Executing query: " + statement.toString());
                statement.executeUpdate();

                ResultSet generatedKeys = statement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int groupId = generatedKeys.getInt(1);

                    String insertMembershipSql = "INSERT INTO GroupMemberships (user_id, group_id) VALUES (?, ?)";
                    statement = connection.prepareStatement(insertMembershipSql);
                    statement.setInt(1, userId);
                    statement.setInt(2, groupId);
                    System.out.println("Executing query: " + statement.toString());
                    statement.executeUpdate();
                }

                out.print("Group created successfully");
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
