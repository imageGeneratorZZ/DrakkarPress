package com.drakkarpress.platform.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Servicio de conversión de formatos de ebook usando Calibre CLI
 * Soporta: EPUB → MOBI, AZW3, PDF, KPF
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EbookConversionService {

    @Value("${app.calibre.ebook-convert-path:/usr/bin/ebook-convert}")
    private String ebookConvertPath;

    @Value("${app.conversion.temp-dir:${java.io.tmpdir}/drakkarpress-conversions}")
    private String tempDir;

    /**
     * Convierte EPUB a MOBI (formato Kindle legacy)
     */
    public File convertEpubToMobi(File epubFile) throws IOException, InterruptedException {
        String outputPath = getTempFilePath(epubFile.getName(), ".mobi");
        List<String> command = new ArrayList<>();
        command.add(ebookConvertPath);
        command.add(epubFile.getAbsolutePath());
        command.add(outputPath);
        command.add("--output-profile=kindle");
        command.add("--no-inline-toc");
        
        executeConversion(command);
        return new File(outputPath);
    }

    /**
     * Convierte EPUB a AZW3 (formato Kindle moderno, soporta KF8)
     */
    public File convertEpubToAzw3(File epubFile) throws IOException, InterruptedException {
        String outputPath = getTempFilePath(epubFile.getName(), ".azw3");
        List<String> command = new ArrayList<>();
        command.add(ebookConvertPath);
        command.add(epubFile.getAbsolutePath());
        command.add(outputPath);
        command.add("--output-profile=kindle_pw3");
        command.add("--enable-heuristics");
        
        executeConversion(command);
        return new File(outputPath);
    }

    /**
     * Convierte EPUB a PDF (para impresión o lectura)
     */
    public File convertEpubToPdf(File epubFile, boolean printReady) throws IOException, InterruptedException {
        String outputPath = getTempFilePath(epubFile.getName(), ".pdf");
        List<String> command = new ArrayList<>();
        command.add(ebookConvertPath);
        command.add(epubFile.getAbsolutePath());
        command.add(outputPath);
        
        if (printReady) {
            // PDF optimizado para impresión: márgenes, tamaño papel
            command.add("--pdf-page-numbers");
            command.add("--paper-size=letter");
            command.add("--pdf-default-font-size=12");
            command.add("--pdf-mono-font-size=12");
            command.add("--margin-left=72");
            command.add("--margin-right=72");
            command.add("--margin-top=72");
            command.add("--margin-bottom=72");
        } else {
            // PDF para lectura digital
            command.add("--pdf-page-numbers");
            command.add("--paper-size=a4");
        }
        
        executeConversion(command);
        return new File(outputPath);
    }

    /**
     * Convierte EPUB a KPF (Kindle Print Replica)
     * Nota: KPF requiere herramientas especializadas de Amazon KDP
     */
    public File convertEpubToKpf(File epubFile) throws IOException {
        // KPF es formato propietario de Amazon
        // Requiere Amazon Kindle Create o conversión manual en KDP
        log.warn("KPF conversion not directly supported - must use Amazon Kindle Create tool");
        throw new UnsupportedOperationException(
            "KPF conversion requires Amazon Kindle Create. " +
            "Upload EPUB directly to KDP for automatic KPF generation."
        );
    }

    /**
     * Valida que el archivo EPUB sea válido usando epubcheck
     */
    public boolean validateEpub(File epubFile) {
        try {
            // TODO: Integrar epubcheck CLI
            // Por ahora validación básica
            return epubFile.exists() && 
                   epubFile.length() > 0 && 
                   epubFile.getName().endsWith(".epub");
        } catch (Exception e) {
            log.error("Error validating EPUB", e);
            return false;
        }
    }

    /**
     * Optimiza EPUB para distribución (compresión, limpieza)
     */
    public File optimizeEpub(File epubFile) throws IOException, InterruptedException {
        String outputPath = getTempFilePath(epubFile.getName(), "_optimized.epub");
        List<String> command = new ArrayList<>();
        command.add(ebookConvertPath);
        command.add(epubFile.getAbsolutePath());
        command.add(outputPath);
        command.add("--remove-first-image");
        command.add("--insert-metadata");
        command.add("--epub-inline-toc");
        
        executeConversion(command);
        return new File(outputPath);
    }

    private void executeConversion(List<String> command) throws IOException, InterruptedException {
        log.info("Executing conversion: {}", String.join(" ", command));
        
        ProcessBuilder pb = new ProcessBuilder(command);
        pb.redirectErrorStream(true);
        Process process = pb.start();

        // Capturar output
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        StringBuilder output = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            output.append(line).append("\n");
            log.debug("Calibre: {}", line);
        }

        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new IOException("Conversion failed with exit code " + exitCode + ": " + output.toString());
        }

        log.info("Conversion completed successfully");
    }

    private String getTempFilePath(String originalName, String extension) {
        try {
            Path tempDirPath = Paths.get(tempDir);
            if (!Files.exists(tempDirPath)) {
                Files.createDirectories(tempDirPath);
            }
            String baseName = originalName.replaceFirst("[.][^.]+$", "");
            return tempDirPath.resolve(baseName + extension).toString();
        } catch (IOException e) {
            throw new RuntimeException("Failed to create temp directory", e);
        }
    }

    /**
     * Limpia archivos temporales de conversión
     */
    public void cleanupTempFiles() {
        try {
            Path tempDirPath = Paths.get(tempDir);
            if (Files.exists(tempDirPath)) {
                Files.walk(tempDirPath)
                    .filter(Files::isRegularFile)
                    .forEach(file -> {
                        try {
                            Files.delete(file);
                            log.debug("Deleted temp file: {}", file);
                        } catch (IOException e) {
                            log.warn("Failed to delete temp file: {}", file, e);
                        }
                    });
            }
        } catch (IOException e) {
            log.error("Error cleaning temp files", e);
        }
    }
}
