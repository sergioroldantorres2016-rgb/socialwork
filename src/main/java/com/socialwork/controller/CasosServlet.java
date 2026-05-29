package com.socialwork.controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.socialwork.model.Caso;
import com.socialwork.model.CasoDAO;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.BufferedReader;
import java.io.IOException;

@WebServlet("/api/casos/*")
public class CasosServlet extends HttpServlet {
    private final Gson gson = new Gson();
    private final CasoDAO casoDAO = new CasoDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        try {
            String path = req.getPathInfo();
            if (path == null || "/".equals(path)) {
                escribirJson(resp, 200, gson.toJson(casoDAO.findAll()));
            } else {
                int id = Integer.parseInt(path.replace("/", ""));
                Caso c = casoDAO.findById(id);
                if (c != null) escribirJson(resp, 200, gson.toJson(c));
                else escribirJson(resp, 404, "{\"error\":\"No encontrado\"}");
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

            Caso c = new Caso();
            c.setUsuarioId((int) req.getSession().getAttribute("usuarioId"));
            c.setTitulo(body.get("titulo").getAsString());
            c.setDescripcion(body.has("descripcion") ? body.get("descripcion").getAsString() : "");
            c.setUbicacion(body.has("ubicacion") ? body.get("ubicacion").getAsString() : "");
            c.setEstado(body.has("estado") ? body.get("estado").getAsString() : "activo");
            c = casoDAO.insert(c);
            escribirJson(resp, 201, gson.toJson(casoDAO.findById(c.getId())));
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
            Caso c = gson.fromJson(sb.toString(), Caso.class);
            c.setId(id);
            casoDAO.update(c);
            escribirJson(resp, 200, gson.toJson(casoDAO.findById(id)));
        } catch (Exception e) {
            escribirJson(resp, 500, "{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        try {
            int id = Integer.parseInt(req.getPathInfo().replace("/", ""));
            casoDAO.delete(id);
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
