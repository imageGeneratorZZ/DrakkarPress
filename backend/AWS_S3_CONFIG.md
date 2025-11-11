# ☁️ Configuración de AWS S3 - DrakkarPress

## 🎯 Arquitectura de Storage

DrakkarPress usa 3 buckets separados para mejor organización y seguridad:

| Bucket | Propósito | Acceso Público | Tamaño Estimado |
|--------|-----------|----------------|-----------------|
| **drakkarpress-books** | PDFs generados, ePubs | ❌ Privado | ~100 MB por libro |
| **drakkarpress-covers** | Portadas de libros | ✅ Público (CDN) | ~500 KB por imagen |
| **drakkarpress-avatars** | Avatares de usuario | ✅ Público (CDN) | ~200 KB por avatar |

---

## 🔧 Configuración Inicial AWS

### 1. Crear Cuenta AWS

1. Ir a [AWS Console](https://aws.amazon.com/)
2. Sign Up → Free Tier (12 meses gratis)
   - **5 GB** de almacenamiento S3
   - **20,000** GET requests
   - **2,000** PUT requests
3. Verificar tarjeta de crédito

### 2. Crear Usuario IAM

**Importante**: Nunca usar root account para aplicaciones.

1. AWS Console → IAM → Users → Create user
2. **User name**: `drakkarpress-app`
3. **Access type**: 
   - ✅ Programmatic access (genera Access Key)
   - ❌ AWS Management Console access
4. **Permissions**: Attach policies directly
   - ✅ `AmazonS3FullAccess` (temporal, luego restringir)
5. **Tags** (opcional):
   - Key: `Application`, Value: `DrakkarPress`
   - Key: `Environment`, Value: `Production`
6. Review → Create user
7. **IMPORTANTE**: Descargar Access Key ID y Secret Access Key
   ```
   Access Key ID: AKIAIOSFODNN7EXAMPLE
   Secret Access Key: wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY
   ```

---

## 📦 Crear Buckets S3

### Bucket 1: Books (Privado)

```bash
# AWS CLI (si lo tienes instalado)
aws s3 mb s3://drakkarpress-books --region us-east-1

# O vía Console:
# S3 → Create bucket
# Bucket name: drakkarpress-books
# Region: US East (N. Virginia) us-east-1
# Block all public access: ✅ ON
# Bucket Versioning: ❌ OFF (opcional)
# Encryption: Server-side encryption (SSE-S3)
```

**Configuración**:
- ✅ Block all public access
- ✅ Server-side encryption (AES-256)
- ✅ Versioning (opcional, para recuperar archivos borrados)
- ✅ Lifecycle policy: eliminar después de X días (opcional)

**Estructura de carpetas**:
```
drakkarpress-books/
├── pdf/
│   ├── user-123/
│   │   ├── libro-abc-def-ghi.pdf
│   │   └── libro-xyz-123-456.pdf
│   └── user-456/
│       └── libro-789-012-345.pdf
├── epub/
│   └── user-123/
│       └── libro-abc-def-ghi.epub
└── mobi/
    └── user-123/
        └── libro-abc-def-ghi.mobi
```

### Bucket 2: Covers (Público con CDN)

```bash
aws s3 mb s3://drakkarpress-covers --region us-east-1
```

**Configuración**:
- ❌ Block all public access → **OFF**
- ✅ Public read access para objetos
- ✅ CloudFront CDN (opcional pero recomendado)
- ✅ Compression (gzip)

**Bucket Policy** (para acceso público de lectura):

```json
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Sid": "PublicReadGetObject",
      "Effect": "Allow",
      "Principal": "*",
      "Action": "s3:GetObject",
      "Resource": "arn:aws:s3:::drakkarpress-covers/*"
    }
  ]
}
```

**CORS Configuration**:

```json
[
  {
    "AllowedHeaders": ["*"],
    "AllowedMethods": ["GET", "HEAD"],
    "AllowedOrigins": [
      "https://www.drakkarpress.com",
      "https://drakkarpress.com"
    ],
    "ExposeHeaders": ["ETag"],
    "MaxAgeSeconds": 3600
  }
]
```

**Estructura de carpetas**:
```
drakkarpress-covers/
├── books/
│   ├── default-covers/
│   │   ├── scifi.jpg
│   │   ├── fantasy.jpg
│   │   └── mystery.jpg
│   └── generated/
│       ├── abc-def-ghi.jpg
│       └── xyz-123-456.jpg
└── thumbnails/
    ├── abc-def-ghi-thumb.jpg
    └── xyz-123-456-thumb.jpg
```

### Bucket 3: Avatars (Público)

```bash
aws s3 mb s3://drakkarpress-avatars --region us-east-1
```

**Configuración**: Igual que `drakkarpress-covers`

**Bucket Policy**: Mismo JSON que covers (cambiar nombre del bucket)

**Estructura de carpetas**:
```
drakkarpress-avatars/
├── users/
│   ├── user-123.jpg
│   ├── user-456.png
│   └── user-789.webp
├── default/
│   ├── default-1.svg
│   ├── default-2.svg
│   └── default-3.svg
└── thumbnails/
    ├── user-123-thumb.jpg
    └── user-456-thumb.jpg
```

---

## 🔐 Configurar Permisos IAM (Restrictivo)

**Política personalizada** (reemplazar `AmazonS3FullAccess`):

```json
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Sid": "ListBuckets",
      "Effect": "Allow",
      "Action": "s3:ListBucket",
      "Resource": [
        "arn:aws:s3:::drakkarpress-books",
        "arn:aws:s3:::drakkarpress-covers",
        "arn:aws:s3:::drakkarpress-avatars"
      ]
    },
    {
      "Sid": "ReadWriteObjects",
      "Effect": "Allow",
      "Action": [
        "s3:PutObject",
        "s3:GetObject",
        "s3:DeleteObject",
        "s3:PutObjectAcl"
      ],
      "Resource": [
        "arn:aws:s3:::drakkarpress-books/*",
        "arn:aws:s3:::drakkarpress-covers/*",
        "arn:aws:s3:::drakkarpress-avatars/*"
      ]
    }
  ]
}
```

**Aplicar política**:
1. IAM → Users → `drakkarpress-app`
2. Permissions → Remove `AmazonS3FullAccess`
3. Add inline policy → JSON → Pegar el JSON de arriba
4. Review → Name: `DrakkarPressS3Policy` → Create policy

---

## 💻 Implementación Backend (Spring Boot)

### 1. Dependencias Maven

```xml
<!-- AWS SDK for S3 -->
<dependency>
    <groupId>software.amazon.awssdk</groupId>
    <artifactId>s3</artifactId>
    <version>2.21.0</version>
</dependency>

<!-- Optional: Para presigned URLs -->
<dependency>
    <groupId>software.amazon.awssdk</groupId>
    <artifactId>s3-transfer-manager</artifactId>
    <version>2.21.0</version>
</dependency>
```

### 2. Configuración

```java
@Configuration
public class S3Config {
    
    @Value("${aws.access.key.id}")
    private String accessKeyId;
    
    @Value("${aws.secret.access.key}")
    private String secretAccessKey;
    
    @Value("${aws.region}")
    private String region;
    
    @Bean
    public S3Client s3Client() {
        AwsBasicCredentials credentials = AwsBasicCredentials.create(
            accessKeyId, 
            secretAccessKey
        );
        
        return S3Client.builder()
            .region(Region.of(region))
            .credentialsProvider(StaticCredentialsProvider.create(credentials))
            .build();
    }
}
```

### 3. Service de S3

```java
@Service
public class S3Service {
    
    @Autowired
    private S3Client s3Client;
    
    @Value("${aws.s3.bucket.books}")
    private String bucketBooks;
    
    @Value("${aws.s3.bucket.covers}")
    private String bucketCovers;
    
    @Value("${aws.s3.bucket.avatars}")
    private String bucketAvatars;
    
    // ===== UPLOAD FILE =====
    public String uploadFile(String bucketName, String key, byte[] fileContent, String contentType) {
        try {
            PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType(contentType)
                .build();
            
            s3Client.putObject(request, RequestBody.fromBytes(fileContent));
            
            // Retornar URL
            if (bucketName.equals(bucketBooks)) {
                // Privado, retornar presigned URL
                return generatePresignedUrl(bucketName, key, 7); // 7 días
            } else {
                // Público, retornar URL directa
                return String.format("https://%s.s3.amazonaws.com/%s", bucketName, key);
            }
        } catch (S3Exception e) {
            throw new RuntimeException("Error uploading file to S3: " + e.getMessage(), e);
        }
    }
    
    // ===== UPLOAD BOOK PDF =====
    public String uploadBookPdf(Long userId, String bookId, byte[] pdfContent) {
        String key = String.format("pdf/user-%d/%s.pdf", userId, bookId);
        return uploadFile(bucketBooks, key, pdfContent, "application/pdf");
    }
    
    // ===== UPLOAD COVER =====
    public String uploadCover(String bookId, byte[] imageContent, String extension) {
        String key = String.format("books/generated/%s.%s", bookId, extension);
        String contentType = "image/" + extension;
        return uploadFile(bucketCovers, key, imageContent, contentType);
    }
    
    // ===== UPLOAD AVATAR =====
    public String uploadAvatar(Long userId, byte[] imageContent, String extension) {
        String key = String.format("users/user-%d.%s", userId, extension);
        String contentType = "image/" + extension;
        return uploadFile(bucketAvatars, key, imageContent, contentType);
    }
    
    // ===== DOWNLOAD FILE =====
    public byte[] downloadFile(String bucketName, String key) {
        try {
            GetObjectRequest request = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();
            
            ResponseBytes<GetObjectResponse> response = s3Client.getObjectAsBytes(request);
            return response.asByteArray();
        } catch (S3Exception e) {
            throw new RuntimeException("Error downloading file from S3: " + e.getMessage(), e);
        }
    }
    
    // ===== DELETE FILE =====
    public void deleteFile(String bucketName, String key) {
        try {
            DeleteObjectRequest request = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();
            
            s3Client.deleteObject(request);
        } catch (S3Exception e) {
            throw new RuntimeException("Error deleting file from S3: " + e.getMessage(), e);
        }
    }
    
    // ===== PRESIGNED URL (para downloads privados) =====
    public String generatePresignedUrl(String bucketName, String key, int expirationDays) {
        try {
            S3Presigner presigner = S3Presigner.create();
            
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();
            
            GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofDays(expirationDays))
                .getObjectRequest(getObjectRequest)
                .build();
            
            PresignedGetObjectRequest presignedRequest = presigner.presignGetObject(presignRequest);
            
            return presignedRequest.url().toString();
        } catch (S3Exception e) {
            throw new RuntimeException("Error generating presigned URL: " + e.getMessage(), e);
        }
    }
    
    // ===== LIST FILES BY USER =====
    public List<String> listUserBooks(Long userId) {
        try {
            String prefix = String.format("pdf/user-%d/", userId);
            
            ListObjectsV2Request request = ListObjectsV2Request.builder()
                .bucket(bucketBooks)
                .prefix(prefix)
                .build();
            
            ListObjectsV2Response response = s3Client.listObjectsV2(request);
            
            return response.contents().stream()
                .map(S3Object::key)
                .collect(Collectors.toList());
        } catch (S3Exception e) {
            throw new RuntimeException("Error listing user books: " + e.getMessage(), e);
        }
    }
    
    // ===== CHECK IF FILE EXISTS =====
    public boolean fileExists(String bucketName, String key) {
        try {
            HeadObjectRequest request = HeadObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();
            
            s3Client.headObject(request);
            return true;
        } catch (NoSuchKeyException e) {
            return false;
        } catch (S3Exception e) {
            throw new RuntimeException("Error checking file existence: " + e.getMessage(), e);
        }
    }
}
```

### 4. Controller de Upload

```java
@RestController
@RequestMapping("/api/upload")
public class UploadController {
    
    @Autowired
    private S3Service s3Service;
    
    @PostMapping("/avatar")
    public ResponseEntity<Map<String, String>> uploadAvatar(
            @RequestParam("file") MultipartFile file,
            Authentication authentication) throws IOException {
        
        User user = (User) authentication.getPrincipal();
        
        // Validaciones
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "File is empty"));
        }
        
        if (file.getSize() > 5 * 1024 * 1024) { // 5 MB
            return ResponseEntity.badRequest().body(Map.of("error", "File too large (max 5MB)"));
        }
        
        String contentType = file.getContentType();
        if (!contentType.startsWith("image/")) {
            return ResponseEntity.badRequest().body(Map.of("error", "File must be an image"));
        }
        
        // Extension
        String extension = file.getOriginalFilename()
            .substring(file.getOriginalFilename().lastIndexOf(".") + 1);
        
        // Upload
        String url = s3Service.uploadAvatar(
            user.getId(), 
            file.getBytes(), 
            extension
        );
        
        return ResponseEntity.ok(Map.of(
            "url", url,
            "message", "Avatar uploaded successfully"
        ));
    }
    
    @PostMapping("/cover")
    public ResponseEntity<Map<String, String>> uploadCover(
            @RequestParam("file") MultipartFile file,
            @RequestParam("bookId") String bookId,
            Authentication authentication) throws IOException {
        
        // Similar al de avatar
        String extension = file.getOriginalFilename()
            .substring(file.getOriginalFilename().lastIndexOf(".") + 1);
        
        String url = s3Service.uploadCover(bookId, file.getBytes(), extension);
        
        return ResponseEntity.ok(Map.of(
            "url", url,
            "bookId", bookId
        ));
    }
}
```

### 5. Variables de Entorno

```bash
# AWS Credentials
AWS_ACCESS_KEY_ID=AKIAIOSFODNN7EXAMPLE
AWS_SECRET_ACCESS_KEY=wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY
AWS_REGION=us-east-1

# S3 Buckets
AWS_S3_BUCKET_BOOKS=drakkarpress-books
AWS_S3_BUCKET_COVERS=drakkarpress-covers
AWS_S3_BUCKET_AVATARS=drakkarpress-avatars

# CDN URLs (si usas CloudFront)
AWS_CDN_COVERS_URL=https://d123abc456def.cloudfront.net
AWS_CDN_AVATARS_URL=https://d456def789ghi.cloudfront.net
```

---

## 🌐 Configurar CloudFront CDN (Opcional pero recomendado)

### ¿Por qué CloudFront?

- ✅ **Mejor rendimiento**: Cache global en +400 ubicaciones
- ✅ **Menor costo**: Menos requests directos a S3
- ✅ **HTTPS gratis**: Certificado SSL incluido
- ✅ **Compresión automática**: gzip/brotli

### Configuración

1. AWS Console → CloudFront → Create distribution
2. **Origin domain**: `drakkarpress-covers.s3.amazonaws.com`
3. **Origin access**: Origin access control settings (recommended)
   - Create control setting → Name: `drakkarpress-covers-oac`
4. **Viewer protocol policy**: Redirect HTTP to HTTPS
5. **Allowed HTTP methods**: GET, HEAD, OPTIONS
6. **Compress objects automatically**: ✅ Yes
7. **Price class**: Use all edge locations (best performance)
8. Create distribution
9. **IMPORTANTE**: Copiar el **Distribution domain name**:
   ```
   d123abc456def.cloudfront.net
   ```

10. Actualizar bucket policy de S3 para permitir CloudFront:

```json
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Sid": "AllowCloudFrontServicePrincipal",
      "Effect": "Allow",
      "Principal": {
        "Service": "cloudfront.amazonaws.com"
      },
      "Action": "s3:GetObject",
      "Resource": "arn:aws:s3:::drakkarpress-covers/*",
      "Condition": {
        "StringEquals": {
          "AWS:SourceArn": "arn:aws:cloudfront::TU_ACCOUNT_ID:distribution/TU_DISTRIBUTION_ID"
        }
      }
    }
  ]
}
```

11. Repetir para `drakkarpress-avatars`

---

## 🧪 Testing

### Script de Test (PowerShell)

```powershell
# test-s3-upload.ps1

$apiUrl = "http://localhost:8080/api/upload/avatar"
$token = "TU_JWT_TOKEN"
$imagePath = "C:\Users\...\avatar.jpg"

$headers = @{
    "Authorization" = "Bearer $token"
}

$form = @{
    file = Get-Item -Path $imagePath
}

$response = Invoke-RestMethod -Uri $apiUrl -Method Post -Headers $headers -Form $form
Write-Host "✅ Upload exitoso: $($response.url)" -ForegroundColor Green
```

### curl (Linux/Mac)

```bash
curl -X POST http://localhost:8080/api/upload/avatar \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -F "file=@/path/to/avatar.jpg"
```

---

## 💰 Estimación de Costos

### AWS S3 Pricing (us-east-1)

**Storage**:
- $0.023 por GB/mes (primeros 50 TB)

**Requests**:
- PUT/POST: $0.005 por 1,000 requests
- GET: $0.0004 por 1,000 requests

**Data Transfer**:
- IN (upload): Gratis
- OUT (download): $0.09 por GB (después de 100 GB gratis/mes)

### Ejemplo: 1,000 usuarios activos

```
Storage:
- 1000 usuarios × 10 libros × 100 MB = 1 TB = $23/mes
- 1000 usuarios × 1 cover × 500 KB = 500 MB = $0.01/mes
- 1000 usuarios × 1 avatar × 200 KB = 200 MB = $0.01/mes
Total storage: ~$23/mes

Requests:
- 10,000 uploads/mes = $0.05
- 100,000 downloads/mes = $0.04
Total requests: ~$0.09/mes

Data Transfer (con CloudFront):
- 10 GB/mes = Gratis (primeros 100 GB)

TOTAL ESTIMADO: ~$25/mes
```

### CloudFront Pricing

- $0.085 por GB (primeros 10 TB)
- 10,000,000 requests: $10/mes

**TOTAL CON CDN**: ~$35/mes

---

## 🔐 Checklist de Seguridad

- [ ] Crear usuario IAM separado (no usar root)
- [ ] Política IAM restrictiva (solo buckets necesarios)
- [ ] Habilitar MFA para cuenta AWS
- [ ] Rotar Access Keys cada 90 días
- [ ] Habilitar CloudTrail logging
- [ ] Encriptar buckets con SSE-S3
- [ ] Block public access en bucket de books
- [ ] Configurar CORS correctamente
- [ ] Validar file types en backend
- [ ] Limitar tamaño de uploads (5 MB avatars, 200 MB books)
- [ ] Usar presigned URLs para downloads privados
- [ ] Configurar lifecycle policies (eliminar archivos antiguos)
- [ ] Monitorear costos con AWS Budgets

---

## 📚 Recursos Adicionales

- [AWS S3 Console](https://s3.console.aws.amazon.com/)
- [AWS SDK for Java](https://docs.aws.amazon.com/sdk-for-java/)
- [S3 Best Practices](https://docs.aws.amazon.com/AmazonS3/latest/userguide/BestPractices.html)
- [CloudFront Setup](https://docs.aws.amazon.com/AmazonCloudFront/latest/DeveloperGuide/)

---

**Creado**: 2025-11-11  
**Última actualización**: 2025-11-11  
**AWS SDK Version**: 2.21.0  
**Region**: us-east-1
