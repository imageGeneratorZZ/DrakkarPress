package com.drakkarpress.repository;

import com.drakkarpress.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    
    List<Book> findByAuthorId(Long authorId);
    
    List<Book> findByGenre(String genre);
    
    List<Book> findByPublished(boolean published);
    
    @Query("SELECT b FROM Book b WHERE b.title LIKE %:keyword% OR b.description LIKE %:keyword%")
    List<Book> searchByKeyword(@Param("keyword") String keyword);
    
    @Query("SELECT b FROM Book b WHERE b.published = true ORDER BY b.createdAt DESC")
    List<Book> findPublishedBooksOrderByDate();
    
    @Query("SELECT b FROM Book b WHERE b.published = true ORDER BY b.price ASC")
    List<Book> findPublishedBooksOrderByPriceAsc();
    
    @Query("SELECT b FROM Book b WHERE b.published = true ORDER BY b.price DESC")
    List<Book> findPublishedBooksOrderByPriceDesc();
    
    @Query("SELECT b FROM Book b JOIN Sale s ON b.id = s.bookId GROUP BY b.id ORDER BY COUNT(s.id) DESC")
    List<Book> findBestSellers();
    
    Optional<Book> findByIsbn(String isbn);
}
