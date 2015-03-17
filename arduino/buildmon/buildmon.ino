#define DEFAULTBLUESTEPPING 15
#define DEFAULTREDSTEPPING 25

const int buzzer = 9;
const int redled = 10;
const int greenled = 3;
const int blueled = 11;

const byte BLANK = '0';
const byte BUILDING = '1';
const byte SUCCESS = '2';
const byte FAILURE = '3';
const byte WARNING = '4';
const byte UNKNOWN = '5';
const byte BUZZ = '6';
const byte BEEP = '7';
const byte DEMO = '8';

byte previous_state = DEMO;
byte state = DEMO;

int redbrightness = 0;
int redstepping = DEFAULTREDSTEPPING;
int redpulses = 0;

int greenbrightness = 0;

int bluebrightness = 255;
int bluestepping = DEFAULTBLUESTEPPING;

void setup()  {
  Serial.begin(9600);
  randomSeed(analogRead(0));
  pinMode(redled, OUTPUT);
  pinMode(greenled, OUTPUT);
  pinMode(blueled, OUTPUT);
  analogWrite(redled, 0);
  analogWrite(greenled, 0);
  analogWrite(blueled, 0);
  Serial.write(125);
}

// the loop routine runs over and over again forever:
void loop()  {
  //    Serial.print("State: ");
  //    Serial.println(state);

  if (Serial.available() > 0)
  {
    serialEvent();
  }

  switch (state)
  {
    case FAILURE:
      red();
      state = 200;
      break;
    case SUCCESS:
      green();
      //       state = 500;
      break;
    case BUILDING:
      blue();
      state = 100;
      break;
    case WARNING:
      yellow();
      break;
    case UNKNOWN:
      purple();
      state = 250;
      break;
    case DEMO:
      writeRGB(random(256), random(250), random(216));
      break;
    case BLANK:
      reset();
      break;
    case BUZZ:
      buzz();
      state = previous_state;
      break;
    case BEEP:
      beep();
      state = previous_state;
      break;
    case 100:
      fadeblue();
      break;
    case 200:
      pulsered();
      if (redpulses > 15)
      {
        state = 201;
      }
      break;
    case 201:
      red();
      break;
    case 250:
      fadepurple();
      break;
  }
  play_note();
}

void reset()
{
  writeRGB(0, 0, 0);
}


void serialEvent()
{
  byte command = state;
  command = Serial.read();
  while (Serial.available() > 0)
  {
    Serial.read();
  }

  //  Serial.print("Sent Command: ");
  //  Serial.write(command);
  //  Serial.println();
  if (command > DEMO || command < BLANK)
  {
    command = DEMO;
  }
  //  Serial.print("Picked Command: ");
  //  Serial.write(command);
  //  Serial.println();
  previous_state = state;
  state = command;
}

void red()
{
  redstepping = DEFAULTREDSTEPPING;
  redpulses = 0;
  redbrightness = 255;
  writeRGB(255, 0, 0);
}

void pulsered()
{
  writeRGB(redbrightness, 0, 0);
  if (redbrightness <= 50 || redbrightness >= 255)
  {
    redstepping *= -1;
    redpulses++;
  }
  redbrightness += redstepping;
}

void green()
{
  writeRGB(0, 255, 0);
}

void fadeblue()
{
  writeRGB(0, 15, bluebrightness);
  if (bluebrightness <= 25 || bluebrightness >= 255)
  {
    bluestepping *= -1;
  }
  bluebrightness += bluestepping;
}

void blue()
{
  bluestepping = DEFAULTBLUESTEPPING;
  bluebrightness = 255;
  writeRGB(0, 15, 255);
}


void yellow()
{
  writeRGB(255, 100, 0);
}

void purple()
{
  writeRGB(255, 0, 155);
  bluestepping = 25;
  bluebrightness = 155;
  redstepping = 25;
  redbrightness = 255;
}

void fadepurple()
{
  writeRGB(redbrightness, 15, bluebrightness);
  if (bluebrightness <= 25 || bluebrightness >= 155)
  {
    bluestepping *= -1;
  }
  bluebrightness += bluestepping;
  if (redbrightness <= 150 || redbrightness >= 255)
  {
    redstepping *= -1;
  }
  redbrightness += redstepping;
}


void writeRed(int r)
{
  analogWrite(redled, r);
  //  redbrightness = r;
}

void writeGreen(int g)
{
  analogWrite(greenled, g);
  //  greenbrightness = g;
}

void writeBlue(int b)
{
  analogWrite(blueled, b);
  //  bluebrightness = b;
}

void writeRGB(int r, int g, int b)
{
  writeRed(r);
  writeGreen(g);
  writeBlue(b);
}

