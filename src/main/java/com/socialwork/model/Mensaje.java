package com.socialwork.model;

public class Mensaje {
    private int id;
    private int conversacionId;
    private int remitenteId;
    private String contenido;
    private String createdAt;
    private String remitenteNombre;
    private String remitenteAvatar;

    public Mensaje() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getConversacionId() { return conversacionId; }
    public void setConversacionId(int conversacionId) { this.conversacionId = conversacionId; }
    public int getRemitenteId() { return remitenteId; }
    public void setRemitenteId(int remitenteId) { this.remitenteId = remitenteId; }
    public String getContenido() { return contenido; }
    public void setContenido(String contenido) { this.contenido = contenido; }
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    public String getRemitenteNombre() { return remitenteNombre; }
    public void setRemitenteNombre(String remitenteNombre) { this.remitenteNombre = remitenteNombre; }
    public String getRemitenteAvatar() { return remitenteAvatar; }
    public void setRemitenteAvatar(String remitenteAvatar) { this.remitenteAvatar = remitenteAvatar; }
}
