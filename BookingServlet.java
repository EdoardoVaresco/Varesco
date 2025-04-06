import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.*;

public class BookingServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String email = request.getParameter("email"); // Email dell'utente
        int libroId = Integer.parseInt(request.getParameter("libro_id")); // ID del libro da prenotare

        // Connessione al database
        Connection conn = null;
        PreparedStatement pst = null;
        ResultSet rs = null;

        try {
            // 1. Connettersi al database
            String dbURL = "jdbc:mysql://localhost:3306/biblioteca_servlet"; // URL del database
            String dbUser = "root"; // Utente database
            String dbPass = "password"; // Password del database
            conn = DriverManager.getConnection(dbURL, dbUser, dbPass);

            // 2. Verifica se l'email esiste nella tabella degli utenti
            String queryUtente = "SELECT id FROM utenti WHERE email = ?";
            pst = conn.prepareStatement(queryUtente);
            pst.setString(1, email);
            rs = pst.executeQuery();

            if (rs.next()) {
                int utenteId = rs.getInt("id"); // Ottieni l'ID dell'utente

                // 3. Verifica se ci sono copie disponibili del libro
                String queryLibro = "SELECT copie_disponibili FROM libri WHERE id = ?";
                pst = conn.prepareStatement(queryLibro);
                pst.setInt(1, libroId);
                rs = pst.executeQuery();

                if (rs.next()) {
                    int copieDisponibili = rs.getInt("copie_disponibili");

                    if (copieDisponibili > 0) {
                        // 4. Prenotazione: inserisci nella tabella prenotazioni
                        String queryPrenotazione = "INSERT INTO prenotazioni (utente_id, libro_id) VALUES (?, ?)";
                        pst = conn.prepareStatement(queryPrenotazione);
                        pst.setInt(1, utenteId);
                        pst.setInt(2, libroId);
                        int result = pst.executeUpdate();

                        // 5. Se la prenotazione è avvenuta con successo, aggiorna il numero di copie disponibili
                        if (result > 0) {
                            String queryAggiornaCopie = "UPDATE libri SET copie_disponibili = copie_disponibili - 1 WHERE id = ?";
                            pst = conn.prepareStatement(queryAggiornaCopie);
                            pst.setInt(1, libroId);
                            pst.executeUpdate();

                            // Risposta di successo
                            response.getWriter().write("Prenotazione effettuata con successo.");
                        } else {
                            // Risposta in caso di errore nella prenotazione
                            response.getWriter().write("Errore nella prenotazione. Riprova.");
                        }
                    } else {
                        // Risposta se non ci sono copie disponibili
                        response.getWriter().write("Non ci sono copie disponibili per questo libro.");
                    }
                }
            } else {
                // Risposta se l'utente non esiste
                response.getWriter().write("Utente non trovato.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            response.getWriter().write("Errore di connessione al database.");
        } finally {
            try {
                if (rs != null) rs.close();
                if (pst != null) pst.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
