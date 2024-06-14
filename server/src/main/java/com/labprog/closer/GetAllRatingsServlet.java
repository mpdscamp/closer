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

@WebServlet("/get-all-ratings")
public class GetAllRatingsServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        List<Rating> ratings = new ArrayList<>();

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DatabaseConnection.getConnection();

            String sql = "SELECT group_id, rating, user_rated, user_id FROM ratings";
            statement = connection.prepareStatement(sql);
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int groupId = resultSet.getInt("group_id");
                String rating = resultSet.getString("rating");
                String userRated = resultSet.getString("user_rated");
                int userId = resultSet.getInt("user_id");
                ratings.add(new Rating(groupId, rating, userRated, userId));
            }

            Gson gson = new Gson();
            String json = gson.toJson(ratings);
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

    class Rating {
        int groupId;
        String rating;
        String userRated;
        int userId;

        Rating(int groupId, String rating, String userRated, int userId) {
            this.groupId = groupId;
            this.rating = rating;
            this.userRated = userRated;
            this.userId = userId;
        }
    }
}
