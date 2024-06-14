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

@WebServlet("/get-group-members")
public class GetGroupMembersServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String groupId = request.getParameter("groupId");
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        List<String> groupMembers = new ArrayList<>();

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DatabaseConnection.getConnection();

            String sql = "SELECT u.email FROM Users u JOIN GroupMemberships gm ON u.user_id = gm.user_id WHERE gm.group_id = ?";
            statement = connection.prepareStatement(sql);
            statement.setInt(1, Integer.parseInt(groupId));
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String email = resultSet.getString("email");
                groupMembers.add(email);
            }

            Gson gson = new Gson();
            String json = gson.toJson(groupMembers);
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
}
