# REGLAMENTO DE DEDICATORIAS

## Propósito
Las dedicatorias permiten agregar un mensaje personalizado del comprador al ejemplar digital (EPUB) sin alterar el contenido original del autor.

## Principios
1. Integridad del texto: La IA de DrakkarPress **no modifica** capítulos, párrafos ni palabras del libro original. Solo agrega una página de dedicatoria antes del índice.
2. Trazabilidad: Cada dedicatoria genera un **hash SHA-256** y un QR verificable que confirma su autenticidad sin exponer datos sensibles.
3. Respeto y legalidad: Se aplica filtrado básico contra lenguaje ofensivo. Mensajes ilegales u ofensivos podrán ser eliminados.
4. Transparencia: El autor puede ver métricas agregadas (cantidad de dedicatorias, temas recurrentes) sin acceso a datos privados del comprador.
5. No edición posterior: Una vez incrustada, la dedicatoria queda registrada como parte del archivo distribuido (versión dedicada). Reemplazos requieren nueva compra o proceso administrativo.

## Flujo Técnico
1. El comprador introduce su mensaje en el checkout.
2. Se sanitiza (recorte, filtrado de palabras bloqueadas, normalización).
3. Se persiste `PurchaseDedication` con hash y referencia a la compra.
4. El servicio de inyección abre el EPUB y agrega la página (pendiente de implementación completa).
5. El hash y el QR apuntan al endpoint público `/api/dedications/verify/{hash}`.

## Límites
- Longitud máxima: 500 caracteres.
- Palabras censuradas: lista dinámica administrable (versión inicial fija en código).
- Idiomas soportados: cualquiera, sujeto a revisión moderación.

## Privacidad
No se publican datos personales; el mensaje se considera contenido del comprador, no del autor.

## Uso de la IA
La IA puede:
- Formatear la página HTML de dedicatoria.
- Generar variantes visuales (futuro) sin tocar texto del libro.

La IA **no puede**:
- Reescribir contenido del autor.
- Ajustar estilo narrativo del libro vía dedicatorias.
- Insertar publicidad o enlaces externos no verificados.

## Sanciones
Mensajes que violen normas de conducta podrán ser anulados sin reembolso parcial; el libro seguirá disponible sin dedicatoria.

## Actualizaciones
Este reglamento puede evolucionar (versión inicial 1.0). Cambios se documentarán y versionarán en el repositorio.
