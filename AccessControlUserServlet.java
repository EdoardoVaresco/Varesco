package Servlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class AccessControlUserServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false); // Non creare una nuova sessione se non esiste

        if (session != null && session.getAttribute("user") != null) {
            String role = (String) session.getAttribute("role");

            if (!role.equals("utente")) {
                response.sendRedirect("adminDashboard.html");
            }
        } else {
            response.sendRedirect("login.html");
        }
    }

}