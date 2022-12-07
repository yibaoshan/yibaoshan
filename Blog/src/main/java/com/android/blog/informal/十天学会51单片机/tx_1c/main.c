#include <reg52.h>


void delay(){
	unsigned int cnt = 11000;
	while(cnt--);
}

void main(){
	while(1){
		once_led();
		once_digita();
		delay();
	}
}


