Mobile Computing - Winter 2024 - Assignment 3
Sensing and Native Code
This repository contains the code for Assignment 3 of the Mobile Computing course, focusing on sensing data from accelerometers and implementing a convolutional neural network (CNN) in native code for image classification.

Table of Contents
About
Requirements
Usage
Files
Contributing
License
About
This assignment consists of two main tasks:

Developing an Android app that collects data from the device's accelerometers and displays real-time orientation angles. Additionally, the app stores this data in a database for historical analysis, presenting it through graphs.
Implementing a CNN using Android's native neural networks API to classify a set of images.
Requirements
To run the app and execute the CNN program, you need:

Android Studio (version X.X.X)
Android device/emulator with accelerometer support
Python (version X.X.X) with TensorFlow and NumPy installed for CNN processing
Desktop environment for analyzing historical data and running the CNN program
Usage
App for Accelerometer Data:
Open the Android project in Android Studio.
Connect your Android device or use an emulator with accelerometer support.
Build and run the app on the device/emulator.
The app will display real-time orientation angles and store historical data in the database.
Navigate to the historical data section to view graphs of orientation over time.
CNN Image Classification:
Collect a set of images for classification.
Run the provided Python script (cnn_image_classification.py) in your desktop environment.
Follow the prompts to input the image data.
The script will execute a CNN model using TensorFlow for image classification.
Files
app/: Contains the Android app source code for accelerometer data collection.
cnn_image_classification.py: Python script for CNN image classification.
README.md: This file, providing instructions and information about the repository.
Contributing
Feel free to contribute to this repository by submitting pull requests or reporting issues.
