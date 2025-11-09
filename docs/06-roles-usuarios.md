# Dashboards por Rol de Usuario - DrakkarPress

## Visión General

Cada tipo de usuario tiene un dashboard personalizado con herramientas específicas para su rol. Diseño inspirado en **paneles de portales inmobiliarios** con métricas claras y acciones rápidas.

---

## 1. Dashboard del ESCRITOR / AUTOR

**URL:** `/dashboard/escritor`

### Layout Principal

```
┌──────────────────────────────────────────────────────────────────┐
│ [Header] Bienvenido, Carlos Méndez                    [Perfil ▾] │
├──────────────────────────────────────────────────────────────────┤
│                                                                  │
│ ┌────────────┐  ┌────────────┐  ┌────────────┐  ┌────────────┐ │
│ │📚 MIS LIBROS│  │💰 INGRESOS │  │📊 ANÁLISIS │  │🤖 IA       │ │
│ └────────────┘  └────────────┘  └────────────┘  └────────────┘ │
│                                                                  │
│ ┌────────────────────────────────────────────────────────────┐  │
│ │  📊 RESUMEN DEL MES                                        │  │
│ │  ────────────────────────────────────────────────────────  │  │
│ │                                                            │  │
│ │  $2,450.00       124           8            450          │  │
│ │  💰 Ingresos    📚 Ventas    📖 Libros    👁️ Vistas     │  │
│ │                                                            │  │
│ │  [Ver detalle completo →]                                  │  │
│ └────────────────────────────────────────────────────────────┘  │
│                                                                  │
│ ┌────────────────────────────────────────────────────────────┐  │
│ │  📚 MIS LIBROS                      [+ Publicar nuevo libro]│  │
│ │  ────────────────────────────────────────────────────────  │  │
│ │                                                            │  │
│ │  [Grid] [Lista] Filtrar: [Todos ▾] Ordenar: [Recientes ▾] │  │
│ │                                                            │  │
│ │  ┌─────────────────┐  ┌─────────────────┐  ┌────────────┐│  │
│ │  │ [PORTADA]       │  │ [PORTADA]       │  │ [PORTADA]  ││  │
│ │  │                 │  │                 │  │            ││  │
│ │  │ El Mar Eterno   │  │ Noches Oscuras  │  │ Recetas... ││  │
│ │  │ Romance         │  │ Thriller        │  │ Cocina     ││  │
│ │  │ ✅ Publicado    │  │ 📝 Borrador     │  │ ✅ Public. ││  │
│ │  │                 │  │                 │  │            ││  │
│ │  │ 🛒 45 ventas    │  │ --              │  │ 🛒 12 vtas ││  │
│ │  │ ⭐ 4.8 (23)     │  │ --              │  │ ⭐ 4.6 (8) ││  │
│ │  │                 │  │                 │  │            ││  │
│ │  │ [📊][✏️][⚙️]    │  │ [✏️][🗑️]        │  │ [📊][✏️]   ││  │
│ │  └─────────────────┘  └─────────────────┘  └────────────┘│  │
│ │                                                            │  │
│ │  [Ver todos mis libros →]                                  │  │
│ └────────────────────────────────────────────────────────────┘  │
│                                                                  │
│ ┌────────────────────────────────────────────────────────────┐  │
│ │  💡 SUGERENCIAS DE IA                                      │  │
│ │  ────────────────────────────────────────────────────────  │  │
│ │                                                            │  │
│ │  🎯 "Tu libro 'El Mar Eterno' podría beneficiarse de      │  │
│ │      una sinopsis más atractiva. ¿Quieres que la mejore?" │  │
│ │      [Mejorar sinopsis con IA →]                           │  │
│ │                                                            │  │
│ │  📈 "Los libros de Romance están en tendencia. Considera  │  │
│ │      publicar una secuela."                                │  │
│ │      [Generar ideas con IA →]                              │  │
│ │                                                            │  │
│ └────────────────────────────────────────────────────────────┘  │
│                                                                  │
│ ┌────────────────────────────────────────────────────────────┐  │
│ │  📈 ACTIVIDAD RECIENTE                                     │  │
│ │  ────────────────────────────────────────────────────────  │  │
│ │                                                            │  │
│ │  • Nueva venta: "El Mar Eterno" - Digital - $12.99       │  │
│ │    hace 2 horas                                            │  │
│ │                                                            │  │
│ │  • Nueva reseña: ⭐⭐⭐⭐⭐ en "Recetas de la Abuela"      │  │
│ │    hace 5 horas                                            │  │
│ │                                                            │  │
│ │  • Pedido de impresión completado: "El Mar Eterno"        │  │
│ │    hace 1 día                                              │  │
│ │                                                            │  │
│ │  [Ver todas →]                                             │  │
│ └────────────────────────────────────────────────────────────┘  │
│                                                                  │
└──────────────────────────────────────────────────────────────────┘
```

### Sección: MIS LIBROS (Detallada)

```
┌──────────────────────────────────────────────────────────────────┐
│ 📚 MIS LIBROS                                                    │
│                                                                  │
│ [+ Publicar nuevo libro]  [Importar desde archivo]              │
│                                                                  │
│ ┌──────────────────────────────────────────────────────────────┐│
│ │ Filtros:                                                     ││
│ │ [Todos] [Publicados] [Borradores] [En revisión] [Archivados]││
│ │                                                              ││
│ │ Categoría: [Todas ▾] | Plataforma: [Todas ▾] | Buscar: [🔍]││
│ └──────────────────────────────────────────────────────────────┘│
│                                                                  │
│ Vista LISTA (detallada):                                         │
│                                                                  │
│ ┌────────────────────────────────────────────────────────────┐  │
│ │ ┌────┐ EL MAR ETERNO                           ✅ Publicado│  │
│ │ │IMG │ Romance • 324 págs • Español                        │  │
│ │ └────┘ Publicado: 15 ene 2025 • ISBN: 978-X-XXXX-XXXX-X   │  │
│ │                                                            │  │
│ │ 📊 VENTAS                                                  │  │
│ │ Total: 45 ($562.50 ingresos)                               │  │
│ │ • Digital: 32 vtas ($12.99 c/u)                            │  │
│ │ • Impreso: 13 vtas ($24.99 c/u)                            │  │
│ │                                                            │  │
│ │ 🏪 PLATAFORMAS                                             │  │
│ │ ✅ Shopify | ✅ MercadoLibre | ✅ Lulu.com configurado     │  │
│ │                                                            │  │
│ │ ⭐ RESEÑAS                                                  │  │
│ │ 4.8/5.0 (23 reseñas) • [Ver todas →]                       │  │
│ │                                                            │  │
│ │ [📊 Ver estadísticas] [✏️ Editar] [🔗 Compartir] [⚙️ Config]│  │
│ └────────────────────────────────────────────────────────────┘  │
│                                                                  │
│ ┌────────────────────────────────────────────────────────────┐  │
│ │ ┌────┐ NOCHES OSCURAS                          📝 Borrador │  │
│ │ │IMG │ Thriller • 0 págs • Español                         │  │
│ │ └────┘ Última edición: hace 3 días                         │  │
│ │                                                            │  │
│ │ 📝 PROGRESO                                                │  │
│ │ ▓▓▓▓▓▓▓▓░░░░░░░░ 45% completado                           │  │
│ │ • ✅ Título y portada                                      │  │
│ │ • ✅ Sinopsis                                              │  │
│ │ • ⏳ Contenido (8 de 15 capítulos)                         │  │
│ │ • ❌ Revisión final                                        │  │
│ │                                                            │  │
│ │ 💡 Sugerencia IA: "¿Necesitas ayuda para terminar el      │  │
│ │    siguiente capítulo?" [Usar IA →]                        │  │
│ │                                                            │  │
│ │ [✏️ Continuar escribiendo] [👁️ Vista previa] [🗑️ Eliminar]│  │
│ └────────────────────────────────────────────────────────────┘  │
│                                                                  │
└──────────────────────────────────────────────────────────────────┘
```

### Sección: INGRESOS

```
┌──────────────────────────────────────────────────────────────────┐
│ 💰 INGRESOS Y REGALÍAS                                           │
│                                                                  │
│ Período: [Noviembre 2025 ▾]                  [Exportar PDF]     │
│                                                                  │
│ ┌──────────────────────────────────────────────────────────────┐│
│ │  RESUMEN FINANCIERO                                          ││
│ │                                                              ││
│ │  $2,450.00        $1,890.00        $560.00        $2,100.00 ││
│ │  Ganado este mes  Disponible      Pendiente      Histórico  ││
│ │                                                              ││
│ │  Próximo pago: 1 dic 2025 • $1,890.00                       ││
│ │  [Solicitar pago anticipado]                                 ││
│ └──────────────────────────────────────────────────────────────┘│
│                                                                  │
│ ┌──────────────────────────────────────────────────────────────┐│
│ │  INGRESOS POR LIBRO                                          ││
│ │  ────────────────────────────────────────────────────────    ││
│ │                                                              ││
│ │  1. El Mar Eterno                            $1,850.00      ││
│ │     45 ventas • 90% directo / 60% con revendedor             ││
│ │     [Ver detalle →]                                          ││
│ │                                                              ││
│ │  2. Recetas de la Abuela                       $450.00      ││
│ │     12 ventas • 90% directo / 60% con revendedor             ││
│ │     [Ver detalle →]                                          ││
│ │                                                              ││
│ │  3. Historias del Bosque                       $150.00      ││
│ │     8 ventas • 90% directo / 60% con revendedor              ││
│ │     [Ver detalle →]                                          ││
│ └──────────────────────────────────────────────────────────────┘│
│                                                                  │
│ ┌──────────────────────────────────────────────────────────────┐│
│ │  INGRESOS POR PLATAFORMA                                     ││
│ │  ────────────────────────────────────────────────────────    ││
│ │                                                              ││
│ │  🏪 Shopify            $1,680.00  (68%)  ▓▓▓▓▓▓▓░░░         ││
│ │  🛒 MercadoLibre         $620.00  (25%)  ▓▓▓░░░░░░░         ││
│ │  📚 Directo DP           $150.00   (6%)  ▓░░░░░░░░░         ││
│ └──────────────────────────────────────────────────────────────┘│
│                                                                  │
│ ┌──────────────────────────────────────────────────────────────┐│
│ │  HISTORIAL DE PAGOS                                          ││
│ │  ────────────────────────────────────────────────────────    ││
│ │                                                              ││
│ │  ✅ 1 nov 2025    $2,100.00    Transferencia    [Recibo]    ││
│ │  ✅ 1 oct 2025    $1,850.00    Transferencia    [Recibo]    ││
│ │  ✅ 1 sep 2025    $1,420.00    Transferencia    [Recibo]    ││
│ │                                                              ││
│ │  [Ver historial completo →]                                  ││
│ └──────────────────────────────────────────────────────────────┘│
│                                                                  │
│ 💡 Tip: Promociona tus libros en redes sociales para aumentar   │
│    ventas. [Generar contenido con IA →]                         │
│                                                                  │
└──────────────────────────────────────────────────────────────────┘
```

### Sección: HERRAMIENTAS DE IA

```
┌──────────────────────────────────────────────────────────────────┐
│ 🤖 IA DE DRAKKARPRESS                                            │
│                                                                  │
│ Uso diario: ▓▓▓▓▓▓▓░░░ 47/100 consultas     [Upgrade plan →]   │
│                                                                  │
│ ┌──────────────────────────────────────────────────────────────┐│
│ │  HERRAMIENTAS DISPONIBLES                                    ││
│ │                                                              ││
│ │  ┌────────────────┐  ┌────────────────┐  ┌────────────────┐││
│ │  │ 💡 GENERAR     │  │ 📝 EXTENDER    │  │ 🎯 MEJORAR     │││
│ │  │    IDEAS       │  │    TEXTO       │  │    TEXTO       │││
│ │  │                │  │                │  │                │││
│ │  │ Crea ideas de  │  │ Continúa tus   │  │ Perfecciona tu │││
│ │  │ libros según   │  │ capítulos con  │  │ escritura con  │││
│ │  │ categoría      │  │ coherencia     │  │ sugerencias    │││
│ │  │                │  │                │  │                │││
│ │  │ [Usar →]       │  │ [Usar →]       │  │ [Usar →]       │││
│ │  └────────────────┘  └────────────────┘  └────────────────┘││
│ │                                                              ││
│ │  ┌────────────────┐  ┌────────────────┐  ┌────────────────┐││
│ │  │ 📖 SINOPSIS    │  │ 🏷️ TÍTULOS    │  │ 📢 MARKETING   │││
│ │  │                │  │                │  │                │││
│ │  │ Genera         │  │ Sugiere títulos│  │ Crea posts para│││
│ │  │ sinopsis       │  │ atractivos     │  │ redes sociales │││
│ │  │ atractivas     │  │ comerciales    │  │                │││
│ │  │                │  │                │  │                │││
│ │  │ [Usar →]       │  │ [Usar →]       │  │ [Usar →]       │││
│ │  └────────────────┘  └────────────────┘  └────────────────┘││
│ └──────────────────────────────────────────────────────────────┘│
│                                                                  │
│ ┌──────────────────────────────────────────────────────────────┐│
│ │  HISTORIAL DE IA                                             ││
│ │  ────────────────────────────────────────────────────────    ││
│ │                                                              ││
│ │  • Generaste 5 ideas para libro de Thriller                  ││
│ │    hace 2 horas • [Ver resultados]                           ││
│ │                                                              ││
│ │  • Mejoraste sinopsis de "El Mar Eterno"                     ││
│ │    hace 1 día • [Ver antes/después]                          ││
│ │                                                              ││
│ │  • Extendiste capítulo 5 de "Noches Oscuras"                ││
│ │    hace 3 días • [Ver texto]                                 ││
│ │                                                              ││
│ │  [Ver historial completo →]                                  ││
│ └──────────────────────────────────────────────────────────────┘│
│                                                                  │
└──────────────────────────────────────────────────────────────────┘
```

### Flujo: Publicar Nuevo Libro

**Wizard paso a paso:**

```
PASO 1/5: INFORMACIÓN BÁSICA
┌──────────────────────────────────────────────────────┐
│ Título del libro: [________________]                 │
│                                                      │
│ Subtítulo (opcional): [________________]             │
│                                                      │
│ Categoría: [Romance ▾]                               │
│                                                      │
│ Idioma: [Español ▾]                                  │
│                                                      │
│ Número de páginas: [324]                             │
│                                                      │
│ 💡 Necesitas ayuda? [Generar título con IA]         │
│                                                      │
│            [Cancelar]         [Siguiente →]          │
└──────────────────────────────────────────────────────┘

PASO 2/5: PORTADA
┌──────────────────────────────────────────────────────┐
│ Sube o diseña tu portada                             │
│                                                      │
│ ┌────────────────────┐                              │
│ │                    │                              │
│ │  Arrastra imagen   │                              │
│ │  o haz clic        │                              │
│ │                    │                              │
│ │  [Subir archivo]   │                              │
│ └────────────────────┘                              │
│                                                      │
│ Requisitos:                                          │
│ • Tamaño mínimo: 1600x2560 px                       │
│ • Formato: JPG o PNG                                 │
│ • Peso máximo: 10 MB                                 │
│                                                      │
│ [Usar plantilla] [Contratar diseñador]              │
│                                                      │
│          [← Atrás]              [Siguiente →]        │
└──────────────────────────────────────────────────────┘

PASO 3/5: CONTENIDO Y SINOPSIS
┌──────────────────────────────────────────────────────┐
│ Sinopsis (aparecerá en la tienda):                   │
│ ┌────────────────────────────────────────────────┐  │
│ │                                                │  │
│ │                                                │  │
│ │                                                │  │
│ │                                                │  │
│ └────────────────────────────────────────────────┘  │
│ 450/500 caracteres                                   │
│                                                      │
│ [Generar con IA] [Mejorar con IA]                   │
│                                                      │
│ Archivo del libro:                                   │
│ ┌────────────────────────────────────────────────┐  │
│ │ [📄 Subir PDF/DOCX] o [✏️ Escribir en editor] │  │
│ └────────────────────────────────────────────────┘  │
│                                                      │
│          [← Atrás]              [Siguiente →]        │
└──────────────────────────────────────────────────────┘

PASO 4/5: PRECIO Y DISTRIBUCIÓN
┌──────────────────────────────────────────────────────┐
│ Formatos disponibles:                                │
│                                                      │
│ ☑ Digital (eBook)                                    │
│   Precio: [$12.99]                                   │
│   Tu regalía: $11.69 (90% directo) o $7.79 (60% c/revendedor) │
│   Plataforma DrakkarPress: 10% ($1.30)              │
│                                                      │
│ ☑ Impreso (Tapa blanda)                              │
│   Precio: [$24.99]                                   │
│   Costo impresión: $5.20                             │
│   Tu regalía: $17.81 (90% directo) o $11.87 (60% c/revendedor) │
│   Plataforma DrakkarPress: 10% ($1.98)              │
│                                                      │
│ □ Audiolibro (próximamente)                          │
│                                                      │
│ Plataformas de venta:                                │
│ ☑ Shopify (automático)                               │
│ ☑ MercadoLibre                                       │
│ ☑ Tienda DrakkarPress                                │
│                                                      │
│          [← Atrás]              [Siguiente →]        │
└──────────────────────────────────────────────────────┘

PASO 5/5: REVISIÓN Y PUBLICACIÓN
┌──────────────────────────────────────────────────────┐
│ Revisa toda la información:                          │
│                                                      │
│ ✅ Título: "El Secreto del Faro"                     │
│ ✅ Categoría: Romance                                │
│ ✅ Portada cargada                                   │
│ ✅ Contenido (PDF): 324 páginas                      │
│ ✅ Sinopsis completada                               │
│ ✅ Precios configurados                              │
│                                                      │
│ ⚠️ Una vez publicado, tu libro entrará en revisión   │
│    (24-48 horas) antes de aparecer en tiendas.      │
│                                                      │
│ □ Acepto términos y condiciones                      │
│                                                      │
│       [← Atrás]  [Guardar borrador] [🚀 PUBLICAR]   │
└──────────────────────────────────────────────────────┘
```

---

## 2. Dashboard del REVENDEDOR / AFILIADO

**URL:** `/dashboard/revendedor`

### Layout Principal

```
┌──────────────────────────────────────────────────────────────────┐
│ [Header] Bienvenido, María Sánchez (Afiliado)       [Perfil ▾]  │
├──────────────────────────────────────────────────────────────────┤
│                                                                  │
│ ┌────────────┐  ┌────────────┐  ┌────────────┐  ┌────────────┐ │
│ │💼 MI CATÁLOGO│ │💰 COMISIONES│ │📊 ESTADÍS. │  │🎯 MARKETING│ │
│ └────────────┘  └────────────┘  └────────────┘  └────────────┘ │
│                                                                  │
│ ┌────────────────────────────────────────────────────────────┐  │
│ │  📊 RESUMEN DEL MES                                        │  │
│ │  ────────────────────────────────────────────────────────  │  │
│ │                                                            │  │
│ │  $890.00         56           342          1,245         │  │
│ │  💰 Comisiones  🛒 Ventas   📚 Catálogo   👁️ Clics      │  │
│ │                                                            │  │
│ │  Tasa de conversión: 4.5% ↑                                │  │
│ │  [Ver detalle completo →]                                  │  │
│ └────────────────────────────────────────────────────────────┘  │
│                                                                  │
│ ┌────────────────────────────────────────────────────────────┐  │
│ │  💼 MI CATÁLOGO PERSONALIZADO                              │  │
│ │  ────────────────────────────────────────────────────────  │  │
│ │                                                            │  │
│ │  342 libros en tu catálogo    [+ Agregar libros]          │  │
│ │                                                            │  │
│ │  Categorías más vendidas:                                  │  │
│ │  1. Romance (45%)                                          │  │
│ │  2. Thriller (28%)                                         │  │
│ │  3. Cocina (18%)                                           │  │
│ │                                                            │  │
│ │  [Gestionar catálogo →]                                    │  │
│ └────────────────────────────────────────────────────────────┘  │
│                                                                  │
│ ┌────────────────────────────────────────────────────────────┐  │
│ │  🔗 MIS ENLACES DE AFILIADO                                │  │
│ │  ────────────────────────────────────────────────────────  │  │
│ │                                                            │  │
│ │  Link principal:                                           │  │
│ │  https://drakkarpress.com/a/mariasanchez                   │  │
│ │  [📋 Copiar] [🔗 QR] [📱 Compartir]                        │  │
│ │                                                            │  │
│ │  Enlaces personalizados:                                   │  │
│ │  • Instagram Stories: drk.press/i/maria123                 │  │
│ │  • Facebook: drk.press/f/maria123                          │  │
│ │  • Email: drk.press/e/maria123                             │  │
│ │                                                            │  │
│ │  [Crear enlace personalizado]                              │  │
│ └────────────────────────────────────────────────────────────┘  │
│                                                                  │
│ ┌────────────────────────────────────────────────────────────┐  │
│ │  📈 VENTAS RECIENTES                                       │  │
│ │  ────────────────────────────────────────────────────────  │  │
│ │                                                            │  │
│ │  • "El Mar Eterno" - $12.99 - Tu comisión (30%): $3.90    │  │
│ │    hace 1 hora • vía Instagram                             │  │
│ │                                                            │  │
│ │  • "Recetas de la Abuela" - $19.99 - Tu comisión (30%): $6.00 │  │
│ │    hace 3 horas • vía Facebook                             │  │
│ │                                                            │  │
│ │  • "Noches en París" - $15.99 - Tu comisión (30%): $4.80  │  │
│ │    hace 5 horas • vía Email                                │  │
│ │                                                            │  │
│ │  [Ver historial completo →]                                │  │
│ └────────────────────────────────────────────────────────────┘  │
│                                                                  │
└──────────────────────────────────────────────────────────────────┘
```

### Sección: GESTIONAR CATÁLOGO

```
┌──────────────────────────────────────────────────────────────────┐
│ 💼 GESTIONAR MI CATÁLOGO                                         │
│                                                                  │
│ ┌──────────────────────────────────────────────────────────────┐│
│ │ Buscar en catálogo de DrakkarPress:                          ││
│ │ [🔍 Buscar libros, autores, ISBN...                      ]   ││
│ │                                                              ││
│ │ Filtros: [Categoría ▾] [Precio ▾] [Rating ▾] [Novedades]   ││
│ └──────────────────────────────────────────────────────────────┘│
│                                                                  │
│ ┌──────────────────────────────────────────────────────────────┐│
│ │ LIBROS DISPONIBLES PARA AGREGAR                              ││
│ │                                                              ││
│ │ ┌────┐ El Secreto del Bosque        ⭐ 4.9  Tu comisión: 30%││
│ │ │IMG │ Fantasía • $14.99 → Ganas $4.50 por venta            ││
│ │ └────┘ 234 ventas totales                                    ││
│ │        [+ Agregar a mi catálogo] [👁️ Vista previa]          ││
│ │                                                              ││
│ │ ┌────┐ Misterio en la Noche        ⭐ 4.7  Tu comisión: 30% ││
│ │ │IMG │ Thriller • $12.99 → Ganas $3.90 por venta            ││
│ │ └────┘ 189 ventas totales                                    ││
│ │        [+ Agregar a mi catálogo] [👁️ Vista previa]          ││
│ └──────────────────────────────────────────────────────────────┘│
│                                                                  │
│ ┌──────────────────────────────────────────────────────────────┐│
│ │ TU CATÁLOGO ACTUAL (342 libros)                              ││
│ │                                                              ││
│ │ Ordenar: [Más vendidos ▾] Ver: [Grid] [Lista]               ││
│ │                                                              ││
│ │ ┌────┐ El Mar Eterno              🛒 12 ventas este mes     ││
│ │ │IMG │ Romance • $12.99            💰 $18.00 comisiones      ││
│ │ └────┘ https://drk.press/i/maria123/mar-eterno               ││
│ │        [🔗 Copiar link] [📊 Stats] [🗑️ Quitar]              ││
│ │                                                              ││
│ │ ┌────┐ Recetas de la Abuela       🛒 8 ventas este mes      ││
│ │ │IMG │ Cocina • $19.99             💰 $24.00 comisiones      ││
│ │ └────┘ https://drk.press/i/maria123/recetas                  ││
│ │        [🔗 Copiar link] [📊 Stats] [🗑️ Quitar]              ││
│ └──────────────────────────────────────────────────────────────┘│
│                                                                  │
└──────────────────────────────────────────────────────────────────┘
```

### Sección: HERRAMIENTAS DE MARKETING

```
┌──────────────────────────────────────────────────────────────────┐
│ 🎯 HERRAMIENTAS DE MARKETING                                     │
│                                                                  │
│ ┌──────────────────────────────────────────────────────────────┐│
│ │ 📱 GENERADOR DE CONTENIDO PARA REDES SOCIALES                ││
│ │                                                              ││
│ │ Selecciona un libro: [El Mar Eterno ▾]                       ││
│ │ Plataforma: [Instagram ▾]                                    ││
│ │ Tipo: [Post ▾] [Story] [Reel]                                ││
│ │                                                              ││
│ │ [🤖 Generar contenido con IA]                                ││
│ └──────────────────────────────────────────────────────────────┘│
│                                                                  │
│ ┌──────────────────────────────────────────────────────────────┐│
│ │ CONTENIDO GENERADO:                                          ││
│ │                                                              ││
│ │ 📸 Instagram Post:                                           ││
│ │ ┌──────────────────────────────────────┐                    ││
│ │ │ 🌊 ¿Buscas una historia de amor que  │                    ││
│ │ │ te haga soñar? "El Mar Eterno" te    │                    ││
│ │ │ llevará a un viaje inolvidable.      │                    ││
│ │ │                                      │                    ││
│ │ │ 💙 Romance | ⭐ 4.8/5 | 324 páginas  │                    ││
│ │ │                                      │                    ││
│ │ │ 🔗 Link en bio                       │                    ││
│ │ │                                      │                    ││
│ │ │ #Romance #Libros #Lectura #ElMarEter│                    ││
│ │ └──────────────────────────────────────┘                    ││
│ │                                                              ││
│ │ [📋 Copiar texto] [📥 Descargar imagen] [Generar otro]      ││
│ └──────────────────────────────────────────────────────────────┘│
│                                                                  │
│ ┌──────────────────────────────────────────────────────────────┐│
│ │ 🎨 IMÁGENES PROMOCIONALES                                    ││
│ │                                                              ││
│ │ Descarga imágenes listas para redes sociales:                ││
│ │                                                              ││
│ │ [IMG] [IMG] [IMG] [IMG]                                      ││
│ │ Story  Post  Banner Facebook                                 ││
│ │                                                              ││
│ │ [Descargar todas las imágenes]                               ││
│ └──────────────────────────────────────────────────────────────┘│
│                                                                  │
│ ┌──────────────────────────────────────────────────────────────┐│
│ │ 🎯 CÓDIGOS QR PERSONALIZADOS                                 ││
│ │                                                              ││
│ │ Genera códigos QR para tus libros:                           ││
│ │                                                              ││
│ │ Libro: [El Mar Eterno ▾]                                     ││
│ │ Estilo QR: [Clásico ▾] [Con logo] [Colorido]                ││
│ │                                                              ││
│ │ [Generar QR]                                                 ││
│ │                                                              ││
│ │ [QR CODE]  → [📥 Descargar PNG] [📥 Descargar SVG]          ││
│ └──────────────────────────────────────────────────────────────┘│
│                                                                  │
└──────────────────────────────────────────────────────────────────┘
```

---

## 3. Dashboard de IMPRENTA

**URL:** `/dashboard/imprenta`

### Layout Principal

```
┌──────────────────────────────────────────────────────────────────┐
│ [Header] PrintFast México                            [Perfil ▾]  │
├──────────────────────────────────────────────────────────────────┤
│                                                                  │
│ ┌────────────┐  ┌────────────┐  ┌────────────┐  ┌────────────┐ │
│ │🏭 PEDIDOS  │  │💰 PAGOS    │  │📊 ESTADÍS. │  │⚙️ CONFIG.  │ │
│ └────────────┘  └────────────┘  └────────────┘  └────────────┘ │
│                                                                  │
│ ┌────────────────────────────────────────────────────────────┐  │
│ │  📊 RESUMEN DEL MES                                        │  │
│ │  ────────────────────────────────────────────────────────  │  │
│ │                                                            │  │
│ │  $4,250.00       68           23           45            │  │
│ │  💰 Ingresos    📦 Pedidos   ⏳ Pendientes ✅ Completos   │  │
│ │                                                            │  │
│ │  Eficiencia: 95% ↑ • Tiempo promedio: 2.3 días           │  │
│ │  [Ver detalle completo →]                                  │  │
│ └────────────────────────────────────────────────────────────┘  │
│                                                                  │
│ ┌────────────────────────────────────────────────────────────┐  │
│ │  📦 PEDIDOS PENDIENTES                   [Actualizar]      │  │
│ │  ────────────────────────────────────────────────────────  │  │
│ │                                                            │  │
│ │  🔴 URGENTE (2)                                            │  │
│ │  ┌────────────────────────────────────────────────────────┐│  │
│ │  │ #DP-2025-001234                     📍 Ciudad de México││  │
│ │  │ El Mar Eterno • Romance                                ││  │
│ │  │ • 1 copia • Tapa blanda • Color • 324 págs             ││  │
│ │  │ • Recibido hace: 2 horas                               ││  │
│ │  │ • Fecha límite: Hoy                                    ││  │
│ │  │ [Marcar en producción] [Ver detalles]                  ││  │
│ │  └────────────────────────────────────────────────────────┘│  │
│ │                                                            │  │
│ │  🟡 EN PRODUCCIÓN (23)                                     │  │
│ │  ┌────────────────────────────────────────────────────────┐│  │
│ │  │ #DP-2025-001220                      📍 Guadalajara    ││  │
│ │  │ Recetas de la Abuela • Cocina                          ││  │
│ │  │ • 3 copias • Tapa blanda • Color • 256 págs            ││  │
│ │  │ • En producción desde: hace 1 día                      ││  │
│ │  │ ▓▓▓▓▓▓▓░░░ 75% completado                             ││  │
│ │  │ [Marcar como enviado] [Ver detalles]                   ││  │
│ │  └────────────────────────────────────────────────────────┘│  │
│ │                                                            │  │
│ │  🟢 LISTO PARA ENVÍO (5)                                   │  │
│ │                                                            │  │
│ │  [Ver todos los pedidos →]                                 │  │
│ └────────────────────────────────────────────────────────────┘  │
│                                                                  │
│ ┌────────────────────────────────────────────────────────────┐  │
│ │  📍 TU ZONA DE COBERTURA                                   │  │
│ │  ────────────────────────────────────────────────────────  │  │
│ │                                                            │  │
│ │  [MAPA DE MÉXICO]                                          │  │
│ │  • Ciudad de México (zona principal)                       │  │
│ │  • Guadalajara, Monterrey (envío rápido)                   │  │
│ │  • Resto del país (envío estándar)                         │  │
│ │                                                            │  │
│ │  [Actualizar zonas de cobertura]                           │  │
│ └────────────────────────────────────────────────────────────┘  │
│                                                                  │
└──────────────────────────────────────────────────────────────────┘
```

### Vista Detalle de Pedido

```
┌──────────────────────────────────────────────────────────────────┐
│ 📦 PEDIDO #DP-2025-001234                        [← Volver]      │
│                                                                  │
│ ┌──────────────────────────────────────────────────────────────┐│
│ │ INFORMACIÓN DEL LIBRO                                        ││
│ │ ──────────────────────────────────────────────────────────   ││
│ │                                                              ││
│ │ ┌────┐  Título: El Mar Eterno                               ││
│ │ │IMG │  Autor: Carlos Méndez                                ││
│ │ └────┘  ISBN: 978-X-XXXX-XXXX-X                             ││
│ │         Categoría: Romance                                   ││
│ └──────────────────────────────────────────────────────────────┘│
│                                                                  │
│ ┌──────────────────────────────────────────────────────────────┐│
│ │ ESPECIFICACIONES DE IMPRESIÓN                                ││
│ │ ──────────────────────────────────────────────────────────   ││
│ │                                                              ││
│ │ Cantidad: 1 copia                                            ││
│ │ Tamaño: US Trade (6" x 9")                                   ││
│ │ Encuadernación: Tapa blanda (Perfect Bind)                   ││
│ │ Interior: Color • 324 páginas                                ││
│ │ Papel: Blanco 60lb                                           ││
│ │ Acabado portada: Mate                                        ││
│ │                                                              ││
│ │ [📥 Descargar archivos de impresión]                         ││
│ │ • Interior.pdf (45 MB)                                       ││
│ │ • Portada.pdf (12 MB)                                        ││
│ └──────────────────────────────────────────────────────────────┘│
│                                                                  │
│ ┌──────────────────────────────────────────────────────────────┐│
│ │ INFORMACIÓN DE ENVÍO                                         ││
│ │ ──────────────────────────────────────────────────────────   ││
│ │                                                              ││
│ │ Destinatario: Laura González                                 ││
│ │ Dirección: Av. Insurgentes Sur 1234, Col. Del Valle          ││
│ │            Ciudad de México, CDMX 03100                      ││
│ │ Teléfono: +52 55 1234 5678                                   ││
│ │ Email: laura.gonzalez@email.com                              ││
│ │                                                              ││
│ │ Nivel de envío: Express (2-3 días)                           ││
│ └──────────────────────────────────────────────────────────────┘│
│                                                                  │
│ ┌──────────────────────────────────────────────────────────────┐│
│ │ ESTADO DEL PEDIDO                                            ││
│ │ ──────────────────────────────────────────────────────────   ││
│ │                                                              ││
│ │ Estado actual: 🔴 RECIBIDO                                   ││
│ │                                                              ││
│ │ ✅ Recibido        09 nov 2025, 08:30                        ││
│ │ ⏳ En producción   -- : --                                   ││
│ │ ⏳ Impreso          -- : --                                   ││
│ │ ⏳ Enviado          -- : --                                   ││
│ │ ⏳ Entregado        -- : --                                   ││
│ │                                                              ││
│ │ Acciones:                                                    ││
│ │ [Marcar: En producción] [Reportar problema]                  ││
│ └──────────────────────────────────────────────────────────────┘│
│                                                                  │
│ ┌──────────────────────────────────────────────────────────────┐│
│ │ INFORMACIÓN DE PAGO                                          ││
│ │ ──────────────────────────────────────────────────────────   ││
│ │                                                              ││
│ │ Pago por impresión: $5.20                                    ││
│ │ Pago por envío: $3.50                                        ││
│ │ Total: $8.70                                                 ││
│ │                                                              ││
│ │ Estado: Se pagará el 1 dic 2025                              ││
│ └──────────────────────────────────────────────────────────────┘│
│                                                                  │
│ ┌──────────────────────────────────────────────────────────────┐│
│ │ TRACKING DE ENVÍO                                            ││
│ │                                                              ││
│ │ Courier: [DHL ▾]                                             ││
│ │ Número de guía: [____________]                               ││
│ │                                                              ││
│ │ [Actualizar tracking]                                        ││
│ └──────────────────────────────────────────────────────────────┘│
│                                                                  │
└──────────────────────────────────────────────────────────────────┘
```

---

## 4. Dashboard del LECTOR / CLIENTE

**URL:** `/dashboard/lector`

### Layout Principal

```
┌──────────────────────────────────────────────────────────────────┐
│ [Header] Hola, Ana López                         [Perfil ▾]      │
├──────────────────────────────────────────────────────────────────┤
│                                                                  │
│ ┌────────────┐  ┌────────────┐  ┌────────────┐  ┌────────────┐ │
│ │📚 MI       │  │🛒 PEDIDOS  │  │❤️ FAVORITOS│  │⭐ RESEÑAS  │ │
│ │  BIBLIOTECA│  │            │  │            │  │            │ │
│ └────────────┘  └────────────┘  └────────────┘  └────────────┘ │
│                                                                  │
│ ┌────────────────────────────────────────────────────────────┐  │
│ │  📚 MI BIBLIOTECA                                          │  │
│ │  ────────────────────────────────────────────────────────  │  │
│ │                                                            │  │
│ │  12 libros digitales • 5 libros físicos                    │  │
│ │                                                            │  │
│ │  Filtrar: [Todos ▾] [Digital] [Físico] [Sin leer]         │  │
│ │  Ordenar: [Recientes ▾]                       [Grid][Lista]│  │
│ │                                                            │  │
│ │  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐    │  │
│ │  │ [PORTADA]    │  │ [PORTADA]    │  │ [PORTADA]    │    │  │
│ │  │              │  │              │  │              │    │  │
│ │  │ El Mar Eterno│  │ Recetas...   │  │ Thriller...  │    │  │
│ │  │ Digital      │  │ Físico       │  │ Digital      │    │  │
│ │  │              │  │              │  │              │    │  │
│ │  │ [📖 Leer]    │  │ [📦 Rastrear]│  │ [📖 Leer]    │    │  │
│ │  │ [💬 Reseñar] │  │ [💬 Reseñar] │  │ [💬 Reseñar] │    │  │
│ │  └──────────────┘  └──────────────┘  └──────────────┘    │  │
│ │                                                            │  │
│ │  [Ver toda la biblioteca →]                                │  │
│ └────────────────────────────────────────────────────────────┘  │
│                                                                  │
│ ┌────────────────────────────────────────────────────────────┐  │
│ │  🛒 PEDIDOS RECIENTES                                      │  │
│ │  ────────────────────────────────────────────────────────  │  │
│ │                                                            │  │
│ │  #DP-123456 • "Recetas de la Abuela" • Impreso            │  │
│ │  🚚 En tránsito • Llega: 12 nov 2025                       │  │
│ │  [Rastrear pedido →]                                       │  │
│ │                                                            │  │
│ │  #DP-123455 • "El Mar Eterno" • Digital                    │  │
│ │  ✅ Completado • 5 nov 2025                                │  │
│ │  [Leer ahora →]                                            │  │
│ │                                                            │  │
│ │  [Ver historial completo →]                                │  │
│ └────────────────────────────────────────────────────────────┘  │
│                                                                  │
│ ┌────────────────────────────────────────────────────────────┐  │
│ │  💡 RECOMENDACIONES PARA TI                                │  │
│ │  ────────────────────────────────────────────────────────  │  │
│ │                                                            │  │
│ │  Basado en tus lecturas de Romance y Cocina:               │  │
│ │                                                            │  │
│ │  [IMG] [IMG] [IMG] [IMG]                                   │  │
│ │                                                            │  │
│ │  [Ver más recomendaciones →]                               │  │
│ └────────────────────────────────────────────────────────────┘  │
│                                                                  │
└──────────────────────────────────────────────────────────────────┘
```

---

## Próximos Pasos

1. Crear mockups visuales de cada dashboard
2. Implementar componentes UI reutilizables
3. Definir flujos de navegación entre secciones
4. Prototipar en Figma o similar
5. Testing de usabilidad con usuarios reales por rol
