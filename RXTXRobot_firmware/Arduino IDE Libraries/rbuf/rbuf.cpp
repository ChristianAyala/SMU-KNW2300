//
// simple ring buffer class
//
#include "rbuf.h"

//
// Constructor/Destructor
//
rbuf::rbuf()
{
    rbuf::empty();
}
rbuf::~rbuf()
{
}

//
// Empty ring buffer
//
void rbuf::empty()
{
    m_pIn = &m_Buffer[0];
    m_pOut = &m_Buffer[0];
    m_Length = 0;
}

//
// Return ring buffer length
//
uint16_t rbuf::len()
{
    return m_Length;
}

//
// Put data into buffer (immediate, not via pointer)
// return: 1=success, 0=fail
//
uint8_t rbuf::putI(uint8_t Data)
{
    if(m_Length == RBUF_SIZE) return 0;

    *m_pIn++ = Data;

    // check for wrap
    if(m_pIn == &m_Buffer[RBUF_SIZE])
        m_pIn = &m_Buffer[0];

    // increment length
    m_Length++;
    return 1;
}

//
// Put data into buffer (value to ASCII HEX text conversion)
// return: 1=success, 0=fail
//
uint8_t rbuf::putAHex(uint8_t Data)
{
	uint8_t nh, nl;
	
	if(m_Length >= RBUF_SIZE-1) return 0;

	nh = (Data >> 4) & 0xF;
	nl = Data & 0xF;
	
	nh += (nh < 10) ? 0x30 : 0x37;
	nl += (nl < 10) ? 0x30 : 0x37;
	
	if(rbuf::putI(nh))
		if(rbuf::putI(nl))
			return 1;

	return 0;
}

//
// Put data into buffer
// return: 1=success, 0=fail
//
uint8_t rbuf::put(uint8_t* pData)
{
    return rbuf::putI(*pData);
}

//
// Get data from buffer
// return: 1=success, 0=fail
//
uint8_t rbuf::get(uint8_t* pData)
{
    if(m_Length == 0) return 0;

    *pData = *m_pOut++;

    // check for wrap
    if(m_pOut == &m_Buffer[RBUF_SIZE])
        m_pOut = &m_Buffer[0];

    // decrement length
    m_Length--;
    return 1;
}

//
// Get data from buffer (immediate, not via pointer)
// return: data (if buffer not empty), 0 (if buffer empty)
//
uint8_t rbuf::getI()
{
	uint8_t Data;
	
	if(rbuf::get(&Data))
		return Data;
	
	return 0;
}

//
// Put 16bit word into buffer (immediate, not via pointer)
// return: 1=success, 0=fail
//
uint8_t rbuf::putWordI(uint16_t Data)
{
    if(m_Length >= RBUF_SIZE-1) return 0;

    if(rbuf::putI((uint8_t)(Data & 0xFF)))
        if(rbuf::putI((uint8_t)((Data >> 8) & 0xFF)))
            return 1;

    return 0;
}

//
// Put 16bit word into buffer
// return: 1=success, 0=fail
//
uint8_t rbuf::putWord(uint16_t* pData)
{
    if(m_Length >= RBUF_SIZE-1) return 0;

    if(rbuf::putI(*(uint8_t*)pData))
        if(rbuf::putI(*(((uint8_t*)pData)+1)))
            return 1;

    return 0;
}

//
// Get 16bit word from buffer
// return: 1=success, 0=fail
//
uint8_t rbuf::getWord(uint16_t* pData)
{
    if(m_Length <= 1) return 0;

    if(rbuf::get((uint8_t*)pData))
        if(rbuf::get(((uint8_t*)pData)+1))
            return 1;

    return 0;
}

//
// Put block of data into buffer
// return: 1=success, 0=fail
//
uint8_t rbuf::putBlock(const void* vpData, uint16_t Length)
{
    uint8_t* pData = (uint8_t*)vpData;

    // check if buffer would overflow with added data
    if(Length > (RBUF_SIZE - m_Length)) return 0;

    for(; Length; Length--)
    {
        m_Length++;
        *m_pIn++ = *pData++;
        // check for wrap
        if(m_pIn == &m_Buffer[RBUF_SIZE])
            m_pIn = &m_Buffer[0];
    }

    return 1;
}

//
// Get block of data from buffer
// return: 1=success, 0=fail
//
uint8_t rbuf::getBlock(void* vpData, uint16_t Length)
{
    uint8_t* pData = (uint8_t*)vpData;

    // check if buffer would underflow with data removed
    if(m_Length < Length) return 0;

    for(; Length; Length--)
    {
        *pData++ = *m_pOut++;
        m_Length--;
        // check for wrap
        if(m_pOut == &m_Buffer[RBUF_SIZE])
            m_pOut = &m_Buffer[0];
    }
    return 1;
}
