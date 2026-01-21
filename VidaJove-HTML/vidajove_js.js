// ========================================
// VIDAJOVE - MAIN JAVASCRIPT FILE
// ========================================

// Dark Mode Toggle
document.addEventListener('DOMContentLoaded', function() {
    const darkModeToggle = document.getElementById('darkModeToggle');
    
    // Check for saved theme preference or default to 'light'
    const currentTheme = localStorage.getItem('theme') || 'light';
    document.documentElement.setAttribute('data-theme', currentTheme);
    
    // Dark mode toggle event
    if (darkModeToggle) {
        darkModeToggle.addEventListener('click', function() {
            let theme = document.documentElement.getAttribute('data-theme');
            let newTheme = theme === 'light' ? 'dark' : 'light';
            
            document.documentElement.setAttribute('data-theme', newTheme);
            localStorage.setItem('theme', newTheme);
        });
    }
});

// Smooth Scroll for anchor links
document.querySelectorAll('a[href^="#"]').forEach(anchor => {
    anchor.addEventListener('click', function (e) {
        e.preventDefault();
        const target = document.querySelector(this.getAttribute('href'));
        if (target) {
            target.scrollIntoView({
                behavior: 'smooth',
                block: 'start'
            });
        }
    });
});

// Form Validation Functions
function validateEmail(email) {
    const re = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return re.test(email);
}

function validatePassword(password) {
    return password.length >= 8;
}

function validatePhone(phone) {
    const re = /^[0-9]{9}$/;
    return re.test(phone.replace(/\s/g, ''));
}

function validateDateOfBirth(date) {
    const birthDate = new Date(date);
    const today = new Date();
    const age = today.getFullYear() - birthDate.getFullYear();
    const monthDiff = today.getMonth() - birthDate.getMonth();
    
    if (monthDiff < 0 || (monthDiff === 0 && today.getDate() < birthDate.getDate())) {
        age--;
    }
    
    return age >= 18 && age <= 30;
}

// Show error message
function showError(input, message) {
    const formGroup = input.parentElement;
    let error = formGroup.querySelector('.error-message');
    
    if (!error) {
        error = document.createElement('span');
        error.className = 'error-message';
        error.style.color = 'var(--error)';
        error.style.fontSize = '0.875rem';
        error.style.marginTop = '0.25rem';
        error.style.display = 'block';
        formGroup.appendChild(error);
    }
    
    error.textContent = message;
    input.style.borderColor = 'var(--error)';
}

// Clear error message
function clearError(input) {
    const formGroup = input.parentElement;
    const error = formGroup.querySelector('.error-message');
    
    if (error) {
        error.remove();
    }
    
    input.style.borderColor = 'var(--border-color)';
}

// Registration Form Validation
if (document.getElementById('registroForm')) {
    const form = document.getElementById('registroForm');
    
    form.addEventListener('submit', function(e) {
        e.preventDefault();
        
        let isValid = true;
        
        // Get form values
        const nombre = document.getElementById('nombre');
        const apellidos = document.getElementById('apellidos');
        const email = document.getElementById('email');
        const password = document.getElementById('password');
        const confirmPassword = document.getElementById('confirmPassword');
        const fechaNacimiento = document.getElementById('fechaNacimiento');
        const telefono = document.getElementById('telefono');
        
        // Clear all errors
        [nombre, apellidos, email, password, confirmPassword, fechaNacimiento, telefono].forEach(clearError);
        
        // Validate name
        if (nombre.value.trim() === '') {
            showError(nombre, 'El nombre es obligatorio');
            isValid = false;
        }
        
        // Validate apellidos
        if (apellidos.value.trim() === '') {
            showError(apellidos, 'Los apellidos son obligatorios');
            isValid = false;
        }
        
        // Validate email
        if (!validateEmail(email.value)) {
            showError(email, 'Por favor, introduce un email válido');
            isValid = false;
        }
        
        // Validate password
        if (!validatePassword(password.value)) {
            showError(password, 'La contraseña debe tener al menos 8 caracteres');
            isValid = false;
        }
        
        // Validate confirm password
        if (password.value !== confirmPassword.value) {
            showError(confirmPassword, 'Las contraseñas no coinciden');
            isValid = false;
        }
        
        // Validate date of birth
        if (!validateDateOfBirth(fechaNacimiento.value)) {
            showError(fechaNacimiento, 'Debes tener entre 18 y 30 años');
            isValid = false;
        }
        
        // Validate phone (optional but if provided must be valid)
        if (telefono.value && !validatePhone(telefono.value)) {
            showError(telefono, 'Por favor, introduce un teléfono válido (9 dígitos)');
            isValid = false;
        }
        
        if (isValid) {
            // Here you would normally send data to the database
            // For now, we'll simulate success
            alert('¡Registro exitoso! En una aplicación real, esto se conectaría a la base de datos.');
            
            // Example of data that would be sent:
            const userData = {
                nombre: nombre.value,
                apellidos: apellidos.value,
                email: email.value,
                password: password.value, // In real app, this would be hashed
                fecha_nacimiento: fechaNacimiento.value,
                telefono: telefono.value,
                rol: 'usuario'
            };
            
            console.log('Datos a enviar a la BD:', userData);
            
            // Redirect to login
            // window.location.href = 'login.html';
        }
    });
}

// Login Form Validation
if (document.getElementById('loginForm')) {
    const form = document.getElementById('loginForm');
    
    form.addEventListener('submit', function(e) {
        e.preventDefault();
        
        let isValid = true;
        
        const email = document.getElementById('email');
        const password = document.getElementById('password');
        
        clearError(email);
        clearError(password);
        
        if (!validateEmail(email.value)) {
            showError(email, 'Por favor, introduce un email válido');
            isValid = false;
        }
        
        if (password.value.trim() === '') {
            showError(password, 'La contraseña es obligatoria');
            isValid = false;
        }
        
        if (isValid) {
            alert('Iniciando sesión... En una aplicación real, esto verificaría las credenciales en la base de datos.');
            
            const loginData = {
                email: email.value,
                password: password.value
            };
            
            console.log('Datos de login:', loginData);
            
            // window.location.href = 'index.html';
        }
    });
}

// Forgot Password Form
if (document.getElementById('forgotPasswordForm')) {
    const form = document.getElementById('forgotPasswordForm');
    
    form.addEventListener('submit', function(e) {
        e.preventDefault();
        
        const email = document.getElementById('email');
        
        clearError(email);
        
        if (!validateEmail(email.value)) {
            showError(email, 'Por favor, introduce un email válido');
            return;
        }
        
        alert('Se ha enviado un email para restablecer tu contraseña. (Simulado)');
        
        console.log('Email de recuperación enviado a:', email.value);
    });
}

// Contact Form Validation
if (document.getElementById('contactForm')) {
    const form = document.getElementById('contactForm');
    
    form.addEventListener('submit', function(e) {
        e.preventDefault();
        
        let isValid = true;
        
        const nombre = document.getElementById('nombre');
        const email = document.getElementById('email');
        const asunto = document.getElementById('asunto');
        const mensaje = document.getElementById('mensaje');
        
        [nombre, email, asunto, mensaje].forEach(clearError);
        
        if (nombre.value.trim() === '') {
            showError(nombre, 'El nombre es obligatorio');
            isValid = false;
        }
        
        if (!validateEmail(email.value)) {
            showError(email, 'Por favor, introduce un email válido');
            isValid = false;
        }
        
        if (asunto.value === '') {
            showError(asunto, 'Por favor, selecciona un asunto');
            isValid = false;
        }
        
        if (mensaje.value.trim() === '') {
            showError(mensaje, 'El mensaje es obligatorio');
            isValid = false;
        }
        
        if (isValid) {
            alert('¡Mensaje enviado correctamente! Te responderemos pronto.');
            
            const contactData = {
                nombre: nombre.value,
                email: email.value,
                asunto: asunto.value,
                mensaje: mensaje.value,
                fecha: new Date().toISOString()
            };
            
            console.log('Datos de contacto:', contactData);
            
            form.reset();
        }
    });
}

// News Filter (for noticias.html)
if (document.getElementById('categoryFilter')) {
    const filterSelect = document.getElementById('categoryFilter');
    
    filterSelect.addEventListener('change', function() {
        const selectedCategory = this.value;
        const newsCards = document.querySelectorAll('.news-card');
        
        newsCards.forEach(card => {
            const category = card.querySelector('.news-category');
            
            if (selectedCategory === 'todas' || !category) {
                card.style.display = 'block';
            } else if (category.classList.contains(selectedCategory)) {
                card.style.display = 'block';
            } else {
                card.style.display = 'none';
            }
        });
    });
}

// Search functionality
if (document.getElementById('searchInput')) {
    const searchInput = document.getElementById('searchInput');
    
    searchInput.addEventListener('input', function() {
        const searchTerm = this.value.toLowerCase();
        const newsCards = document.querySelectorAll('.news-card');
        
        newsCards.forEach(card => {
            const title = card.querySelector('h3').textContent.toLowerCase();
            const content = card.querySelector('p') ? card.querySelector('p').textContent.toLowerCase() : '';
            
            if (title.includes(searchTerm) || content.includes(searchTerm)) {
                card.style.display = 'block';
            } else {
                card.style.display = 'none';
            }
        });
    });
}

// Animate elements on scroll
const observerOptions = {
    threshold: 0.1,
    rootMargin: '0px 0px -100px 0px'
};

const observer = new IntersectionObserver(function(entries) {
    entries.forEach(entry => {
        if (entry.isIntersecting) {
            entry.target.style.opacity = '1';
            entry.target.style.transform = 'translateY(0)';
        }
    });
}, observerOptions);

// Observe all cards
document.querySelectorAll('.feature-card, .news-card, .event-card').forEach(el => {
    el.style.opacity = '0';
    el.style.transform = 'translateY(30px)';
    el.style.transition = 'all 0.6s ease-out';
    observer.observe(el);
});