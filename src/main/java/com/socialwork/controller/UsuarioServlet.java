package com.socialwork.controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.socialwork.model.Usuario;
import com.socialwork.model.UsuarioDAO;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.BufferedReader;
import java.io.IOException;

@WebServlet("/api/usuarios/*")
public class UsuarioServlet extends HttpServlet {
    private final Gson gson = new Gson();
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        try {
            String path = req.getPathInfo();
            if ("/sugerencias".equals(path)) {
                int uid = (int) req.getSession().getAttribute("usuarioId");
                java.util.List<Usuario> sugs = usuarioDAO.findSuggestions(uid);
                sugs.forEach(u -> u.setPasswordHash(null));
                escribirJson(resp, 200, gson.toJson(sugs));
            } else if ("/todos".equals(path)) {
                java.util.List<Usuario> todos = usuarioDAO.findAll();
                todos.forEach(u -> u.setPasswordHash(null));
                escribirJson(resp, 200, gson.toJson(todos));
            } else if (path != null && path.startsWith("/")) {
                int id = Integer.parseInt(path.replace("/", ""));
                Usuario u = usuarioDAO.findById(id);
                if (u != null) { u.setPasswordHash(null); escribirJson(resp, 200, gson.toJson(u)); }
                else escribirJson(resp, 404, "{\"error\":\"No encontrado\"}");
            }
        } catch (Exception e) {
            escribirJson(resp, 500, "{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        try {
            StringBuilder sb = new StringBuilder();
            BufferedReader reader = req.getReader();
            String line;
            while ((line = reader.readLine()) != null) sb.append(line);
            Usuario u = gson.fromJson(sb.toString(), Usuario.class);

            Usuario existente = usuarioDAO.findById(u.getId());
            if (existente == null) {
                escribirJson(resp, 404, "{\"error\":\"Usuario no encontrado\"}");
                return;
            }
            existente.setNombre(u.getNombre());
            existente.setEmail(u.getEmail());
            existente.setRol(u.getRol());
            existente.setAvatar(u.getAvatar());
            usuarioDAO.update(existente);
            existente.setPasswordHash(null);
            escribirJson(resp, 200, gson.toJson(existente));
        } catch (Exception e) {
            escribirJson(resp, 500, "{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        try {
            int id = Integer.parseInt(req.getPathInfo().replace("/", ""));
            usuarioDAO.delete(id);
            escribirJson(resp, 200, "{\"success\":true}");
        } catch (Exception e) {
            escribirJson(resp, 500, "{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    private void escribirJson(HttpServletResponse resp, int status, String json) throws IOException {
        resp.setStatus(status);
        resp.getWriter().write(json);
    }
}
