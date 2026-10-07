# ROCm still provides gfx803 code generation, but not the corresponding
# rocBLAS kernel library, so this target set covers only what remains viable.
if(NOT DEFINED CONFORMANCE_BACKENDS)
  set(CONFORMANCE_BACKENDS hip hsa)
endif()
if(NOT DEFINED AMDGPU_ARCHITECTURES)
  set(AMDGPU_ARCHITECTURES gfx803)
endif()
