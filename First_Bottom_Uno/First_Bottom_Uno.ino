#include <Wire.h>
#include <Adafruit_MotorShield.h>
#include "utility/Adafruit_PWMServoDriver.h"
#include <SoftwareSerial.h>
#include <Thread.h>
#include <ThreadController.h>
#include <SPI.h>

Adafruit_MotorShield AFMS = Adafruit_MotorShield();
Adafruit_DCMotor *myMotorFL = AFMS.getMotor(3);
Adafruit_DCMotor *myMotorFR = AFMS.getMotor(2);
Adafruit_DCMotor *myMotorBR = AFMS.getMotor(1);
Adafruit_DCMotor *myMotorBL = AFMS.getMotor(4);

SoftwareSerial bluetooth(7, 6);

Thread thBlueTooth = Thread();
ThreadController controll = ThreadController();

bool isdetect;
bool iscover;

void bluetooththread()
{
  char cmd;
  String data;
  String speed;
  if (bluetooth.available())
  {
    while (bluetooth.available())
    {
      cmd = (char)bluetooth.read();
      data += cmd;
    }

    data.trim();
    Serial.println(data);

    if (Split(data, '/', 0) == "motorspeed")
    {
      speed = Split(data, '/', 1);
      data = Split(data, '/', 0);
      Serial.println(data);
      Serial.println(speed);
    }

    if (data == "leftup")
    {
      Serial.println(data);

      myMotorFL->run(RELEASE);
      myMotorFR->run(BACKWARD);
      myMotorBL->run(RELEASE);
      myMotorBR->run(BACKWARD);

    }
    else if (data == "up")
    {
      Serial.println(data);

      myMotorFL->run(FORWARD);
      myMotorFR->run(BACKWARD);
      myMotorBL->run(FORWARD);
      myMotorBR->run(BACKWARD);

    }
    else if (data == "rightup")
    {
      Serial.println(data);

      myMotorFL->run(FORWARD);
      myMotorFR->run(RELEASE);
      myMotorBL->run(FORWARD);
      myMotorBR->run(RELEASE);

    }
    else if (data == "left")
    {
      Serial.println(data);
      
      myMotorFL->run(BACKWARD);
      myMotorFR->run(BACKWARD);
      myMotorBL->run(BACKWARD);
      myMotorBR->run(BACKWARD);
    }
    else if (data == "right")
    {
      Serial.println(data);

      myMotorFL->run(FORWARD);
      myMotorFR->run(FORWARD);
      myMotorBL->run(FORWARD);
      myMotorBR->run(FORWARD);

    } else if (data == "leftdown")
    {
      Serial.println(data);

      myMotorFL->run(RELEASE);
      myMotorFR->run(FORWARD);
      myMotorBL->run(RELEASE);
      myMotorBR->run(FORWARD);

    } else if (data == "down")
    {
      Serial.println(data);

      myMotorFL->run(BACKWARD);
      myMotorFR->run(FORWARD);
      myMotorBL->run(BACKWARD);
      myMotorBR->run(FORWARD);
      
    } else if (data == "rightdown")
    {
      Serial.println(data);

      myMotorFL->run(BACKWARD);
      myMotorFR->run(RELEASE);
      myMotorBL->run(BACKWARD);
      myMotorBR->run(RELEASE);
    }
    else if (data == "stop")
    {
      Serial.println(data);
      myMotorFL->run(RELEASE);
      myMotorFR->run(RELEASE);
      myMotorBL->run(RELEASE);
      myMotorBR->run(RELEASE);
    }
    else if (data == "motorspeed")
    {
      myMotorFL->setSpeed(speed.toInt());
      myMotorFR->setSpeed(speed.toInt());
      myMotorBL->setSpeed(speed.toInt());
      myMotorBR->setSpeed(speed.toInt());
    }
    else if(data == "coveropen")
    {
      Wire.beginTransmission(4);
      Wire.write('p');
      Wire.endTransmission();
    }
    else if(data == "coverclose")
    {
      Wire.beginTransmission(4);
      Wire.write('c');
      Wire.endTransmission();
    }
    else if(data == "detmovon")
    {
      Wire.beginTransmission(4); 
      Wire.write('o');
      Wire.endTransmission();
    }
    else if(data == "detmovoff")
    {
      Wire.beginTransmission(4);
      Wire.write('x');
      Wire.endTransmission();
    }
    else if(data == "reset")
    {
      Wire.beginTransmission(4);
      Wire.write('x');
      Wire.endTransmission();
      
      delay(500);
      
      Wire.beginTransmission(4);
      Wire.write('c');
      Wire.endTransmission();

      delay(100);
      
      myMotorFL->run(RELEASE);
      myMotorFR->run(RELEASE);
      myMotorBL->run(RELEASE);
      myMotorBR->run(RELEASE);

      delay(100);
      
      myMotorFL->setSpeed(230);
      myMotorFR->setSpeed(230);
      myMotorBL->setSpeed(230);
      myMotorBR->setSpeed(230);
    }
    else
    {
      Serial.println(data);
      data = "";
    }
  }
}

void bluetoothwrite()
{
  
  bluetooth.write("Dd");
}

String Split(String sData, char cSeparator, int reqindex)
{
  int nCount = 0;
  int nGetindex = 0;

  String sTemp = "";

  String sCopy = sData;

  while (true)
  {
    nGetindex = sCopy.indexOf(cSeparator);

    if (nGetindex != -1)
    {
      sTemp = sCopy.substring(0, nGetindex);

      sCopy = sCopy.substring(nGetindex + 1);
    }
    else
    {
      if (reqindex == 0)
      {
        return sTemp;
      }
      else
      {
        return sCopy;
      }
      break;
    }
    ++nCount;
  }
}

void setup() {
  // put your setup code here, to run once:
  Serial.begin(9600);
  bluetooth.begin(9600);
  Wire.begin();
  AFMS.begin();

  myMotorFL->setSpeed(230);
  myMotorFR->setSpeed(230);
  myMotorBL->setSpeed(230);
  myMotorBR->setSpeed(230);

  thBlueTooth.onRun(bluetooththread);
  thBlueTooth.setInterval(100);

  controll.add(&thBlueTooth);
}

void loop() {
  // put your main code here, to run repeatedly:
  controll.run();
}
