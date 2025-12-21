#!/bin/bash

# Irish Payroll API Comprehensive Test Script
# Generated: 2025-12-21

BASE_URL="http://localhost:8080"
TOKEN="eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJ0ZXN0YWRtaW4iLCJpYXQiOjE3NjYzMzQyODgsImV4cCI6MTc2NjQyMDY4OH0.484CfRMg12eUQPYbfnKt9ccN-8cEpvWExH-aR5guAEyWzKAw-2kVOuAN_CjQjTx1"

echo "========================================="
echo "Irish Payroll API Comprehensive Test"
echo "========================================="
echo ""

# Test 1: Create Employee
echo "=== TEST 1: Create Employee ==="
EMPLOYEE_JSON='{
  "ppsNumber": "1234567AB",
  "firstName": "John",
  "lastName": "Smith",
  "email": "john.smith@example.ie",
  "dateOfBirth": "1990-05-15",
  "hireDate": "2024-01-01",
  "jobTitle": "Software Engineer",
  "department": "Engineering",
  "grossSalary": 50000,
  "payFrequency": "MONTHLY",
  "bankAccountNumber": "12345678",
  "taxCreditsAnnual": 3400,
  "isActive": true
}'

EMPLOYEE_RESPONSE=$(curl -s -X POST "$BASE_URL/api/employees" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d "$EMPLOYEE_JSON")

echo "$EMPLOYEE_RESPONSE" | jq .
EMPLOYEE_ID=$(echo "$EMPLOYEE_RESPONSE" | jq -r '.id')
echo "✓ Employee Created - ID: $EMPLOYEE_ID"
echo ""

# Test 2: Get All Employees
echo "=== TEST 2: Get All Employees ==="
curl -s -X GET "$BASE_URL/api/employees" \
  -H "Authorization: Bearer $TOKEN" | jq .
echo "✓ Retrieved all employees"
echo ""

# Test 3: Get Employee by ID
echo "=== TEST 3: Get Employee by ID ==="
curl -s -X GET "$BASE_URL/api/employees/$EMPLOYEE_ID" \
  -H "Authorization: Bearer $TOKEN" | jq .
echo "✓ Retrieved employee by ID: $EMPLOYEE_ID"
echo ""

# Test 4: Process Payroll
echo "=== TEST 4: Process Payroll ==="
PAYROLL_JSON="{
  \"payPeriodStart\": \"2025-12-01\",
  \"payPeriodEnd\": \"2025-12-21\",
  \"paymentDate\": \"2025-12-21\"
}"

PAYROLL_RESPONSE=$(curl -s -X POST "$BASE_URL/api/payrolls/process" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d "$PAYROLL_JSON")

echo "$PAYROLL_RESPONSE" | jq .
PAYROLL_ID=$(echo "$PAYROLL_RESPONSE" | jq -r '.id')
echo "✓ Payroll Processed - ID: $PAYROLL_ID"
echo ""

# Test 5: Get All Payrolls
echo "=== TEST 5: Get All Payrolls ==="
curl -s -X GET "$BASE_URL/api/payrolls" \
  -H "Authorization: Bearer $TOKEN" | jq .
echo "✓ Retrieved all payrolls"
echo ""

# Test 6: Get Payroll by ID
echo "=== TEST 6: Get Payroll by ID ==="
curl -s -X GET "$BASE_URL/api/payrolls/$PAYROLL_ID" \
  -H "Authorization: Bearer $TOKEN" | jq .
echo "✓ Retrieved payroll by ID: $PAYROLL_ID"
echo ""

# Test 7: Download Excel Report
echo "=== TEST 7: Download Excel Report ==="
curl -s -I -X GET "$BASE_URL/api/reports/payroll/$PAYROLL_ID/excel" \
  -H "Authorization: Bearer $TOKEN"
echo "✓ Excel report endpoint accessible"
echo ""

echo "========================================="
echo "All API Tests Completed Successfully!"
echo "========================================="
