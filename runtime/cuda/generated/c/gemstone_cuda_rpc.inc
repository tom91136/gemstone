#ifndef GEMSTONE_CUDA_RPC_INC
#define GEMSTONE_CUDA_RPC_INC

#include <stdint.h>

#define GEMSTONE_CUDA_RPC_SCHEMA_FINGERPRINT "ec53bf9a873f247b54719e7328615bc3ff5fa3c0eb4321e934c923c0a2a6016a"
#define GEMSTONE_CUDA_RPC_METHODS(X) \
  X(__cudaGemstoneManagedRegister, UINT64_C(0xc6207aea8d73f5ba), UINT64_C(0x2db4cd6e3a364b1a)) \
  X(__cudaGemstoneManagedUnregister, UINT64_C(0x93e5439ad242c3c0), UINT64_C(0x8e20904fec96004c)) \
  X(cuCtxCreate_v2, UINT64_C(0xe6eb5473c0a097e3), UINT64_C(0xa17c8d63cec75a44)) \
  X(cuCtxDestroy_v2, UINT64_C(0xb574aacc9b35ee6a), UINT64_C(0x8135b883071cc261)) \
  X(cuCtxSynchronize, UINT64_C(0xe21f80c194a589d4), UINT64_C(0x4af681a7ef75bb10)) \
  X(cuDeviceGet, UINT64_C(0xc91170009b2ba6c9), UINT64_C(0x263d0d32e04e06a7)) \
  X(cuDeviceGetCount, UINT64_C(0x831f6fffdf16f0d4), UINT64_C(0x3c498239d16d9867)) \
  X(cuDriverGetVersion, UINT64_C(0xe584ec56943a425d), UINT64_C(0x3c512e60803234f9)) \
  X(cuInit, UINT64_C(0x3c1688958f08660e), UINT64_C(0x1c2a40d0799c268c)) \
  X(cuLaunchHostFunc, UINT64_C(0xced40f65d252b18e), UINT64_C(0xdc2b29f9f2e965b6)) \
  X(cuLaunchKernel, UINT64_C(0x0541d9142559d437), UINT64_C(0x4cd345cbce10e2df)) \
  X(cuMemAlloc_v2, UINT64_C(0x7077a5441f532f0e), UINT64_C(0x677e154cf996113d)) \
  X(cuMemFree_v2, UINT64_C(0xb5fdc569871ec54e), UINT64_C(0x418d186e2d8efa3a)) \
  X(cuMemcpyDtoD_v2, UINT64_C(0xe27c11476bbea751), UINT64_C(0x681167b2aac69936)) \
  X(cuMemcpyDtoH_v2, UINT64_C(0x0cc3708077b8e76a), UINT64_C(0xaa0bf9a6432ca141)) \
  X(cuMemcpyHtoD_v2, UINT64_C(0x9a8f59036790ef69), UINT64_C(0x4808ed612739b8fa)) \
  X(cuMemsetD8_v2, UINT64_C(0x2c1383458eec311a), UINT64_C(0x73e621c93fd80644)) \
  X(cuModuleGetFunction, UINT64_C(0x4b6b65a1226b0723), UINT64_C(0xc3ac0d7e32b1d4b5)) \
  X(cuModuleLoadData, UINT64_C(0xfef489c6c628b458), UINT64_C(0x480e5cb6fe964d3f)) \
  X(cuModuleUnload, UINT64_C(0xb94c365c6c5c3902), UINT64_C(0x2a1466600700709a)) \
  X(cuStreamAddCallback, UINT64_C(0x589d21fb33f2aca5), UINT64_C(0xbe72b9cfe9179b7a)) \
  X(cudaFree, UINT64_C(0xc9831c51cb850c1e), UINT64_C(0x0300fb6b734556b4)) \
  X(cudaGetDeviceCount, UINT64_C(0x29770d4f155d9a98), UINT64_C(0xccec9231bf109bef)) \
  X(cudaMalloc, UINT64_C(0x36207ff276ba04db), UINT64_C(0x8ed2474e0fef125d))

#endif
