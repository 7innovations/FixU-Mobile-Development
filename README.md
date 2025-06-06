# FixU Mobile Development

FixU is a mobile application designed to help users detect and manage their mental health, with a specific focus on depression detection. The app utilizes artificial neural network, cloud integration, and an intuitive mobile interface to provide a seamless, user-friendly, and reliable experience.

## Mobile Features Developed

- **Cloud Authentication for Login**
  - Secure login and registration via cloud authentication to ensure user privacy and data protection.

- **Depression Detection System**
  - Users can select their status as "Professional" or "Student."
  - Custom questions are presented based on the chosen status.
  - Responses are collected through various input formats.
  - Data is processed using a artificial neural network models that is deployed in the cloud.
  - Personalized diagnostic results are generated based on the user’s inputs.

- **Dairy Notes with Reminders**
  - Users can create personal notes by tapping a floating action button.
  - Created notes are stored in the cloud and can be accessed at any time.
  - Daily reminders can be enabled for mental health activities.
  - Custom reminder times can be set based on user preferences.

- **Dark Mode Support**
  - Toggle between light and dark themes.
  - User preferences are saved, and the theme is applied automatically when the app restarts.

- **Bottom Navigation Bar**
  - Simple and intuitive navigation with sections for Home, Diagnose, Diary Notes, and Profile.

## Technical Specifications

- **Languages and Frameworks**: Kotlin, Android Studio, Retrofit, Room Database.
- **UI Libraries**: Material Design, Glide.
- **Theme Management**: Material3 with custom Base.Theme.fixu.
- **Data Storage**: Room Database and Cloud API for question-answer processing.
- **Artificial Neural Network**: Depression detection using a custom-trained on-cloud models.
- **Cloud Integration**: APIs deployed in the cloud for backend processing.

## Project Structure

- **Mobile Development**: UI/UX design and integration with APIs.
- **Deep Learning**: Creation and evaluation of the depression detection model.
- **Cloud Computing**: API development and deployment for backend data processing.

## Installation

1. Clone the repository
   ```bash
   git clone https://github.com/7innovations/FixU-Mobile-Development
   ```
2. Open the project in Android Studio
3. Sync the Gradle files
4. Set up your endpoint services in `local.properties`
5. Add a property named `BASE_URL` in `local.properties`
6. Rebuild the project
7. Set the correct API URL in `ApiService`
8. Run the application on an emulator or a physical device

### How to Connect the App to Firebase
1. Go to [Firebase Console](https://console.firebase.google.com)
2. **Create a project** and **register** the app with your Firebase project
3. Download `google-services.json` and place it in the root **app** folder
4. Add the required dependencies and plugins to your Gradle files
5. **Sync Gradle**

### How to Enable Firebase Authentication
1. Go to your Firebase project in the Firebase Console
2. Navigate to **Build -> Authentication**
3. Enable the **Email/Password provider** under the sign-in method
4. Make sure your `build.gradle` files have these dependencies
   - **Gradle Project**
   ```gradle
   plugins {
     ...
     id("com.google.gms.google-services") version "4.4.2" apply false
   }
   ```
   - **Gradle Module**
   ```gradle
   plugins {
     ...
     id("com.google.gms.google-services")
   }
   dependencies {
     ...
     implementation(platform("com.google.firebase:firebase-bom:33.7.0"))
     implementation("com.google.firebase:firebase-auth")
   }
   ```

6. **Sync Gradle** if you made changes to your Gradle files.

---
