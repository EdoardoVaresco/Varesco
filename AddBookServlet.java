package Servlet;

import DB.DatabaseConnection;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class AddBookServlet extends HttpServlet {
    
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {


        String titolo = request.getParameter("titolo").trim();
        String autore = request.getParameter("autore").trim();
        String anno_pubblicazione = request.getParameter("anno_pubblicazione").trim();
        String genere = request.getParameter("genere").trim();  
        String copie_disponibili = request.getParameter("copie_disponibili").trim(); 

        try (Connection conn = DatabaseConnection.getConnection()) {
            // Step 1: Controlla se il libro esiste già
            String checkQuery = "SELECT COUNT(*) FROM libri WHERE titolo = ? AND autore = ?";
            try (PreparedStatement checkStmt = conn.prepareStatement(checkQuery)) {
                checkStmt.setString(1, titolo);
                checkStmt.setString(2, autore);
                ResultSet rs = checkStmt.executeQuery();
                
                if (rs.next() && rs.getInt(1) > 0) {
                    // Libro già esistente → Redirect con errore in query string
                    response.sendRedirect("adminDashboard.html?error=Il+libro+esiste+già+nel+database!");
                    return;
                }
            }

            // Step 2: Inserisce il nuovo libro
            String insertQuery = "INSERT INTO libri (titolo, autore, anno_pubblicazione, genere, copie_disponibili) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(insertQuery)) {
                stmt.setString(1, titolo);
                stmt.setString(2, autore);
                stmt.setString(3, anno_pubblicazione);
                stmt.setString(4, genere);
                stmt.setString(5, copie_disponibili);

                stmt.executeUpdate();
                // Redirect con messaggio di successo
                response.sendRedirect("adminDashboard.html?message=Libro+aggiunto+con+successo.");
            }

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            response.sendRedirect("adminDashboard.html?error=Errore+del+database.");
        }
    }
}
