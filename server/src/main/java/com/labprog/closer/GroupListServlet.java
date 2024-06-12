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

@WebServlet("/group-list")
public class GroupListServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String userEmail = request.getParameter("email");
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        String jdbcUrl = "jdbc:mysql://localhost:3306/closer";
        String username = "root";
        String password = "MyNewPass"; // Replace with your actual password

        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        List<Group> groups = new ArrayList<>();

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(jdbcUrl, username, password);
            System.out.println("Database connection established.");

            String sql = "SELECT ug.group_id, ug.group_name, ug.theme, ug.image_url " +
                    "FROM UserGroups ug " +
                    "JOIN GroupMemberships gm ON ug.group_id = gm.group_id " +
                    "JOIN Users u ON gm.user_id = u.user_id " +
                    "WHERE u.email = ?";
            statement = connection.prepareStatement(sql);
            statement.setString(1, userEmail);
            System.out.println("Executing query: " + statement.toString());
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int groupId = resultSet.getInt("group_id");
                String groupName = resultSet.getString("group_name");
                String theme = resultSet.getString("theme");
                String imageUrl = resultSet.getString("image_url");
                groups.add(new Group(groupId, groupName, theme, imageUrl));
            }

            if (groups.isEmpty()) {
                System.out.println("No groups found for the given user.");
            }

            Gson gson = new Gson();
            String json = gson.toJson(groups);
            out.print(json);

        } catch (ClassNotFoundException e) {
            e.printStackTrace(out);
            System.err.println("JDBC Driver not found.");
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\":\"JDBC Driver not found\"}");
        } catch (SQLException e) {
            e.printStackTrace(out);
            System.err.println("SQL Error: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\":\"SQL Error: " + e.getMessage() + "\"}");
        } catch (Exception e) {
            e.printStackTrace(out);
            System.err.println("Unknown Error: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\":\"Unknown Error: " + e.getMessage() + "\"}");
        } finally {
            try {
                if (resultSet != null) resultSet.close();
                if (statement != null) statement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace(out);
                System.err.println("Error closing resources: " + e.getMessage());
            }
        }
    }

    class Group {
        int groupId;
        String groupName;
        String theme;
        String imageUrl;

        Group(int groupId, String groupName, String theme, String imageUrl) {
            this.groupId = groupId;
            this.groupName = groupName;
            this.theme = theme;
            this.imageUrl = imageUrl;
        }
    }
}
