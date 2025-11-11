# 🚀 DrakkarPress - Guía de Inicio Rápido

**Para desarrolladores que continúan el proyecto**

---

## 📁 Archivos Creados

### ✅ Documentación (100% completo)
```
DrakkarPress.com/
├── ARQUITECTURA_ECOSISTEMA_COMPLETO.md  # 📘 Arquitectura completa
├── ROADMAP_COMPLETO.md                  # 📋 Plan de trabajo detallado
└── RESUMEN_EJECUTIVO_COMPLETO.md        # 🎯 Presentación ejecutiva
```

### ✅ Base de Datos (100% completo)
```
database/
├── schema.sql              # 🗄️ 17 tablas + triggers + views
└── seeds/
    └── init-data.sql       # 🌱 24 runas + 8 badges + users de prueba
```

### 🚧 Backend Java (20% completo)
```
backend/src/main/java/com/drakkarpress/platform/
└── model/
    ├── Rune.java          # ✅ Entity de runas
    └── Badge.java         # ✅ Entity de badges
```

---

## ⚡ Iniciar Base de Datos (PRIMER PASO)

### 1. Levantar PostgreSQL con Docker

```powershell
# Navegar al directorio backend
cd c:\Users\SuperUsuario\DrakkarPress.com\backend

# Iniciar base de datos con Docker Compose
docker-compose up -d postgres
```

### 2. Crear el esquema

```powershell
# Conectar a PostgreSQL
docker exec -it drakkarpress-postgres psql -U drakkarpress -d drakkarpress_db

# Dentro de psql, ejecutar:
\i /path/to/database/schema.sql
\i /path/to/database/seeds/init-data.sql
```

O usando psql local:
```powershell
psql -h localhost -p 5432 -U drakkarpress -d drakkarpress_db -f database/schema.sql
psql -h localhost -p 5432 -U drakkarpress -d drakkarpress_db -f database/seeds/init-data.sql
```

### 3. Verificar instalación

```sql
-- Dentro de psql
SELECT COUNT(*) FROM runes;         -- Debe retornar 24
SELECT COUNT(*) FROM badges;        -- Debe retornar 8
SELECT COUNT(*) FROM users;         -- Debe retornar 3 (admin + 2 test)
SELECT * FROM v_user_full_profile;  -- Ver perfiles completos
```

---

## 🔨 Continuar el Backend Java

### Paso 1: Completar Entities (Modelos)

**Archivos a crear:**
```
backend/src/main/java/com/drakkarpress/platform/model/

Ya creados:
✅ Rune.java
✅ Badge.java

Falta crear (15 archivos):
❌ User.java                     # Usuario base (extender existente)
❌ Membership.java               # Membresías
❌ UserRune.java                 # Relación usuario-runa
❌ UserBadge.java                # Relación usuario-badge
❌ UserRole.java                 # Roles multi-rol
❌ RoleVerification.java         # Verificaciones
❌ AiUsageLimit.java             # Límites de IA por plan
❌ AiUsageTracking.java          # Tracking de uso
❌ AiUsageMonthlySummary.java    # Resumen mensual
❌ Connection.java               # Conexiones sociales
❌ UserActivityFeed.java         # Feed de actividad
❌ Message.java                  # Mensajería
❌ PaymentTransaction.java       # Transacciones
❌ AdminAuditLog.java            # Log de auditoría
❌ SessionToken.java             # Tokens de sesión
```

**Template de Entity:**
```java
package com.drakkarpress.platform.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "nombre_tabla")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NombreEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    // Campos...
    
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    
    // Relaciones con @OneToMany, @ManyToOne, etc.
}
```

### Paso 2: Crear Repositorios (JPA)

**Archivos a crear:**
```
backend/src/main/java/com/drakkarpress/platform/repository/

❌ RuneRepository.java
❌ BadgeRepository.java
❌ UserRepository.java
❌ MembershipRepository.java
❌ UserRoleRepository.java
❌ ConnectionRepository.java
❌ MessageRepository.java
// etc.
```

**Template de Repository:**
```java
package com.drakkarpress.platform.repository;

import com.drakkarpress.platform.model.NombreEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface NombreRepository extends JpaRepository<NombreEntity, UUID> {
    
    // Métodos custom
    Optional<NombreEntity> findByEmail(String email);
    
    @Query("SELECT e FROM NombreEntity e WHERE e.campo = :valor")
    List<NombreEntity> findByCustomQuery(String valor);
}
```

### Paso 3: Crear DTOs

**Archivos a crear:**
```
backend/src/main/java/com/drakkarpress/platform/dto/

❌ UserRegistrationDTO.java
❌ UserProfileDTO.java
❌ MembershipDTO.java
❌ RuneSelectionDTO.java
❌ BadgeDTO.java
// etc.
```

**Template de DTO:**
```java
package com.drakkarpress.platform.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NombreDTO {
    
    @NotBlank(message = "Campo requerido")
    private String campo1;
    
    @Email(message = "Email inválido")
    private String email;
    
    @Min(value = 1, message = "Debe ser mayor a 0")
    private Integer campo2;
}
```

### Paso 4: Crear Services

**Archivos a crear:**
```
backend/src/main/java/com/drakkarpress/platform/service/

❌ AuthService.java              # Autenticación JWT
❌ UserService.java              # Gestión usuarios
❌ MembershipService.java        # Lógica de membresías
❌ RuneService.java              # Selección de runas
❌ BadgeService.java             # Asignación de badges
❌ RoleService.java              # Gestión de roles
❌ VerificationService.java      # Verificación documentos
❌ AiLimitService.java           # Control límites IA
❌ ConnectionService.java        # Red social
❌ MessageService.java           # Mensajería
❌ AdminService.java             # Panel admin
❌ PaymentService.java           # Pagos (Stripe)
```

**Template de Service:**
```java
package com.drakkarpress.platform.service;

import com.drakkarpress.platform.model.*;
import com.drakkarpress.platform.repository.*;
import com.drakkarpress.platform.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class NombreService {
    
    private final NombreRepository nombreRepository;
    // Otros repositories...
    
    public NombreDTO create(NombreDTO dto) {
        // Lógica...
    }
    
    public NombreDTO getById(UUID id) {
        // Lógica...
    }
}
```

### Paso 5: Crear Controllers (REST API)

**Archivos a crear:**
```
backend/src/main/java/com/drakkarpress/platform/controller/

❌ AuthController.java           # /api/auth/*
❌ UserController.java           # /api/users/*
❌ MembershipController.java     # /api/memberships/*
❌ RuneController.java           # /api/runes/*
❌ ProfileController.java        # /api/profiles/*
❌ AdminController.java          # /api/admin/*
// etc.
```

**Template de Controller:**
```java
package com.drakkarpress.platform.controller;

import com.drakkarpress.platform.service.*;
import com.drakkarpress.platform.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("/api/nombre")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class NombreController {
    
    private final NombreService nombreService;
    
    @PostMapping
    public ResponseEntity<NombreDTO> create(@Valid @RequestBody NombreDTO dto) {
        return ResponseEntity.ok(nombreService.create(dto));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<NombreDTO> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(nombreService.getById(id));
    }
}
```

---

## 🔐 Configurar Seguridad (JWT)

### Paso 1: Crear JwtTokenProvider

**Archivo:** `backend/src/main/java/com/drakkarpress/platform/security/JwtTokenProvider.java`

```java
@Component
public class JwtTokenProvider {
    
    @Value("${jwt.secret}")
    private String jwtSecret;
    
    @Value("${jwt.expiration}")
    private long jwtExpiration;
    
    public String generateToken(Authentication authentication) {
        // Genera JWT
    }
    
    public boolean validateToken(String token) {
        // Valida JWT
    }
    
    public UUID getUserIdFromToken(String token) {
        // Extrae user ID del token
    }
}
```

### Paso 2: Crear JwtAuthenticationFilter

**Archivo:** `backend/src/main/java/com/drakkarpress/platform/security/JwtAuthenticationFilter.java`

```java
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) {
        // Valida token en cada request
    }
}
```

### Paso 3: Configurar Spring Security

**Archivo:** `backend/src/main/java/com/drakkarpress/platform/config/SecurityConfig.java`

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {
        http
            .csrf().disable()
            .cors()
            .and()
            .authorizeHttpRequests()
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            .and()
            .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        
        return http.build();
    }
}
```

---

## 📝 Crear application.properties

**Archivo:** `backend/src/main/resources/application.properties`

```properties
# Server
server.port=8080

# Database
spring.datasource.url=jdbc:postgresql://localhost:5432/drakkarpress_db
spring.datasource.username=drakkarpress
spring.datasource.password=drakkarpress_password
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

# JWT
jwt.secret=TU_SECRET_KEY_AQUI_CAMBIAR_EN_PRODUCCION
jwt.expiration=86400000
jwt.refresh-expiration=604800000

# Stripe
stripe.api.key=sk_test_XXXXXX
stripe.webhook.secret=whsec_XXXXXX

# File Upload
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB

# Email (SendGrid)
spring.mail.host=smtp.sendgrid.net
spring.mail.port=587
spring.mail.username=apikey
spring.mail.password=TU_SENDGRID_API_KEY

# Logging
logging.level.com.drakkarpress=DEBUG
```

---

## 🧪 Probar la API

### 1. Iniciar el backend

```powershell
cd backend
./mvnw spring-boot:run
```

### 2. Probar endpoints con curl/Postman

**Registro:**
```bash
POST http://localhost:8080/api/auth/register
Content-Type: application/json

{
  "email": "test@example.com",
  "username": "testuser",
  "password": "Test123!@#",
  "firstName": "Test",
  "lastName": "User"
}
```

**Login:**
```bash
POST http://localhost:8080/api/auth/login
Content-Type: application/json

{
  "email": "test@example.com",
  "password": "Test123!@#"
}
```

**Obtener runas:**
```bash
GET http://localhost:8080/api/runes
Authorization: Bearer YOUR_JWT_TOKEN
```

---

## 📚 Recursos de Referencia

### Documentación Completa
1. `ARQUITECTURA_ECOSISTEMA_COMPLETO.md` - Arquitectura detallada
2. `ROADMAP_COMPLETO.md` - Plan de trabajo
3. `RESUMEN_EJECUTIVO_COMPLETO.md` - Resumen ejecutivo

### Base de Datos
- `database/schema.sql` - Esquema completo
- `database/seeds/init-data.sql` - Datos iniciales

### Ejemplos de Código
- `backend/src/main/java/com/drakkarpress/platform/model/Rune.java`
- `backend/src/main/java/com/drakkarpress/platform/model/Badge.java`

---

## 🎯 Próximos Pasos Inmediatos

### Semana 1
1. ✅ Completar todas las Entities (15 archivos)
2. ✅ Crear todos los Repositories (15 archivos)
3. ✅ Probar conexión a base de datos

### Semana 2
4. ✅ Implementar AuthService + UserService
5. ✅ Configurar Spring Security + JWT
6. ✅ Crear AuthController + UserController
7. ✅ Probar registro y login

### Semana 3
8. ✅ Implementar MembershipService
9. ✅ Lógica de fases (auto-asignación de badges)
10. ✅ Integración básica con Stripe

---

## 💡 Tips Importantes

### Usar Lombok
Todas las entities usan `@Data`, `@Builder`, `@NoArgsConstructor`, `@AllArgsConstructor` para reducir boilerplate.

### UUIDs en lugar de Long
Todos los IDs son UUID para mejor seguridad.

### Soft Delete
Usar `deleted_at` en lugar de eliminar físicamente.

### Timestamps Automáticos
`@CreationTimestamp` y `@UpdateTimestamp` de Hibernate.

### Transacciones
Usar `@Transactional` en services.

### Validaciones
Usar `@Valid` en controllers y validaciones de Jakarta en DTOs.

---

## 🆘 Troubleshooting

### Error: "Table doesn't exist"
- Ejecutar `schema.sql` y `init-data.sql`

### Error: "Connection refused"
- Verificar que PostgreSQL esté corriendo: `docker ps`

### Error: "JWT expired"
- Los tokens expiran en 24h, hacer login nuevamente

### Error: "Access denied"
- Verificar que el token tenga el role correcto

---

## 📞 Soporte

**Repositorio:** github.com/imageGeneratorZZ/DrakkarPress  
**Branch:** main  
**Documentación:** `/docs` folder

---

**¡Éxito con el desarrollo!** 🚀⚔️📚
