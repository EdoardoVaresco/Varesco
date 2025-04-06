package Servlet;

import DB.DatabaseConnection;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class GetBooksServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            // Ottieni i dati formattati come stringa JSON
            String jsonResponse = getBooksAsText();
            response.getWriter().write(jsonResponse);
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(new JSONObject().put("error", "Errore interno del server").toString());
        }
    }

    private String getBooksAsText() throws SQLException, ClassNotFoundException {
        ArrayList<HashMap<String, Object>> books = new ArrayList<>();

        // Connessione al DB
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM libri";
            try (PreparedStatement stmt = conn.prepareStatement(query);
                 ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    HashMap<String, Object> book = new HashMap<>();
                    book.put("id", rs.getInt("id"));
                    book.put("titolo", rs.getString("titolo"));
                    book.put("autore", rs.getString("autore"));
                    book.put("anno_pubblicazione", rs.getDate("anno_pubblicazione"));
                    book.put("genere", rs.getString("genere"));
                    book.put("copie_disponibili", rs.getInt("copie_disponibili"));
                    books.add(book);
                }
            }
        }

        // Se non ci sono libri, restituisci un messaggio
        if (books.isEmpty()) {
            return new JSONObject().put("message", "Nessun libro trovato").toString();
        }

        // Crea un JSONArray a partire dalla lista dei libri
        JSONArray booksJsonArray = new JSONArray(books);
        return booksJsonArray.toString();
    }
}