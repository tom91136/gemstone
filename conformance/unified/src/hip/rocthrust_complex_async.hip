#include "test_support.hpp"

#define UNIFIED_API_ROCTHRUST 1
#include "backend_thrust.hpp"
#undef UNIFIED_API_ROCTHRUST

#include "cases/rocthrust_complex_async.inl"
