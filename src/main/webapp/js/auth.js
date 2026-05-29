var usuarioActual = null;

function cargarUsuario() {
    return apiFetch('/auth').then(function (u) {
        usuarioActual = u;
        actualizarUIUsuario();
        return u;
    }).catch(function (err) {
        window.location.href = 'login.html';
        throw err;
    });
}

function actualizarUIUsuario() {
    if (!usuarioActual) return;
    var avatarEls = document.querySelectorAll('#sidebar-avatar, #post-avatar');
    avatarEls.forEach(function (el) { el.textContent = usuarioActual.avatar; });
    var nameEl = document.getElementById('sidebar-name');
    if (nameEl) nameEl.textContent = usuarioActual.nombre;
    var roleEl = document.getElementById('sidebar-role');
    if (roleEl) roleEl.textContent = usuarioActual.rol;
    var adminMenu = document.getElementById('menu-admin');
    if (adminMenu) {
        adminMenu.style.display = usuarioActual.rol === 'Admin' ? '' : 'none';
    }
}

function login(email, password) {
    return apiFetch('/auth/login', {
        method: 'POST',
        body: JSON.stringify({ email: email, password: password })
    }).then(function (u) {
        usuarioActual = u;
        window.location.href = 'app.html';
    });
}

function register(nombre, email, rol, password) {
    return apiFetch('/auth/register', {
        method: 'POST',
        body: JSON.stringify({
            nombre: nombre,
            email: email,
            rol: rol,
            password: password
        })
    }).then(function (u) {
        usuarioActual = u;
        window.location.href = 'app.html';
    });
}

function logout() {
    apiFetch('/auth/logout', { method: 'POST' }).then(function () {
        window.location.href = 'login.html';
    });
}

(function () {
    var loginForm = document.getElementById('login-form');
    if (loginForm) {
        loginForm.addEventListener('submit', function (e) {
            e.preventDefault();
            var email = document.getElementById('email').value;
            var password = document.getElementById('password').value;
            var errorEl = document.getElementById('auth-error');
            login(email, password).catch(function (err) {
                if (errorEl) {
                    errorEl.textContent = err.message;
                    errorEl.style.display = 'block';
                }
            });
        });
    }

    var registerForm = document.getElementById('register-form');
    if (registerForm) {
        registerForm.addEventListener('submit', function (e) {
            e.preventDefault();
            var nombre = document.getElementById('nombre').value;
            var email = document.getElementById('email').value;
            var rol = document.getElementById('rol').value || 'Trabajador social';
            var password = document.getElementById('password').value;
            var errorEl = document.getElementById('auth-error');
            register(nombre, email, rol, password).catch(function (err) {
                if (errorEl) {
                    errorEl.textContent = err.message;
                    errorEl.style.display = 'block';
                }
            });
        });
    }
})();
