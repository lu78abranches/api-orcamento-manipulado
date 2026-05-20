# 🧴 API Orçamento Manipulado - 3-Step Prescription Flow

## Overview

The application now implements a 3-step workflow for prescription processing and quote generation:

1. **STEP 1 - Upload Prescription** (Public)
2. **STEP 2 - Pharmacist Approves** (JWT Required)
3. **STEP 3 - Generate Final Quote** (JWT Required)

The data format has been converted from raw JSON to Markdown for frontend display.

---

## Data Processing Pipeline

```
Prescription Image (uploaded)
     ↓
AI Service → extracts item names
     ↓
PriceCalculationEngine → normalizes prices
     ↓
OrcamentoService → converts to clean DTOs
     ↓
Frontend receives Markdown formatted response
```

---

## API Endpoints

### STEP 1: Upload Prescription

**Endpoint:** `POST /api/orcamentos/upload`

**Authentication:** None (Public)

**Request:**
```
POST /api/orcamentos/upload
Content-Type: multipart/form-data

imagem: <image_file>
```

**Response (200 OK):**
```json
{
  "protocol": 4,
  "status": "PENDING_REVIEW",
  "message": "Prescrição enviada para análise farmacêutica"
}
```

**Response Format:**
- `protocol` (Long): Unique prescription ID for tracking
- `status` (String): Always "PENDING_REVIEW" on initial upload
- `message` (String): Portuguese confirmation message

**Processing:**
1. Image uploaded to backend
2. AI extracts items from prescription
3. PriceCalculationEngine normalizes prices
4. OrcamentoService creates preliminary quote
5. Returns minimal DTO for frontend

---

### STEP 2: Pharmacist Approves

**Endpoint:** `PUT /api/orcamentos/{id}/aprovar`

**Authentication:** JWT Required (Bearer Token)

**Request:**
```
PUT /api/orcamentos/4/aprovar
Authorization: Bearer <JWT_TOKEN>
```

**Response (200 OK):**
```json
{
  "status": "APPROVED"
}
```

**Response Format:**
- `status` (String): "APPROVED" to confirm pharmacist review

**Security:**
- Requires valid JWT token in Authorization header
- Token obtained via `/api/auth/login` endpoint
- Only pharmacist users can approve

**Example with cURL:**
```bash
curl -X PUT http://localhost:8080/api/orcamentos/4/aprovar \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

---

### STEP 3: Generate Final Quote

**Endpoint:** `GET /api/orcamentos/{id}/final-quote`

**Authentication:** JWT Required (Bearer Token)

**Request:**
```
GET /api/orcamentos/4/final-quote
Authorization: Bearer <JWT_TOKEN>
```

**Response (200 OK):**
```json
{
  "protocol": 4,
  "status": "APPROVED",
  "totalValue": 34.00,
  "markdownContent": "# 💊 Orçamento Aprovado\n\n**Protocolo:** 4\n\n**Status:** APPROVED\n\n## Itens Solicitados\n\n- **Vitamina C:** R$ 12.00\n- **Amoxicilina 500mg:** R$ 25.00\n\n## Resumo de Valores\n\n| Descrição | Valor |\n|-----------|-------|\n| Subtotal dos Insumos | R$ 37.00 |\n| Taxa de Manipulação | R$ 10.00 |\n| **VALOR TOTAL** | **R$ 47.00** |\n\n✅ Seu orçamento foi aprovado por nosso farmacêutico!\n🚀 Seu pedido já pode ser enviado para manipulação.\n📞 Entre em contato para confirmar a entrega.\n"
}
```

**Response Format:**
- `protocol` (Long): Same protocol ID from STEP 1
- `status` (String): "APPROVED" indicating approved status
- `totalValue` (BigDecimal): Final quote amount in Brazilian Real
- `markdownContent` (String): Complete quote formatted as Markdown for frontend rendering

**Frontend Usage:**
The `markdownContent` should be rendered as Markdown (HTML conversion) to display:
- Header with 💊 emoji
- Protocol and status
- Itemized list of medications
- Summary table with subtotal, manipulation fee, and total
- Friendly confirmation message with emojis

---

## DTOs (Data Transfer Objects)

### OrcamentoPendingReviewDTO
```java
public record OrcamentoPendingReviewDTO(
    Long protocol,
    String status,
    String message
) {}
```

### OrcamentoApprovedDTO
```java
public record OrcamentoApprovedDTO(
    String status
) {}
```

### OrcamentoFinalQuoteDTO
```java
public record OrcamentoFinalQuoteDTO(
    Long protocol,
    String status,
    BigDecimal totalValue,
    String markdownContent
) {}
```

---

## Error Handling

### Common Errors

**400 Bad Request:** Invalid prescription image
```json
{
  "error": "Imagem inválida ou não é uma prescrição"
}
```

**401 Unauthorized:** Missing or invalid JWT token
```json
{
  "error": "Unauthorized"
}
```

**404 Not Found:** Protocol ID doesn't exist
```json
{
  "error": "Orçamento não encontrado: {id}"
}
```

**409 Conflict:** Attempting to get final quote on non-approved prescription
```json
{
  "error": "Orçamento não foi aprovado"
}
```

---

## Security

### JWT Authentication Flow

1. **Login:** POST `/api/auth/login`
   ```json
   {
     "username": "farmaceutico1",
     "password": "senha123"
   }
   ```

2. **Response:**
   ```json
   {
     "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
     "type": "Bearer"
   }
   ```

3. **Use Token:** Include in all authenticated requests
   ```
   Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
   ```

### Protected Endpoints

- `PUT /api/orcamentos/{id}/aprovar` - STEP 2 (Pharmacist approval)
- `GET /api/orcamentos/{id}/final-quote` - STEP 3 (Final quote)

### Public Endpoints

- `POST /api/orcamentos/upload` - STEP 1 (Prescription upload)
- `POST /api/auth/login` - Authentication

---

## Example Complete Flow

### 1. Upload Prescription (No Auth Required)
```bash
curl -X POST http://localhost:8080/api/orcamentos/upload \
  -F "imagem=@receita.jpg"

Response:
{
  "protocol": 4,
  "status": "PENDING_REVIEW",
  "message": "Prescrição enviada para análise farmacêutica"
}
```

### 2. Pharmacist Reviews and Approves (With Auth)
```bash
# First, get JWT token
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "farmaceutico1", "password": "senha123"}'

Response:
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer"
}

# Approve prescription
curl -X PUT http://localhost:8080/api/orcamentos/4/aprovar \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."

Response:
{
  "status": "APPROVED"
}
```

### 3. Generate Final Quote (With Auth)
```bash
curl -X GET http://localhost:8080/api/orcamentos/4/final-quote \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."

Response:
{
  "protocol": 4,
  "status": "APPROVED",
  "totalValue": 34.00,
  "markdownContent": "# 💊 Orçamento Aprovado\n..."
}
```

---

## Frontend Integration

### Markdown Rendering

The `markdownContent` from STEP 3 should be rendered using a Markdown library (e.g., `react-markdown`):

```jsx
import ReactMarkdown from 'react-markdown';

function QuoteDisplay({ quote }) {
  return (
    <div className="quote-container">
      <ReactMarkdown>{quote.markdownContent}</ReactMarkdown>
    </div>
  );
}
```

### State Management

Recommended flow for frontend state:
1. Store `protocol` ID after STEP 1
2. After STEP 2 confirmation, enable STEP 3 button
3. Display `markdownContent` as formatted quote to user
4. Show `totalValue` as the final price

---

## Price Calculation

### Price Normalization Engine

The `PriceCalculationEngine` normalizes prices for common pharmaceutical items:

- **Base Medication Prices:** 
  - Vitamina C: R$ 12.00
  - Amoxicilina: R$ 25.00
  - Paracetamol: R$ 8.50
  - (See full table in `PriceCalculationEngine.java`)

- **Fixed Manipulation Fee:** R$ 10.00 (always added to total)

- **Custom Pricing:** If item not found in table:
  ```
  Price = (item_name_length × 1.20) + 5.00
  ```

---

## Status Values

The application tracks prescriptions through these statuses:

- `PENDENTE_REVISAO` (PENDING_REVIEW): Initial upload, awaiting pharmacist review
- `APROVADO` (APPROVED): Pharmacist has approved
- `RECUSADO` (REFUSED): Pharmacist rejected the prescription
- `REJEITADO` (REJECTED): System rejected (invalid image, etc.)

---

## Testing

All 26 unit and integration tests pass:

```bash
mvn clean test
# Results: 26 tests passed, 0 failures
```

Test coverage includes:
- Controller endpoint validation
- Service business logic
- Security filter and JWT validation
- Repository operations
- Price calculation engine
- Integration tests for authentication flow
