// DrakkarPress Shared Navigation Component
// Usage: Include this script after api-client.js, and call initNavBar() with config

function initNavBar(config = {}) {
  const {
    pageTitle = 'DrakkarPress',
    showSearch = false,
    backLink = null
  } = config;

  const navHTML = `
    <div class="dkp-navbar" style="background:#fff;border-bottom:1px solid #DBDBDB;padding:14px 24px;display:flex;align-items:center;justify-content:space-between;">
      <div style="display:flex;align-items:center;gap:20px;">
        <a href="/dashboard.html" style="text-decoration:none;color:#262626;font-size:20px;font-weight:700;display:flex;align-items:center;gap:10px;">
          <svg style="width:28px;height:28px;" viewBox="0 0 100 100" xmlns="http://www.w3.org/2000/svg">
            <circle cx="50" cy="50" r="48" fill="none" stroke="#D4AF37" stroke-width="2"/>
            <path d="M30,35 L30,65 Q30,70 35,70 L45,70 Q50,70 50,65 L50,35 Z" fill="#1A4D7A"/>
            <path d="M55,35 L55,65 Q55,70 60,70 L70,70 Q75,70 75,65 L75,35 Z" fill="#1A4D7A"/>
          </svg>
          ${pageTitle}
        </a>
        ${backLink ? `<a href="${backLink}" style="color:#1A4D7A;text-decoration:none;font-size:14px;font-weight:600;">← Volver</a>` : ''}
      </div>
      <div style="display:flex;align-items:center;gap:20px;">
        ${showSearch ? '<input type="search" placeholder="Buscar..." style="padding:8px 12px;border:1px solid #DBDBDB;border-radius:8px;font-size:14px;width:240px;" id="dkp-search" />' : ''}
        <a href="/generators.html" style="color:#262626;text-decoration:none;font-weight:600;font-size:15px;">Generadores</a>
        <a href="/my-books.html" style="color:#262626;text-decoration:none;font-weight:600;font-size:15px;">Mis Libros</a>
        <div style="position:relative;display:inline-block;">
          <button id="dkp-user-menu-btn" style="background:none;border:none;cursor:pointer;display:flex;align-items:center;gap:8px;font-size:14px;font-weight:600;color:#262626;">
            <div id="dkp-avatar" style="width:32px;height:32px;border-radius:50%;background:#1A4D7A;color:#fff;display:flex;align-items:center;justify-content:center;font-weight:700;font-size:14px;">U</div>
            <span id="dkp-username">Usuario</span>
            <i class="fas fa-chevron-down"></i>
          </button>
          <div id="dkp-dropdown" style="position:absolute;top:42px;right:0;background:#fff;border:1px solid #DBDBDB;border-radius:12px;box-shadow:0 4px 16px rgba(0,0,0,.1);min-width:220px;display:none;z-index:100;">
            <a href="/dashboard.html" style="display:block;padding:12px 18px;color:#262626;text-decoration:none;font-size:14px;"><i class="fas fa-home" style="margin-right:10px;width:16px;"></i>Dashboard</a>
            <a href="/profile.html" style="display:block;padding:12px 18px;color:#262626;text-decoration:none;font-size:14px;"><i class="fas fa-user" style="margin-right:10px;width:16px;"></i>Mi Perfil</a>
            <a href="/settings.html" style="display:block;padding:12px 18px;color:#262626;text-decoration:none;font-size:14px;"><i class="fas fa-cog" style="margin-right:10px;width:16px;"></i>Configuración</a>
            <a href="/help.html" style="display:block;padding:12px 18px;color:#262626;text-decoration:none;font-size:14px;"><i class="fas fa-question-circle" style="margin-right:10px;width:16px;"></i>Ayuda</a>
            <a href="#" id="dkp-logout-btn" style="display:block;padding:12px 18px;color:#ED4956;text-decoration:none;font-size:14px;border-top:1px solid #DBDBDB;"><i class="fas fa-sign-out-alt" style="margin-right:10px;width:16px;"></i>Cerrar Sesión</a>
          </div>
        </div>
      </div>
    </div>
  `;

  // Inject navbar at top of body
  const navbar = document.createElement('div');
  navbar.innerHTML = navHTML;
  document.body.insertBefore(navbar.firstElementChild, document.body.firstChild);

  // Load user data
  (async () => {
    if (!api.isAuthenticated()) return;
    const profile = await api.getMyProfile();
    if (profile.success && profile.data) {
      const u = profile.data;
      document.getElementById('dkp-username').textContent = u.username || 'Usuario';
      document.getElementById('dkp-avatar').textContent = (u.username || 'U')[0].toUpperCase();
    }
  })();

  // Dropdown toggle
  const menuBtn = document.getElementById('dkp-user-menu-btn');
  const dropdown = document.getElementById('dkp-dropdown');
  menuBtn.addEventListener('click', (e) => {
    e.stopPropagation();
    dropdown.style.display = dropdown.style.display === 'none' ? 'block' : 'none';
  });
  document.addEventListener('click', () => {
    dropdown.style.display = 'none';
  });

  // Logout
  document.getElementById('dkp-logout-btn').addEventListener('click', (e) => {
    e.preventDefault();
    api.logout();
    location.replace('/login.html');
  });

  // Search (if enabled)
  if (showSearch) {
    const searchInput = document.getElementById('dkp-search');
    searchInput.addEventListener('input', (e) => {
      console.log('Search query:', e.target.value);
      // TODO: implement search logic
    });
  }
}

// Auto-init if data-dkp-nav attribute present on body
if (document.body.dataset.dkpNav) {
  document.addEventListener('DOMContentLoaded', () => {
    const config = JSON.parse(document.body.dataset.dkpNav || '{}');
    initNavBar(config);
  });
}
