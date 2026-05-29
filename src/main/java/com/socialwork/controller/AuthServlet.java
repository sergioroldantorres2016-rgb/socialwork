package com.socialwork.controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.socialwork.model.Usuario;
import com.socialwork.model.UsuarioDAO;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.BufferedReader;
import java.io.IOException;

@WebServlet("/api/auth/*")
public class AuthServlet extends HttpServlet {
    private final Gson gson = new Gson();
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        String path = req.getPathInfo();

        try {
            if ("/login".equals(path)) {
                handleLogin(req, resp);
            } else if ("/register".equals(path)) {
                handleRegister(req, resp);
            } else if ("/logout".equals(path)) {
                req.getSession().invalidate();
                escribirJson(resp, 200, "{\"success\":true}");
            } else {
                escribirJson(resp, 404, "{\"error\":\"Ruta no encontrada\"}");
            }
        } catch (Exception e) {
            escribirJson(resp, 500, "{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        HttpSession session = req.getSession(false);
        if (session != null && session.getAttribute("usuarioId") != null) {
            try {
                Usuario u = usuarioDAO.findById((int) session.getAttribute("usuarioId"));
                if (u != null) {
                    u.setPasswordHash(null);
                    escribirJson(resp, 200, gson.toJson(u));
                    return;
                }
            } catch (Exception e) {}
        }
        escribirJson(resp, 401, "{\"error\":\"No autenticado\"}");
    }

    private void handleLogin(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        JsonObject body = leerBody(req);
        String email = body.get("email").getAsString();
        String password = body.get("password").getAsString();

        Usuario u = usuarioDAO.findByEmail(email);
        if (u == null || !u.getPasswordHash().equals(password)) {
            escribirJson(resp, 401, "{\"error\":\"Credenciales inválidas\"}");
            return;
        }
        req.getSession().setAttribute("usuarioId", u.getId());
        u.setPasswordHash(null);
        escribirJson(resp, 200, gson.toJson(u));
    }

    private void handleRegister(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        JsonObject body = leerBody(req);
        Usuario u = new Usuario();
        u.setNombre(body.get("nombre").getAsString());
        u.setEmail(body.get("email").getAsString());
        u.setPasswordHash(body.get("password").getAsString());
        u.setRol(body.has("rol") ? body.get("rol").getAsString() : "Trabajador social");
        String ini = u.getNombre().substring(0, 1).toUpperCase();
        int idx = u.getNombre().indexOf(" ");
        String ap = idx > 0 ? u.getNombre().substring(idx + 1, idx + 2).toUpperCase() : "";
        u.setAvatar(ini + ap);

        u = usuarioDAO.insert(u);
        req.getSession().setAttribute("usuarioId", u.getId());
        u.setPasswordHash(null);
        escribirJson(resp, 201, gson.toJson(u));
    }

    private JsonObject leerBody(HttpServletRequest req) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader reader = req.getReader();
        String line;
        while ((line = reader.readLine()) != null) sb.append(line);
        return gson.fromJson(sb.toString(), JsonObject.class);
    }

    private void escribirJson(HttpServletResponse resp, int status, String json) throws IOException {
        resp.setStatus(status);
        resp.getWriter().write(json);
    }
}
