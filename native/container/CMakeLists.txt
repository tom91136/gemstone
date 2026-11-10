cmake_minimum_required(VERSION 3.25)
project(gemstone_native_container NONE)

# Host-side preparation for building native in the toolchain image: the image itself and the memory fixtures, which
# need container engines of their own and so cannot be built inside it.
get_filename_component(REPOSITORY_DIRECTORY "${CMAKE_CURRENT_SOURCE_DIR}/../.." ABSOLUTE)
set(CONFORMANCE_SOURCE_DIRECTORY "${REPOSITORY_DIRECTORY}/conformance")
include("${CONFORMANCE_SOURCE_DIRECTORY}/artifacts/Toolchains.cmake")
include("${CMAKE_CURRENT_SOURCE_DIR}/../cmake/MemoryFixtures.cmake")

container_image(tag stamp
  EXECUTABLE "${CONTAINER_EXECUTABLE}"
  CONTEXT "${CONFORMANCE_SOURCE_DIRECTORY}"
  JOB_POOL container_build
  ${conformance_native_toolchain_image})
add_custom_target(toolchain_image DEPENDS "${stamp}")
file(WRITE "${CMAKE_BINARY_DIR}/toolchain-image" "${tag}")

foreach(api IN LISTS GEMSTONE_MEMORY_APIS)
  gemstone_memory_image_for("${api}" unused_image)
endforeach()
