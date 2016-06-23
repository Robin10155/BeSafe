#include <msp430.h> 
#include <stdint.h>
#include "cipher.h"
/*
 * main.c
 */
#define LED1	0x00
#define LED2	0x06

#define BUTTON	0x03

#define Pgreen 0x03
#define Pblack  0x10
int blink=0;
void spi_init();
#define placeAddress 0xE000

uint8_t * ptr;
volatile const uint8_t emergency=0xFF;
volatile uint8_t lEmer;

void flash_write(uint8_t *addr, uint8_t value);
void flash_erase(uint8_t *addr);
uint8_t temp;
int main(void) {
	ptr=(uint8_t *)placeAddress;
    WDTCTL = WDTPW | WDTHOLD;	// Stop watchdog timer
//	//WDTCTL = WDT_MDLY_32; // Set Watchdog Timer interval to ~32ms
//
	DCOCTL = CALDCO_16MHZ;    // DCO frequency set to　16 MHz
	BCSCTL1 = CALBC1_16MHZ;   // DCO range set to 16 MHz
//	//IE1 |= WDTIE; // Enable WDT interrupt
//	//LED control
    P1DIR |= (1<<LED1);//|(1<<LED2);
    P1OUT &= ~((1<<LED1));//|(1<<LED2));
//
    //BUTTON control
    P1REN |= (1<<BUTTON);
    P1OUT |= (1<<BUTTON);
    //P1IES |= (1<<BUTTON);
    //P1IE |= (1<<BUTTON);
    spi_init();
    __enable_interrupt();
    //_BIS_SR(LPM3_bits + GIE); // Enter LPM3 w/interrupt
    lEmer=*ptr;
    int i;
    for(i=0;i<(2*60);i++)
    	__delay_cycles(16000000);
    while(1){
    	if((P1IN&(1<<BUTTON))==0){
    		flash_write(ptr,0);
    		//flash_erase(ptr);
    		lEmer=*ptr;
    		__delay_cycles(1000);
    	}else{

    	}
    	//P1OUT ^= ((1<<LED1)|(1<<LED2));
    	//__delay_cycles(50000000);
    }
}

void flash_write(uint8_t *addr, uint8_t value)
{
  _disable_interrupt();                             // Disable interrupts(IAR workbench).
  FCTL2 = FWKEY + FSSEL_1 + FN0;       // Clk = SMCLK/4
  FCTL3 = FWKEY;                       // Clear Lock bit
  FCTL1 = FWKEY + WRT;                 // Set WRT bit for write operation

  	  *addr=value;
//  if(option == WRITE_FROM_BUFFER)
////    for (i=0; i<64; i++)
////      *addr++ = data_buffer[i];         // copy value to flash
//
//  else if(option == WRITE_FROM_DUMP)
//    for (i=0; i<64; i++)
//      *addr++ = data_dump[i];           // copy value to flash

  FCTL1 = FWKEY;                        // Clear WRT bit
  FCTL3 = FWKEY + LOCK;                 // Set LOCK bit
  _enable_interrupt();
}

// __DINT() is in IAR workbench
void flash_erase(uint8_t *addr)
{
__disable_interrupt();                             // Disable interrupts. This is important, otherwise,
                                       // a flash operation in progress while interrupt may
                                       // crash the system.
  while(BUSY & FCTL3);                 // Check if Flash being used
  FCTL2 = FWKEY + FSSEL_1 + FN3;       // Clk = SMCLK/4
  FCTL1 = FWKEY + ERASE;               // Set Erase bit
  FCTL3 = FWKEY;                       // Clear Lock bit
  *addr = 0;                           // Dummy write to erase Flash segment
  while(BUSY & FCTL3);                 // Check if Flash being used
  FCTL1 = FWKEY;                       // Clear WRT bit
  FCTL3 = FWKEY + LOCK;                // Set LOCK bit
  __enable_interrupt();
}
//
void spi_init(){
//	UCA0CTL1 = UCSWRST;
//	UCA0CTL0 |= (UCMSB | UCSYNC);
//	UCA0CTL1 |= (UCSSEL_2);
//	UCA0BR0 |= (0x02);
//	UCA0BR1 = 0;
//	UCA0MCTL = 0;
//	UCA0CTL1 &= ~UCSWRST;

	UCB0CTL1 = UCSWRST;     // **Put state machine in reset**
	UCB0CTL0 = 0x00;
	UCB0CTL0 = (UCMODE_2) | (UCSYNC) | (UCMSB) | (UCCKPH);
	P1SEL = BIT6 | BIT7 | BIT4 | BIT5;
	P1SEL2 = BIT6 | BIT7 | BIT4 | BIT5;
	//P1DIR |= (BIT6);
	UCB0CTL1 &= ~UCSWRST;   // **Initialize USCI state machine**
	IE2 |= UCB0RXIE;        // Enable USCI0 RX interrupt

	UCB0TXBUF = 0x00;
}


//#pragma vector=WDT_VECTOR
//__interrupt void watchdog_timer(void)
//{
//	P1OUT &= ~((1<<LED1)|(1<<LED2));
//	_BIS_SR(LPM3_bits + GIE); // Enter LPM3 w/interrupt
//}

#pragma vector=PORT1_VECTOR
__interrupt void Port_1(void)
{
	//flash_erase(ptr);
    //_BIC_SR(LPM3_EXIT); // wake up from low power mode
    P1IFG =0;
}
uint16_t k[]={0xD2A7,0xFA9D,0xA887,0x293F};
uint16_t received=0;
uint16_t index=0;
uint8_t vTemp[4];
uint8_t vTemp2[4];
uint16_t v[2];
#pragma vector=USCIAB0RX_VECTOR
__interrupt void USCI0RX_ISR(void)
{
	//P1OUT ^= ((1<<LED1));//|(1<<LED2));
	uint8_t data = UCB0RXBUF;
	uint8_t sData=0;
	if(index<4)
		vTemp[index++]=data;
	if(received==3){
		v[0]=(vTemp[0])|(vTemp[1]<<8);
		v[1]=(vTemp[2])|(vTemp[3]<<8);
		encrypt(v,k);
		vTemp2[0]=v[0]&0xFF;
		vTemp2[1]=(v[0]>>8)&0xFF;
		vTemp2[2]=v[1]&0xFF;
		vTemp2[3]=(v[1]>>8)&0xFF;
	}
	if(received>=3&&received<7){
		sData=vTemp2[received-3];
	}
	if(*ptr!=0xFF)
		sData=0;
	received++;
	if(received==8){
		received=0;
		index=0;
	}
	UCB0TXBUF = sData;
} //RX interrupt


