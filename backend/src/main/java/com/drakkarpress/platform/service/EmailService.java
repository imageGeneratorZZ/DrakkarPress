package com.drakkarpress.platform.service;

import com.drakkarpress.platform.model.Book;
import com.drakkarpress.platform.model.BookPurchase;
import com.drakkarpress.platform.model.User;
import com.drakkarpress.platform.model.PaymentTransaction;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;

/**
 * Servicio de envío de emails
 */
@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private TemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${app.frontend-url:http://localhost:3000}")
    private String frontendUrl;

    @Value("${app.name:DrakkarPress}")
    private String appName;

    /**
     * Enviar email de bienvenida con información de fase
     */
    public void sendWelcomeEmail(User user, PricingService.PricingInfo pricing) {
        try {
            Context context = new Context();
            context.setVariable("userName", user.getFullName() != null ? user.getFullName() : user.getUsername());
            context.setVariable("userNumber", user.getUserNumber());
            context.setVariable("phase", getPhaseName(pricing.plan));
            context.setVariable("monthlyPrice", pricing.monthlyPrice);
            context.setVariable("annualPrice", pricing.annualPrice);
            context.setVariable("isGrandfathered", pricing.isGrandfathered);
            context.setVariable("badge", pricing.badge);
            context.setVariable("welcomeMessage", getWelcomeMessage(user.getUserNumber(), pricing));
            context.setVariable("frontendUrl", frontendUrl);
            context.setVariable("appName", appName);

            String htmlContent = templateEngine.process("email/welcome", context);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(user.getEmail());
            helper.setSubject("¡Bienvenido a " + appName + "! - Usuario #" + user.getUserNumber());
            helper.setText(htmlContent, true);

            mailSender.send(message);
        } catch (MessagingException e) {
            // Log error pero no fallar el registro
            System.err.println("Error sending welcome email: " + e.getMessage());
        }
    }

    /**
     * Enviar confirmación de compra de membresía
     */
    public void sendPurchaseConfirmation(User user, PaymentTransaction transaction, PricingService.PricingInfo pricing) {
        try {
            Context context = new Context();
            context.setVariable("userName", user.getFullName() != null ? user.getFullName() : user.getUsername());
            context.setVariable("userNumber", user.getUserNumber());
            context.setVariable("transactionId", transaction.getId().toString());
            context.setVariable("plan", getPhaseName(pricing.plan));
            context.setVariable("amount", transaction.getAmount());
            context.setVariable("frequency", transaction.getPlanType());
            context.setVariable("date", transaction.getCreatedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
            context.setVariable("isGrandfathered", pricing.isGrandfathered);
            context.setVariable("badge", pricing.badge);
            context.setVariable("monthlyPrice", pricing.monthlyPrice);
            context.setVariable("annualPrice", pricing.annualPrice);
            context.setVariable("frontendUrl", frontendUrl);
            context.setVariable("appName", appName);

            String htmlContent = templateEngine.process("email/purchase-confirmation", context);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(user.getEmail());
            helper.setSubject("Confirmación de compra - " + appName + " Premium");
            helper.setText(htmlContent, true);

            mailSender.send(message);
        } catch (MessagingException e) {
            System.err.println("Error sending purchase confirmation: " + e.getMessage());
        }
    }

    /**
     * Enviar recordatorio de renovación
     */
    public void sendRenewalReminder(User user, int daysUntilExpiration) {
        try {
            Context context = new Context();
            context.setVariable("userName", user.getFullName() != null ? user.getFullName() : user.getUsername());
            context.setVariable("daysUntilExpiration", daysUntilExpiration);
            context.setVariable("frontendUrl", frontendUrl);
            context.setVariable("appName", appName);

            String htmlContent = templateEngine.process("email/renewal-reminder", context);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(user.getEmail());
            helper.setSubject("Recordatorio: Tu membresía expira en " + daysUntilExpiration + " días");
            helper.setText(htmlContent, true);

            mailSender.send(message);
        } catch (MessagingException e) {
            System.err.println("Error sending renewal reminder: " + e.getMessage());
        }
    }

    /**
     * Enviar email de verificación
     */
    public void sendVerificationEmail(User user, String verificationToken) {
        try {
            String verificationUrl = frontendUrl + "/verify-email?token=" + verificationToken;

            Context context = new Context();
            context.setVariable("userName", user.getFullName() != null ? user.getFullName() : user.getUsername());
            context.setVariable("verificationUrl", verificationUrl);
            context.setVariable("appName", appName);

            String htmlContent = templateEngine.process("email/verification", context);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(user.getEmail());
            helper.setSubject("Verifica tu cuenta de " + appName);
            helper.setText(htmlContent, true);

            mailSender.send(message);
        } catch (MessagingException e) {
            System.err.println("Error sending verification email: " + e.getMessage());
        }
    }

    /**
     * Enviar email de reset de contraseña
     */
    public void sendPasswordResetEmail(User user, String resetToken) {
        try {
            String resetUrl = frontendUrl + "/reset-password?token=" + resetToken;

            Context context = new Context();
            context.setVariable("userName", user.getFullName() != null ? user.getFullName() : user.getUsername());
            context.setVariable("resetUrl", resetUrl);
            context.setVariable("appName", appName);

            String htmlContent = templateEngine.process("email/password-reset", context);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(user.getEmail());
            helper.setSubject("Restablecer contraseña - " + appName);
            helper.setText(htmlContent, true);

            mailSender.send(message);
        } catch (MessagingException e) {
            System.err.println("Error sending password reset email: " + e.getMessage());
        }
    }

    /**
     * Obtener nombre de fase legible
     */
    private String getPhaseName(String plan) {
        return switch (plan) {
            case "PREMIUM_PHASE_1" -> "Fundador";
            case "PREMIUM_PHASE_2" -> "Early Adopter";
            case "PREMIUM_PHASE_3" -> "Launch Member";
            case "PREMIUM_REGULAR" -> "Premium";
            case "PREMIUM_COURTESY" -> "Premium Cortesía";
            default -> "Free";
        };
    }

    /**
     * Generar mensaje de bienvenida personalizado
     */
    private String getWelcomeMessage(Long userNumber, PricingService.PricingInfo pricing) {
        if (userNumber <= 1000) {
            return "¡Felicidades! Eres FUNDADOR #" + userNumber + ". " +
                   "Tu precio de $" + pricing.annualPrice + "/año está bloqueado para siempre. " +
                   "Recibirás el badge exclusivo 'Founder' que nadie más podrá obtener después.";
        }
        
        if (userNumber <= 10000) {
            return "¡Bienvenido Early Adopter #" + userNumber + "! " +
                   "Tu precio de $" + pricing.annualPrice + "/año está bloqueado de por vida. " +
                   "Recibirás el badge exclusivo 'Early Adopter'.";
        }
        
        if (userNumber <= 15000) {
            return "¡Bienvenido! Eres el usuario #" + userNumber + ". " +
                   "Tu precio de $" + pricing.annualPrice + "/año está bloqueado de por vida. " +
                   "Recibirás el badge exclusivo 'Launch Member'.";
        }
        
        return "¡Bienvenido! Eres el usuario #" + userNumber + ". " +
               "Tu precio regular es $" + pricing.monthlyPrice + "/mes o $" + pricing.annualPrice + "/año.";
    }

    /**
     * Enviar confirmación de compra de ebook con link de descarga
     */
    public void sendEbookPurchaseConfirmation(com.drakkarpress.platform.model.BookPurchase purchase) {
        try {
            User user = purchase.getUser();
            com.drakkarpress.platform.model.Book book = purchase.getBook();

            String downloadExpires = purchase.getDownloadExpiresAt() != null ? 
                purchase.getDownloadExpiresAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : "Sin expiración";

            String htmlContent = "<!DOCTYPE html>" +
                "<html><head><meta charset='UTF-8'><style>" +
                "body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }" +
                ".container { max-width: 600px; margin: 0 auto; padding: 20px; }" +
                ".header { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; padding: 30px; text-align: center; }" +
                ".content { background: #f9f9f9; padding: 30px; }" +
                ".button { display: inline-block; padding: 15px 30px; background: #667eea; color: white; text-decoration: none; border-radius: 5px; font-weight: bold; }" +
                ".details { background: white; padding: 20px; margin: 20px 0; border-radius: 5px; }" +
                "</style></head><body><div class='container'>" +
                "<div class='header'><h1>📚 ¡Gracias por tu compra!</h1></div>" +
                "<div class='content'>" +
                "<p>Hola <strong>" + (user.getFullName() != null ? user.getFullName() : user.getUsername()) + "</strong>,</p>" +
                "<p>Tu compra ha sido procesada exitosamente. Ya puedes descargar tu ebook:</p>" +
                "<div class='details'>" +
                "<h3>" + book.getTitle() + "</h3>" +
                "<p><strong>Autor:</strong> " + book.getAuthor() + "</p>" +
                "<p><strong>Formato:</strong> " + purchase.getFormatName() + "</p>" +
                "<p><strong>Precio:</strong> " + purchase.getFormattedPrice() + "</p>" +
                "</div>" +
                "<center><a href='" + purchase.getDownloadLink() + "' class='button'>⬇️ Descargar Ahora</a></center>" +
                "<p style='margin-top: 30px;'><strong>⚠️ Importante:</strong></p><ul>" +
                "<li>Puedes descargar este libro hasta " + purchase.getDownloadLimit() + " veces</li>" +
                "<li>El link expira el " + downloadExpires + "</li>" +
                "<li>También puedes descargarlo desde tu <a href='" + frontendUrl + "/my-books.html'>Biblioteca Personal</a></li>" +
                "</ul>" +
                "<p>Si tienes problemas, contáctanos a " + fromEmail + "</p>" +
                "<p style='margin-top: 30px;'>¡Disfruta tu lectura! 📖<br>El equipo de " + appName + "</p>" +
                "</div></div></body></html>";

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(user.getEmail());
            helper.setSubject("📚 Tu ebook está listo para descargar - " + book.getTitle());
            helper.setText(htmlContent, true);

            mailSender.send(message);
            System.out.println("✅ Email de ebook enviado a: " + user.getEmail());

        } catch (MessagingException e) {
            System.err.println("❌ Error enviando email de ebook: " + e.getMessage());
            throw new RuntimeException("Error enviando email", e);
        }
    }
}
