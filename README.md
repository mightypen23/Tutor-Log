# Tutor Log 📓✨

A modern retro tutor log application built with **Kotlin** and **Jetpack Compose** for Android. Designed with a Wes Anderson–inspired aesthetic, tactile retro ledger cards, and local-first persistence.

---

## 🎨 Color Palette

Built strictly around your chosen color palette:

| Name | Hex Code | Purpose |
| :--- | :--- | :--- |
| **Sinopia** | `#D7340B` | Primary action accents, retro stamp badges, FAB, duration indicators |
| **Vanilla** | `#E0DDAE` | Warm vintage parchment canvas, navigation bar, tactile ledger background |
| **Caribbean** | `#336467` | Deep slate teal headers, status bar, card borders, typography |

---

## 🚀 Key Features

1. **Activity Session Logging**:
   - **Student / Subject**: Autocomplete chip suggestions from previously logged students.
   - **Date Picker**: Retro calendar dialog defaults to today, with custom day-of-week stamp (`OCT 17 SAT`).
   - **Start & End Time Pickers**: Interactive retro dial pickers.
   - **Real-Time Duration Calculation**: Auto-computes and displays elapsed session hours and minutes (e.g., `1h 30m`).
   - **Notes & Activities**: Multiline notepad area for lesson notes, topics covered, and homework.

2. **Local Storage on Phone**:
   - Powered by a local SQLite database (`tutor_log.db`).
   - 100% offline — your data stays private and safe on your device.
   - Fast indexing on session date and time for instant load times.

3. **Share to Samsung Notes & Other Apps**:
   - Tap **SHARE** or **SHARE LEDGER** to open the export preview dialog.
   - Choose export scope:
     - **All logs saved up to today** (default)
     - **All recorded logs**
     - **Current filtered view**
   - Triggers Android's native share sheet (`Intent.createChooser`), allowing you to select **Samsung Notes**, **Google Keep**, **WhatsApp**, **Email**, or copy directly to clipboard.
   - Generates a cleanly formatted text ledger:
     ```text
     ╔══════════════════════════════════════════╗
     ║           TUTOR ACTIVITY LOG             ║
     ╚══════════════════════════════════════════╝
     Period: ALL LOGS SAVED UP TO TODAY (2026-10-17)
     Total Sessions: 8
     Total Hours Logged: 12h 30m
     ──────────────────────────────────────────
     [01] Oct 17, 2026 (SAT)
          Student: Maya Lin (Calculus)
          Time:    2:00 PM – 3:30 PM (1h 30m)
          Notes:   Derivatives review & practice set #3
     ...
     ```

4. **Modern Retro UI & Dashboard**:
   - **Stats Ledger**: Live counters for total hours taught, total sessions, and student count.
   - **Date Filters**: Filter by `All Time`, `Up to Today`, `This Month`, `This Week`, or `Today`.
   - **Instant Search**: Search across student names, subjects, and session notes.
   - **Tactile Cards**: Offset solid shadows, crisp ink borders, and depression animations.

---

## 📱 How to Run or Install on Your Phone

### Option A: Install the Built APK Directly
The debug APK has been compiled:
- Location: `app/build/outputs/apk/debug/app-debug.apk`

To install directly to a connected Samsung / Android phone via ADB:
```bash
adb install "app/build/outputs/apk/debug/app-debug.apk"
```
Or transfer `app-debug.apk` via USB, Quick Share, or Google Drive and tap to install on your phone.

### Option B: Run via Android Studio
1. Open this folder in **Android Studio**.
2. Connect your phone via USB or Wi-Fi (with Developer Options & USB Debugging enabled).
3. Click the green **Run (▶)** button.
