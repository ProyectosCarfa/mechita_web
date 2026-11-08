document.addEventListener("DOMContentLoaded", function () {
    // Obtenemos la ruta actual de la URL
    const path = window.location.pathname;

    // Quitamos la clase active de todos los enlaces
    document.querySelectorAll('.nav-link-mechita').forEach(link => link.classList.remove('active'));

    // Marcamos el que corresponde
    if (path === '/' || path === '') {
        document.getElementById('link-inicio')?.classList.add('active');
    } else if (path.includes('/productos')) {
        document.getElementById('link-productos')?.classList.add('active');
    } else if (path.includes('/categorias')) {
        document.getElementById('link-categorias')?.classList.add('active');
    } else if (path.includes('/nosotros')) {
        document.getElementById('link-nosotros')?.classList.add('active');
    } else if (path.includes('/contacto')) {
        document.getElementById('link-contacto')?.classList.add('active');
    }
});