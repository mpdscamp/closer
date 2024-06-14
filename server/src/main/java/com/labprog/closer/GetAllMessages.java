package com.labprog.closer;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
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

@WebServlet("/get-messages-by-group-id")
public class GetAllMessages extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        List<Message> messages = new ArrayList<>();

        // Capturando o parâmetro group_id da requisição
        int groupId = Integer.parseInt(request.getParameter("group_id"));

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            // Estabelecendo a conexão com o banco de dados
            connection = DatabaseConnection.getConnection();

            // Consulta SQL para selecionar mensagens por group_id e incluir o username
            String sql = "SELECT i.group_id, i.image_url, i.user_id, u.username FROM images i JOIN Users u ON i.user_id = u.user_id WHERE i.group_id = ?";
            statement = connection.prepareStatement(sql);
            statement.setInt(1, groupId);
            resultSet = statement.executeQuery();

            // Processando os resultados da consulta
            while (resultSet.next()) {
                int groupIdResult = resultSet.getInt("group_id");
                String imageUrl = resultSet.getString("image_url");
                int userId = resultSet.getInt("user_id");
                String username = resultSet.getString("username");
                messages.add(new Message(groupIdResult, imageUrl, userId, username));
            }

            // Convertendo a lista de mensagens para JSON
            Gson gson = new Gson();
            String json = gson.toJson(messages);
            out.print(json);

        } catch (SQLException e) {
            e.printStackTrace(out);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } finally {
            // Fechando recursos
            try {
                if (resultSet != null) resultSet.close();
                if (statement != null) statement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace(out);
            }
        }
    }

    // Classe interna para representar uma mensagem
    class Message {
        int groupId;
        String imageUrl;
        int userId;
        String username;

        Message(int groupId, String imageUrl, int userId, String username) {
            this.groupId = groupId;
            this.imageUrl = imageUrl;
            this.userId = userId;
            this.username = username;
        }
    }
}
