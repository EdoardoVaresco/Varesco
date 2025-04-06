package Servlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class LogoutServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Ottieni la sessione esistente senza crearne una nuova
        HttpSession session = request.getSession(false);
        
        if (session != null) {
            session.invalidate(); // Distrugge la sessione
            System.out.println("Sessione distrutta, utente disconnesso.");
        }
        
        // Reindirizza l'utente alla pagina di login
        response.sendRedirect("index.html");
    }
}
