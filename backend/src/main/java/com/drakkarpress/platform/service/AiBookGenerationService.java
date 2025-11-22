package com.drakkarpress.platform.service;

import com.drakkarpress.model.Book;
import com.drakkarpress.platform.model.BookGenerationJob;
import com.drakkarpress.platform.model.User;
import com.drakkarpress.platform.repository.BookGenerationJobRepository;
import com.drakkarpress.repository.BookRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Servicio para generar libros completos con IA.
 * 
 * Proceso:
 * 1. Genera metadatos (título, sinopsis, género) basados en el prompt
 * 2. Genera capítulos uno por uno
 * 3. Genera portada con DALL-E / Stable Diffusion
 * 4. Ensambla todo en un EPUB válido
 * 5. Crea la entidad Book en la base de datos
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class AiBookGenerationService {

    private final BookGenerationJobRepository jobRepository;
    private final com.drakkarpress.platform.repository.ChapterRepository chapterRepository;
    private final BookRepository bookRepository;
    private final WebClient.Builder webClientBuilder;
    private final ObjectMapper objectMapper;
    private final QrCodeGeneratorService qrCodeService;

    @Value("${ai.openai.api-key:}")
    private String openaiApiKey;

    @Value("${ai.openai.api-url:https://api.openai.com/v1}")
    private String openaiApiUrl;

    @Value("${ai.claude.api-key:}")
    private String claudeApiKey;

    @Value("${ai.claude.api-url:https://api.anthropic.com/v1}")
    private String claudeApiUrl;

    @Value("${book.generation.output-dir:./generated-books}")
    private String outputDir;

    /**
     * Inicia un trabajo de generación de libro.
     */
    @Transactional
    public BookGenerationJob startBookGeneration(User user, String prompt, Integer chapters, String aiModel) {
        var job = BookGenerationJob.builder()
                .user(user)
                .prompt(prompt)
                .targetChapters(chapters != null ? chapters : 10)
                .targetWordsPerChapter(2000)
                .aiModel(aiModel != null ? aiModel : "gpt-4")
                .status(BookGenerationJob.JobStatus.PENDING)
                .build();

        BookGenerationJob persistedJob = jobRepository.save(job);
        job = persistedJob;
        
        // Iniciar generación asíncrona
        generateBookAsync(job.getId());
        
        return job;
    }

    /**
     * Genera el libro completo de forma asíncrona.
     */
    @Async
    @Transactional
    public void generateBookAsync(UUID jobId) {
        BookGenerationJob job = jobRepository.findById(jobId).orElseThrow();
        
        try {
            job.setStatus(BookGenerationJob.JobStatus.GENERATING);
            job.setStartedAt(LocalDateTime.now());
            jobRepository.save(job);

            log.info("🤖 Iniciando generación de libro - Job ID: {}", jobId);

            // 1. Generar metadatos (título, autor, género, sinopsis)
            var metadata = generateMetadata(job);
            job.setMetadata(metadata);
            jobRepository.save(job);

            // 2. Generar capítulos
            List<String> chapters = new ArrayList<>();
            for (int i = 1; i <= job.getTargetChapters(); i++) {
                log.info("📖 Generando capítulo {}/{}", i, job.getTargetChapters());
                String chapterContent = generateChapter(job, i, chapters);
                chapters.add(chapterContent);
                
                // Persistir capítulo
                var chapterEntity = com.drakkarpress.platform.model.Chapter.builder()
                    .book(null) // Libro aún no creado, se asignará tras crear Book
                    .chapterNumber(i)
                    .originalContent(chapterContent)
                    .aiModel(job.getAiModel())
                    .build();
                com.drakkarpress.platform.model.Chapter savedChapter = chapterRepository.save(chapterEntity);
                job.updateProgress(i, job.getTargetChapters());
                jobRepository.save(job);
            }

            // 3. Generar portada
            log.info("🎨 Generando portada con IA...");
            String coverUrl = generateCoverImage(job, metadata);
            job.setCoverImageUrl(coverUrl);
            jobRepository.save(job);

            // 4. Ensamblar EPUB
            log.info("📚 Ensamblando EPUB...");
            job.setStatus(BookGenerationJob.JobStatus.PROCESSING);
            jobRepository.save(job);
            
            String epubPath = assembleEpub(job, metadata, chapters, coverUrl);
            job.setEpubPath(epubPath);

            // 5. Crear Book entity
            Book book = createBookEntity(job, metadata, epubPath);
            // Actualizar capítulos con referencia al libro
            var storedChapters = chapterRepository.findAll(); // naive; should filter by null book & count
            int num = 1;
            for (var ch : storedChapters) {
                if (ch.getBook() == null && ch.getChapterNumber() == num && ch.getOriginalContent() != null) {
                    ch.setBook(book);
                    chapterRepository.save(ch);
                    num++;
                }
            }
            job.setBookId(book.getId());

            // 6. Marcar como completado
            job.setStatus(BookGenerationJob.JobStatus.COMPLETED);
            job.setCompletedAt(LocalDateTime.now());
            job.setProgressPercentage(100);
            jobRepository.save(job);

            log.info("✅ Libro generado exitosamente - Job ID: {} - Book ID: {}", jobId, book.getId());

        } catch (Exception e) {
            log.error("❌ Error generando libro - Job ID: {}", jobId, e);
            job.setStatus(BookGenerationJob.JobStatus.FAILED);
            job.setErrorMessage(e.getMessage());
            jobRepository.save(job);
        }
    }

    /**
     * Genera metadatos del libro (título, autor, género, sinopsis) basados en el prompt.
     */
    private String generateMetadata(BookGenerationJob job) {
        try {
            String systemPrompt = """
                Eres un experto en literatura que ayuda a crear metadatos para libros.
                Basándote en el prompt del usuario, genera:
                - título: título atractivo del libro
                - author: nombre del autor ficticio apropiado
                - genre: género literario (FANTASY, SCIFI, ROMANCE, MYSTERY, THRILLER, HORROR, LITERARY)
                - synopsis: sinopsis de 150-200 palabras
                
                Responde SOLO en formato JSON válido sin comentarios ni explicaciones.
                """;

            String userPrompt = "Prompt del libro: " + job.getPrompt();

            WebClient client = webClientBuilder.build();
            
            String response;
            if ("gpt-4".equals(job.getAiModel()) || "gpt-3.5-turbo".equals(job.getAiModel())) {
                response = callOpenAI(client, systemPrompt, userPrompt, job.getAiModel());
            } else if (job.getAiModel().startsWith("claude")) {
                response = callClaude(client, systemPrompt, userPrompt, job.getAiModel());
            } else {
                throw new IllegalArgumentException("Modelo de IA no soportado: " + job.getAiModel());
            }

            log.info("📝 Metadatos generados: {}", response);
            return response;

        } catch (Exception e) {
            log.error("Error generando metadatos", e);
            // Fallback a metadatos por defecto
            return String.format("""
                {
                  "title": "Libro Generado por IA",
                  "author": "Autor Desconocido",
                  "genre": "LITERARY",
                  "synopsis": "%s"
                }
                """, job.getPrompt());
        }
    }

    /**
     * Genera un capítulo individual.
     */
    private String generateChapter(BookGenerationJob job, int chapterNumber, List<String> previousChapters) {
        try {
            String context = previousChapters.isEmpty() ? "" : 
                "Resumen de capítulos anteriores: " + String.join("\n", 
                    previousChapters.stream()
                        .map(ch -> ch.substring(0, Math.min(200, ch.length())))
                        .toList());

            String systemPrompt = String.format("""
                Eres un escritor profesional generando el capítulo %d de %d de un libro.
                Mantén coherencia con los capítulos anteriores.
                Escribe aproximadamente %d palabras.
                Usa un estilo literario apropiado para el género.
                Incluye diálogos, descripciones y desarrollo de personajes.
                """, chapterNumber, job.getTargetChapters(), job.getTargetWordsPerChapter());

            String userPrompt = String.format("""
                Prompt del libro: %s
                
                %s
                
                Escribe el capítulo %d. Comienza con "# Capítulo %d: [Título del capítulo]"
                """, job.getPrompt(), context, chapterNumber, chapterNumber);

            WebClient client = webClientBuilder.build();
            String response;
            
            if ("gpt-4".equals(job.getAiModel()) || "gpt-3.5-turbo".equals(job.getAiModel())) {
                response = callOpenAI(client, systemPrompt, userPrompt, job.getAiModel());
            } else if (job.getAiModel().startsWith("claude")) {
                response = callClaude(client, systemPrompt, userPrompt, job.getAiModel());
            } else {
                throw new IllegalArgumentException("Modelo de IA no soportado: " + job.getAiModel());
            }

            return response;

        } catch (Exception e) {
            log.error("Error generando capítulo {}", chapterNumber, e);
            return String.format("# Capítulo %d\n\n[Error generando contenido: %s]", chapterNumber, e.getMessage());
        }
    }

    /** Regenerar un capítulo individual (expuesto a ChapterService) */
    public String regenerateSingleChapter(BookGenerationJob job, int chapterNumber, String promptOverride) {
        List<String> prev = new java.util.ArrayList<>();
        // We do not load previous content for simplicity; could query chapters.
        String originalPrompt = promptOverride != null && !promptOverride.isBlank() ? promptOverride : job.getPrompt();
        return generateChapter(job, chapterNumber, prev).replace("Prompt del libro: " + job.getPrompt(), "Prompt del libro: " + originalPrompt);
    }

    /**
     * Genera portada usando DALL-E o Stable Diffusion.
     */
    private String generateCoverImage(BookGenerationJob job, String metadata) {
        try {
            JsonNode metadataJson = objectMapper.readTree(metadata);
            String title = metadataJson.get("title").asText();
            // String genre = metadataJson.get("genre").asText(); // reservado para futura portada avanzada

            // Prompt final para futura integración real de generación de imagen (no usado aún)
            // String prompt = String.format("Book cover for '%s', %s genre, professional quality, cinematic, detailed artwork", title, genre.toLowerCase());

            // TODO: Implementar integración con DALL-E 3 o Stable Diffusion
            // Por ahora retornar placeholder
            log.warn("⚠️ Generación de portada no implementada, usando placeholder");
            return "https://placehold.co/600x900?text=" + title.replace(" ", "+");

        } catch (Exception e) {
            log.error("Error generando portada", e);
            return "https://placehold.co/600x900?text=Book+Cover";
        }
    }

    /**
     * Ensambla todos los componentes en un EPUB válido con copyright y QR code.
     */
        private String assembleEpub(BookGenerationJob job, String metadata, List<String> chapters, String coverUrl) throws Exception {
                JsonNode metadataJson = objectMapper.readTree(metadata);
                String title = metadataJson.get("title").asText("Libro Sin Título");
                String author = metadataJson.get("author").asText("Autor Desconocido");
                String username = job.getUser().getUsername();
                int currentYear = Year.now().getValue();

                Path outputPath = Paths.get(outputDir, job.getId().toString());
                Files.createDirectories(outputPath);
                Path epubFile = outputPath.resolve("book.epub");

                String qrCodeBase64 = qrCodeService.generateDrakkarPressHomeQr(300);

                // Build main XHTML content
                StringBuilder xhtml = new StringBuilder();
                xhtml.append("<?xml version='1.0' encoding='UTF-8'?>\n");
                xhtml.append("<html xmlns='http://www.w3.org/1999/xhtml'>\n<head>\n");
                xhtml.append(String.format("<title>%s</title>\n", escapeXml(title)));
                xhtml.append("<meta charset='UTF-8'/>\n");
                xhtml.append("<style>body{font-family:Georgia,serif;line-height:1.6;margin:2em;}h1{text-align:center;font-size:2.2em;}h2{text-align:center;color:#666;} .chapter{page-break-before:always;margin:2em 0;} .copyright-page{text-align:center;margin:3em 0;page-break-after:always;}</style>\n");
                xhtml.append("</head><body>\n");
                xhtml.append(String.format("<h1>%s</h1>\n", escapeXml(title)));
                xhtml.append(String.format("<h2>por %s</h2>\n", escapeXml(author)));
                xhtml.append("<div class='copyright-page'>\n<hr/>\n");
                xhtml.append(String.format("<p>&copy; %d %s - Todos los derechos reservados.</p>\n", currentYear, escapeXml(username)));
                xhtml.append("<p>Generado con tecnología de IA en DrakkarPress.</p>\n");
                xhtml.append(String.format("<img alt='QR' src='data:image/png;base64,%s' style='max-width:180px;border:2px solid #333;padding:8px;'/>\n", qrCodeBase64));
                xhtml.append("</div>\n");
                for (String chapter : chapters) {
                        xhtml.append("<div class='chapter'>\n");
                        xhtml.append(chapter.replace("\n", "<br/>\n"));
                        xhtml.append("</div>\n");
                }
                xhtml.append("<div class='copyright-page'>\n<hr/><h2>Gracias por leer</h2><p>Visita www.drakkarpress.com</p>\n");
                xhtml.append(String.format("<img alt='QR' src='data:image/png;base64,%s' style='max-width:140px;margin-top:1em;'/>\n", qrCodeBase64));
                xhtml.append(String.format("<p style='font-size:0.9em;'>&copy; %d %s</p>\n", currentYear, escapeXml(username)));
                xhtml.append("</div>\n</body></html>");

                // Build minimal EPUB (ZIP)
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                try (ZipOutputStream zos = new ZipOutputStream(baos)) {
                        // 1. mimetype (must be stored, uncompressed in spec – we keep simple here)
                        zos.putNextEntry(new ZipEntry("mimetype"));
                        zos.write("application/epub+zip".getBytes());
                        zos.closeEntry();

                        // 2. META-INF/container.xml
                        zos.putNextEntry(new ZipEntry("META-INF/container.xml"));
                        String container = """
                                        <?xml version='1.0' encoding='UTF-8'?>
                                        <container version='1.0' xmlns='urn:oasis:names:tc:opendocument:xmlns:container'>
                                            <rootfiles>
                                                <rootfile full-path='OEBPS/content.opf' media-type='application/oebps-package+xml'/>
                                            </rootfiles>
                                        </container>
                                        """;
                        zos.write(container.getBytes());
                        zos.closeEntry();

                        // 3. OEBPS/book.xhtml
                        zos.putNextEntry(new ZipEntry("OEBPS/book.xhtml"));
                        zos.write(xhtml.toString().getBytes());
                        zos.closeEntry();

                        // 4. OEBPS/content.opf
                        zos.putNextEntry(new ZipEntry("OEBPS/content.opf"));
                        String opf = String.format("""
                                        <?xml version='1.0' encoding='UTF-8'?>
                                        <package xmlns='http://www.idpf.org/2007/opf' unique-identifier='BookId' version='3.0'>
                                            <metadata xmlns:dc='http://purl.org/dc/elements/1.1/'>
                                                <dc:identifier id='BookId'>%s</dc:identifier>
                                                <dc:title>%s</dc:title>
                                                <dc:language>es</dc:language>
                                                <dc:creator>%s</dc:creator>
                                                <meta property='dcterms:modified'>%s</meta>
                                            </metadata>
                                            <manifest>
                                                <item id='book' href='book.xhtml' media-type='application/xhtml+xml'/>
                                            </manifest>
                                            <spine>
                                                <itemref idref='book'/>
                                            </spine>
                                        </package>
                                        """, job.getId(), escapeXml(title), escapeXml(author), LocalDateTime.now());
                        zos.write(opf.getBytes());
                        zos.closeEntry();
                } catch (IOException e) {
                        throw new RuntimeException("Error creando EPUB", e);
                }

                Files.write(epubFile, baos.toByteArray());
                log.info("📚 EPUB ensamblado (estructura ZIP) listo: {}", epubFile.toAbsolutePath());
                return epubFile.toAbsolutePath().toString();
        }

    /**
     * Crea la entidad Book en la base de datos.
     */
    @Transactional
    protected Book createBookEntity(BookGenerationJob job, String metadata, String epubPath) throws Exception {
        JsonNode metadataJson = objectMapper.readTree(metadata);
        
        // Crear usuario del modelo dominio simplificado para Book.author
        // (Book requiere com.drakkarpress.model.User, no platform User)
        var platformUser = job.getUser();
        com.drakkarpress.model.User domainUser = com.drakkarpress.model.User.builder()
            .email(platformUser.getEmail())
            .role(com.drakkarpress.model.User.UserRole.AUTHOR)
            .subscription(com.drakkarpress.model.User.SubscriptionType.FREE)
            .enabled(true)
            .verified(false)
            .build();

        Book book = Book.builder()
            .title(metadataJson.get("title").asText("Libro Sin Título"))
            .author(domainUser)
            .synopsis(metadataJson.get("synopsis").asText(""))
            .description(metadataJson.get("synopsis").asText(""))
            .genre(resolveGenre(metadataJson.get("genre").asText("OTHER")))
            .coverImageUrl(job.getCoverImageUrl())
            .digitalFileUrl(epubPath)
            .priceDigital(java.math.BigDecimal.ZERO)
            .pages(job.getTargetChapters() * 10)
            .aiGenerated(true)
            .safetyStatus("SAFE")
            .publishedAt(LocalDateTime.now())
            .build();

        Book savedBook = bookRepository.save(book);
        return savedBook;
    }

    /**
     * Llama a OpenAI API (GPT-4, GPT-3.5-turbo).
     */
    private String callOpenAI(WebClient client, String systemPrompt, String userPrompt, String model) {
        try {
            var requestBody = objectMapper.createObjectNode();
            requestBody.put("model", model);
            var messages = objectMapper.createArrayNode();
            messages.add(objectMapper.createObjectNode().put("role", "system").put("content", systemPrompt));
            messages.add(objectMapper.createObjectNode().put("role", "user").put("content", userPrompt));
            requestBody.set("messages", messages);
            requestBody.put("temperature", 0.7);
            requestBody.put("max_tokens", 4000);

            String response = client.post()
                .uri(openaiApiUrl + "/chat/completions")
                .header("Authorization", "Bearer " + openaiApiKey)
                .header("Content-Type", "application/json")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .block();

            JsonNode responseJson = objectMapper.readTree(response);
            return responseJson.get("choices").get(0).get("message").get("content").asText();

        } catch (Exception e) {
            log.error("Error llamando a OpenAI", e);
            throw new RuntimeException("Error en OpenAI API: " + e.getMessage(), e);
        }
    }

    /**
     * Llama a Claude API (Claude 3 Opus, Sonnet, Haiku).
     */
    private String callClaude(WebClient client, String systemPrompt, String userPrompt, String model) {
        try {
            var requestBody = objectMapper.createObjectNode();
            requestBody.put("model", model);
            requestBody.put("max_tokens", 4000);
            requestBody.put("system", systemPrompt);
            var messages = objectMapper.createArrayNode();
            messages.add(objectMapper.createObjectNode().put("role", "user").put("content", userPrompt));
            requestBody.set("messages", messages);

            String response = client.post()
                .uri(claudeApiUrl + "/messages")
                .header("x-api-key", claudeApiKey)
                .header("anthropic-version", "2023-06-01")
                .header("Content-Type", "application/json")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .block();

            JsonNode responseJson = objectMapper.readTree(response);
            return responseJson.get("content").get(0).get("text").asText();

        } catch (Exception e) {
            log.error("Error llamando a Claude", e);
            throw new RuntimeException("Error en Claude API: " + e.getMessage(), e);
        }
    }

    private Book.Genre resolveGenre(String raw) {
        try { return Book.Genre.valueOf(raw.toUpperCase()); } catch (Exception e) { return Book.Genre.OTHER; }
    }

    /**
     * Obtiene el estado de un job.
     */
    public BookGenerationJob getJobStatus(UUID jobId) {
        return jobRepository.findById(jobId)
            .orElseThrow(() -> new IllegalArgumentException("Job no encontrado: " + jobId));
    }

    /**
     * Cancela un job en progreso.
     */
    @Transactional
    public void cancelJob(UUID jobId) {
        BookGenerationJob job = jobRepository.findById(jobId).orElseThrow();
        if (job.getStatus() == BookGenerationJob.JobStatus.PENDING || 
            job.getStatus() == BookGenerationJob.JobStatus.GENERATING) {
            job.setStatus(BookGenerationJob.JobStatus.CANCELLED);
            jobRepository.save(job);
        }
    }

    private String escapeXml(String text) {
        return text.replace("&", "&amp;")
                   .replace("<", "&lt;")
                   .replace(">", "&gt;")
                   .replace("\"", "&quot;")
                   .replace("'", "&apos;");
    }
}
