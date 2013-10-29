#include <Wire.h> 
#include <LiquidCrystal_I2C.h> 
LiquidCrystal_I2C lcd(0x27,16,2);
int DHpin = 8; 
int buzzer=10;//设置控制蜂鸣器的数字IO脚
byte dat[5]; 
//char c_read;
boolean ledflag=false;
boolean wenflag=false;
boolean shiflag=false;
byte read_data() 
{ 
  byte data; 
  for(int i=0; i<8; i++)
  { 
    if(digitalRead(DHpin) == LOW)
    { 
      while(digitalRead(DHpin) == LOW); //等待50us；
      delayMicroseconds(30); //判断高电平的持续时间，以判定数据是‘0’还是‘1’； 
      if(digitalRead(DHpin) == HIGH) 
      data |= (1<<(7-i)); //高位在前，低位在后；
      while(digitalRead(DHpin) == HIGH); //数据‘1’，等待下一位的接收； 
    } 
  } 
return data; 
}
void shidu()
{
 // start_test(); 
  //lcd.clear();
  lcd.setCursor(0,0);
  lcd.print("HUMDITY:");
  lcd.print(dat[0]);
  lcd.print(".");
  lcd.print(dat[1]);
  Serial.print("%");
  Serial.print(dat[0]); //显示湿度的整数位；
  Serial.print(".");
  Serial.print(dat[1]);
  Serial.print("@");
}
void wendu()
{
   
 // lcd.clear();
  lcd.setCursor(0,1);
  lcd.print("TEMPERATURE:");
  lcd.print(dat[2]);
  lcd.print(".");
  lcd.print(dat[3]); 
 // delay(1000);//延时0.5 秒
  Serial.print("%");
  Serial.print(dat[2]); //显示温度的整数位； 
  Serial.print(".");
  Serial.print(dat[3]);
  Serial.print("@");
}
void start_test() 
{ 
  digitalWrite(DHpin,LOW); //拉低总线，发开始信号； 
  delay(30); //延时要大于18ms，以便DHT11能检测到开始信号；
  digitalWrite(DHpin,HIGH); 
  delayMicroseconds(40); //等待DHT11响应； 
  pinMode(DHpin,INPUT);
  while(digitalRead(DHpin) == HIGH); 
  delayMicroseconds(80); //DHT11发出响应，拉低总线80us； 
  if(digitalRead(DHpin) == LOW);
  delayMicroseconds(80); //DHT11拉高总线80us后开始发送数据； 
  for(int i=0;i<4;i++) //接收温湿度数据，校验位不考虑； 
  dat[i] = read_data(); 
  pinMode(DHpin,OUTPUT);
  digitalWrite(DHpin,HIGH); //发送完一次数据后释放总线，等待主机的下一次开始信号； 
}
void setup() 
{ 
lcd.init(); // initialize the lcd // Print a message to the LCD. 
lcd.backlight(); 
//pinMode(7, OUTPUT);
Serial.begin(9600);

pinMode(DHpin,OUTPUT); 
pinMode(buzzer,OUTPUT);//设置数字IO脚模式，OUTPUT为辒出 
//lcd.print("www.geeetech.com"); 
}
void loop()
{
  char c_read;
  while(Serial.available())
   {
     c_read=Serial.read();
   }
     
      if(c_read=='a')
        {
          ///lcd.clear();
         // wendu();
         wenflag=!wenflag;
         if(wenflag)
         Serial.println("WENDU OPEN!");
         else
         Serial.println("WENDU CLOSE!");
        }else if(c_read=='b')
        {
            // lcd.clear();
          shiflag=!shiflag;
          if(shiflag)
          Serial.println("SHIDU OPEN!");
          else
          Serial.println("SHIDU CLOSE!");
        }
        else if(c_read=='c')
        {
          ledflag=!ledflag;
           if(ledflag==true)
          {
            digitalWrite(buzzer,HIGH); //继电器导通； 
            Serial.println("LED OPEN!");
          }
          else
          {
            digitalWrite(buzzer,LOW);
            Serial.println("LED CLOSE!");
          }
          
//          unsigned char i,j;//定义变量
//         
//          for(i=0;i<80;i++)//辒出一个频率的声音
//          { 
//          digitalWrite(buzzer,HIGH);//发声音
//          delay(1);//延时1ms 
//          digitalWrite(buzzer,LOW);//不发声音
//          delay(1);//延时ms 
//          } 
//          for(i=0;i<100;i++)//辒出另一个频率癿声音 
//          { 
//          digitalWrite(buzzer,HIGH);//发声音
//          delay(2);//延时2ms 
//          digitalWrite(buzzer,LOW);//不发声音
//          delay(2);//延时2ms 
//          } 
//          Serial.print("%");
//          Serial.print("Hello World!");
//          Serial.print("@");
        }
        else 
        {
       //   Serial.print("C");
        }
       // Serial.print(c_read);
       start_test();
       lcd.clear();
       
     if(wenflag)
     {
         wendu();
     }
      if(shiflag)
     {
         shidu();
     }
     delay(500);
}
