package com.drakkarpress.platform.dto;

import com.drakkarpress.model.Book;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookPublicResponse {
    private UUID id;
    private String title;
    private String synopsis;
    private String coverImageUrl;
    private BigDecimal priceDigital;
    private BigDecimal pricePhysical;
    private String genre;
    private String authorName;

    public static BookPublicResponse from(Book b) {
        return BookPublicResponse.builder()
                .id(b.getId())
                .title(b.getTitle())
                .synopsis(b.getSynopsis())
                .coverImageUrl(b.getCoverImageUrl())
                .priceDigital(b.getPriceDigital())
                .pricePhysical(b.getPricePhysical())
                .genre(b.getGenre() != null ? b.getGenre().name() : null)
                .authorName(b.getAuthor() != null ? b.getAuthor().getFullName() : null)
                .build();
    }
}
