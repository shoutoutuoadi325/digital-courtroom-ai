# Digital Courtroom AI

An advanced LegalTech platform designed for high accuracy and accessibility in legal assistance. The system combines hybrid reasoning (rule-based and LLM-based), multi-agent frameworks, and interactive features to make legal knowledge accessible to a broader audience.

## 🎯 Vision & Architecture

### Core Features

#### 1. **Hybrid Reasoning Engine**
- **Intelligent Query Routing**: Incoming queries are intelligently routed based on complexity
  - **Rule-Based Engine**: Standardized, structured queries (e.g., contract breach, divorce, inheritance) are routed to a deterministic rule-based expert system
  - **LLM-Based Engine**: Complex, ambiguous, or open-ended queries are handled by semantic understanding and logical parsing
- Ensures accurate determinate responses for standard legal scenarios while enabling flexibility for edge cases

#### 2. **Human-in-the-Loop (HITL) Pipeline**
- Legal professionals can review and correct AI-generated analysis through an interactive interface
- Corrections form a continuous feedback loop that:
  - Expands the legal corpus with curated data
  - Triggers LLM fine-tuning and reinforcement learning improvements
  - Improves system accuracy over time

#### 3. **Multi-Agent Framework**
- Spawn multiple heterogeneous LLM agents for complex scenarios
- Enable cross-debate and cross-verification of logical outputs
- Agents self-correct through multi-perspective analysis

#### 4. **Multimodal Input & Output**
- **Input Processing**:
  - Text input for direct queries
  - Document upload & OCR for image and PDF parsing
  - Foundation for voice input (optimized for regional dialects like Sichuanese and Cantonese)
- **Output Generation**:
  - Parse complex legal relationships and procedural steps
  - Render as interactive mind maps and visual diagrams for easy comprehension

#### 5. **Gamified Mock Trial Adventure**
- Immersive simulation engine using the multi-agent system
- **Roleplay Mechanics**: Users select roles (plaintiff, defendant, lawyer, judge, etc.)
- **Real-time Agent Interaction**: Interact with AI agents assigned to remaining roles
- Learn legal procedures through interactive, engaging gameplay

## 🏗️ Project Structure

```
digital-courtroom-ai/
├── backend/                    # Spring Boot REST API
│   ├── pom.xml                # Maven configuration
│   └── src/main/java/com/courtroom/
│       ├── Application.java    # Spring Boot entry point
│       ├── controller/         # REST API endpoints
│       ├── service/            # Business logic
│       ├── repository/         # Data access layer
│       ├── entity/             # JPA entities
│       ├── dto/                # Data transfer objects
│       ├── exception/          # Custom exceptions
│       └── config/             # Configuration classes
└── frontend/                   # Vue 3 + Vite application
    ├── src/
    │   ├── App.vue            # Root component
    │   ├── main.js            # Entry point
    │   ├── router/            # Vue Router configuration
    │   ├── views/             # Page components
    │   └── components/        # Reusable components
    └── vite.config.js         # Vite configuration
```

## 🚀 Tech Stack

### Backend
- **Framework**: Spring Boot 3.2.3
- **Java Version**: 17
- **Database**: MySQL
- **ORM**: JPA/Hibernate
- **Build Tool**: Maven

### Frontend
- **Framework**: Vue 3
- **Build Tool**: Vite
- **State Management**: Pinia
- **Router**: Vue Router
- **HTTP Client**: Axios

## 🛠️ Setup & Installation

### Prerequisites
- Java 17+
- Maven 3.6+
- Node.js 16+
- MySQL 8.0+

### Backend Setup

1. **Configure Database**
   - Update database credentials in `backend/src/main/resources/application.properties`:
     ```properties
     spring.datasource.url=jdbc:mysql://localhost:3306/digital_courtroom
     spring.datasource.username=root
     spring.datasource.password=your_password
     ```

2. **Build & Run**
   ```bash
   cd backend
   mvn clean install
   mvn spring-boot:run
   ```
   - Server runs on `http://localhost:8080`

### Frontend Setup

1. **Install Dependencies**
   ```bash
   cd frontend
   npm install
   ```

2. **Development Server**
   ```bash
   npm run dev
   ```
   - Access at `http://localhost:5173`

3. **Build for Production**
   ```bash
   npm run build
   ```

## 📡 API Endpoints

### Legal Queries (`/api/queries`)
- `POST /` - Submit a legal query
  - Request: `QueryRequest` with `queryText` and `inputType`
  - Response: `QueryResponse` with analysis results
- `POST /upload` - Upload document for legal analysis
  - Parameters: `file` (MultipartFile), `inputType` (text/image/pdf)
  - Response: `QueryResponse` with extracted and analyzed content
- `GET /` - Retrieve all queries
- `GET /{id}` - Retrieve specific query by ID

### Legal Corpus (`/api/corpus`)
- Manage the legal knowledge base
- CRUD operations for legal documents and rules

### Mock Trials (`/api/mock-trials`)
- Create and manage mock trial simulations
- Real-time interaction with AI agents

### HITL Reviews (`/api/reviews`)
- Submit corrections and feedback
- Improve system accuracy through human review

## 🔄 Query Routing Logic

The system intelligently routes queries based on complexity:

### Rule-Based Keywords (Standardized Cases)
- Contract breach (合同违约)
- Divorce (离婚)
- Inheritance (继承)
- Traffic accident (交通事故)
- Labor dispute (劳动争议)
- Property dispute (房产纠纷)
- And more...

### Complex Indicators (LLM Processing)
- "complex", "ambiguous", "unclear", "multiple"
- "what if", "suppose", "hypothetically"
- "analyze", "evaluate", "compare"

## 🗄️ Core Entities

- **LegalQuery**: Stores user queries with routing information
- **LegalCorpus**: Knowledge base of legal rules and precedents
- **MockTrial**: Simulation instances with roles and participants
- **TrialMessage**: Chat history for mock trial interactions
- **HitlReview**: Human feedback and corrections

## 📝 Current Implementation Status

### ✅ Completed
- Backend REST API with Spring Boot
- Database schema with JPA entities
- Query routing logic (rule-based vs LLM-based)
- Document upload and processing
- Data initialization and seeding
- CORS configuration for frontend communication

### 🚧 In Development
- LLM integration for semantic query processing
- Multi-agent reasoning framework
- Mock trial simulation engine
- Multimodal input handlers (voice, advanced OCR)
- Interactive mind map visualization
- Human-in-the-loop review interface

## 🔧 Configuration

### Environment Variables
- `DB_URL`: MySQL connection string (defaults to localhost)
- `server.port`: Backend server port (default: 8080)

### CORS Settings
- Currently configured for `http://localhost:5173` (development)
- Update `spring.web.cors.allowed-origins` for production

## 📚 Database Schema

The system uses JPA/Hibernate for ORM with automatic schema generation. Key tables:
- `legal_query` - User queries and routing information
- `legal_corpus` - Legal knowledge base
- `mock_trial` - Trial simulations
- `trial_message` - Chat/interaction log
- `hitl_review` - Human feedback records

## 🤝 Contributing

This project is under active development. To contribute:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/YourFeature`)
3. Commit your changes (`git commit -m 'Add YourFeature'`)
4. Push to the branch (`git push origin feature/YourFeature`)
5. Open a Pull Request

## 📞 Support

For questions or issues, please open an GitHub issue or contact the development team.
