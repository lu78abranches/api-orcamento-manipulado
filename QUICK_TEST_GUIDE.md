# Quick Test Guide - 3-Step Prescription Flow

## 🚀 Quick Start

### Prerequisites
- Java 17
- Maven installed
- Application running on `http://localhost:8080`

### Build & Run Tests
```bash
cd c:\api-orcamento-manipulado

# Build application
mvn clean package -DskipTests

# Run all tests (should pass 26/26)
mvn test
```

---

## 🧪 Testing the 3-Step Flow

### Option 1: Using cURL (Command Line)

#### STEP 1: Upload Prescription
```bash
curl -X POST http://localhost:8080/api/orcamentos/upload \
  -F "imagem=@receita.jpg"
```

**Expected Response:**
```json
{
  "protocol": 4,
  "status": "PENDING_REVIEW",
  "message": "Prescrição enviada para análise farmacêutica"
}
```

**Save the protocol ID (e.g., 4) for next steps**

---

#### STEP 2: Get JWT Token (Required for next steps)
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "farmaceutico1",
    "password": "senha123"
  }'
```

**Expected Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer"
}
```

**Copy the token for authenticated requests**

---

#### STEP 3: Pharmacist Approves (With JWT)
```bash
# Replace:
# - 4 with your protocol ID
# - TOKEN with your JWT token from above

curl -X PUT http://localhost:8080/api/orcamentos/4/aprovar \
  -H "Authorization: Bearer TOKEN"
```

**Expected Response:**
```json
{
  "status": "APPROVED"
}
```

---

#### STEP 4: Generate Final Quote (With JWT)
```bash
# Replace:
# - 4 with your protocol ID
# - TOKEN with your JWT token

curl -X GET http://localhost:8080/api/orcamentos/4/final-quote \
  -H "Authorization: Bearer TOKEN"
```

**Expected Response:**
```json
{
  "protocol": 4,
  "status": "APPROVED",
  "totalValue": 34.00,
  "markdownContent": "# 💊 Orçamento Aprovado\n\n**Protocolo:** 4\n\n**Status:** APPROVED\n\n## Itens Solicitados\n\n- **Item 1:** R$ 12.00\n- **Item 2:** R$ 22.00\n\n## Resumo de Valores\n\n| Descrição | Valor |\n|-----------|-------|\n| Subtotal dos Insumos | R$ 34.00 |\n| Taxa de Manipulação | R$ 10.00 |\n| **VALOR TOTAL** | **R$ 44.00** |\n\n✅ Seu orçamento foi aprovado por nosso farmacêutico!\n🚀 Seu pedido já pode ser enviado para manipulação.\n📞 Entre em contato para confirmar a entrega.\n"
}
```

---

### Option 2: Using Postman

#### Create Collection with 4 Requests:

**1. Upload Prescription**
- Method: POST
- URL: `http://localhost:8080/api/orcamentos/upload`
- Body: form-data
  - Key: `imagem` (File type)
  - Value: Select an image file (receipt/prescription image)

**2. Get JWT Token**
- Method: POST
- URL: `http://localhost:8080/api/auth/login`
- Headers: `Content-Type: application/json`
- Body (raw JSON):
  ```json
  {
    "username": "farmaceutico1",
    "password": "senha123"
  }
  ```
- Save token from response in Postman environment variable

**3. Approve Prescription**
- Method: PUT
- URL: `http://localhost:8080/api/orcamentos/{{protocol}}/aprovar`
- Headers: `Authorization: Bearer {{token}}`
- No body needed

**4. Get Final Quote**
- Method: GET
- URL: `http://localhost:8080/api/orcamentos/{{protocol}}/final-quote`
- Headers: `Authorization: Bearer {{token}}`
- No body needed

---

## ✅ Verification Checklist

- [ ] STEP 1 returns protocol, status=PENDING_REVIEW, message in Portuguese
- [ ] STEP 2 login returns valid JWT token
- [ ] STEP 2 approval returns only status=APPROVED
- [ ] STEP 3 quote returns protocol, status, totalValue, markdownContent
- [ ] STEP 3 markdownContent is valid Markdown format
- [ ] All 26 unit tests pass
- [ ] Application builds successfully

---

## 📝 Response Format Verification

### STEP 1 - Minimal Response
```
✓ Has "protocol" (long)
✓ Has "status" = "PENDING_REVIEW"
✓ Has "message" in Portuguese
✗ Does NOT have full quote details
```

### STEP 2 - Simple Approval
```
✓ Has "status" = "APPROVED"
✗ Does NOT have protocol or quote details
```

### STEP 3 - Full Quote with Markdown
```
✓ Has "protocol"
✓ Has "status" = "APPROVED"
✓ Has "totalValue" (number)
✓ Has "markdownContent" (text)
✓ markdownContent is valid Markdown
✓ Includes Markdown table
✓ Includes friendly message with emojis
```

---

## 🔐 Security Verification

### Test Public Access (No Auth)
✅ Should work:
```bash
curl -X POST http://localhost:8080/api/orcamentos/upload -F "imagem=@file.jpg"
```

✅ Should work:
```bash
curl -X POST http://localhost:8080/api/auth/login -d '{"username":"farmaceutico1","password":"senha123"}'
```

### Test Protected Endpoints (Requires Auth)
❌ Should FAIL (401 Unauthorized):
```bash
curl -X PUT http://localhost:8080/api/orcamentos/4/aprovar
```

✅ Should WORK (with token):
```bash
curl -X PUT http://localhost:8080/api/orcamentos/4/aprovar \
  -H "Authorization: Bearer YOUR_TOKEN"
```

---

## 🐛 Troubleshooting

### Issue: "Orçamento não encontrado"
**Solution:** Use the protocol ID returned from STEP 1

### Issue: "401 Unauthorized" on STEP 2/3
**Solution:** 
1. Get token from login endpoint
2. Include in Authorization header: `Authorization: Bearer TOKEN`

### Issue: "Orçamento não foi aprovado" on STEP 3
**Solution:** STEP 3 only works after STEP 2 approval. Approve first, then get quote.

### Issue: Image upload fails
**Solution:** Ensure image file is readable and is actually an image file (jpg, png, etc.)

---

## 📊 API Summary

| STEP | Endpoint | Method | Auth | Response |
|------|----------|--------|------|----------|
| 1 | `/api/orcamentos/upload` | POST | ❌ No | OrcamentoPendingReviewDTO |
| Auth | `/api/auth/login` | POST | ❌ No | LoginResponseDTO (JWT token) |
| 2 | `/api/orcamentos/{id}/aprovar` | PUT | ✅ Yes | OrcamentoApprovedDTO |
| 3 | `/api/orcamentos/{id}/final-quote` | GET | ✅ Yes | OrcamentoFinalQuoteDTO |

---

## 📚 Documentation Files

- `API_FLOW_DOCUMENTATION.md` - Complete API reference
- `IMPLEMENTATION_SUMMARY.md` - Technical implementation details
- `QUICK_TEST_GUIDE.md` - This file (quick testing reference)

---

## 🎯 Next Steps

1. **Test the flow** using cURL or Postman
2. **Verify all responses** match expected formats
3. **Check Markdown rendering** in frontend
4. **Integrate with frontend** to display Markdown content
5. **Deploy to production** when ready

---

## 📞 Support

For issues or questions:
1. Check API_FLOW_DOCUMENTATION.md for detailed API reference
2. Review IMPLEMENTATION_SUMMARY.md for technical details
3. Run `mvn test` to verify all tests pass
4. Check application logs for error details
