# Guía de Marca - DrakkarPress

## 🎨 Identidad Visual Corporativa

---

## 🚢 Logo Principal

### Concepto

El logo de DrakkarPress representa un **Drakkar** (barco vikingo) navegando, simbolizando:
- **Navegación**: Los libros viajan por el mundo
- **Conquista**: Alcanzar nuevos lectores y mercados
- **Fortaleza**: Plataforma robusta y confiable
- **Comunidad**: Flota (muchos barcos juntos)

### Versiones del Logo

#### **Logo Completo (Versión Principal)**
```
┌─────────────────────────────────────┐
│  ╔══╗                                │
│  ║⚓ ║  DrakkarPress                 │
│  ╚══╝  La Flota Editorial Digital   │
└─────────────────────────────────────┘
```

**Elementos:**
- **Icono**: Barco vikingo estilizado con ancla
- **Nombre**: "DrakkarPress" en tipografía bold
- **Tagline**: "La Flota Editorial Digital"

#### **Logo Compacto (Versión Secundaria)**
```
┌──────────────────┐
│  ⚓ DrakkarPress │
└──────────────────┘
```

#### **Isotipo (Solo Icono)**
```
┌────┐
│ ⚓  │
│⎯⎯⎯⎯│
└────┘
```

---

## 🎨 Paleta de Colores Corporativos

### Colores Primarios

#### **Azul Vikingo (Color Principal)**
```
Nombre: Azul Vikingo / Viking Blue
HEX:    #1A4D7A
RGB:    26, 77, 122
CMYK:   79, 37, 0, 52
Uso:    Encabezados, botones principales, marca
```

#### **Azul Marino Nórdico (Oscuro)**
```
Nombre: Noche Nórdica / Nordic Night
HEX:    #0A2540
RGB:    10, 37, 64
CMYK:   84, 42, 0, 75
Uso:    Fondos oscuros, texto principal, footer
```

#### **Azul Océano (Claro)**
```
Nombre: Océano Vikingo / Viking Ocean
HEX:    #2E6BA0
RGB:    46, 107, 160
CMYK:   71, 33, 0, 37
Uso:    Hover states, degradados, fondos claros
```

### Colores Secundarios (Acentos)

#### **Oro Nórdico (Acento Principal)**
```
Nombre: Oro Nórdico / Nordic Gold
HEX:    #D4AF37
RGB:    212, 175, 55
CMYK:   0, 17, 74, 17
Uso:    CTAs importantes, iconos premium, destacados
```

#### **Oro Claro**
```
Nombre: Oro Luz / Light Gold
HEX:    #E8C968
RGB:    232, 201, 104
CMYK:   0, 13, 55, 9
Uso:    Hover en oro, gradientes dorados
```

#### **Agua Vikinga (Acento Complementario)**
```
Nombre: Agua Vikinga / Viking Water
HEX:    #00B4D8
RGB:    0, 180, 216
CMYK:   60, 0, 0, 15
Uso:    Links, elementos interactivos, notificaciones
```

### Colores por Rol de Usuario

#### **Escritor**
```
Nombre: Azul Escritor / Writer Blue
HEX:    #3498DB
RGB:    52, 152, 219
Uso:    Dashboard de escritores, iconos de autor
```

#### **Revendedor**
```
Nombre: Verde Comercio / Commerce Green
HEX:    #27AE60
RGB:    39, 174, 96
Uso:    Dashboard de afiliados, comisiones
```

#### **Imprenta**
```
Nombre: Naranja Producción / Production Orange
HEX:    #E67E22
RGB:    230, 126, 34
Uso:    Dashboard de imprentas, órdenes
```

#### **Lector**
```
Nombre: Morado Lector / Reader Purple
HEX:    #9B59B6
RGB:    155, 89, 182
Uso:    Dashboard de lectores, biblioteca
```

### Colores Neutrales

#### **Texto Oscuro**
```
HEX:    #1A1A1A
RGB:    26, 26, 26
Uso:    Texto principal, contenido
```

#### **Texto Medio**
```
HEX:    #4A4A4A
RGB:    74, 74, 74
Uso:    Texto secundario, subtítulos
```

#### **Texto Claro**
```
HEX:    #7A7A7A
RGB:    122, 122, 122
Uso:    Placeholders, texto terciario
```

#### **Fondo Claro**
```
HEX:    #F8F9FA
RGB:    248, 249, 250
Uso:    Fondo de página, secciones alternas
```

#### **Blanco**
```
HEX:    #FFFFFF
RGB:    255, 255, 255
Uso:    Tarjetas, texto sobre fondos oscuros
```

---

## 🎭 Gradientes Corporativos

### **Gradiente Primario (Océano Vikingo)**
```css
background: linear-gradient(135deg, #0A2540 0%, #1A4D7A 50%, #2E6BA0 100%);
```
**Uso:** Headers, heros, banners principales

### **Gradiente Dorado**
```css
background: linear-gradient(135deg, #D4AF37 0%, #E8C968 100%);
```
**Uso:** Botones premium, CTAs importantes, badges

### **Gradiente Azul Claro**
```css
background: linear-gradient(135deg, #2E6BA0 0%, #00B4D8 100%);
```
**Uso:** Cards, elementos hover, backgrounds secundarios

---

## 📝 Tipografía

### Fuente Principal
```
Familia: Segoe UI, -apple-system, BlinkMacSystemFont, sans-serif
Pesos:   300 (Light), 400 (Regular), 600 (Semibold), 700 (Bold)
```

### Jerarquía Tipográfica

```css
/* Títulos Principales (H1) */
font-size: 3rem (48px)
font-weight: 700
color: #1A4D7A
line-height: 1.2

/* Subtítulos (H2) */
font-size: 2.5rem (40px)
font-weight: 700
color: #1A4D7A

/* Encabezados (H3) */
font-size: 1.5rem (24px)
font-weight: 600
color: #0A2540

/* Texto Cuerpo */
font-size: 1rem (16px)
font-weight: 400
color: #1A1A1A
line-height: 1.6

/* Texto Pequeño */
font-size: 0.875rem (14px)
font-weight: 400
color: #4A4A4A
```

---

## 🔘 Botones y Componentes

### Botón Primario
```css
background: linear-gradient(135deg, #D4AF37 0%, #E8C968 100%);
color: #0A2540;
padding: 0.8rem 2rem;
border-radius: 25px;
font-weight: 600;
box-shadow: 0 4px 12px rgba(212, 175, 55, 0.3);
```

### Botón Secundario
```css
background: #1A4D7A;
color: #FFFFFF;
padding: 0.8rem 2rem;
border-radius: 25px;
font-weight: 600;
border: none;
```

### Botón Outline
```css
background: transparent;
color: #1A4D7A;
padding: 0.8rem 2rem;
border-radius: 25px;
border: 2px solid #1A4D7A;
font-weight: 600;
```

### Cards
```css
background: #FFFFFF;
border-radius: 10px;
box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
padding: 2rem;
border-top: 4px solid [color según tipo];
```

---

## 🎯 Iconografía

### Estilo de Iconos
- **Estilo:** Line icons (contorno)
- **Grosor:** 2px
- **Esquinas:** Redondeadas
- **Color:** Hereda del contexto o uso de colores corporativos

### Iconos Principales

```
⚓ - Ancla (Logo, navegación)
🚢 - Barco (Flota, envíos)
📚 - Libros (Catálogo, biblioteca)
✍️ - Escritor (Autor, crear)
💼 - Revendedor (Afiliados, comercio)
🏭 - Imprenta (Producción, POD)
🤖 - IA (Inteligencia artificial)
🎯 - Objetivo (Metas, KPIs)
💰 - Dinero (Pagos, regalías)
📊 - Estadísticas (Analytics)
🌍 - Global (Internacional)
```

---

## 📐 Espaciado y Grid

### Sistema de Espaciado
```
Base: 8px

xs:  4px  (0.25rem)
sm:  8px  (0.5rem)
md:  16px (1rem)
lg:  24px (1.5rem)
xl:  32px (2rem)
2xl: 48px (3rem)
3xl: 64px (4rem)
```

### Breakpoints Responsive
```
Mobile:  320px - 767px
Tablet:  768px - 1023px
Desktop: 1024px - 1439px
Large:   1440px+
```

---

## 🖼️ Imágenes y Fotografía

### Estilo Fotográfico
- **Tono:** Profesional pero accesible
- **Colores:** Naturales con tinte azulado
- **Composición:** Limpia, con espacio negativo
- **Personas:** Diversas, auténticas

### Tratamiento de Imágenes
- **Overlay:** Gradiente azul con 40% opacidad cuando hay texto encima
- **Bordes:** Radio de 10px para cards
- **Sombras:** `box-shadow: 0 4px 20px rgba(0,0,0,0.1)`

---

## ✨ Efectos y Animaciones

### Transiciones
```css
transition: all 0.3s ease;
```

### Hover States
```css
/* Botones */
transform: translateY(-2px);
box-shadow: 0 6px 16px rgba(0,0,0,0.2);

/* Cards */
transform: translateY(-5px);
box-shadow: 0 8px 30px rgba(0,0,0,0.15);

/* Links */
color: #D4AF37;
```

### Animaciones de Entrada
```css
@keyframes fadeInUp {
    from {
        opacity: 0;
        transform: translateY(30px);
    }
    to {
        opacity: 1;
        transform: translateY(0);
    }
}
```

---

## 📱 Aplicaciones del Logo

### Uso en Fondos Claros
- Logo completo con colores corporativos
- Azul Vikingo (#1A4D7A) para texto
- Oro Nórdico (#D4AF37) para icono

### Uso en Fondos Oscuros
- Logo en blanco (#FFFFFF)
- Icono en Oro Nórdico (#D4AF37)
- Tagline en blanco con 90% opacidad

### Tamaños Mínimos
- **Logo completo:** 120px de ancho mínimo
- **Logo compacto:** 80px de ancho mínimo
- **Isotipo solo:** 32px de ancho mínimo

### Espacios de Seguridad
- Mantener área clara equivalente a 50% de la altura del logo
- No colocar elementos a menos de esta distancia

---

## 🚫 Usos Incorrectos

### NO hacer:
❌ Cambiar los colores del logo  
❌ Distorsionar las proporciones  
❌ Agregar efectos de sombra al logo  
❌ Usar el logo sobre fondos complejos sin overlay  
❌ Rotar el logo  
❌ Usar tipografías diferentes para el nombre  
❌ Colocar el logo dentro de formas geométricas  

---

## 🎨 Paleta Extendida para Uso Específico

### Estados del Sistema

#### **Éxito**
```
HEX:    #27AE60
RGB:    39, 174, 96
Uso:    Mensajes de confirmación, pagos exitosos
```

#### **Advertencia**
```
HEX:    #F39C12
RGB:    243, 156, 18
Uso:    Alertas, atención requerida
```

#### **Error**
```
HEX:    #E74C3C
RGB:    231, 76, 60
Uso:    Errores, acciones destructivas
```

#### **Información**
```
HEX:    #00B4D8
RGB:    0, 180, 216
Uso:    Notificaciones informativas, tooltips
```

---

## 📊 Ejemplos de Aplicación

### Header Principal
```
Fondo:     Gradiente Primario
Logo:      Blanco con icono dorado
Links:     Blanco
CTA:       Gradiente Dorado
Sombra:    0 2px 10px rgba(0,0,0,0.2)
```

### Cards de Productos
```
Fondo:     Blanco (#FFFFFF)
Borde:     Superior 4px (color según categoría)
Título:    Azul Vikingo (#1A4D7A)
Precio:    Verde (#27AE60)
Botón:     Azul Vikingo → Hover: Oro Nórdico
Sombra:    0 4px 20px rgba(0,0,0,0.1)
```

### Dashboard Sections
```
Escritor:    Borde #3498DB, fondo rgba(52,152,219,0.05)
Revendedor:  Borde #27AE60, fondo rgba(39,174,96,0.05)
Imprenta:    Borde #E67E22, fondo rgba(230,126,34,0.05)
Lector:      Borde #9B59B6, fondo rgba(155,89,182,0.05)
```

---

## 🎯 Tono de Comunicación

### Voz de Marca
- **Profesional** pero **accesible**
- **Empoderador** (ayudamos a publicar)
- **Global** pero **personal**
- **Innovador** (IA, tecnología) pero **confiable**

### Palabras Clave
- Navegar, Flota, Zarpar
- Comunidad, Escritores, Lectores
- Digital, Global, Instantáneo
- Regalías, Transparente, Justo

### Evitar
- Jerga técnica excesiva
- Promesas exageradas
- Lenguaje corporativo frío
- Términos negativos

---

## 📁 Recursos Descargables

### Archivos del Logo
```
/assets/logo/
  ├── drakkarpress-logo-full.svg
  ├── drakkarpress-logo-full.png (transparente)
  ├── drakkarpress-logo-compact.svg
  ├── drakkarpress-icon.svg
  ├── drakkarpress-icon.png (32x32, 64x64, 128x128)
  └── drakkarpress-logo-white.svg (versión blanca)
```

### Paleta de Colores
```
/assets/colors/
  ├── drakkarpress-palette.ase (Adobe Swatch)
  ├── drakkarpress-palette.sketchpalette (Sketch)
  └── drakkarpress-colors.json
```

---

## 🔄 Versión y Actualizaciones

**Versión:** 1.0  
**Fecha:** Noviembre 2025  
**Próxima Revisión:** Q2 2026  
**Responsable:** Equipo de Diseño DrakkarPress

---

## 📞 Contacto

Para dudas sobre el uso de la marca:
- **Email:** brand@drakkarpress.com
- **Slack:** #brand-guidelines
- **Documentación:** docs.drakkarpress.com/brand

---

**© 2025 DrakkarPress. Todos los derechos reservados.**

Esta guía es un documento vivo y puede actualizarse según las necesidades de la plataforma.
