var conversaciones = [];
var conversacionActiva = null;
var pollInterval = null;

function cargarConversaciones() {
    apiFetch('/conversaciones').then(function (convs) {
        conversaciones = convs;
        renderizarChatList();
        renderizarMensajesRecientes();
        actualizarBadgeMensajes();
    });
}

function renderizarChatList() {
    var lista = document.getElementById('chat-list');
    if (!lista) return;
    lista.innerHTML = '';
    if (conversaciones.length === 0) {
        lista.innerHTML = '<div class="chat" style="cursor:default;color:var(--text-muted);padding:0.75rem 1rem">Sin conversaciones</div>';
        return;
    }
    conversaciones.forEach(function (c) {
        var nombre = obtenerNombreConversacion(c);
        var ultimo = c.ultimoMensaje ? c.ultimoMensaje.contenido : '';
        var div = document.createElement('div');
        div.className = 'chat' + (conversacionActiva && conversacionActiva.id === c.id ? ' active' : '');
        div.innerHTML = '<div style="font-weight:600;font-size:0.9rem">' + nombre + '</div>'
            + '<div style="font-size:0.8rem;color:var(--text-muted);overflow:hidden;text-overflow:ellipsis;white-space:nowrap">' + (ultimo.length > 30 ? ultimo.substring(0, 30) + '...' : ultimo) + '</div>';
        div.setAttribute('data-id', c.id);
        div.setAttribute('role', 'tab');
        div.tabIndex = 0;
        div.addEventListener('click', function () { seleccionarConversacion(c); });
        div.addEventListener('keydown', function (e) {
            if (e.key === 'Enter' || e.key === ' ') { e.preventDefault(); seleccionarConversacion(c); }
        });
        lista.appendChild(div);
    });
}

function obtenerNombreConversacion(c) {
    if (c.participantes && c.participantes.length > 0) {
        return c.participantes[0].nombre;
    }
    return 'Conversaci\u00F3n #' + c.id;
}

function seleccionarConversacion(c) {
    conversacionActiva = c;
    if (pollInterval) { clearInterval(pollInterval); pollInterval = null; }
    renderizarChatList();

    var avatar = c.participantes && c.participantes.length > 0 ? c.participantes[0].avatar : '?';
    var nombre = obtenerNombreConversacion(c);
    var header = document.getElementById('chat-header');
    var headerAvatar = document.getElementById('chat-avatar');
    var headerName = document.getElementById('chat-name');
    if (headerAvatar) { headerAvatar.textContent = avatar; headerAvatar.style.background = ''; }
    if (headerName) { headerName.textContent = nombre; headerName.style.color = ''; }

    cargarMensajes(c.id);
    activarChatInput();
    pollInterval = setInterval(function () { cargarMensajes(c.id); }, 3000);
}

function cargarMensajes(convId) {
    var body = document.getElementById('messages-body');
    if (!body) return;

    apiFetch('/mensajes/conversacion/' + convId).then(function (mensajes) {
        var scrollAlFinal = body.scrollTop + body.clientHeight >= body.scrollHeight - 50;
        body.innerHTML = '';
        if (mensajes.length === 0) {
            body.innerHTML = '<div class="loading">No hay mensajes aún. Escribe el primero.</div>';
            return;
        }
        mensajes.forEach(function (m) {
            body.appendChild(crearMensajeElement(m));
        });
        if (scrollAlFinal || mensajes.length > 0) {
            body.scrollTop = body.scrollHeight;
        }
        actualizarBadgeMensajes();
    });
}

function crearMensajeElement(m) {
    var div = document.createElement('div');
    var esPropio = usuarioActual && m.remitenteId === usuarioActual.id;
    div.className = 'message ' + (esPropio ? 'outgoing' : 'incoming');
    var hora = m.createdAt ? new Date(m.createdAt + (m.createdAt.includes('T') ? '' : 'T00:00:00')) : null;
    var horaStr = hora && !isNaN(hora.getTime()) ? hora.getHours().toString().padStart(2, '0') + ':' + hora.getMinutes().toString().padStart(2, '0') : '';
    div.innerHTML = escaparHTML(m.contenido) + (horaStr ? '<div style="font-size:0.7rem;opacity:0.7;margin-top:0.25rem;text-align:' + (esPropio ? 'right' : 'left') + '">' + horaStr + '</div>' : '');
    return div;
}

function activarChatInput() {
    var input = document.getElementById('chat-input');
    var btn = document.getElementById('btn-enviar');
    if (input) input.disabled = false;
    if (btn) btn.disabled = false;
}

function enviarMensaje() {
    var input = document.getElementById('chat-input');
    if (!input || !conversacionActiva) return;
    var contenido = input.value.trim();
    if (!contenido) return;

    apiFetch('/mensajes', {
        method: 'POST',
        body: JSON.stringify({ conversacion_id: conversacionActiva.id, contenido: contenido })
    }).then(function () {
        input.value = '';
        cargarMensajes(conversacionActiva.id);
        cargarConversaciones();
    });
}

function renderizarMensajesRecientes() {
    var contenedor = document.getElementById('recent-messages');
    if (!contenedor) return;
    contenedor.innerHTML = '<div class="title">Mensajes recientes</div>';
    if (conversaciones.length === 0) {
        contenedor.innerHTML += '<div style="font-size:0.85rem;color:var(--text-muted);padding:0.5rem 0">Sin mensajes recientes</div>';
        return;
    }
    conversaciones.slice(0, 3).forEach(function (c) {
        var div = document.createElement('div');
        div.className = 'item';
        div.textContent = obtenerNombreConversacion(c);
        div.addEventListener('click', function () {
            cambiarVista('mensajes');
            setTimeout(function () { seleccionarConversacion(c); }, 100);
        });
        contenedor.appendChild(div);
    });
}

function actualizarBadgeMensajes() {
    var badge = document.getElementById('mensajes-badge');
    if (badge) badge.textContent = conversaciones.length;
}

function abrirNuevoChat() {
    var lista = document.getElementById('chat-list');
    if (!lista) return;

    apiFetch('/usuarios/todos').then(function (usuarios) {
        var disponibles = usuarios.filter(function (u) { return usuarioActual && u.id !== usuarioActual.id; });
        var html = '<div style="padding:0.75rem 1rem;border-bottom:1px solid var(--border);font-weight:600;font-size:0.9rem;background:var(--bg)">Selecciona un usuario para chatear</div>';
        if (disponibles.length === 0) {
            html += '<div style="padding:0.75rem 1rem;color:var(--text-muted)">No hay usuarios disponibles</div>';
        } else {
            disponibles.forEach(function (u) {
                html += '<div class="chat nuevo-chat-item" data-uid="' + u.id + '">'
                    + '<div class="avatar" style="width:1.75rem;height:1.75rem;font-size:0.7rem;display:inline-flex;margin-right:0.5rem">' + (u.avatar || 'US') + '</div>'
                    + u.nombre + ' <span style="color:var(--text-muted);font-size:0.8rem">(' + (u.rol || '') + ')</span>'
                    + '</div>';
            });
        }
        lista.innerHTML = html;

        lista.querySelectorAll('.nuevo-chat-item').forEach(function (el) {
            el.addEventListener('click', function () {
                var uid = parseInt(el.getAttribute('data-uid'));
                apiFetch('/conversaciones', {
                    method: 'POST',
                    body: JSON.stringify({ usuario_id: uid })
                }).then(function (conv) {
                    cargarConversaciones();
                    setTimeout(function () { seleccionarConversacion(conv); }, 200);
                });
            });
        });
    });
}

(function () {
    var btnEnviar = document.getElementById('btn-enviar');
    var chatInput = document.getElementById('chat-input');
    var btnNuevoChat = document.getElementById('btn-nuevo-chat');

    if (btnEnviar) { btnEnviar.addEventListener('click', enviarMensaje); }
    if (chatInput) {
        chatInput.addEventListener('keydown', function (e) {
            if (e.key === 'Enter' && !e.shiftKey) { e.preventDefault(); enviarMensaje(); }
        });
    }
    if (btnNuevoChat) {
        btnNuevoChat.addEventListener('click', function () {
            if (pollInterval) { clearInterval(pollInterval); pollInterval = null; }
            conversacionActiva = null;
            abrirNuevoChat();
        });
    }
})();
