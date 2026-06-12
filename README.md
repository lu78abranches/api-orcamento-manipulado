# 💊 AI-Powered Pharmacy Budget API (API Orçamento Manipulado)

[![Java 17](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.x-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![React](https://img.shields.io/badge/React-19-61DAFB?style=flat&logo=react&logoColor=white)](https://react.dev/)
[![Vite](https://img.shields.io/badge/Vite-8-646CFF?style=flat&logo=vite&logoColor=white)](https://vite.dev/)
[![Axios](https://img.shields.io/badge/Axios-HTTP-5A29E4?style=flat)](https://axios-http.com/)
[![Gemini AI](https://img.shields.io/badge/Google%20Gemini-AI%20Integration-blue.svg)](https://ai.google.dev/)
[![MySQL](https://img.shields.io/badge/Database-MySQL-lightgrey.svg)](https://www.mysql.com/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-Database-336791?style=flat&logo=postgresql&logoColor=white)](https://www.postgresql.org/)
[![Netlify](https://img.shields.io/badge/Netlify-Frontend-00C7B7?style=flat&logo=netlify&logoColor=white)](https://www.netlify.com/)
[![Render](https://img.shields.io/badge/Render-Backend-46E3B2?style=flat&logo=render&logoColor=white)](https://render.com/)


A modern, highly efficient RESTful API designed for compounding pharmacies (farmácias de manipulação). This application leverages **Google's Generative AI (Gemini)** to automatically scan, analyze, and extract structured data (items and prices) directly from images of medical prescriptions and receipts.

This project demonstrates strong capabilities in **backend engineering, system integrations, and applied artificial intelligence**, making it a standout piece for automated data processing in the healthcare/retail sector.

---

### 🔄 Interactive End-to-End Workflow
## 🌐 **Access the Interactive Dashboard:** [AI-Powered Pharmacy-Dashboard](https://api-orcamento-manipulado.netlify.app/)
1. **Upload Prescription:** Send a medical prescription image directly to the cloud backend.
2. **AI Automated Processing:** Google Gemini Vision reads the image, running NLP data extraction in the background.
3. **Instant Budget Engine:** The system structures the data, runs the specialized pricing engine, and automatically saves a new record to the PostgreSQL instance.
4. **Real-Time Data Sync:** The record is instantly made available for real-time dashboard polling.
---
> ⚠️ **Technical Note About Hosting (Free Plan):** The application is hosted on Render’s free-tier infrastructure. If the system remains inactive for a few minutes, the server will enter hibernation mode (cold start). The first API request (such as trying to log in or sending the webhook payload) may take around **45 to 60 seconds** to wake up the Java/Spring Boot environment and establish the connection with the managed PostgreSQL instance. Subsequent requests will process instantaneously.

## 🚀 Key Features

* **AI Image Processing**: Integrates with the **Google Gemini API** (using the multimodal `gemini-2.5-flash` model) to read uploaded medical prescription images and intelligently parse text, medications, and pricing.
* **Prescription Validation**: Rejects non-prescription uploads with a friendly Portuguese error message, ensuring the application accepts only valid medical prescriptions.
* **RESTful Architecture**: Clean, scalable, and well-documented HTTP endpoints for uploading files and managing budgets.
* **Robust Data Persistence**: Utilizes Spring Data JPA and Hibernate to reliably store budget information in a relational MySQL database.
* **Environment Security**: Uses external `.env` configuration to keep sensitive API keys and database credentials secure and out of the source code.
* **Clean Code & Tooling**: Built with Lombok for boilerplate reduction and structured utilizing standard Java/Spring design patterns (Controller-Service-Repository).

## 🛠️ Tech Stack

* **Backend Framework**: Java 17, Spring Boot 3.5
* **Artificial Intelligence**: Google Generative AI (Gemini 2.5 REST API)
* **Database & ORM**: MySQL / PostgreSQL (production), Spring Data JPA, Hibernate (H2 for tests)
* **Backend Utilities**: Lombok, Jackson (JSON parsing), Maven, Spring Security (JWT)

* **Frontend (`frontend/`)**: React 19, Vite 8, Axios
* **Frontend UI**: `react-dropzone` (prescription upload), `lucide-react` (icons), `framer-motion` (animations)
* **Frontend Styling**: CSS modules in `index.css` (`.markdown-content`, `.report-panel`, `.result-cards` grid)
* **Frontend Tooling**: ESLint, `@vitejs/plugin-react`
* **Frontend Hosting**: Netlify (dashboard); API base URL via `VITE_API_URL` in `.env`

---

## ⚙️ How it Works

1. **Client Request**: The user sends a `POST` request containing a multipart file (the image of the prescription/budget).
2. **Prescription Validation**: The backend verifies that the uploaded file contains a valid medical prescription and rejects non-prescription content with a friendly Portuguese message.
3. **AI Analysis**: The API encodes the image in Base64 and sends a prompt to the Google Gemini AI, instructing it to extract the items and pricing into a strictly formatted JSON.
4. **Data Parsing**: The Spring backend receives the AI's markdown/JSON response, parses it using Jackson, and maps it to Java DTOs.
5. **Persistence**: The extracted items are processed, persisted into the PostgreSQL database, and returned to the client as a structured financial budget.

---

## 📂 Directory Structure

```text
api-orcamento-manipulado/
├── frontend/                              # React dashboard (deployed on Netlify)
│   ├── src/
│   │   ├── components/
│   │   │   ├── DashboardLayout.jsx
│   │   │   ├── ProcessingState.jsx
│   │   │   ├── ResultCard.jsx             # Cards: Medicamentos / Orçamento / Status
│   │   │   ├── UploadCard.jsx
│   │   │   └── UploadZone.jsx             # Upload + relatório Markdown
│   │   ├── pages/
│   │   │   └── Dashboard.jsx
│   │   ├── services/
│   │   │   └── api.js                     # Axios client (VITE_API_URL)
│   │   ├── utils/
│   │   │   └── markdownToHtml.js
│   │   ├── App.jsx
│   │   ├── main.jsx
│   │   └── index.css                      # .markdown-content, .result-cards
│   ├── index.html
│   ├── package.json
│   ├── vite.config.js
│   └── eslint.config.js
├── src/
│   ├── main/
│   │   ├── java/com/farmacia/api_orcamento_manipulado/
│   │   │   ├── config/
│   │   │   │   ├── DataInitializer.java
│   │   │   │   ├── JpaConfig.java
│   │   │   │   └── security/
│   │   │   │       ├── CustomUserDetailsService.java
│   │   │   │       ├── SecurityConfig.java
│   │   │   │       └── SecurityFilter.java
│   │   │   ├── controller/
│   │   │   │   ├── AuthController.java
│   │   │   │   ├── GlobalExceptionHandler.java
│   │   │   │   ├── OrcamentoController.java
│   │   │   │   ├── PrescriptionUploadController.java
│   │   │   │   └── TwilioWebhookController.java
│   │   │   ├── dto/
│   │   │   │   ├── ItemExtraidoDTO.java
│   │   │   │   ├── OrcamentoProcessadoDTO.java
│   │   │   │   ├── OrcamentoPendenteDTO.java
│   │   │   │   └── ...
│   │   │   ├── mapper/
│   │   │   │   └── OrcamentoMapper.java
│   │   │   ├── model/
│   │   │   │   ├── ItemOrcamento.java
│   │   │   │   ├── Orcamento.java
│   │   │   │   ├── OrcamentoStatus.java
│   │   │   │   └── Usuario.java
│   │   │   ├── repository/
│   │   │   │   ├── OrcamentoRepository.java
│   │   │   │   └── UsuarioRepository.java
│   │   │   ├── service/
│   │   │   │   ├── GeminiReceitaService.java
│   │   │   │   ├── IAReceitaService.java
│   │   │   │   ├── OpenAIReceitaService.java
│   │   │   │   ├── OrcamentoService.java
│   │   │   │   ├── PriceCalculationEngine.java
│   │   │   │   └── TokenService.java
│   │   │   └── ApiOrcamentoManipuladoApplication.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── java/com/farmacia/api_orcamento_manipulado/
│           ├── controller/
│           ├── config/
│           ├── model/
│           ├── repository/
│           ├── security/
│           └── service/
├── pom.xml
├── README.md
├── HELP.md
└── Documento_de_Requisitos_API_Farmacia.docx
```

---

## 📈 Project Status / Roadmap

Based on the **System Requirements Document (DRS)**, the following modules are either functional or pending implementation.

### ✅ What has been developed
* **AI Data Extraction (RF-02):** Google Gemini Vision extracts medications and prices from prescription images via REST (`/prescriptions/upload` and related flows).
* **Price Calculation Engine (RF-03, RN-03):** `PriceCalculationEngine` sums item prices (with normalization table) and applies the fixed handling fee (R$ 10,00).
* **React Dashboard (`frontend/`):** Vite + React app with drag-and-drop upload (`react-dropzone`), Markdown budget report (`.markdown-content`), and structured summary cards (Medicamentos / Orçamento / Status) fed by `OrcamentoProcessadoDTO`.
* **Budget API & DTOs:** Pending, processed, approval, and final-quote responses; GFM-friendly Markdown generation on the backend plus `budget` and `medications` fields for the UI.
* **Pharmacist workflow (backend):** Budgets saved as `PENDENTE_REVISAO` with approve/reject endpoints and pending listing.
* **WhatsApp intake (partial RF-01):** `TwilioWebhookController` receives inbound messages and triggers preliminary budget creation.
* **Authentication foundation (RNF-01):** JWT login (`AuthController`, `TokenService`), Spring Security filter chain, and BCrypt-backed users.
* **Data persistence & security:** Spring Data JPA (MySQL/PostgreSQL in production, H2 in tests), `.env` for secrets, and automated test suite (controllers, services, security).

### 🚧 What is left to develop
* **Pharmacist Validation Panel UI (RF-04, RN-01, RN-02):** Dashboard screens to review pending budgets, edit items, and approve or reject before client delivery (backend rules exist; UI workflow is incomplete).
* **WhatsApp outbound (RF-05):** Send approved quotes and payment links to the customer after pharmacist validation (inbound webhook only today).
* **Full pricing formula (RN-03):** Extend the engine with packaging, markup, and configurable rules beyond subtotal + fixed fee.
* **Controlled Substances Alert (RN-04):** Detect active ingredients under Brazilian Ordinance 344/98 and surface warnings in API and dashboard.
* **Cloud Object Storage (RF-06):** Store original prescription images in S3 or GCS and link URLs to each budget record.
* **Advanced Authentication (RNF-02):** Role-based profiles (e.g. pharmacist vs admin), token refresh, and tighter endpoint policies.
* **Real-time sync:** Live polling or WebSocket updates on the dashboard for pending-queue changes without manual refresh.

---

## 🏃‍♂️ Running the Project Locally

### Prerequisites
* Java 17 or higher
* Maven (or use the provided `./mvnw` wrapper)
* MySQL Server running on `localhost:3306`
* A valid Google Gemini API Key

### Setup
1. **Clone the repository**:
   ```bash
   git clone https://github.com/your-username/api-orcamento-manipulado.git
   cd api-orcamento-manipulado
   ```

2. **Configure Environment Variables**:
   Create a `.env` file in the root directory based on your credentials:
   ```env
   DB_USERNAME=your_mysql_user
   DB_PASSWORD=your_mysql_password
   GEMINI_API_KEY=your_google_gemini_api_key
   ```

3. **Database Preparation**:
   Ensure you have a MySQL database created named `api_farmacia`. The application is configured to create the database if it does not exist (using the MySQL connector), and JPA will automatically update the schema.

4. **Start the Application**:
   ```bash
   ./mvnw spring-boot:run
   ```
   The API will be available at `http://localhost:8082`.

   ---

   ## ☁️ Deploy no Render + Neon (PostgreSQL)

   Se você implanta o backend no Render usando um banco Neon (PostgreSQL gerenciado), siga estas recomendações para evitar erros de conexão JDBC/SSL:

   - **Variáveis de ambiente essenciais** (defina no painel do Render):
      - `SPRING_DATASOURCE_URL` — Ex: `jdbc:postgresql://<host>:5432/<dbname>?sslmode=require`
      - `DB_USERNAME` — usuário do banco (ex: `neondb_owner`)
      - `DB_PASSWORD` — senha do banco
      - `SPRING_PROFILES_ACTIVE=prod`

   - **Exemplo de URL JDBC recomendado** (não inclua parâmetros com underline como `channel_binding`):

      jdbc:postgresql://ep-jolly-smoke-ackhmqb2-pooler.sa-east-1.aws.neon.tech:5432/neondb?sslmode=require

   - **Teste de conexão com psql (local ou container com psql instalado)**:

   ```bash
   psql "host=ep-jolly-smoke-ackhmqb2-pooler.sa-east-1.aws.neon.tech port=5432 dbname=neondb user=neondb_owner password='SUA_SENHA' sslmode=require"
   ```

   - **Problemas comuns e soluções rápidas**:
      - `The connection attempt failed` / SQLState 08001: verifique se a URL contém a porta (`:5432`) e se `sslmode=require` está presente.
      - Se a URL contiver `channel_binding=require` (com underline), remova esse parâmetro — algumas versões do driver JDBC não aceitam o nome com underline e falham no handshake.
      - Confirme no painel do Render que não há espaços em branco extras nas variáveis (ex.: `DB_USERNAME= neondb_owner` causa erro).
      - Se o Neon tiver configurações de allowlist, confirme que o serviço da Render tem acesso à instância (mas o pooler público do Neon normalmente permite conexões externas com SSL).

   - **Debug temporário**: se precisar de logs detalhados da inicialização e do pool de conexões, adicione temporariamente em `src/main/resources/application-prod.properties` as linhas abaixo (remova após a investigação):

   ```
   logging.level.org.springframework=DEBUG
   logging.level.org.hibernate=DEBUG
   logging.level.com.zaxxer.hikari=DEBUG
   spring.jpa.show-sql=true
   ```

   Se quiser, eu adiciono essa configuração temporária ao projeto (já incluída para debug pelo branch atual). Remova-as depois que o problema for resolvido para evitar logs verbosos em produção.

---

## 📬 Contact & Developer

[![GitHub](https://img.shields.io/badge/GitHub-181717?style=for-the-badge&logo=github&logoColor=white)](https://github.com/lu78abranches)

[![LinkedIn](https://img.shields.io/badge/LinkedIn-0077B5?style=for-the-badge&logo=linkedin&logoColor=white)](https://www.linkedin.com/in/luis-abranches/)

[![Gmail](https://img.shields.io/badge/Gmail-D14836?style=for-the-badge&logo=gmail&logoColor=white)](mailto:luisabranches.violao@gmail.com)

> *Note for Recruiters: This project highlights my ability to integrate modern cloud AI services into enterprise-grade Java backends, handle unstructured data formats, and build robust REST APIs using the Spring ecosystem.*
