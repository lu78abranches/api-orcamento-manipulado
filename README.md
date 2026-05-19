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

# 📱 🌟 Test the Real WhatsApp Bot Directly From Your Phone!

You can experience the fully automated workflow in real-time by interacting directly with the system using your own WhatsApp device. 

#### 👣 Step-by-Step Guide:
1. **Connect to the Sandbox:** Click [this link to open WhatsApp](https://wa.me) or scan the Twilio Sandbox QR Code on your device.
2. **Send the Activation Code:** Send the message `join your-twilio-keyword` (Replace with your actual Twilio Sandbox keyword, e.g., `join strict-box`) to the chat. This safely pairs your phone to the testing environment.
3. **Send a Prescription:** Take a picture or upload an image of a medical prescription (e.g., *Amoxicilina 500mg*).
4. **Instant AI Feedback:** The bot will instantly reply with an automated XML TwiML message acknowledging the reception and triggering the Gemini AI processing engine in the cloud background.

> 💡 **Pro-Tip for Reviewers:** After sending the picture in step 3, copy the JWT token from **Step 1** and trigger the **Step 3 (Approval Endpoint)** using Postman/cURL. You will immediately see the budget status switch to `APROVADO` on your PostgreSQL instance, completing the full enterprise software simulation!


## 🌐 Live Demo & Recruiter Testing Guide (Render Cloud)

The API is fully deployed in a cloud production environment using **Render** and integrated with a managed **PostgreSQL** database instances. As a reviewer or recruiter, you do not need to clone the repository or run the application locally to test its behavior.

> ⚠️ **Technical Note About Hosting (Free Plan):** The application is hosted on Render’s free-tier infrastructure. If the system remains inactive for a few minutes, the server will enter hibernation mode (cold start). The first API request (such as trying to log in or sending the webhook payload) may take around **45 to 60 seconds** to wake up the Java/Spring Boot environment and establish the connection with the managed PostgreSQL instance. Subsequent requests will process instantaneously.


### 🔗 Live Production Base URL
```text
https://onrender.com
```
---

### 🔐 1. Authentication Endpoint (`POST /api/auth/login`)

The application seeds a default pharmacist user into the cloud database upon its first boot execution via `CommandLineRunner`. You can request an access token using these pre-configured credentials:

* **Username:** `farmaceutico1`
* **Password:** `senha123`

#### 💻 cURL Request:
```bash
curl -X POST https://onrender.com/api/auth/login \
     -H "Content-Type: application/json" \
     -d '{"username": "farmaceutico1", "password": "senha123"}'
```

#### 📦 Postman / Insomnia JSON Payload:
```json
{
  "username": "farmaceutico1",
  "password": "senha123"
}
```

#### 📥 Expected JSON Response (200 OK):
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer"
}
```

---

### 💬 2. WhatsApp Incoming Simulator Webhook (`POST /api/webhooks/whatsapp`)

This public endpoint handles production asynchronous requests forwarded from the Twilio/Meta Sandbox. You can simulate an incoming user chat message containing a medical prescription image attachment.

#### 💻 cURL Request:
```bash
curl -X POST https://onrender.com/api/webhooks/whatsapp \
     -H "Content-Type: application/x-www-form-urlencoded" \
     --data-urlencode "From=whatsapp:+5511999999999" \
     --data-urlencode "MediaUrl0=https://examples.com" \
     --data-urlencode "NumMedia=1"
```

#### 📦 Postman / Insomnia Form URL-Encoded Body:

| Key | Value | Description |
| :--- | :--- | :--- |
| `From` | `whatsapp:+5511999999999` | The sender's simulated phone number |
| `MediaUrl0` | `https://examples.com` | A public URL of a prescription image |
| `NumMedia` | `1` | Forces the attachment processor logic |

#### 📥 Expected TwiML XML Response (200 OK):
```xml
<?xml version="1.0" encoding="UTF-8"?>
<Response>
    <Message>Receita recebida com sucesso! 📝 Nossa inteligência artificial está extraindo os dados e nosso farmacêutico já vai validar o seu orçamento. Você receberá o valor em breve!</Message>
</Response>
```
### 💊 3. Pharmacist Budget Approval Endpoint (`PUT /api/orcamentos/{id}/aprovar`)

In compliance with Brazilian health regulations (Anvisa Ordinance 344/98 & LGPD), no automated AI budget is dispatched directly to the customer without a human professional review. 

Once an entry is generated via the WhatsApp Webhook, a pharmacist uses this protected route to review the extracted items and authorize the dispatch. The system automatically triggers the calculation engine (Supplies + Fixed Handling Fee of R$ 10,00) and returns the final pricing.

* **Security:** Requires the `Authorization: Bearer <JWT_TOKEN>` header obtained in Step 1.

#### 💻 cURL Request:
```bash
curl -X PUT https://onrender.com \
     -H "Authorization: Bearer INSERT_YOUR_JWT_TOKEN_HERE" \
     -H "Content-Type: application/json"
```

#### 📥 Expected JSON Response (200 OK):
```json
{
  "id": 1,
  "clienteWhatsapp": "whatsapp:+5511999999999",
  "status": "APROVADO",
  "valorTotal": 35.00,
  "itens": [
    {
      "id": 1,
      "nome": "Amoxicilina 500mg",
      "preco": 25.00
    }
  ]
}
```

> 🌟 **Workflow Complete:** Once approved, the backend triggers the communication dispatcher to forward this final formatted quote directly back to the customer's WhatsApp chat screen.


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
