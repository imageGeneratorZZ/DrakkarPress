package com.drakkarpress.backend.dto.lulu;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LuluBookSpecificationDTO {
    private String title;
    private String subtitle;
    private String author;
    private String isbn;
    
    // Formato del libro
    private String bindingType; // PERFECT_BOUND, SADDLE_STITCH, COIL
    private String coverFinish; // GLOSS, MATTE
    private String trimSize; // US_TRADE_6X9, US_LETTER_8.5X11, etc.
    private String paperType; // WHITE, CREAM
    
    // Contenido
    private Integer pageCount;
    private String pdfUrl; // URL del PDF en S3
    private String coverUrl; // URL de la portada en S3
    
    // Precios
    private BigDecimal printCost; // Costo de impresión de Lulu
    private BigDecimal authorRevenue; // Ganancia del autor
    private BigDecimal retailPrice; // Precio al público
    
    // Metadata
    private String description;
    private String language; // ES, EN, PT, FR, DE, IT
    private String[] categories;
}
