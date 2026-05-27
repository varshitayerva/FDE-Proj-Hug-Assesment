# AI Stylist - Direct AI-Powered Fashion Recommendations

An intelligent fashion styling web application that provides instant personalized outfit recommendations using AI. Simply fill in your style preferences and get AI-generated outfit combinations with detailed styling guidance - no login required, direct access to AI stylist.

---

## 📋 Project Overview

**AI Stylist** is a streamlined fashion recommendation engine that combines advanced AI models with a clean, intuitive interface. Users access the application directly and receive personalized outfit recommendations in real-time.

### What It Does
1. User fills in a simple style preference form (13 fields)
2. AI analyzes preferences and generates style persona
3. AI creates 3 customized outfit combinations
4. Dual AI models validate recommendations for safety and quality
5. User gets complete styling package with confidence scores and explanations

### Key Characteristics
- ✅ **Real-time Processing** - Instant AI recommendations
- ✅ **Multi-Model Validation** - Safety + Quality checks
- ✅ **Interactive UI** - Expandable outfit cards with full details
- ✅ **Confidence Scoring** - Know how reliable each recommendation is
- ✅ **Clean & Professional** - Markdown-free, polished summaries

---

## 🚀 Setup Instructions

### Prerequisites
- **Java 21** or higher
- **Maven 3.8+**
- **Node.js 18+** with npm
- **HuggingFace API Token** (free - see below)

### Step 1: Get HuggingFace API Token

1. Visit https://huggingface.co and create a free account
2. Go to **Account Settings → Access Tokens**
3. Click **New token**
4. Select **Read** access type
5. Click **Create token** and copy it (save for later)

### Step 2: Set Environment Variable

**IMPORTANT:** Never hardcode API keys in configuration files. Use environment variables instead.

**On Windows (Command Prompt):**
```bash
# Set HuggingFace API token as environment variable
set HF_API_TOKEN=your_hugging_face_token_here
```

**On Windows (PowerShell):**
```powershell
# Set HuggingFace API token as environment variable
$env:HF_API_TOKEN="your_hugging_face_token_here"
```

**On macOS/Linux (Bash/Zsh):**
```bash
# Set HuggingFace API token as environment variable
export HF_API_TOKEN=your_hugging_face_token_here
```

**Or add to your system environment variables permanently:**
1. On Windows: Search "Edit environment variables for your account"
2. Click "New" and add:
   - Variable name: `HF_API_TOKEN`
   - Variable value: Your actual HuggingFace API token
3. Click OK and restart your terminal/IDE

### Step 3: Start Backend

Make sure the `HF_API_TOKEN` environment variable is set from Step 2, then:

```bash
cd backend

# Install dependencies
mvn clean install

# Start the application (will use HF_API_TOKEN environment variable)
mvn spring-boot:run

# You should see:
# Started Application in X seconds
# Tomcat started on port 8000
```

**If you see an error about missing HF_API_TOKEN:**
- Verify the environment variable is set correctly
- Restart your terminal/IDE after setting the variable
- Check the variable is in your system PATH

**Backend URL:** http://localhost:8000

### Step 4: Start Frontend

```bash
cd frontend

# Install dependencies
npm install

# Start development server
ng serve

# You should see:
# Angular Live Development Server is listening on localhost:4200
# Open your browser to http://localhost:4200/
```

**Frontend URL:** http://localhost:4200

### Step 5: Use the Application

1. Open http://localhost:4200 in your browser
2. Fill in your style preferences:
   - Gender, age group, body type, skin tone
   - Preferred colors, style preference, fit preference
   - Budget range, occasion type, weather condition
   - Fashion confidence level, favorite brands, additional notes
3. Click **"Get Styling Recommendations"**
4. Wait 6-9 seconds for AI processing
5. View your personalized styling results!

---

## ✨ Key Features

### What Users Can Do
- **Answer Style Questions** - Gender, age, body type, colors, budget, occasion, etc.
- **Get AI Recommendations** - Instant outfit combinations with specific pieces
- **View Style Analysis** - Fashion persona, fit guidance, suitable colors
- **Read Full Summaries** - Complete styling explanations without truncation
- **Explore Outfit Details** - Click to expand each outfit for piece-by-piece info
- **See Confidence Scores** - Visual indicators of recommendation quality

### AI-Powered Capabilities
- ✅ **Style Persona Detection** - AI identifies your unique fashion identity
- ✅ **Outfit Generation** - Creates 3 distinct, wearable combinations
- ✅ **Budget-Conscious** - Respects your budget constraints
- ✅ **Occasion-Aware** - Tailored for specific events
- ✅ **Color Recommendations** - Matches colors to skin tone and preferences
- ✅ **Quality Validation** - Double-checks for style coherence
- ✅ **Safety Assurance** - Ensures recommendations are appropriate

### User Interface Features
- ✅ **Interactive Outfit Cards** - Expandable/collapsible with full details
- ✅ **Style Analysis Grid** - Visual cards for persona, colors, scores
- ✅ **Progress Bars** - Visual representation of style and confidence scores
- ✅ **Color & Category Tags** - Easy-to-scan style information
- ✅ **Responsive Design** - Works on desktop, tablet, mobile
- ✅ **Professional Styling** - Gradients, shadows, smooth interactions

### Security & Reliability
- ✅ **Prompt Injection Protection** - Blocks malicious input patterns
- ✅ **Input Validation** - Sanitizes all user inputs
- ✅ **Output Cleaning** - Removes markdown artifacts
- ✅ **Content Safety Check** - AI validates recommendations are appropriate
- ✅ **Quality Assurance** - Second AI model checks recommendation quality

---

## 🏗️ Architecture & Workflow

### System Design
```
User Browser (Angular)
    ↓ HTTP Request
    ↓ Style Preferences
    ↓
Spring Boot Backend
    ├─ Input Validation (Injection Filter)
    ├─ LLM Call #1: Style Analysis
    ├─ LLM Call #2: Outfit Recommendations
    ├─ LLM Call #3: Summary Report
    ├─ Safety Validation (Judge Model)
    ├─ Quality Validation (Quality Judge)
    └─ Output Sanitization
    ↓ HTTP Response
    ↓ JSON Recommendations
    ↓
User Sees Results
    ├─ Style Analysis
    ├─ 3 Outfit Cards (Expandable)
    ├─ Full Summary
    └─ Confidence Scores
```

### AI Processing Steps
```
1. Form Submission
   └─ User submits 13 style preference fields

2. Input Validation
   └─ PromptInjectionFilter checks for malicious patterns
   └─ If blocked: Error message to user

3. Style Analysis (LLM #1)
   └─ Model: DeepSeek-V4-Flash
   └─ Output: Fashion persona, fit guidance, color recommendations, scores

4. Safety Check
   └─ Model: DeepSeek-V4-Pro
   └─ Validates: No harmful/inappropriate content
   └─ If failed: Recommendation rejected

5. Outfit Generation (LLM #2)
   └─ Model: DeepSeek-V4-Flash
   └─ Output: 3 complete outfits (top, bottom, shoes, accessories, costs)

6. Quality Validation
   └─ Model: Llama-2-7b
   └─ Scores: Style coherence, quality (1-5), diversity, practicality
   └─ Generates: Confidence score (0-100%) and explanation

7. Summary Generation (LLM #3)
   └─ Model: DeepSeek-V4-Flash
   └─ Output: Encouraging styling summary with explanations

8. Output Cleaning
   └─ Removes markdown formatting (**bold**, *italic*, # headers)
   └─ Ensures clean presentation

9. Send to Frontend
   └─ Complete styling package with all data
   └─ Frontend displays results
```

### Models Used
| Model | Purpose | Provider |
|-------|---------|----------|
| DeepSeek-V4-Flash | Recommendations | HuggingFace |
| DeepSeek-V4-Pro | Safety Validation | HuggingFace |
| Llama-2-7b | Quality Assessment | HuggingFace |

---

## 🤖 AI Capabilities Used

### 1. Natural Language Generation
- **Style Analysis** - Analyzes user input to identify fashion personality
- **Outfit Combinations** - Creates realistic, trend-aware outfit suggestions
- **Explanations** - Provides reasoning for each recommendation
- **Summaries** - Writes encouraging, personalized styling guidance

### 2. Pattern & Style Recognition
- **Fashion Persona Detection** - Identifies user's style identity (e.g., "Edgy Business Minimalist")
- **Color Theory** - Matches colors to skin tone, style, and preferences
- **Occasion Matching** - Ensures outfits fit the stated occasion
- **Budget Optimization** - Respects financial constraints

### 3. Multi-Model Validation
- **Safety Detection** - Identifies harmful or inappropriate suggestions
- **Quality Assessment** - Rates coherence and quality
- **Diversity Checking** - Ensures variety across outfit suggestions
- **Practicality Validation** - Confirms outfits are actually wearable

### 4. Advanced Prompt Engineering
- **Structured Outputs** - Forces LLM to follow specific format
- **Conditional Logic** - Adjusts recommendations based on confidence
- **Sequential Processing** - Uses multiple LLM calls for complex tasks
- **Confidence Scoring** - AI generates reliability metrics

### 5. Security & Robustness
- **Injection Detection** - Blocks 15+ malicious keywords and 8+ patterns
- **Input Sanitization** - Cleans user input before processing
- **Output Sanitization** - Removes markdown and formatting artifacts
- **Fallback Handling** - Graceful errors if models unavailable

---

## 🛠️ Technology Stack

### Backend
- **Java 21** - Modern Java platform
- **Spring Boot** - REST API framework
- **Maven** - Build and dependency management
- **H2 Database** - In-memory data storage (session-only)
- **Logging** - SLF4J for debugging and auditing

### Frontend
- **Angular** - Web application framework
- **TypeScript** - Type-safe JavaScript
- **CSS3** - Responsive styling with gradients and animations
- **Reactive Forms** - Form validation and binding
- **npm** - JavaScript package management

### External Services
- **HuggingFace Inference API** - LLM endpoints
  - Router Endpoint: https://router.huggingface.co/v1
  - OpenAI-compatible API format

### Key Libraries & Tools
- **Jackson** - JSON processing
- **Lombok** - Code generation (reduced boilerplate)
- **Angular CLI** - Frontend build and development
- **Git** - Version control

---

## 🔐 Security Configuration

### API Token Security (IMPORTANT)

⚠️ **Never hardcode API keys in configuration files or commit them to version control.**

The application uses environment variables to securely manage sensitive credentials:

**How It Works:**
1. Application.properties file references environment variable: `${HF_API_TOKEN:}`
2. API token is provided via system environment variable `HF_API_TOKEN`
3. If environment variable is not set, an empty string is used (will cause error on API call)
4. This prevents accidental exposure of credentials in source code

**Setting Environment Variables:**

**Windows (Command Prompt):**
```bash
set HF_API_TOKEN=hf_your_actual_token_here
```

**Windows (PowerShell):**
```powershell
$env:HF_API_TOKEN="hf_your_actual_token_here"
```

**macOS/Linux (Bash/Zsh):**
```bash
export HF_API_TOKEN=hf_your_actual_token_here
```

**Windows (Permanent - System Settings):**
1. Right-click "This PC" or "My Computer" → Properties
2. Click "Advanced system settings"
3. Click "Environment Variables"
4. Click "New" under "User variables" or "System variables"
5. Variable name: `HF_API_TOKEN`
6. Variable value: Your actual HuggingFace API token
7. Click OK and restart any open terminals/IDEs

### Best Practices

✅ **DO:**
- Use environment variables for all sensitive data
- Rotate API tokens regularly
- Use tokens with minimal required permissions
- Monitor API usage for suspicious activity
- Store tokens in secure secret management systems (for production)

❌ **DON'T:**
- Hardcode API keys in source files
- Commit .env files with actual tokens to git
- Share API tokens via email or chat
- Use personal tokens in shared environments
- Log or print API tokens

### .gitignore Configuration

The repository's `.gitignore` should exclude:
```
.env
.env.local
.env.*.local
*.properties.local
```

See `.env.example` for template of required environment variables.

---

## 📊 Challenges Faced & Solutions

### Challenge 1: Making LLM Output Consistent
**Problem:** Different LLM models produce different output formats, making data parsing unreliable

**Solution:**
- Engineered structured prompts with explicit format requirements
- Created regex-based parsing with fallback defaults
- Added secondary validation to catch parsing errors
- Each parser handles multiple format variations

### Challenge 2: Prompt Injection Security Risk
**Problem:** Users could craft inputs like "ignore previous instructions" to manipulate the LLM

**Solution:**
- Built PromptInjectionFilter with 15+ keyword blocklist
- Added 8+ advanced regex patterns to catch variations
- Implemented dual-layer protection:
  - Input validation: Checks user submissions
  - Output sanitization: Cleans LLM responses
- Comprehensive logging of all blocked attempts

### Challenge 3: Summary Text Truncation in UI
**Problem:** CSS `line-clamp` was cutting off complete summaries mid-sentence, making text incomplete

**Solution:**
- Added markdown parsing to clean LLM output (removes **, *, # symbols)
- Removed CSS line-clamp from summary section
- Kept line-clamp for outfit recommendations (to keep UI compact)
- Users now see complete, untruncated summaries

### Challenge 4: TypeScript Deprecation Warnings
**Problem:** 4 TypeScript deprecation errors preventing clean builds for TypeScript 7.0

**Solution:**
- Removed deprecated `baseUrl` and replaced with modern `paths` mapping
- Updated `moduleResolution` from "node" to "bundler"
- Added explicit `rootDir: "./src"` configuration
- Removed unnecessary `downlevelIteration` option
- Config is now forward-compatible with TypeScript 7.0+

### Challenge 5: Balancing AI Model Selection
**Problem:** Need to balance quality, speed, and API costs with different models

**Solution:**
- **DeepSeek-V4-Flash** - Fast, high-quality recommendations (primary)
- **DeepSeek-V4-Pro** - Advanced safety detection (expensive, used selectively)
- **Llama-2-7b** - Fashion expertise and quality validation (specialized)
- Carefully sequenced to minimize costs while maximizing quality

### Challenge 6: Displaying Outfit Data Effectively
**Problem:** Large amounts of structured outfit data (3 outfits × 4 pieces each) hard to display clearly

**Solution:**
- Created interactive outfit cards with expand/collapse functionality
- Hidden piece details by default for clean initial view
- Click to expand shows full piece information (category, brand, description, cost)
- Grid layout adapts to screen size
- Visual badges for occasions, color coding, and cost highlighting

### Challenge 7: Managing Build & Dependencies
**Problem:** Multiple deprecated options, missing configurations, version mismatches

**Solution:**
- Modernized both backend (Maven) and frontend (Angular) configs
- Cleaned up unnecessary dependencies
- Updated to current stable versions
- Both build cleanly with no warnings

---

## 🔮 Future Improvements

### Phase 1: User Authentication & Persistence (Next Priority)
- **User Login System** - Email/password authentication for persistent accounts
- **User Registration** - Create accounts with profile verification
- **Save Recommendations** - Users can save outfit recommendations to their account
- **Discard Recommendations** - Remove unwanted recommendations from saved list
- **Recommendation Management** - View, organize, and manage all saved recommendations
- **PostgreSQL Database** - Migrate from H2 (in-memory) to PostgreSQL for data persistence
- **User Profiles** - Store user preferences and style history across sessions

### Phase 2: AI-Powered Wardrobe Management
- **Create Personal Wardrobe** - Users build their complete wardrobe with AI assistance
- **Add Clothing Items** - Input existing wardrobe pieces (brand, color, category, cost)
- **AI Wardrobe Analysis** - AI analyzes owned pieces and suggests combinations
- **Smart Outfit Generation** - Create outfits from existing wardrobe items
- **Wardrobe Organization** - Categorize items (tops, bottoms, shoes, accessories, outerwear)
- **Piece Details** - Store detailed info (size, color, condition, purchase date, cost)
- **Wardrobe Statistics** - Analytics on wardrobe diversity and cost distribution

### Phase 3: Advanced Features
- **Trend Integration** - Incorporate current fashion trends into recommendations
- **Price Comparison** - Link to shopping sites for recommended pieces
- **Size Recommendations** - Suggest sizes based on brand standards
- **Style Evolution** - Track how user's style changes over time
- **Outfit Combinations** - Suggest new combinations from existing wardrobe
- **Color Coordination** - AI suggests color combinations for owned pieces

### Phase 4: Social & Community
- **Share Recommendations** - Email/share outfits with friends
- **Community Styles** - See what similar style types are wearing
- **Styling Ratings** - Rate outfit quality for AI improvement
- **Trending Styles** - See most popular recommendations
- **Social Sharing** - Post outfits to Instagram/Pinterest directly

### Phase 5: Advanced Technology
- **AR Virtual Try-On** - Augmented reality outfit visualization
- **Image Analysis** - Upload photos of wardrobe items for auto-categorization
- **Video Styling** - Real-time styling with video input
- **Personal Stylist AI** - Learns from user feedback over time
- **Body Measurements** - Store body metrics for accurate fit recommendations
- **Lifestyle Integration** - Connect to calendar for occasion-based suggestions

### Phase 6: Mobile & Integration
- **Mobile App** - Native iOS/Android applications
- **API Integration** - Connect to e-commerce platforms (Shopify, WooCommerce)
- **Social Media Integration** - Direct integration with Instagram/Pinterest/TikTok
- **Offline Mode** - Sync wardrobe and recommendations for offline access
- **Push Notifications** - Notify users of trend matches and new recommendations
- **Cross-Platform Sync** - Seamless experience across web and mobile

---

## 📚 Additional Documentation

- **CHANGES_MADE.md** - Complete development history (988 lines, 7 phases)
- **PROJECT_OVERVIEW.md** - Visual summary and statistics
- **PROMPT_INJECTION_PROTECTION.md** - Security implementation guide
- **DUAL_JUDGE_SYSTEM.md** - Quality assurance architecture
- **DOCUMENTATION_INDEX.md** - Navigation guide to all docs

---


