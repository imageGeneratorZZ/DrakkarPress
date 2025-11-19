package com.drakkarpress.repository;

import com.drakkarpress.model.Book;
import com.drakkarpress.model.Book.BookStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BookRepository extends JpaRepository<Book, UUID> {
    
    List<Book> findByAuthorId(UUID authorId);
    
    List<Book> findByGenre(String genre);
    
    List<Book> findByStatus(BookStatus status);

    List<Book> findByStatusOrderByCreatedAtDesc(BookStatus status);

    List<Book> findByStatusOrderByPriceDigitalAsc(BookStatus status);

    List<Book> findByStatusOrderByPriceDigitalDesc(BookStatus status);
    
    @Query("SELECT b FROM Book b WHERE b.title LIKE %:keyword% OR b.description LIKE %:keyword%")
    List<Book> searchByKeyword(@Param("keyword") String keyword);

    default List<Book> findPublishedBooksOrderByDate() {
        return findByStatusOrderByCreatedAtDesc(BookStatus.PUBLISHED);
    }

    default List<Book> findPublishedBooksOrderByPriceAsc() {
        return findByStatusOrderByPriceDigitalAsc(BookStatus.PUBLISHED);
    }

    default List<Book> findPublishedBooksOrderByPriceDesc() {
        return findByStatusOrderByPriceDigitalDesc(BookStatus.PUBLISHED);
    }
    
    @Query("SELECT b FROM Book b JOIN b.salesList s GROUP BY b ORDER BY COUNT(s.id) DESC")
    List<Book> findBestSellers();
    
    Optional<Book> findByIsbn(String isbn);
}
