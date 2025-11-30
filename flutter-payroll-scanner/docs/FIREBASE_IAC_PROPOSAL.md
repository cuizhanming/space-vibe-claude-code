# Firebase Infrastructure as Code (IaC) Proposal

This document outlines the plan to automate the Firebase setup described in `FIREBASE_SETUP.md` using Terraform and GitHub Actions.

## 1. Overview

**Goal**: Replace manual Firebase configuration with an automated, version-controlled Infrastructure as Code (IaC) pipeline.
**Tools**: Terraform (Infrastructure), GitHub Actions (CI/CD), Google Cloud Platform (GCP).

## 2. Prerequisites

1.  **Google Cloud Project**: An existing project (e.g., `flutter-payroll-scanner`).
2.  **Billing Enabled (Blaze Plan)**: Required for automating Firebase Authentication via Terraform (Identity Platform API).
3.  **Terraform**: Installed locally for initial setup.
4.  **GitHub Repository**: Access to configure Secrets and Actions.

## 3. Architecture

-   **Terraform State**: Stored in **Terraform Cloud** (HCP Terraform) Free Tier.
-   **Execution**: GitHub Actions runs Terraform, using TFC for state storage (Remote Backend).
-   **Authentication**: GitHub Actions uses **Workload Identity Federation** to authenticate with GCP without long-lived service account keys.
-   **Resources Managed**:
    -   Enabled APIs (Firestore, Auth, Identity Toolkit)
    -   Firestore Database & Indexes
    -   Security Rules (Firestore)
    -   Authentication Providers (Email/Password, Google)
    -   **Note**: Storage is managed via **Google Drive API** (per login user-owned), not Firebase Storage.

## 4. Implementation Steps

### Step 1: GCP Setup & Workload Identity

1.  **Terraform Cloud Setup**:
    -   Create a free account at [app.terraform.io](https://app.terraform.io).
    -   Create an Organization.
    -   Create a Workspace (e.g., `flutter-payroll-scanner-prod`).
    -   Set "Execution Mode" to **Local** (since we want GitHub Actions to execute Terraform).
    -   Generate a **User API Token** or **Team API Token**.
2.  **GCP Setup**:
    -   Create a Service Account for Terraform (e.g., `terraform-runner@<project-id>.iam.gserviceaccount.com`).
3.  Grant necessary roles:
    -   `roles/editor` (or granular roles like `roles/firebase.admin`, `roles/resourcemanager.projectIamAdmin`).
4.  Configure **Workload Identity Federation**:
    -   Create a Workload Identity Pool and Provider.
    -   Allow the GitHub Action to impersonate the Service Account.

### Step 2: Terraform Configuration

Create a `terraform/` directory with the following structure:

```
terraform/
├── main.tf          # Main resource definitions
├── backend.tf       # Terraform Cloud backend config
├── variables.tf     # Input variables
├── outputs.tf       # Output values
├── provider.tf      # Provider configuration
├── provider.tf      # Provider configuration
└── firestore.rules  # Copied from FIREBASE_SETUP.md
```

#### Backend Configuration (`backend.tf`)

```hcl
terraform {
  cloud {
    organization = "your-organization-name"

    workspaces {
      name = "flutter-payroll-scanner-prod"
    }
  }
}
```

#### Key Resources in `main.tf`

**1. Enable APIs:**
```hcl
resource "google_project_service" "firebase" {
  service = "firebase.googleapis.com"
}
resource "google_project_service" "firestore" {
  service = "firestore.googleapis.com"
}
resource "google_project_service" "identitytoolkit" {
  service = "identitytoolkit.googleapis.com" # Required for Auth
}
# Note: Storage API not needed as we use Google Drive
```

**2. Firestore Database & Rules:**
```hcl
resource "google_firestore_database" "database" {
  name        = "(default)"
  location_id = var.region
  type        = "FIRESTORE_NATIVE"
}

resource "google_firebaserules_ruleset" "firestore" {
  source {
    files {
      name    = "firestore.rules"
      content = file("firestore.rules")
    }
  }
}

resource "google_firebaserules_release" "firestore" {
  name         = "cloud.firestore"
  ruleset_name = google_firebaserules_ruleset.firestore.name
}
```



**4. Authentication (Identity Platform):**
```hcl
resource "google_identity_platform_config" "default" {
  sign_in {
    email {
      enabled = true
    }
  }
  # Google provider configuration would go here (requires client ID/secret)
}
```

**5. Firestore Indexes:**
```hcl
resource "google_firestore_index" "payroll_documents_date" {
  collection = "payroll_documents"
  fields {
    field_path = "userId"
    order      = "ASCENDING"
  }
  fields {
    field_path = "paymentDate"
    order      = "DESCENDING"
  }
}
# ... other indexes
```

### Step 3: GitHub Actions Workflow

Create `.github/workflows/firebase-iac.yml`:

```yaml
name: Firebase IaC

on:
  push:
    branches: [ main ]
    paths: [ 'terraform/**' ]
  pull_request:
    branches: [ main ]
    paths: [ 'terraform/**' ]

env:
  TF_VAR_project_id: ${{ vars.GCP_PROJECT_ID }}
  TF_VAR_region: "europe-west2"

jobs:
  terraform:
    runs-on: ubuntu-latest
    permissions:
      contents: 'read'
      id-token: 'write' # Required for Workload Identity

    steps:
    - uses: actions/checkout@v3

    - id: 'auth'
      uses: 'google-github-actions/auth@v1'
      with:
        workload_identity_provider: '${{ secrets.WIF_PROVIDER }}'
        service_account: '${{ secrets.WIF_SERVICE_ACCOUNT }}'

    - uses: hashicorp/setup-terraform@v2
      with:
        cli_config_credentials_token: ${{ secrets.TF_API_TOKEN }}

    - name: Terraform Init
      run: terraform init
      working-directory: ./terraform

    - name: Terraform Plan
      run: terraform plan
      working-directory: ./terraform

    - name: Terraform Apply
      if: github.ref == 'refs/heads/main' && github.event_name == 'push'
      run: terraform apply -auto-approve
      working-directory: ./terraform
```

## 5. Migration Strategy

If resources already exist (created manually per `FIREBASE_SETUP.md`), they must be **imported** into Terraform state to avoid errors or duplication.

**Import Commands:**
```bash
terraform import google_firestore_database.database "(default)"
terraform import google_storage_bucket.default <project-id>.appspot.com
# ... and so on for other resources
```

## 6. Secrets Management

-   **GitHub Secrets**:
    -   `WIF_PROVIDER`: Workload Identity Provider resource name.
    -   `WIF_SERVICE_ACCOUNT`: Service Account email.
    -   `TF_API_TOKEN`: Terraform Cloud API Token.
-   **GitHub Variables**:
    -   `GCP_PROJECT_ID`: The Google Cloud Project ID.

## 7. Next Steps

1.  Confirm if the project is on the **Blaze plan** (required for Auth IaC).
2.  Set up the GCP Service Account and Workload Identity.
3.  Initialize the `terraform/` directory with the code above.
4.  Run `terraform import` for existing resources.
5.  Commit and push to trigger the GitHub Action.
