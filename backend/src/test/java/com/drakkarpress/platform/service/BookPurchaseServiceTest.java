package com.drakkarpress.platform.service;

import com.drakkarpress.model.Book;
import com.drakkarpress.platform.model.BookPurchase;
import com.drakkarpress.platform.model.PaymentTransaction;
import com.drakkarpress.platform.model.RoyaltySplit;
import com.drakkarpress.platform.model.User;
import com.drakkarpress.platform.repository.*;
import com.drakkarpress.platform.repository.PlatformUserRepository;
import com.drakkarpress.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Test básico de cálculo de comisión (25% usuarios FREE, 5% PREMIUM) en BookPurchaseService
 */
@SuppressWarnings("null")
public class BookPurchaseServiceTest {

    private BookPurchaseRepository purchaseRepository;
    private BookRepository bookRepository;
    private PlatformUserRepository userRepository;
    private PaymentTransactionRepository paymentRepository;
    private EmailService emailService;
    private RoyaltySplitRepository royaltySplitRepository;
    private BookPurchaseService service;

    @BeforeEach
    void setup() {
        purchaseRepository = mock(BookPurchaseRepository.class);
        bookRepository = mock(BookRepository.class);
        userRepository = mock(PlatformUserRepository.class);
        paymentRepository = mock(PaymentTransactionRepository.class);
        emailService = mock(EmailService.class);
        royaltySplitRepository = mock(RoyaltySplitRepository.class);
        service = new BookPurchaseService(purchaseRepository, bookRepository, userRepository, paymentRepository, emailService, royaltySplitRepository);
    }

    @Test
    void testRoyaltySplitFreeUserApplies25Percent() {
        UUID purchaseId = UUID.randomUUID();
        User user = User.builder().id(UUID.randomUUID()).subscription("FREE").username("autor").build();
        Book book = Book.builder().id(UUID.randomUUID()).title("Libro Test").author(null).priceDigital(new BigDecimal("10.00")).pages(100).genre(Book.Genre.FANTASY).build();

        BookPurchase purchase = BookPurchase.builder()
                .id(purchaseId)
                .user(user)
                .book(book)
                .pricePaid(new BigDecimal("10.00"))
                .status("PENDING")
                .build();
        PaymentTransaction tx = PaymentTransaction.builder().id(UUID.randomUUID()).paymentStatus("PENDING").build();

        when(purchaseRepository.findById(purchaseId)).thenReturn(Optional.of(purchase));
        when(paymentRepository.findById(any())).thenReturn(Optional.of(tx));

        service.markEbookPurchaseCompleted(purchaseId);

        ArgumentCaptor<RoyaltySplit> captor = ArgumentCaptor.forClass(RoyaltySplit.class);
        verify(royaltySplitRepository, times(1)).save(captor.capture());
        RoyaltySplit split = captor.getValue();
        assertEquals(new BigDecimal("10.00"), split.getGrossAmount());
        assertEquals(new BigDecimal("2.5000"), split.getPlatformFee().setScale(4)); // 25%
        assertEquals(new BigDecimal("7.50"), split.getNetAmount());
    }

    @Test
    void testRoyaltySplitPremiumUserApplies5Percent() {
        UUID purchaseId = UUID.randomUUID();
        User user = User.builder().id(UUID.randomUUID()).subscription("PREMIUM_REGULAR").username("autor").build();
        Book book = Book.builder().id(UUID.randomUUID()).title("Libro Test").author(null).priceDigital(new BigDecimal("10.00")).pages(100).genre(Book.Genre.FANTASY).build();

        BookPurchase purchase = BookPurchase.builder()
                .id(purchaseId)
                .user(user)
                .book(book)
                .pricePaid(new BigDecimal("10.00"))
                .status("PENDING")
                .build();
        PaymentTransaction tx = PaymentTransaction.builder().id(UUID.randomUUID()).paymentStatus("PENDING").build();

        when(purchaseRepository.findById(purchaseId)).thenReturn(Optional.of(purchase));
        when(paymentRepository.findById(any())).thenReturn(Optional.of(tx));

        service.markEbookPurchaseCompleted(purchaseId);

        ArgumentCaptor<RoyaltySplit> captor = ArgumentCaptor.forClass(RoyaltySplit.class);
        verify(royaltySplitRepository, times(1)).save(captor.capture());
        RoyaltySplit split = captor.getValue();
        assertEquals(new BigDecimal("10.00"), split.getGrossAmount());
        assertEquals(new BigDecimal("0.5000"), split.getPlatformFee().setScale(4)); // 5%
        assertEquals(new BigDecimal("9.50"), split.getNetAmount());
    }
}
