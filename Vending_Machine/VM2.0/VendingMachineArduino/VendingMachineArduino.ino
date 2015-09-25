#define Relay_1  2
#define Relay_2  3
#define Relay_3  4
#define Relay_4  5
#define Relay_5  6
#define Relay_6  7
#define Relay_7  8
#define Relay_8  9
#define Relay_9  10
#define Relay_10 11
#define Relay_11 12
#define Relay_12 A4
#define Relay_13 A0
#define Relay_14 A1
#define Relay_15 A2
#define Relay_16 A3

int row;
int col;

void setup()
{
  pinMode(Relay_1, OUTPUT);
  pinMode(Relay_2, OUTPUT);
  pinMode(Relay_3, OUTPUT);
  pinMode(Relay_4, OUTPUT);
  pinMode(Relay_5, OUTPUT);
  pinMode(Relay_6, OUTPUT);
  pinMode(Relay_7, OUTPUT);
  pinMode(Relay_8, OUTPUT);
  pinMode(Relay_9, OUTPUT);
  pinMode(Relay_10, OUTPUT);
  pinMode(Relay_11, OUTPUT);
  pinMode(Relay_12, OUTPUT);
  pinMode(Relay_13, OUTPUT);
  pinMode(Relay_14, OUTPUT);
  pinMode(Relay_15, OUTPUT);
  pinMode(Relay_16, OUTPUT);
  digitalWrite(Relay_1, HIGH);
  digitalWrite(Relay_2, HIGH);
  digitalWrite(Relay_3, HIGH);
  digitalWrite(Relay_4, HIGH);
  digitalWrite(Relay_5, HIGH);
  digitalWrite(Relay_6, HIGH);
  digitalWrite(Relay_7, HIGH);
  digitalWrite(Relay_8, HIGH);
  digitalWrite(Relay_9, HIGH);
  digitalWrite(Relay_10, HIGH);
  digitalWrite(Relay_11, HIGH);
  digitalWrite(Relay_12, HIGH);
  digitalWrite(Relay_13, HIGH);
  digitalWrite(Relay_14, HIGH);
  digitalWrite(Relay_15, HIGH);
  digitalWrite(Relay_16, HIGH);
  Serial.begin(9600);
}

void loop()
{  
 if(Serial.available())
  {  
    row = Serial.parseInt();
    col = Serial.parseInt();
    switchRow(row);
    switchCol(col);
    delay(7700);
    rowsOff();
    colsOff();
    Serial.print("*");
    while(Serial.available())
      Serial.read();
  }
}

void switchCol(int col)
{
  switch(col)
  {
     case 0: 
       colsOff();
       digitalWrite(Relay_1, LOW);
       break;
     case 1:
       colsOff();
       digitalWrite(Relay_2, LOW);
       break;
     case 2:
       colsOff();
       digitalWrite(Relay_3, LOW);
       break;
     case 3:
       colsOff();
       digitalWrite(Relay_4, LOW);
       break;
     case 4:
       colsOff();
       digitalWrite(Relay_5, LOW);
       break;
     case 5:
       colsOff();
       digitalWrite(Relay_6, LOW);
       break;
     case 6:
       colsOff();
       digitalWrite(Relay_7, LOW);
       break;
     case 7:
       colsOff();
       digitalWrite(Relay_8, LOW);
       break;
     case 8:
       colsOff();
       digitalWrite(Relay_9, LOW);
       break;
     case 9:
       colsOff();
       digitalWrite(Relay_10, LOW);
       break; 
     default:
       colsOff();
       break;     
  }
}

void colsOff()
{
  digitalWrite(Relay_1, HIGH);
  digitalWrite(Relay_2, HIGH);
  digitalWrite(Relay_3, HIGH);
  digitalWrite(Relay_4, HIGH);
  digitalWrite(Relay_5, HIGH);
  digitalWrite(Relay_6, HIGH);
  digitalWrite(Relay_7, HIGH);
  digitalWrite(Relay_8, HIGH);
  digitalWrite(Relay_9, HIGH);
  digitalWrite(Relay_10, HIGH);
}

void rowsOff()
{
  digitalWrite(Relay_11, HIGH);
  digitalWrite(Relay_12, HIGH);
  digitalWrite(Relay_13, HIGH);
  digitalWrite(Relay_14, HIGH);
  digitalWrite(Relay_15, HIGH);
  digitalWrite(Relay_16, HIGH);
}
  

void switchRow(int row)
{
  switch(row)
  {
     case 0: 
       rowsOff();
       digitalWrite(Relay_11, LOW);
       break;
     case 1:
       rowsOff();
       digitalWrite(Relay_12, LOW);
       break;
     case 2:
       rowsOff();
       digitalWrite(Relay_13, LOW);
       break;
     case 3:
       rowsOff();
       digitalWrite(Relay_14, LOW);
       break;
     case 4:
       rowsOff();
       digitalWrite(Relay_15, LOW);
       break;
     case 5:
       rowsOff();
       digitalWrite(Relay_16, LOW);
       break;
     default:
       rowsOff();
       break;
  }
}

