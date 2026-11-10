get_filename_component(REPOSITORY_DIRECTORY "${CMAKE_CURRENT_LIST_DIR}/../.." ABSOLUTE)
set(CONFORMANCE_ARTIFACT_PROJECT "${REPOSITORY_DIRECTORY}/conformance/artifacts" CACHE PATH
  "Independent conformance artifact build entry point")
set(CONTAINER_EXECUTABLE podman CACHE STRING "OCI container executable")
set(ROCM_VERSION 7.1.1 CACHE STRING "ROCm toolchain version")
set(GEMSTONE_PREBUILT_MEMORY_FIXTURES "" CACHE PATH
  "Directory of memory fixtures from the gemstone_memory_fixtures target, used instead of the container recipe")
set(GEMSTONE_MEMORY_FIXTURE_DIRECTORY "${CMAKE_BINARY_DIR}/rpc/memory-fixtures")
set_property(GLOBAL APPEND PROPERTY JOB_POOLS "container_build=1")
add_custom_target(gemstone_memory_fixtures)

function(gemstone_memory_image backend architecture)
  if(TARGET gemstone_memory_${backend}_image)
    return()
  endif()
  set(build "${CMAKE_BINARY_DIR}/artifact-builds/memory-${backend}")
  if(backend STREQUAL "cuda")
    set(outputs)
    foreach(name IN ITEMS kernel exchange atomic fence contended)
      list(APPEND outputs "${GEMSTONE_MEMORY_FIXTURE_DIRECTORY}/${name}.ptx")
    endforeach()
  elseif(backend STREQUAL "hsa")
    set(outputs "${GEMSTONE_MEMORY_FIXTURE_DIRECTORY}/kernel.hsaco")
  else()
    set(outputs "${GEMSTONE_MEMORY_FIXTURE_DIRECTORY}/kernel.spv")
  endif()
  if(GEMSTONE_PREBUILT_MEMORY_FIXTURES)
    foreach(output IN LISTS outputs)
      get_filename_component(name "${output}" NAME)
      if(NOT EXISTS "${GEMSTONE_PREBUILT_MEMORY_FIXTURES}/${name}")
        message(FATAL_ERROR "GEMSTONE_PREBUILT_MEMORY_FIXTURES lacks ${name} for ${backend}")
      endif()
      configure_file("${GEMSTONE_PREBUILT_MEMORY_FIXTURES}/${name}" "${output}" COPYONLY)
    endforeach()
    add_custom_target(gemstone_memory_${backend}_image)
    return()
  endif()
  add_custom_target(gemstone_memory_${backend}_image
    COMMAND "${CMAKE_COMMAND}" -S "${CONFORMANCE_ARTIFACT_PROJECT}" -B "${build}" -G Ninja --log-level=WARNING
      -DCONFORMANCE_ARTIFACT_KIND=fixture -DCONFORMANCE_FIXTURE=memory
      "-DCONFORMANCE_BACKEND=${backend}" "-DCONFORMANCE_ARCHITECTURE=${architecture}"
      "-DCONFORMANCE_ARTIFACT_DIRECTORY=${GEMSTONE_MEMORY_FIXTURE_DIRECTORY}"
      "-DCONTAINER_EXECUTABLE=${CONTAINER_EXECUTABLE}" "-DROCM_VERSION=${ROCM_VERSION}"
    COMMAND "${CMAKE_COMMAND}" --build "${build}" --target artifacts --parallel 1 -- --quiet
    BYPRODUCTS ${outputs} JOB_POOL container_build VERBATIM)
  add_dependencies(gemstone_memory_fixtures gemstone_memory_${backend}_image)
endfunction()

set(GEMSTONE_MEMORY_APIS cuda hip hsa opencl levelzero)

function(gemstone_memory_image_for api target_output)
  if(api STREQUAL "cuda")
    gemstone_memory_image(cuda "")
    set(image gemstone_memory_cuda_image)
  elseif(api MATCHES "^(hip|hsa)$")
    gemstone_memory_image(hsa gfx900)
    set(image gemstone_memory_hsa_image)
  else()
    gemstone_memory_image(opencl "")
    set(image gemstone_memory_opencl_image)
  endif()
  set(${target_output} "${image}" PARENT_SCOPE)
endfunction()
