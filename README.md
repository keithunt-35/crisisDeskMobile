# 🚨 Crisis Desk Mobile — Field Staff & Incident Command App

Crisis Desk Mobile is a real-time, production-grade native Android application built for event staff, field operatives, and department leads operating on the ground during live events. It serves as the mobile node of an Event Emergency Room & Incident Command Center, enabling ultra-fast incident logging, live synchronization, task assignment, status tracking, and robust offline resilience.

---

## 🌟 Core Features & Capabilities

1. **Lightning-Fast Incident Logging (<20s)**:
   - Designed for high-pressure environments. Field operatives can quickly log issues (audio/visual failures, facilities problems, security breaches, VIP concerns) with category chips, color-coded severity selectors, descriptions, and locations.
2. **Real-Time Synchronization**:
   - Powered by Cloud Firestore reactive snapshot listeners (`callbackFlow`). Any incident logged on mobile instantly syncs to command dashboards, and vice versa.
3. **Offline-First Resilience**:
   - Built-in Firestore local disk caching (`PersistentCacheSettings`). Operatives can log and view incidents in deep basements or zero-connectivity zones; data automatically uploads once connection is re-established.
4. **Role-Aware Command Workflow**:
   - Dedicated views for **Home Dashboard** (quick metrics & active feed), **My Tasks** (assigned incidents), **Command Incident Feed** (search & multi-criteria filtering), and **Incident Details** (activity timeline, status changes, comments, and mandatory resolution notes).
5. **Secure Authentication & Profile Management**:
   - Firebase Authentication (Email + Password) with secure session handling and role simulation.
6. **Push Notifications**:
   - Firebase Cloud Messaging (FCM) integration to dispatch high-priority emergency alerts and task assignments directly to operatives' lock screens.

---

## 🎨 Design Principles & Severity Palette

Crisis Desk is engineered to remain **calm under pressure** following strict Material Design 3 guidelines:
- **Critical** → Deep Red (`#B91C1C`)
- **High** → Orange (`#EA580C`)
- **Medium** → Amber (`#D97706`)
- **Low** → Blue (`#2563EB`)
- **Resolved** → Emerald (`#059669`)

---

## 📱 Tech Stack

- **Language**: Kotlin 2.2+
- **UI**: Jetpack Compose + Material Design 3
- **Architecture**: MVVM + Clean Repository Pattern
- **Backend & Services**: Firebase (Authentication, Cloud Firestore, Cloud Messaging, Storage)
- **Dependency Injection**: Hilt
- **Navigation**: Jetpack Navigation Compose
- **Asynchronous**: Kotlin Coroutines & StateFlow
- **Image Loading**: Coil
- **Minimum SDK**: Android 8.0 (API 26+)
- **Target SDK**: Android 35+

---

## 📁 Project Architecture

```tree
com.example.crisisdeskmobile/
├── data/
│   ├── model/         # Incident, User, TimelineEntry data classes
│   └── repository/    # AuthRepository, IncidentRepository (Firestore + Offline Cache)
├── domain/            # Business rules & shared logic
├── ui/
│   ├── auth/          # LoginScreen, RegisterScreen, AuthViewModel
│   ├── home/          # HomeScreen, HomeViewModel
│   ├── incident/      # LogIncidentScreen, MyIncidentsScreen, AllIncidentsScreen, IncidentDetailScreen
│   ├── profile/       # ProfileScreen, ProfileViewModel
│   ├── main/          # MainAppScreen (App Shell, Bottom Navigation, FAB)
│   └── theme/         # Color.kt, Theme.kt, Type.kt (M3 Material Theme)
└── util/              # Resource wrapper, CrisisDeskMessagingService (FCM)
```

---

## ⚙️ Setup & Installation Guide (For GitHub / Open Source)

### Prerequisites
- Android Studio (Giraffe / Jellyfish or newer)
- JDK 11 or higher
- A Firebase Project (Console)

### 1. Clone the Repository
```bash
git clone https://github.com/your-username/crisis-desk-mobile.git
cd crisisDeskMobile
```

### 2. Configure Firebase (Important)
This repository does **not** include production Firebase credentials for security reasons. To connect your own backend:
1. Go to the [Firebase Console](https://console.firebase.google.com/).
2. Create a new project named **Crisis Desk**.
3. Register an Android app with package name:
   `com.example.crisisdeskmobile`
4. Download your official **`google-services.json`** file.
5. Place your `google-services.json` file inside the project directory at:
   `app/google-services.json`
6. In the Firebase Console, enable:
   - **Authentication** (Email/Password provider)
   - **Cloud Firestore Database** (Test mode or production mode with rules)

### 3. Open & Build in Android Studio
1. Open Android Studio and select **Open**, then choose the `crisisDeskMobile` folder.
2. Let Gradle sync project dependencies.
3. Connect an Android device via USB (with USB Debugging enabled) or start an emulator.
4. Click **Run 'app'** to build and deploy!

---

## 🔒 Security Best Practices
- **No Secrets Committed**: Sensitive configuration files (`google-services.json`, local keystores, API keys) are excluded via `.gitignore`.
- **Server-Side Integrations**: External telecommunication services (such as Africa's Talking SMS/Voice gateways) should be securely triggered via Firebase Cloud Functions on the backend rather than exposed on client devices.

---

## 📜 License
This project is open-source and available under the [MIT License](LICENSE).
