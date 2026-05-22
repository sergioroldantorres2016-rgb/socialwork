var casoActual = null;

function cargarCasos() {
    var contenedor = document.getElementById('casos-container');
    if (!contenedor) return;
    contenedor.innerHTML = '<div class="loading">Cargando casos...</div>';

    apiFetch('/casos').then(function (casos) {
        contenedor.innerHTML = '';
        if (casos.length === 0) {
            contenedor.innerHTML = '<div class="loading">No hay casos registrados.</div>';
            return;
        }
        casos.forEach(function (c) {
            contenedor.appendChild(crearCasoCard(c));
        });
        var activos = casos.filter(function (c) { return c.estado === 'activo'; }).length;
        var statCasos = document.getElementById('stat-casos');
        if (statCasos) statCasos.textContent = activos;
    });
}

function crearCasoCard(c) {
    var div = document.createElement('div');
    div.className = 'case-card';
    var labels = { activo: 'Activo', seguimiento: 'Seguimiento', cerrado: 'Cerrado' };

    div.innerHTML = '<div style="display:flex;justify-content:space-between;align-items:start">'
        + '<div class="title">' + escaparHTML(c.titulo) + '</div>'
        + '<button class="btn-eliminar" data-tipo="caso" data-id="' + c.id + '" title="Eliminar caso" aria-label="Eliminar caso">&times;</button>'
        + '</div>'
        + '<div class="description">' + escaparHTML(c.descripcion || 'Sin descripción') + '</div>'
        + '<div class="meta">'
        + (c.ubicacion ? '<span>\uD83D\uDCCD ' + escaparHTML(c.ubicacion) + '</span>' : '')
        + '<span>Apertura: ' + formatearFecha(c.createdAt) + '</span>'
        + '</div>'
        + '<div class="status ' + c.estado + '">' + (labels[c.estado] || c.estado) + '</div>'
        + '<button type="button" class="btn-ver-caso" data-id="' + c.id + '">' + (c.estado === 'cerrado' ? 'Ver resumen' : 'Ver caso') + '</button>';

    div.querySelector('.btn-ver-caso').addEventListener('click', function () {
        mostrarDetalleCaso(c.id);
    });

    var btnEliminar = div.querySelector('.btn-eliminar');
    if (btnEliminar) {
        btnEliminar.addEventListener('click', function () {
            if (confirm('¿Eliminar el caso "' + c.titulo + '"?')) {
                apiFetch('/casos/' + c.id, { method: 'DELETE' }).then(function () {
                    mostrarExito('Caso eliminado');
                    cargarCasos();
                });
            }
        });
    }

    return div;
}

function mostrarDetalleCaso(casoId) {
    casoActual = casoId;
    document.querySelectorAll('.section').forEach(function (s) { s.classList.remove('active'); });
    document.getElementById('view-caso-detalle').classList.add('active');

    var contenedor = document.getElementById('caso-detalle-container');
    contenedor.innerHTML = '<div class="loading">Cargando detalle...</div>';

    apiFetch('/casos/' + casoId).then(function (c) {
        var labels = { activo: 'Activo', seguimiento: 'Seguimiento', cerrado: 'Cerrado' };
        contenedor.innerHTML = '<div class="post">'
            + '<div style="display:flex;justify-content:space-between;align-items:start">'
            + '<div class="title" style="font-size:1.2rem">' + escaparHTML(c.titulo) + '</div>'
            + '<div style="display:flex;gap:0.5rem;align-items:center">'
            + '<div class="status ' + c.estado + '" style="flex-shrink:0">' + (labels[c.estado] || c.estado) + '</div>'
            + '<button class="btn-eliminar" data-tipo="caso-detalle" data-id="' + c.id + '" title="Eliminar este caso" aria-label="Eliminar caso">&times;</button>'
            + '</div>'
            + '</div>'
            + '<div style="font-size:0.95rem;line-height:1.6">' + escaparHTML(c.descripcion || 'Sin descripción') + '</div>'
            + '<div class="meta">'
            + (c.ubicacion ? '<span>\uD83D\uDCCD ' + escaparHTML(c.ubicacion) + '</span>' : '')
            + '<span>Apertura: ' + formatearFecha(c.createdAt) + '</span>'
            + '<span>\u00DAltima actualizaci\u00F3n: ' + formatearFecha(c.updatedAt) + '</span>'
            + '</div>'
            + '<hr style="border:none;border-top:1px solid var(--border);margin:1rem 0">'
            + '<div style="font-weight:600;margin-bottom:0.75rem">Comentarios del caso</div>'
            + '<div id="caso-comentarios-lista"></div>'
            + '<div class="comentario-input-wrapper" style="margin-top:0.75rem">'
            + '<input class="comentario-input" id="caso-comentario-input" placeholder="Aporta tu experiencia sobre este caso..." aria-label="Comentar caso">'
            + '<button class="btn-comentar-enviar" id="btn-caso-comentar">Enviar</button>'
            + '</div>'
            + '</div>';

        cargarComentariosCaso(casoId);

        document.getElementById('btn-caso-comentar').addEventListener('click', function () {
            enviarComentarioCaso(casoId);
        });
        document.getElementById('caso-comentario-input').addEventListener('keydown', function (e) {
            if (e.key === 'Enter') { e.preventDefault(); enviarComentarioCaso(casoId); }
        });

        var btnEliminarCaso = contenedor.querySelector('.btn-eliminar');
        if (btnEliminarCaso) {
            btnEliminarCaso.addEventListener('click', function () {
                if (confirm('¿Eliminar el caso "' + c.titulo + '"?')) {
                    apiFetch('/casos/' + c.id, { method: 'DELETE' }).then(function () {
                        mostrarExito('Caso eliminado');
                        document.getElementById('btn-volver-casos').click();
                        cargarCasos();
                    });
                }
            });
        }
    });
}

function cargarComentariosCaso(casoId) {
    var lista = document.getElementById('caso-comentarios-lista');
    if (!lista) return;
    lista.innerHTML = '<div class="loading" style="padding:0.5rem 0;font-size:0.85rem">Cargando comentarios...</div>';

    apiFetch('/comentarios/caso/' + casoId).then(function (comentarios) {
        lista.innerHTML = '';
        if (comentarios.length === 0) {
            lista.innerHTML = '<div style="padding:0.5rem 0;font-size:0.85rem;color:var(--text-muted)">Aún no hay comentarios en este caso. Aporta tu experiencia.</div>';
            return;
        }
        comentarios.forEach(function (c) {
            var div = document.createElement('div');
            div.className = 'comentario-item';
            div.innerHTML = '<div class="avatar" style="width:1.75rem;height:1.75rem;font-size:0.7rem;flex-shrink:0">' + (c.autorAvatar || 'US') + '</div>'
                + '<div style="flex:1"><div style="font-weight:600;font-size:0.85rem">' + c.autorNombre + '</div>'
                + '<div style="font-size:0.85rem">' + escaparHTML(c.contenido) + '</div></div>'
                + '<button class="btn-eliminar-comentario" data-id="' + c.id + '" title="Eliminar" aria-label="Eliminar comentario">&times;</button>';
            lista.appendChild(div);

            var btnDel = div.querySelector('.btn-eliminar-comentario');
            if (btnDel) {
                btnDel.addEventListener('click', function () {
                    if (confirm('¿Eliminar este comentario?')) {
                        apiFetch('/comentarios/' + c.id, { method: 'DELETE' }).then(function () {
                            mostrarExito('Comentario eliminado');
                            cargarComentariosCaso(casoId);
                        });
                    }
                });
            }
        });
    });
}

function enviarComentarioCaso(casoId) {
    var input = document.getElementById('caso-comentario-input');
    if (!input) return;
    var contenido = input.value.trim();
    if (!contenido) return;

    apiFetch('/comentarios', {
        method: 'POST',
        body: JSON.stringify({ caso_id: casoId, contenido: contenido })
    }).then(function () {
        input.value = '';
        cargarComentariosCaso(casoId);
        mostrarExito('Comentario añadido al caso');
    });
}

(function () {
    var btnNuevoCaso = document.getElementById('btn-nuevo-caso');
    var casoCreate = document.getElementById('caso-create');
    var btnCancelar = document.getElementById('btn-caso-cancelar');
    var btnGuardar = document.getElementById('btn-caso-guardar');
    var btnVolver = document.getElementById('btn-volver-casos');

    if (btnNuevoCaso && casoCreate) {
        btnNuevoCaso.addEventListener('click', function () {
            casoCreate.style.display = casoCreate.style.display === 'none' ? 'block' : 'none';
        });
    }

    if (btnCancelar && casoCreate) {
        btnCancelar.addEventListener('click', function () {
            casoCreate.style.display = 'none';
            document.getElementById('caso-titulo').value = '';
            document.getElementById('caso-ubicacion').value = '';
            document.getElementById('caso-descripcion').value = '';
        });
    }

    if (btnGuardar) {
        btnGuardar.addEventListener('click', function () {
            var titulo = document.getElementById('caso-titulo').value.trim();
            var ubicacion = document.getElementById('caso-ubicacion').value.trim();
            var descripcion = document.getElementById('caso-descripcion').value.trim();
            if (!titulo) { mostrarError('Escribe un título para el caso'); return; }

            apiFetch('/casos', {
                method: 'POST',
                body: JSON.stringify({ titulo: titulo, ubicacion: ubicacion, descripcion: descripcion, estado: 'activo' })
            }).then(function () {
                document.getElementById('caso-titulo').value = '';
                document.getElementById('caso-ubicacion').value = '';
                document.getElementById('caso-descripcion').value = '';
                casoCreate.style.display = 'none';
                cargarCasos();
                mostrarExito('Caso creado correctamente');
            });
        });
    }

    if (btnVolver) {
        btnVolver.addEventListener('click', function () {
            document.querySelectorAll('.section').forEach(function (s) { s.classList.remove('active'); });
            document.getElementById('view-casos').classList.add('active');
            cargarCasos();
        });
    }
})();
