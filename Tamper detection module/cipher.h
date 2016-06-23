/*
 * cipher.h
 *
 *  Created on: 25-May-2016
 *      Author: robin10155
 */

#ifndef CIPHER_H_
#define CIPHER_H_
#include <stdint.h>

// encryption routine
void encrypt (uint16_t* v, uint16_t* k);

// decryption routine
void decrypt (uint16_t* v, uint16_t* k);




#endif /* CIPHER_H_ */
