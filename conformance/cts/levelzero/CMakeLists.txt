# The upstream suite is Google Test based. Build only the core executables
# whose short cases cover driver objects, memory, command submission and SPIR-V.
set(levelzero_cts_programs
  test_driver test_device test_context test_cmdqueue test_event test_fence
  test_memory test_module test_copy)

conformance_fetch_url(conformance_levelzero_cts levelzero_cts_source)
conformance_fetch_url(conformance_levelzero_gtest levelzero_gtest_source)

set(staged_source "${CMAKE_CURRENT_BINARY_DIR}/source")
file(REMOVE_RECURSE "${staged_source}")
file(COPY "${levelzero_cts_source}/" DESTINATION "${staged_source}")
file(MAKE_DIRECTORY "${staged_source}/third_party/googletest")
file(COPY "${levelzero_gtest_source}/" DESTINATION "${staged_source}/third_party/googletest")

# This upstream revision requires static Boost 1.92, while supported toolchains
# ship a shared Boost 1.90. Neither choice affects the Level Zero API coverage.
set(upstream_cmake "${staged_source}/CMakeLists.txt")
file(READ "${upstream_cmake}" upstream_content)
foreach(patch IN ITEMS
    "set(Boost_USE_STATIC_LIBS ON)|set(Boost_USE_STATIC_LIBS OFF)"
    "find_package(Boost 1.92|find_package(Boost 1.90")
  string(REPLACE "|" ";" pair "${patch}")
  list(GET pair 0 before)
  list(GET pair 1 after)
  string(FIND "${upstream_content}" "${before}" at)
  if(at EQUAL -1)
    message(FATAL_ERROR "Pinned Level Zero CTS integration point changed: ${before}")
  endif()
  string(REPLACE "${before}" "${after}" upstream_content "${upstream_content}")
endforeach()
file(WRITE "${upstream_cmake}" "${upstream_content}")

# Upstream configures every core subtree even when only a few executables are
# built. Some unrelated subtrees fetch Vulkan headers at configure time.
set(core_cmake "${staged_source}/conformance_tests/core/CMakeLists.txt")
file(READ "${core_cmake}" core_content)
foreach(program IN LISTS levelzero_cts_programs)
  string(FIND "${core_content}" "add_subdirectory(${program})" at)
  if(at EQUAL -1)
    message(FATAL_ERROR "Pinned Level Zero CTS core integration point changed: ${program}")
  endif()
endforeach()
string(REGEX MATCHALL "add_subdirectory\\(test_[A-Za-z0-9_]+\\)" core_subdirectories "${core_content}")
foreach(line IN LISTS core_subdirectories)
  string(REGEX REPLACE "^add_subdirectory\\(([^)]+)\\)$" "\\1" program "${line}")
  if(NOT program IN_LIST levelzero_cts_programs)
    string(REPLACE "${line}" "" core_content "${core_content}")
  endif()
endforeach()
file(WRITE "${core_cmake}" "${core_content}")

# The pinned CTS gives a 16-element buffer to module_add_two_arrays but
# launches 16 groups of 16 work items. Correct the group count so its multiple
# indirect-launch assertion does not depend on out-of-bounds device writes.
set(module_cpp "${staged_source}/conformance_tests/core/test_module/src/test_module.cpp")
file(READ "${module_cpp}" module_content)
set(invalid_group_count "mult_th_group_dim.groupCountX = 16;")
string(FIND "${module_content}" "${invalid_group_count}" group_count_at)
if(group_count_at EQUAL -1)
  message(FATAL_ERROR "Pinned Level Zero CTS indirect-launch integration point changed")
endif()
string(REPLACE "${invalid_group_count}" "mult_th_group_dim.groupCountX = 1;"
  module_content "${module_content}")
file(WRITE "${module_cpp}" "${module_content}")

# The sampler guard accepts UNSUPPORTED_FEATURE and then uses an uninitialized image module.
# Query capability instead; this also avoids leaking the guard's temporary sampler.
set(args_cpp "${staged_source}/conformance_tests/core/test_module/src/test_kernel_args.cpp")
file(READ "${args_cpp}" args_content)
set(sampler_guard [=[bool sampler_support() {
  ze_sampler_desc_t descriptor = {};
  descriptor.stype = ZE_STRUCTURE_TYPE_SAMPLER_DESC;
  ze_sampler_handle_t sampler = nullptr;
  if (zeSamplerCreate(lzt::get_default_context(),
                      lzt::zeDevice::get_instance()->get_device(), &descriptor,
                      &sampler) == ZE_RESULT_ERROR_UNINITIALIZED) {
    return false;
  } else {
    return true;
  }
}]=])
set(sampler_capability [=[bool sampler_support() {
  ze_device_image_properties_t properties{};
  properties.stype = ZE_STRUCTURE_TYPE_IMAGE_PROPERTIES;
  return zeDeviceGetImageProperties(lzt::zeDevice::get_instance()->get_device(), &properties) == ZE_RESULT_SUCCESS
      && properties.maxSamplers > 0;
}]=])
string(FIND "${args_content}" "${sampler_guard}" sampler_at)
if(sampler_at EQUAL -1)
  message(FATAL_ERROR "Pinned Level Zero CTS sampler guard integration point changed")
endif()
string(REPLACE "${sampler_guard}" "${sampler_capability}" args_content "${args_content}")
file(WRITE "${args_cpp}" "${args_content}")

# Upstream initializes a device before Google Test can list cases. Artifacts
# must expose their inventory during relocation checks without a GPU present.
foreach(program IN LISTS levelzero_cts_programs)
  set(main_cpp "${staged_source}/conformance_tests/core/${program}/src/main.cpp")
  file(READ "${main_cpp}" main_content)
  set(init_line "::testing::InitGoogleMock(&argc, argv);")
  string(FIND "${main_content}" "${init_line}" at)
  if(at EQUAL -1)
    message(FATAL_ERROR "Pinned Level Zero CTS inventory integration point changed: ${program}")
  endif()
  string(REPLACE "${init_line}" "${init_line}\n  if (GTEST_FLAG_GET(list_tests)) return RUN_ALL_TESTS();"
    main_content "${main_content}")
  file(WRITE "${main_cpp}" "${main_content}")
endforeach()

set(GROUP "/conformance_tests/core")
set(GTEST_ROOT "${staged_source}/third_party/googletest")
set(LevelZero_INCLUDE_DIR "${conformance_levelzero_include}")
set(LevelZero_LIBRARY "${CONFORMANCE_LEVELZERO_LOADER}")
add_subdirectory("${staged_source}" build EXCLUDE_FROM_ALL)

# Device enumeration and affinity tests spawn these upstream helpers.
set(levelzero_device_helpers
  test_affinity_helper test_pci_device_order_helper test_device_hierarchy_helper)
install(TARGETS ${levelzero_device_helpers} RUNTIME DESTINATION device COMPONENT architecture)

foreach(program IN LISTS levelzero_cts_programs)
  set_target_properties("${program}" PROPERTIES OUTPUT_NAME "levelzero_cts_${program}")
  conformance_register_test_program("${program}" ${CONFORMANCE_TIMEOUT_CTS} gtest)
  # The upstream tests open SPIR-V files relative to their working directory.
  file(GLOB kernels "${staged_source}/conformance_tests/core/${program}/kernels/*.spv")
  if(kernels)
    install(FILES ${kernels} DESTINATION . COMPONENT architecture)
  endif()
endforeach()
install(FILES "${staged_source}/mediadata/internal/png/test_input.png"
  DESTINATION . COMPONENT architecture OPTIONAL)
install(FILES "${staged_source}/LICENSE"
  DESTINATION licenses RENAME Level-Zero-Tests.txt COMPONENT support)
install(FILES "${staged_source}/third_party/googletest/LICENSE"
  DESTINATION licenses RENAME GoogleTest.txt COMPONENT support)
add_custom_target(conformance_cts_artifact DEPENDS ${levelzero_cts_programs} ${levelzero_device_helpers})
