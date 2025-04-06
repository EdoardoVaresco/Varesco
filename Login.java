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
import org.mindrot.jbcrypt.BCrypt;

public class Login extends HttpServlet {


    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        session.removeAttribute("loginError"); // Rimuove eventuali errori precedenti

        String email = request.getParameter("email").trim();
        String passwordInserita = request.getParameter("password");

        if (email.isEmpty() || passwordInserita.isEmpty()) {
            session.setAttribute("loginError", "Inserisci email e password.");
            response.sendRedirect("login.html");
            return;
        }

        try {
            try (Connection conn = DatabaseConnection.getConnection()) {

                // Query combinata per password e ruolo
                String loginQuery = "SELECT password, ruolo FROM utenti WHERE email=?";
                try (PreparedStatement stmt = conn.prepareStatement(loginQuery)) {
                    stmt.setString(1, email);

                    try (ResultSet rs = stmt.executeQuery()) {
                        if (rs.next()) {
                            String passwordSalvata = rs.getString("password");

                            // Confronto delle password con BCrypt
                            if (BCrypt.checkpw(passwordInserita, passwordSalvata)) {
                                System.out.println("Login riuscito!");
                                session.setAttribute("user", email);
                                String ruolo = rs.getString("ruolo");
                                session.setAttribute("ruolo", ruolo);

                                if (ruolo.equals("utente")) {
                                    response.sendRedirect("userDashboard.html");
                                } else {
                                    response.sendRedirect("adminDashboard.html");
                                }
                            } else {
                                session.setAttribute("loginError", "Credenziali errate.");
                                response.sendRedirect("login.html");
                            }
                        } else {
                            session.setAttribute("loginError", "Credenziali errate.");
                            response.sendRedirect("login.html");
                        }
                    }
                }
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            session.setAttribute("loginError", "Errore nel database.");
            response.sendRedirect("login.html");
        }
    }
}
