# Sistema de Autenticación y SSO (Single Sign-On) - DrakkarPress

## Visión General

DrakkarPress implementa un **sistema de autenticación centralizado** que permite a los usuarios registrarse e iniciar sesión usando múltiples métodos, con soporte completo para **OAuth 2.0** y **OpenID Connect**.

---

## 🔐 Métodos de Autenticación Disponibles

### 1. **Email/Password (Tradicional)**
- Registro con validación de email
- Contraseña segura (mínimo 8 caracteres, mayúsculas, números, símbolos)
- Hash con bcrypt (cost factor: 12)
- Recuperación de contraseña por email

### 2. **OAuth Providers (Social Login)**

```
┌─────────────────────────────────────────────────┐
│         OPCIONES DE INICIO DE SESIÓN            │
├─────────────────────────────────────────────────┤
│                                                 │
│  📧  Email y Contraseña                        │
│  ────────────────────────                      │
│  [          Email          ]                   │
│  [       Contraseña        ]                   │
│  [  Iniciar Sesión  ]                         │
│                                                 │
│  ──────── O continuar con ────────             │
│                                                 │
│  🔴 [  Google  ]    🔵 [  Facebook  ]         │
│  🟣 [  GitHub  ]    💼 [  LinkedIn  ]         │
│  🍎 [  Apple   ]    🐦 [  Twitter/X  ]        │
│                                                 │
│  ¿No tienes cuenta? [Regístrate]               │
│                                                 │
└─────────────────────────────────────────────────┘
```

#### Proveedores OAuth Soportados:

| Provider | Uso Principal | Datos Obtenidos |
|----------|---------------|-----------------|
| **Google** | Universal | Email, nombre, foto, idioma |
| **Facebook** | Redes sociales | Email, nombre, foto, amigos (opcional) |
| **GitHub** | Desarrolladores/Tech | Email, nombre, repos públicos |
| **LinkedIn** | Profesionales | Email, nombre, empresa, puesto |
| **Apple** | iOS/macOS users | Email (puede ser privado), nombre |
| **Twitter/X** | Escritores/Influencers | Email, nombre, handle, followers |

---

## 🏗️ Arquitectura del Sistema

```
┌─────────────────────────────────────────────────────────────┐
│                    FRONTEND (Múltiples Portales)             │
│  www.drakkarpress.com | escritores | afiliados | imprentas  │
└────────────────────────┬────────────────────────────────────┘
                         │
                         ▼
            ┌────────────────────────┐
            │   API GATEWAY          │
            │   Spring Cloud Gateway │
            └────────┬───────────────┘
                     │
                     ▼
        ┌────────────────────────────┐
        │   AUTH SERVICE             │
        │   (Puerto 8081)            │
        │                            │
        │  • Login/Register          │
        │  • OAuth Integration       │
        │  • JWT Generation          │
        │  • Token Refresh           │
        │  • SSO Management          │
        └────────┬───────────────────┘
                 │
    ┌────────────┼────────────┐
    │            │            │
    ▼            ▼            ▼
┌────────┐  ┌────────┐  ┌──────────┐
│ Google │  │Facebook│  │  GitHub  │
│  OAuth │  │ OAuth  │  │  OAuth   │
└────────┘  └────────┘  └──────────┘
```

---

## 💾 Modelo de Datos

### Tabla: `users`

```sql
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    username VARCHAR(100) UNIQUE,
    password_hash VARCHAR(255), -- NULL si solo usa OAuth
    
    -- Información personal
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    avatar_url TEXT,
    phone VARCHAR(20),
    
    -- Rol y estado
    role VARCHAR(20) NOT NULL, -- WRITER, RESELLER, PRINTER, READER, ADMIN
    status VARCHAR(20) DEFAULT 'ACTIVE', -- ACTIVE, SUSPENDED, PENDING_VERIFICATION
    
    -- Email verification
    email_verified BOOLEAN DEFAULT FALSE,
    email_verification_token VARCHAR(255),
    email_verification_sent_at TIMESTAMP,
    
    -- Password reset
    password_reset_token VARCHAR(255),
    password_reset_expires_at TIMESTAMP,
    
    -- OAuth
    oauth_provider VARCHAR(50), -- 'google', 'facebook', 'github', etc.
    oauth_provider_id VARCHAR(255), -- ID del usuario en el provider
    
    -- Audit
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_login_at TIMESTAMP,
    login_count INTEGER DEFAULT 0,
    
    -- Soft delete
    deleted_at TIMESTAMP
);

CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_oauth ON users(oauth_provider, oauth_provider_id);
CREATE INDEX idx_users_role ON users(role);
```

### Tabla: `oauth_connections`

Para usuarios que vinculan múltiples cuentas OAuth:

```sql
CREATE TABLE oauth_connections (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id),
    
    provider VARCHAR(50) NOT NULL, -- 'google', 'facebook', etc.
    provider_user_id VARCHAR(255) NOT NULL,
    provider_email VARCHAR(255),
    
    access_token TEXT,
    refresh_token TEXT,
    token_expires_at TIMESTAMP,
    
    scope TEXT, -- Permisos otorgados
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    UNIQUE(provider, provider_user_id),
    UNIQUE(user_id, provider) -- Un usuario puede tener solo una conexión por provider
);
```

### Tabla: `refresh_tokens`

```sql
CREATE TABLE refresh_tokens (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id),
    token VARCHAR(500) UNIQUE NOT NULL,
    
    expires_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    -- Device tracking
    device_info TEXT,
    ip_address INET,
    user_agent TEXT,
    
    -- Revocación
    revoked BOOLEAN DEFAULT FALSE,
    revoked_at TIMESTAMP
);

CREATE INDEX idx_refresh_tokens_user ON refresh_tokens(user_id);
CREATE INDEX idx_refresh_tokens_token ON refresh_tokens(token);
```

---

## 🔧 Implementación Backend (Java)

### AuthController.java

```java
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final AuthService authService;
    private final OAuth2Service oauth2Service;
    private final JwtService jwtService;
    
    // ==================== REGISTRO ====================
    
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        User user = authService.register(request);
        
        // Enviar email de verificación
        authService.sendVerificationEmail(user);
        
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        
        return ResponseEntity.ok(AuthResponse.builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .user(UserDTO.from(user))
            .message("Cuenta creada. Por favor verifica tu email.")
            .build());
    }
    
    // ==================== LOGIN TRADICIONAL ====================
    
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        User user = authService.authenticate(request.getEmail(), request.getPassword());
        
        // Actualizar último login
        authService.updateLastLogin(user);
        
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        
        return ResponseEntity.ok(AuthResponse.builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .user(UserDTO.from(user))
            .redirectUrl(getPortalUrl(user.getRole()))
            .build());
    }
    
    // ==================== OAUTH 2.0 ====================
    
    @GetMapping("/oauth/{provider}")
    public ResponseEntity<OAuth2RedirectResponse> initiateOAuth(
            @PathVariable String provider,
            @RequestParam(required = false) String role) {
        
        String authorizationUrl = oauth2Service.getAuthorizationUrl(provider, role);
        
        return ResponseEntity.ok(OAuth2RedirectResponse.builder()
            .authorizationUrl(authorizationUrl)
            .build());
    }
    
    @GetMapping("/oauth/{provider}/callback")
    public ResponseEntity<AuthResponse> oauthCallback(
            @PathVariable String provider,
            @RequestParam String code,
            @RequestParam(required = false) String state) {
        
        // 1. Intercambiar code por access token
        OAuth2TokenResponse tokenResponse = oauth2Service.exchangeCodeForToken(provider, code);
        
        // 2. Obtener información del usuario del provider
        OAuth2UserInfo userInfo = oauth2Service.getUserInfo(provider, tokenResponse.getAccessToken());
        
        // 3. Buscar o crear usuario
        User user = authService.findOrCreateOAuthUser(provider, userInfo, state);
        
        // 4. Guardar conexión OAuth
        oauth2Service.saveOAuthConnection(user, provider, userInfo, tokenResponse);
        
        // 5. Generar tokens JWT
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        
        return ResponseEntity.ok(AuthResponse.builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .user(UserDTO.from(user))
            .redirectUrl(getPortalUrl(user.getRole()))
            .isNewUser(user.getCreatedAt().isAfter(LocalDateTime.now().minusMinutes(1)))
            .build());
    }
    
    // ==================== REFRESH TOKEN ====================
    
    @PostMapping("/refresh")
    public ResponseEntity<TokenRefreshResponse> refreshToken(@Valid @RequestBody TokenRefreshRequest request) {
        String newAccessToken = jwtService.refreshAccessToken(request.getRefreshToken());
        
        return ResponseEntity.ok(TokenRefreshResponse.builder()
            .accessToken(newAccessToken)
            .build());
    }
    
    // ==================== LOGOUT ====================
    
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader("Authorization") String token) {
        String refreshToken = extractRefreshToken(token);
        jwtService.revokeRefreshToken(refreshToken);
        return ResponseEntity.noContent().build();
    }
    
    // ==================== EMAIL VERIFICATION ====================
    
    @GetMapping("/verify-email")
    public ResponseEntity<MessageResponse> verifyEmail(@RequestParam String token) {
        authService.verifyEmail(token);
        return ResponseEntity.ok(MessageResponse.builder()
            .message("Email verificado exitosamente")
            .build());
    }
    
    // ==================== PASSWORD RESET ====================
    
    @PostMapping("/forgot-password")
    public ResponseEntity<MessageResponse> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        authService.sendPasswordResetEmail(request.getEmail());
        return ResponseEntity.ok(MessageResponse.builder()
            .message("Si el email existe, recibirás instrucciones para restablecer tu contraseña")
            .build());
    }
    
    @PostMapping("/reset-password")
    public ResponseEntity<MessageResponse> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request.getToken(), request.getNewPassword());
        return ResponseEntity.ok(MessageResponse.builder()
            .message("Contraseña restablecida exitosamente")
            .build());
    }
    
    // ==================== HELPERS ====================
    
    private String getPortalUrl(UserRole role) {
        return switch (role) {
            case WRITER -> "https://escritores.drakkarpress.com";
            case RESELLER -> "https://afiliados.drakkarpress.com";
            case PRINTER -> "https://imprentas.drakkarpress.com";
            case READER -> "https://www.drakkarpress.com/biblioteca";
            case ADMIN -> "https://admin.drakkarpress.com";
        };
    }
}
```

### OAuth2Service.java

```java
@Service
@RequiredArgsConstructor
public class OAuth2Service {
    
    private final RestTemplate restTemplate;
    private final OAuth2ConnectionRepository oauth2ConnectionRepository;
    
    @Value("${oauth2.google.client-id}")
    private String googleClientId;
    
    @Value("${oauth2.google.client-secret}")
    private String googleClientSecret;
    
    @Value("${oauth2.google.redirect-uri}")
    private String googleRedirectUri;
    
    // Similar para Facebook, GitHub, etc.
    
    public String getAuthorizationUrl(String provider, String role) {
        String state = generateState(role); // Para preservar el rol durante el flujo
        
        return switch (provider.toLowerCase()) {
            case "google" -> String.format(
                "https://accounts.google.com/o/oauth2/v2/auth?" +
                "client_id=%s&" +
                "redirect_uri=%s&" +
                "response_type=code&" +
                "scope=openid email profile&" +
                "state=%s",
                googleClientId, googleRedirectUri, state
            );
            
            case "facebook" -> String.format(
                "https://www.facebook.com/v18.0/dialog/oauth?" +
                "client_id=%s&" +
                "redirect_uri=%s&" +
                "scope=email,public_profile&" +
                "state=%s",
                facebookClientId, facebookRedirectUri, state
            );
            
            case "github" -> String.format(
                "https://github.com/login/oauth/authorize?" +
                "client_id=%s&" +
                "redirect_uri=%s&" +
                "scope=user:email&" +
                "state=%s",
                githubClientId, githubRedirectUri, state
            );
            
            // ... otros providers
            
            default -> throw new UnsupportedProviderException(provider);
        };
    }
    
    public OAuth2TokenResponse exchangeCodeForToken(String provider, String code) {
        return switch (provider.toLowerCase()) {
            case "google" -> exchangeGoogleCode(code);
            case "facebook" -> exchangeFacebookCode(code);
            case "github" -> exchangeGitHubCode(code);
            // ... otros providers
            default -> throw new UnsupportedProviderException(provider);
        };
    }
    
    private OAuth2TokenResponse exchangeGoogleCode(String code) {
        String tokenUrl = "https://oauth2.googleapis.com/token";
        
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("code", code);
        params.add("client_id", googleClientId);
        params.add("client_secret", googleClientSecret);
        params.add("redirect_uri", googleRedirectUri);
        params.add("grant_type", "authorization_code");
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        
        ResponseEntity<GoogleTokenResponse> response = restTemplate.postForEntity(
            tokenUrl, request, GoogleTokenResponse.class
        );
        
        return OAuth2TokenResponse.from(response.getBody());
    }
    
    public OAuth2UserInfo getUserInfo(String provider, String accessToken) {
        return switch (provider.toLowerCase()) {
            case "google" -> getGoogleUserInfo(accessToken);
            case "facebook" -> getFacebookUserInfo(accessToken);
            case "github" -> getGitHubUserInfo(accessToken);
            // ... otros providers
            default -> throw new UnsupportedProviderException(provider);
        };
    }
    
    private OAuth2UserInfo getGoogleUserInfo(String accessToken) {
        String userInfoUrl = "https://www.googleapis.com/oauth2/v2/userinfo";
        
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        
        HttpEntity<String> entity = new HttpEntity<>(headers);
        
        ResponseEntity<GoogleUserInfo> response = restTemplate.exchange(
            userInfoUrl, HttpMethod.GET, entity, GoogleUserInfo.class
        );
        
        GoogleUserInfo googleUser = response.getBody();
        
        return OAuth2UserInfo.builder()
            .providerId(googleUser.getId())
            .email(googleUser.getEmail())
            .emailVerified(googleUser.getVerifiedEmail())
            .firstName(googleUser.getGivenName())
            .lastName(googleUser.getFamilyName())
            .avatarUrl(googleUser.getPicture())
            .locale(googleUser.getLocale())
            .build();
    }
    
    public void saveOAuthConnection(User user, String provider, 
                                     OAuth2UserInfo userInfo, 
                                     OAuth2TokenResponse tokenResponse) {
        
        OAuth2Connection connection = oauth2ConnectionRepository
            .findByUserIdAndProvider(user.getId(), provider)
            .orElse(new OAuth2Connection());
        
        connection.setUserId(user.getId());
        connection.setProvider(provider);
        connection.setProviderUserId(userInfo.getProviderId());
        connection.setProviderEmail(userInfo.getEmail());
        connection.setAccessToken(tokenResponse.getAccessToken());
        connection.setRefreshToken(tokenResponse.getRefreshToken());
        connection.setTokenExpiresAt(LocalDateTime.now().plusSeconds(tokenResponse.getExpiresIn()));
        connection.setScope(tokenResponse.getScope());
        
        oauth2ConnectionRepository.save(connection);
    }
}
```

### AuthService.java

```java
@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    
    public User register(RegisterRequest request) {
        // Validar que el email no exista
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException();
        }
        
        // Crear usuario
        User user = new User();
        user.setEmail(request.getEmail());
        user.setUsername(request.getUsername());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setRole(request.getRole());
        user.setEmailVerificationToken(generateVerificationToken());
        user.setEmailVerificationSentAt(LocalDateTime.now());
        
        return userRepository.save(user);
    }
    
    public User authenticate(String email, String password) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new InvalidCredentialsException());
        
        if (user.getPasswordHash() == null) {
            throw new OAuthOnlyUserException("Este usuario solo puede iniciar sesión con OAuth");
        }
        
        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }
        
        if (!user.isEmailVerified()) {
            throw new EmailNotVerifiedException();
        }
        
        return user;
    }
    
    public User findOrCreateOAuthUser(String provider, OAuth2UserInfo userInfo, String state) {
        // Buscar por provider ID
        Optional<User> existingUser = userRepository.findByOAuthProviderAndProviderId(
            provider, userInfo.getProviderId()
        );
        
        if (existingUser.isPresent()) {
            return existingUser.get();
        }
        
        // Buscar por email (vincular cuenta existente)
        Optional<User> userByEmail = userRepository.findByEmail(userInfo.getEmail());
        if (userByEmail.isPresent()) {
            User user = userByEmail.get();
            user.setOauthProvider(provider);
            user.setOauthProviderId(userInfo.getProviderId());
            user.setEmailVerified(userInfo.isEmailVerified()); // OAuth ya verificó el email
            return userRepository.save(user);
        }
        
        // Crear nuevo usuario
        User newUser = new User();
        newUser.setEmail(userInfo.getEmail());
        newUser.setFirstName(userInfo.getFirstName());
        newUser.setLastName(userInfo.getLastName());
        newUser.setAvatarUrl(userInfo.getAvatarUrl());
        newUser.setOauthProvider(provider);
        newUser.setOauthProviderId(userInfo.getProviderId());
        newUser.setEmailVerified(userInfo.isEmailVerified());
        newUser.setRole(extractRoleFromState(state)); // Del parámetro state
        
        return userRepository.save(newUser);
    }
    
    public void sendVerificationEmail(User user) {
        String verificationLink = String.format(
            "https://www.drakkarpress.com/verify-email?token=%s",
            user.getEmailVerificationToken()
        );
        
        emailService.sendEmail(
            user.getEmail(),
            "Verifica tu cuenta en DrakkarPress",
            "verification-email.html",
            Map.of(
                "firstName", user.getFirstName(),
                "verificationLink", verificationLink
            )
        );
    }
    
    public void verifyEmail(String token) {
        User user = userRepository.findByEmailVerificationToken(token)
            .orElseThrow(() -> new InvalidTokenException());
        
        if (user.getEmailVerificationSentAt().isBefore(LocalDateTime.now().minusHours(24))) {
            throw new TokenExpiredException();
        }
        
        user.setEmailVerified(true);
        user.setEmailVerificationToken(null);
        userRepository.save(user);
    }
    
    // ... métodos de password reset, etc.
}
```

---

## 🎨 Frontend - Componente de Login

### LoginPage.tsx (React/Next.js)

```tsx
'use client';

import { useState } from 'react';
import { useRouter } from 'next/navigation';
import { useAuth } from '@/hooks/useAuth';

export default function LoginPage() {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState('');
  
  const router = useRouter();
  const { login, loginWithOAuth } = useAuth();
  
  const handleEmailLogin = async (e: React.FormEvent) => {
    e.preventDefault();
    setIsLoading(true);
    setError('');
    
    try {
      const response = await login(email, password);
      router.push(response.redirectUrl);
    } catch (err: any) {
      setError(err.message || 'Error al iniciar sesión');
    } finally {
      setIsLoading(false);
    }
  };
  
  const handleOAuthLogin = async (provider: string) => {
    try {
      // Redirigir al flujo OAuth
      const response = await fetch(`/api/auth/oauth/${provider}`);
      const data = await response.json();
      window.location.href = data.authorizationUrl;
    } catch (err: any) {
      setError(err.message || `Error al conectar con ${provider}`);
    }
  };
  
  return (
    <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-blue-900 to-blue-700">
      <div className="bg-white p-8 rounded-2xl shadow-2xl w-full max-w-md">
        {/* Logo */}
        <div className="text-center mb-8">
          <h1 className="text-3xl font-bold text-blue-900">DrakkarPress</h1>
          <p className="text-gray-600 mt-2">Inicia sesión en tu cuenta</p>
        </div>
        
        {/* Error Message */}
        {error && (
          <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded mb-4">
            {error}
          </div>
        )}
        
        {/* Email/Password Form */}
        <form onSubmit={handleEmailLogin} className="space-y-4">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Email
            </label>
            <input
              type="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              required
              className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              placeholder="tu@email.com"
            />
          </div>
          
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Contraseña
            </label>
            <input
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
              className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              placeholder="••••••••"
            />
          </div>
          
          <div className="flex items-center justify-between text-sm">
            <label className="flex items-center">
              <input type="checkbox" className="mr-2" />
              Recordarme
            </label>
            <a href="/forgot-password" className="text-blue-600 hover:underline">
              ¿Olvidaste tu contraseña?
            </a>
          </div>
          
          <button
            type="submit"
            disabled={isLoading}
            className="w-full bg-blue-600 text-white py-3 rounded-lg font-semibold hover:bg-blue-700 transition disabled:opacity-50"
          >
            {isLoading ? 'Iniciando sesión...' : 'Iniciar Sesión'}
          </button>
        </form>
        
        {/* Divider */}
        <div className="relative my-6">
          <div className="absolute inset-0 flex items-center">
            <div className="w-full border-t border-gray-300"></div>
          </div>
          <div className="relative flex justify-center text-sm">
            <span className="px-4 bg-white text-gray-500">O continuar con</span>
          </div>
        </div>
        
        {/* OAuth Buttons */}
        <div className="grid grid-cols-2 gap-3">
          <button
            onClick={() => handleOAuthLogin('google')}
            className="flex items-center justify-center gap-2 px-4 py-2 border border-gray-300 rounded-lg hover:bg-gray-50 transition"
          >
            <img src="/icons/google.svg" alt="Google" className="w-5 h-5" />
            Google
          </button>
          
          <button
            onClick={() => handleOAuthLogin('facebook')}
            className="flex items-center justify-center gap-2 px-4 py-2 border border-gray-300 rounded-lg hover:bg-gray-50 transition"
          >
            <img src="/icons/facebook.svg" alt="Facebook" className="w-5 h-5" />
            Facebook
          </button>
          
          <button
            onClick={() => handleOAuthLogin('github')}
            className="flex items-center justify-center gap-2 px-4 py-2 border border-gray-300 rounded-lg hover:bg-gray-50 transition"
          >
            <img src="/icons/github.svg" alt="GitHub" className="w-5 h-5" />
            GitHub
          </button>
          
          <button
            onClick={() => handleOAuthLogin('linkedin')}
            className="flex items-center justify-center gap-2 px-4 py-2 border border-gray-300 rounded-lg hover:bg-gray-50 transition"
          >
            <img src="/icons/linkedin.svg" alt="LinkedIn" className="w-5 h-5" />
            LinkedIn
          </button>
        </div>
        
        {/* Register Link */}
        <p className="text-center mt-6 text-sm text-gray-600">
          ¿No tienes cuenta?{' '}
          <a href="/register" className="text-blue-600 font-semibold hover:underline">
            Regístrate gratis
          </a>
        </p>
      </div>
    </div>
  );
}
```

---

## 🔄 Flujo Completo OAuth (Ejemplo: Google)

```
1. Usuario hace clic en "Continuar con Google"
   ↓
2. Frontend llama a /api/auth/oauth/google
   ↓
3. Backend genera URL de autorización y la devuelve
   ↓
4. Frontend redirige al usuario a Google
   ↓
5. Usuario autoriza en Google
   ↓
6. Google redirige a /api/auth/oauth/google/callback?code=XXX
   ↓
7. Backend intercambia code por access_token
   ↓
8. Backend obtiene info del usuario desde Google API
   ↓
9. Backend busca o crea usuario en BD
   ↓
10. Backend guarda conexión OAuth
   ↓
11. Backend genera JWT tokens
   ↓
12. Backend redirige a portal según rol
   ↓
13. Usuario está autenticado ✅
```

---

## 🔒 Seguridad

### Tokens JWT

```java
@Service
public class JwtService {
    
    @Value("${jwt.secret}")
    private String jwtSecret;
    
    @Value("${jwt.access-token-expiration}") // 15 minutos
    private long accessTokenExpiration;
    
    @Value("${jwt.refresh-token-expiration}") // 7 días
    private long refreshTokenExpiration;
    
    public String generateAccessToken(User user) {
        return Jwts.builder()
            .setSubject(user.getId().toString())
            .claim("email", user.getEmail())
            .claim("role", user.getRole())
            .claim("type", "access")
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + accessTokenExpiration))
            .signWith(SignatureAlgorithm.HS512, jwtSecret)
            .compact();
    }
    
    public String generateRefreshToken(User user) {
        String token = Jwts.builder()
            .setSubject(user.getId().toString())
            .claim("type", "refresh")
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + refreshTokenExpiration))
            .signWith(SignatureAlgorithm.HS512, jwtSecret)
            .compact();
        
        // Guardar en BD
        saveRefreshToken(user, token);
        
        return token;
    }
}
```

### Configuración en application.yml

```yaml
oauth2:
  google:
    client-id: ${GOOGLE_CLIENT_ID}
    client-secret: ${GOOGLE_CLIENT_SECRET}
    redirect-uri: https://api.drakkarpress.com/auth/oauth/google/callback
    
  facebook:
    client-id: ${FACEBOOK_CLIENT_ID}
    client-secret: ${FACEBOOK_CLIENT_SECRET}
    redirect-uri: https://api.drakkarpress.com/auth/oauth/facebook/callback
    
  github:
    client-id: ${GITHUB_CLIENT_ID}
    client-secret: ${GITHUB_CLIENT_SECRET}
    redirect-uri: https://api.drakkarpress.com/auth/oauth/github/callback
    
  linkedin:
    client-id: ${LINKEDIN_CLIENT_ID}
    client-secret: ${LINKEDIN_CLIENT_SECRET}
    redirect-uri: https://api.drakkarpress.com/auth/oauth/linkedin/callback

jwt:
  secret: ${JWT_SECRET}
  access-token-expiration: 900000 # 15 minutos
  refresh-token-expiration: 604800000 # 7 días
```

---

## ✅ Ventajas del Sistema

### Para Usuarios:
✅ **Un solo clic** para registrarse con cuentas existentes  
✅ **Sin contraseñas adicionales** si usan OAuth  
✅ **Sincronización automática** de foto de perfil y datos  
✅ **Menos fricción** = más conversiones

### Para la Plataforma:
✅ **Mayor tasa de registro** (OAuth reduce abandono en 30-50%)  
✅ **Datos verificados** (emails ya validados por Google, Facebook, etc.)  
✅ **Menos soporte** (menos "olvidé mi contraseña")  
✅ **Seguridad mejorada** (delegamos autenticación a expertos)

---

## 📊 Métricas a Monitorear

```sql
-- Distribución de métodos de autenticación
SELECT 
    CASE 
        WHEN oauth_provider IS NOT NULL THEN oauth_provider
        ELSE 'email_password'
    END as auth_method,
    COUNT(*) as users,
    ROUND(COUNT(*) * 100.0 / SUM(COUNT(*)) OVER (), 2) as percentage
FROM users
WHERE deleted_at IS NULL
GROUP BY auth_method
ORDER BY users DESC;

-- Tasa de verificación de email
SELECT 
    role,
    COUNT(*) as total_users,
    SUM(CASE WHEN email_verified THEN 1 ELSE 0 END) as verified,
    ROUND(SUM(CASE WHEN email_verified THEN 1 ELSE 0 END) * 100.0 / COUNT(*), 2) as verification_rate
FROM users
WHERE deleted_at IS NULL
GROUP BY role;

-- Logins por día
SELECT 
    DATE(last_login_at) as date,
    COUNT(DISTINCT id) as unique_users,
    SUM(login_count) as total_logins
FROM users
WHERE last_login_at >= CURRENT_DATE - INTERVAL '30 days'
GROUP BY DATE(last_login_at)
ORDER BY date DESC;
```

---

**Versión:** 1.0  
**Última actualización:** 9 nov 2025  
**Próxima revisión:** Configuración de OAuth providers
