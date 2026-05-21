# 💊 AI-Powered Pharmacy Budget API (API Orçamento Manipulado)

[![Java 17](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.x-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Gemini AI](https://img.shields.io/badge/Google%20Gemini-AI%20Integration-blue.svg)](https://ai.google.dev/)
[![MySQL](https://img.shields.io/badge/Database-MySQL-lightgrey.svg)](https://www.mysql.com/)
[![PostgreSQL](https://shields.io)](https://postgresql.org)
[![Render](https://shields.io)](https://render.com)


A modern, highly efficient RESTful API designed for compounding pharmacies (farmácias de manipulação). This application leverages **Google's Generative AI (Gemini)** to automatically scan, analyze, and extract structured data (items and prices) directly from images of medical prescriptions and receipts.

This project demonstrates strong capabilities in **backend engineering, system integrations, and applied artificial intelligence**, making it a standout piece for automated data processing in the healthcare/retail sector.

---

### 🔄 Interactive End-to-End Workflow
## 🌐 **Access the Interactive Dashboard:** [MiniMarket AI Dashboard](https://api-orcamento-manipulado.netlify.app/)
1. **Upload Prescription:** Send a medical prescription image directly to the cloud backend.
2. **AI Automated Processing:** Google Gemini Vision reads the image, running NLP data extraction in the background.
3. **Instant Budget Engine:** The system structures the data, runs the specialized pricing engine, and automatically saves a new record to the PostgreSQL instance.
4. **Real-Time Data Sync:** The record is instantly made available for real-time dashboard polling.
---
> ⚠️ **Technical Note About Hosting (Free Plan):** The application is hosted on Render’s free-tier infrastructure. If the system remains inactive for a few minutes, the server will enter hibernation mode (cold start). The first API request (such as trying to log in or sending the webhook payload) may take around **45 to 60 seconds** to wake up the Java/Spring Boot environment and establish the connection with the managed PostgreSQL instance. Subsequent requests will process instantaneously.

## 🚀 Key Features

* **AI Image Processing**: Integrates with the **Google Gemini API** (using the multimodal `gemini-2.5-flash` model) to read uploaded medical prescription images and intelligently parse text, medications, and pricing.
* **RESTful Architecture**: Clean, scalable, and well-documented HTTP endpoints for uploading files and managing budgets.
* **Robust Data Persistence**: Utilizes Spring Data JPA and Hibernate to reliably store budget information in a relational MySQL database.
* **Environment Security**: Uses external `.env` configuration to keep sensitive API keys and database credentials secure and out of the source code.
* **Clean Code & Tooling**: Built with Lombok for boilerplate reduction and structured utilizing standard Java/Spring design patterns (Controller-Service-Repository).

## 🛠️ Tech Stack

* **Backend Framework**: Java 17, Spring Boot 3.5
* **Artificial Intelligence**: Google Generative AI (Gemini 2.5 REST API)
* **Database & ORM**: MySQL, Spring Data JPA, Hibernate (with H2 for testing)
* **Utilities**: Lombok, Jackson (JSON Parsing), Maven

---

## ⚙️ How it Works

1. **Client Request**: The user sends a `POST` request containing a multipart file (the image of the prescription/budget).
2. **AI Analysis**: The API encodes the image in Base64 and sends a prompt to the Google Gemini AI, instructing it to extract the items and pricing into a strictly formatted JSON.
3. **Data Parsing**: The Spring backend receives the AI's markdown/JSON response, parses it using Jackson, and maps it to Java DTOs.
4. **Persistence**: The extracted items are processed, persisted into the MySQL database, and returned to the client as a structured financial budget.

---

## 📂 Directory Structure

```text
api-orcamento-manipulado/
├── src/
│   ├── main/
│   │   ├── java/com/farmacia/api_orcamento_manipulado/
│   │   │   ├── config/
│   │   │   │   └── JpaConfig.java
│   │   │   ├── controller/
│   │   │   │   └── OrcamentoController.java
│   │   │   ├── dto/
│   │   │   │   └── ItemExtraidoDTO.java
│   │   │   ├── model/
│   │   │   │   ├── ItemOrcamento.java
│   │   │   │   └── Orcamento.java
│   │   │   ├── repository/
│   │   │   │   └── OrcamentoRepository.java
│   │   │   ├── service/
│   │   │   │   ├── GeminiReceitaService.java
│   │   │   │   ├── IAReceitaService.java
│   │   │   │   ├── OpenAIReceitaService.java
│   │   │   │   └── OrcamentoService.java
│   │   │   └── ApiOrcamentoManipuladoApplication.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── java/com/farmacia/api_orcamento_manipulado/
│           ├── controller/
│           │   └── OrcamentoControllerTest.java
│           ├── model/
│           │   └── OrcamentoTest.java
│           ├── repository/
│           │   └── OrcamentoRepositoryTest.java
│           ├── service/
│           │   └── OrcamentoServiceTest.java
│           └── ApiOrcamentoManipuladoApplicationTests.java
├── pom.xml
├── README.md
├── HELP.md
└── Documento_de_Requisitos_API_Farmacia.docx
```

---

## 📈 Project Status / Roadmap

Based on the **System Requirements Document (DRS)**, the following modules are either functional or pending implementation.

### ✅ What has been developed
* **AI Data Extraction (RF-02):** Functional integration with Google Gemini Vision to extract medications, concentrations, and quantities from images sent via HTTP REST.
* **Base Data Persistence:** Initial database configuration (MySQL/H2) via Spring Data JPA with robustly mapped business entities.
* **Credentials Security (RNF-05):** Structural protection through secure reading of the `.env` file.
* **Automated Testing & Security Foundation (RNF-03):** All 26s tests are now passing with BUILD SUCCESS. We have consolidated the foundation of our information security, which is essential for compliance with the Brazilian LGPD and the secure handling of prescriptions controlled by Ordinance 344/98.

### 🚧 What is left to develop
* **WhatsApp Webhook Integration (RF-01, RF-05):** Enable receiving messages and directly sending approved budgets through the Meta WhatsApp Business API.
* **Pharmacist Validation Panel (RF-04, RN-01, RN-02):** The extracted budget needs to be pending; every submission requires human review and authorization before being sent to the client.
* **Price Calculation Engine (RF-03, RN-03):** Create/Configure the logic that calculates: `(Supplies Cost + Packaging) * Markup + Fixed Handling Fee (R$ 10)`.
* **Controlled Substances Alert (RN-04):** Logic to check if the mapped active ingredient is under Ordinance 344/98 (Brazil) and trigger a system warning.
* **Cloud Object Storage (RF-06):** Permanent storage of the original file (prescription photo) in Amazon S3 / Google Cloud Storage with a URL linked to the budget.
* **Advanced Authentication (RNF-01, RNF-02):** Further refinement of access using Spring Security (profiles and JWT tokens), and encrypting user passwords with BCrypt.

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

## 📬 Contact & Developer

[![GitHub](https://img.shields.io/badge/GitHub-181717?style=for-the-badge&logo=github&logoColor=white)](https://github.com/lu78abranches)

[![LinkedIn](https://img.shields.io/badge/LinkedIn-0077B5?style=for-the-badge&logo=linkedin&logoColor=white)](https://www.linkedin.com/in/luis-abranches/)

[![Gmail](https://img.shields.io/badge/Gmail-D14836?style=for-the-badge&logo=gmail&logoColor=white)](mailto:luisabranches.violao@gmail.com)

> *Note for Recruiters: This project highlights my ability to integrate modern cloud AI services into enterprise-grade Java backends, handle unstructured data formats, and build robust REST APIs using the Spring ecosystem.*
