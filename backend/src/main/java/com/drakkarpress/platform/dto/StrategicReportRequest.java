package com.drakkarpress.platform.dto;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class StrategicReportRequest {
    private Double pricingBase;            // Precio base eBook
    private Double pricingTest;            // Precio test eBook
    private Double cvrBaseline;            // Conversión baseline (%)
    private Double cvrTolerancia;          // Tolerancia caída (%)
    private Map<String, Double> elasticidadSegmentos; // Ej: {romance:-0.9}
    private Map<String, String> comisionTiers;        // Ej: {debut:20%}
    private Double mixOrganicoActual;      // % tráfico orgánico actual
    private Double mixOrganicoTargetM18;   // % objetivo M18
    private Double ltvCacActual;           // Ratio LTV/CAC actual
    private Double ltvCacTarget;           // Meta LTV/CAC
    private List<Double> proyeccionesIngresosY; // Ingresos Y1..Y5 (en millones)
    private Double wacc;                   // WACC asumido
    private Double tasaDescuento;          // Tasa descuento si distinta de WACC
    private Map<String, Double> costosFijos; // {infra:..., soporte:...}
    private Map<String, Double> costosVariablesUnit; // {ebook:..., pod:...}
    private Double retencionAutor12m;      // %
    private Double retencionLector90d;     // %
    private Double npsAutor;               // NPS autores
    private Double npsLector;              // NPS lectores
    private List<String> idiomasPlan;      // PT, EN, FR...
    private Map<String, Double> capexIdiomas; // {PT:10000, EN:15000}
    private Map<String, Double> podMargen; // {proveedorA:0.32}
    private Map<String, Double> churnDrivers; // {autor:0.18, lector:0.27}
    private Map<String, Double> escenariosPrecio; // {down:-0.3, up:+0.25}
    private Integer monteCarloSimulaciones; // N simulaciones deseadas
}