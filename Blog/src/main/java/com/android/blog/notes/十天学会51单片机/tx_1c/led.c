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


unsigned int index = 0;
unsigned int toggle = 1;

void once_led(){

	if(index>=8){
		index = 0;
		if(toggle==1)toggle = 0;
		else toggle = 1;
	}

	beep = toggle;

	switch(index){
		case 0:led1 = toggle;
		break;
		case 1:led2 = toggle;
		break;
		case 2:led3 = toggle;
		break;
		case 3:led3 = toggle;
		break;
		case 4:led4 = toggle;
		break;
		case 5:led5 = toggle;
		break;
		case 6:led6 = toggle;
		break;
		case 7:led7 = toggle;
		break;
		case 8:led8 = toggle;
		break;
	}
	index++;

}
