document.addEventListener('DOMContentLoaded', function () {
    const navbar = document.getElementById('mainNavbar');
    function handleNavbarScroll() {
        if (!navbar) return;
        if (window.scrollY > 50) {
            navbar.classList.add('scrolled');
        } else {
            navbar.classList.remove('scrolled');
        }
    }
    handleNavbarScroll();
    window.addEventListener('scroll', handleNavbarScroll, { passive: true });

    const reveals = document.querySelectorAll('[data-reveal]');
    const revealObserver = new IntersectionObserver(function (entries) {
        entries.forEach(function (entry) {
            if (entry.isIntersecting) {
                entry.target.classList.add('revealed');
                revealObserver.unobserve(entry.target);
            }
        });
    }, { threshold: 0.1, rootMargin: '0px 0px -50px 0px' });
    reveals.forEach(function (el) { revealObserver.observe(el); });

    const badgeInicial = document.querySelector('.badge-carrito');
    let cartCount = badgeInicial ? parseInt(badgeInicial.textContent, 10) || 0 : 0;
    actualizarBadgeCarrito(cartCount);

    document.querySelectorAll('.btn-add-cart').forEach(function (btn) {
        btn.addEventListener('click', function (e) {
            if (btn.form) return;
            e.preventDefault();
            cartCount++;
            localStorage.setItem('mechita_cart', cartCount);
            actualizarBadgeCarrito(cartCount);
            mostrarToast('Producto agregado al carrito');
        });
    });

    const heroSection = document.querySelector('.hero-mechita');
    const heroDots = document.querySelectorAll('.hero-dot');
    const heroSlides = [
        'https://images.unsplash.com/photo-1587574293340-e0011c4e8ecf?w=1600&q=80',
        'https://images.unsplash.com/photo-1558961363-fa8fdf82db35?w=1600&q=80',
        'https://images.unsplash.com/photo-1601050690597-df0568f70950?w=1600&q=80'
    ];
    let currentHeroSlide = 0;

    function setHeroSlide(index) {
        if (!heroSection) return;
        currentHeroSlide = index;
        heroSection.style.backgroundImage = 'url(' + heroSlides[index] + ')';
        heroDots.forEach(function (dot, i) {
            dot.classList.toggle('active', i === index);
        });
    }

    heroDots.forEach(function (dot, i) {
        dot.addEventListener('click', function () { setHeroSlide(i); });
    });

    const heroPrev = document.querySelector('.hero-arrow-prev');
    const heroNext = document.querySelector('.hero-arrow-next');

    if (heroPrev) heroPrev.addEventListener('click', function () {
        setHeroSlide((currentHeroSlide - 1 + heroSlides.length) % heroSlides.length);
    });
    if (heroNext) heroNext.addEventListener('click', function () {
        setHeroSlide((currentHeroSlide + 1) % heroSlides.length);
    });

    if (heroSection) {
        setInterval(function () {
            setHeroSlide((currentHeroSlide + 1) % heroSlides.length);
        }, 6000);
    }

});

// ==========================================
// SOLUCIÓN AL ERROR DEL BACKDROP DE BOOTSTRAP
// ==========================================
// Escuchamos cuando el modal está a punto de abrirse
document.addEventListener('show.bs.modal', function (event) {
    // Si el modal que se abre es el de logout
    if (event.target.id === 'logoutModal') {
        // Buscamos si hay un menú desplegable abierto
        const dropdownToggle = document.querySelector('[data-bs-toggle="dropdown"].show');
        if (dropdownToggle) {
            // Lo cerramos instantáneamente antes de que el modal se dibuje
            const dropdownInstance = bootstrap.Dropdown.getInstance(dropdownToggle);
            if (dropdownInstance) {
                dropdownInstance.hide();
            }
        }
    }
});


function actualizarBadgeCarrito(count) {
    document.querySelectorAll('.badge-carrito').forEach(function (badge) {
        badge.textContent = count;
        badge.style.display = count > 0 ? 'flex' : 'none';
    });
}

function mostrarToast(mensaje) {
    const existing = document.querySelector('.toast-mechita');
    if (existing) existing.remove();

    const toast = document.createElement('div');
    toast.className = 'toast-mechita';
    toast.innerHTML = '<i class="bi bi-check-circle-fill"></i> ' + mensaje;
    document.body.appendChild(toast);

    setTimeout(function () { toast.classList.add('show'); }, 10);
    setTimeout(function () {
        toast.classList.remove('show');
        setTimeout(function () { toast.remove(); }, 300);
    }, 2500);
}

const toastStyle = document.createElement('style');
toastStyle.textContent = `
    .toast-mechita {
        position: fixed;
        bottom: 30px;
        right: 30px;
        background: #1A1A1A;
        color: white;
        padding: 1rem 1.5rem;
        border-radius: 12px;
        box-shadow: 0 10px 30px rgba(0,0,0,0.3);
        transform: translateX(400px);
        transition: transform 0.3s ease;
        z-index: 9999;
        font-weight: 500;
        display: flex;
        align-items: center;
        gap: 10px;
        border-left: 4px solid #A0522D;
        max-width: 320px;
    }
    .toast-mechita.show { transform: translateX(0); }
    .toast-mechita i { color: #D4A574; font-size: 1.2rem; }
`;
document.head.appendChild(toastStyle);