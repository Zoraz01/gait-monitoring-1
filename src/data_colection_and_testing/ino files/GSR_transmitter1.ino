
#include <ArduinoBLE.h>


byte mybuff[4] = {0, 0, 0, 0};

BLEService GSRService("180F");   // Set Service

// Bluetooth® Low Energy Battery Level Characteristic
BLECharacteristic GSRLevelChar("2A19",         // Set standard 16-bit characteristic UUID
    BLERead | BLENotify, 4);                   // remote clients will be able to get notifications if this characteristic changes


long previousMillis = 0;  // last time the battery level was checked, in ms
int GSR_pin = A0;         // Set pin A0 as GSR sensor input pin
int IMU_value = 0;        // Initialize GSR_value to be zero
int EMG_value = 0; 

int count = 0;

void setup() {
  Serial.begin(9600);     // initialize serial communication
  pinMode(GSR_pin,INPUT); // Set analog pin as input pin
  
  if (!BLE.begin()) {
    Serial.println("starting BLE failed!");
    while (1);   // Stay here until reset
  }

  /* Set a local name for the Bluetooth® Low Energy device
     This name will appear in advertising packets
     and can be used by remote devices to identify this Bluetooth® Low Energy device
     The name can be changed but maybe be truncated based on space left in advertisement packet
  */
  BLE.setLocalName("GSRMonitor");
  BLE.setAdvertisedService(GSRService);    // add the service UUID
  GSRService.addCharacteristic(GSRLevelChar); // add the battery level characteristic
  BLE.addService(GSRService); // Add the battery service
  GSRLevelChar.writeValue(mybuff,2); // set initial value for this characteristic

  /* Start advertising Bluetooth® Low Energy.  It will start continuously transmitting Bluetooth® Low Energy
     advertising packets and will be visible to remote Bluetooth® Low Energy central devices
     until it receives a new connection */

  // start advertising
  BLE.advertise();
}

void loop() {
  // wait for a Bluetooth® Low Energy central
  BLEDevice central = BLE.central();

  // if a central is connected to the peripheral:
  if (central) {
    
    // check the battery level every 1000ms
    // while the central is connected:
    while (central.connected()) {
      long currentMillis = millis();
      // if 1000ms have passed, check the battery level:
      if (currentMillis - previousMillis >= 1000) {
        previousMillis = currentMillis;
        updateGSRLevel();
      }
    }
    // when the central disconnects, turn off the LED:
    digitalWrite(LED_BUILTIN, LOW);
  }
}

void updateGSRLevel() {
  // Read the current voltage level on the A0 analog input pin
   if (count == 0)
   {
       IMU_value = 1000;      
       EMG_value = 1500;
   }
       else if (count == 1)
       {
       IMU_value = -1000;      
       EMG_value = -1500;
   }
   count = count+1;
   if (count > 1)
      {
       count = 0;
      }
   
   mybuff[0] = IMU_value >> 8;
   mybuff[1] = IMU_value;
   
   mybuff[2] = EMG_value >> 8;
   mybuff[3] = EMG_value;
   
   GSRLevelChar.writeValue(mybuff,4);  // and update the battery level characteristic
}
