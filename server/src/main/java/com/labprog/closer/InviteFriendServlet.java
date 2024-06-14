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

@WebServlet("/invite-friend")
public class InviteFriendServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String userEmail = request.getParameter("email");
        String friendEmail = request.getParameter("friendEmail");
        response.setContentType("text/plain");
        PrintWriter out = response.getWriter();
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DatabaseConnection.getConnection();

            // Get user IDs for both users
            String getUserSql = "SELECT user_id FROM Users WHERE email = ?";
            statement = connection.prepareStatement(getUserSql);
            statement.setString(1, userEmail);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                int userId1 = rs.getInt("user_id");

                statement.setString(1, friendEmail);
                rs = statement.executeQuery();
                if (rs.next()) {
                    int userId2 = rs.getInt("user_id");

                    // Check if there is an existing invite from the friend
                    String checkInviteSql = "SELECT invite_id FROM FriendInvites WHERE user1_id = ? AND user2_id = ?";
                    PreparedStatement checkInviteStmt = connection.prepareStatement(checkInviteSql);
                    checkInviteStmt.setInt(1, userId2);
                    checkInviteStmt.setInt(2, userId1);
                    ResultSet inviteRs = checkInviteStmt.executeQuery();

                    if (inviteRs.next()) {
                        // If there is an invite, create a friendship and delete both invites
                        String insertFriendshipSql = "INSERT INTO Friendships (user1_id, user2_id) VALUES (?, ?)";
                        PreparedStatement insertFriendshipStmt = connection.prepareStatement(insertFriendshipSql);
                        insertFriendshipStmt.setInt(1, userId1);
                        insertFriendshipStmt.setInt(2, userId2);
                        insertFriendshipStmt.executeUpdate();

                        String deleteInvitesSql = "DELETE FROM FriendInvites WHERE (user1_id = ? AND user2_id = ?) OR (user1_id = ? AND user2_id = ?)";
                        PreparedStatement deleteInvitesStmt = connection.prepareStatement(deleteInvitesSql);
                        deleteInvitesStmt.setInt(1, userId1);
                        deleteInvitesStmt.setInt(2, userId2);
                        deleteInvitesStmt.setInt(3, userId2);
                        deleteInvitesStmt.setInt(4, userId1);
                        deleteInvitesStmt.executeUpdate();

                        out.print("Convites aceitos automaticamente, agora vocês são amigos!");
                    } else {
                        // Insert the new invite
                        String inviteSql = "INSERT INTO FriendInvites (user1_id, user2_id) VALUES (?, ?)";
                        statement = connection.prepareStatement(inviteSql);
                        statement.setInt(1, userId1);
                        statement.setInt(2, userId2);
                        statement.executeUpdate();

                        out.print("Convite enviado com sucesso!");
                    }
                } else {
                    out.print("Friend user not found");
                }
            } else {
                out.print("User not found");
            }

        } catch (ClassNotFoundException e) {
            e.printStackTrace(out);
        } catch (SQLException e) {
            e.printStackTrace(out);
        } finally {
            try {
                if (statement != null) statement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace(out);
            }
        }
    }
}
