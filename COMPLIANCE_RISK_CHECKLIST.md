# Compliance & Risk Checklist

## Legal & Protección Menores
- [ ] Detección CSAM (hash + NLP) activa.
- [ ] Reporte automático autoridad (NCMEC / local) configurado.
- [ ] Flujos de apelación documentados.
- [ ] Parental consent verificado para <13 y audit log.

## Privacidad & Datos (GDPR / DPA)
- [ ] DPIA para IA y moderación archivada.
- [ ] Campos sensibles cifrados (DOB, emails).
- [ ] Política retención: flags 24m, contenido bloqueado 90d.
- [ ] Mecanismo derecho supresión usuarios.

## Seguridad
- [ ] Secrets rotan cada 90 días (integraciones externas).
- [ ] Cifrado en reposo (S3, DB) verificado.
- [ ] Logs acceso admin auditables (WORM).

## Monetización & Fiscal
- [ ] Cálculo comisiones probado (FREE 10%).
- [ ] Registro `RoyaltySplit` consolidado mensual.
- [ ] Export contable CSV / API listo.

## Moderación Operacional
- [ ] SLA revisión <30min riesgo menores.
- [ ] Panel moderación con razones y scores.
- [ ] Métricas: FP%, FN%, tiempo resolución.

## Transparencia & Reporting
- [ ] Endpoint métricas públicas /transparency/metrics.
- [ ] Informe trimestral generado (script automatizado).

## Riesgos Técnicos
- [ ] Circuit breakers export (KDP/Google/Lulu).
- [ ] Retry backoff implementado.
- [ ] Test carga feed (p95 < 400ms).

## Contenido & UX
- [ ] Marcado safetyStatus en Book (SAFE/REVIEW/BLOCKED).
- [ ] Indicadores visuales revisión en frontend.
- [ ] Alt text obligatorio imágenes.

## Incidentes & Respuesta
- [ ] Playbook grooming / explotación.
- [ ] Canal interno Trust & Safety.
- [ ] Auditoría apelaciones.
