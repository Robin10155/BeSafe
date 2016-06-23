#include "buffer.h"

void buffer_init(app_fifo_t *handle){
	app_fifo_init(handle,handle->p_buf,handle->buf_size_mask);
	buffer_flush(handle);
	uint32_t data;
	buffer_get(handle,&data);
	buffer_get(handle,&data);
}

uint32_t  buffer_flush(app_fifo_t * handle){
	return app_fifo_flush(handle);
}

uint32_t  buffer_get(app_fifo_t * handle,uint32_t * ret){
	uint32_t data=0;
	uint8_t d;
	uint32_t error;
	error=app_fifo_get(handle,&d);
	data=(data<<8)|d;
	error=app_fifo_get(handle,&d);
	data=(data<<8)|d;
	error=app_fifo_get(handle,&d);
	data=(data<<8)|d;
	error=app_fifo_get(handle,&d);
	data=(data<<8)|d;
	*ret=data;
	return error;
}

uint32_t  buffer_put(app_fifo_t * handle,uint32_t data){
	uint32_t error;
	error=app_fifo_put(handle,((data>>24)&0xFF));
	data=data<<8;
	error=app_fifo_put(handle,((data>>24)&0xFF));
	data=data<<8;
	error=app_fifo_put(handle,((data>>24)&0xFF));
	data=data<<8;
	error=app_fifo_put(handle,((data>>24)&0xFF));
	data=data<<8;
	return error;
}
