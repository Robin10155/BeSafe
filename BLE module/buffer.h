
#ifndef BUFFER_H
#define BUFFER_H
#include <stdint.h>
#include "nrf.h"
#include "app_fifo.h"


#define BUFFER_DEFINE(NAME,SIZE)	uint8_t NAME##_buffer[SIZE*4]; \
																		app_fifo_t NAME##_handle={	\
																			NAME##_buffer,	\
																			SIZE,	\
																			0,	\
																			0	\
																		};	
																	
																	
#define BUFFER(NAME)		&NAME##_handle

//#define BUFFER_INIT(NAME)	app_fifo_init(&NAME##_handle,(uint8 *)&NAME##_buffer,sizeof(NAME##_buffer))	
																	
void buffer_init(app_fifo_t *handle);

uint32_t  buffer_flush(app_fifo_t * handle);

uint32_t  buffer_get(app_fifo_t * handle,uint32_t * ret);

uint32_t  buffer_put(app_fifo_t * handle,uint32_t data);
																	
#endif // BUFFER_H
