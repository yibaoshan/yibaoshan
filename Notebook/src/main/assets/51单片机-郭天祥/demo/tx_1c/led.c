#include <reg52.h>

sbit led1 = P1^0;
sbit led2 = P1^1;
sbit led3 = P1^2;
sbit led4 = P1^3;
sbit led5 = P1^4;
sbit led6 = P1^5;
sbit led7 = P1^6;
sbit led8 = P1^7;

sbit beep = P2^3;

void delay(){
	unsigned int cnt = 11000;
	while(cnt--);
}

void start(){

	unsigned int cnt;

	while(1){

		//0 = on £¬ 1 = off
		beep = 0;

		led1 = 0;

		delay();

		led1 = 1;
		led2 = 0;

		beep = 1;
		delay();

		led2 = 1;
		led3 = 0;
		beep = 0;
		delay();

		led3 = 1;
		led4 = 0;
		beep = 1;
		delay();

		led4 = 1;
		led5 = 0;

		delay();

		led5 = 1;
		led6 = 0;

		delay();

		led6 = 1;
		led7 = 0;

		delay();

		led7 = 1;
		led8 = 0;

		delay();

		led8 = 1;
		//delay();

	}
}