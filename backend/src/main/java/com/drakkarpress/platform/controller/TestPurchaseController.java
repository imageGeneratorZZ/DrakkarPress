package com.drakkarpress.platform.controller;

import com.drakkarpress.platform.model.BookPurchase;
import com.drakkarpress.platform.model.User;
import com.drakkarpress.platform.repository.BookPurchaseRepository;
import com.drakkarpress.platform.repository.PlatformUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

/**
 * Controlador temporal para facilitar pruebas de dedicatorias:
 * Permite crear una compra dummy de un libro ya generado.
 * NO usar en producción; eliminar cuando exista flujo real de checkout.
 */
@RestController
@RequestMapping("/api/public/test-purchases") // Cambiar a /public/ para bypass security
@RequiredArgsConstructor
public class TestPurchaseController {

    private final BookPurchaseRepository purchaseRepository;
    private final PlatformUserRepository platformUserRepository;

    @PostMapping("/{bookId}")
    public ResponseEntity<?> createDummyPurchase(@PathVariable UUID bookId,
                                                 @RequestBody(required = false) Map<String, Object> body) {
        try {
            System.out.println("=== TEST PURCHASE REQUEST ===");
            System.out.println("BookId: " + bookId);
            System.out.println("Body: " + body);
            
            // Crear usuario dummy para testing (sin autenticación real)
            User user = platformUserRepository.findByEmail("demo@drakkarpress.com").orElseThrow(() ->
                new RuntimeException("Usuario demo no encontrado"));
            
            System.out.println("Usuario encontrado: " + user.getEmail());

            // Para testing: crear purchase sin libro real (book_id nullable)
            String format = (String) (body != null ? body.getOrDefault("format", "EPUB") : "EPUB");
            String message = (String) (body != null ? body.getOrDefault("dedication", "Probando dedicación") : "Probando dedicación");
            BigDecimal price = BigDecimal.valueOf(Double.parseDouble(String.valueOf(body != null ? body.getOrDefault("price", 0.00) : 0.00)));

            System.out.println("Creando purchase: format=" + format + ", message=" + message + ", price=" + price);

            String testFilePath = "https://example.com/test-book-" + bookId + ".epub";
            
            BookPurchase purchase = BookPurchase.createEbookPurchase(user, null, price, format, null, message);
            System.out.println("Purchase creado, seteando filePath...");
            purchase.setFilePath(testFilePath);
            purchase.markCompleted();
            
            System.out.println("Guardando en DB...");
            purchase = purchaseRepository.save(purchase);
            System.out.println("Purchase guardado con ID: " + purchase.getId());

            Map<String, Object> response = Map.of(
                    "purchaseId", purchase.getId(),
                    "bookId", bookId,
                    "filePath", purchase.getFilePath(),
                    "dedicationMessage", purchase.getDedicationMessage()
            );
            System.out.println("Response: " + response);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            System.err.println("ERROR EN TEST PURCHASE: " + e.getClass().getName() + " - " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
}