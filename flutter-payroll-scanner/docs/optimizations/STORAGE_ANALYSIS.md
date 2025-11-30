# Storage Analysis: Firebase Storage vs. Google Drive

## Current Status
- **Firebase Storage** is currently **disabled** in `pubspec.yaml` (commented out).
- The app does not currently have an active storage solution implemented in code.

## Comparison

| Feature | Firebase Storage | Google Drive API |
| :--- | :--- | :--- |
| **Primary Use Case** | App-managed content (e.g., social media uploads, public assets). | User-managed content (e.g., personal documents, spreadsheets). |
| **Data Ownership** | Files live in the **App's** bucket. User has no direct access outside the app. | Files live in the **User's** personal Drive. User can view/organize them natively. |
| **Cost** | Free tier (Spark) up to 5GB. Pay-as-you-go (Blaze) after. | **Free** (uses user's existing Drive quota). |
| **Authentication** | Seamless with Firebase Auth. | Requires **Google Sign-In** with additional scopes (`drive.file`). |
| **Security** | Managed via **Security Rules** (e.g., `allow read if uid == userId`). | Managed via **OAuth Scopes**. App can only access files it created (`drive.file` scope recommended). |
| **Implementation** | Simple SDK. `ref.putFile()`. | Moderate complexity. REST API or `googleapis` package. |
| **IaC (Terraform)** | Fully supported (Buckets, Rules). | Not applicable (User's Drive is not infra you provision). |

## Recommendation

**Use Google Drive if:**
1.  The app is a **personal tool** or **productivity utility** for the user.
2.  You want users to own their data and access it outside the app.
3.  You want to avoid *any* potential cloud costs for storage.
4.  You are okay with the complexity of Google Sign-In scopes.

**Use Firebase Storage if:**
1.  You need strict control over file structure and validation (e.g., max file size, specific types) via Security Rules.
2.  You plan to share files between users in the future.
3.  You want a simpler implementation code-wise.

## Impact on Infrastructure as Code (IaC)

-   **If we choose Google Drive**: We will **remove** the Storage Bucket and Storage Rules from the Terraform proposal. The IaC will only manage Auth and Firestore.
-   **If we choose Firebase Storage**: We will proceed with the current proposal.

## Proposed Next Steps

Since you mentioned this is for "storing all *my* payrolls", **Google Drive seems like the better fit** for your use case.

**Action Plan if switching to Google Drive:**
1.  Update `FIREBASE_IAC_PROPOSAL.md` to remove Storage resources.
2.  Update `FIREBASE_SETUP.md` to reflect Google Drive usage.
3.  Implementation will involve adding `googleapis` and configuring Google Sign-In scopes instead of Firebase Storage rules.
