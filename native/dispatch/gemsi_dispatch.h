#ifndef GEMSI_DISPATCH_H
#define GEMSI_DISPATCH_H

#include "gemsi_protocol.h"

#ifdef __cplusplus
extern "C" {
#endif

int gemsi_dispatch_request(GemsiWireRequest *request, GemsiWireResponse *response);
void gemsi_dispatch_record_boundary(uint32_t opcode, int32_t status);

#ifdef __cplusplus
}
#endif

#endif
