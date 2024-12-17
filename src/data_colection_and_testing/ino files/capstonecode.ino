#include <LSM6DS3.h>
#include <Wire.h>
//#include <FlashStorage.h>
//Capstone code

// Create an instance of class LSM6DS3
LSM6DS3 myIMU(I2C_MODE, 0x6A); // I2C device address 0x6A
float aX, aY, aZ, gX, gY, gZ;
const float accelerationThreshold = 2.5; // threshold of significance in G's
const int numSamples = 119;
int samplesRead = numSamples;

// Define a struct to hold sensor data
struct SensorData {
  float gyroX;
  float gyroY;
  float gyroZ;
};

// Create an instance of the FlashStorage class to store the data
//FlashStorage(sensorDataStorage, SensorData);

void setup() {
  // put your setup code here, to run once:
  Serial.begin(9600);
  while (!Serial);

  // Call .begin() to configure the IMU
  if (myIMU.begin() != 0) {
    Serial.println("Device error");
  } else {
    Serial.println("aX,aY,aZ,gX,gY,gZ");
  }
}

void loop() {
  // Read sensor data
  float gyroX = myIMU.readFloatGyroX();
  float gyroY = myIMU.readFloatGyroY();
  float gyroZ = myIMU.readFloatGyroZ();

  // Print sensor data to Serial (for debugging)
  Serial.print(gyroX, 3);
  Serial.print(',');
  Serial.print(gyroY, 3);
  Serial.print(',');
  Serial.print(gyroZ, 3);
  Serial.println();

  // Store sensor data in Flash memory
  SensorData dataToStore;
  dataToStore.gyroX = gyroX;
  dataToStore.gyroY = gyroY;
  dataToStore.gyroZ = gyroZ;

  // Write the data to Flash memory
  //sensorDataStorage.write(dataToStore);

  // Delay for a short period if needed
  delay(100); // Adjust the delay as needed to control data capture frequency
}