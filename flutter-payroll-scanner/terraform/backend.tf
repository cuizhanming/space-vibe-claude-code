terraform {
  cloud {
    organization = "your-organization-name"

    workspaces {
      name = "flutter-payroll-scanner-prod"
    }
  }
}
