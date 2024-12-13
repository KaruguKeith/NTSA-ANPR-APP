
NTSA ANPR App

NTSA ANPR (Automatic Number Plate Recognition) is an Android application integrated with a Flask-based backend to identify and process vehicle license plates. This project aims to streamline vehicle identification for various applications, such as traffic management and law enforcement.

Features

- Real-Time ANPR**: Capture and process license plates using the Android app.
- Flask API Integration**: Backend functionality for license plate recognition using Python.
- Seamless Communication**: App communicates with the Flask API hosted on a local network.
- Professional UI**: User-friendly and aesthetically pleasing interface built with Android Studio Koala 2024.1.1.

---

Technologies Used

Frontend:
- Android Studio (Koala 2024.1.1)
- Java/Kotlin for Android development

Backend:
- Python (Flask Framework)
- OpenCV for image processing and license plate recognition

Other Tools:
- Local Network Communication
- Ubuntu for hosting the Flask API

---

Prerequisites

1. Android Device:
   - Android version 8.0 (Oreo) or higher.
2. Development Environment:
   - Python 3.8 or higher
   - Android Studio 2024.1.1 Koala
   - Ubuntu for backend hosting
3. Dependencies:
   - Flask
   - OpenCV
   - Requests (Python library)

---

Installation

Backend Setup

1. Clone the repository to your PC:
   ```bash
   git clone https://github.com/your-username/ntsa-anpr.git
   ```

2. Navigate to the backend directory:
   ```bash
   cd ntsa-anpr/backend
   ```

3. Install the required Python dependencies:
   ```bash
   pip install -r requirements.txt
   ```

4. Run the Flask API:
   ```bash
   python app.py
   ```
   The Flask server will start at `http://<your-pc-ip>:5000`.

Android App Setup

1. Open the Android project in Android Studio.
2. Update the `API_BASE_URL` in the app's configuration file with your Flask server's IP address:
   ```java
   String API_BASE_URL = "http://<your-pc-ip>:5000";
   ```
3. Build and run the app on an emulator or physical device.


Usage

1. Launch the App:
   Open the NTSA ANPR app on your Android device.
2. Capture Image:
   Use the camera feature to capture a vehicle’s license plate.
3. Process Data:
   The app sends the image to the Flask API for recognition and displays the results.
4. Review Results:
   View the recognized license plate data in the app interface.

---

## Project Structure

```plaintext
ntsa-anpr/
├── backend/       # Flask API and Python scripts for ANPR
│   └── app.py   # Main Flask application
├── frontend/      # Android project files
│   └── src/    # Android source code
├── README.md     # Project documentation
└── requirements.txt  # Python dependencies
```

---

Contributing

Contributions are welcome! 

License

This project is licensed under the [MIT License](LICENSE). Feel free to use, modify, and distribute it as per the license terms.

---

Contact

For any questions or feedback, feel free to reach out:
- Name: [Keith KIrugumi]
- Email: [karugukeith@gmail.com]
- GitHub: [https://github.com/your-username](https://github.com/karugukeith)

