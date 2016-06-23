#ifndef BLE_GATEWAY_H__
#define BLE_GATEWAY_H__

#include "ble.h"
#include "ble_srv_common.h"
#include <stdint.h>
#include <stdbool.h>

#define BLE_UUID_GATEWAY_SERVICE 0x0001                      /**< The UUID of the Nordic GATEWAY service */
#define BLE_GATEWAY_MAX_DATA_LEN (GATT_MTU_SIZE_DEFAULT - 3) /**< Maximum length of data (in bytes) that can be transmitted to the peer by the Nordic GATEWAY service module. */

/* Forward declaration of the ble_gateway_t type. */
typedef struct ble_gateway_s ble_gateway_t;

/**@brief Nordic GATEWAY Service event handler type. */
typedef void (*ble_gateway_data_handler_t) (ble_gateway_t * p_gateway, uint8_t * p_data, uint16_t length);

/**@brief Nordic GATEWAY Service initialization structure.
 *
 * @details This structure contains the initialization information for the service. The application
 * must fill this structure and pass it to the service using the @ref ble_gateway_init
 *          function.
 */
typedef struct
{
    ble_gateway_data_handler_t data_handler; /**< Event handler to be called for handling received data. */
} ble_gateway_init_t;

/**@brief Nordic GATEWAY Service structure.
 *
 * @details This structure contains status information related to the service.
 */
struct ble_gateway_s
{
    uint8_t                  uuid_type;               /**< UUID type for Nordic GATEWAY Service Base UUID. */
    uint16_t                 service_handle;          /**< Handle of Nordic GATEWAY Service (as provided by the S110 SoftDevice). */
    ble_gatts_char_handles_t conn_id_handles;         /**< Handles related to the connectionId characteristic (as provided by the S110 SoftDevice). */
    uint16_t                 conn_handle;             /**< Handle of the current connection (as provided by the S110 SoftDevice). BLE_CONN_HANDLE_INVALID if not in a connection. */
    ble_gateway_data_handler_t   data_handler;        /**< Event handler to be called for handling received data. */
};

/**@brief Function for initializing the Nordic GATEWAY Service.
 *
 * @param[out] p_gateway      Nordic GATEWAY Service structure. This structure must be supplied
 *                        by the application. It is initialized by this function and will
 *                        later be used to identify this particular service instance.
 * @param[in] p_gateway_init  Information needed to initialize the service.
 *
 * @retval NRF_SUCCESS If the service was successfully initialized. Otherwise, an error code is returned.
 * @retval NRF_ERROR_NULL If either of the pointers p_gateway or p_gateway_init is NULL.
 */
uint32_t ble_gateway_init(ble_gateway_t * p_gateway, const ble_gateway_init_t * p_gateway_init);

/**@brief Function for handling the Nordic GATEWAY Service's BLE events.
 *
 * @details The Nordic GATEWAY Service expects the application to call this function each time an
 * event is received from the S110 SoftDevice. This function processes the event if it
 * is relevant and calls the Nordic GATEWAY Service event handler of the
 * application if necessary.
 *
 * @param[in] p_gateway       Nordic GATEWAY Service structure.
 * @param[in] p_ble_evt   Event received from the S110 SoftDevice.
 */
void ble_gateway_on_ble_evt(ble_gateway_t * p_gateway, ble_evt_t * p_ble_evt);

#endif // BLE_GATEWAY_H__

