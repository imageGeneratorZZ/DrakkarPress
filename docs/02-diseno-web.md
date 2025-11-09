# Diseño Web - www.drakkarpress.com

## Concepto: Flota Editorial Digital

DrakkarPress es una **flota editorial digital** que conecta cuatro tipos de usuarios en una comunidad global:

### 🚢 Los 4 Tipos de Usuario

#### 1. **✍️ Escritores / Autores**
- **Función:** Crear y subir libros a la plataforma
- **Herramientas:** IA de DrakkarPress para generar ideas, extender textos, crear sinopsis
- **Ingresos:** **90%** en ventas directas o **60%** cuando hay revendedor (plataforma retiene 10%)
- **Canales:** Sus libros se venden automáticamente en Shopify, MercadoLibre y tienda DrakkarPress

#### 2. **💼 Revendedores (Afiliados que venden libros)**
- **Función:** Armar catálogos personalizados de libros para vender en sus redes
- **Herramientas:** 
  - Eligen qué libros incluir de todo el catálogo
  - Generan enlaces con tracking (cada clic y venta se registra)
  - Códigos QR personalizados
  - IA genera contenido para redes sociales (posts, stories, copies)
- **Ingresos:** **30%** de comisión por cada venta (plataforma retiene 10%, autor recibe 60%)
- **Ventaja:** Sin inventario, sin inversión inicial

#### 3. **🏭 Red de Imprentas (Impresión bajo demanda)**
- **Función:** Recibir pedidos automáticos y imprimir libros localmente
- **Ubicación:** Imprentas distribuidas en distintos países para envío rápido
- **Flujo:**
  - Reciben pedido con archivos del libro (PDF interior + portada)
  - Imprimen según especificaciones (tapa blanda/dura, color/B&N)
  - Envían al cliente local
  - Actualizan tracking en la plataforma
- **Ingresos:** Pago por cada trabajo de impresión realizado
- **Ventaja:** Flujo constante de trabajo, pagos automáticos

#### 4. **📚 Clientes / Lectores**
- **Función:** Comprar libros en formato digital o físico
- **Experiencia:**
  - Compran en Shopify, MercadoLibre o tienda DrakkarPress
  - Si eligen digital: Descarga inmediata, biblioteca personal
  - Si eligen físico: **🎁 Reciben PDF gratis por email + libro impreso en 3-7 días**
- **Beneficio:** 
  - Impresión local = envío más rápido y económico
  - **Compra física incluye versión digital gratis** (empieza a leer al instante)

### 🎯 La Innovación: Portal Inmobiliario para Libros

DrakkarPress adopta el modelo exitoso de **portales inmobiliarios** (Idealista, Fotocasa, Zillow) aplicado al mundo editorial:

- **Búsqueda avanzada** con múltiples filtros (como buscar casas)
- **Fichas detalladas** de cada libro (como anuncios de propiedades)
- **Mapa de imprentas** por ubicación geográfica (como mapa de propiedades)
- **Alertas personalizadas** para nuevos libros en categorías favoritas
- **Comparador de precios** entre formatos (digital vs impreso) y marketplaces
- **Sistema de publicación** por escritores (como inmobiliarias publican propiedades)

## Visión de Diseño

DrakkarPress presenta una interfaz moderna tipo marketplace editorial que transmite confianza y descubrimiento. La web debe ser:
- **Intuitiva:** Búsqueda potente como portal inmobiliario
- **Responsive:** Optimizada para móvil, tablet y desktop
- **Rápida:** Tiempos de carga < 2 segundos
- **Accesible:** WCAG 2.1 nivel AA
- **Orientada a búsqueda:** El buscador es el elemento principal

## Página Principal (Home)

URL: `https://www.drakkarpress.com`

### Estructura de Secciones

#### 1. Header / Navegación Principal

**Layout:** Fixed top navbar con buscador prominente

**Elementos:**
```
┌────────────────────────────────────────────────────────────────────────┐
│ [Logo] │ [🔍 Buscar libros, autores, ISBN...        ] [🔍] │ Publicar │
│         │                                                    │ Mi Cuenta│
│         │ Comprar  Vender  Imprentas  IA  Ayuda            │ ❤️ (3)    │
└────────────────────────────────────────────────────────────────────────┘
```

**Menú principal:**
- **Comprar** → Catálogo de libros
- **Vender** → Panel de escritor/revendedor
- **Imprentas** → Red de imprentas (mapa)
- **IA** → Herramientas de IA de DrakkarPress
- **Ayuda** → FAQ y soporte
- **Publicar** (botón destacado) → Publicar nuevo libro
- **Mi Cuenta** → Dashboard personalizado
- **❤️ Favoritos** → Lista de libros guardados

**Responsive:**
- Desktop: Buscador central prominente
- Mobile: Buscador expandible + hamburger menu

---

#### 2. Hero / Buscador Principal

**Diseño visual inspirado en portales inmobiliarios:**
- Fondo: Imagen hero de biblioteca moderna (overlay oscuro)
- Altura: 60vh (más compacta, enfoque en búsqueda)
- Buscador GRANDE y prominente (como Idealista)

**Contenido:**

```
╔════════════════════════════════════════════════════════════════════╗
║                                                                    ║
║                    🚢 DRAKKARPRESS                                 ║
║              Encuentra tu próximo libro perfecto                   ║
║                                                                    ║
║  ┌────────────────────────────────────────────────────────────┐   ║
║  │ 🔍  ¿Qué libro buscas?                               [🔍]  │   ║
║  └────────────────────────────────────────────────────────────┘   ║
║                                                                    ║
║  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐ ┌──────────┐   ║
║  │Categoría ▾  │ │ Formato ▾   │ │ Precio ▾    │ │ Más ▾    │   ║
║  └─────────────┘ └─────────────┘ └─────────────┘ └──────────┘   ║
║                                                                    ║
║              [🔍 BUSCAR LIBROS]  [Búsqueda avanzada →]           ║
║                                                                    ║
║  📊 En DrakkarPress ahora:                                         ║
║  124,583 libros | 8,421 autores | 326 imprentas en 45 países     ║
║                                                                    ║
╚════════════════════════════════════════════════════════════════════╝
```

**Filtros rápidos en el buscador:**

1. **Categoría** (dropdown):
   - Todas las categorías
   - Scryptorium (Infantil)
   - Erótica
   - Thriller/Suspenso
   - Romance
   - Fantasía/Sci-Fi
   - Cocina
   - No Ficción

2. **Formato** (dropdown):
   - Cualquiera
   - Digital (eBook)
   - Impreso (Tapa blanda)
   - Impreso (Tapa dura)
   - Audiolibro

3. **Precio** (dropdown):
   - Cualquier precio
   - Gratis
   - Menos de $5
   - $5 - $15
   - $15 - $30
   - Más de $30

4. **Más filtros** (modal):
   - Idioma
   - Año de publicación
   - Editorial
   - País de impresión disponible
   - Rating mínimo
   - Con descuento
   - Bestsellers
   - Novedades (últimos 30 días)

**Búsqueda avanzada** (modal completo):
- Todos los filtros anteriores
- Número de páginas (rango)
- ISBN específico
- Autor específico
- Palabras clave en sinopsis
- Ordenar por: Relevancia, Precio, Fecha, Popularidad, Rating

**Elementos adicionales:**
- Suggestions mientras escribes (autocomplete)
- Búsquedas recientes
- Búsquedas populares

---

#### 3. Sección "Libros Destacados" (Resultados Tipo Portal)

**Diseño similar a listado de propiedades:**

```
┌───────────────────────────────────────────────────────────────┐
│  📚 Libros destacados esta semana                             │
│  [Ver todos →]                              [Grid] [Lista] ▾  │
├───────────────────────────────────────────────────────────────┤
│                                                               │
│  ┌─────────────────────┐  ┌─────────────────────┐           │
│  │ [IMG PORTADA]       │  │ [IMG PORTADA]       │           │
│  │                     │  │                     │           │
│  │ Romance | 324 págs  │  │ Thriller | 456 págs │           │
│  │ ⭐⭐⭐⭐⭐ 4.8 (234)  │  │ ⭐⭐⭐⭐☆ 4.5 (89)   │           │
│  │                     │  │                     │           │
│  │ El Secreto del Mar  │  │ Noche en Paris      │           │
│  │ por Ana García      │  │ por Carlos López    │           │
│  │                     │  │                     │           │
│  │ 💲 $12.99 Digital   │  │ 💲 $15.99 Digital   │           │
│  │ 💲 $24.99 Impreso   │  │ 💲 $28.99 Impreso   │           │
│  │                     │  │                     │           │
│  │ 📍 Impresión: MX,ES │  │ 📍 Impresión: AR,CL │           │
│  │ 🚚 3-5 días         │  │ 🚚 2-4 días         │           │
│  │                     │  │                     │           │
│  │ [❤️ Guardar][🛒 Comprar]│[❤️ Guardar][🛒 Comprar]        │
│  └─────────────────────┘  └─────────────────────┘           │
│                                                               │
│  ┌─────────────────────┐  ┌─────────────────────┐           │
│  │ [IMG PORTADA]       │  │ [IMG PORTADA]       │           │
│  │ ...                 │  │ ...                 │           │
└───────────────────────────────────────────────────────────────┘
```

**Información en cada card (tipo anuncio inmobiliario):**
- 📸 Portada grande (hover: zoom)
- 🏷️ Categoría + Páginas
- ⭐ Rating y número de reseñas
- 📖 Título del libro
- ✍️ Autor
- 💲 Precio digital y físico
- 📍 Países donde hay impresión disponible
- 🚚 Tiempo estimado de entrega
- ❤️ Botón guardar en favoritos
- 🛒 Botón comprar rápido
- 🔗 Icono si está en MercadoLibre/Shopify

**Vistas disponibles:**
- Grid (2-4 columnas según dispositivo)
- Lista (detalle completo)
- Compacta (solo esenciales)

---

#### 4. Sección "Buscar por Categoría" (Estilo Portal)

**Diseño:**
- Grid de categorías con estadísticas

```
┌───────────────────────────────────────────────────────────┐
│  🗂️ Explora por Categoría                                 │
├───────────────────────────────────────────────────────────┤
│                                                           │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐   │
│  │  🧒 SCRYPTO  │  │  🔥 ERÓTICA  │  │  🔪 THRILLER │   │
│  │              │  │              │  │              │   │
│  │  12,483      │  │  8,942       │  │  15,678      │   │
│  │  libros      │  │  libros      │  │  libros      │   │
│  │              │  │              │  │              │   │
│  │ Desde $4.99  │  │ Desde $9.99  │  │ Desde $7.99  │   │
│  │ [Explorar →] │  │ [Explorar →] │  │ [Explorar →] │   │
│  └──────────────┘  └──────────────┘  └──────────────┘   │
│                                                           │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐   │
│  │  💕 ROMANCE  │  │  🚀 FANTASÍA │  │  🍳 COCINA   │   │
│  │  ...         │  │  ...         │  │  ...         │   │
└───────────────────────────────────────────────────────────┘
```

---

#### 5. Sección "Mapa de Imprentas" (Innovación Clave)

**Diseño tipo mapa inmobiliario:**

```
┌───────────────────────────────────────────────────────────┐
│  🗺️ Red Global de Imprentas DrakkarPress                  │
│  Impresión local, entrega rápida                          │
├───────────────────────────────────────────────────────────┤
│                                                           │
│  [MAPA INTERACTIVO MUNDIAL]                               │
│  • Marcadores por país con número de imprentas            │
│  • Click en país = lista de imprentas disponibles         │
│  • Tiempo promedio de impresión y envío                   │
│  • Hover = preview info de imprenta                       │
│                                                           │
│  📍 326 imprentas en 45 países                            │
│  🚚 Entrega promedio: 3-7 días                            │
│  ⚡ Impresión bajo demanda                                 │
│                                                           │
│  ┌────────────────────────────────────┐                  │
│  │ Busca imprentas en tu zona:        │                  │
│  │ [🔍 País, ciudad o código postal]  │                  │
│  └────────────────────────────────────┘                  │
│                                                           │
│  [Ver lista completa de imprentas →]                      │
└───────────────────────────────────────────────────────────┘
```

**Funcionalidades del mapa:**
- Zoom interactivo
- Filtros: Tapa blanda, Tapa dura, Color, B&N
- Tiempos de entrega por zona
- Costos de envío estimados
- Ratings de imprentas

---

#### 6. Sección "IA de DrakkarPress"

**Diseño:**
- Banner horizontal con demo interactiva
- Menos prominente que antes, pero accesible

```
┌──────────────────────────────────────────────────────────┐
│  🤖 Crea tu libro con IA de DrakkarPress                 │
│  ────────────────────────────────────────────────────    │
│                                                          │
│  ✨ Genera ideas  📝 Extiende textos  🎯 Crea sinopsis  │
│  🎨 Diseña estructuras  📢 Marketing inteligente        │
│                                                          │
│  [⚡ Probar IA gratis] [Ver ejemplos →]                  │
└──────────────────────────────────────────────────────────┘
```

---

---

#### 7. Sección "La Comunidad DrakkarPress" (Flujo Visual)

**Diseño:**
- Explicación visual del ecosistema

```
┌───────────────────────────────────────────────────────────────┐
│          🌐 Cómo Funciona la Flota DrakkarPress              │
│                                                               │
│  ┌───────────┐        ┌───────────┐        ┌───────────┐   │
│  │ ✍️ AUTOR  │───────▶│ 📚 LIBRO  │───────▶│ 🛒 VENTA  │   │
│  │           │ publica│           │ aparece │           │   │
│  │ Crea y    │        │ • Shopify │   en    │ Cliente   │   │
│  │ sube su   │        │ • MercLi  │         │ compra    │   │
│  │ libro     │        │ • DP.com  │         │           │   │
│  └───────────┘        └─────┬─────┘        └─────┬─────┘   │
│       ▲                     │                     │          │
│       │                     │                     │          │
│       │              ┌──────▼─────┐        ┌─────▼─────┐   │
│       │              │ 💼 AFILIADO│        │ 🏭 IMPRENTA│   │
│       │              │            │        │            │   │
│       │              │ Promociona │        │ Si es      │   │
│       │              │ en sus     │        │ físico:    │   │
│       │              │ redes      │        │ imprime    │   │
│       │              │            │        │ y envía    │   │
│       │              └────────────┘        └────────────┘   │
│       │                     │                     │          │
│       │                     └──────────┬──────────┘          │
│       │                                │                     │
│       └────────────────────────────────┘                     │
│              Todos reciben su parte:                         │
│              Autor 70% | Afiliado 15% | Imprenta $X          │
└───────────────────────────────────────────────────────────────┘
```

**Explicación en 4 Columnas:**

```
┌────────────────┬────────────────┬────────────────┬───────────────┐
│ ✍️ ESCRITORES  │ 💼 REVENDEDORES│ 🏭 IMPRENTAS   │ 📚 CLIENTES   │
├────────────────┼────────────────┼────────────────┼───────────────┤
│ CREAN          │ VENDEN         │ IMPRIMEN       │ COMPRAN       │
│                │                │                │               │
│ • Escriben y   │ • Arman su     │ • Reciben      │ • Buscan      │
│   publican     │   catálogo     │   pedidos      │   libros      │
│   libros       │                │   automáticos  │               │
│                │ • Generan      │                │ • Compran     │
│ • Usan IA para │   enlaces con  │ • Imprimen     │   digital o   │
│   mejorar      │   tracking     │   y envían     │   impreso     │
│   contenido    │                │                │               │
│                │ • Comparten en │ • Actualizan   │ • Reciben en  │
│ • Suben a      │   redes        │   estado       │   3-7 días    │
│   DrakkarPress │   sociales     │                │   (impreso)   │
│                │                │ • Cobran por   │               │
│ • Reciben      │ • Cobran       │   cada trabajo │ • Descargan   │
│   60-70% de    │   15-20% de    │                │   (digital)   │
│   regalías     │   comisión     │ • Pagos        │               │
│                │                │   automáticos  │ • Dejan       │
│ • Sin inventar │ • Sin          │                │   reseñas     │
│ • Sin imprimir │   inventario   │ • Flujo        │               │
│                │ • Sin inversión│   constante    │               │
└────────────────┴────────────────┴────────────────┴───────────────┘
```

**CTA:** "Únete a la flota" → `/registro`

---

#### 8. Sección "Últimas Novedades" (Feed Tipo Portal)

**Diseño lista compacta:**

```
┌───────────────────────────────────────────────────────────┐
│  📅 Nuevos esta semana                [Ver todos →]       │
├───────────────────────────────────────────────────────────┤
│                                                           │
│  [IMG] El Arte del Engaño          ⭐ 4.9  $14.99       │
│        Thriller • María Santos      🛒 Comprar            │
│  ─────────────────────────────────────────────────────    │
│  [IMG] Recetas de la Abuela        ⭐ 4.7  $19.99       │
│        Cocina • Jorge Ramírez       🛒 Comprar            │
│  ─────────────────────────────────────────────────────    │
│  [IMG] Amor en Tiempo de Guerra    ⭐ 4.8  $12.99       │
│        Romance • Ana Belén          🛒 Comprar            │
│                                                           │
└───────────────────────────────────────────────────────────┘
```

---

#### 9. Sección "Estadísticas y Confianza"

**Diseño:**
- Contadores animados + badges

```
┌───────────────────────────────────────────────────────────┐
│                                                           │
│  📊 DrakkarPress en números                               │
│                                                           │
│  124,583          8,421           326           45       │
│  📚 Libros       ✍️ Autores      🏭 Imprentas   🌍 Países│
│                                                           │
│  ✅ Integrado con:                                        │
│  [Logo Shopify] [Logo MercadoLibre] [Logo Lulu.com]     │
│                                                           │
│  🔒 Pago seguro | 📦 Envío trackeable | ⚡ Impresión 24h │
│                                                           │
└───────────────────────────────────────────────────────────┘
```

---

#### 6. Sección "Cómo Funciona"

**Diseño:** Timeline horizontal (desktop) / vertical (mobile)

**Pasos:**

```
1️⃣ REGÍSTRATE                2️⃣ CREA                  3️⃣ PUBLICA
   Elige tu rol:                Con ayuda de IA:          En múltiples
   • Escritor                   • Escribe                 formatos:
   • Revendedor                 • Diseña                  • Digital
   • Imprenta                   • Optimiza                • Impreso
   • Lector                                               • Audiolibro*
   
4️⃣ VENDE                     5️⃣ GANA                   6️⃣ CRECE
   Tu libro llega a:            Recibe:                   Expande tu:
   • Tienda global              • Regalías                • Audiencia
   • Afiliados                  • Comisiones              • Catálogo
   • Marketplaces               • Reportes                • Ingresos
```

---

#### 7. Sección "Testimonios"

**Diseño:** Carrusel de cards con fotos (pueden ser ilustraciones inicialmente)

**Estructura de testimonio:**

```
┌─────────────────────────────────────┐
│  "Texto del testimonio sobre        │
│   su experiencia con DrakkarPress"  │
│                                     │
│   [Foto]  Nombre Apellido           │
│           Rol | País                │
│           ⭐⭐⭐⭐⭐                   │
└─────────────────────────────────────┘
```

**Ejemplos (placeholder):**

1. **María García** - Escritora | España
   - "Publiqué mi primer libro infantil en Scryptorium y en 3 meses vendí 500 copias gracias a la red de afiliados."

2. **Carlos Mendoza** - Revendedor | México
   - "Como afiliado de DrakkarPress, genero ingresos pasivos promocionando libros en mis redes. Muy recomendado."

3. **Editorial PrintFast** - Imprenta | Argentina
   - "Recibimos pedidos automáticos y el sistema es muy fácil de usar. Excelente integración."

---

#### 8. Sección "Precios y Planes"

**Diseño:** Tabla comparativa o cards lado a lado

**Estructura:**

```
┌────────────┬────────────┬────────────┬────────────┐
│   GRATIS   │  ESCRITOR  │ REVENDEDOR │  IMPRENTA  │
│            │    PRO     │    PRO     │    PRO     │
├────────────┼────────────┼────────────┼────────────┤
│   $0/mes   │  $19/mes   │  $29/mes   │  $49/mes   │
├────────────┼────────────┼────────────┼────────────┤
│ • 1 libro  │ • Ilimitado│ • Catálogo │ • Pedidos  │
│ • 60%      │   libros   │   amplio   │   ilimita- │
│   regalías │ • 70%      │ • 15%      │   dos      │
│ • IA       │   regalías │   comisión │ • Panel    │
│   básica   │ • IA Pro   │ • IA       │   avanzado │
│            │ • Analytics│   marketing│ • API      │
│            │ • Prioridad│ • Reportes │   integra- │
│            │            │            │   ción     │
└────────────┴────────────┴────────────┴────────────┘
```

**Nota:** Los precios son ejemplos, ajustar según modelo de negocio

**Para lectores:** Siempre gratuito registrarse y comprar

---

#### 9. Sección "FAQ"

**Diseño:** Acordeón expandible

**Preguntas frecuentes:**

1. **¿Qué es DrakkarPress?**
   - R: Una plataforma editorial que conecta autores, revendedores, imprentas y lectores...

2. **¿Cómo funciona la IA de DrakkarPress?**
   - R: Es un asistente de escritura que te ayuda a generar ideas, extender textos...

3. **¿Cuánto cuesta publicar un libro?**
   - R: Puedes empezar gratis con 1 libro. Los planes Pro...

4. **¿Cómo funcionan las regalías?**
   - R: Los autores reciben entre 60-70% del precio de venta...

5. **¿Qué es Scryptorium?**
   - R: Nuestra categoría especializada en libros infantiles...

6. **¿Puedo vender en mi país?**
   - R: Sí, DrakkarPress tiene presencia global con imprentas en múltiples países...

7. **¿Necesito ser escritor profesional?**
   - R: No, cualquier persona puede publicar. La IA te ayuda...

8. **¿Cómo me uno como revendedor?**
   - R: Regístrate como afiliado, elige tu catálogo y empieza a compartir...

---

#### 10. Footer

**Diseño:** Dark footer con múltiples columnas

```
┌─────────────────────────────────────────────────────────────┐
│                                                             │
│  [Logo DrakkarPress]                                        │
│  La flota editorial global                                  │
│                                                             │
│  PLATAFORMA      CATEGORÍAS        SOPORTE       LEGAL      │
│  • Para autores  • Scryptorium     • FAQ         • Términos│
│  • Para afilia. • Erótica          • Contacto    • Privaci.│
│  • Para impren. • Thriller         • Tutorial    • Cookies │
│  • Catálogo     • Romance          • Blog        • Derechos│
│                  • Fantasía                                 │
│                  • Cocina                                   │
│                  • No ficción                               │
│                                                             │
│  🌐 Idioma: Español ▾                                       │
│                                                             │
│  SÍGUENOS                                                   │
│  [📘 Facebook] [📷 Instagram] [🐦 Twitter] [💼 LinkedIn]    │
│  [📺 YouTube] [📱 TikTok]                                   │
│                                                             │
│  ───────────────────────────────────────────────────────    │
│  © 2025 DrakkarPress. Todos los derechos reservados.       │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

---

## Páginas de Categorías

### Página de Categoría Individual

**URL:** `/categoria/{categoria-slug}`

**Ejemplo:** `/categoria/scryptorium`

**Estructura:**

```
┌─────────────────────────────────────────────────────────┐
│  [Breadcrumb: Home > Categorías > Scryptorium]          │
│                                                         │
│  🧒 Scryptorium                                         │
│  Libros infantiles y para colorear                      │
│  ──────────────────────────────────────                 │
│                                                         │
│  [Hero con ilustración temática]                        │
│                                                         │
│  📖 Sobre esta categoría                                │
│  Texto descriptivo extenso (3-4 párrafos) sobre        │
│  el tipo de libros, audiencia, estilo, etc.            │
│                                                         │
│  ✍️ ¿Quieres publicar en Scryptorium?                  │
│  [Botón: Empieza tu libro infantil]                    │
│                                                         │
│  🤖 IA especializada                                    │
│  La IA de DrakkarPress está optimizada para:           │
│  • Generar ideas de historias infantiles               │
│  • Proponer estructuras de libros para colorear        │
│  • Sugerir personajes y tramas apropiadas              │
│  • Crear descripciones llamativas para padres          │
│                                                         │
│  📚 Libros destacados en Scryptorium                    │
│  [Grid de libros con portadas, autor, precio]          │
│                                                         │
│  🎯 Ejemplos de títulos exitosos                        │
│  1. "El dragón que pintaba arcoíris"                    │
│  2. "Mi primer libro de colorear: Animales"            │
│  3. "Aventuras en el bosque mágico"                     │
│                                                         │
│  💡 Tips para autores de Scryptorium                    │
│  • Usa ilustraciones llamativas                         │
│  • Escribe para edades específicas (3-5, 6-8, etc)     │
│  • Incluye elementos educativos                         │
│                                                         │
└─────────────────────────────────────────────────────────┘
```

**Funcionalidades:**
- Filtros: Precio, edad recomendada, idioma, más vendidos
- Búsqueda dentro de la categoría
- "Agregar al carrito" desde la vista de grid

---

## Páginas de Usuario Público

### Página de Resultados / Catálogo (Estilo Idealista)

**URL:** `/catalogo` o `/buscar?q={query}`

**Layout tipo portal inmobiliario:**

```
┌────────────────────────────────────────────────────────────────────┐
│ [Header con buscador]                                              │
├────────────────────────────────────────────────────────────────────┤
│                                                                    │
│ ┌─────────────┐  ┌─────────────────────────────────────────────┐ │
│ │             │  │                                             │ │
│ │  FILTROS    │  │  Mostrando 1,234 libros                     │ │
│ │  ─────────  │  │  [Grid] [Lista]  Ordenar: Relevancia ▾     │ │
│ │             │  │  ───────────────────────────────────────    │ │
│ │ Categoría   │  │                                             │ │
│ │ □ Scrypto   │  │  ┌─────────┐  ┌─────────┐  ┌─────────┐    │ │
│ │ □ Erótica   │  │  │  [IMG]  │  │  [IMG]  │  │  [IMG]  │    │ │
│ │ □ Thriller  │  │  │         │  │         │  │         │    │ │
│ │ □ Romance   │  │  │ Título  │  │ Título  │  │ Título  │    │ │
│ │ ...         │  │  │ Autor   │  │ Autor   │  │ Autor   │    │ │
│ │             │  │  │ ⭐ 4.8  │  │ ⭐ 4.5  │  │ ⭐ 4.9  │    │ │
│ │ Formato     │  │  │ $12.99  │  │ $15.99  │  │ $9.99   │    │ │
│ │ ☑ Digital   │  │  │ [🛒][❤️]│  │ [🛒][❤️]│  │ [🛒][❤️] │    │ │
│ │ □ Impreso   │  │  └─────────┘  └─────────┘  └─────────┘    │ │
│ │             │  │                                             │ │
│ │ Precio      │  │  ┌─────────┐  ┌─────────┐  ┌─────────┐    │ │
│ │ ◄═══●═══►   │  │  │  ...    │  │  ...    │  │  ...    │    │ │
│ │ $0 - $50    │  │  └─────────┘  └─────────┘  └─────────┘    │ │
│ │             │  │                                             │ │
│ │ Idioma      │  │  [Paginación: 1 2 3 ... 42 →]              │ │
│ │ ☑ Español   │  │                                             │ │
│ │ □ Inglés    │  └─────────────────────────────────────────────┘ │
│ │ □ Francés   │                                                  │
│ │             │                                                  │
│ │ Rating      │                                                  │
│ │ ☑ 4+ ⭐     │                                                  │
│ │ □ 3+ ⭐     │                                                  │
│ │             │                                                  │
│ │ Disponibil. │                                                  │
│ │ □ En stock  │                                                  │
│ │ □ Pre-orden │                                                  │
│ │             │                                                  │
│ │ Autor       │                                                  │
│ │ [Buscar...] │                                                  │
│ │             │                                                  │
│ │ País impres.│                                                  │
│ │ [🗺️ Ver]   │                                                  │
│ │             │                                                  │
│ │[Limpiar]    │                                                  │
│ └─────────────┘                                                  │
│                                                                    │
└────────────────────────────────────────────────────────────────────┘
```

**Características clave:**

1. **Filtros avanzados (sidebar izquierdo):**
   - Categorías (checkbox múltiple)
   - Formato (digital/impreso/audio)
   - Rango de precio (slider)
   - Idioma
   - Rating mínimo
   - Disponibilidad
   - Búsqueda por autor
   - País de impresión (con mapa)
   - Número de páginas
   - Año de publicación
   - Editorial
   - Con descuento
   - Bestsellers
   - Novedades

2. **Área de resultados:**
   - Vista Grid o Lista (toggle)
   - Ordenamiento: Relevancia, Precio (↑↓), Fecha, Rating, Popularidad
   - Contador de resultados
   - Paginación o scroll infinito

3. **Cada card de libro muestra:**
   - Badge "NUEVO" / "OFERTA" / "BESTSELLER"
   - Portada
   - Título + Autor
   - Rating y número de reseñas
   - Precio(s)
   - Botón compra rápida
   - Botón favoritos
   - Icono plataforma (Shopify/MercadoLibre)
   - Disponibilidad impresión (países)

4. **Acciones rápidas:**
   - Comparar (hasta 3 libros)
   - Guardar búsqueda (crear alerta)
   - Compartir resultados
   - Exportar lista

---

### Vista de Lista (Alternativa)

**Layout horizontal con más información:**

```
┌──────────────────────────────────────────────────────────────┐
│  ┌────┐  EL SECRETO DEL MAR            ⭐⭐⭐⭐⭐ 4.8 (234)   │
│  │IMG │  por Ana García • Romance                           │
│  │    │  324 páginas • Español • Editorial DrakkarPress     │
│  └────┘  "Una historia de amor que transcurre en..."        │
│           💲 $12.99 Digital | $24.99 Impreso                │
│           📍 Impresión: México, España, Argentina           │
│           🚚 3-5 días • 🏪 Shopify + MercadoLibre           │
│           [❤️ Guardar]  [👁️ Ver más]  [🛒 Comprar]         │
├──────────────────────────────────────────────────────────────┤
│  ┌────┐  NOCHE EN PARIS                ⭐⭐⭐⭐☆ 4.5 (89)    │
│  │IMG │  por Carlos López • Thriller                        │
│  ...                                                         │
└──────────────────────────────────────────────────────────────┘
```

### Página de Detalle de Libro

**URL:** `/libro/{isbn-o-slug}`

**Elementos:**
```
┌─────────────────────────────────────────────────────────┐
│  [Breadcrumb]                                           │
│                                                         │
│  ┌─────────────┐  Título del Libro                     │
│  │   PORTADA   │  por Nombre Autor                     │
│  │   GRANDE    │                                        │
│  │             │  ⭐⭐⭐⭐☆ 4.5 (127 reseñas)            │
│  │             │                                        │
│  │             │  Categoría: Romance                    │
│  └─────────────┘  Formato: Digital / Impreso           │
│                   Páginas: 324                          │
│  [Galería mini]   Idioma: Español                       │
│                                                         │
│                   💲 $12.99 (Digital)                   │
│                   💲 $24.99 (Impreso)                   │
│                                                         │
│                   [🛒 Agregar al carrito]               │
│                   [📖 Vista previa (primeras páginas)]  │
│                                                         │
│  ───────────────────────────────────────────────────    │
│                                                         │
│  📝 Sinopsis                                            │
│  [Texto de la sinopsis del libro]                      │
│                                                         │
│  📋 Detalles                                            │
│  • ISBN: 978-X-XXXX-XXXX-X                             │
│  • Editorial: DrakkarPress                              │
│  • Fecha publicación: 15 enero 2025                     │
│  • Dimensiones: 15 x 23 cm                              │
│                                                         │
│  ⭐ Reseñas de lectores                                 │
│  [Lista de reseñas con estrellas, texto, fecha]        │
│                                                         │
│  📚 También te puede gustar                             │
│  [Carrusel de libros similares]                         │
│                                                         │
└─────────────────────────────────────────────────────────┘
```

---

## Diseño Responsive

### Breakpoints

```css
/* Mobile */
@media (max-width: 768px) {
  - Menú hamburger
  - Grid 1 columna
  - Hero height: 80vh
  - Botones full-width
}

/* Tablet */
@media (min-width: 769px) and (max-width: 1024px) {
  - Grid 2 columnas
  - Hero height: 90vh
}

/* Desktop */
@media (min-width: 1025px) {
  - Grid 3-4 columnas
  - Hero height: 100vh
  - Menú horizontal completo
}
```

---

## Paleta de Colores

### Colores Principales

```css
/* Primario (Azul Vikingo) */
--primary: #1A4D7A
--primary-light: #2E6BA0
--primary-dark: #0F3555

/* Secundario (Oro Nórdico) */
--secondary: #D4AF37
--secondary-light: #F5D76E
--secondary-dark: #B8941F

/* Acentos por Rol */
--writer-blue: #3498DB
--reseller-green: #27AE60
--printer-orange: #E67E22
--reader-purple: #9B59B6

/* Neutrales */
--gray-50: #F8F9FA
--gray-100: #E9ECEF
--gray-200: #DEE2E6
--gray-700: #495057
--gray-900: #212529

/* Semánticos */
--success: #28A745
--warning: #FFC107
--error: #DC3545
--info: #17A2B8
```

---

## Tipografía

```css
/* Headings */
font-family: 'Montserrat', sans-serif;
/* Fuerte, moderna, autoridad */

/* Body */
font-family: 'Open Sans', sans-serif;
/* Legible, limpia, profesional */

/* Display (Hero) */
font-family: 'Playfair Display', serif;
/* Elegante, editorial */

/* Code (opcional) */
font-family: 'Fira Code', monospace;
```

### Jerarquía

```css
h1: 48px / 3rem (Hero)
h2: 36px / 2.25rem (Secciones)
h3: 28px / 1.75rem (Subsecciones)
h4: 24px / 1.5rem (Cards)
body: 16px / 1rem
small: 14px / 0.875rem
```

---

## Iconografía

**Librería recomendada:** Font Awesome 6 o Material Icons

**Iconos clave:**
- ✍️ Escritor: `fa-pen-fancy`
- 💼 Revendedor: `fa-chart-line`
- 🏭 Imprenta: `fa-print`
- 📚 Lector: `fa-book-reader`
- 🤖 IA: `fa-robot`
- 🚢 Flota: `fa-ship` (logo)
- ⭐ Rating: `fa-star`
- 🛒 Carrito: `fa-shopping-cart`

---

## Animaciones y Micro-interacciones

### Elementos Animados

1. **Hero:**
   - Fade in del texto principal (1s delay)
   - Botones aparecen con efecto slide-up (staggered)
   - Ilustración de barco con parallax suave

2. **Scroll Animations:**
   - Sections aparecen con fade-in cuando entran al viewport
   - Contadores numéricos con efecto count-up
   - Cards con hover scale (1.05)

3. **Botones:**
   - Hover: Elevación con shadow
   - Active: Scale 0.98
   - Transición: 200ms ease

4. **Carruseles:**
   - Transición suave (300ms)
   - Indicadores animados

---

## Performance

### Optimizaciones

1. **Imágenes:**
   - Formato WebP con fallback JPEG
   - Lazy loading para imágenes below-fold
   - Responsive images (srcset)
   - CDN para portadas

2. **CSS:**
   - Critical CSS inline
   - CSS minificado
   - Unused CSS removed (PurgeCSS)

3. **JavaScript:**
   - Code splitting por ruta
   - Lazy loading de componentes
   - Bundle size < 200KB

4. **Fonts:**
   - Font-display: swap
   - Preload critical fonts
   - Variable fonts cuando sea posible

### Métricas Objetivo

```
Lighthouse Score:
- Performance: > 90
- Accessibility: > 95
- Best Practices: > 90
- SEO: 100

Core Web Vitals:
- LCP: < 2.5s
- FID: < 100ms
- CLS: < 0.1
```

---

## Accesibilidad

### Requisitos WCAG 2.1 AA

1. **Contraste:** Mínimo 4.5:1 para texto normal
2. **Navegación por teclado:** Todos los elementos interactivos
3. **Alt text:** Todas las imágenes descriptivas
4. **ARIA labels:** En elementos complejos
5. **Focus visible:** Indicador claro en todos los elementos
6. **Responsive text:** Zoom hasta 200% sin pérdida de funcionalidad
7. **Screen reader:** Compatible con NVDA, JAWS

---

## SEO

### Optimizaciones On-Page

1. **Meta tags:**
   ```html
   <title>DrakkarPress - La Flota Editorial Global</title>
   <meta name="description" content="Plataforma editorial...">
   <meta name="keywords" content="publicar libro, editorial...">
   ```

2. **Open Graph:**
   ```html
   <meta property="og:title" content="DrakkarPress">
   <meta property="og:image" content="[URL imagen]">
   <meta property="og:description" content="...">
   ```

3. **Schema.org:**
   - Organization schema
   - Book schema para cada libro
   - Review schema para reseñas

4. **Sitemap XML:** Auto-generado
5. **Robots.txt:** Configurado
6. **URLs amigables:** `/categoria/romance` no `/cat?id=5`

---

## Próximos Pasos de Diseño

1. Crear wireframes de alta fidelidad en Figma
2. Diseñar sistema de componentes UI
3. Crear prototipos interactivos
4. Testing de usabilidad con usuarios reales
5. Implementación frontend con React/Next.js
