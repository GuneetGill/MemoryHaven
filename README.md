# MemoryHaven

Acknowledgments
This project was developed as part of the **CMPT 276: Introduction to Software Engineering** course at Simon Fraser University, with other team members: **Guneet Gill, Julian Loewenherz, Mckenzie Chen, Brayden Yee, Tanhim Haque**

Memory Haven is a mobile application designed to assist dementia patients by leveraging reminiscence therapy through photos and audio contributions from their loved ones. The app enables secure interactions between patients, caregivers, and their support networks to foster emotional connections and track memory retention over time.

---

## Features

### Secure Login System
- Role-based authentication using Firebase Authentication.
- Separate login interfaces for patients and their family or friends.

### Multimedia Uploads
- Family and friends can upload photos and audio files with descriptions, dates, and tags.
- Media is securely stored in Firebase Realtime Database and Firebase Storage.

### Daily Feed
- Patients receive a curated feed of up to 5 random or new uploads daily.
- Integrated audio playback for photos to enhance engagement.

### Archive
- Comprehensive archive view with sorting options:
  - Chronological order.
  - Keyword-based search through descriptions.

### Ratings and Visualization
- Interactive response rating system for caregivers to assess patient memory retention.
- Memory trends visualized using MPAndroidChart.

---

## Development Environment
- **Language**: Java  
- **Frameworks/Libraries**: MPAndroidChart, Firebase Authentication, Firebase Realtime Database, Firebase Storage  
- **IDE**: Android Studio (version LadyBug)  
- **Version Control**: Git  

---

## User Stories

### Patient
1. **Daily Feed Interaction**: View daily photos and listen to associated audio explanations.
2. **Archive Access**: Search and browse uploaded content by date or keyword.

### Family & Friends
1. **Content Upload**: Add photos, audio, and descriptions to the patient’s collection.
2. **Role-Based Access**: Log in using the patient’s unique ID to contribute content securely.

### Caregiver
1. **Account Management**: Assist patients in creating and managing accounts.
2. **Memory Ratings**: Evaluate patient memory responses (1-5 stars) and track trends.

---

## Installation and Setup

1. Clone the repository:
   ```bash
   git clone https://github.com/your-username/memory-haven.git
   ```

2. Open the project in Android Studio.

3. Configure Firebase:
   - Add the `google-services.json` file to the `app/` directory.
   - Set up Firebase Realtime Database and Storage in the Firebase console.

4. Build and run the application on an Android device or emulator.

---


