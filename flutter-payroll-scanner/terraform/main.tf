resource "google_project_service" "firebase" {
  service = "firebase.googleapis.com"
  disable_on_destroy = false
}

resource "google_project_service" "firestore" {
  service = "firestore.googleapis.com"
  disable_on_destroy = false
}

resource "google_project_service" "identitytoolkit" {
  service = "identitytoolkit.googleapis.com"
  disable_on_destroy = false
}

resource "google_firestore_database" "database" {
  name        = "(default)"
  location_id = var.region
  type        = "FIRESTORE_NATIVE"
  depends_on  = [google_project_service.firestore]
}

resource "google_firebaserules_ruleset" "firestore" {
  source {
    files {
      name    = "firestore.rules"
      content = file("firestore.rules")
    }
  }
  depends_on = [google_project_service.firestore]
}

resource "google_firebaserules_release" "firestore" {
  name         = "cloud.firestore"
  ruleset_name = google_firebaserules_ruleset.firestore.name
  depends_on   = [google_project_service.firestore]
}

resource "google_identity_platform_config" "default" {
  sign_in {
    email {
      enabled = true
    }
  }
  depends_on = [google_project_service.identitytoolkit]
}

resource "google_firestore_index" "payroll_documents_date" {
  collection = "payroll_documents"
  database   = google_firestore_database.database.name
  fields {
    field_path = "userId"
    order      = "ASCENDING"
  }
  fields {
    field_path = "paymentDate"
    order      = "DESCENDING"
  }
}

resource "google_firestore_index" "payroll_documents_period" {
  collection = "payroll_documents"
  database   = google_firestore_database.database.name
  fields {
    field_path = "userId"
    order      = "ASCENDING"
  }
  fields {
    field_path = "payPeriod"
    order      = "DESCENDING"
  }
}
