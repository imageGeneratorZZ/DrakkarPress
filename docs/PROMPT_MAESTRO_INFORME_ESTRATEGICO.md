## Prompt Maestro Informe Estratégico Drakkar

Este archivo define un PROMPT reutilizable para generar, actualizar o auditar el **Informe Profesional Estratégico** de la Plataforma Drakkar con todas las características actuales: pricing escalable, fairness pro‑autor, motor de crecimiento IA + influencers, POD dual‑source, expansión multi‑idioma, evaluación financiera (VAN/TIR), escenarios M&A, Balanced Scorecard, mapa estratégico, barra/ecualizador de decisión y recomendaciones finales.

---
### 1. Objetivo General del Informe
Generar un documento ejecutivo profesional, con rigor cuantitativo y trazabilidad, que:
- Explique la propuesta de valor diferenciada (fairness + atribución + influencia basada en lectores activos + POD global).
- Presente proyecciones financieras a 5 años (ingresos, margen, EBITDA, FCF, VAN/TIR).
- Evalúe escenarios de salida (M&A) con rangos y múltiplos comparables.
- Defina gates críticos y un roadmap accionable.
- Integre riesgos y mitigaciones con evidencia cuantitativa.
- Traduzca la estrategia en Balanced Scorecard y mapa causa‑efecto.
- Genere una barra/equalizer de decisión con rating ponderado GO / HOLD / NO‑GO.
- Produzca conclusiones finales integradas y recomendaciones priorizadas.

---
### 2. Entradas Esperadas (Variables / Data Points)
El modelo debe aceptar (o simular si faltan) estas entradas:
- Pricing eBook actual y precio test (p.ej. $4.50 vs $5.29) + CVR baseline y tolerancia (≤ −12%).
- Estructura de comisión por escalas (20–15–10–8% con thresholds volumen autor).
- Elasticidad estimada por segmento (romance, técnico, general). Ej: E = −0.9 a −1.6.
- Cohortes de adopción orgánico vs paid (mix % mensual, CAC orgánico, CAC paid).
- LTV/CAC histórico y meta (≥ 6× Base, aspiración 8× Upside).
- Supuestos SaaS + comisiones eBook + comisiones impresos POD + otros ingresos.
- Costos fijos/año y costos variables unitarios (infra + pagos + soporte).
- Retención autor (12m) y lector (90d), NPS autor y lector.
- Datos POD: proveedores, SLA, % distribución, coste unitario, margen plataforma.
- Idiomas planificados y CAPEX por fase (PT, EN, FR, DE, IT, etc.).
- Benchmarks comparables M&A (múltiplos ingresos y EBITDA, casos: Wattpad, Lulu, Smashwords...).
- Parámetros WACC, tasa descuento, escenarios sensibilidad (precio, CAC, mix orgánico, churn, POD mix).
- Simulación Monte Carlo (si disponible): distribución de VAN y prob VAN>0.

---
### 3. Estructura de Salida (Secciones)
1. Resumen Ejecutivo.
2. Propuesta de Valor y Posicionamiento.
3. Modelo Comercial y Pricing.
4. Motor de Crecimiento.
5. Proyecciones Financieras 5 Años.
6. Evaluación Económica (VAN/TIR/FCF/Sensibilidad/Monte Carlo).
7. Escenarios M&A.
8. Balanced Scorecard.
9. Mapa Estratégico y Dashboard.
10. Riesgos y Mitigaciones.
11. Gates y Roadmap.
12. Barra/Ecualizador de Decisión.
13. Conclusiones Finales Integradas.
14. Apéndices (supuestos, metodología, fórmulas, fuentes).

---
### 4. Estilo y Formato
- Formato: Markdown profesional.
- Encabezados: `##` secciones, `###` subsecciones.
- Tablas legibles con unidades claras.
- Cada sección termina con bloque **Conclusión (...)**.
- Métricas: símbolos (≥, ≤, ±), unidades (M, %, pp, $).
- Lenguaje: técnico, conciso, orientado a decisión.
- Toda afirmación fuerte respaldada numéricamente.

---
### 5. Conclusiones y Validaciones (Guía)
Patrón mínimo por conclusión:
```
Conclusión (Etiqueta): [Afirmación central]; [Métrica 1]; [Métrica 2]; [Robustez / Sensibilidad]; [Riesgo residual + Mitigación].
```
Ejemplo:
```
Conclusión (Economía): VAN ≈ $3.7M @ 13% WACC (1.8× equity); TIR 24% > WACC+11pp; prob VAN>0 = 78% (Monte Carlo 10K); principal sensibilidad precio eBook (±$0.30 → ±$1.2M VAN) mitigada por segmentación E=-0.9 a -1.6.
```

---
### 6. Barra de Decisión (Ecualizador)
Pesos por dimensión:
```
Fairness / Propuesta Valor    0.20
Tracción / Mercado            0.20
Economics / Rentabilidad      0.20
Motor Crecimiento (CAC/LTV)   0.15
Operaciones / Riesgos         0.10
Opcionalidad Estratégica      0.10
Equipo / Capacidad Aprendiz.  0.05
```
Score total:
```
Score_total = Σ(peso_i * score_i) donde score_i ∈ [0,10]
Umbrales: GO ≥ 7.0 | HOLD 6.0–6.9 | NO‑GO < 6.0
```
Cada fila: Dimensión | Peso | Score | Barra | Justificación (<90 caracteres).
Resumen: **Score Ponderado Total: X → Categoría GO/HOLD/NO‑GO** + triggers vigilancia.
Triggers recomendados:
- CVR test precio < baseline −12%.
- Mix orgánico < 45% M12 / < 50% M18.
- Precisión IA < 70% dos periodos.
- Discrepancia atribución > 5%.
- Fraude influencers > 10% claims auditados.
Plan acción si Score < 7.0:
- Rollback parcial precio segmentado.
- Refuerzo SEO/contenido.
- Optimización motor recomendaciones.
- Auditoría antifraude intensiva.
- Postergar expansión idiomas.

---
### 7. Gates Críticos (Formato)
| Gate | Métrica | Umbral | Método Validación | Horizonte | Plan B |
|------|---------|--------|-------------------|----------|--------|
| Precio eBook | ΔCVR (%) | ≥ -12% | Test A/B n≥2500, potencia 80% | M6–M9 Y1 | Mantener $4.50 en segmentos sensibles |
| Mix Orgánico | % tráfico | ≥ 50% M18 | Cohortes, regresión LTV/CAC | M12–M18 | Incremento presupuesto contenido |
| Activación Tiers | % autores >50 ventas/mes | ≥ 40% | Curva adopción Q3–Q4 Y1 | Y2 | Extender incentivos onboarding |
| POD SLA | % cumplimiento | ≥ 95% | Monitor dual-source | M6+ | Rebalance proveedor/tercer regional |
| Idioma PT | CAC | < $35 | Piloto segmentado | Y4 | Delay y optimizar landing |

---
### 8. Riesgos y Mitigaciones (Formato)
| Riesgo | Impacto | Prob. | Señal Temprana | Mitigación | Residual |
|--------|---------|-------|----------------|------------|----------|
| Elasticidad mayor | Margen / ingreso | Media | CVR caídas > esperado | Segmentación precio / bundles | Baja |
| Saturación paid | CAC ↑ | Alta | Tendencia CAC +$ > plan | Migrar orgánico / influencers | Media |
| Dependencia POD | Interrupción supply | Media | SLA < 95% 2 meses | Dual-source + QA | Baja |
| Fraude atribución | Costo / margen | Media | Claims no verificados | Tracking hash + auditoría | Baja |
| Churn autor | LTV ↓ | Media | Retención < 80% | Mejoras soporte/value add | Media |

---
### 9. Recomendaciones (R1–R6)
- R1 (0–6m): Test precio eBook (n≥2,500) → decidir escalamiento.
- R2 (0–9m): Acelerar orgánico ≥45% M12 vía cluster contenido + interlinking.
- R3 (6–12m): Comité atribución + antifraude (discrepancia <5%).
- R4 (9–18m): Extender POD multi‑regional (share proveedor líder <50%).
- R5 (12–24m): Data room vivo para opcionalidad M&A Y4–Y5.
- R6 (Continuo): Revisión trimestral BSC y alertas automáticas.

---
### 10. Metodologías y Criterios de Rigor
- Test A/B: potencia ≥ 80%, α=0.05, tamaño muestra y efecto mínimo detectable.
- Monte Carlo: ≥ 5K simulaciones; P10 / P50 / P90; correlaciones claves.
- Sensibilidad: tornado ±10%/±20% sobre drivers principales.
- Elasticidad: intervalos IC95% por segmento.
- M&A: fuentes/múltiplos normalizados por margen/crecimiento.

---
### 11. Checklist Calidad del Output
- Cada sección con conclusión y datos.
- Tabla proyecciones SaaS / eBook / POD / Otros Y1–Y5.
- VAN/TIR con sensibilidad y Monte Carlo.
- M&A: rangos, múltiplos, ventana óptima.
- Balanced Scorecard completo (objetivos, KPIs, iniciativas).
- Barra decisión con score ponderado.
- 5 conclusiones finales y R1–R6.
- Riesgos matriz + señales tempranas.
- Gates con validación + plan B.

---
### 12. Plantilla Conclusión
```
Conclusión (Etiqueta): [Afirmación central]; [Métrica 1]; [Métrica 2]; [Robustez / Sensibilidad]; [Riesgo residual + Mitigación].
```
Ejemplo:
```
Conclusión (Growth): CAC blended cae $42→$28 (-33%) soportado por migración orgánico 35%→52% (r=0.67 con LTV/CAC); estabilidad influencers (CAC $24 SD=$3.2) amortigua saturación paid; riesgo fraude residual 6% mitigado con auditorías 90d.
```

---
### 13. Convenciones Numéricas y Símbolos
- Millones: M (3.40M)
- pp: puntos porcentuales (+5pp)
- Probabilidad: %
- Monetario: prefijo $.

---
### 14. Instrucción Principal para el Modelo
"Genera el informe siguiendo la estructura y estilo definidos. Utiliza datos proporcionados o simula dentro de rangos razonables (marcados como estimados). Asegura rigor cuantitativo en cada conclusión. Incluye la barra de decisión con cálculo ponderado y clasificación final GO/HOLD/NO‑GO. No omitas secciones. Destaca cada conclusión en box."

---
### 15. Ejemplo de Invocación (Pseudo)
```
INPUT:
pricing_base=4.50
pricing_test=5.29
cvr_baseline=7.8%
cvr_tolerancia=-12%
elasticidad_segmentos={romance:-0.9, tecnico:-1.6, general:-1.4}
comision_tiers={debut:20%, intermedio:15%, avanzado:10%, bestseller:8%}
mix_organico_actual=35%
mix_organico_target_M18=50%
LTV_CAC_actual=6.2
LTV_CAC_target=8.0
proyecciones_ingresos_Y=[3.40,5.90,8.40,15.20,26.05]
...
OUTPUT: Informe conforme a secciones 1–14.
```

---
### 16. Notas de Mantenimiento
Actualizar este prompt si:
- Cambian umbrales de gates.
- Se agregan canales de crecimiento (audio/audiolibros).
- Se revisa WACC / estructura capital.
- Nuevos benchmarks M&A disponibles.

---
### 17. Licencia de Uso Interno
Uso interno en la plataforma Drakkar para generación y auditoría estratégica; no publicar externamente sin revisión de datos sensibles.

---
### 18. Próximos Pasos Sugeridos
- Integrar este prompt en módulo `ai_integration.py` como plantilla.
- Añadir endpoint POST `/generate-report` que reciba JSON y devuelva Markdown.
- Extender export a HTML / DOCX / PDF (headless) en pipeline.

---
Fin del Prompt Maestro