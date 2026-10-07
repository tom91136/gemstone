include_guard(GLOBAL)

# A changed configuration signature discards the build tree so stale CMake
# cache entries cannot leak into a differently configured artifact.
function(prepare_work_directory directory signature)
  file(MAKE_DIRECTORY "${directory}")
  set(signature_file "${directory}/.signature")
  if(EXISTS "${signature_file}")
    file(READ "${signature_file}" previous)
    if(NOT previous STREQUAL signature)
      file(REMOVE_RECURSE "${directory}/build")
    endif()
  endif()
  file(WRITE "${signature_file}" "${signature}")
endfunction()
