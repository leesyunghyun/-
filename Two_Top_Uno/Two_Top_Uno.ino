#include <Wire.h>
#include <Adafruit_MotorShield.h>
#include "utility/Adafruit_PWMServoDriver.h"
#include <SoftwareSerial.h>
#include <Thread.h>
#include <ThreadController.h>
#include <Servo.h>

Adafruit_MotorShield AFMS = Adafruit_MotorShield();

Thread thServoOpen = Thread();
Thread thServoClose = Thread();
Thread thServoOC = Thread();
Thread thDetectMov = Thread();
ThreadController controll = ThreadController();

Servo openServo;
Servo closeServo;

#define PIR 7

void opclCover()
{

}

void openCover()
{
  if((openServo.attached() == false) && (closeServo.attached() == false))
  {
    Serial.println("mt: opencover: attach");
    openServo.attach(9);
    closeServo.attach(10);
  }

  if((openServo.readMicroseconds() != 3600) && (closeServo.readMicroseconds() != 3600))
  {
     Serial.println("mt: opencover");
     openServo.writeMicroseconds(3600);
     closeServo.writeMicroseconds(3600);
  }
  delay(2000);

  controll.remove(&thServoOpen);
}

void closeCover()
{
  if((openServo.attached() == false) && (closeServo.attached() == false))
  {
    Serial.println("mt: closecover: attach");
    openServo.attach(9);
    closeServo.attach(10);
  }

  if((openServo.readMicroseconds() != 0) && (closeServo.readMicroseconds() != 0))
  {
    Serial.println("mt: closecover");
    openServo.writeMicroseconds(0);
    closeServo.writeMicroseconds(0);
  }
  

  delay(2000);

  controll.remove(&thServoClose);
}

void receiveEvent(int howmany)
{  
  if(Wire.available())
  {
    while(Wire.available())
    {
      char c = Wire.read();
      if(c == 'o')
      {
        Serial.println("revEv: detectadd");
        controll.add(&thDetectMov);
      }
      else if(c == 'p')
      {
        Serial.println("revEv: opencover");
        controll.add(&thServoOpen);
      }
      else if(c == 'c')
      {
        Serial.println("revEv: closecover");
        controll.add(&thServoClose);
      }
      else if(c == 'x')
      {
        Serial.println("revEv: detectdelete");
        controll.remove(&thDetectMov);
        controll.add(&thServoClose);
      }
      else
      {
        Serial.println("revEv: " + c);
      }
    }
  }
  else
  {
    delay(100);
  }
}

void detected()
{
  if((openServo.attached() == false) && (closeServo.attached() == false))
  {
    Serial.println("mt: detect: attach");
    openServo.attach(9);
    closeServo.attach(10);
  }

  
  if(digitalRead(PIR) == HIGH)
  {
    if((openServo.readMicroseconds() != 3600) && (closeServo.readMicroseconds() != 3600))
    {
      Serial.println("mt: detect: No 3600");
      openServo.writeMicroseconds(3600);
      closeServo.writeMicroseconds(3600);
      delay(10000);
    }
  }
  else
  {
    if((openServo.readMicroseconds() != 0) && (closeServo.readMicroseconds() != 0))
    {
      Serial.println("mt: detect: No Zero");
      openServo.writeMicroseconds(0);
      closeServo.writeMicroseconds(0);
    }
  }
  delay(500);
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
  Wire.begin(4);
  Wire.onReceive(receiveEvent); 
  AFMS.begin();
  pinMode(PIR,INPUT);
 // pinMode(12,OUTPUT); 
  
  openServo.attach(9);
  closeServo.attach(10);

  thServoOpen.onRun(openCover);
  thServoOpen.setInterval(100);

  thServoClose.onRun(closeCover);
  thServoClose.setInterval(100);

  thDetectMov.onRun(detected);
  thDetectMov.setInterval(100);

  controll.add(&thServoClose);
}

void loop() {
  // put your main code here, to run repeatedly:
  controll.run();
}
