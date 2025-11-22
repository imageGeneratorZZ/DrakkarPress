package com.drakkarpress.platform.service;

import com.drakkarpress.platform.model.PurchaseDedication;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * Servicio responsable de inyectar una página de dedicatoria en un EPUB ya generado.
 * Implementación completa básica: reescribe el ZIP (EPUB) agregando dedication.xhtml y
 * actualiza el manifiesto (content.opf) para incluir el nuevo item y referencia en el spine.
 *
 * Limitaciones:
 * - Asume estructura estándar con META-INF/container.xml apuntando a un único content.opf
 * - No reordena TOC/nav; se inserta al inicio del spine.
 * - No modifica archivos NAV. Mejoras futuras: añadir entrada en toc.ncx / nav.xhtml.
 */
@Service
@RequiredArgsConstructor
public class EpubDedicationService {
    private static final Logger log = LoggerFactory.getLogger(EpubDedicationService.class);

    public String injectDedication(String epubPath, PurchaseDedication dedication) {
        if (epubPath == null) return null;
        Path original = Path.of(epubPath);
        if (!Files.exists(original)) {
            log.warn("EPUB no encontrado para dedicatoria: {}", epubPath);
            return epubPath;
        }
        String newFileName = original.getFileName().toString().replaceFirst("(?i)\\.epub$", "-dedicado.epub");
        Path target = original.getParent().resolve(newFileName);
           try (ZipInputStream zin2 = new ZipInputStream(Files.newInputStream(original));
               ZipOutputStream zout2 = new ZipOutputStream(Files.newOutputStream(target))) {
            String rootfilePath = null;
            // 1. Localizar container.xml para hallar content.opf
            ZipEntry entry;
            var entriesMemory = new java.util.LinkedHashMap<String, byte[]>();
            while ((entry = zin2.getNextEntry()) != null) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                zin2.transferTo(baos);
                byte[] data = baos.toByteArray();
                entriesMemory.put(entry.getName(), data);
                if ("META-INF/container.xml".equals(entry.getName())) {
                    rootfilePath = extractRootFile(data);
                }
            }
            if (rootfilePath == null) {
                log.warn("container.xml no encontrado, no se puede inyectar dedicación");
                return epubPath;
            }

            // 2. Modificar content.opf
            byte[] opfData = entriesMemory.get(rootfilePath);
            if (opfData == null) {
                log.warn("content.opf no encontrado en {}", rootfilePath);
                return epubPath;
            }
            byte[] updatedOpf = addDedicationToOpf(opfData);
            entriesMemory.put(rootfilePath, updatedOpf);

            // 3. Añadir dedication.xhtml
            String dedicationPath = deriveDedicationPath(rootfilePath);
            entriesMemory.put(dedicationPath, buildDedicationXhtml(dedication));

            // 4. Escribir nuevo EPUB
            for (var e : entriesMemory.entrySet()) {
                zout2.putNextEntry(new ZipEntry(e.getKey()));
                zout2.write(e.getValue());
                zout2.closeEntry();
            }
            zout2.finish();
            log.info("Dedicatoria inyectada en EPUB: {}", target);
            return target.toAbsolutePath().toString();
        } catch (Exception e) {
            log.error("Error al inyectar dedicatoria", e);
            return epubPath; // fallback: original sin cambios
        }
    }

    private String extractRootFile(byte[] containerXml) {
        try (var in = new ByteArrayInputStream(containerXml)) {
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(in);
            var list = doc.getElementsByTagName("rootfile");
            if (list.getLength() > 0) {
                return list.item(0).getAttributes().getNamedItem("full-path").getNodeValue();
            }
        } catch (Exception e) {
            log.warn("No se pudo parsear container.xml", e);
        }
        return null;
    }

    private byte[] addDedicationToOpf(byte[] opf) {
        try (var in = new ByteArrayInputStream(opf)) {
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(in);
            doc.getDocumentElement().normalize();
            Element manifest = (Element) doc.getElementsByTagName("manifest").item(0);
            Element spine = (Element) doc.getElementsByTagName("spine").item(0);
            if (manifest == null || spine == null) return opf; // estructura inesperada

            String itemId = "dedication-" + UUID.randomUUID();
            Element item = doc.createElement("item");
            item.setAttribute("id", itemId);
            item.setAttribute("href", "dedication.xhtml");
            item.setAttribute("media-type", "application/xhtml+xml");
            manifest.appendChild(item);

            Element itemref = doc.createElement("itemref");
            itemref.setAttribute("idref", itemId);
            // Insertar al principio del spine
            spine.insertBefore(itemref, spine.getFirstChild());

            Transformer t = TransformerFactory.newInstance().newTransformer();
            t.setOutputProperty(OutputKeys.INDENT, "yes");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            t.transform(new DOMSource(doc), new StreamResult(baos));
            return baos.toByteArray();
        } catch (Exception e) {
            log.warn("No se pudo modificar content.opf para dedicatoria", e);
            return opf;
        }
    }

    private String deriveDedicationPath(String opfPath) {
        int slash = opfPath.lastIndexOf('/');
        if (slash < 0) return "dedication.xhtml"; // raíz
        return opfPath.substring(0, slash + 1) + "dedication.xhtml";
    }

        private byte[] buildDedicationXhtml(PurchaseDedication d) {
                String msg = escape(d.getEffectiveMessage());
                String hash = d.getHash();
                StringBuilder sb = new StringBuilder();
                sb.append("<?xml version='1.0' encoding='UTF-8'?>\n");
                sb.append("<html xmlns='http://www.w3.org/1999/xhtml'>\n");
                sb.append("  <head>\n");
                sb.append("    <title>Dedicatoria</title>\n");
                sb.append("    <meta charset='UTF-8'/>\n");
                sb.append("    <style>body{font-family:Georgia,serif;margin:3em;}h1{text-align:center;}p{margin-top:1.5em;}</style>\n");
                sb.append("  </head>\n");
                sb.append("  <body>\n");
                sb.append("    <h1>Dedicatoria</h1>\n");
                sb.append("    <p>").append(msg).append("</p>\n");
                sb.append("    <hr/>\n");
                sb.append("    <p style='font-size:0.8em;color:#666'>Hash verificación: ").append(hash).append("</p>\n");
                sb.append("  </body>\n");
                sb.append("</html>\n");
                return sb.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8);
        }

    private String escape(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}
