# 💊 AI-Powered Pharmacy Budget API (API Orçamento Manipulado)

[![Java 17](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.x-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Gemini AI](https://img.shields.io/badge/Google%20Gemini-AI%20Integration-blue.svg)](https://ai.google.dev/)
[![MySQL](https://img.shields.io/badge/Database-MySQL-lightgrey.svg)](https://www.mysql.com/)

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
