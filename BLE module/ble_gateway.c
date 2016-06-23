#include "ble_gateway.h"
#include <string.h>
#include "nordic_common.h"
#include "ble_hci.h"
#include "ble_srv_common.h"

#define BLE_UUID_GATEWAY_CONN_ID_CHARACTERISTIC 0x0002                      /**< The UUID of the conn_id Characteristic. */

#define BLE_GATEWAY_MAX_CONN_ID_CHAR_LEN        BLE_GATEWAY_MAX_DATA_LEN        /**< Maximum length of the TX Characteristic (in bytes). */

#define GATEWAY_BASE_UUID                  {{0x9E, 0xCA, 0xDC, 0x24, 0x0E, 0xE5, 0xA9, 0xE0, 0x93, 0xF3, 0xA3, 0xB5, 0x00, 0x00, 0x40, 0x6E}} /**< Used vendor specific UUID. */

/**@brief Function for handling the @ref BLE_GAP_EVT_CONNECTED event from the S110 SoftDevice.
 *
 * @param[in] p_gateway     Nordic GATEWAY Service structure.
 * @param[in] p_ble_evt Pointer to the event received from BLE stack.
 */
static void on_connect(ble_gateway_t * p_gateway, ble_evt_t * p_ble_evt)
{
    p_gateway->conn_handle = p_ble_evt->evt.gap_evt.conn_handle;
}


/**@brief Function for handling the @ref BLE_GAP_EVT_DISCONNECTED event from the S110 SoftDevice.
 *
 * @param[in] p_gateway     Nordic GATEWAY Service structure.
 * @param[in] p_ble_evt Pointer to the event received from BLE stack.
 */
static void on_disconnect(ble_gateway_t * p_gateway, ble_evt_t * p_ble_evt)
{
    UNUSED_PARAMETER(p_ble_evt);
    p_gateway->conn_handle = BLE_CONN_HANDLE_INVALID;
}


/**@brief Function for handling the @ref BLE_GATTS_EVT_WRITE event from the S110 SoftDevice.
 *
 * @param[in] p_gateway     Nordic GATEWAY Service structure.
 * @param[in] p_ble_evt Pointer to the event received from BLE stack.
 */
static void on_write(ble_gateway_t * p_gateway, ble_evt_t * p_ble_evt)
{
    ble_gatts_evt_write_t * p_evt_write = &p_ble_evt->evt.gatts_evt.params.write;

    if ((p_evt_write->handle == p_gateway->conn_id_handles.value_handle)
             &&
             (p_gateway->data_handler != NULL)
            )
    {
        p_gateway->data_handler(p_gateway, p_evt_write->data, p_evt_write->len);
			  sd_ble_gap_disconnect	(p_gateway->conn_handle,BLE_HCI_REMOTE_USER_TERMINATED_CONNECTION );		
    }
}


/**@brief Function for adding conn_id characteristic.
 *
 * @param[in] p_gateway       Nordic GATEWAY Service structure.
 * @param[in] p_gateway_init  Information needed to initialize the service.
 *
 * @return NRF_SUCCESS on success, otherwise an error code.
 */
static uint32_t conn_id_char_add(ble_gateway_t * p_gateway, const ble_gateway_init_t * p_gateway_init)
{
    ble_gatts_char_md_t char_md;
    ble_gatts_attr_t    attr_char_value;
    ble_uuid_t          ble_uuid;
    ble_gatts_attr_md_t attr_md;

    memset(&char_md, 0, sizeof(char_md));

    char_md.char_props.write         = 1;
    char_md.char_props.write_wo_resp = 1;
    char_md.p_char_user_desc         = NULL;
    char_md.p_char_pf                = NULL;
    char_md.p_user_desc_md           = NULL;
    char_md.p_cccd_md                = NULL;
    char_md.p_sccd_md                = NULL;

    ble_uuid.type = p_gateway->uuid_type;
    ble_uuid.uuid = BLE_UUID_GATEWAY_CONN_ID_CHARACTERISTIC;

    memset(&attr_md, 0, sizeof(attr_md));

    BLE_GAP_CONN_SEC_MODE_SET_OPEN(&attr_md.read_perm);
    BLE_GAP_CONN_SEC_MODE_SET_OPEN(&attr_md.write_perm);

    attr_md.vloc    = BLE_GATTS_VLOC_STACK;
    attr_md.rd_auth = 0;
    attr_md.wr_auth = 0;
    attr_md.vlen    = 1;

    memset(&attr_char_value, 0, sizeof(attr_char_value));

    attr_char_value.p_uuid    = &ble_uuid;
    attr_char_value.p_attr_md = &attr_md;
    attr_char_value.init_len  = 1;
    attr_char_value.init_offs = 0;
    attr_char_value.max_len   = BLE_GATEWAY_MAX_CONN_ID_CHAR_LEN;

    return sd_ble_gatts_characteristic_add(p_gateway->service_handle,
                                           &char_md,
                                           &attr_char_value,
                                           &p_gateway->conn_id_handles);
}


void ble_gateway_on_ble_evt(ble_gateway_t * p_gateway, ble_evt_t * p_ble_evt)
{
    if ((p_gateway == NULL) || (p_ble_evt == NULL))
    {
        return;
    }

    switch (p_ble_evt->header.evt_id)
    {
        case BLE_GAP_EVT_CONNECTED:
            on_connect(p_gateway, p_ble_evt);
            break;

        case BLE_GAP_EVT_DISCONNECTED:
            on_disconnect(p_gateway, p_ble_evt);
            break;

        case BLE_GATTS_EVT_WRITE:
            on_write(p_gateway, p_ble_evt);
            break;

        default:
            // No implementation needed.
            break;
    }
}


uint32_t ble_gateway_init(ble_gateway_t * p_gateway, const ble_gateway_init_t * p_gateway_init)
{
    uint32_t      err_code;
    ble_uuid_t    ble_uuid;
    ble_uuid128_t gateway_base_uuid = GATEWAY_BASE_UUID;

    if ((p_gateway == NULL) || (p_gateway_init == NULL))
    {
        return NRF_ERROR_NULL;
    }

    // Initialize the service structure.
    p_gateway->conn_handle             = BLE_CONN_HANDLE_INVALID;
    p_gateway->data_handler            = p_gateway_init->data_handler;

    /**@snippet [Adding proprietary Service to S110 SoftDevice] */
    // Add a custom base UUID.
    err_code = sd_ble_uuid_vs_add(&gateway_base_uuid, &p_gateway->uuid_type);
    if (err_code != NRF_SUCCESS)
    {
        return err_code;
    }

    ble_uuid.type = p_gateway->uuid_type;
    ble_uuid.uuid = BLE_UUID_GATEWAY_SERVICE;

    // Add the service.
    err_code = sd_ble_gatts_service_add(BLE_GATTS_SRVC_TYPE_PRIMARY,
                                        &ble_uuid,
                                        &p_gateway->service_handle);
    /**@snippet [Adding proprietary Service to S110 SoftDevice] */
    if (err_code != NRF_SUCCESS)
    {
        return err_code;
    }


    // Add the conn_id Characteristic.
    err_code = conn_id_char_add(p_gateway, p_gateway_init);
    if (err_code != NRF_SUCCESS)
    {
        return err_code;
    }

    return NRF_SUCCESS;
}
