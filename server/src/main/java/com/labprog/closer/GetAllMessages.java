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

@WebServlet("/get-all-messages")
public class GetAllMessages extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        String jdbcUrl = "jdbc:mysql://localhost:3306/closer";
        String username = "root";
        String password = "password"; // Replace with your actual password

        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        List<Message> messages = new ArrayList<>();

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(jdbcUrl, username, password);

            String sql = "SELECT group_id, image_url, user_id FROM images";
            statement = connection.prepareStatement(sql);
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int groupId = resultSet.getInt("group_id");
                String imageUrl = resultSet.getString("image_url");
                int userId = resultSet.getInt("user_id");
                messages.add(new Message(groupId, imageUrl, userId));
            }

            Gson gson = new Gson();
            String json = gson.toJson(messages);
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

    class Message {
        int groupId;
        String imageUrl;
        int userId;

        Message(int groupId, String imageUrl, int userId) {
            this.groupId = groupId;
            this.imageUrl = imageUrl;
            this.userId = userId;
        }
    }
}