# <img src="screenshots/icon.png" width="24" alt="SharedNote icon" /> SharedNote

SharedNote is a simple and convenient **note-taking Android application** built with **Kotlin** and **Jetpack Components**.  
It is designed to demonstrate **clean architecture, multi-language support, local persistence, and modern Android development practices**.

---

## ✨ Features

- 📝 Create, edit and delete notes with titles and content
- 🎨 Highlight important notes by coloring their titles
- 📂 Organize notes into folders
- 🌍 Multi-language support (English, Ukrainian, Russian)
- 🗂 Switch between **list** and **grid** view modes
- 🔀 Sort notes by different parameters
- 📤 Share notes as plain text via any messenger (Telegram, email, etc.)
- 🔄 Share notes between devices running SharedNote
- 🎨 Custom themes and note appearance personalization
- 📺 Simple, clean, Material Design inspired UI

---

## 📷 Screenshots

<img src="screenshots/screenshot_01.png" width="160" alt="Children screen" />&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<img src="screenshots/screenshot_02.png" width="160" alt="Sickness screen" />&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<img src="screenshots/screenshot_03.png" width="160" alt="Daily routine screen" />&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<img src="screenshots/screenshot_04.png" width="160" alt="Statistic screen" />

---

## 🛠 Tech Stack

- **Kotlin**
- **Jetpack Components**:
  - ViewModel
  - LiveData / StateFlow
  - Room Database (for local persistence)
  - Navigation
- **Coroutines & Flow** – asynchronous operations and reactive UI updates
- **Material Design 3 (Compose-ready)** for modern UI
- **ActivityResult API** – for system interactions (e.g., sharing)
- **Google AdMob** – for in-app advertising

---

## 📁 Project Structure

app/  
├── colors/ &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp; # Color palette definitions and color group objects (used in note editor and theming)<br>
├── dao/ &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; # Data Access Objects (Room DAO interfaces)<br>
├── data/ &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; # Database setup, repositories, and MainViewModel<br>
├── dto/ &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; # Data Transfer Objects (used for sharing/export)<br>
├── entity/ &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; # Room entity classes (NoteEntity, FolderEntity, etc.)<br>
├── layout/ &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; # Layout-related components for note presentation<br>
├── navigation/ &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; # Navigation graph and navigation helpers<br>
├── screen/ &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; # Jetpack Compose screens (UI for different app modules)<br>
├── settings/ &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; # Settings screen and preference management<br>
├── share/ &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; # Share-related functions (export/import notes, cross-device sharing)<br>
├── ui.theme/ &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; # UI theming (colors, typography, shapes)<br>
└── util/ &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; # Utility classes and helper functions<br>

---

## 🔒 Privacy & Security

- 🚫 No registration or login required
- 🚫 No cloud storage – all notes are stored **locally on the device**
- ✅ Data leaves the device only if the user explicitly shares it
- 📢 [Privacy Policy](https://maksimtest.github.io/SharedNotebook/privacy-policy.html)

---

## 📦 Installation

The app will be available on **Google Play** (planned release).  
For now, you can clone the repo and build it locally:

```bash
git clone https://github.com/yourusername/SharedNote.git
open in Android Studio
Run ▶️
```
------------------
