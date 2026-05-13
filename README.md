# 📙 Namma Santhe Ledger
A "Simplified Digital Khata" that makes managing market credits as easy as checking the time.

Built for local market (**Santhe**) vendors and small shop owners with low digital literacy — big buttons, high contrast, saffron market tones, and zero technical jargon.

| Platform | Language | UI | minSdk | Backend |
| :--- | :--- | :--- | :--- | :--- |
| Android | Kotlin | Jetpack Compose | 24 | Room (Offline-First) |

---

## Why this app exists
A local market vendor sells goods on credit every day. They usually carry a worn-out diary (**Khata**) to track who owes what. Paper is easy to lose, hard to search, and doesn't send reminders. **Namma Santhe Ledger** is the digital upgrade: a vendor opens the app, taps a few large buttons, records a sale, and can send a WhatsApp reminder in seconds. The whole product is built around one rule:

**Data integrity over complexity. If a step needs an explanation, the step is wrong.**

---

## Design rules (non-negotiable)
| Rule | What it means in the app |
| :--- | :--- |
| **Large touch targets** | Every primary action is a $\ge$ 56 dp control; numeric keys are large for one-handed market use. |
| **Market Colors** | Saffron (Primary), Tomato (Dues), and Green (Paid). Financial status is understood through color, not just text. |
| **Zero-Entry Prefixes** | Users never type "+91". The app masks the input with a prefix and handles country codes internally for reminders. |
| **Instant Khata view** | Net balances are animated and color-coded. Red means "They owe you", Green means "Settled". |
| **No jargon** | "Udari (Credit)" for sales, "Payment" for collections. "Dues" instead of "Accounts Receivable". |

---

## Features

### 🏠 Home — the market dashboard
A quick-glance screen for the busy vendor:
*   **Total Outstanding** — A big-picture view of how much total money is currently "in the market."
*   **Today's Sales** — Real-time tracking of Credit (Udari) transactions made in the current day.
*   **Quick Actions** — Direct access to "Add Transaction" or "Add Customer" via unmistakable large buttons.
*   **Recent Activity** — A feed of the last 5 transactions for immediate verification.

### 👥 Customer Management — the digital diary
*   **Frictionless Entry** — Automated `+91` prefixing. The user only types the 10-digit number.
*   **Duplicate Prevention** — Unique name enforcement ensures no two Khatas get mixed up.
*   **Safety First Edit** — Updating a phone number or name uses `@Update` logic to preserve every single past transaction. History is never wiped out during an edit.
*   **Clean Display** — Automatically strips "91" from stored numbers for a clean 10-digit UI display, but prepends it for reminders.

### 💸 Digital Khata — recording sales
*   **Udari (Credit)** — Increases the customer's due amount (represented as a Sale).
*   **Payment** — Records money received and instantly reduces the balance.
*   **Custom Keypad** — A simplified, large-button numeric keypad optimized for one-handed use in busy markets.
*   **Notes** — Optional tags like "Onions 5kg" to resolve future disputes.

### 📢 Reminders — getting paid
*   **WhatsApp Reminder** — Pre-fills a professional message with your contact details and the specific due amount: *"Hello [Name], you have a pending due of Rs. [Amount] at Namma Santhe Ledger..."*
*   **SMS Reminder** — A robust fallback that pre-fills the native SMS app using URI schemes compatible with Samsung, Xiaomi, and OnePlus. Always prepends `+91` to the recipient number automatically.

---

## Tech stack
| Layer | Choice |
| :--- | :--- |
| **Language** | Kotlin |
| **UI** | Jetpack Compose (Material 3) |
| **Architecture** | MVVM + Repository Pattern |
| **Database** | Room SQLite (with Flow for reactive updates) |
| **Navigation** | Navigation-Compose (Type-safe) |

---

## Project structure
`app/src/main/java/com/example/nammasantheledger/`
├─ MainActivity.kt             # Entry point & NavHost setup
├─ data/
│  ├─ model/                   # Customer & Transaction entities
│  ├─ dao/                     # Room DAOs with Custom SQL queries
│  └─ repository/              # LedgerRepository (Single source of truth)
├─ ui/
│  ├─ theme/                   # Saffron/Tomato palette & Large typography
│  ├─ components/              # Shared UI components (TransactionItem, etc.)
│  ├─ navigation/              # Screen definitions & NavGraph
│  └─ screens/
│     ├─ HomeScreen.kt         # The main dashboard
│     ├─ CustomerListScreen.kt # Search, Edit & Duplicate validation
│     ├─ CustomerDetail.kt     # Transaction history & Reminders
│     ├─ AddCustomer.kt        # Entry with prefix automation
│     └─ AddTransaction.kt     # Multi-step entry flow
└─ viewmodel/                  # Business logic & UI state management

---

## Room Data Model

### `customers` table
- `id`: Int (**Primary Key**, Auto-increment)
- `name`: String (**Unique Index**)
- `phone`: String (Stored as 10-digit base)

### `transactions` table
- `id`: Int (**Primary Key**, Auto-increment)
- `customerId`: Int (**Foreign Key** with **CASCADE** deletion)
- `amount`: Double
- `type`: "CREDIT" | "PAYMENT"
- `note`: String
- `date`: Long (Timestamp)

---

## Getting started
1.  **Clone the project** and open in Android Studio Koala or newer.
2.  **Gradle Sync** — Let the version catalog (`libs.versions.toml`) sync the dependencies.
3.  **Database Migration** — The app uses `version 3` with `fallbackToDestructiveMigration()` for easy development.
4.  **Build & Run** — Optimized for API 24 (Android 7.0) and above.

---

## Roadmap / Known Limitations
*   **PDF Statements** — Generating a monthly "Statement of Account" as a PDF to share via WhatsApp.
*   **Voice Entry** — Allowing vendors to speak the amount (e.g., "Fifty Rupees Credit").
*   **Cloud Backup** — Currently data is local-only. Future updates will include Google Drive backup.
*   **Multi-language** — Support for Kannada and Hindi UI labels.

---
Built with ❤️ for the heartbeat of Indian commerce — the local market.
