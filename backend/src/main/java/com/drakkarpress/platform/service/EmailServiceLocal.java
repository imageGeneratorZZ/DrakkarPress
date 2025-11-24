package com.drakkarpress.platform.service;

import com.drakkarpress.platform.model.PaymentTransaction;
import com.drakkarpress.platform.model.User;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

/**
 * Stub de EmailService para entorno local (no envía correos)
 */
@Service
@Profile("local")
public class EmailServiceLocal extends EmailServiceBase {

    public void sendWelcomeEmail(User user, PricingService.PricingInfo pricing) {
        System.out.println("[LOCAL][EMAIL] Bienvenida omitida para usuario " + user.getEmail());
    }

    public void sendPurchaseConfirmation(User user, PaymentTransaction tx, PricingService.PricingInfo pricing) {
        System.out.println("[LOCAL][EMAIL] Confirmación compra omitida tx=" + tx.getId());
    }

    public void sendRenewalReminder(User user, int days) {
        System.out.println("[LOCAL][EMAIL] Recordatorio renovación omitido days=" + days);
    }

    public void sendVerificationEmail(User user, String token) {
        System.out.println("[LOCAL][EMAIL] Verificación omitida token=" + token);
    }

    public void sendPasswordResetEmail(User user, String token) {
        System.out.println("[LOCAL][EMAIL] Reset password omitido token=" + token);
    }

    public void sendEbookPurchaseConfirmation(com.drakkarpress.platform.model.BookPurchase purchase) {
        System.out.println("[LOCAL][EMAIL] Ebook purchase omitida purchase=" + purchase.getId());
    }

    public void sendMembershipActivatedEmail(String email, String planType) {
        System.out.println("[LOCAL][EMAIL] Membresía activada (no real) email=" + email + " plan=" + planType);
    }
}
