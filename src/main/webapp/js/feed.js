function cargarFeed() {
    var contenedor = document.getElementById('feed-container');
    if (!contenedor) return;
    contenedor.innerHTML = '<div class="loading">Cargando publicaciones...</div>';

    apiFetch('/publicaciones').then(function (posts) {
        contenedor.innerHTML = '';
        if (posts.length === 0) {
            contenedor.innerHTML = '<div class="loading">No hay publicaciones aún. ¡Sé el primero en compartir!</div>';
            return;
        }
        posts.forEach(function (p) {
            contenedor.appendChild(crearPostElement(p));
        });
        var statPub = document.getElementById('stat-publicaciones');
        if (statPub) statPub.textContent = posts.length;
    });
}

function crearPostElement(p) {
    var div = document.createElement('div');
    div.className = 'post';
    div.innerHTML = '<div class="header">'
        + '<div class="avatar">' + (p.autorAvatar || 'US') + '</div>'
        + '<div style="flex:1"><div class="name">' + (p.autorNombre || 'Usuario') + '</div>'
        + '<div class="meta">' + formatearFecha(p.createdAt) + '</div></div>'
        + '<button class="btn-eliminar" data-tipo="publicacion" data-id="' + p.id + '" title="Eliminar publicación" aria-label="Eliminar publicación">&times;</button>'
        + '</div>'
        + '<div class="content">' + escaparHTML(p.contenido) + '</div>'
        + '<div class="tags" id="tags-' + p.id + '"></div>'
        + '<div class="post-actions">'
        + '<span class="action-comentar" data-id="' + p.id + '">Comentar</span>'
        + '<span>Aportar experiencia</span>'
        + '<span>Guardar</span>'
        + '</div>'
        + '<div class="comentarios-seccion" id="comentarios-' + p.id + '" style="display:none">'
        + '<div class="comentarios-lista" id="comentarios-lista-' + p.id + '"></div>'
        + '<div class="comentario-input-wrapper"><input class="comentario-input" id="comentario-input-' + p.id + '" placeholder="Escribe un comentario..." aria-label="Escribe un comentario">'
        + '<button class="btn-comentar-enviar" data-id="' + p.id + '">Enviar</button></div>'
        + '</div>';

    var palabras = p.contenido.match(/#\w+/g) || [];
    var tagsEl = div.querySelector('#tags-' + p.id);
    if (palabras.length === 0) { tagsEl.style.display = 'none'; }
    else {
        palabras.forEach(function (t) {
            var span = document.createElement('span');
            span.textContent = t;
            tagsEl.appendChild(span);
        });
    }

    div.querySelector('.action-comentar').addEventListener('click', function () {
        toggleComentarios(p.id);
    });
    div.querySelector('.btn-comentar-enviar').addEventListener('click', function () {
        enviarComentarioPublicacion(p.id);
    });
    var input = div.querySelector('.comentario-input');
    input.addEventListener('keydown', function (e) {
        if (e.key === 'Enter') { e.preventDefault(); enviarComentarioPublicacion(p.id); }
    });

    var btnEliminar = div.querySelector('.btn-eliminar');
    if (btnEliminar) {
        btnEliminar.addEventListener('click', function () {
            if (confirm('¿Eliminar esta publicación?')) {
                apiFetch('/publicaciones/' + p.id, { method: 'DELETE' }).then(function () {
                    mostrarExito('Publicación eliminada');
                    cargarFeed();
                });
            }
        });
    }

    return div;
}

function toggleComentarios(postId) {
    var seccion = document.getElementById('comentarios-' + postId);
    if (!seccion) return;
    if (seccion.style.display === 'none') {
        seccion.style.display = 'block';
        cargarComentariosPublicacion(postId);
    } else {
        seccion.style.display = 'none';
    }
}

function cargarComentariosPublicacion(postId) {
    var lista = document.getElementById('comentarios-lista-' + postId);
    if (!lista) return;
    lista.innerHTML = '<div class="loading" style="padding:0.5rem 0;font-size:0.85rem">Cargando...</div>';

    apiFetch('/comentarios/publicacion/' + postId).then(function (comentarios) {
        lista.innerHTML = '';
        if (comentarios.length === 0) {
            lista.innerHTML = '<div style="padding:0.5rem 0;font-size:0.85rem;color:var(--text-muted)">Sin comentarios. ¡Sé el primero!</div>';
            return;
        }
        comentarios.forEach(function (c) {
            var div = document.createElement('div');
            div.className = 'comentario-item';
            div.innerHTML = '<div class="avatar" style="width:1.75rem;height:1.75rem;font-size:0.7rem;flex-shrink:0">' + (c.autorAvatar || 'US') + '</div>'
                + '<div style="flex:1"><div style="font-weight:600;font-size:0.85rem">' + c.autorNombre + '</div>'
                + '<div style="font-size:0.85rem">' + escaparHTML(c.contenido) + '</div></div>'
                + '<button class="btn-eliminar-comentario" data-id="' + c.id + '" data-postid="' + postId + '" title="Eliminar" aria-label="Eliminar comentario">&times;</button>';
            lista.appendChild(div);

            var btnDel = div.querySelector('.btn-eliminar-comentario');
            if (btnDel) {
                btnDel.addEventListener('click', function () {
                    if (confirm('¿Eliminar este comentario?')) {
                        apiFetch('/comentarios/' + c.id, { method: 'DELETE' }).then(function () {
                            mostrarExito('Comentario eliminado');
                            cargarComentariosPublicacion(postId);
                        });
                    }
                });
            }
        });
    });
}

function enviarComentarioPublicacion(postId) {
    var input = document.getElementById('comentario-input-' + postId);
    if (!input) return;
    var contenido = input.value.trim();
    if (!contenido) return;

    apiFetch('/comentarios', {
        method: 'POST',
        body: JSON.stringify({ publicacion_id: postId, contenido: contenido })
    }).then(function () {
        input.value = '';
        cargarComentariosPublicacion(postId);
        mostrarExito('Comentario enviado');
    });
}

function crearPublicacion(contenido) {
    if (!contenido || contenido.trim() === '') {
        mostrarError('Escribe algo para publicar');
        return;
    }
    apiFetch('/publicaciones', {
        method: 'POST',
        body: JSON.stringify({ contenido: contenido.trim() })
    }).then(function () {
        document.getElementById('post-input').value = '';
        cargarFeed();
        mostrarExito('Publicación creada');
    });
}

function escaparHTML(texto) {
    var d = document.createElement('div');
    d.textContent = texto;
    return d.innerHTML;
}

function formatearFecha(fechaStr) {
    if (!fechaStr) return '';
    var f = new Date(fechaStr + (fechaStr.includes('T') ? '' : 'T00:00:00'));
    if (isNaN(f.getTime())) return fechaStr;
    var ahora = new Date();
    var diffMs = ahora - f;
    var diffMin = Math.floor(diffMs / 60000);
    if (diffMin < 1) return 'Ahora';
    if (diffMin < 60) return 'Hace ' + diffMin + ' min';
    var diffHoras = Math.floor(diffMin / 60);
    if (diffHoras < 24) return 'Hace ' + diffHoras + 'h';
    var diffDias = Math.floor(diffHoras / 24);
    if (diffDias < 7) return 'Hace ' + diffDias + ' d\u00edas';
    return f.toLocaleDateString('es-ES');
}

(function () {
    var btnPublicar = document.getElementById('btn-publicar');
    var postInput = document.getElementById('post-input');
    if (btnPublicar && postInput) {
        btnPublicar.addEventListener('click', function () { crearPublicacion(postInput.value); });
        postInput.addEventListener('keydown', function (e) {
            if (e.key === 'Enter' && !e.shiftKey) { e.preventDefault(); crearPublicacion(postInput.value); }
        });
    }
})();
