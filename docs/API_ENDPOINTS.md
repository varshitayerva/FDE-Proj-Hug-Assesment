# AI-Stylist API Endpoints Documentation

## Base URL
- **Development:** `http://localhost:8080/api`
- **Production:** `https://api.aistyle.com/api`

## Authentication

All protected endpoints require a JWT token in the Authorization header:
```
Authorization: Bearer <jwt_token>
```

---

## Authentication Endpoints

### 1. User Signup
**Endpoint:** `POST /auth/signup`

**Description:** Register a new user account

**Request Body:**
```json
{
  "email": "user@example.com",
  "password": "SecurePassword123",
  "confirmPassword": "SecurePassword123",
  "firstName": "John",
  "lastName": "Doe"
}
```

**Response (201 Created):**
```json
{
  "success": true,
  "message": "User registered successfully",
  "data": {
    "token": "eyJhbGciOiJIUzUxMiJ9...",
    "userId": 1,
    "email": "user@example.com",
    "firstName": "John",
    "lastName": "Doe",
    "role": "USER"
  },
  "statusCode": 201
}
```

**Validation Rules:**
- Email must be valid format
- Password must be at least 6 characters
- Passwords must match
- Email must be unique
- First/Last names must be 2-50 characters

---

### 2. User Login
**Endpoint:** `POST /auth/login`

**Description:** Authenticate user and get JWT token

**Request Body:**
```json
{
  "email": "user@example.com",
  "password": "SecurePassword123"
}
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Login successful",
  "data": {
    "token": "eyJhbGciOiJIUzUxMiJ9...",
    "userId": 1,
    "email": "user@example.com",
    "firstName": "John",
    "lastName": "Doe",
    "role": "USER"
  },
  "statusCode": 200
}
```

**Error Response (400 Bad Request):**
```json
{
  "success": false,
  "message": "Invalid email or password",
  "statusCode": 400,
  "error": "Bad request"
}
```

---

### 3. Health Check
**Endpoint:** `GET /auth/health`

**Description:** Check if auth service is healthy

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Auth service is healthy",
  "data": "OK",
  "statusCode": 200
}
```

---

## Stylist Endpoints (Require Authentication)

### 4. Get Stylist Recommendations
**Endpoint:** `POST /stylist/recommend`

**Description:** Generate AI-powered styling recommendations based on user profile

**Headers:**
```
Authorization: Bearer <jwt_token>
Content-Type: application/json
```

**Request Body:**
```json
{
  "gender": "FEMALE",
  "ageGroup": "TWENTIES",
  "bodyType": "HOURGLASS",
  "skinTone": "MEDIUM",
  "budgetRange": "MODERATE",
  "occasionType": "CASUAL",
  "preferredColors": "Blue, White, Earth Tones",
  "stylePreference": "CLASSIC",
  "weather": "WARM",
  "confidenceLevel": "MEDIUM",
  "favoriteBrands": "Zara, H&M",
  "fitPreference": "REGULAR",
  "wardrobePreferences": "Comfortable and stylish"
}
```

**Enum Values:**
- **Gender:** MALE, FEMALE, NON_BINARY, PREFER_NOT_TO_SAY
- **AgeGroup:** TEENS, TWENTIES, THIRTIES, FORTIES, FIFTIES_PLUS
- **BodyType:** PETITE, PEAR, HOURGLASS, RECTANGLE, APPLE, INVERTED_TRIANGLE
- **SkinTone:** FAIR, LIGHT, MEDIUM, OLIVE, DEEP, DARK
- **BudgetRange:** BUDGET, MODERATE, PREMIUM, LUXURY
- **OccasionType:** CASUAL, BUSINESS, PARTY, EVENING, WEEKEND, VACATION
- **StylePreference:** CLASSIC, TRENDY, SPORTY, BOHEMIAN, MINIMALIST, VINTAGE, EDGY, ROMANTIC
- **WeatherCondition:** HOT, WARM, COOL, COLD, RAINY, SNOWY
- **ConfidenceLevel:** VERY_LOW, LOW, MEDIUM, HIGH, VERY_HIGH
- **FitPreference:** SLIM, REGULAR, LOOSE, OVERSIZED

**Response (201 Created):**
```json
{
  "success": true,
  "message": "Stylist recommendations generated successfully",
  "data": {
    "recommendationId": 42,
    "styleAnalysis": {
      "fashionPersona": "Contemporary Elegant",
      "bodyFitRecommendation": "Well-fitted and tailored pieces",
      "suitableColors": ["Navy", "White", "Cream", "Rust"],
      "styleMatchScore": 85,
      "confidenceScore": 78,
      "recommendedCategories": ["Casual", "Business Casual", "Evening wear"]
    },
    "outfitRecommendations": "Based on your style profile...",
    "summaryReport": "Your fashion persona suggests...",
    "numberOfLlmCalls": 3,
    "isAdvancedRecommendation": true
  },
  "statusCode": 201
}
```

**AI Workflow:**
- **LLM Call 1:** Analyzes user profile and generates style analysis
- **LLM Call 2 (IF/ELSE):**
  - If styleMatchScore >= 70: Advanced personalized recommendations
  - Else: Beginner-friendly guidance
- **LLM Call 3:** Optional summary report

---

### 5. Get User Recommendations
**Endpoint:** `GET /stylist/recommendations`

**Description:** Retrieve all styling recommendations for the authenticated user

**Headers:**
```
Authorization: Bearer <jwt_token>
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Recommendations retrieved successfully",
  "data": [
    {
      "id": 42,
      "userId": 1,
      "styleAnalysis": "{...}",
      "outfitRecommendations": "...",
      "summaryReport": "...",
      "styleMatchScore": 85,
      "confidenceScore": 78,
      "numberOfLlmCalls": 3,
      "isAdvancedRecommendation": true,
      "createdAt": "2026-05-26T14:30:00"
    }
  ],
  "statusCode": 200
}
```

---

### 6. Get Recommendation by ID
**Endpoint:** `GET /stylist/recommendation/{id}`

**Description:** Retrieve a specific recommendation by ID

**Headers:**
```
Authorization: Bearer <jwt_token>
```

**URL Parameters:**
- `id` (required): Recommendation ID

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Recommendation retrieved successfully",
  "data": {
    "id": 42,
    "userId": 1,
    "styleAnalysis": "{...}",
    "outfitRecommendations": "...",
    "summaryReport": "...",
    "styleMatchScore": 85,
    "confidenceScore": 78,
    "numberOfLlmCalls": 3,
    "isAdvancedRecommendation": true,
    "createdAt": "2026-05-26T14:30:00"
  },
  "statusCode": 200
}
```

**Error Response (404 Not Found):**
```json
{
  "success": false,
  "message": "Recommendation not found",
  "statusCode": 404,
  "error": "Resource not found"
}
```

**Error Response (403 Forbidden - Unauthorized Access):**
```json
{
  "success": false,
  "message": "Unauthorized access to recommendation",
  "statusCode": 403,
  "error": "Resource not found"
}
```

---

## Error Responses

### 400 Bad Request
```json
{
  "success": false,
  "message": "Validation failed",
  "data": {
    "email": "Email should be valid",
    "password": "Password must be at least 6 characters"
  },
  "statusCode": 400,
  "error": "Validation error"
}
```

### 401 Unauthorized
```json
{
  "success": false,
  "message": "Token expired or invalid",
  "statusCode": 401,
  "error": "Unauthorized"
}
```

### 500 Internal Server Error
```json
{
  "success": false,
  "message": "An unexpected error occurred",
  "statusCode": 500,
  "error": "Error details"
}
```

---

## API Usage Examples

### Using cURL

**Signup:**
```bash
curl -X POST http://localhost:8080/api/auth/signup \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "password": "Password123",
    "confirmPassword": "Password123",
    "firstName": "John",
    "lastName": "Doe"
  }'
```

**Login:**
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "password": "Password123"
  }'
```

**Get Recommendations (with token):**
```bash
curl -X GET http://localhost:8080/api/stylist/recommendations \
  -H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9..."
```

### Using JavaScript/Fetch

```javascript
// Signup
const signupResponse = await fetch('http://localhost:8080/api/auth/signup', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({
    email: 'user@example.com',
    password: 'Password123',
    confirmPassword: 'Password123',
    firstName: 'John',
    lastName: 'Doe'
  })
});

const data = await signupResponse.json();
const token = data.data.token;

// Get Recommendations
const recsResponse = await fetch('http://localhost:8080/api/stylist/recommendations', {
  headers: { 'Authorization': `Bearer ${token}` }
});

const recommendations = await recsResponse.json();
console.log(recommendations.data);
```

---

## Rate Limiting

Currently no rate limiting is enforced, but consider implementing:
- 100 requests per 15 minutes per IP address
- 50 AI recommendation requests per day per user

---

## Token Details

JWT Token Structure:
```json
{
  "sub": "user@example.com",
  "userId": 1,
  "iat": 1620000000,
  "exp": 1620086400
}
```

**Token Expiration:** 24 hours (configurable via JWT_EXPIRATION env var)

---

## CORS Policy

**Allowed Origins:**
- http://localhost:4200 (Angular dev)
- http://localhost:3000 (Alternative dev)

**Allowed Methods:** GET, POST, PUT, DELETE, OPTIONS

**Allowed Headers:** * (All headers)

**Credentials:** true

---

## Database Schema

### Users Table
```sql
CREATE TABLE users (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  email VARCHAR(255) UNIQUE NOT NULL,
  password VARCHAR(255) NOT NULL,
  first_name VARCHAR(50) NOT NULL,
  last_name VARCHAR(50) NOT NULL,
  role ENUM('USER', 'ADMIN') NOT NULL DEFAULT 'USER',
  is_active BOOLEAN NOT NULL DEFAULT true,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

### User Profiles Table
```sql
CREATE TABLE user_profiles (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT UNIQUE NOT NULL,
  gender VARCHAR(20),
  age_group VARCHAR(20),
  body_type VARCHAR(30),
  skin_tone VARCHAR(20),
  budget_range VARCHAR(20),
  occasion_type VARCHAR(20),
  preferred_colors TEXT,
  style_preference VARCHAR(20),
  weather VARCHAR(20),
  confidence_level VARCHAR(20),
  favorite_brands TEXT,
  fit_preference VARCHAR(20),
  wardrobe_preferences TEXT,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (user_id) REFERENCES users(id)
);
```

### Recommendations Table
```sql
CREATE TABLE recommendations (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  style_analysis LONGTEXT,
  outfit_recommendations LONGTEXT,
  summary_report LONGTEXT,
  style_match_score INT,
  confidence_score INT,
  used_prompt LONGTEXT,
  hugging_face_model_used VARCHAR(255),
  number_of_llm_calls INT,
  is_advanced_recommendation BOOLEAN,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (user_id) REFERENCES users(id),
  INDEX idx_user_id (user_id),
  INDEX idx_created_at (created_at)
);
```

---

## Testing the API

Use Postman, Insomnia, or Thunder Client to test endpoints:

1. **Create User:** POST /auth/signup
2. **Login:** POST /auth/login (get token)
3. **Get Recommendations:** POST /stylist/recommend (with token)
4. **View Recommendations:** GET /stylist/recommendations (with token)
5. **View Specific:** GET /stylist/recommendation/{id} (with token)

---

## Troubleshooting

**401 Unauthorized:**
- Token expired (renew by logging in again)
- Invalid token format
- Missing Authorization header

**400 Bad Request:**
- Invalid JSON in request body
- Missing required fields
- Invalid enum values

**500 Internal Server Error:**
- Hugging Face API unavailable
- Database connection error
- Invalid configuration

Check backend logs: `docker logs ai-stylist-backend`
