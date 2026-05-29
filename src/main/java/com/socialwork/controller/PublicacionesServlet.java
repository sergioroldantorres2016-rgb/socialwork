package com.socialwork.controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.socialwork.model.Publicacion;
import com.socialwork.model.PublicacionDAO;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.BufferedReader;
import java.io.IOException;

@WebServlet("/api/publicaciones/*")
public class PublicacionesServlet extends HttpServlet {
    private final Gson gson = new Gson();
    private final PublicacionDAO publicacionDAO = new PublicacionDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        try {
            String path = req.getPathInfo();
            if (path == null || "/".equals(path)) {
                escribirJson(resp, 200, gson.toJson(publicacionDAO.findAll()));
            } else {
                int id = Integer.parseInt(path.replace("/", ""));
                Publicacion p = publicacionDAO.findById(id);
                if (p != null) escribirJson(resp, 200, gson.toJson(p));
                else escribirJson(resp, 404, "{\"error\":\"No encontrada\"}");
            }
        } catch (Exception e) {
            escribirJson(resp, 500, "{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        try {
            StringBuilder sb = new StringBuilder();
            BufferedReader reader = req.getReader();
            String line;
            while ((line = reader.readLine()) != null) sb.append(line);
            JsonObject body = gson.fromJson(sb.toString(), JsonObject.class);

            Publicacion p = new Publicacion();
            p.setUsuarioId((int) req.getSession().getAttribute("usuarioId"));
            p.setContenido(body.get("contenido").getAsString());
            p = publicacionDAO.insert(p);
            p = publicacionDAO.findById(p.getId());
            escribirJson(resp, 201, gson.toJson(p));
        } catch (Exception e) {
            escribirJson(resp, 500, "{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        try {
            int id = Integer.parseInt(req.getPathInfo().replace("/", ""));
            StringBuilder sb = new StringBuilder();
            BufferedReader reader = req.getReader();
            String line;
            while ((line = reader.readLine()) != null) sb.append(line);
            JsonObject body = gson.fromJson(sb.toString(), JsonObject.class);
            Publicacion p = publicacionDAO.findById(id);
            if (p == null) { escribirJson(resp, 404, "{\"error\":\"No encontrada\"}"); return; }
            p.setContenido(body.get("contenido").getAsString());
            publicacionDAO.update(p);
            escribirJson(resp, 200, gson.toJson(publicacionDAO.findById(id)));
        } catch (Exception e) {
            escribirJson(resp, 500, "{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        try {
            int id = Integer.parseInt(req.getPathInfo().replace("/", ""));
            publicacionDAO.delete(id);
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
