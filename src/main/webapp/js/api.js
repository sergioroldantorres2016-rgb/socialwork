const API_BASE = window.location.origin + '/api';

function apiFetch(url, options) {
    const defaults = {
        headers: { 'Content-Type': 'application/json' },
    };
    const merged = Object.assign({}, defaults, options);

    return fetch(API_BASE + url, merged)
        .then(function (res) {
            if (!res.ok) {
                return res.json().then(function (err) {
                    throw new Error(err.error || 'Error del servidor');
                });
            }
            return res.json();
        })
        .catch(function (err) {
            mostrarError(err.message);
            throw err;
        });
}

function mostrarError(msg) {
    var contenedor = document.getElementById('toast-container');
    if (!contenedor) return;
    var toast = document.createElement('div');
    toast.className = 'toast error';
    toast.textContent = msg;
    contenedor.appendChild(toast);
    setTimeout(function () { toast.remove(); }, 4000);
}

function mostrarExito(msg) {
    var contenedor = document.getElementById('toast-container');
    if (!contenedor) return;
    var toast = document.createElement('div');
    toast.className = 'toast success';
    toast.textContent = msg;
    contenedor.appendChild(toast);
    setTimeout(function () { toast.remove(); }, 3000);
}
