# SYCL CTS shard membership, read by the CTS build and by the CI matrix.
#
# math_builtin_api and vector_swizzles dominate the compile time and the group_functions device compiles peak above
# 7 GB each, so group_functions is cut by translation unit into executables of its own. A category belongs to the
# shard after the first pattern it matches, and shard 0 takes the rest.
set(sycl_cts_group_functions_slices 8)
set(sycl_cts_shards
  "^math_builtin_api$"
  "^vector_swizzles$"
  "^(accessor_.*|address_space|atomic.*|host_accessor|kernel_bundle|multi_ptr|reduction|spec_constants)$")
math(EXPR sycl_cts_last_slice "${sycl_cts_group_functions_slices} - 1")
foreach(slice RANGE ${sycl_cts_last_slice})
  list(APPEND sycl_cts_shards "^group_functions_${slice}$")
endforeach()
list(LENGTH sycl_cts_shards sycl_cts_shard_count)
math(EXPR sycl_cts_shard_count "${sycl_cts_shard_count} + 1")

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
