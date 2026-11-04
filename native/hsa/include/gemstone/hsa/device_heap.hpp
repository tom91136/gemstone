#pragma once

#include <cstddef>

namespace gemstone::hsa {

/** The size of the device's malloc heap. It is set before the first dispatch reserves the heap, which rounds it up to
 * 2 MiB; afterwards a new value is only reported.
 */
size_t deviceHeapLimit();
void setDeviceHeapLimit(size_t limit);

} // namespace gemstone::hsa
