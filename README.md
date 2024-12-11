# FixU

FixU is a mobile application aimed at assisting users in detecting and managing their mental health, specifically focusing on depression detection. This application leverages machine learning algorithms, cloud integration, and user-friendly mobile interfaces to deliver a seamless and reliable user experience.

## Mobile Features Developed

- **Login with Cloud Authentication**
  - Secure register and login feature using on cloud authentication.

- **Depression Detection System**
  - Users can choose their status as "Professional" or "Student."
  - Displays customized questions based on the chosen status.
  - Answers are collected in a dynamic format (radio buttons, text fields, etc.).
  - Data is processed through a machine learning model deployed on the cloud.
  - Users receive detailed diagnostic results based on their inputs.

- **Notes with Reminder**
  - the user can create a note by pressing a floating action botton.
  - Display a list of notes that have been created by the user and stored in the on-cloud database.
  - Enable or disable daily reminders for mental health activities.
  - Set custom reminder times.

- **Dark Mode Support**
  - Toggle between light and dark themes with a Material Switch.
  - Theme preferences are saved and applied automatically when the app restarts.

- **Bottom Navigation Bar**
  - Intuitive navigation for Home, Diagnose, notes, and Profile sections.

## Technical Specifications

- **Languages and Frameworks**: Kotlin, Android Studio, Retrofit, Room Database.
- **UI Libraries**: Material Design, Glide for image loading.
- **Theme Management**: Material3 with Base.Theme.fixu.
- **Data Storage**: Room Database (local) and Cloud API for question and answer processing.
- **Machine Learning**: Depressive state detection using a custom-trained on-cloud ML model.
- **Cloud Integration**: Cloud-deployed APIs for data processing.

## Project Structure

- **Mobile Development**: UI/UX design and integration with APIs.
- **Machine Learning**: Depression detection model creation and evaluation.
- **Cloud Computing**: API development and deployment for backend processing.

## Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/your-organization/fixu.git
   ```
2. Open the project in Android Studio.
3. Sync the Gradle files.
4. Set up your end point services at local.properties
5. Make a properties named BASE_URL in local.properties
7. Rebuild your project
8. Set your API url to match your services at `ApiService` 
9. Run the application on an emulator or a physical device.

### How to Connect the App to Firebase
1. Go to [Firebase Console](https://console.firebase.google.com).
2. **Create a project** and **register** this app to your firebase project
3. Download ***google-services.json*** and place it in root **app** folder
4. Add all required dependencies and plugin to your gradle files
6. `Sync gradle`

### How to enable the Authentication Firebase
1. Go to your firebase project at Firebase Console.
2. Go to Build -> Authentication
3. Enable Email/Password provider at sign-in method
4. Make sure your have these dependencies:
   - Gradle(project)
   ```
   plugins {
    ...
    id("com.google.gms.google-services") version "4.4.2" apply false
   }
   ```
   - Gradle(module)
   ```
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

6. `Sync gradle`, if you change your gradle


## How It Works

### Depression Detection Flow
1. Users select their status (Professional or Student).
2. Answer a series of 10 questions tailored to the selected status.
3. Answers are compiled into an array and sent to the cloud.
4. The ML model processes the data and returns a diagnosis.
5. The app displays the results with actionable insights.

### Notes Flow
- User adding note by pressing FAB.
- After user confirming done, data sent to endpoint and get saved on-cloud database.
- Application GET data from database by endpoint API and showing list to the fragment Notes.

### Dark Mode
- User preferences are stored using SharedPreferences.
- Toggle switch in the Profile section enables or disables dark mode.

## Future Enhancements

- **Consult**: provide suggestion based on diagnosis results to mental health professionals.
- **Health Tips**: Provide actionable suggestions based on diagnostic results.
- **Community Integration**: Connect users with mental health professionals.
- **Multi-Language Support**: Expand the app to support more languages.

## Contributing

1. Fork the repository.
2. Create a feature branch:
   ```bash
   git checkout -b feature/your-feature
   ```
3. Commit your changes:
   ```bash
   git commit -m "Add your message here"
   ```
4. Push to the branch:
   ```bash
   git push origin feature/your-feature
   ```
5. Open a pull request.

---

Developed with ❤️ by the MobileDevelopment team of FixU. Together, we strive to improve mental health awareness and accessibility for everyone.
