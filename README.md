# FixU

FixU is a mobile application aimed at assisting users in detecting and managing their mental health, specifically focusing on depression detection. This application leverages machine learning algorithms, cloud integration, and user-friendly mobile interfaces to deliver a seamless and reliable user experience.

## Features

- **Login with Google Account**
  Secure login feature using Google accounts.

- **Depression Detection System**
  - Users can choose their status as "Professional" or "Student."
  - Displays customized questions based on the chosen status.
  - Answers are collected in a dynamic format (radio buttons, text fields, etc.).
  - Data is processed through a machine learning model deployed on the cloud.
  - Users receive detailed diagnostic results based on their inputs.

- **Event Management**
  - Displays upcoming and finished events in a user-friendly RecyclerView.
  - Users can mark events as favorites and manage their preferences.

- **Dark Mode Support**
  - Toggle between light and dark themes with a Material Switch.
  - Theme preferences are saved and applied automatically when the app restarts.

- **Reminders**
  - Enable or disable daily reminders for mental health activities.
  - Set custom reminder times.

- **Bottom Navigation Bar**
  - Intuitive navigation for Home, Events, and Profile sections.

## Technical Specifications

- **Languages and Frameworks**: Kotlin, Android Studio, Retrofit, Room Database.
- **UI Libraries**: Material Design, Glide for image loading.
- **Theme Management**: Material3 with Base.Theme.DiEvent.
- **Data Storage**: Room Database (local) and Cloud API for question and answer processing.
- **Machine Learning**: Depressive state detection using a custom-trained ML model.
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
4. Set up the necessary API keys and endpoints in `ApiConfig`.
5. Run the application on an emulator or a physical device.

## How It Works

### Depression Detection Flow
1. Users select their status (Professional or Student).
2. Answer a series of 10 questions tailored to the selected status.
3. Answers are compiled into an array and sent to the cloud.
4. The ML model processes the data and returns a diagnosis.
5. The app displays the results with actionable insights.

### Event Management
- Fetches events from `https://event-api.dicoding.dev/`.
- Allows users to favorite events and view details.

### Dark Mode
- User preferences are stored using SharedPreferences.
- Toggle switch in the Profile section enables or disables dark mode.

## Future Enhancements

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

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

Developed with ❤️ by the FixU team. Together, we strive to improve mental health awareness and accessibility for everyone.
