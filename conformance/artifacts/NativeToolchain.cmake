cmake_minimum_required(VERSION 3.25)

# Prints the tag of the native toolchain image.
#
#   cmake -P NativeToolchain.cmake

get_filename_component(CONFORMANCE_SOURCE_DIRECTORY "${CMAKE_CURRENT_LIST_DIR}/.." ABSOLUTE)
include("${CMAKE_CURRENT_LIST_DIR}/Toolchains.cmake")

container_image_tag(tag ${conformance_native_toolchain_image})
execute_process(COMMAND "${CMAKE_COMMAND}" -E echo "${tag}")
