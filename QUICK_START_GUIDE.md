# 🎯 HIZLI BAŞLANGIÇ: ÖNCELİKLİ 5 ÖZELLİK

Bu dosya sonraki **4 hafta** içinde uygulanabilecek en kritik özellikleri adım adım açıklar.

---

## 📊 UYGULAMA SIRALAMASI

```
HAFTA 1-2: User Authentication System
    ↓
HAFTA 3-4: Payment & Ticket System
    ↓
HAFTA 5-6: Rating & Review System
    ↓
HAFTA 7-8: Admin Dashboard
    ↓
HAFTA 9+: Sonraki Aşama
```

---

## 🔐 ÖZELLİK 1: USER AUTHENTICATION (WEEK 1-2)

### Database Değişiklikleri

```sql
-- 1. Users Tablosu
CREATE TABLE Users (
    id BIGINT PRIMARY KEY IDENTITY(1,1),
    email VARCHAR(150) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    phone_number VARCHAR(20),
    is_active BIT DEFAULT 1,
    is_email_verified BIT DEFAULT 0,
    verification_token VARCHAR(255),
    password_reset_token VARCHAR(255),
    password_reset_expires DATETIME,
    created_at DATETIME DEFAULT GETDATE(),
    updated_at DATETIME,
    last_login DATETIME
);

-- 2. Roles Tablosu
CREATE TABLE Roles (
    id INT PRIMARY KEY IDENTITY(1,1),
    name VARCHAR(50) NOT NULL UNIQUE,  -- ADMIN, ORGANIZER, PARTICIPANT
    description VARCHAR(200)
);

INSERT INTO Roles VALUES ('ADMIN', 'System Administrator');
INSERT INTO Roles VALUES ('ORGANIZER', 'Event Organizer');
INSERT INTO Roles VALUES ('PARTICIPANT', 'Event Participant');

-- 3. UserRoles (Many-to-Many)
CREATE TABLE UserRoles (
    user_id BIGINT NOT NULL,
    role_id INT NOT NULL,
    assigned_at DATETIME DEFAULT GETDATE(),
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES Users(id),
    FOREIGN KEY (role_id) REFERENCES Roles(id)
);

-- 4. Participants update (link to Users)
ALTER TABLE Participants
ADD user_id BIGINT,
    CONSTRAINT FK_Participants_UserId FOREIGN KEY(user_id) REFERENCES Users(id);
```

### Backend: Entities

```java
// User.java
@Entity
@Table(name = "Users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String email;
    
    @Column(nullable = false)
    private String passwordHash;
    
    private String firstName;
    private String lastName;
    private String phoneNumber;
    
    @Column(nullable = false)
    private Boolean isActive = true;
    
    @Column(nullable = false)
    private Boolean isEmailVerified = false;
    
    private String verificationToken;
    private String passwordResetToken;
    private LocalDateTime passwordResetExpires;
    
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    private LocalDateTime lastLogin;
    
    @ManyToMany
    @JoinTable(
        name = "UserRoles",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();
}

// Role.java
@Entity
@Table(name = "Roles")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(unique = true, nullable = false)
    private String name;  // ADMIN, ORGANIZER, PARTICIPANT
    
    private String description;
}
```

### Backend: DTOs

```java
// RegisterRequest.java
public class RegisterRequest {
    @NotBlank
    @Email
    private String email;
    
    @NotBlank
    @Size(min = 8)
    private String password;
    
    @NotBlank
    private String firstName;
    
    @NotBlank
    private String lastName;
    
    private String phoneNumber;
}

// LoginRequest.java
public class LoginRequest {
    @NotBlank
    @Email
    private String email;
    
    @NotBlank
    private String password;
}

// AuthResponse.java
public class AuthResponse {
    private Long userId;
    private String email;
    private String accessToken;
    private String refreshToken;
    private List<String> roles;
    private LocalDateTime expiresIn;
}
```

### Backend: Service

```java
@Service
public class AuthService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private RoleRepository roleRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtProvider jwtProvider;
    
    @Autowired
    private EmailService emailService;
    
    public AuthResponse register(RegisterRequest request) {
        // 1. Email validation
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already in use");
        }
        
        // 2. Create user
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setIsEmailVerified(false);
        user.setVerificationToken(generateToken());
        
        // 3. Add PARTICIPANT role
        Role participantRole = roleRepository.findByName("PARTICIPANT")
            .orElseThrow(() -> new RuntimeException("Role not found"));
        user.getRoles().add(participantRole);
        
        // 4. Save user
        User savedUser = userRepository.save(user);
        
        // 5. Send verification email
        emailService.sendVerificationEmail(
            user.getEmail(),
            user.getVerificationToken(),
            user.getFirstName()
        );
        
        // 6. Generate tokens
        String accessToken = jwtProvider.generateToken(savedUser.getId());
        String refreshToken = jwtProvider.generateRefreshToken(savedUser.getId());
        
        return buildAuthResponse(savedUser, accessToken, refreshToken);
    }
    
    public AuthResponse login(LoginRequest request) {
        // 1. Find user
        User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        // 2. Validate password
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Invalid password");
        }
        
        // 3. Check if email verified
        if (!user.getIsEmailVerified()) {
            throw new RuntimeException("Please verify your email first");
        }
        
        // 4. Update last login
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);
        
        // 5. Generate tokens
        String accessToken = jwtProvider.generateToken(user.getId());
        String refreshToken = jwtProvider.generateRefreshToken(user.getId());
        
        return buildAuthResponse(user, accessToken, refreshToken);
    }
    
    public void verifyEmail(String token) {
        User user = userRepository.findByVerificationToken(token)
            .orElseThrow(() -> new RuntimeException("Invalid token"));
        
        user.setIsEmailVerified(true);
        user.setVerificationToken(null);
        userRepository.save(user);
    }
    
    public void requestPasswordReset(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        String resetToken = generateToken();
        user.setPasswordResetToken(resetToken);
        user.setPasswordResetExpires(LocalDateTime.now().plusHours(1));
        userRepository.save(user);
        
        // Send reset link via email
        emailService.sendPasswordResetEmail(email, resetToken, user.getFirstName());
    }
    
    public void resetPassword(String token, String newPassword) {
        User user = userRepository.findByPasswordResetToken(token)
            .orElseThrow(() -> new RuntimeException("Invalid or expired token"));
        
        if (user.getPasswordResetExpires().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Token has expired");
        }
        
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        user.setPasswordResetToken(null);
        user.setPasswordResetExpires(null);
        userRepository.save(user);
    }
}
```

### Backend: Controller

```java
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
@Tag(name = "Authentication", description = "User authentication endpoints")
public class AuthController {
    
    @Autowired
    private AuthService authService;
    
    @PostMapping("/register")
    @Operation(summary = "Register new user")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    
    @PostMapping("/login")
    @Operation(summary = "Login user")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/verify-email")
    @Operation(summary = "Verify email address")
    public ResponseEntity<String> verifyEmail(@RequestParam String token) {
        authService.verifyEmail(token);
        return ResponseEntity.ok("Email verified successfully");
    }
    
    @PostMapping("/forgot-password")
    @Operation(summary = "Request password reset")
    public ResponseEntity<String> forgotPassword(@RequestParam String email) {
        authService.requestPasswordReset(email);
        return ResponseEntity.ok("Password reset link sent to email");
    }
    
    @PostMapping("/reset-password")
    @Operation(summary = "Reset password with token")
    public ResponseEntity<String> resetPassword(
            @RequestParam String token,
            @RequestBody ResetPasswordRequest request) {
        authService.resetPassword(token, request.getNewPassword());
        return ResponseEntity.ok("Password reset successfully");
    }
}
```

### JWT Configuration

```java
// JwtProvider.java
@Component
public class JwtProvider {
    
    @Value("${jwt.secret}")
    private String jwtSecret;
    
    @Value("${jwt.expiration}")
    private long jwtExpirationMs;
    
    public String generateToken(Long userId) {
        return Jwts.builder()
            .setSubject(userId.toString())
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
            .signWith(SignatureAlgorithm.HS512, jwtSecret)
            .compact();
    }
    
    public Long getUserIdFromToken(String token) {
        return Long.parseLong(Jwts.parser()
            .setSigningKey(jwtSecret)
            .parseClaimsJws(token)
            .getBody()
            .getSubject());
    }
    
    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}

// JwtAuthenticationFilter.java
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    @Autowired
    private JwtProvider jwtProvider;
    
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            String jwt = getJwtFromRequest(request);
            
            if (jwt != null && jwtProvider.validateToken(jwt)) {
                Long userId = jwtProvider.getUserIdFromToken(jwt);
                UserDetails userDetails = UserDetailsService.loadUserById(userId);
                
                UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userDetails, null,
                        userDetails.getAuthorities());
                
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        filterChain.doFilter(request, response);
    }
    
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}

// SecurityConfig.java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf().disable()
            .cors()
            .and()
            .exceptionHandling()
            .and()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .authorizeRequests()
            .antMatchers("/api/auth/**").permitAll()
            .antMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
            .anyRequest().authenticated()
            .and()
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```

### application.properties güncellemesi

```properties
# JWT Configuration
jwt.secret=your-very-long-secret-key-minimum-32-characters-for-HS256
jwt.expiration=86400000  # 24 hours in ms
jwt.refresh-expiration=604800000  # 7 days in ms
```

### pom.xml Bağımlılıkları

```xml
<!-- JWT -->
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt</artifactId>
    <version>0.11.5</version>
</dependency>

<!-- Security -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>

<!-- BCrypt -->
<dependency>
    <groupId>org.springframework.security</groupId>
    <artifactId>spring-security-crypto</artifactId>
</dependency>
```

---

## 💳 ÖZELLİK 2: PAYMENT & TICKET SYSTEM (WEEK 3-4)

### Database

```sql
CREATE TABLE Tickets (
    id BIGINT PRIMARY KEY IDENTITY(1,1),
    event_id BIGINT NOT NULL,
    participant_id BIGINT NOT NULL,
    ticket_number VARCHAR(50) NOT NULL UNIQUE,
    ticket_type VARCHAR(50),  -- VIP, STANDARD, EARLY_BIRD
    price DECIMAL(10,2),
    discount_amount DECIMAL(10,2) DEFAULT 0,
    final_price DECIMAL(10,2),
    purchase_date DATETIME DEFAULT GETDATE(),
    payment_status VARCHAR(50),  -- PENDING, PAID, REFUNDED
    payment_method VARCHAR(50),  -- CREDIT_CARD, BANK_TRANSFER
    transaction_id VARCHAR(100),
    qr_code VARCHAR(500),
    is_used BIT DEFAULT 0,
    used_at DATETIME,
    FOREIGN KEY (event_id) REFERENCES Events(id),
    FOREIGN KEY (participant_id) REFERENCES Participants(id)
);

CREATE INDEX IX_Tickets_ParticipantId ON Tickets(participant_id);
CREATE INDEX IX_Tickets_EventId ON Tickets(event_id);
CREATE INDEX IX_Tickets_PaymentStatus ON Tickets(payment_status);
```

### Entity & Service

```java
@Service
public class TicketService {
    
    @Autowired
    private TicketRepository ticketRepository;
    
    @Autowired
    private StripeService stripeService;
    
    public TicketDTO purchaseTicket(Long eventId, Long participantId, 
                                     String stripeToken, String ticketType) {
        // 1. Create Stripe charge
        ChargeRequest chargeRequest = new ChargeRequest();
        chargeRequest.setAmount((long) (eventPrice * 100)); // Cents
        chargeRequest.setCurrency("TRY");
        chargeRequest.setToken(stripeToken);
        
        StripeResponse charge = stripeService.charge(chargeRequest);
        
        if (!charge.isSuccess()) {
            throw new RuntimeException("Payment failed: " + charge.getError());
        }
        
        // 2. Create ticket
        Ticket ticket = new Ticket();
        ticket.setEventId(eventId);
        ticket.setParticipantId(participantId);
        ticket.setTicketNumber(generateTicketNumber());
        ticket.setTicketType(ticketType);
        ticket.setPrice(eventPrice);
        ticket.setFinalPrice(eventPrice);
        ticket.setPaymentStatus("PAID");
        ticket.setPaymentMethod("CREDIT_CARD");
        ticket.setTransactionId(charge.getId());
        ticket.setQrCode(generateQRCode(ticket.getTicketNumber()));
        
        Ticket savedTicket = ticketRepository.save(ticket);
        
        return convertToDTO(savedTicket);
    }
}
```

---

## ⭐ ÖZELLİK 3: RATING & REVIEW (WEEK 5-6)

### Database

```sql
CREATE TABLE EventReviews (
    id BIGINT PRIMARY KEY IDENTITY(1,1),
    event_id BIGINT NOT NULL,
    participant_id BIGINT NOT NULL,
    rating INT CHECK (rating >= 1 AND rating <= 5),
    comment VARCHAR(2000),
    helpful_count INT DEFAULT 0,
    created_at DATETIME DEFAULT GETDATE(),
    FOREIGN KEY (event_id) REFERENCES Events(id),
    FOREIGN KEY (participant_id) REFERENCES Participants(id),
    CONSTRAINT UK_UserEventReview UNIQUE(event_id, participant_id)
);
```

### Service & API

```java
@Service
public class ReviewService {
    
    public EventReview createReview(Long eventId, Long participantId,
                                    int rating, String comment) {
        EventReview review = new EventReview();
        review.setEventId(eventId);
        review.setParticipantId(participantId);
        review.setRating(rating);
        review.setComment(comment);
        
        EventReview saved = reviewRepository.save(review);
        updateEventAverageRating(eventId);
        
        return saved;
    }
    
    private void updateEventAverageRating(Long eventId) {
        double avgRating = reviewRepository.getAverageRatingByEventId(eventId);
        // Update event table
    }
}
```

---

## 🎯 VERİLEN HEDEF: ÖNCELİK SIRLAMASI

```
1️⃣ Authentication System
   Time: 5-7 days
   Value: CRITICAL
   
2️⃣ Payment System
   Time: 7-10 days
   Value: CRITICAL
   
3️⃣ Rating System
   Time: 3-4 days
   Value: HIGH
   
4️⃣ Admin Dashboard
   Time: 7-10 days
   Value: HIGH
```

---

## 🚀 BAŞLAMAK İÇİN

### 1. Database Scriptini Çalıştır
```bash
# SQL Server'da
sqlcmd -S localhost -U sa -P Salih.12345 -i ROADMAP_CHANGES.sql
```

### 2. Entities Oluştur
```bash
# src/main/java/com/example/demo/entity/ içinde
User.java
Role.java
Ticket.java
EventReview.java
```

### 3. Repositories Oluştur
```bash
# src/main/java/com/example/demo/repository/ içinde
UserRepository.java
RoleRepository.java
TicketRepository.java
ReviewRepository.java
```

### 4. Services İmplemente Et
```bash
# src/main/java/com/example/demo/service/ içinde
AuthService.java
AuthServiceImpl.java
TicketService.java
ReviewService.java
```

### 5. Controllers Oluştur
```bash
# src/main/java/com/example/demo/controller/ içinde
AuthController.java
TicketController.java
ReviewController.java
```

---

**Estimated Implementation:** 4-6 weeks  
**Team Size:** 2-3 developers  
**Status:** Ready to Start
