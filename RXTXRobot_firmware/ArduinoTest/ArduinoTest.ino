boolean digitalOk;
boolean analogOk;
boolean digimem;
boolean anmem;
int digital[3];
int analog[6];
boolean test;
boolean digi[3];
boolean an[6];

void setup() {
  pinMode(2, INPUT);
  pinMode(3, INPUT);
  pinMode(4, INPUT);
  pinMode(5, OUTPUT);
  pinMode(6, OUTPUT);
  pinMode(7, OUTPUT);
  pinMode(8, OUTPUT);
  pinMode(9, OUTPUT);
  pinMode(10, OUTPUT);
  pinMode(11, OUTPUT);
  pinMode(12, OUTPUT);
  pinMode(13, OUTPUT);
  Serial.begin(9600);
  test = true;
}

void loop() {
  /*
  PB 0, 1, 2 ,3, 4, 5
  PD 5, 6, 7
  
  */
  //Toggle all pins ON
  PORTB |= 0b11111111;
  PORTD |= 0b11111111;
  tester(1);
  check();
  //Remember if all pins were ok
  digimem = digitalOk;
  anmem = analogOk;
  delay(500);
  //Toggle all pins off
  PORTB &= 0x00;
  PORTD &= 0x00;
  tester(0);
  check();
  delay(500);
  
  if(!(digimem && digitalOk)) //Are all digital pins ok?
  {
    for(int i = 0; i < 3; i++)
      if(!digi[i]) //If this pin is bad, say so
      {
       Serial.print("Digital Pin ");
       Serial.print(i+2);
       Serial.println(" inoperable");
      } 
  }
  else
    Serial.println("Digital OK");
  if(!(anmem && analogOk)) //Are all analog pins ok?
  {
    for(int i = 0; i < 6; i++)
      if(!an[i])//If this pin is bad, say so
      {
        Serial.print("Analog Pin ");
        Serial.print(i);
        Serial.println(" inoperable");
      }
  }
  else
    Serial.println("Analog OK");
}

void tester(int input)
{
  
  delay(200);
  //Refresh all pins
  digital[0] = digitalRead(2);
  digital[1] = digitalRead(3);
  digital[2] = digitalRead(4);
  analog[0] = analogRead(0);
  analog[1] = analogRead(1);
  analog[2] = analogRead(2);
  analog[3] = analogRead(3);
  analog[4] = analogRead(4);
  analog[5] = analogRead(5);
  //Set memories
  for(int i = 0; i < 3; i++)
  {
    if(digital[i] == input)
      digi[i] = true;
    else
      digi[i] = false;
    
  }
  for(int i = 0; i < 6; i++)
  {
    if(analog[i] > 512)
      if(input == 1)
        an[i] = true;
      else
        an[i] = false;
    else
      if(input == 0)
        an[i] = true;
      else
        an[i] = false;
  } 
}

void check()
{
  digitalOk = true;
  analogOk = true;
  for(int i = 0; i < 3; i++)
    if(!digi[i]) //If any pins were found bad, set the global variable to false
      digitalOk = false;
  for(int i = 0; i < 6; i++)
    if(!an[i]) //If any pins were found bad, set the global variable to false
      analogOk = false;
}
