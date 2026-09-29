# Single source of truth for conformance test program timeouts, in seconds.
# The emulator is far slower than hardware, so cubic GEMM, the step-heavy
# miniapps, and a few OpenCL CTS categories get longer budgets. The OpenCL CTS
# override is also applied per case by the isolated launcher.
set(CONFORMANCE_TIMEOUT_UNIFIED 600)
set(CONFORMANCE_TIMEOUT_MINIAPP 900)
set(CONFORMANCE_TIMEOUT_CTS 120)
set(CONFORMANCE_TIMEOUT_OPENCL_DEFAULT 120)
set(CONFORMANCE_TIMEOUT_OPENCL_MULTIPLES 600)
set(CONFORMANCE_TIMEOUT_OPENCL_THREAD_DIMENSIONS 1800)
set(CONFORMANCE_TIMEOUT_OPENCL_ISOLATED 600)
# The handler copy matrix passes alone in ~42s but exceeds 120s when sharing
# emulator workers with the other compact workloads.
set(CONFORMANCE_TIMEOUT_SYCL_HANDLER_ISOLATED 300)
