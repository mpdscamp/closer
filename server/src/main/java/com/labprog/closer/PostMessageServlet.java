package com.labprog.closer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/post-message")
public class PostMessageServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        // Leitura do corpo da requisição
        StringBuilder sb = new StringBuilder();
        BufferedReader reader = request.getReader();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        String requestBody = sb.toString();

        // Parse do JSON
        JsonObject jsonObject = JsonParser.parseString(requestBody).getAsJsonObject();
        String userEmail = jsonObject.get("email").getAsString();
        int groupId = jsonObject.get("groupId").getAsInt();
        String imageUrl = jsonObject.get("imageUrl").getAsString();
        int challengeId = 1;

        Connection connection = null;
        PreparedStatement statement = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DatabaseConnection.getConnection();
            System.out.println("Database connection established.");

            String getUserSql = "SELECT user_id FROM Users WHERE email = ?";
            statement = connection.prepareStatement(getUserSql);
            statement.setString(1, userEmail);
            System.out.println("Executing query: " + statement.toString());
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                int userId = rs.getInt("user_id");

                String insertImageSql = "INSERT INTO Images (group_id, user_id, challenge_id, image_url, submitted_at) VALUES (?, ?, ?, ?, NOW())";
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
