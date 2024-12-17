#include <ArduinoBLE.h>
#include <LSM6DS3.h>
#include <Wire.h>

LSM6DS3 myIMU(I2C_MODE, 0x6A);    // I2C device address 0x6A

byte mybuff[8] = {0, 0, 0, 0, 0, 0, 0, 0};

BLEService CaneService("180F");  // Cane Service UUID

// Correcting the declaration for BLECharacteristic
BLECharacteristic CaneDataChar("2A19", BLERead | BLENotify, 8);  // Cane data characteristic

long previousMillis = 0;  // Last time the battery level was checked, in ms
int EMG_pin = A0;         // Set pin A0 as emg reading pin
int EMG_value = 0;        // Initialize emg value to be zero
int IMU_value1 = 0;       // Initialize IMU_value1 to be zero
int IMU_value2 = 0;       // Initialize IMU_value2 to be zero
int IMU_value3 = 0;       // Initialize IMU_value3 to be zero
int redLedPin = 8;        // Red LED pin
int greenLedPin = 5;      // Green LED pin
int sampleRate = 10;      // Sample rate in milliseconds

void setup() {
  Serial.begin(9600);
  pinMode(EMG_pin, INPUT);
  pinMode(redLedPin, OUTPUT);
  pinMode(greenLedPin, OUTPUT);
  pinMode(LED_BLUE, OUTPUT);
  
  if (myIMU.begin() != 0) {
  } else {
  }
  
  if (!BLE.begin()) {
    Serial.println("starting BLE failed!");
    while (1);
  }
  
  BLE.setLocalName("CaneMonitor");
  BLE.setAdvertisedService(CaneService);
  CaneService.addCharacteristic(CaneDataChar); // Now correctly referencing the declared characteristic
  BLE.addService(CaneService);
  BLE.advertise();
}

void loop() {
  BLEDevice central = BLE.central();

  if (central) {
    Serial.println("Cane is connected to phone");
    while (central.connected()) {
      long currentMillis = millis();
      if (currentMillis - previousMillis >= sampleRate) {
        previousMillis = currentMillis;
        updateCaneData();
      }
    }
    Serial.println("Phone disconnected");
    digitalWrite(redLedPin, HIGH);
    digitalWrite(greenLedPin, LOW);
    digitalWrite(LED_BLUE, HIGH);
  }
}

void updateCaneData() {
  IMU_value1 = (int)myIMU.readFloatGyroY();
  IMU_value2 = (int)myIMU.readFloatGyroX();
  IMU_value3 = (int)myIMU.readFloatAccelY();
  
  mybuff[0] = IMU_value1 >> 8;
  mybuff[1] = IMU_value1;
  mybuff[2] = IMU_value2 >> 8;
  mybuff[3] = IMU_value2;
  mybuff[4] = IMU_value3 >> 8;
  mybuff[5] = IMU_value3;
  
  CaneDataChar.writeValue(mybuff, 8);  // Correctly use the characteristic to write values

  // Battery voltage checking moved into the correct function
  float batteryVoltage = readBatteryVoltage();
  if (isCharging(batteryVoltage)) {
    chargingLED();
  } else {
    onLED();
  }
}

float readBatteryVoltage() {
  int sensorValue = analogRead(A1);  // Assuming A1 is your battery pin
  return sensorValue * (5.0 / 1023.0);  // Convert analog reading to voltage
}

bool isCharging(float voltage) {
  const float batteryThreshold = 3.7; // Threshold voltage for considering the battery as charging
  return voltage > batteryThreshold;
}

void chargingLED() {
  static bool flash = false;
  digitalWrite(redLedPin, flash ? HIGH : LOW);
  flash = !flash;
  digitalWrite(greenLedPin, LOW);
}

void onLED() {
  digitalWrite(greenLedPin, HIGH);
  digitalWrite(redLedPin, LOW);
}