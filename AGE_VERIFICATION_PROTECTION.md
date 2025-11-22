# Age Verification & Minor Protection

## Objetivos
- Cumplir COPPA (<13), protección reforzada <16 (UE DSA), salvaguardar menores de explotación y contacto inapropiado.

## Segmentación de Edades
| Grupo | Rango | Características |
|-------|-------|-----------------|
| Child | <13 | Necesita consentimiento parental verificable. Perfil semiprivado. Sin ventas directas. |
| Teen | 13-15 | Funciones sociales limitadas (DM restringidos, no contenido +18). |
| Young | 16-17 | Acceso ampliado, still tag de menor para moderación reforzada. |
| Adult | >=18 | Acceso completo. |

## Verificación
1. **Declarativa Inicial**: Fecha de nacimiento + país.
2. **Riesgo / Intensificación**: Si usuario quiere vender libros o monetizar → KYC-lite (documento ID reducido / tercero Stripe Identity / SumSub).
3. **Parental Consent (<13)**
   - Email padre / tutor + código verificación.
   - Consent log con timestamp y alcance (qué datos se usan).

## Restricciones Menores
- No acceso a contenido etiquetado como "MATURE".
- Motor de recomendación filtra obras 18+.
- Publicación requiere pasar moderación obligatoria (no fast-path SAFE directo para <16).
- Sin endpoints de creación de Reels con audio explícito.
- DM: sólo seguidores aprobados + bloqueo de adultos desconocidos.

## Campos Nuevos Usuario
| Campo | Tipo | Descripción |
| ageGroup | ENUM(CHILD, TEEN, YOUNG, ADULT) | Derivado DOB |
| dob | DATE | Guardado cifrado. |
| parentalConsentStatus | ENUM(NOT_REQUIRED, REQUESTED, VERIFIED, REJECTED) | |
| kycStatus | ENUM(NOT_REQUIRED, PENDING, VERIFIED, FAILED) | Monetización. |

## Flujos
Registro -> Fecha nacimiento -> Cálculo ageGroup -> si CHILD: iniciar parental consent.
Intento monetizar -> kycStatus=PENDING -> proveedor externo -> VERIFIED/FAILED.

## Moderación Reforzada
- ageGroup != ADULT => multiplicador de sensibilidad en minor_risk_score.
- Contenido generado por menor siempre pasa por revisión humana si toca temas sensibles (violencia, sexual, drogas).

## Datos & Retención
- DOB cifrado (campo separado). Minimización: edad calculada para lógica diaria.
- Logs de consentimiento conservados mínimo 5 años (pruebas regulatorias COPPA).

## Incidentes
- Detección grooming: patrones conversación -> flag GROOMING_SUSPECT -> bloqueo DM y alerta seguridad.
- Protocolo respuesta incluye notificación interna equipo Trust & Safety.
