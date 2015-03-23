#include <Servo.h>
#include <NewPing.h>

// declaration of pins
int const servoPin = 3;
int const mainMotorDirPin = 4;
int const mainMotorPWMPin = 5;

int direction = 1;
int pwm = 0;

// ultrasonic senser
const int triggerPin = 6;
const int echoPin = 7;
const int maxDistance = 30;

NewPing sonar(triggerPin, echoPin, maxDistance);
unsigned int pingSpeed = 20;
unsigned long pingTimer;

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
  
  // setup ultrasonic sensor
  pingTimer = millis();
}

void loop() {
  if (stringComplete) {
    // process command
    processCommand(inputString);
    // clear the string:
    
    inputString = "";
    stringComplete = false;
  }
 
  if (millis() >= pingTimer) {   // pingSpeed milliseconds since last ping, do another ping.
    pingTimer += pingSpeed;      // Set the next ping time.
    sonar.ping_timer(updateDistance);
  }
}

void updateDistance(){
    if (sonar.check_timer()) {
      float distance = sonar.ping_result / US_ROUNDTRIP_CM;
      Serial.println(distance);
      if(distance < 10 ){
        brake();
      } else if(distance < 100 && pwm > 150){
        pwm = 150;
      }
  }
}

void brake(){
  if(pwm == 0)
    return;
    
  if(direction == 1){
    digitalWrite(mainMotorDirPin, 0);
    analogWrite(mainMotorPWMPin, 255);
    delay(1000);
    analogWrite(mainMotorPWMPin, 0);
    pwm = 0;
    Serial.println("brake");
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
  0 = stop, 100 = max speed
*/
void moveMainMotor(int speed){
  digitalWrite(13, HIGH);
  if(speed < 0){
    direction = 0;
    speed = -speed;
  } else{
    direction = 1;
  }
  
  if(speed == 0){
    pwm = 0;
  } else{
    pwm = map(speed, 1, 100, 150, 255);
  }
  
  digitalWrite(mainMotorDirPin, direction);
  analogWrite(mainMotorPWMPin, pwm);
}

void moveSteeringMotor(int angle){
  steeringServo.write(90 + angle);
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
