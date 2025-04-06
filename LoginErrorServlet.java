package Servlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class LoginErrorServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession();
        String errorMessage = (String) session.getAttribute("loginError");

        if (errorMessage != null) {
            response.getWriter().write(errorMessage);
            session.removeAttribute("LoginError"); // Pulisce l'errore dopo averlo inviato
        } else {
            response.getWriter().write(""); // Nessun errore presente
        }
    }
}
