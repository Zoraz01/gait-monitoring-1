#include <ArduinoBLE.h>
#include <LSM6DS3.h>
#include <Wire.h>

LSM6DS3 myIMU(I2C_MODE, 0x6A);    //I2C device address 0x6A

byte mybuff[8] = {0, 0, 0, 0, 0, 0, 0, 0};

BLEService ShankService("180F");   // Set Service

// Bluetooth® Low Energy Battery Level Characteristic
BLECharacteristic ShankLevelChar("2A19",         // Set standard 16-bit characteristic UUID
    BLERead | BLENotify, 8);                   // remote clients will be able to get notifications if this characteristic changes

long previousMillis = 0;  // last time the battery level was checked, in ms
int EMG_pin = A0;         // Set pin A0 as emg reading pin
int EMG_value = 0;        // Initialize emg value to be zero
int IMU_value1 = 0;        // Initialize IMU_value1 to be zero
int IMU_value2 = 0;        // Initialize IMU_value2 to be zero
int IMU_value3 = 0;        // Initialize IMU_value3 to be zero
int redLedPin = 8;       // Red LED pin
int greenLedPin = 5;     // Green LED pin

const float batteryThreshold = 3.7; // Threshold voltage for considering the battery as charging
const int batteryPin = A1; // Analog pin to read battery voltage


void setup() {
  Serial.begin(9600);     // initialize serial communication
  pinMode(EMG_pin, INPUT); // Set analog pin as input pin
  pinMode(redLedPin, OUTPUT); // Set Red LED pin as output
  pinMode(greenLedPin, OUTPUT); // Set Green LED pin as output
  pinMode(LED_BLUE, OUTPUT);
  
  if (myIMU.begin() != 0) {
  } else {
  }
  
  if (!BLE.begin()) {
    Serial.println("starting BLE failed!");
    while (1);   // Stay here until reset
  }
  
  BLE.setLocalName("ShankMonitor");
  BLE.setAdvertisedService(ShankService);    // add the service UUID
  ShankService.addCharacteristic(ShankLevelChar); // add the battery level characteristic
  BLE.addService(ShankService); // Add the battery service
  ShankLevelChar.writeValue(mybuff, 8); // set initial value for this characteristic

  BLE.advertise(); // Start advertising Bluetooth® Low Energy
}

void loop() {
  BLEDevice central = BLE.central(); // wait for a Bluetooth® Low Energy central

  if (central) {
    Serial.println("Shank is connected to phone");
    while (central.connected()) { // if a central is connected to the peripheral
      long currentMillis = millis();
      if (currentMillis - previousMillis >= 10) { // check the sensor values every 10ms
        previousMillis = currentMillis;
        updateShankLevel();
      }
    }
    Serial.println("Phone disconnected");
    digitalWrite(redLedPin, HIGH); // when the central disconnects, turn off green LED
    digitalWrite(greenLedPin, LOW);
    digitalWrite(LED_BLUE, HIGH);
  }
}

void updateShankLevel() {
  IMU_value1 = (int)myIMU.readFloatGyroY();
  IMU_value2 = (int)myIMU.readFloatGyroX();
  IMU_value3 = (int)myIMU.readFloatAccelY();
  // EMG_value = analogRead(EMG_pin);
   
  mybuff[0] = IMU_value1 >> 8;
  mybuff[1] = IMU_value1;
   
  mybuff[2] = IMU_value2 >> 8;
  mybuff[3] = IMU_value2;
   
  mybuff[4] = IMU_value3 >> 8;
  mybuff[5] = IMU_value3;
   
  mybuff[6] = EMG_value >> 8;
  mybuff[7] = EMG_value;

  ShankLevelChar.writeValue(mybuff, 8);  // Update the battery level characteristic

 // LED control based on conditions
  float batteryVoltage = readBatteryVoltage();
  if (isCharging(batteryVoltage)) {
    chargingLED();
  } else {
    onLED();
  }
}

void chargingLED() {
  static bool flash = false;
  if (flash) {
    digitalWrite(redLedPin, HIGH);
  } else {
    digitalWrite(redLedPin, LOW);
  }
  flash = !flash;
  digitalWrite(greenLedPin, LOW); // Turn off green LED
  delay(500); // Flashing interval
}

void onLED() {
  digitalWrite(greenLedPin, HIGH);
  digitalWrite(redLedPin, LOW);
}

bool isCharging(float voltage) {
  return voltage > batteryThreshold;
}

float readBatteryVoltage() {
  int sensorValue = analogRead(batteryPin);
  float voltage = sensorValue * (5.0 / 1023.0); // Convert analog reading to voltage
  return voltage;
}