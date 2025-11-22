package com.drakkarpress.platform.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * Servicio para generar códigos QR.
 * Usado para incluir QR de DrakkarPress en libros generados.
 */
@Service
@Slf4j
public class QrCodeGeneratorService {

    /**
     * Genera un código QR con la URL especificada.
     * 
     * @param url URL a codificar en el QR
     * @param width Ancho del QR en píxeles
     * @param height Alto del QR en píxeles
     * @return BufferedImage del QR generado
     */
    public BufferedImage generateQrCode(String url, int width, int height) throws WriterException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        hints.put(EncodeHintType.MARGIN, 1);
        
        BitMatrix bitMatrix = qrCodeWriter.encode(url, BarcodeFormat.QR_CODE, width, height, hints);
        return MatrixToImageWriter.toBufferedImage(bitMatrix);
    }

    /**
     * Genera un código QR y lo guarda en el path especificado.
     */
    public Path generateQrCodeFile(String url, Path outputPath, int size) throws WriterException, IOException {
        BufferedImage qrImage = generateQrCode(url, size, size);
        ImageIO.write(qrImage, "PNG", outputPath.toFile());
        log.info("✅ Código QR generado: {}", outputPath);
        return outputPath;
    }

    /**
     * Genera un código QR y lo retorna como Base64 para embeber en HTML.
     */
    public String generateQrCodeBase64(String url, int size) throws WriterException, IOException {
        BufferedImage qrImage = generateQrCode(url, size, size);
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(qrImage, "PNG", baos);
        byte[] imageBytes = baos.toByteArray();
        
        return Base64.getEncoder().encodeToString(imageBytes);
    }

    /**
     * Genera QR específico para DrakkarPress con libro ID.
     */
    public String generateDrakkarPressQr(String bookId, int size) throws WriterException, IOException {
        String url = "https://www.drakkarpress.com/books/" + bookId;
        return generateQrCodeBase64(url, size);
    }

    /**
     * Genera QR genérico para DrakkarPress homepage.
     */
    public String generateDrakkarPressHomeQr(int size) throws WriterException, IOException {
        return generateQrCodeBase64("https://www.drakkarpress.com", size);
    }
}
