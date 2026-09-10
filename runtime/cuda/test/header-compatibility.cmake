# Configure-only compatibility regression; do not compile against the synthetic headers.
foreach(version IN ITEMS 12080 13020 13030 13040)
  set(directory "${TEST_DIRECTORY}/${version}")
  file(MAKE_DIRECTORY "${directory}/include")
  file(WRITE "${directory}/include/cuda.h" "#define CUDA_VERSION ${version}\n")
  execute_process(COMMAND "${CMAKE_COMMAND}" -S "${CUDA_SOURCE}" -B "${directory}/build"
    "-DCUDA_INCLUDE_DIR=${directory}/include" -DBUILD_TESTING=OFF
    RESULT_VARIABLE result OUTPUT_VARIABLE output ERROR_VARIABLE error)
  if(version EQUAL 13030)
    if(NOT result EQUAL 0)
      message(FATAL_ERROR "Pinned header profile was rejected: ${output}${error}")
    endif()
  elseif(result EQUAL 0 OR NOT "${output}${error}" MATCHES "require CUDA 13.3 headers")
    message(FATAL_ERROR "Incompatible header profile ${version} did not fail clearly: ${output}${error}")
  endif()
endforeach()
