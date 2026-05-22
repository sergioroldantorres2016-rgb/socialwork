package com.socialwork.model;

public class Comentario {
    private int id;
    private Integer publicacionId;
    private Integer casoId;
    private int usuarioId;
    private String contenido;
    private String createdAt;
    private String autorNombre;
    private String autorAvatar;

    public Comentario() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public Integer getPublicacionId() { return publicacionId; }
    public void setPublicacionId(Integer publicacionId) { this.publicacionId = publicacionId; }
    public Integer getCasoId() { return casoId; }
    public void setCasoId(Integer casoId) { this.casoId = casoId; }
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
