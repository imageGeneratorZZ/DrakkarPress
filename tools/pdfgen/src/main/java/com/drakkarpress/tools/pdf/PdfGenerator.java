package com.drakkarpress.tools.pdf;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import com.vladsch.flexmark.ext.tables.TablesExtension;
import com.vladsch.flexmark.html2md.converter.FlexmarkHtmlConverter;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.data.MutableDataSet;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Base64;
import java.util.List;
import java.util.Map;

public class PdfGenerator {

    public static void main(String[] args) throws Exception {
        Map<String, String> opts = Args.parse(args);
        String title = opts.getOrDefault("title", "Libro");
        String author = opts.getOrDefault("author", "Autor");
        String input = required(opts, "input");
        String cover = opts.get("cover"); // optional
        String out = opts.getOrDefault("out", "book.pdf");
        String css = opts.get("css"); // optional

        String contentHtml = loadContentAsHtml(input);
        String template = loadResource("/templates/book.html");
        String styles = (css != null) ? FileUtils.readFileToString(new File(css), StandardCharsets.UTF_8)
                                      : loadResource("/templates/styles.css");

        String coverDataUri = (cover != null && !cover.isBlank())
                ? toDataUri(new File(cover))
                : "";

        String html = template
                .replace("{{TITLE}}", esc(title))
                .replace("{{AUTHOR}}", esc(author))
                .replace("{{DATE}}", esc(LocalDate.now().toString()))
                .replace("{{COVER_IMAGE}}", coverDataUri)
                .replace("{{STYLES}}", styles)
                .replace("{{CONTENT}}", contentHtml);

        try (FileOutputStream fos = new FileOutputStream(out)) {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();
            builder.withHtmlContent(html, new File(".").toURI().toString());
            builder.toStream(fos);
            // Register fonts only if present in resources/fonts/
            registerFontIfPresent(builder, "/fonts/SourceSans3-Regular.ttf", "Source Sans 3", 400);
            registerFontIfPresent(builder, "/fonts/SourceSans3-Bold.ttf", "Source Sans 3", 700);
            registerFontIfPresent(builder, "/fonts/SourceSerif4-Regular.ttf", "Source Serif 4", 400);
            builder.metadata().title(title).author(author);
            builder.run();
        }

        System.out.println("PDF generado: " + out);
    }

    static String loadContentAsHtml(String path) throws Exception {
        String raw = FileUtils.readFileToString(new File(path), StandardCharsets.UTF_8);
        if (path.toLowerCase().endsWith(".md")) {
            MutableDataSet options = new MutableDataSet();
            options.set(Parser.EXTENSIONS, List.of(TablesExtension.create()));
            Parser parser = Parser.builder(options).build();
            Node doc = parser.parse(raw);
            String html = com.vladsch.flexmark.html.HtmlRenderer.builder(options).build().render(doc);
            return "<article class='content markdown'>\n" + html + "\n</article>";
        } else {
            return "<article class='content html'>\n" + raw + "\n</article>";
        }
    }

    static String loadResource(String res) throws Exception {
        try (var is = PdfGenerator.class.getResourceAsStream(res)) {
            if (is == null) throw new IllegalArgumentException("Recurso no encontrado: " + res);
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    static String toDataUri(File img) throws Exception {
        String mime = mime(img.getName());
        byte[] bytes = FileUtils.readFileToByteArray(img);
        return "data:" + mime + ";base64," + Base64.getEncoder().encodeToString(bytes);
    }

    static String mime(String name) {
        String n = name.toLowerCase();
        if (n.endsWith(".png")) return "image/png";
        if (n.endsWith(".jpg") || n.endsWith(".jpeg")) return "image/jpeg";
        if (n.endsWith(".webp")) return "image/webp";
        if (n.endsWith(".svg")) return "image/svg+xml";
        return "application/octet-stream";
    }

    static String esc(String s) {
        return s.replace("&","&amp;").replace("<","&lt;").replace(">","&gt;");
    }

    static String required(Map<String,String> o, String k) {
        String v = o.get(k);
        if (v == null || v.isBlank()) throw new IllegalArgumentException("--" + k + " es requerido");
        return v;
    }

    static void registerFontIfPresent(PdfRendererBuilder builder, String res, String family, int weight) {
        try (var is = PdfGenerator.class.getResourceAsStream(res)) {
            if (is != null) {
                builder.useFont(() -> PdfGenerator.class.getResourceAsStream(res), family, weight, PdfRendererBuilder.FontStyle.NORMAL, true);
            }
        } catch (Exception ignored) {}
    }

    static final class Args {
        static Map<String,String> parse(String[] args) {
            java.util.HashMap<String,String> map = new java.util.HashMap<>();
            for (int i=0; i<args.length; i++) {
                String a = args[i];
                if (a.startsWith("--")) {
                    String key = a.substring(2);
                    String val = ((i+1)<args.length && !args[i+1].startsWith("--")) ? args[++i] : "true";
                    map.put(key, val);
                }
            }
            return map;
        }
    }
}
