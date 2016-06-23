
#ifndef SPI_SLAVE_EXAMPLE_H__
#define SPI_SLAVE_EXAMPLE_H__

#include <stdint.h>
#include "spi_slave.h"

typedef uint32_t (*spi_slave_get_data)(uint8_t xfer_com);

/**@brief Function for initializing the SPI slave example.
 *
 * @retval NRF_SUCCESS  Operation success.
 */ 
uint32_t spi_slave_gateway_init(spi_slave_get_data get_data);

#endif // SPI_SLAVE_EXAMPLE_H__
