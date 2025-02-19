# ✅ BucketList App

The **BucketList App** is an Android application that allows users to maintain and track their **lifetime goals**. Users can create, update, and manage their goals, add progress notes, mark goals as completed or paused, and even attach photos. The app follows **MVVM architecture**, utilizes **Jetpack ViewModel**, **Room Database**, and implements best practices from Android development.

---

## 📜 Overview

The **BucketList App** is a multi-phase project that builds iteratively across **three parts**. It provides a structured way for users to document and track their goals, using **database persistence, navigation components, and a list-detail interface**.

### **🔹 Key Features Across Project Phases**

- **📌 Part 1: Basic Goal Management**
  - Users can create and view goals with **two main screens**:
    - **GoalListFragment:** Displays a list of goals.
    - **GoalDetailFragment:** Displays details of a selected goal.
  - Implements **ViewModel** to retain data across configuration changes.
  - Uses a **RecyclerView** to display the list of goals.

- **📌 Part 2: Database & Navigation**
  - Integrates **Room Database** for goal persistence.
  - Enables **navigation** between GoalListFragment and GoalDetailFragment.
  - Adds **progress tracking** with editable notes.
  - Allows marking goals as **Paused** or **Completed** with corresponding UI updates.
  - Implements **ViewModel with LiveData** to observe and update UI seamlessly.

- **📌 Part 3: Advanced Features**
  - Users can **add photos** to their goals using the camera.
  - Adds a **share feature** to send goal details via messaging apps.
  - Implements **swipe-to-delete** functionality for both goals and progress notes.
  - Uses **RecyclerView in GoalDetailFragment** for dynamic note management.
  - Displays **large zoomed images** in a separate dialog when tapped.
  - Implements **state management** ensuring user changes persist across app usage.

---

## 🚀 Features

✔️ **Add, Edit, and View Goals** – Track your lifetime aspirations.  
✔️ **Goal Status** – Mark goals as **paused** or **completed**.  
✔️ **Add Progress Notes** – Log incremental progress on goals.  
✔️ **Photo Attachment** – Capture and attach images to goals.  
✔️ **Persistent Storage** – Uses **Room Database** for data storage.  
✔️ **Navigation** – Users can move between goal list and detail views.  
✔️ **RecyclerView for Goals & Notes** – Efficient and scalable UI.  
✔️ **Swipe to Delete** – Remove goals and notes with a swipe gesture.  
✔️ **LiveData & ViewModel** – Ensures data survives configuration changes.  
✔️ **Implicit Intents** – Share goals and launch camera seamlessly.  
✔️ **Dark Mode Compatibility** – Supports both light and dark themes.  

---

## 🛠️ Tech Stack

🔹 **Kotlin** – Primary language for Android development.  
🔹 **Android Jetpack Components** – ViewModel, LiveData, Navigation, Room.  
🔹 **MVVM Architecture** – Proper separation of concerns.  
🔹 **RecyclerView & CardView** – Efficient UI management.  
🔹 **Material Design** – Clean and modern UI elements.  
🔹 **Gradle & Android Studio** – Development and dependency management.  
🔹 **Jetpack Navigation** – Handles screen transitions.  

---

## 🎮 How to Use

1️⃣ **Add a Goal** – Click the "New Goal" button in the app bar.  
2️⃣ **Edit a Goal** – Click on a goal from the list to edit its details.  
3️⃣ **Track Progress** – Add **progress notes** to update your goal.  
4️⃣ **Pause or Complete Goals** – Use checkboxes to mark status.  
5️⃣ **Attach Photos** – Take and save images for your goals.  
6️⃣ **Delete Goals/Notes** – Swipe left on a goal/note to remove it.  
7️⃣ **View Large Images** – Tap a goal photo to see a zoomed-in version.  
8️⃣ **Share Goals** – Send goal details via SMS, email, or other apps.  

---



