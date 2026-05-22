package com.socialwork.controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.socialwork.model.Comentario;
import com.socialwork.model.ComentarioDAO;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.BufferedReader;
import java.io.IOException;

@WebServlet("/api/comentarios/*")
public class ComentariosServlet extends HttpServlet {
    private final Gson gson = new Gson();
    private final ComentarioDAO comentarioDAO = new ComentarioDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        try {
            String path = req.getPathInfo();
            if (path != null && path.startsWith("/publicacion/")) {
                int pubId = Integer.parseInt(path.replace("/publicacion/", ""));
                escribirJson(resp, 200, gson.toJson(comentarioDAO.findByPublicacion(pubId)));
            } else if (path != null && path.startsWith("/caso/")) {
                int casoId = Integer.parseInt(path.replace("/caso/", ""));
                escribirJson(resp, 200, gson.toJson(comentarioDAO.findByCaso(casoId)));
            } else {
                escribirJson(resp, 404, "{\"error\":\"Ruta no válida\"}");
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

            Comentario c = new Comentario();
            c.setUsuarioId((int) req.getSession().getAttribute("usuarioId"));
            c.setContenido(body.get("contenido").getAsString());
            if (body.has("publicacion_id")) {
                c.setPublicacionId(body.get("publicacion_id").getAsInt());
            } else if (body.has("caso_id")) {
                c.setCasoId(body.get("caso_id").getAsInt());
            } else {
                escribirJson(resp, 400, "{\"error\":\"Falta publicacion_id o caso_id\"}");
                return;
            }
            c = comentarioDAO.insert(c);
            escribirJson(resp, 201, gson.toJson(c));
        } catch (Exception e) {
            escribirJson(resp, 500, "{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        try {
            int id = Integer.parseInt(req.getPathInfo().replace("/", ""));
            comentarioDAO.delete(id);
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
