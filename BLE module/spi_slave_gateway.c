/* Copyright (c) 2014 Nordic Semiconductor. All Rights Reserved.
 *
 * The information contained herein is property of Nordic Semiconductor ASA.
 * Terms and conditions of usage are described in detail in NORDIC
 * SEMICONDUCTOR STANDARD SOFTWARE LICENSE AGREEMENT.
 *
 * Licensees are granted free, non-transferable use of the information. NO
 * WARRANTY of ANY KIND is provided. This heading must NOT be removed from
 * the file.
 *
 */
 
#include "spi_slave_gateway.h"
#include "spi_slave.h"
#include "app_error.h"
#include "bsp.h"
#include <stdlib.h>

#define DEF_CHARACTER 0xAAu             /**< SPI default character. Character clocked out in case of an ignored transaction. */      
#define ORC_CHARACTER 0x55u             /**< SPI over-read character. Character clocked out after an over-read of the transmit buffer. */      


static spi_slave_get_data slave_get_data=NULL;


/**@brief Function for SPI slave event callback.
 *
 * Upon receiving an SPI transaction complete event, LED1 will blink and the buffers will be set.
 *
 * @param[in] event SPI slave driver event.
 */
static void spi_slave_event_handle(spi_slave_evt_t event)
{
    uint32_t err_code;
    
    if (event.evt_type == SPI_SLAVE_XFER_DONE)
    {
        err_code = bsp_indication_set(BSP_INDICATE_RCV_OK);
        APP_ERROR_CHECK(err_code);
        if(slave_get_data!=NULL)
					err_code=slave_get_data(1);
        APP_ERROR_CHECK(err_code);          
    }
}

/**@brief Function for initializing SPI slave.
 *
 *  Function configures a SPI slave and sets buffers.
 *
 * @retval NRF_SUCCESS  Initialization successful.
 */
uint32_t spi_slave_gateway_init(spi_slave_get_data get_data)
{
    uint32_t           err_code;
    spi_slave_config_t spi_slave_config;
        
    err_code = spi_slave_evt_handler_register(spi_slave_event_handle);
    APP_ERROR_CHECK(err_code);    

    spi_slave_config.pin_miso         = SPIS_MISO_PIN;
    spi_slave_config.pin_mosi         = SPIS_MOSI_PIN;
    spi_slave_config.pin_sck          = SPIS_SCK_PIN;
    spi_slave_config.pin_csn          = SPIS_CSN_PIN;
    spi_slave_config.mode             = SPI_MODE_0;
    spi_slave_config.bit_order        = SPIM_MSB_FIRST;
    spi_slave_config.def_tx_character = DEF_CHARACTER;
    spi_slave_config.orc_tx_character = ORC_CHARACTER;
    
    err_code = spi_slave_init(&spi_slave_config);
    APP_ERROR_CHECK(err_code);
		slave_get_data=get_data;
		slave_get_data(1);
    
    
    APP_ERROR_CHECK(err_code);            

    return NRF_SUCCESS;
}
