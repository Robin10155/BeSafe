/*
 * cipher.c
 *
 *  Created on: 25-May-2016
 *      Author: robin10155
 */
#include <stdint.h>
#include "cipher.h"
// encryption routine
void encrypt (uint16_t* v, uint16_t* k) {
	uint16_t v0=v[0], v1=v[1], sum=0, i;
	uint16_t delta=0x9e37;
	uint16_t k0=k[0], k1=k[1], k2=k[2], k3=k[3];
	for (i=0; i < 16; i++) {
		sum += delta;
		v0 += ((v1<<4) + k0) ^ (v1 + sum) ^ ((v1>>5) + k1);
		v1 += ((v0<<4) + k2) ^ (v0 + sum) ^ ((v0>>5) + k3);
	}
	v[0]=v0; v[1]=v1;
}

// decryption routine
void decrypt (uint16_t* v, uint16_t* k) {
	uint16_t v0=v[0], v1=v[1], sum=0xE370, i;
	uint16_t delta=0x9e37;
	uint16_t k0=k[0], k1=k[1], k2=k[2], k3=k[3];
	for (i=0; i<16; i++) {
		v1 -= ((v0<<4) + k2) ^ (v0 + sum) ^ ((v0>>5) + k3);
		v0 -= ((v1<<4) + k0) ^ (v1 + sum) ^ ((v1>>5) + k1);
		sum -= delta;
	}
	v[0]=v0; v[1]=v1;
}



