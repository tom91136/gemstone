# SYCL CTS shard membership, read by the CTS build and by the CI matrix.
#
# A few categories dominate the compile time and the group_functions device compiles peak above 7 GB each, so those
# are cut by translation unit into numbered executables, one shard per slice. Each entry is
# category:case-list-variable:slices. A category belongs to the shard after the first pattern it matches, and shard 0
# takes the rest.
set(sycl_cts_slices
  "math_builtin_api:TEST_CASES_LIST:2"
  "vector_swizzles:TEST_CASES_LIST:4"
  "accessor_generic:test_cases_list:2"
  "accessor_legacy:test_cases_list:3"
  "reduction:test_cases_list:2"
  "group_functions:test_cases_list:8")
set(sycl_cts_shards)
foreach(entry IN LISTS sycl_cts_slices)
  string(REPLACE ":" ";" entry "${entry}")
  list(GET entry 0 category)
  list(GET entry 2 count)
  math(EXPR last "${count} - 1")
  foreach(slice RANGE ${last})
    list(APPEND sycl_cts_shards "^${category}_${slice}$")
  endforeach()
endforeach()
list(APPEND sycl_cts_shards
  "^accessor_placeholder$"
  "^host_accessor$"
  "^(kernel_bundle|multi_ptr|spec_constants)$"
  "^(accessor_basic|address_space|atomic.*)$"
  "^(local_accessor|buffer|marray_.*)$"
  "^(vector_.*|handler|usm)$")
list(LENGTH sycl_cts_shards sycl_cts_shard_count)
math(EXPR sycl_cts_shard_count "${sycl_cts_shard_count} + 1")

# a device or host frontend compile peaks at 5 to 12 GB on these, so a job runs one of them at a time; categories
# matching sycl_cts_wide stay under 4 GB and run at the full job width, the others at depth two
set(sycl_cts_serial "^((group_functions|accessor_legacy|reduction)_[0-9]+|handler)$")
set(sycl_cts_wide
  "^(math_builtin_api|vector_swizzles|accessor_generic|accessor_basic|accessor_placeholder|address_space|atomic.*|host_accessor|kernel_bundle|multi_ptr|spec_constants)(_[0-9]+)?$")

function(sycl_cts_shard_of category output)
  set(owner 0)
  set(shard 0)
  foreach(pattern IN LISTS sycl_cts_shards)
    math(EXPR shard "${shard} + 1")
    if(category MATCHES "${pattern}")
      set(owner ${shard})
      break()
    endif()
  endforeach()
  set(${output} ${owner} PARENT_SCOPE)
endfunction()
