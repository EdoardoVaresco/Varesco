package Servlet;

import DB.DatabaseConnection;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.regex.Pattern;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.mindrot.jbcrypt.BCrypt;  


public class Signup extends HttpServlet {


    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        session.removeAttribute("signupError"); // Pulisce eventuali errori precedenti

        String firstName = request.getParameter("firstName").trim();
        String lastName = request.getParameter("lastName").trim();
        String email = request.getParameter("email").trim();
        String passwordInput = request.getParameter("password");  // Rinomina la variabile locale per evitare conflitto
        String nome = firstName + " " + lastName;

        // Validazione lato server
        String errorMessage = validateInputs(firstName, lastName, email, passwordInput);

        if (!errorMessage.isEmpty()) {
            session.setAttribute("signupError", errorMessage);
            response.sendRedirect("signup.html");
            return;
        }

        try {
            try (Connection conn = DatabaseConnection.getConnection()) {
                String hashedPassword = BCrypt.hashpw(passwordInput, BCrypt.gensalt(12));  // Usa passwordInput

                // Inserimento dell'utente nel database
                String insertQuery = "INSERT INTO utenti (nome, email, password) VALUES (?, ?, ?)";
                try (PreparedStatement stmt = conn.prepareStatement(insertQuery)) {
                    stmt.setString(1, nome);
                    stmt.setString(2, email);
                    stmt.setString(3, hashedPassword);

                    stmt.executeUpdate();
                    response.sendRedirect("login.html");
                } catch (SQLException e) {
                    // Gestisci l'errore, ad esempio, se la violazione della chiave univoca
                    if (e.getErrorCode() == 1062) {  // Codice errore MySQL per violazione chiave unica
                        session.setAttribute("signupError", "L'email è già registrata.");
                        response.sendRedirect("signup.html");
                        return;
                    }
                    // Altri tipi di errore
                    session.setAttribute("signupError", "Errore nel database.");
                    response.sendRedirect("signup.html");
                }
            }
        } catch (ClassNotFoundException | SQLException e) {
            session.setAttribute("signupError", "Errore nel database.");
            response.sendRedirect("signup.html");
        }
    }

    private String validateInputs(String firstName, String lastName, String email, String password) {
        String nameRegex = "^[A-Z][a-zA-Z]*$";
        String emailRegex = "^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$";
        String passwordRegex = "^(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*]).{8,}$";

        String error = "";

        if (!Pattern.matches(nameRegex, firstName)) {
            error += "Il nome deve iniziare con una maiuscola e contenere solo lettere.";
        }
        if (!Pattern.matches(nameRegex, lastName)) {
            error += "Il cognome deve iniziare con una maiuscola e contenere solo lettere.";
        }
        if (!Pattern.matches(emailRegex, email)) {
            error += "Inserisci un'email valida.";
        }
        if (!Pattern.matches(passwordRegex, password)) {
            error += "La password deve contenere almeno 8 caratteri, una maiuscola, un numero e un carattere speciale.";
        }
        return error;
    }
}
