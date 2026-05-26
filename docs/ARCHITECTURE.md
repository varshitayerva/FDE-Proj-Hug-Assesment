# AI-Stylist Architecture Documentation

## High-Level Architecture Diagram

```
┌─────────────────────────────────────────────────────────────────┐
│                      Client Layer (Browser)                      │
│  ┌────────────────────────────────────────────────────────────┐  │
│  │  Angular 18 Frontend (Port 4200)                           │  │
│  │  - Dashboard, Stylist Form, Recommendations Pages          │  │
│  │  - Reactive Forms, RxJS Observables                        │  │
│  │  - JWT Token Management, Auth Guards                       │  │
│  └────────────────────────────────────────────────────────────┘  │
└────────────────┬────────────────────────────────────────────────┘
                 │ HTTP/REST
                 │ JSON over HTTPS
                 │
┌────────────────▼────────────────────────────────────────────────┐
│                    API Gateway / Load Balancer                   │
│                   (Port 8080 - Spring Boot)                      │
└────────────────┬────────────────────────────────────────────────┘
                 │
    ┌────────────┴────────────┐
    │                         │
┌───▼──────────────────┐   ┌─▼──────────────────────┐
│  Controller Layer    │   │   Security Layer       │
│  - AuthController    │   │  - JwtTokenProvider    │
│  - StylistController │   │  - CustomUserDetails   │
│                      │   │  - SecurityConfig      │
└───┬──────────────────┘   └─┬──────────────────────┘
    │                        │
    └────────────┬───────────┘
                 │
    ┌────────────▼────────────┐
    │   Service Layer         │
    │ ┌──────────────────────┐│
    │ │  AuthService         ││
    │ │  - signup()          ││
    │ │  - login()           ││
    │ └──────────────────────┘│
    │ ┌──────────────────────┐│
    │ │ StylistService       ││
    │ │ - getRecommendation()││
    │ │ - getUserRecs()      ││
    │ └──────────────────────┘│
    │ ┌──────────────────────┐│
    │ │ StylistWorkflow      ││
    │ │ - analyzeStyle()     ││
    │ │ - generateOutfits()  ││
    │ │ - generateSummary()  ││
    │ └──────────────────────┘│
    └────────────┬────────────┘
                 │
    ┌────────────┴────────────┐
    │                         │
┌───▼──────────────────┐   ┌─▼──────────────────────┐
│ Repository Layer     │   │  Hugging Face Client   │
│ - UserRepository     │   │  - generateResponse()  │
│ - UserProfile...     │   │  - Failover routing    │
│ - Recommendation...  │   │  - Error handling      │
└───┬──────────────────┘   └─┬──────────────────────┘
    │                        │
    └────────────┬───────────┘
                 │
    ┌────────────▼────────────┐
    │   Entity Layer (JPA)    │
    │ - User                  │
    │ - UserProfile           │
    │ - Recommendation        │
    └────────────┬────────────┘
                 │
    ┌────────────▼────────────┐
    │   Data Layer            │
    │  MySQL Database         │
    │  - users table          │
    │  - user_profiles table  │
    │  - recommendations table│
    └────────────────────────┘

┌────────────────────────────────────────────────────────────────┐
│              External Services                                 │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │  Hugging Face Inference API                              │  │
│  │  - Primary: mistralai/Mistral-7B-Instruct-v0.3          │  │
│  │  - Fallback: google/gemma-2-2b-it                        │  │
│  │  - Max 3 LLM calls per recommendation                    │  │
│  └──────────────────────────────────────────────────────────┘  │
└────────────────────────────────────────────────────────────────┘
```

---

## Backend Architecture

### Technology Stack

**Framework:** Spring Boot 3.3.0
**Language:** Java 21
**ORM:** Hibernate/Spring Data JPA
**Database:** MySQL 8.0+
**Security:** Spring Security + JWT (JJWT)
**HTTP Client:** Spring WebFlux (WebClient)
**Build:** Maven 3.8+

### Package Structure

```
com.aistyle/
├── controller/
│   ├── AuthController          - Login/signup endpoints
│   └── StylistController       - Recommendation endpoints
├── service/
│   ├── AuthService             - Authentication logic
│   └── StylistService          - Business logic for recommendations
├── repository/
│   ├── UserRepository          - User CRUD operations
│   ├── UserProfileRepository   - User profile operations
│   └── RecommendationRepository - Recommendation history
├── entity/
│   ├── User                    - User JPA entity with enums
│   ├── UserProfile             - User profile with 10+ enums
│   └── Recommendation          - Recommendation results
├── dto/
│   ├── AuthRequest/Response    - Auth DTOs
│   ├── SignupRequest           - Registration DTO
│   ├── UserProfileRequest      - Profile form DTO
│   ├── StyleAnalysisResponse   - AI analysis DTO
│   ├── StylistRecommendationResponse - Recommendation response
│   └── ApiResponse<T>          - Generic API response wrapper
├── security/
│   ├── JwtTokenProvider        - JWT token generation/validation
│   ├── CustomUserDetails       - Spring Security user details
│   ├── CustomUserDetailsService - User details loader
│   └── JwtAuthenticationFilter - JWT request filter
├── config/
│   ├── SecurityConfig          - Spring Security configuration
│   └── WebConfig               - CORS & MVC configuration
├── ai/
│   └── HuggingFaceClient       - Hugging Face API client with failover
├── workflow/
│   └── StylistWorkflow         - AI workflow (2-3 LLM calls)
├── exception/
│   ├── ResourceNotFoundException
│   ├── BadRequestException
│   ├── AIServiceException
│   └── GlobalExceptionHandler
├── validation/
│   └── (Custom validators if needed)
├── util/
│   └── (Utility classes)
└── AiStylistApplication        - Main Spring Boot app
```

### Data Flow

```
1. User Signup/Login Request
   ↓
2. AuthController receives request
   ↓
3. AuthService processes authentication
   ↓
4. PasswordEncoder (BCrypt) validates password
   ↓
5. JwtTokenProvider generates JWT token
   ↓
6. Token returned to frontend
   ↓
7. Frontend stores token in localStorage

---

8. User requests styling recommendations
   ↓
9. JwtAuthenticationFilter validates token
   ↓
10. StylistController receives form data
    ↓
11. UserProfileRequest DTO validated
    ↓
12. StylistService processes request
    ↓
13. StylistWorkflow executes AI workflow:
    
    LLM CALL 1: Analyze User Style
    ↓
    ├─ Extract JSON response
    ├─ Validate & parse StyleAnalysisResponse
    └─ Score: 0-100
    
    LLM CALL 2: Generate Recommendations (IF/ELSE)
    ├─ If styleMatchScore >= 70:
    │  └─ Advanced recommendations
    └─ Else:
       └─ Beginner-friendly guidance
    
    LLM CALL 3 (Optional): Generate Summary
    ↓
    └─ Final styling report
    
14. Save Recommendation to database
    ↓
15. Return response to frontend
```

---

## Frontend Architecture

### Technology Stack

**Framework:** Angular 18
**Language:** TypeScript 5.4
**Build Tool:** Angular CLI
**HTTP Client:** @angular/common/http
**State Management:** RxJS + Service-based
**Styling:** CSS3 + Responsive Design

### Component Structure

```
src/
├── app/
│   ├── models/
│   │   ├── auth.model.ts      - Auth interfaces
│   │   └── stylist.model.ts   - Stylist interfaces
│   ├── services/
│   │   ├── auth.service.ts    - Auth API calls
│   │   ├── stylist.service.ts - Stylist API calls
│   │   └── http.interceptor.ts - JWT injection
│   ├── guards/
│   │   ├── auth.guard.ts      - Route authentication
│   │   └── login.guard.ts     - Login page protection
│   ├── components/
│   │   └── navbar/            - Navigation component
│   ├── pages/
│   │   ├── login/             - Login page
│   │   ├── signup/            - Signup page
│   │   ├── dashboard/         - Home/dashboard
│   │   ├── stylist/           - Style form page
│   │   ├── recommendations/   - Recommendations list
│   │   └── recommendation-detail/ - Single recommendation
│   ├── app.component.*        - Root component
│   ├── app.routes.ts          - Routing configuration
│   └── environments/          - Environment configs
├── index.html                 - HTML entry point
├── main.ts                    - Bootstrap file
└── styles.css                 - Global styles
```

### Data Flow (Frontend)

```
User Input (Form)
    ↓
Form Validation (Reactive Forms)
    ↓
Component calls Service
    ↓
Service makes HTTP POST request
    ↓
HttpConfigInterceptor adds JWT token
    ↓
Request sent to backend
    ↓
Response received
    ↓
Parse response & extract data
    ↓
Update component state/BehaviorSubject
    ↓
Template renders updated data
    ↓
User sees results
```

---

## AI Workflow (Detailed)

### Workflow Architecture

```
┌─────────────────────────────────────────────────────┐
│  StylistWorkflow Component                          │
│  ┌───────────────────────────────────────────────┐  │
│  │ analyzeStyle(userProfile)                     │  │
│  │  - Build analysis prompt                      │  │
│  │  - Call HuggingFaceClient (LLM CALL 1)       │  │
│  │  - Extract & parse JSON response             │  │
│  │  - Return StyleAnalysisResponse               │  │
│  └───────────────────────────────────────────────┘  │
│                                                      │
│  ┌───────────────────────────────────────────────┐  │
│  │ generateOutfitRecommendations(profile, analysis)│
│  │  - Build outfit prompt                        │  │
│  │  - IF/ELSE Logic:                            │  │
│  │    if (styleMatchScore >= 70) → Advanced     │  │
│  │    else → Beginner                           │  │
│  │  - Call HuggingFaceClient (LLM CALL 2)       │  │
│  │  - Return recommendations text               │  │
│  └───────────────────────────────────────────────┘  │
│                                                      │
│  ┌───────────────────────────────────────────────┐  │
│  │ generateSummaryReport(profile, analysis, recs) │
│  │  - Build summary prompt                       │  │
│  │  - Call HuggingFaceClient (LLM CALL 3)       │  │
│  │  - Return summary report                      │  │
│  └───────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────┘
         │
         │ Uses
         ▼
┌─────────────────────────────────────────────────────┐
│  HuggingFaceClient                                  │
│  ┌───────────────────────────────────────────────┐  │
│  │ generateResponseWithFallback(prompt, maxTokens)│
│  │  Try:                                         │  │
│  │    - Primary Model: Mistral-7B              │  │
│  │  Catch:                                       │  │
│  │    - Fallback Model: Gemma-2-2b             │  │
│  │                                              │  │
│  │  Returns: Generated text from LLM            │  │
│  └───────────────────────────────────────────────┘  │
│                                                      │
│  HTTP POST to: https://api-inference.huggingface.co│
│  Authorization: Bearer {HF_API_TOKEN}             │  │
└─────────────────────────────────────────────────────┘
```

### Prompt Engineering

**LLM CALL 1 - Style Analysis Prompt:**
```
Analyze the following user's fashion style and provide recommendations.
[User Profile Data]
Return ONLY a valid JSON object with these fields:
- fashion_persona: string
- body_fit_recommendation: string
- suitable_colors: string[]
- style_match_score: number (0-100)
- confidence_score: number (0-100)
- recommended_categories: string[]
```

**LLM CALL 2 - Outfit Recommendations Prompt (IF/ELSE):**
```
IF style_match_score >= 70:
  "Based on user's high confidence profile, provide ADVANCED 
   personalized outfit combinations including premium styling, 
   accessories, and expert tips."
ELSE:
  "Based on user's developing style, provide BEGINNER-FRIENDLY 
   outfit combinations with simple styling rules and easy 
   color matching tips."
```

**LLM CALL 3 - Summary Report Prompt:**
```
Create a brief stylist summary explaining:
1. Why these recommendations were selected
2. Overall style explanation  
3. Confidence-building styling tips
Keep the tone encouraging and positive.
```

---

## Database Design

### Entity Relationship Diagram

```
┌──────────────┐
│    User      │
├──────────────┤
│ id (PK)      │
│ email (UK)   │
│ password     │
│ firstName    │
│ lastName     │
│ role         │
│ isActive     │
│ createdAt    │
│ updatedAt    │
└────────┬─────┘
         │
         │ 1:1
         │
┌────────▼──────────────────┐
│   UserProfile             │
├───────────────────────────┤
│ id (PK)                   │
│ userId (FK, UK)           │
│ gender (ENUM)             │
│ ageGroup (ENUM)           │
│ bodyType (ENUM)           │
│ skinTone (ENUM)           │
│ budgetRange (ENUM)        │
│ occasionType (ENUM)       │
│ preferredColors (TEXT)    │
│ stylePreference (ENUM)    │
│ weather (ENUM)            │
│ confidenceLevel (ENUM)    │
│ favoriteBrands (TEXT)     │
│ fitPreference (ENUM)      │
│ wardrobePreferences (TEXT)│
│ createdAt                 │
│ updatedAt                 │
└─────────────────────────────┘

         ┌──────────────┐
         │    User      │
         └────────┬─────┘
                  │
                  │ 1:N
                  │
         ┌────────▼──────────────┐
         │  Recommendation       │
         ├───────────────────────┤
         │ id (PK)               │
         │ userId (FK)           │
         │ styleAnalysis (TEXT)  │
         │ outfitRecs (TEXT)     │
         │ summaryReport (TEXT)  │
         │ styleMatchScore       │
         │ confidenceScore       │
         │ numberOfLlmCalls      │
         │ isAdvancedRec         │
         │ huggingFaceModel      │
         │ createdAt             │
         └───────────────────────┘
```

### Indexing Strategy

```sql
-- User table
INDEX idx_email (email) -- Used for login queries

-- UserProfile table
INDEX idx_user_id (user_id) -- FK lookup

-- Recommendation table
INDEX idx_user_id (user_id) -- Filter by user
INDEX idx_created_at (created_at) -- Sort recommendations
INDEX idx_user_created (user_id, created_at) -- Combined
```

---

## Security Architecture

### Authentication Flow

```
1. User enters credentials
   ↓
2. Frontend sends POST /auth/login
   ↓
3. Backend validates credentials
   ├─ Check email exists
   └─ PasswordEncoder.matches(password, hash)
   ↓
4. If valid:
   ├─ Create JWT token
   ├─ Sign with secret key (HS512)
   └─ Return token with 24-hour expiration
   ↓
5. Frontend stores token in localStorage
   ↓
6. For subsequent requests:
   ├─ Inject token in Authorization header
   ├─ JwtAuthenticationFilter validates token
   ├─ Load UserDetails from database
   └─ Set SecurityContext for authorization
   ↓
7. If token invalid/expired:
   └─ Return 401 Unauthorized
```

### Authorization Flow

```
Authenticated Request
   ↓
JwtAuthenticationFilter extracts token
   ↓
JwtTokenProvider validates signature & expiration
   ↓
CustomUserDetailsService loads UserDetails
   ↓
SecurityContext stores Authentication
   ↓
Controller receives request (user authenticated)
   ↓
RoleBasedAuthorization checks permissions
   ├─ USER role → /api/user/*, /api/stylist/*
   └─ ADMIN role → /api/admin/*
   ↓
If authorized → Process request
If denied → Return 403 Forbidden
```

---

## Error Handling Strategy

### Exception Hierarchy

```
RuntimeException
├── ResourceNotFoundException
│   └─ Resource not found (404)
├── BadRequestException
│   └─ Invalid input (400)
└── AIServiceException
    └─ AI/Hugging Face error (500)

Global Exception Handler catches all
and returns standardized ApiResponse
```

### Validation Strategy

```
Frontend Validation:
├─ Reactive Forms validation
├─ Email format validation
├─ Password strength validation
└─ Required field validation

Backend Validation (Defense in Depth):
├─ @Valid annotation on DTOs
├─ @NotNull, @NotBlank, @Email, @Size
├─ Global exception handler catches violations
└─ Returns detailed error messages
```

---

## Performance Considerations

### Caching Strategy
- JWT tokens cached in memory
- User details cached per request
- Recommendations cached in database (retrieve only)

### Database Optimization
- Indexed queries on user_id and created_at
- LONGTEXT for large recommendation data
- Appropriate column types (ENUM for fixed values)

### API Response Optimization
- Gzip compression enabled
- JSON serialization optimized
- Lazy loading of relationships

---

## Deployment Architecture

### Development
```
LocalHost:4200 (Angular) → LocalHost:8080 (Spring Boot) → MySQL:3306
```

### Production
```
CDN → Load Balancer → Docker Container (Backend)
                   → Database Cluster (MySQL)
                   → Cache Layer (Redis optional)
```

### Docker Setup
```dockerfile
FROM openjdk:21-slim
COPY target/ai-stylist.jar app.jar
ENV DB_URL=jdbc:mysql://db:3306/aistyle
ENV HF_API_TOKEN=${HF_API_TOKEN}
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]
```

---

## Monitoring & Logging

### Logging Levels
```
ROOT: INFO
com.aistyle: DEBUG
Spring Security: DEBUG (only in dev)
Hibernate: DEBUG (only in dev)
```

### Key Metrics to Monitor
- API response times
- AI API call success/failure rate
- Database query performance
- JWT token validation rate
- User registration/login rate
- Recommendation generation success rate

---

## Security Best Practices Implemented

✅ Password hashing with BCrypt
✅ JWT token with signature validation
✅ HTTPS in production
✅ CORS restricted to specific origins
✅ Input validation on all endpoints
✅ SQL injection prevention (parameterized queries)
✅ XSS prevention (JSON serialization)
✅ Rate limiting on AI APIs
✅ Environment variable secret management
✅ Prepared statements via JPA/Hibernate
