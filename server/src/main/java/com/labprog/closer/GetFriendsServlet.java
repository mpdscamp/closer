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

@WebServlet("/get-friends")
public class GetFriendsServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String userEmail = request.getParameter("email");
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        String jdbcUrl = "jdbc:mysql://localhost:3306/closer";
        String username = "root";
        String password = "password"; // Replace with your actual password

        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        List<User> friends = new ArrayList<>();

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(jdbcUrl, username, password);

            String sql = "SELECT u.username, u.email " +
                    "FROM Friendships f " +
                    "JOIN Users u ON (f.user1_id = u.user_id OR f.user2_id = u.user_id) " +
                    "JOIN Users u2 ON (u2.user_id = f.user1_id OR u2.user_id = f.user2_id) " +
                    "WHERE u2.email = ? AND u.email != ?";
            statement = connection.prepareStatement(sql);
            statement.setString(1, userEmail);
            statement.setString(2, userEmail);
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String userName = resultSet.getString("username");
                String email = resultSet.getString("email");
                friends.add(new User(userName, email));
            }

            Gson gson = new Gson();
            String json = gson.toJson(friends);
            out.print(json);

        } catch (ClassNotFoundException e) {
            e.printStackTrace(out);
        } catch (SQLException e) {
            e.printStackTrace(out);
        } finally {
            try {
                if (resultSet != null) resultSet.close();
                if (statement != null) statement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace(out);
            }
        }
    }

    class User {
        String username;
        String email;

        User(String username, String email) {
            this.username = username;
            this.email = email;
        }
    }
}
