document.addEventListener('DOMContentLoaded', function () {
    cargarUsuario().then(function () {
        if (esAdmin()) {
            cambiarVista('admin');
            cargarAdmin();
        } else {
            cargarFeed();
        }
        cargarCasos();
        cargarConversaciones();
        cargarSugerencias();
        cargarEstadisticas();
        cargarRed();
    });

    var logoLogout = document.getElementById('logo-logout');
    if (logoLogout) {
        logoLogout.addEventListener('click', function () { logout(); });
    }

    var btnLogout = document.getElementById('btn-cerrar-sesion');
    if (btnLogout) {
        btnLogout.addEventListener('click', logout);
    }

    var menuItems = document.querySelectorAll('.menu-item[data-view]');
    menuItems.forEach(function (item) {
        item.addEventListener('click', function () { cambiarVista(item.getAttribute('data-view')); });
        item.addEventListener('keydown', function (e) {
            if (e.key === 'Enter' || e.key === ' ') { e.preventDefault(); cambiarVista(item.getAttribute('data-view')); }
        });
    });
});

function cambiarVista(vista) {
    if (vista === 'caso-detalle') return;

    document.querySelectorAll('.menu-item[data-view]').forEach(function (el) { el.classList.remove('active'); });
    document.querySelectorAll('.section').forEach(function (s) { s.classList.remove('active'); });

    var menuItem = document.querySelector('.menu-item[data-view="' + vista + '"]');
    if (menuItem) menuItem.classList.add('active');

    var section = document.getElementById('view-' + vista);
    if (section) section.classList.add('active');

    if (vista === 'feed') cargarFeed();
    if (vista === 'casos') { cargarCasos(); }
    if (vista === 'mensajes') {
        if (pollInterval) { clearInterval(pollInterval); pollInterval = null; }
        cargarConversaciones();
    }
    if (vista === 'red') cargarRed();
    if (vista === 'admin') cargarAdmin();
}

function cargarSugerencias() {
    var contenedor = document.getElementById('sugerencias-container');
    if (!contenedor) return;
    apiFetch('/usuarios/sugerencias').then(function (usuarios) {
        contenedor.innerHTML = '';
        if (usuarios.length === 0) {
            contenedor.innerHTML = '<div style="font-size:0.85rem;color:var(--text-muted)">No hay sugerencias</div>';
            return;
        }
        usuarios.forEach(function (u) {
            var div = document.createElement('div');
            div.className = 'user';
            div.innerHTML = '<div class="avatar" style="width:2rem;height:2rem;font-size:0.75rem">' + (u.avatar || 'US') + '</div>'
                + '<div class="name">' + u.nombre + '</div>'
                + '<button type="button" data-id="' + u.id + '" aria-label="Conectar con ' + u.nombre + '">+</button>';
            var btn = div.querySelector('button');
            btn.addEventListener('click', function () {
                apiFetch('/conexiones', {
                    method: 'POST',
                    body: JSON.stringify({ seguido_id: u.id })
                }).then(function () {
                    mostrarExito('Conectado con ' + u.nombre);
                    btn.textContent = '\u2713';
                    btn.disabled = true;
                    cargarEstadisticas();
                });
            });
            contenedor.appendChild(div);
        });
    });
}

function cargarEstadisticas() {
    apiFetch('/conexiones/contar').then(function (data) {
        var el = document.getElementById('stat-conexiones');
        if (el) el.textContent = data.conteo;
    });
}

function cargarRed() {
    var contenedor = document.getElementById('red-container');
    if (!contenedor) return;
    contenedor.innerHTML = '<div class="loading">Cargando tu red...</div>';

    apiFetch('/conexiones').then(function (conexiones) {
        contenedor.innerHTML = '';
        if (conexiones.length === 0) {
            contenedor.innerHTML = '<div class="loading">Aún no tienes conexiones. Conecta con otros profesionales desde el panel lateral.</div>';
            return;
        }
        conexiones.forEach(function (cx) {
            var div = document.createElement('div');
            div.className = 'case-card';
            div.style.flexDirection = 'row';
            div.style.alignItems = 'center';
            div.style.justifyContent = 'space-between';
            div.innerHTML = '<div style="display:flex;align-items:center;gap:0.75rem">'
                + '<div class="avatar">' + (cx.seguidoAvatar || 'US') + '</div>'
                + '<div><div class="title" style="margin:0">' + (cx.seguidoNombre || 'Usuario') + '</div>'
                + '<div style="font-size:0.85rem;color:var(--text-secondary)">' + (cx.seguidoRol || '') + '</div></div>'
                + '</div>'
                + '<button type="button" class="btn-chatear" data-uid="' + cx.seguidoId + '" style="padding:0.5rem 0.75rem;background:var(--primary);color:white;border:none;border-radius:var(--radius);font-size:0.85rem;cursor:pointer">Mensaje</button>';
            div.querySelector('.btn-chatear').addEventListener('click', function () {
                var uid = parseInt(this.getAttribute('data-uid'));
                apiFetch('/conversaciones', {
                    method: 'POST',
                    body: JSON.stringify({ usuario_id: uid })
                }).then(function (conv) {
                    cambiarVista('mensajes');
                    setTimeout(function () {
                        cargarConversaciones();
                        setTimeout(function () { seleccionarConversacion(conv); }, 200);
                    }, 200);
                });
            });
            contenedor.appendChild(div);
        });
    });
}
