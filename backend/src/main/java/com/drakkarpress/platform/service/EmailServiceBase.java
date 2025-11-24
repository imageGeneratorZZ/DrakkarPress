package com.drakkarpress.platform.service;

import com.drakkarpress.platform.model.PaymentTransaction;
import com.drakkarpress.platform.model.User;
import com.drakkarpress.platform.model.BookPurchase;

/**
 * Base contract simplificado para servicios de email.
 * Evita acoplar PaymentService a implementación concreta en perfil local.
 */
public abstract class EmailServiceBase {
    public void sendWelcomeEmail(User user, PricingService.PricingInfo pricing) {}
    public void sendPurchaseConfirmation(User user, PaymentTransaction tx, PricingService.PricingInfo pricing) {}
    public void sendRenewalReminder(User user, int days) {}
    public void sendVerificationEmail(User user, String token) {}
    public void sendPasswordResetEmail(User user, String token) {}
    public void sendEbookPurchaseConfirmation(BookPurchase purchase) {}
    public void sendMembershipActivatedEmail(String email, String planType) {}
}
