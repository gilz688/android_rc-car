#include <Servo.h>

// declaration of pins
int const servoPin = 3;
int const mainMotorDirPin = 4;
int const mainMotorPWMPin = 5;

String inputString = ""; 
boolean stringComplete = false;

Servo steeringServo;  // create servo object to control steering servo motor

void setup() {
  // initialize serial:
  Serial.begin(9600);
  // reserve 200 bytes for the inputString:
  inputString.reserve(200);
  pinMode(13,OUTPUT);
  pinMode(mainMotorPWMPin, OUTPUT);
  pinMode(mainMotorDirPin, OUTPUT);
  
  // initial position of steering motor
  // 90 is 0 degrees wrt center
  // values of servo should be within
  // 45 to 135
  steeringServo.attach(servoPin);
  steeringServo.write(90);
}

void loop() {
  if (stringComplete) {
    // process command
    processCommand(inputString);
    // clear the string:
    
    inputString = "";
    stringComplete = false;
  }
}

void processCommand(String line){
  int separator = line.indexOf(' ');
  if(separator == -1)
    separator = line.length();
  String command = line.substring(0,separator);
  String rawParam = line.substring(separator+1);
  int param = rawParam.toInt();
  boolean hasParam = rawParam.length() > 0;
  
  boolean validParam = String(param).equalsIgnoreCase(rawParam);
  boolean valid = false;
  
  if(validParam){
    valid = processIntParam(command, param);
  }
  
  if(valid){
    sendOk();
  } else{
    sendInvalid();
  }
}

boolean processIntParam(String command, int param){
  boolean valid = false;
  if(command.equalsIgnoreCase("move")){
    moveMainMotor(param);
    valid = true;
  } else if(command.equalsIgnoreCase("steer")){
    moveSteeringMotor(param);
    valid = true;
  }
  return valid;
}

void sendOk(){
   Serial.println("OK");
}

void sendInvalid(){
  Serial.println("INVALID COMMAND");
}

/*
  0 = stop, 255 = max speed
*/
void moveMainMotor(int speed){
  int direction = 1;
  digitalWrite(13, HIGH);
  if(speed < 0){
    direction = 0;
    speed = -speed;
  }
  digitalWrite(mainMotorDirPin, direction);
  analogWrite(mainMotorPWMPin, speed);
}

void moveSteeringMotor(int angle){
  steeringServo.write(90 - angle);
}

/*
  SerialEvent occurs whenever a new data comes in the
 hardware serial RX.
 */
void serialEvent() {
  while (Serial.available()) {
    // get the new byte:
    char inChar = (char) Serial.read();
    
    // if the incoming character is a newline, set a flag
    // so the main loop can do something about it:
    if (inChar == 0x0D) {
      stringComplete = true;
    } else{
      // add it to the inputString:
      inputString += inChar;
    }
  }
}
