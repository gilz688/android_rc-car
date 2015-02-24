#include <Servo.h> 

String inputString = ""; 
boolean stringComplete = false;
int servoPin = 3;
int mainMotorPWMPin = 4;
int mainMotorDirPin = 5;
int mainMotorEnablePin = 6;
Servo steeringServo;  // create servo object to control steering servo motor

void setup() {
  // initialize serial:
  Serial.begin(9600);
  // reserve 200 bytes for the inputString:
  inputString.reserve(200);
  
  pinMode(mainMotorPWMPin, OUTPUT);
  pinMode(mainMotorDirPin, OUTPUT);
  pinMode(mainMotorEnablePin, OUTPUT);
  
  // initial position of steering motor
  // 90 is 0 degrees wrt center
  // values of servo should be within
  // 45 to 135
  steeringServo.write(90);
}

void loop() {
  if (stringComplete) {
    Serial.println(inputString);
    // process command
    process_command(inputString);
    // clear the string:
    inputString = "";
    stringComplete = false;
  }
}

void process_command(String command){
  // TODO
}

/*
  0 = stop, 255 = max speed
*/
void moveMainMotor(int direction, int speed){
  digitalWrite(mainMotorEnablePin, 1);
  digitalWrite(mainMotorDirPin, direction);
  analogWrite(mainMotorPWMPin, speed);
}

void stopMainMotor(){
  digitalWrite(mainMotorEnablePin, 0);
}

/*
  SerialEvent occurs whenever a new data comes in the
 hardware serial RX.
 */
void serialEvent() {
  while (Serial.available()) {
    // get the new byte:
    char inChar = (char)Serial.read();
    // add it to the inputString:
    inputString += inChar;
    // if the incoming character is a newline, set a flag
    // so the main loop can do something about it:
    if (inChar == '\n') {
      stringComplete = true;
    }
  }
}
