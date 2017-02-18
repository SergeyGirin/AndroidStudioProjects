#include <Wire.h>
#include "rgb_lcd.h"

rgb_lcd lcd;

const int pinLight = A0;
const int pinTemp = A1;

void setup() {
  Serial.begin(9600);
  
  Serial1.begin(9600); //Set BLE baud rate to default
  Serial1.print("AT+CLEAR"); //clear all previous settings
  Serial1.print("AT+ROLE0"); //set the Grove as a BLE slave
  Serial1.print("AT+SAVE1");  //don't save the connection settings
  
  // set up the LCD's columns and rows
    lcd.begin(16, 2);
}

char recvChar;

void loop() {
  if(Serial.available())
    {
       recvChar = Serial.read();
       Serial1.print(recvChar);
       
    }
    
    if(Serial1.available())
    {
       recvChar = Serial1.read();
       Serial.print(recvChar);
    //   lcd.print(recvChar);
    }
    
    int sensorValue = analogRead(pinLight);    //the light sensor is attached to analog 0
    lcd.clear();
    lcd.print(sensorValue);
 }
