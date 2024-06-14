package com.labprog.closer;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/test-db-connection")
public class TestDatabaseConnectionServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DatabaseConnection.getConnection();
            out.println("Connection successful!");
            conn.close();
        } catch (ClassNotFoundException e) {
            out.println("JDBC Driver not found.");
            e.printStackTrace(out);
        } catch (SQLException e) {
            out.println("Connection failed.");
            e.printStackTrace(out);
        } finally {
            out.close();
        }
    }
}
