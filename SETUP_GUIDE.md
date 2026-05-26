# AI-Stylist Complete Setup Guide

## Prerequisites

Before starting, ensure you have installed:

- **Java 21** ([https://www.oracle.com/java/technologies/downloads/](https://www.oracle.com/java/technologies/downloads/))
- **Maven 3.8+** ([https://maven.apache.org/download.cgi](https://maven.apache.org/download.cgi))
- **Node.js 18+** ([https://nodejs.org/](https://nodejs.org/))
- **MySQL 8.0+** ([https://dev.mysql.com/downloads/mysql/](https://dev.mysql.com/downloads/mysql/))
- **Git** ([https://git-scm.com/](https://git-scm.com/))

### Verify Installations

```bash
java -version           # Should show Java 21.x
mvn -version           # Should show Maven 3.8+
node --version         # Should show Node 18+
mysql --version        # Should show MySQL 8.0+
git --version          # Should show Git version
```

---

## Step 1: Hugging Face API Token Setup

1. Go to [huggingface.co](https://huggingface.co) and create a free account
2. Navigate to **Account → Settings → Access Tokens**
3. Click **New token**
4. Name it "ai-stylist-dev"
5. Set type to **Read** (only needed for inference)
6. Copy the token (starts with `hf_`)
7. Save it securely (you'll need it later)

---

## Step 2: Database Setup

### Option A: MySQL Command Line

```bash
# Login to MySQL
mysql -u root -p

# Create database
CREATE DATABASE aistyle;

# Create user
CREATE USER 'aistyle_user'@'localhost' IDENTIFIED BY 'aistyle_password';

# Grant privileges
GRANT ALL PRIVILEGES ON aistyle.* TO 'aistyle_user'@'localhost';

# Apply changes
FLUSH PRIVILEGES;

# Verify
SHOW DATABASES;
EXIT;
```

### Option B: MySQL Workbench (GUI)

1. Open MySQL Workbench
2. Connect to local MySQL server
3. Right-click **Databases** → **Create New Database**
4. Name: `aistyle`
5. Click **Apply**
6. Right-click **Users and Privileges**
7. Create new user: `aistyle_user`
8. Set password: `aistyle_password`
9. Assign all privileges on `aistyle` database

### Option C: Docker MySQL (Recommended)

```bash
# Run MySQL in Docker
docker run --name aistyle-mysql \
  -e MYSQL_DATABASE=aistyle \
  -e MYSQL_USER=aistyle_user \
  -e MYSQL_PASSWORD=aistyle_password \
  -e MYSQL_ROOT_PASSWORD=root_password \
  -p 3306:3306 \
  -d mysql:8.0

# Verify connection
mysql -h localhost -u aistyle_user -p aistyle_password aistyle -e "SELECT 1;"
```

---

## Step 3: Backend Setup

### 3.1 Clone/Navigate to Project

```bash
cd FDE-Proj-Hug/backend
```

### 3.2 Create Environment Variables

```bash
# Copy example to .env
cp .env.example .env

# Edit .env with your values
# Windows:
notepad .env
# macOS/Linux:
nano .env
```

**Update these values:**
```
DB_URL=jdbc:mysql://localhost:3306/aistyle
DB_USERNAME=aistyle_user
DB_PASSWORD=aistyle_password
HF_API_TOKEN=hf_your_token_here
JWT_SECRET=your_super_secret_key_at_least_32_chars
```

### 3.3 Build Backend

```bash
# Download dependencies (might take 2-3 minutes)
mvn clean install

# Expected output:
# [INFO] BUILD SUCCESS
# [INFO] Total time: XX.XXXs
```

### 3.4 Run Backend

```bash
# Start Spring Boot application
mvn spring-boot:run

# Expected output:
# Started AiStylistApplication in XX.XXX seconds
# INFO: Tomcat started on port(s): 8080 (http)
```

**Verify Backend is Running:**
```bash
# In new terminal window
curl http://localhost:8080/api/auth/health

# Should return:
# {"success":true,"message":"Auth service is healthy","data":"OK","statusCode":200}
```

---

## Step 4: Frontend Setup

### 4.1 Navigate to Frontend

```bash
cd ../frontend
```

### 4.2 Install Dependencies

```bash
# Install npm packages (might take 1-2 minutes)
npm install

# Expected output:
# added XXX packages
```

### 4.3 Create Environment File (Optional)

```bash
# Frontend uses environment.ts by default
# For custom API endpoint:
# Create src/environments/.env or update environment.ts
```

### 4.4 Run Frontend Dev Server

```bash
# Start Angular development server
npm start

# Or using ng serve:
ng serve

# Expected output:
# ✔ Compiled successfully.
# ✔ Compiled successfully. [XX.XXXs]
# Application bundle generated successfully.
# Watch mode enabled.
# Local: http://localhost:4200/
```

---

## Step 5: Access Application

### Frontend
- **URL:** [http://localhost:4200](http://localhost:4200)
- **Signup:** Create a new account
- **Login:** Use your credentials
- **Dashboard:** View recommendations or start styling

### Backend API
- **Health Check:** [http://localhost:8080/api/auth/health](http://localhost:8080/api/auth/health)
- **API Docs:** See `docs/API_ENDPOINTS.md`

### Database
```bash
# Connect to database
mysql -h localhost -u aistyle_user -p aistyle_password aistyle

# View tables
SHOW TABLES;

# View users
SELECT * FROM users;
```

---

## Step 6: Test the Application

### 6.1 Signup

1. Go to [http://localhost:4200](http://localhost:4200)
2. Click **Signup**
3. Enter:
   - **First Name:** John
   - **Last Name:** Doe
   - **Email:** john@example.com
   - **Password:** Password123
   - **Confirm:** Password123
4. Click **Sign Up**

### 6.2 Login & Get Recommendations

1. Fill out the Stylist form on dashboard
2. Click **Get Styling Recommendations**
3. Wait 15-20 seconds for AI to process
4. View recommendations

### 6.3 Test API Directly (Postman/cURL)

```bash
# 1. Signup
curl -X POST http://localhost:8080/api/auth/signup \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "Password123",
    "confirmPassword": "Password123",
    "firstName": "Jane",
    "lastName": "Smith"
  }'

# Copy the returned token

# 2. Get Recommendations
curl -X POST http://localhost:8080/api/stylist/recommend \
  -H "Authorization: Bearer YOUR_TOKEN_HERE" \
  -H "Content-Type: application/json" \
  -d '{
    "gender": "FEMALE",
    "ageGroup": "TWENTIES",
    "bodyType": "HOURGLASS",
    "skinTone": "MEDIUM",
    "budgetRange": "MODERATE",
    "occasionType": "CASUAL",
    "preferredColors": "Blue, White",
    "stylePreference": "CLASSIC",
    "weather": "WARM",
    "confidenceLevel": "MEDIUM",
    "favoriteBrands": "Zara",
    "fitPreference": "REGULAR",
    "wardrobePreferences": "Comfortable"
  }'
```

---

## Troubleshooting

### Frontend Issues

#### Port 4200 Already in Use
```bash
# Kill the process on port 4200
# Windows:
netstat -ano | findstr :4200
taskkill /PID <PID> /F

# macOS/Linux:
lsof -i :4200
kill -9 <PID>

# Or use different port:
ng serve --port 4201
```

#### CORS Error
```
Access to XMLHttpRequest blocked by CORS policy
```
**Solution:** Verify backend is running on port 8080

#### Module Not Found Errors
```bash
# Clear node_modules and reinstall
rm -rf node_modules package-lock.json
npm install
```

#### Angular Compilation Error
```bash
# Clear Angular cache
ng cache clean
npm install
ng serve
```

---

### Backend Issues

#### Port 8080 Already in Use
```bash
# Find process on 8080
# Windows:
netstat -ano | findstr :8080
taskkill /PID <PID> /F

# macOS/Linux:
lsof -i :8080
kill -9 <PID>

# Or use different port:
mvn spring-boot:run -Dspring-boot.run.arguments="--server.port=8081"
```

#### Database Connection Error
```
Unable to connect to localhost:3306
```
**Solution:** 
1. Verify MySQL is running
2. Check credentials in .env
3. Verify database exists: `SHOW DATABASES;`
4. Test connection: `mysql -h localhost -u aistyle_user -p aistyle_password aistyle`

#### HF_API_TOKEN Error
```
AI service error: Could not authenticate to Hugging Face
```
**Solution:**
1. Verify API token in `.env`
2. Token should start with `hf_`
3. Verify token has Read permissions
4. Test directly: `curl -H "Authorization: Bearer hf_..." https://api-inference.huggingface.co/models/status`

#### Maven Build Failure
```bash
# Clear Maven cache
mvn clean install -U

# If still failing, check Java version
java -version  # Must be 21+

# Download specific dependencies
mvn dependency:resolve
```

---

### Database Issues

#### Database Reset (Warning: Deletes all data)
```bash
# Login to MySQL
mysql -h localhost -u aistyle_user -p aistyle_password aistyle

# Drop tables
DROP TABLE recommendations;
DROP TABLE user_profiles;
DROP TABLE users;

# Exit
EXIT;

# Restart backend to recreate tables
mvn spring-boot:run
```

#### View Database State
```bash
# Connect to database
mysql -h localhost -u aistyle_user -p aistyle_password aistyle

# View all tables
SHOW TABLES;

# View users
SELECT id, email, first_name, last_name, role FROM users;

# View recommendations
SELECT id, user_id, style_match_score, confidence_score, created_at FROM recommendations;

# Count records
SELECT COUNT(*) FROM users;
SELECT COUNT(*) FROM recommendations;
```

---

## Development Workflow

### Making Changes

#### Backend Changes
```bash
# Edit Java files
# Maven will auto-recompile if using spring-boot:run with -Dspring-boot.run.watch-classpath

# Or restart manually:
# 1. Stop backend (Ctrl+C)
# 2. Run: mvn spring-boot:run
```

#### Frontend Changes
```bash
# Edit Angular files
# Hot reload happens automatically
# Just refresh browser (Ctrl+R)

# If hot reload doesn't work:
# 1. Stop ng serve (Ctrl+C)
# 2. Run: ng serve
```

### Database Changes (JPA Migrations)

For changes to entity fields:
```java
// 1. Update @Entity class
@Column(columnDefinition = "VARCHAR(255)")
private String newField;

// 2. Restart backend
// Hibernate auto-updates schema (ddl-auto: update)

// 3. Verify in database
DESCRIBE users;
```

---

## Deployment (Production)

### 1. Build for Production

**Backend:**
```bash
cd backend
mvn clean package -DskipTests

# Creates: target/ai-stylist-1.0.0.jar
```

**Frontend:**
```bash
cd frontend
ng build --configuration production

# Creates: dist/ai-stylist/
```

### 2. Docker Deployment

Create `Dockerfile` in backend:
```dockerfile
FROM openjdk:21-slim
COPY target/ai-stylist-1.0.0.jar app.jar
ENV JAVA_OPTS="-Xmx512m"
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app.jar"]
```

Build and run:
```bash
# Build image
docker build -t aistyle-backend:latest .

# Run container
docker run -d \
  -p 8080:8080 \
  -e DB_URL=jdbc:mysql://db-host:3306/aistyle \
  -e HF_API_TOKEN=hf_... \
  --name aistyle-backend \
  aistyle-backend:latest
```

### 3. Environment Variables (Production)

```bash
# Update to production values
DB_URL=jdbc:mysql://prod-db-host:3306/aistyle
DB_USERNAME=prod_user
DB_PASSWORD=very_strong_password_here
HF_API_TOKEN=hf_prod_token
JWT_SECRET=production_secret_key_at_least_64_chars
SERVER_PORT=8080
LOGGING_LEVEL=INFO  # Not DEBUG
```

---

## Useful Commands

### Backend

```bash
# Build
mvn clean install

# Run
mvn spring-boot:run

# Run tests
mvn test

# Build JAR
mvn clean package

# View dependencies
mvn dependency:tree

# Check for vulnerabilities
mvn dependency-check:check
```

### Frontend

```bash
# Install dependencies
npm install

# Run dev server
ng serve

# Build production
ng build --configuration production

# Run tests
ng test

# Run linting
ng lint

# Check for vulnerabilities
npm audit
```

### Database

```bash
# Login
mysql -u aistyle_user -p aistyle_password

# Show databases
SHOW DATABASES;

# Use database
USE aistyle;

# Show tables
SHOW TABLES;

# Describe table
DESCRIBE users;

# View data
SELECT * FROM users;
SELECT * FROM recommendations;

# Export database
mysqldump -u aistyle_user -p aistyle_password aistyle > backup.sql

# Import database
mysql -u aistyle_user -p aistyle_password aistyle < backup.sql
```

### Git

```bash
# Check status
git status

# View logs
git log --oneline

# Create branch
git checkout -b feature/new-feature

# Commit changes
git commit -m "Add new feature"

# Push to GitHub
git push origin main
```

---

## Next Steps

1. ✅ Setup development environment
2. ✅ Test application locally
3. ✅ Review documentation in `docs/` folder
4. ✅ Explore API endpoints with Postman
5. 📝 Create test data and test workflows
6. 🚀 Deploy to production
7. 📊 Monitor application and gather feedback

---

## Support & Documentation

- **API Documentation:** `docs/API_ENDPOINTS.md`
- **Architecture:** `docs/ARCHITECTURE.md`
- **AI Workflow:** `docs/AI_WORKFLOW.md`
- **Security:** `docs/SECURITY_REVIEW.md`
- **Prompts:** `docs/PROMPTS.md`
- **README:** `README.md`

---

## Common Questions

**Q: Can I use a different database?**
A: The code uses Hibernate JPA, so PostgreSQL, Oracle, SQL Server all work. Update `application.yml` and pom.xml.

**Q: How do I change the API token?**
A: Update `HF_API_TOKEN` in `.env` file and restart backend.

**Q: Can I run on a different port?**
A: Yes, set `SERVER_PORT=8081` in .env or via command line.

**Q: How do I reset my password?**
A: Future feature - currently requires database update or account recreation.

**Q: Is production-ready?**
A: Yes, but add rate limiting, HTTPS, better error handling, and monitoring before production.

---

**Setup Complete!** 🎉

Your AI-Stylist application is ready for development and testing. Happy coding!
