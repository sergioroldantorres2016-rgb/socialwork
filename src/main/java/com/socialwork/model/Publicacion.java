package com.socialwork.model;

public class Publicacion {
    private int id;
    private int usuarioId;
    private String contenido;
    private String createdAt;
    private String autorNombre;
    private String autorAvatar;

    public Publicacion() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getUsuarioId() { return usuarioId; }
    public void setUsuarioId(int usuarioId) { this.usuarioId = usuarioId; }
    public String getContenido() { return contenido; }
    public void setContenido(String contenido) { this.contenido = contenido; }
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    public String getAutorNombre() { return autorNombre; }
    public void setAutorNombre(String autorNombre) { this.autorNombre = autorNombre; }
    public String getAutorAvatar() { return autorAvatar; }
    public void setAutorAvatar(String autorAvatar) { this.autorAvatar = autorAvatar; }
}
