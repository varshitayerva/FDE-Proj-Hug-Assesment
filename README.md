# AI-Stylist

An AI-powered fashion styling platform that recommends personalized outfit combinations based on user preferences, body type, occasion, and other fashion-related factors.

## Project Overview

AI-Stylist leverages advanced AI through Hugging Face Inference APIs to provide intelligent fashion recommendations. The application analyzes user style profiles and generates personalized outfit suggestions with styling explanations.

## Features

### Authentication & User Management
- User Signup with email validation
- User Login with JWT authentication
- Role-based access control (USER, ADMIN)
- Secure password encryption with BCrypt

### User Profile
Users can provide:
- Gender
- Age group
- Body type
- Skin tone
- Budget range
- Occasion type
- Preferred colors
- Style preference
- Weather conditions
- Fashion confidence level
- Favorite brands
- Clothing fit preference
- Current wardrobe preferences

### AI Stylist Engine
- Style analysis and persona detection
- Personalized outfit recommendations
- Color matching guidance
- Styling confidence assessment
- Advanced or beginner-friendly recommendations (based on confidence)
- Final summary report generation

### Admin Features
- Fashion category management
- Recommendation logs
- Analytics dashboard
- User management

## Technology Stack

### Backend
- Java 21
- Spring Boot (latest stable)
- Spring Security
- Spring Data JPA
- Hibernate
- Maven
- REST APIs

### Frontend
- Angular (latest stable)
- TypeScript
- HTML5 & CSS3
- Reactive Forms

### Database
- MySQL

### External APIs
- Hugging Face Inference API (Text Generation)

### DevOps & Deployment
- Docker
- Git & GitHub

## Project Structure

```
FDE-Proj-Hug/
├── backend/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/aistyle/
│   │   │   │   ├── controller/
│   │   │   │   ├── service/
│   │   │   │   ├── repository/
│   │   │   │   ├── entity/
│   │   │   │   ├── dto/
│   │   │   │   ├── config/
│   │   │   │   ├── security/
│   │   │   │   ├── exception/
│   │   │   │   ├── validation/
│   │   │   │   ├── ai/
│   │   │   │   ├── workflow/
│   │   │   │   └── util/
│   │   │   └── resources/
│   │   └── test/
│   └── pom.xml
├── frontend/
│   ├── src/
│   │   ├── app/
│   │   │   ├── components/
│   │   │   ├── services/
│   │   │   ├── pages/
│   │   │   ├── models/
│   │   │   └── guards/
│   │   ├── assets/
│   │   ├── environments/
│   │   ├── index.html
│   │   └── main.ts
│   ├── angular.json
│   ├── package.json
│   └── tsconfig.json
├── docs/
│   ├── ARCHITECTURE.md
│   ├── AI_WORKFLOW.md
│   ├── SECURITY_REVIEW.md
│   ├── PROMPTS.md
│   └── API_ENDPOINTS.md
├── .gitignore
├── README.md
└── SETUP_GUIDE.md
```

## Getting Started

### Prerequisites
- Java 21
- Maven 3.8+
- Node.js 18+ & npm
- MySQL 8.0+
- Docker (optional)
- Hugging Face API Token

### Environment Variables

Create `.env` files in both backend and frontend directories:

**Backend (.env)**
```
HF_API_TOKEN=your_hugging_face_api_token
DB_URL=jdbc:mysql://localhost:3306/aistyle
DB_USERNAME=aistyle_user
DB_PASSWORD=your_password
JWT_SECRET=your_jwt_secret
JWT_EXPIRATION=86400000
```

**Frontend (.env)**
```
API_BASE_URL=http://localhost:8080/api
```

### Database Setup

```bash
# Login to MySQL
mysql -u root -p

# Create database and user
CREATE DATABASE aistyle;
CREATE USER 'aistyle_user'@'localhost' IDENTIFIED BY 'your_password';
GRANT ALL PRIVILEGES ON aistyle.* TO 'aistyle_user'@'localhost';
FLUSH PRIVILEGES;
```

### Backend Setup

```bash
cd backend

# Install dependencies
mvn clean install

# Run the application
mvn spring-boot:run

# Run tests
mvn test
```

Backend runs on: `http://localhost:8080`

### Frontend Setup

```bash
cd frontend

# Install dependencies
npm install

# Run the application
ng serve

# Run tests
ng test

# Build for production
ng build --configuration production
```

Frontend runs on: `http://localhost:4200`

### Hugging Face API Setup

1. Create account at [huggingface.co](https://huggingface.co)
2. Generate API token in account settings
3. Add token to backend `.env` file

**Recommended Models:**
- Primary: `mistralai/Mistral-7B-Instruct-v0.3`
- Fallback: `google/gemma-2-2b-it`

## API Endpoints

See [docs/API_ENDPOINTS.md](docs/API_ENDPOINTS.md) for complete API documentation.

## AI Workflow

The application uses a 2-3 LLM call workflow:

1. **Style Analysis** - Analyzes user input and generates style profile
2. **Conditional Recommendations** - Based on confidence score:
   - If score ≥ 70: Advanced personalized recommendations
   - If score < 70: Beginner-friendly guidance
3. **Summary Report** (Optional) - Final styling summary

See [docs/AI_WORKFLOW.md](docs/AI_WORKFLOW.md) for detailed workflow documentation.

## Testing

```bash
# Backend unit tests
cd backend && mvn test

# Backend integration tests
mvn verify

# Frontend unit tests
cd frontend && ng test

# Frontend e2e tests
ng e2e
```

## Security

This application implements:
- JWT token-based authentication
- BCrypt password encryption
- SQL injection prevention
- XSS protection
- Input validation and sanitization
- Rate limiting on AI APIs
- Secure token storage

See [docs/SECURITY_REVIEW.md](docs/SECURITY_REVIEW.md) for detailed security review.

## Docker Deployment

```bash
# Build backend Docker image
cd backend
docker build -t aistyle-backend .

# Build frontend Docker image
cd frontend
docker build -t aistyle-frontend .

# Run with docker-compose
docker-compose up
```

## Troubleshooting

### Common Issues

**1. Database Connection Error**
- Verify MySQL is running
- Check `.env` credentials
- Ensure database is created

**2. Hugging Face API Error**
- Verify API token is valid
- Check token has appropriate permissions
- Verify model name is correct

**3. CORS Issues**
- Check backend CORS configuration
- Verify frontend API_BASE_URL
- Check Spring Security configuration

**4. Angular Build Errors**
- Delete `node_modules` and `dist`
- Run `npm install` again
- Clear Angular cache: `ng cache clean`

## Documentation

- [Architecture Documentation](docs/ARCHITECTURE.md)
- [AI Workflow Documentation](docs/AI_WORKFLOW.md)
- [Security Review](docs/SECURITY_REVIEW.md)
- [Prompt Engineering](docs/PROMPTS.md)
- [API Endpoints](docs/API_ENDPOINTS.md)

## Future Improvements

- Recommendation history tracking
- Wardrobe inventory management
- Virtual try-on with AR
- Social features (share recommendations)
- Advanced analytics
- Mobile app (React Native)
- Real-time chat styling assistant
- Image-based style detection

## License

Proprietary - All Rights Reserved

## Contact

Developer: l.venkat@globallogic.com
