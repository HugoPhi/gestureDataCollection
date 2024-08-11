# 📱 Wearable Data Collection App

This project is designed to collect sensor data on the TicWatch Pro 0209 using various gestures. The data is stored in CSV files for further analysis. The app is optimized for WearOS devices, specifically the TicWatch Pro 0209.

## ⚙️ Project Configuration

- **Device:** TicWatch Pro 0209
- **OS:** WearOS
- **API Level:** 28 (Android 9.0 Pie)
- **Build Tool Version:** 30.0.3
- **Gradle Version:** 7.0.2
- **Android Gradle Plugin Version:** 7.0.2
- **Wearable Support Library:** `com.google.android.wearable:wearable:2.8.1`
- **Ambient Mode Support Library:** `androidx.wear.ambient:ambient:1.0.0`

## 📋 How to Collect Data

1. **Start the App:**
   - Launch the app on your TicWatch Pro 0209.
   - You will see two dropdown menus and buttons to start data collection.

2. **Select Sampling Rate and Gesture:**
   - **Sampling Rate:** Choose the desired sampling rate from the first dropdown (e.g., 20 dps, 25 dps, 30 dps).
   - **Gesture:** Choose the gesture you want to collect data for from the second dropdown (e.g., Gesture 1, Gesture 2).

3. **Record Data:**
   - Press the **Record** button to start data collection. The session lasts for 4 seconds.
   - During this time, the screen will be disabled to prevent accidental touches, and a countdown timer will indicate the remaining time.
   - Once the session is completed, the data will be automatically saved in a CSV file.

4. **Reset Collection Count:**
   - After data collection, the app will display the number of times data has been collected for the selected gesture.
   - You can reset this count by pressing the **Reset** button.

## 📊 Data Format and Headers

The data collected during each session is saved in CSV format. Each CSV file is named according to the selected gesture and the timestamp of the session.

### Data File Structure

- **File Location:** `/storage/emulated/0/Android/data/com.example.myapplication/files/`
- **File Format:** CSV
- **File Naming Convention:** `GestureName_YYYYMMDD_HH-mm-ss.csv`

### Data Headers and Their Meanings

The CSV files have the following headers:

1. **DATE:** The date of the data collection in `dd/MM/yyyy` format.
2. **TIME:** The time of the data collection in `HH:mm:ss` format.
3. **ax, ay, az:** Accelerometer data along the x, y, and z axes. These values represent the linear acceleration of the device in meters per second squared (m/s²).
4. **gx, gy, gz:** Gyroscope data along the x, y, and z axes. These values represent the rate of rotation around the respective axes in radians per second (rad/s).
5. **lx, ly, lz:** Linear acceleration data corrected by removing the gravity component. These are derived from the raw accelerometer data.
6. **ma:** Magnitude of the accelerometer vector, calculated as `sqrt(ax² + ay² + az²)`. This value represents the total acceleration experienced by the device.
7. **mg:** Magnitude of the gyroscope vector, calculated as `sqrt(gx² + gy² + gz²)`. This value represents the total rotational motion experienced by the device.
8. **label:** The name of the gesture being recorded (e.g., Gesture 1, Gesture 2).

### Example Data Entry


|   DATE    |   TIME   |  ax  |   ay   |  az  |   gx   |   gy   |   gz   |  lx  |   ly   |  lz  |   ma  |   mg  |   label   |
|:---------:|:--------:|:----:|:------:|:----:|:------:|:------:|:------:|:----:|:------:|:----:|:-----:|:-----:|:---------:|
| 12/08/2024 | 14:23:45 | 0.01 | -0.02 | 9.81 | 0.0001 | 0.0003 | 0.0002 | 0.02 | -0.01 | 9.79 | 9.81  | 0.0004 | Gesture 1 |



This example shows one row of data where:

- The accelerometer recorded values close to `0` for `ax` and `ay` and close to `9.81` m/s² for `az`, indicating that the watch was lying flat.
- The gyroscope recorded minimal rotation, with very small values for `gx`, `gy`, and `gz`.
- The linear acceleration (`lx`, `ly`, `lz`) and magnitude values (`ma`, `mg`) are derived from the raw sensor data.
- The data corresponds to "Gesture 1".

### Understanding the Data

- **Accelerometer Data (`ax`, `ay`, `az`):** Useful for detecting movement, orientation, and vibrations.
- **Gyroscope Data (`gx`, `gy`, `gz`):** Helps in understanding rotational motion and orientation changes.
- **Linear Acceleration (`lx`, `ly`, `lz`):** Provides insights into the actual movement by subtracting gravity.
- **Magnitude Values (`ma`, `mg`):** Represent the intensity of the movement or rotation.

With this collected data, you can perform further analysis to understand user behaviors, identify specific gestures, and more.
