function esAdmin() {
    return usuarioActual && usuarioActual.rol === 'Admin';
}

function cargarAdmin() {
    var stats = document.getElementById('admin-stats');
    var content = document.getElementById('admin-content');
    if (!stats) return;

    stats.innerHTML = '<div class="loading">Cargando estadísticas...</div>';
    content.innerHTML = '';

    Promise.all([
        apiFetch('/usuarios/todos'),
        apiFetch('/publicaciones'),
        apiFetch('/casos'),
        apiFetch('/conexiones/contar')
    ]).then(function (results) {
        var usuarios = results[0];
        var publicaciones = results[1];
        var casos = results[2];
        var connData = results[3];

        stats.innerHTML = '<div class="admin-stats-grid">'
            + '<div class="stat-card"><div class="stat-num">' + usuarios.length + '</div><div>Usuarios</div></div>'
            + '<div class="stat-card"><div class="stat-num">' + publicaciones.length + '</div><div>Publicaciones</div></div>'
            + '<div class="stat-card"><div class="stat-num">' + casos.length + '</div><div>Casos</div></div>'
            + '<div class="stat-card"><div class="stat-num">' + (connData.conteo || 0) + '</div><div>Conexiones</div></div>'
            + '</div>';

        mostrarAdminUsuarios();
    });
}

function mostrarAdminUsuarios() {
    var content = document.getElementById('admin-content');
    content.innerHTML = '<div class="loading">Cargando usuarios...</div>';

    apiFetch('/usuarios/todos').then(function (usuarios) {
        content.innerHTML = '<h3 style="font-size:1rem;font-weight:600;margin-bottom:0.5rem">Usuarios registrados</h3>'
            + '<div class="admin-table">'
            + '<div class="admin-tr admin-th"><div>ID</div><div>Nombre</div><div>Email</div><div>Rol</div><div></div></div>';
        usuarios.forEach(function (u) {
            var esAdmin = u.rol === 'Admin';
            content.innerHTML += '<div class="admin-tr">'
                + '<div>' + u.id + '</div>'
                + '<div>' + u.nombre + '</div>'
                + '<div>' + u.email + '</div>'
                + '<div>' + u.rol + '</div>'
                + '<div>' + (esAdmin ? '' : '<button class="btn-eliminar" data-admin-userid="' + u.id + '" title="Eliminar usuario" aria-label="Eliminar usuario">&times;</button>') + '</div>'
                + '</div>';
        });
        content.innerHTML += '</div>';

        content.querySelectorAll('.btn-eliminar[data-admin-userid]').forEach(function (btn) {
            var uid = parseInt(btn.getAttribute('data-admin-userid'));
            btn.addEventListener('click', function () {
                if (confirm('¿Eliminar este usuario y todo su contenido?')) {
                    apiFetch('/usuarios/' + uid, { method: 'DELETE' }).then(function () {
                        mostrarExito('Usuario eliminado');
                        mostrarAdminUsuarios();
                        cargarAdmin();
                    });
                }
            });
        });
    });
}

function mostrarAdminPublicaciones() {
    var content = document.getElementById('admin-content');
    content.innerHTML = '<div class="loading">Cargando publicaciones...</div>';

    apiFetch('/publicaciones').then(function (posts) {
        content.innerHTML = '<h3 style="font-size:1rem;font-weight:600;margin-bottom:0.5rem">Todas las publicaciones</h3>';
        if (posts.length === 0) {
            content.innerHTML += '<div class="loading">No hay publicaciones</div>';
            return;
        }
        posts.forEach(function (p) {
            var div = document.createElement('div');
            div.className = 'post';
            div.innerHTML = '<div class="header">'
                + '<div class="avatar">' + (p.autorAvatar || 'US') + '</div>'
                + '<div style="flex:1"><div class="name">' + (p.autorNombre || 'Usuario') + '</div>'
                + '<div class="meta">' + formatearFecha(p.createdAt) + '</div></div>'
                + '<button class="btn-eliminar" data-admin-postid="' + p.id + '" title="Eliminar" aria-label="Eliminar">&times;</button>'
                + '</div>'
                + '<div class="content">' + escaparHTML(p.contenido) + '</div>';
            content.appendChild(div);

            div.querySelector('.btn-eliminar').addEventListener('click', function () {
                if (confirm('¿Eliminar esta publicación?')) {
                    apiFetch('/publicaciones/' + p.id, { method: 'DELETE' }).then(function () {
                        mostrarExito('Publicación eliminada');
                        mostrarAdminPublicaciones();
                        cargarAdmin();
                    });
                }
            });
        });
    });
}

function mostrarAdminCasos() {
    var content = document.getElementById('admin-content');
    content.innerHTML = '<div class="loading">Cargando casos...</div>';

    apiFetch('/casos').then(function (casos) {
        content.innerHTML = '<h3 style="font-size:1rem;font-weight:600;margin-bottom:0.5rem">Todos los casos</h3>';
        if (casos.length === 0) {
            content.innerHTML += '<div class="loading">No hay casos</div>';
            return;
        }
        var labels = { activo: 'Activo', seguimiento: 'Seguimiento', cerrado: 'Cerrado' };
        casos.forEach(function (c) {
            var div = document.createElement('div');
            div.className = 'case-card';
            div.innerHTML = '<div style="display:flex;justify-content:space-between;align-items:start">'
                + '<div class="title">' + escaparHTML(c.titulo) + '</div>'
                + '<button class="btn-eliminar" data-admin-casoid="' + c.id + '" title="Eliminar" aria-label="Eliminar">&times;</button>'
                + '</div>'
                + '<div class="description">' + escaparHTML(c.descripcion || 'Sin descripción') + '</div>'
                + '<div class="meta"><span>Apertura: ' + formatearFecha(c.createdAt) + '</span></div>'
                + '<div class="status ' + c.estado + '">' + (labels[c.estado] || c.estado) + '</div>';
            content.appendChild(div);

            div.querySelector('.btn-eliminar').addEventListener('click', function () {
                if (confirm('¿Eliminar el caso "' + c.titulo + '"?')) {
                    apiFetch('/casos/' + c.id, { method: 'DELETE' }).then(function () {
                        mostrarExito('Caso eliminado');
                        mostrarAdminCasos();
                        cargarAdmin();
                    });
                }
            });
        });
    });
}

(function () {
    var btnUsuarios = document.getElementById('btn-admin-usuarios');
    var btnPublicaciones = document.getElementById('btn-admin-publicaciones');
    var btnCasos = document.getElementById('btn-admin-casos');

    if (btnUsuarios) btnUsuarios.addEventListener('click', mostrarAdminUsuarios);
    if (btnPublicaciones) btnPublicaciones.addEventListener('click', mostrarAdminPublicaciones);
    if (btnCasos) btnCasos.addEventListener('click', mostrarAdminCasos);
})();
