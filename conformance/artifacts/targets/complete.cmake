if(NOT DEFINED CONFORMANCE_BACKENDS)
  set(CONFORMANCE_BACKENDS cuda hip hsa opencl sycl levelzero vulkan)
endif()
if(NOT DEFINED CUDA_ARCHITECTURES)
  set(CUDA_ARCHITECTURES
    sm_35
    sm_50
    sm_60
    sm_70

    sm_75
    sm_80
    sm_86
    sm_87
    sm_89
    sm_90
    sm_100
    sm_103
    sm_120

    sm_90a
    sm_100a
    sm_100f
    sm_103a
    sm_103f
    sm_120a
    sm_120f
  )
endif()
if(NOT DEFINED AMDGPU_ARCHITECTURES)
  set(AMDGPU_ARCHITECTURES
    gfx803
    gfx900
    gfx906
    gfx90c

    gfx908
    gfx90a
    gfx942
    gfx950

    gfx1010
    gfx1011
    gfx1012
    gfx1013

    gfx1030
    gfx1031
    gfx1032
    gfx1033
    gfx1034
    gfx1035
    gfx1036

    gfx1100
    gfx1101
    gfx1102
    gfx1103
    gfx1150
    gfx1151

    gfx1200
    gfx1201
  )
endif()
