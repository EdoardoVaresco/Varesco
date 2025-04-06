package Servlet;

import DB.DatabaseConnection;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DeleteBookServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String bookId = request.getParameter("id"); // Ottieni l'ID del libro dalla query string

        if (bookId == null || bookId.trim().isEmpty()) {
            response.getWriter().write("{ \"error\": \"ID libro mancante\" }");
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            // 1️⃣ Controlla se il libro ha prenotazioni attive
            if (haPrenotazioniAttive(conn, Integer.parseInt(bookId))) {
                response.getWriter().write("{ \"error\": \"Impossibile eliminare: il libro ha prenotazioni attive\" }");
                return;
            }

            // 2️⃣ Procedi con l'eliminazione se non ha prenotazioni
            String deleteQuery = "DELETE FROM libri WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(deleteQuery)) {
                stmt.setInt(1, Integer.parseInt(bookId));

                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected > 0) {
                    response.getWriter().write("{ \"message\": \"Libro eliminato con successo\" }");
                } else {
                    response.getWriter().write("{ \"error\": \"Libro non trovato\" }");
                }
            }

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            response.getWriter().write("{ \"error\": \"Errore nell'eliminazione del libro\" }");
        }
    }

    // Metodo per verificare se un libro ha prenotazioni attive
    private boolean haPrenotazioniAttive(Connection conn, int bookId) throws SQLException {
        String checkQuery = "SELECT COUNT(*) FROM prenotazioni WHERE libro_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(checkQuery)) {
            stmt.setInt(1, bookId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0; // Se ci sono prenotazioni, restituisce true
            }
        }
        return false;
    }
}
