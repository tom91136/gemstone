# dEQP assumes it owns CMAKE_SOURCE_DIR and CMAKE_BINARY_DIR. Configure it as
# an independent project while retaining our pinned, offline dependency cache.
include(ExternalProject)
conformance_fetch_url(conformance_vulkan_cts vulkan_cts_source)
set(staged_source "${CMAKE_CURRENT_BINARY_DIR}/source")
file(MAKE_DIRECTORY "${staged_source}")
file(COPY "${vulkan_cts_source}/" DESTINATION "${staged_source}")
foreach(dependency IN ITEMS spirv_tools glslang spirv_headers docs amber jsoncpp)
  conformance_fetch_url("conformance_vulkan_${dependency}" dependency_source)
  if(dependency STREQUAL "docs")
    set(directory vulkan-docs)
  else()
    string(REPLACE "_" "-" directory "${dependency}")
  endif()
  file(MAKE_DIRECTORY "${staged_source}/external/${directory}/src")
  file(COPY "${dependency_source}/" DESTINATION "${staged_source}/external/${directory}/src")
  file(GLOB licenses LIST_DIRECTORIES FALSE "${dependency_source}/LICENSE*" "${dependency_source}/COPYING*")
  if(IS_DIRECTORY "${dependency_source}/LICENSES")
    install(DIRECTORY "${dependency_source}/LICENSES/" DESTINATION "licenses/vulkan/${dependency}" COMPONENT support)
  endif()
  install(FILES ${licenses} DESTINATION "licenses/vulkan/${dependency}" COMPONENT support)
endforeach()
set(cts_build "${CMAKE_CURRENT_BINARY_DIR}/build")
set(cts_program "${cts_build}/external/vulkancts/modules/vulkan/deqp-vk")
ExternalProject_Add(vulkan_cts_build
  SOURCE_DIR "${staged_source}"
  BINARY_DIR "${cts_build}"
  DOWNLOAD_COMMAND ""
  UPDATE_COMMAND ""
  CMAKE_ARGS -DCMAKE_BUILD_TYPE=Release -DDEQP_TARGET=vulkan_headless
    -DDEQP_DISABLE_VK_VIDEO_TESTS=ON -DBUILD_TESTING=OFF
    -DCMAKE_MAKE_PROGRAM=${CMAKE_MAKE_PROGRAM}
    -DCMAKE_C_COMPILER=${CMAKE_C_COMPILER} -DCMAKE_CXX_COMPILER=${CMAKE_CXX_COMPILER}
  BUILD_COMMAND "${CMAKE_COMMAND}" --build <BINARY_DIR> --target deqp-vk
  INSTALL_COMMAND ""
  BUILD_ALWAYS TRUE
  BUILD_BYPRODUCTS "${cts_program}")
# An imported executable participates in our manifest and runtime closure, but
# ExternalProject owns its compilation and installation.
add_executable(vulkan_cts IMPORTED GLOBAL)
set_target_properties(vulkan_cts PROPERTIES IMPORTED_LOCATION "${cts_program}" OUTPUT_NAME vulkan_cts)
add_dependencies(vulkan_cts vulkan_cts_build)
conformance_register_test_program(vulkan_cts ${CONFORMANCE_TIMEOUT_CTS} deqp)
install(PROGRAMS "${cts_program}" DESTINATION . RENAME vulkan_cts COMPONENT architecture)
install(DIRECTORY "${vulkan_cts_source}/external/vulkancts/data/"
  DESTINATION data/vulkan COMPONENT support)
install(DIRECTORY "${vulkan_cts_source}/external/vulkancts/mustpass/main/"
  DESTINATION data/vulkan/mustpass COMPONENT support)
install(FILES "${vulkan_cts_source}/LICENSE" DESTINATION licenses
  RENAME Vulkan-CTS.txt COMPONENT support)
add_custom_target(conformance_cts_artifact DEPENDS vulkan_cts_build)
