package com.socialwork.controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.socialwork.model.Conexion;
import com.socialwork.model.ConexionDAO;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.BufferedReader;
import java.io.IOException;

@WebServlet("/api/conexiones/*")
public class ConexionesServlet extends HttpServlet {
    private final Gson gson = new Gson();
    private final ConexionDAO conexionDAO = new ConexionDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        try {
            int uid = (int) req.getSession().getAttribute("usuarioId");
            String path = req.getPathInfo();
            if ("/contar".equals(path)) {
                int count = conexionDAO.countConexiones(uid);
                escribirJson(resp, 200, "{\"conteo\":" + count + "}");
            } else {
                escribirJson(resp, 200, gson.toJson(conexionDAO.findConexiones(uid)));
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

            Conexion cx = new Conexion();
            cx.setSeguidorId((int) req.getSession().getAttribute("usuarioId"));
            cx.setSeguidoId(body.get("seguido_id").getAsInt());
            cx = conexionDAO.insert(cx);
            escribirJson(resp, 201, gson.toJson(cx));
        } catch (Exception e) {
            escribirJson(resp, 500, "{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        try {
            int uid = (int) req.getSession().getAttribute("usuarioId");
            int seguidoId = Integer.parseInt(req.getPathInfo().replace("/", ""));
            conexionDAO.delete(uid, seguidoId);
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
