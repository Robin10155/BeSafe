#ifndef LED_H
#define LED_H

#include "nrf_gpio.h"

typedef enum led_e led_t;

enum led_e{
	LED0=18,
	LED1=19,
	LED2=20,
	LED3=21,
	LED4=22
};


void led_init(led_t led);

void led_set(led_t led,uint8_t value);

uint32_t led_get(led_t led);

#endif //LED_H
