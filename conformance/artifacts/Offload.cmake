cmake_minimum_required(VERSION 3.25)

# Prints the key of the exported llvm-offload archive, or exports the llvm-offload stage image to an archive.
#
#   cmake -P Offload.cmake
#   cmake -DMODE=export -DCONTAINER_EXECUTABLE=<executable> -DIMAGE=<tag> -DOUTPUT=<archive> -P Offload.cmake

get_filename_component(CONFORMANCE_SOURCE_DIRECTORY "${CMAKE_CURRENT_LIST_DIR}/.." ABSOLUTE)
include("${CMAKE_CURRENT_LIST_DIR}/Toolchains.cmake")

if(MODE STREQUAL "export")
  set(export "conformance-llvm-offload-export")
  execute_process(COMMAND "${CONTAINER_EXECUTABLE}" create --replace --name "${export}" "${IMAGE}" true
    COMMAND_ERROR_IS_FATAL ANY)
  get_filename_component(output_directory "${OUTPUT}" DIRECTORY)
  file(MAKE_DIRECTORY "${output_directory}")
  execute_process(
    COMMAND "${CONTAINER_EXECUTABLE}" cp "${export}:/llvm-offload" -
    COMMAND zstd -T0 -q -12 --long=30 -f -o "${OUTPUT}.partial"
    COMMAND_ERROR_IS_FATAL ANY)
  execute_process(COMMAND "${CONTAINER_EXECUTABLE}" rm --force "${export}" COMMAND_ERROR_IS_FATAL ANY)
  file(RENAME "${OUTPUT}.partial" "${OUTPUT}")
else()
  conformance_offload_key(key)
  execute_process(COMMAND "${CMAKE_COMMAND}" -E echo "${key}")
endif()
