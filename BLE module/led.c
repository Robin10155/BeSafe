#include "led.h"

void led_init(led_t led){
	nrf_gpio_cfg_output(led);
}

void led_set(led_t led,uint8_t value){
	if(value)
	{
		NRF_GPIO->OUTSET=(1<<led);
	}else{
		NRF_GPIO->OUTCLR=(1<<led);
	}
}

uint32_t led_get(led_t led){
	return (NRF_GPIO->OUT&(1<<led))?0:1;
}
