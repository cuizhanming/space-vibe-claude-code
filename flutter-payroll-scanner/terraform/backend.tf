terraform { 
  cloud { 
    organization = "cuizhanming-com" 

    workspaces { 
      name = "flutter-payroll-scanner-prod" 
    } 
  } 
}