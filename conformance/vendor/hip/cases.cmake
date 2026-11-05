function(conformance_vendor_collect_executables directory output)
  get_property(targets DIRECTORY "${directory}" PROPERTY BUILDSYSTEM_TARGETS)
  set(result)
  foreach(target IN LISTS targets)
    get_target_property(type "${target}" TYPE)
    if(type STREQUAL "EXECUTABLE")
      list(APPEND result "${target}")
    endif()
  endforeach()
  get_property(subdirectories DIRECTORY "${directory}" PROPERTY SUBDIRECTORIES)
  foreach(subdirectory IN LISTS subdirectories)
    conformance_vendor_collect_executables("${subdirectory}" nested)
    list(APPEND result ${nested})
  endforeach()
  set(${output} "${result}" PARENT_SCOPE)
endfunction()

function(conformance_vendor_lookup table example output)
  set(value "")
  foreach(entry IN LISTS ${table})
    if(entry MATCHES "^${example}\\|(.*)$")
      set(value "${CMAKE_MATCH_1}")
    endif()
  endforeach()
  set(${output} "${value}" PARENT_SCOPE)
endfunction()

# example|label|arguments, an empty label keeps the plain row name
set(conformance_vendor_arguments
  "Applications/convolution||-x 128 -y 128 -i 1"
  "Applications/bitonic_sort||-l 10"
  "Applications/monte_carlo_pi||-s 65536"
  "HIP-Basic/matrix_multiplication||--A_rows 64 --A_cols 64 --B_cols 64"
  "Libraries/rocRAND/c_cpp_api/simple_distributions_cpp|uniform_int|--distribution uniform_int --size 10000"
  "Libraries/rocRAND/c_cpp_api/simple_distributions_cpp|uniform_real|--distribution uniform_real --size 10000"
  "Libraries/rocRAND/c_cpp_api/simple_distributions_cpp|normal|--distribution normal --size 10000"
  "Libraries/rocRAND/c_cpp_api/simple_distributions_cpp|poisson|--distribution poisson --size 10000")

# Lines an example must print to stdout, separated by \n
set(conformance_vendor_expected
  "HIP-Basic/hello_world|Hello world from host!\\nHello world from device or host!\\nHello world from device kernel block 0 thread 0!\\nHello world from device kernel block 0 thread 1!\\nHello world from device kernel block 1 thread 0!\\nHello world from device kernel block 1 thread 1!"
  "HIP-Basic/texture_management|bin[0] = 151552, bin[1] = 151552, bin[2] = 151552, bin[3] = 151552, bin[4] = 151552, bin[5] = 151552, bin[6] = 139264\\nsum of bins: 1048576"
  "HIP-Basic/device_query|(2147483647, 65536, 65536)")

# examples that sample images, which gfx94x and gfx95x cannot (see imageInstructions in native/hsa device_model.hpp)
set(conformance_vendor_image_examples "HIP-Basic/texture_management")

set(conformance_vendor_cases)
set(conformance_vendor_examples)
foreach(directory IN LISTS conformance_vendor_example_directories)
  conformance_vendor_collect_executables("${CMAKE_CURRENT_BINARY_DIR}/rocm-examples/${directory}" executables)
  foreach(target IN LISTS executables)
    set(unsupported "")
    get_target_property(links "${target}" LINK_LIBRARIES)
    foreach(link IN LISTS links)
      if(link MATCHES "^roc::(rocblas|rocfft|rocrand|rocsolver|rocsparse)$")
        set(library "${CMAKE_MATCH_1}")
        if(NOT conformance_vendor_architecture IN_LIST conformance_vendor_${library}_targets)
          set(unsupported "${library} ${ROCM_VERSION} ships no code object for ${conformance_vendor_architecture}")
        endif()
      endif()
    endforeach()
    set_target_properties("${target}" PROPERTIES
      RUNTIME_OUTPUT_DIRECTORY "${CMAKE_BINARY_DIR}/programs/${CONFORMANCE_BACKEND}/vendor-hip")
    target_include_directories("${target}" PRIVATE "${CMAKE_CURRENT_LIST_DIR}")
    install(TARGETS "${target}" RUNTIME DESTINATION vendor-hip COMPONENT architecture)
    get_target_property(source_directory "${target}" SOURCE_DIR)
    file(RELATIVE_PATH example "${rocm_examples_staged}" "${source_directory}")
    if(example IN_LIST conformance_vendor_image_examples AND conformance_vendor_architecture MATCHES "^gfx9[45]")
      set(unsupported "${conformance_vendor_architecture} has no image instructions")
    endif()
    conformance_vendor_lookup(conformance_vendor_expected "${example}" expected)
    set(variants)
    foreach(entry IN LISTS conformance_vendor_arguments)
      if(entry MATCHES "^${example}\\|(.*)$")
        list(APPEND variants "${CMAKE_MATCH_1}")
      endif()
    endforeach()
    if(NOT variants)
      list(APPEND variants "|")
    endif()
    foreach(variant IN LISTS variants)
      string(REGEX MATCH "^([^|]*)\\|(.*)$" _ "${variant}")
      set(name "vendor/hip/rocm-examples/${example}")
      if(NOT CMAKE_MATCH_1 STREQUAL "")
        string(APPEND name "/${CMAKE_MATCH_1}")
      endif()
      string(APPEND conformance_vendor_cases
        "{\"${name}\", \"vendor-hip/${target}\", \"${unsupported}\", \"${CMAKE_MATCH_2}\", \"${expected}\"},\n")
    endforeach()
    list(APPEND conformance_vendor_examples "${target}")
  endforeach()
endforeach()
