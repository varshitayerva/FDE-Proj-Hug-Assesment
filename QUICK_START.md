# AI-Stylist Quick Start Guide

## Prerequisites Check

### ✅ Your System Has:
- **Java 21:** OpenJDK Corretto 21.0.10
- **Node.js:** v24.14.0
- **npm:** Included with Node.js
- **Git:** Available

### ⚠️ Still Needed:
- **Maven:** Required for backend build
- **MySQL:** Required for database

---

## Step 1: Install Maven

### Windows (Using Chocolatey or Manual)

**Option A: Using Chocolatey (recommended)**
```bash
choco install maven
```

**Option B: Manual Download**
1. Download from: https://maven.apache.org/download.cgi
2. Extract to: `C:\Program Files\apache-maven-3.9.0`
3. Add to PATH: `C:\Program Files\apache-maven-3.9.0\bin`
4. Verify: `mvn -v`

**Option C: Using Windows Package Manager**
```bash
winget install Maven.Maven
```

### After Installation:
```bash
mvn -v  # Should show Maven 3.8 or higher
```

---

## Step 2: Install MySQL

### Windows Option A: MySQL Installer
1. Download from: https://dev.mysql.com/downloads/windows/
2. Run installer
3. Choose "Server only" option
4. Configure MySQL Server (port 3306)
5. Create service
6. Set root password
7. Remember root password

### Windows Option B: Chocolatey
```bash
choco install mysql
```

### Windows Option C: Docker (Easiest)
```bash
# Install Docker Desktop from https://www.docker.com/products/docker-desktop/

# Then run:
docker run --name aistyle-mysql ^
  -e MYSQL_DATABASE=aistyle ^
  -e MYSQL_USER=aistyle_user ^
  -e MYSQL_PASSWORD=aistyle_password ^
  -e MYSQL_ROOT_PASSWORD=root_password ^
  -p 3306:3306 ^
  -d mysql:8.0
```

### Verify MySQL is Running:
```bash
mysql -h localhost -u root -p -e "SHOW DATABASES;"
# Enter root password when prompted
```

---

## Step 3: Create Database

```bash
# Login to MySQL
mysql -h localhost -u root -p

# When prompted, enter root password
# Then run in MySQL shell:

CREATE DATABASE aistyle;
CREATE USER 'aistyle_user'@'localhost' IDENTIFIED BY 'aistyle_password';
GRANT ALL PRIVILEGES ON aistyle.* TO 'aistyle_user'@'localhost';
FLUSH PRIVILEGES;
EXIT;
```

### Verify Database Created:
```bash
mysql -h localhost -u aistyle_user -paistyle_password aistyle -e "SHOW TABLES;"
# Should return: Tables_in_aistyle (empty list initially)
```

---

## Step 4: Setup Backend

### 4.1 Create Environment File

```bash
cd FDE-Proj-Hug/backend

# Create .env file
echo DB_URL=jdbc:mysql://localhost:3306/aistyle > .env
echo DB_USERNAME=aistyle_user >> .env
echo DB_PASSWORD=aistyle_password >> .env
echo HF_API_TOKEN=hf_YOUR_TOKEN_HERE >> .env
echo JWT_SECRET=your_super_secret_jwt_key_change_this_in_production >> .env
echo JWT_EXPIRATION=86400000 >> .env
echo SERVER_PORT=8080 >> .env

# Verify .env file
type .env
```

### ⚠️ IMPORTANT: Get Hugging Face Token

1. Go to: https://huggingface.co
2. Click **Sign Up** (or Login if you have account)
3. Go to **Settings → Access Tokens**
4. Click **New token**
5. Name: "ai-stylist-dev"
6. Type: "Read"
7. Copy token (starts with `hf_`)
8. Update .env:
```bash
# Edit .env and replace:
HF_API_TOKEN=hf_YOUR_TOKEN_HERE
# With your actual token
```

### 4.2 Build Backend

```bash
cd FDE-Proj-Hug/backend

# Download dependencies (takes 2-3 minutes first time)
mvn clean install

# Expected output:
# [INFO] BUILD SUCCESS
# [INFO] Total time: XX.XXXs
```

### 4.3 Run Backend

```bash
# Start Spring Boot
mvn spring-boot:run

# Expected output:
# Started AiStylistApplication in XX.XXX seconds
# INFO 8080: Tomcat started on port(s): 8080
```

**Leave this terminal running!** Backend is now running on port 8080.

### Test Backend:
```bash
# In a NEW terminal window:
curl http://localhost:8080/api/auth/health

# Should return:
# {"success":true,"message":"Auth service is healthy","data":"OK","statusCode":200}
```

---

## Step 5: Setup Frontend

### 5.1 Install Dependencies

```bash
cd FDE-Proj-Hug/frontend

# Install npm packages (takes 1-2 minutes first time)
npm install

# Expected output:
# added XXX packages in XX.XXs
```

### 5.2 Run Frontend

```bash
# Start Angular dev server
ng serve

# Or if ng command not found:
npx ng serve

# Expected output:
# ✔ Compiled successfully
# Application bundle generated successfully
# Watch mode enabled
# Local: http://localhost:4200/
```

**Leave this terminal running!** Frontend is now running on port 4200.

---

## Step 6: Access Application

### Open in Browser

1. Go to: **http://localhost:4200**
2. You should see the login page
3. Click **Signup** to create an account

---

## Step 7: Test Signup

### Create Account

1. **Email:** test@example.com
2. **First Name:** John
3. **Last Name:** Doe
4. **Password:** Password123
5. **Confirm Password:** Password123
6. Click **Sign Up**

### Expected Result:
- ✅ Account created
- ✅ Redirected to Dashboard
- ✅ See "Welcome, John!" message

---

## Step 8: Test AI Stylist Feature

### Fill Stylist Form

1. Click **"Get Styled"** button (or "Get Styling Recommendations")
2. Fill the form:

**Personal Information:**
- Gender: FEMALE
- Age Group: TWENTIES

**Body & Style:**
- Body Type: HOURGLASS
- Skin Tone: MEDIUM
- Style Preference: CLASSIC
- Fit Preference: REGULAR

**Preferences & Occasion:**
- Budget Range: MODERATE
- Occasion Type: CASUAL
- Weather: WARM
- Fashion Confidence Level: MEDIUM

**Additional Information:**
- Preferred Colors: Blue, White, Earth Tones
- Favorite Brands: Zara, H&M (optional)
- Wardrobe Preferences: Comfortable and stylish (optional)

3. Click **"Get Styling Recommendations"**

### Expected Result:
- ⏳ Loading message appears
- ⏱️ Wait 15-20 seconds (AI processing)
- 📋 Results appear:
  - **Fashion Persona:** e.g., "Contemporary Elegant"
  - **Body Fit Recommendation:** Tailored pieces guidance
  - **Suitable Colors:** List of recommended colors
  - **Style Match Score:** 0-100 percentage
  - **Confidence Score:** 0-100 percentage
  - **Outfit Recommendations:** Detailed clothing suggestions
  - **Summary Report:** Personalized advice

---

## Step 9: Test Recommendations List

### View All Recommendations

1. Click **"My Recommendations"** (top menu)
2. Should see your recommendation card with:
   - Recommendation ID
   - Style & Confidence scores
   - Level (Advanced or Beginner-Friendly)
3. Click on card to view full details

### Expected Result:
- ✅ All recommendation details displayed
- ✅ Outfit combinations visible
- ✅ Summary report readable

---

## Step 10: Test Login/Logout

### Logout
1. Click your name in top right
2. Click **Logout**
3. Should be redirected to login page

### Login Again
1. Enter email: test@example.com
2. Enter password: Password123
3. Click **Login**
4. Should see dashboard

### Expected Result:
- ✅ Session works correctly
- ✅ Token refreshed
- ✅ Can access protected routes

---

## Troubleshooting

### Backend Won't Start

**Error: "port 8080 already in use"**
```bash
# Kill process on 8080
netstat -ano | findstr :8080
taskkill /PID <PID> /F

# Or use different port
mvn spring-boot:run -Dspring-boot.run.arguments="--server.port=8081"
```

**Error: "Database connection refused"**
```bash
# Verify MySQL is running
mysql -h localhost -u root -p -e "SHOW DATABASES;"

# If not running, start it:
# Windows service: net start MySQL80
# Docker: docker start aistyle-mysql
```

**Error: "HF_API_TOKEN not valid"**
```bash
# Check .env file has correct token
type .env

# Verify token at: https://huggingface.co/settings/tokens
# Token should start with "hf_"
```

### Frontend Won't Start

**Error: "ng command not found"**
```bash
# Use npx instead
npx ng serve
```

**Error: "port 4200 already in use"**
```bash
# Use different port
ng serve --port 4201
# Then access: http://localhost:4201
```

**Error: "Cannot find module"**
```bash
# Reinstall dependencies
rm -rf node_modules package-lock.json
npm install
ng serve
```

### CORS Error in Browser

**Error: "Access to XMLHttpRequest blocked"**
```
Solution: Backend must be running on port 8080
Make sure: mvn spring-boot:run is still running
Check browser console (F12) for exact error
```

### Form Validation Issues

**Error: "Cannot proceed without X field"**
```
Solution: All fields are required
Fill in all dropdowns and text fields
Colors field must not be empty
```

### AI Recommendation Timeout

**Error: "Request taking too long"**
```
Reason: Hugging Face API is processing
Normal wait time: 15-20 seconds
If > 30 seconds: Check HF_API_TOKEN is valid
```

---

## Testing Checklist

### ✅ Signup Flow
- [ ] Can navigate to signup page
- [ ] Can fill signup form
- [ ] Can submit signup
- [ ] Account created successfully
- [ ] Redirected to dashboard
- [ ] User info shown in navbar

### ✅ Login Flow
- [ ] Can logout
- [ ] Can navigate to login
- [ ] Can enter credentials
- [ ] Can login successfully
- [ ] Redirected to dashboard
- [ ] Token stored in localStorage

### ✅ Stylist Feature
- [ ] Can navigate to stylist form
- [ ] Can fill all form fields
- [ ] Can submit form
- [ ] AI processing shows
- [ ] Results appear within 30 seconds
- [ ] Style analysis displayed
- [ ] Outfit recommendations shown
- [ ] Summary report visible

### ✅ Recommendations
- [ ] Can view all recommendations
- [ ] Can see recommendation cards
- [ ] Can click to view details
- [ ] Can see full information

### ✅ Navigation
- [ ] Navbar shows user name
- [ ] Links work correctly
- [ ] Can navigate between pages
- [ ] Logout button works

### ✅ Validation
- [ ] Form validation works
- [ ] Error messages appear
- [ ] Cannot submit with empty fields
- [ ] API errors handled gracefully

---

## API Testing (Optional)

### Test with Postman or cURL

**1. Signup:**
```bash
curl -X POST http://localhost:8080/api/auth/signup \
  -H "Content-Type: application/json" \
  -d "{
    \"email\": \"postman@example.com\",
    \"password\": \"TestPassword123\",
    \"confirmPassword\": \"TestPassword123\",
    \"firstName\": \"Postman\",
    \"lastName\": \"Test\"
  }"
```

**2. Get Token from Response:**
```
Copy the "token" value from response
```

**3. Get Recommendations:**
```bash
curl -X POST http://localhost:8080/api/stylist/recommend \
  -H "Authorization: Bearer YOUR_TOKEN_HERE" \
  -H "Content-Type: application/json" \
  -d "{
    \"gender\": \"MALE\",
    \"ageGroup\": \"THIRTIES\",
    \"bodyType\": \"RECTANGLE\",
    \"skinTone\": \"DEEP\",
    \"budgetRange\": \"PREMIUM\",
    \"occasionType\": \"BUSINESS\",
    \"preferredColors\": \"Navy, Gray\",
    \"stylePreference\": \"CLASSIC\",
    \"weather\": \"COOL\",
    \"confidenceLevel\": \"HIGH\",
    \"favoriteBrands\": \"Hugo Boss\",
    \"fitPreference\": \"SLIM\",
    \"wardrobePreferences\": \"Professional\"
  }"
```

---

## Performance Notes

### Expected Performance

- **Backend startup:** 10-15 seconds
- **Frontend startup:** 5-10 seconds
- **Signup:** <1 second
- **Login:** <1 second
- **AI Recommendation:** 15-20 seconds (Hugging Face processing)
- **View Recommendations:** <1 second

### Database Performance

- **User queries:** <100ms
- **Recommendation save:** <200ms
- **Recommendation retrieval:** <100ms

---

## Next Steps After Testing

1. ✅ Verify all features work
2. ✅ Check error handling
3. ✅ Test with different data
4. ✅ Verify recommendations make sense
5. 📖 Read architecture docs
6. 🔒 Review security documentation
7. 🚀 Consider deployment

---

## Keep Running

```bash
# Terminal 1 (Backend):
cd FDE-Proj-Hug/backend
mvn spring-boot:run

# Terminal 2 (Frontend):
cd FDE-Proj-Hug/frontend
ng serve

# Terminal 3 (Optional - access app):
# Open browser to http://localhost:4200
```

---

## Useful Links

- **Frontend:** http://localhost:4200
- **Backend Health:** http://localhost:8080/api/auth/health
- **Hugging Face:** https://huggingface.co
- **Documentation:** See docs/ folder

---

## Support

For issues:
1. Check SETUP_GUIDE.md troubleshooting section
2. Review logs in terminal windows
3. Check browser console (F12)
4. Verify all services running
5. Check environment variables

Good luck! 🎉
