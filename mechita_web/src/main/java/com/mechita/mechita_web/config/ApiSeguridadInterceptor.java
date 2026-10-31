package com.mechita.mechita_web.config;

import com.mechita.mechita_web.model.Usuario;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class ApiSeguridadInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String ruta = request.getRequestURI();
        boolean lecturaPublica = "GET".equals(request.getMethod())
                && (ruta.startsWith("/api/productos") || ruta.startsWith("/api/categorias"));
        if (lecturaPublica) {
            return true;
        }

        HttpSession session = request.getSession(false);
        Usuario usuario = session == null ? null : (Usuario) session.getAttribute("usuarioLogueado");
        if (usuario != null && usuario.esAdmin()) {
            return true;
        }

        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"status\":403,\"mensaje\":\"Acceso restringido a administradores\"}");
        return false;
    }
}
