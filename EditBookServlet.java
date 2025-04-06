package Servlet;

import DB.DatabaseConnection;
import org.json.JSONObject;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class EditBookServlet extends HttpServlet {

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Imposta il tipo di contenuto come JSON
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // Ottieni l'ID del libro da modificare dalla richiesta
        String bookId = request.getPathInfo().substring(1); // Rimuovi il '/' iniziale

        if (bookId == null || bookId.isEmpty()) {
            response.getWriter().write("{ \"error\": \"ID libro mancante\" }");
            return;
        }

        // Leggi i dati inviati con la richiesta (JSON)
        JSONObject requestData = new JSONObject(new InputStreamReader(request.getInputStream()));

        String titolo = requestData.optString("titolo");
        String autore = requestData.optString("autore");
        int annoPubblicazione = requestData.optInt("anno_pubblicazione");
        String genere = requestData.optString("genere");
        int copieDisponibili = requestData.optInt("copie_disponibili");

        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "UPDATE libri SET titolo = ?, autore = ?, anno_pubblicazione = ?, genere = ?, copie_disponibili = ? WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, titolo);
                stmt.setString(2, autore);
                stmt.setInt(3, annoPubblicazione);
                stmt.setString(4, genere);
                stmt.setInt(5, copieDisponibili);
                stmt.setInt(6, Integer.parseInt(bookId));

                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected > 0) {
                    response.getWriter().write("{ \"message\": \"Libro modificato con successo\" }");
                } else {
                    response.getWriter().write("{ \"error\": \"Libro non trovato\" }");
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            response.getWriter().write("{ \"error\": \"Errore nella modifica del libro\" }");
        }
    }
}
