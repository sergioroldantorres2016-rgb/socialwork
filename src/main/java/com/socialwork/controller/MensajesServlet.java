package com.socialwork.controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.socialwork.model.Mensaje;
import com.socialwork.model.MensajeDAO;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.BufferedReader;
import java.io.IOException;

@WebServlet("/api/mensajes/*")
public class MensajesServlet extends HttpServlet {
    private final Gson gson = new Gson();
    private final MensajeDAO mensajeDAO = new MensajeDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        try {
            String path = req.getPathInfo();
            if (path != null && path.startsWith("/conversacion/")) {
                int convId = Integer.parseInt(path.replace("/conversacion/", ""));
                escribirJson(resp, 200, gson.toJson(mensajeDAO.findByConversacion(convId)));
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

            Mensaje m = new Mensaje();
            m.setConversacionId(body.get("conversacion_id").getAsInt());
            m.setRemitenteId((int) req.getSession().getAttribute("usuarioId"));
            m.setContenido(body.get("contenido").getAsString());
            m = mensajeDAO.insert(m);
            escribirJson(resp, 201, gson.toJson(m));
        } catch (Exception e) {
            escribirJson(resp, 500, "{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    private void escribirJson(HttpServletResponse resp, int status, String json) throws IOException {
        resp.setStatus(status);
        resp.getWriter().write(json);
    }
}
