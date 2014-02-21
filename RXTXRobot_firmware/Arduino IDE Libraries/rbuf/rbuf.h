//
// simple ring buffer class
//
#ifndef RBUF_H
#define RBUF_H

#include <inttypes.h>

// if used for debug adjust buffer size depending on how verbose messages are and
// in/out rates - overflows are blocked prior to add
#define RBUF_SIZE 2048

class rbuf
{
public:
    rbuf();
    ~rbuf();

    // functions
    void     empty();
    uint16_t len();
    uint8_t  putI(uint8_t Data);
    uint8_t  putAHex(uint8_t Data);
    uint8_t  put(uint8_t* pData);
    uint8_t  get(uint8_t* pData);
    uint8_t  getI();
    uint8_t  putWordI(uint16_t Data);
    uint8_t  putWord(uint16_t* pData);
    uint8_t  getWord(uint16_t* pData);
    uint8_t  putBlock(const void* vpData, uint16_t Length);
    uint8_t  getBlock(void* vpData, uint16_t Length);

private:
    // variables
    volatile uint8_t  m_Buffer[RBUF_SIZE];
    volatile uint8_t* m_pIn;
    volatile uint8_t* m_pOut;
    volatile uint16_t m_Length;
};

#endif
