if(NOT DEFINED CUDA_INCLUDE_DIR OR NOT EXISTS "${CUDA_INCLUDE_DIR}/cuda.h")
  message(FATAL_ERROR "CUDA_INCLUDE_DIR must contain cuda.h")
endif()

file(STRINGS "${CUDA_INCLUDE_DIR}/cuda.h" CUDA_VERSION_DEFINITION
  REGEX "^#[ \t]*define[ \t]+CUDA_VERSION[ \t]+[0-9]+")
if(NOT CUDA_VERSION_DEFINITION MATCHES "CUDA_VERSION[ \t]+13030([ \t]|$)")
  message(FATAL_ERROR
    "Gemstone generated CUDA bindings require CUDA 13.3 headers (CUDA_VERSION=13030); "
    "selected ${CUDA_INCLUDE_DIR}/cuda.h: ${CUDA_VERSION_DEFINITION}")
endif()
