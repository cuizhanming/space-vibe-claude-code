# Production Architecture & Deployment Guide

## System Architecture Diagram

```txt
┌─────────────────────────────────────────────────────────────────────────────┐
│                              CLIENT LAYER                                   │
│                                                                             │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐     │
│  │   iOS App    │  │ Android App  │  │   Web App    │  │ Desktop Apps │     │
│  │              │  │              │  │              │  │ (Mac/Win/Lin)│     │
│  └──────┬───────┘  └──────┬───────┘  └──────┬───────┘  └──────┬───────┘     │
│         │                 │                 │                 │             │
│         └─────────────────┴─────────────────┴─────────────────┘             │
│                                    │                                        │
└────────────────────────────────────┼────────────────────────────────────────┘
                                     │
                                     │ HTTPS + Firebase Auth Token
                                     │
┌────────────────────────────────────▼────────────────────────────────────────┐
│                         FIREBASE SERVICES (Google Cloud)                    │
│                                                                             │
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐              │
│  │ Firebase Auth   │  │   Firestore     │  │ Firebase Storage│              │
│  │ - User Accounts │  │ - Documents     │  │ - Images/PDFs   │              │
│  │ - Auth Tokens   │  │ - Cache         │  │ - File Storage  │              │
│  │ - Email/Password│  │ - Sync Data     │  │                 │              │
│  └─────────────────┘  └─────────────────┘  └─────────────────┘              │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
                                     │
                                     │ Authenticated Requests
                                     │
┌────────────────────────────────────▼────────────────────────────────────────┐
│                         BACKEND SERVICE LAYER                               │
│                                                                             │
│  ┌────────────────────────────────────────────────────────────────┐         │
│  │                     Load Balancer / API Gateway                │         │
│  │                  (Cloud Run / App Engine / ALB)                │         │
│  └──────────────────────────┬─────────────────────────────────────┘         │
│                             │                                               │
│  ┌──────────────────────────▼──────────────────────────┐                    │
│  │          Backend API Service (Node.js/Express)      │                    │
│  │                                                     │                    │
│  │  Features:                                          │                    │
│  │  • Firebase Auth Token Verification                 │                    │
│  │  • Rate Limiting (10 req/min/user)                  │                    │
│  │  • Request Validation & Sanitization                │                    │
│  │  • Response Caching (Firestore)                     │                    │
│  │  • Usage Tracking & Analytics                       │                    │
│  │  • Error Handling & Logging                         │                    │
│  │  • Security Headers (Helmet, CORS)                  │                    │
│  └──────────────┬────────────────────┬─────────────────┘                    │
│                 │                    │                                      │
│                 │                    │                                      │
│      ┌──────────▼─────────┐  ┌──────▼──────────┐                            │
│      │   Cache Layer      │  │  Monitoring     │                            │
│      │   (Firestore)      │  │  & Logging      │                            │
│      │   - 24hr TTL       │  │  - CloudWatch   │                            │
│      │   - Cost Savings   │  │  - Sentry       │                            │
│      └────────────────────┘  │  - Analytics    │                            │
│                              └─────────────────┘                            │
└────────────────────────────────────┬────────────────────────────────────────┘
                                     │
                                     │ GEMINI_API_KEY (Secured)
                                     │
┌────────────────────────────────────▼────────────────────────────────────────┐
│                         EXTERNAL AI SERVICES                                │
│                                                                             │
│  ┌─────────────────────────────────────────────────────────────┐            │
│  │              Google Gemini AI API                           │            │
│  │              (gemini-2.0-flash-exp)                         │            │
│  │                                                             │            │
│  │  • Document Analysis                                        │            │
│  │  • Irish Payroll Data Extraction                            │            │
│  │  • PPS Number, PAYE, PRSI, USC Recognition                  │            │
│  └─────────────────────────────────────────────────────────────┘            │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘


┌─────────────────────────────────────────────────────────────────────────────┐
│                         SECURITY LAYERS                                     │
│                                                                             │
│  1. Client → Backend:    HTTPS + Firebase Auth Token                        │
│  2. Backend → Firebase:  Service Account / Admin SDK                        │
│  3. Backend → Gemini:    API Key (Server-Side Only)                         │
│  4. Data at Rest:        Firestore Encryption, Secure Storage               │
│  5. Data in Transit:     TLS 1.3, Certificate Pinning (optional)            │
│  6. Rate Limiting:       Per-User, Per-Endpoint                             │
│  7. Input Validation:    All inputs sanitized                               │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

## Deployment Components

### 1. Client Applications

**Platforms**: iOS, Android, Web, macOS, Windows, Linux

**Deployment Targets**:
- **iOS**: App Store (TestFlight for beta)
- **Android**: Google Play Store (Internal/Beta tracks)
- **Web**: Firebase Hosting / Vercel / Netlify
- **Desktop**: Direct download / Microsoft Store / Mac App Store

**Build Artifacts**:

- iOS: `.ipa` file
- Android: `.apk` / `.aab` (Android App Bundle)
- Web: Static files (HTML/JS/CSS)
- Desktop: Platform-specific executables

### 2. Backend Service

**Deployment Options**:

**Option A: Firebase Cloud Functions** (Recommended for Firebase integration)

```bash
Location: us-central1
Runtime: Node.js 20
Memory: 512MB
Timeout: 60s
Min Instances: 0 (scales to zero)
Max Instances: 100
```

**Option B: Google Cloud Run** (Recommended for flexibility)

```bash
Location: us-central1
Container: Node.js 20 Alpine
CPU: 1 vCPU
Memory: 512Mi
Min Instances: 0
Max Instances: 100
Concurrency: 80
```

**Option C: AWS**

```bash
Service: AWS Lambda + API Gateway
Runtime: Node.js 20
Memory: 512MB
Or: ECS Fargate for containerized deployment
```

### 3. Firebase Services

**Configuration**:

- **Authentication**: Email/Password enabled
- **Firestore**: Multi-region (for HA)
- **Storage**: Multi-region
- **Security Rules**: Enforced
- **App Check**: Enabled (prevents API abuse)

### 4. Monitoring & Logging

**Services**:

- **Google Cloud Logging**: Backend logs
- **Firebase Crashlytics**: Mobile crash reports
- **Sentry**: Error tracking
- **Google Analytics**: User analytics
- **Cloud Monitoring**: Metrics & alerts

### 5. CI/CD Pipeline

**Tools**: GitHub Actions (or GitLab CI, Bitbucket Pipelines)

**Environments**:

- **Development**: Auto-deploy on push to `develop` branch
- **Staging**: Auto-deploy on push to `staging` branch
- **Production**: Manual approval on push to `main` branch

## Data Flow

### Upload & Extract Flow

```bash
1. User uploads image
   ↓
2. Flutter App
   • Validates file (size, type)
   • Compresses image
   • Gets Firebase auth token
   ↓
3. Backend Service
   • Verifies auth token
   • Checks rate limit
   • Checks cache (Firestore)
   • If cached → return immediately
   • If not cached → call Gemini API
   ↓
4. Gemini API
   • Analyzes image
   • Extracts Irish payroll data
   • Returns JSON
   ↓
5. Backend Service
   • Validates response
   • Saves to cache (Firestore)
   • Logs usage
   • Returns to client
   ↓
6. Flutter App
   • Parses JSON
   • Saves to local DB (Drift)
   • Syncs to Firestore
   • Displays to user
```

## Cost Estimation (Monthly)

### Small Scale (100 active users)

- Firebase (Auth + Firestore + Storage): $25-50
- Cloud Run/Functions: $10-20
- Gemini API: $30-100 (with caching)
- **Total**: ~$65-170/month

### Medium Scale (1,000 active users)

- Firebase: $100-200
- Cloud Run/Functions: $50-100
- Gemini API: $200-500 (with caching)
- **Total**: ~$350-800/month

### Large Scale (10,000 active users)

- Firebase: $500-1,000
- Cloud Run/Functions: $200-500
- Gemini API: $1,500-3,000 (with caching)
- **Total**: ~$2,200-4,500/month

**Cost Optimization**:

- Cache responses (24hr) reduces Gemini API costs by 70-80%
- Use Gemini Flash model (cheaper than Pro)
- Implement aggressive rate limiting
- Compress images before sending

## Scalability

### Current Architecture Handles

- **Requests/sec**: 100+ (with auto-scaling)
- **Concurrent Users**: 1,000+
- **Storage**: Unlimited (Firebase/Cloud Storage)
- **Geographic Distribution**: Multi-region possible

### Scaling Strategy

1. **Horizontal**: Add more backend instances
2. **Caching**: Redis/Memcached for hot data
3. **CDN**: CloudFlare for static assets
4. **Database**: Firestore auto-scales
5. **Queue**: Cloud Tasks for batch processing
