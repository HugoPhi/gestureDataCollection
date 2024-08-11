# 📱 Wearable Sensor Data Collector

This project is an Android application designed for Wear OS devices, allowing users to collect sensor data like accelerometer and gyroscope readings. The app is specifically tailored for gesture recognition and provides a streamlined user interface for recording and saving sensor data. 

## 🛠 Features

- 📊 **Real-Time Sensor Data Collection**: Collect accelerometer and gyroscope data at different sampling rates.
- ⏲️ **4-Second Countdown Timer**: Each gesture recording session lasts for 4 seconds.
- 🔢 **Gesture Count Tracker**: Displays the number of gestures collected during the session.
- 🎨 **User-Friendly Interface**: Simple and intuitive design with buttons to start, pause, and reset the recording process.
- 💾 **Data Saving**: Automatically saves recorded sensor data as CSV files, ready for further analysis.

## 🚀 Getting Started

### Prerequisites

- Android Studio
- Wear OS device or emulator

### Installation

1. Clone the repository:

   ```bash
   git clone https://github.com/yourusername/wearable-sensor-collector.git
   ```

2. Open the project in Android Studio.

3. Sync the project to download the necessary dependencies.

4. Run the application on a Wear OS device or emulator.

## 📝 Usage

1. **Select Sampling Rate**: Choose the desired sampling rate from the dropdown menu (20 dps, 25 dps, or 30 dps).
2. **Select Gesture**: Choose the gesture you want to record from the available options.
3. **Start Recording**: Press the `Record` button to start the 4-second recording session. The screen will lock during this period.
4. **Save Data**: Once the timer finishes, the data is automatically saved, and you can reset the session using the `Reset` button.

## 📂 Project Structure

- `MainActivity.java`: The main logic for sensor data collection and user interaction.
- `activity_main.xml`: The layout file for the user interface.
- `ScalarKalmanFilter.java`: The Kalman filter implementation for smoothing sensor data.
- `AndroidManifest.xml`: Contains essential information about the app for the Android system.

## 🛡️ Permissions

The application requires the following permissions:

- `INTERNET`
- `ACCESS_NETWORK_STATE`
- `ACCESS_WIFI_STATE`
- `WAKE_LOCK`
- `VIBRATE`
- `READ_EXTERNAL_STORAGE` (for devices with SDK version 32 or lower)
- `WRITE_EXTERNAL_STORAGE` (for devices with SDK version 32 or lower)

## 🌟 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## 🛠️ Built With

- Java
- Android Studio
- Wear OS SDK

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 👏 Acknowledgements

- Thanks to all the open-source contributors who helped make this project possible.
- Special shoutout to the Wear OS development community for their amazing resources and support!

---

🚀 **Enjoy using the Wearable Sensor Data Collector and start recognizing those gestures!**
