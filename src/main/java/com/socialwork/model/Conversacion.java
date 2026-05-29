package com.socialwork.model;

import java.util.List;

public class Conversacion {
    private int id;
    private String createdAt;
    private List<ParticipanteInfo> participantes;
    private Mensaje ultimoMensaje;

    public Conversacion() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    public List<ParticipanteInfo> getParticipantes() { return participantes; }
    public void setParticipantes(List<ParticipanteInfo> participantes) { this.participantes = participantes; }
    public Mensaje getUltimoMensaje() { return ultimoMensaje; }
    public void setUltimoMensaje(Mensaje ultimoMensaje) { this.ultimoMensaje = ultimoMensaje; }

    public static class ParticipanteInfo {
        private int usuarioId;
        private String nombre;
        private String avatar;

        public int getUsuarioId() { return usuarioId; }
        public void setUsuarioId(int usuarioId) { this.usuarioId = usuarioId; }
        public String getNombre() { return nombre; }
        public void setNombre(String nombre) { this.nombre = nombre; }
        public String getAvatar() { return avatar; }
        public void setAvatar(String avatar) { this.avatar = avatar; }
    }
}
