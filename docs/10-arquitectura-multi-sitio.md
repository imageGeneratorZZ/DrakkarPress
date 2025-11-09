# Arquitectura Multi-Sitio - DrakkarPress

## Visión General

DrakkarPress utiliza una **arquitectura multi-sitio** donde cada tipo de usuario tiene su propia aplicación web independiente, optimizada para sus necesidades específicas. Todos los sitios comparten el mismo backend (microservicios) pero tienen frontends especializados.

---

## 🌐 Estructura de Dominios

### Sitios Principales

```
┌─────────────────────────────────────────────────────────────┐
│             API GATEWAY + BACKEND (Compartido)              │
│                  api.drakkarpress.com                       │
└─────────────────────────┬───────────────────────────────────┘
                          │
        ┌─────────────────┼─────────────────┬────────────────┐
        │                 │                 │                │
        ▼                 ▼                 ▼                ▼
┌──────────────┐  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐
│ MARKETPLACE  │  │  ESCRITORES  │  │ REVENDEDORES │  │  IMPRENTAS   │
│ www.drakkar  │  │ escritores.  │  │ afiliados.   │  │ imprentas.   │
│ press.com    │  │ drakkarpress │  │ drakkarpress │  │ drakkarpress │
└──────────────┘  └──────────────┘  └──────────────┘  └──────────────┘
```

### 1. **Marketplace Principal** - www.drakkarpress.com

**Propósito:** Portal público de descubrimiento y compra de libros

**Usuarios:** Cualquier visitante + Lectores registrados

**Características:**
- Búsqueda y catálogo completo
- Fichas de libros
- Carrito de compras
- Checkout
- Biblioteca personal (usuarios autenticados)
- Blog y recursos

**Tecnología:**
- Next.js 14 (SSR + SSG)
- Optimizado para SEO
- Performance máximo

---

### 2. **Portal Escritores** - escritores.drakkarpress.com

**Propósito:** Plataforma para autores que publican libros

**Acceso:** Solo escritores registrados (autenticación obligatoria)

**Características:**
- Dashboard con métricas
- Gestión de libros (CRUD)
- Asistente de publicación
- Herramientas de IA integradas
- Análisis de ventas y regalías
- Configuración de precios
- Integración con plataformas
- Gestión de archivos (PDF, EPUB)

**Tecnología:**
- Next.js 14 (SPA mode)
- Rich text editors
- Drag & drop file upload
- Real-time charts

**Rutas Principales:**
```
/                    → Dashboard general
/libros              → Lista de mis libros
/libros/nuevo        → Wizard de publicación
/libros/{id}/editar  → Editor de libro
/ingresos            → Regalías y pagos
/estadisticas        → Analytics detallado
/ia                  → Herramientas de IA
/integraciones       → Shopify, ML, Lulu
/configuracion       → Perfil, impuestos, pago
```

---

### 3. **Portal Revendedores** - afiliados.drakkarpress.com

**Propósito:** Plataforma para afiliados que promocionan libros

**Acceso:** Solo afiliados registrados

**Características:**
- Dashboard de comisiones
- Catálogo de libros disponibles
- Generador de enlaces de tracking
- Generador de códigos QR
- Herramientas de marketing (IA)
- Estadísticas de conversión
- Pagos y reportes

**Tecnología:**
- Next.js 14 (SPA mode)
- QR code generation
- Link builder tools
- Social media integration

**Rutas Principales:**
```
/                    → Dashboard con ganancias
/catalogo            → Explorar libros
/mis-libros          → Libros que promociono
/generar-link        → Crear enlace de afiliado
/marketing           → Herramientas de IA marketing
/comisiones          → Historial de comisiones
/estadisticas        → Clics, conversiones, ROI
/configuracion       → Datos de pago
```

---

### 4. **Portal Imprentas** - imprentas.drakkarpress.com

**Propósito:** Plataforma para imprentas POD

**Acceso:** Solo imprentas registradas

**Características:**
- Dashboard de pedidos
- Gestión de órdenes de impresión
- Descarga de archivos (PDF interior/portada)
- Actualización de estados
- Tracking de envíos
- Pagos recibidos
- Estadísticas de producción

**Tecnología:**
- Next.js 14 (SPA mode)
- File download manager
- Real-time order updates
- Print specifications viewer

**Rutas Principales:**
```
/                    → Dashboard de pedidos
/pedidos/nuevos      → Pedidos pendientes
/pedidos/produccion  → En producción
/pedidos/enviados    → Completados
/archivos/{id}       → Descargar PDFs de impresión
/tracking            → Actualizar tracking
/pagos               → Historial de pagos
/configuracion       → Zona de cobertura, capacidad
```

---

### 5. **Portal Lectores** (Opcional - puede estar en www)

**Propósito:** Biblioteca personal y gestión de compras

**Acceso:** Lectores autenticados

**Características:**
- Biblioteca digital
- Descarga de PDFs comprados
- Tracking de pedidos físicos
- Favoritos y listas de lectura
- Reseñas

**Rutas Principales:**
```
/                    → Mi biblioteca
/libro/{id}          → Leer/descargar libro
/pedidos             → Historial de compras
/favoritos           → Libros guardados
/reseñas             → Mis reseñas
```

---

## 🔐 Autenticación y SSO (Single Sign-On)

### Flujo de Login Unificado

```
Usuario ingresa a cualquier portal
         ↓
    auth.drakkarpress.com
         ↓
   ┌─────────────────────┐
   │ LOGIN CENTRALIZADO  │
   │                     │
   │ Email/Password      │
   │ OAuth (Google, etc) │
   └──────────┬──────────┘
              │
              ▼
     Genera JWT Token
              │
    ┌─────────┼─────────┐
    │                   │
    ▼                   ▼
Detecta ROL      Guarda en cookie
    │            (compartida entre
    │             subdominios)
    │
    ├─ Escritor   → Redirige a escritores.drakkarpress.com
    ├─ Revendedor → Redirige a afiliados.drakkarpress.com
    ├─ Imprenta   → Redirige a imprentas.drakkarpress.com
    └─ Lector     → Redirige a www.drakkarpress.com/biblioteca
```

### Implementación SSO

```java
@Service
public class AuthService {
    
    public LoginResponse login(String email, String password) {
        // 1. Validar credenciales
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new InvalidCredentialsException());
        
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new InvalidCredentialsException();
        }
        
        // 2. Generar JWT Token
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        
        // 3. Determinar portal según rol
        String redirectUrl = getPortalUrlByRole(user.getRole());
        
        return LoginResponse.builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .redirectUrl(redirectUrl)
            .user(UserDTO.from(user))
            .build();
    }
    
    private String getPortalUrlByRole(UserRole role) {
        return switch (role) {
            case WRITER -> "https://escritores.drakkarpress.com";
            case RESELLER -> "https://afiliados.drakkarpress.com";
            case PRINTER -> "https://imprentas.drakkarpress.com";
            case READER -> "https://www.drakkarpress.com/biblioteca";
            case ADMIN -> "https://admin.drakkarpress.com";
        };
    }
}
```

### Configuración de Cookies

```javascript
// Cookie compartida entre subdominios
document.cookie = `access_token=${token}; domain=.drakkarpress.com; path=/; secure; samesite=strict`;
```

---

## 📁 Estructura del Proyecto (Monorepo)

```
drakkarpress/
├── apps/
│   ├── marketplace/              # www.drakkarpress.com
│   │   ├── pages/
│   │   ├── components/
│   │   ├── public/
│   │   └── next.config.js
│   │
│   ├── writer-portal/            # escritores.drakkarpress.com
│   │   ├── pages/
│   │   ├── components/
│   │   ├── features/
│   │   │   ├── BookManager/
│   │   │   ├── AITools/
│   │   │   ├── Analytics/
│   │   │   └── Royalties/
│   │   └── next.config.js
│   │
│   ├── reseller-portal/          # afiliados.drakkarpress.com
│   │   ├── pages/
│   │   ├── components/
│   │   ├── features/
│   │   │   ├── LinkGenerator/
│   │   │   ├── MarketingTools/
│   │   │   └── Commissions/
│   │   └── next.config.js
│   │
│   ├── printer-portal/           # imprentas.drakkarpress.com
│   │   ├── pages/
│   │   ├── components/
│   │   ├── features/
│   │   │   ├── OrderManager/
│   │   │   ├── FileDownloader/
│   │   │   └── TrackingUpdater/
│   │   └── next.config.js
│   │
│   └── reader-portal/            # biblioteca.drakkarpress.com
│       ├── pages/
│       ├── components/
│       └── next.config.js
│
├── packages/                     # Código compartido
│   ├── ui/                       # Componentes UI compartidos
│   │   ├── Button/
│   │   ├── Card/
│   │   ├── Modal/
│   │   ├── Table/
│   │   └── index.ts
│   │
│   ├── common/                   # Utilidades compartidas
│   │   ├── utils/
│   │   ├── hooks/
│   │   ├── types/
│   │   └── constants/
│   │
│   ├── api-client/               # Cliente API compartido
│   │   ├── books.ts
│   │   ├── users.ts
│   │   ├── orders.ts
│   │   └── index.ts
│   │
│   └── auth/                     # Lógica de autenticación
│       ├── AuthProvider.tsx
│       ├── useAuth.ts
│       ├── ProtectedRoute.tsx
│       └── index.ts
│
├── services/                     # Backend (Java Spring Boot)
│   ├── user-service/
│   ├── book-service/
│   ├── order-service/
│   └── ... (otros microservicios)
│
├── package.json                  # Root package.json
├── turbo.json                    # Configuración Turborepo
└── docker-compose.yml            # Servicios locales
```

---

## 🚀 Deployment Independiente

### Cada portal se despliega por separado:

```yaml
# Vercel/Netlify config para cada app

# apps/marketplace/vercel.json
{
  "domains": ["www.drakkarpress.com", "drakkarpress.com"],
  "buildCommand": "cd ../.. && npm run build:marketplace",
  "framework": "nextjs"
}

# apps/writer-portal/vercel.json
{
  "domains": ["escritores.drakkarpress.com"],
  "buildCommand": "cd ../.. && npm run build:writer",
  "framework": "nextjs"
}

# apps/reseller-portal/vercel.json
{
  "domains": ["afiliados.drakkarpress.com"],
  "buildCommand": "cd ../.. && npm run build:reseller",
  "framework": "nextjs"
}

# apps/printer-portal/vercel.json
{
  "domains": ["imprentas.drakkarpress.com"],
  "buildCommand": "cd ../.. && npm run build:printer",
  "framework": "nextjs"
}
```

---

## 🔄 Comunicación entre Portales

### Redirecciones Cross-Portal

```javascript
// Desde marketplace → portal escritor
// www.drakkarpress.com/publicar-libro
export default function PublishRedirect() {
  useEffect(() => {
    // Preservar token en redirección
    const token = getAuthToken();
    window.location.href = `https://escritores.drakkarpress.com?token=${token}`;
  }, []);
  
  return <LoadingSpinner />;
}
```

### Navegación Unificada

```javascript
// Componente Header compartido con links a otros portales
const PortalSwitcher = () => {
  const { user } = useAuth();
  
  return (
    <div className="portal-links">
      <a href="https://www.drakkarpress.com">Marketplace</a>
      {user.role === 'WRITER' && (
        <a href="https://escritores.drakkarpress.com">Mi Panel de Escritor</a>
      )}
      {user.role === 'RESELLER' && (
        <a href="https://afiliados.drakkarpress.com">Mi Panel de Afiliado</a>
      )}
      {user.role === 'PRINTER' && (
        <a href="https://imprentas.drakkarpress.com">Mi Panel de Imprenta</a>
      )}
    </div>
  );
};
```

---

## 🎨 Branding por Portal

### Cada portal mantiene identidad pero con variaciones:

```css
/* Marketplace - Colores principales */
:root {
  --primary: #1A4D7A;
  --accent: #D4AF37;
}

/* Portal Escritores - Tinte azul escritor */
:root {
  --primary: #1A4D7A;
  --accent: #3498DB; /* Azul escritor */
  --bg-tint: rgba(52, 152, 219, 0.05);
}

/* Portal Revendedores - Tinte verde */
:root {
  --primary: #1A4D7A;
  --accent: #27AE60; /* Verde revendedor */
  --bg-tint: rgba(39, 174, 96, 0.05);
}

/* Portal Imprentas - Tinte naranja */
:root {
  --primary: #1A4D7A;
  --accent: #E67E22; /* Naranja imprenta */
  --bg-tint: rgba(230, 126, 34, 0.05);
}
```

---

## 📊 Ventajas de Multi-Sitio

### ✅ **Performance**
- Cada portal es más ligero (solo carga lo necesario)
- Mejor Core Web Vitals
- Cache específico por portal

### ✅ **SEO**
- URLs específicas y relevantes
- Contenido optimizado por audiencia
- Mejor ranking por nicho

### ✅ **UX**
- Interfaz especializada por rol
- Sin navegación innecesaria
- Flujos optimizados

### ✅ **Desarrollo**
- Equipos pueden trabajar independientemente
- Deploys sin afectar otros portales
- Testing aislado

### ✅ **Seguridad**
- Aislamiento por rol
- Menos superficie de ataque
- Permisos granulares

---

## 🛠️ Scripts de Build

```json
// package.json (root)
{
  "scripts": {
    "dev": "turbo run dev",
    "dev:marketplace": "turbo run dev --filter=marketplace",
    "dev:writer": "turbo run dev --filter=writer-portal",
    "dev:reseller": "turbo run dev --filter=reseller-portal",
    "dev:printer": "turbo run dev --filter=printer-portal",
    
    "build": "turbo run build",
    "build:marketplace": "turbo run build --filter=marketplace",
    "build:writer": "turbo run build --filter=writer-portal",
    "build:reseller": "turbo run build --filter=reseller-portal",
    "build:printer": "turbo run build --filter=printer-portal",
    
    "test": "turbo run test",
    "lint": "turbo run lint"
  }
}
```

---

## 📈 Monitoreo y Analytics

### Cada portal tiene su propio tracking:

```javascript
// Google Analytics por portal
const GA_IDS = {
  marketplace: 'G-XXXXXXXXX1',
  writer: 'G-XXXXXXXXX2',
  reseller: 'G-XXXXXXXXX3',
  printer: 'G-XXXXXXXXX4'
};

// Mixpanel/Amplitude con proyectos separados
const trackEvent = (event, properties) => {
  mixpanel.track(event, {
    ...properties,
    portal: getCurrentPortal(),
    user_role: getCurrentUserRole()
  });
};
```

---

## ✅ Checklist de Implementación

### Fase 1: Infraestructura
- [ ] Configurar monorepo (Turborepo)
- [ ] Setup DNS y subdominios
- [ ] Configurar SSO centralizado
- [ ] Crear packages compartidos

### Fase 2: Portales Base
- [ ] Marketplace (público)
- [ ] Portal Escritores
- [ ] Portal Revendedores
- [ ] Portal Imprentas

### Fase 3: Integración
- [ ] Implementar SSO entre portales
- [ ] Sistema de navegación cross-portal
- [ ] Shared components library
- [ ] API client unificado

### Fase 4: Deploy
- [ ] CI/CD por portal
- [ ] Configurar CDN
- [ ] Monitoreo independiente
- [ ] Testing E2E cross-portal

---

**Versión:** 1.0  
**Última actualización:** 9 nov 2025  
**Próxima revisión:** Setup inicial de monorepo
