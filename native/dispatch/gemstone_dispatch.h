#ifndef GEMSTONE_DISPATCH_H
#define GEMSTONE_DISPATCH_H

#include "gemstone_protocol.h"

#ifdef __cplusplus
extern "C" {
#endif

int gemstone_dispatch_request(GemstoneWireRequest *request, GemstoneWireResponse *response);
void gemstone_dispatch_record_boundary(uint32_t opcode, int32_t status);

#ifdef __cplusplus
}
#endif

#endif
