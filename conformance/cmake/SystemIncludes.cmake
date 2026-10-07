include_guard(GLOBAL)

function(mark_system_includes target)
  if(NOT TARGET "${target}")
    return()
  endif()
  get_target_property(aliased "${target}" ALIASED_TARGET)
  if(aliased)
    set(target "${aliased}")
  endif()
  set_property(TARGET "${target}" APPEND PROPERTY INTERFACE_SYSTEM_INCLUDE_DIRECTORIES
    "$<TARGET_PROPERTY:${target},INTERFACE_INCLUDE_DIRECTORIES>")
endfunction()
