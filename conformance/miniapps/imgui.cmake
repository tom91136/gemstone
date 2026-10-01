set(miniapp_imgui_models vulkan)
conformance_select_miniapp_models(imgui "vulkan" imgui_models)
if(NOT imgui_models)
  return()
endif()
conformance_fetch_url(conformance_imgui imgui_source)
find_program(imgui_glslc glslc REQUIRED)
find_file(imgui_lavapipe NAMES lvp_icd.x86_64.json lvp_icd.json
  PATHS /usr/share/vulkan/icd.d REQUIRED)

add_library(miniapp_imgui STATIC
  "${imgui_source}/imgui.cpp"
  "${imgui_source}/imgui_draw.cpp"
  "${imgui_source}/imgui_tables.cpp"
  "${imgui_source}/imgui_widgets.cpp"
  "${imgui_source}/backends/imgui_impl_vulkan.cpp")
target_include_directories(miniapp_imgui SYSTEM PUBLIC "${imgui_source}" "${imgui_source}/backends")
target_link_libraries(miniapp_imgui PUBLIC Vulkan::Vulkan)
target_compile_features(miniapp_imgui PUBLIC cxx_std_17)

set(imgui_shaders)
foreach(stage vert frag)
  set(shader "${CMAKE_CURRENT_BINARY_DIR}/imgui_${stage}.hpp")
  add_custom_command(OUTPUT "${shader}"
    COMMAND "${imgui_glslc}" --target-env=vulkan1.1 -mfmt=c
      "${imgui_source}/backends/vulkan/glsl_shader.${stage}" -o "${shader}"
    DEPENDS "${imgui_source}/backends/vulkan/glsl_shader.${stage}" VERBATIM)
  list(APPEND imgui_shaders "${shader}")
endforeach()
add_library(miniapp_imgui_scene STATIC imgui_scene.cpp ${imgui_shaders})
target_include_directories(miniapp_imgui_scene PRIVATE "${CMAKE_CURRENT_BINARY_DIR}")
target_link_libraries(miniapp_imgui_scene PUBLIC miniapp_imgui conformance_device)

# Generate references with lavapipe, independently of the device under test.
add_executable(imgui_reference imgui_vulkan_test.cpp)
target_compile_definitions(imgui_reference PRIVATE IMGUI_REFERENCE_GENERATOR)
target_link_libraries(imgui_reference PRIVATE miniapp_imgui_scene)
set(imgui_reference_directory "${CMAKE_BINARY_DIR}/programs/${CONFORMANCE_BACKEND}/imgui")
set(imgui_references)
foreach(scene primitives layout resize)
  list(APPEND imgui_references "${imgui_reference_directory}/${scene}.ppm")
endforeach()
add_custom_command(OUTPUT ${imgui_references}
  COMMAND "${CMAKE_COMMAND}" -E env "VK_ICD_FILENAMES=${imgui_lavapipe}" "VK_DRIVER_FILES=${imgui_lavapipe}"
    $<TARGET_FILE:imgui_reference> "${imgui_reference_directory}"
  DEPENDS imgui_reference VERBATIM)
add_custom_target(imgui_reference_images DEPENDS ${imgui_references})
add_executable(miniapp_imgui_vulkan imgui_vulkan_test.cpp)
target_link_libraries(miniapp_imgui_vulkan PRIVATE miniapp_imgui_scene Catch2::Catch2WithMain)
add_dependencies(miniapp_imgui_vulkan imgui_reference_images)
conformance_register_test_program(miniapp_imgui_vulkan 300)
list(APPEND miniapp_targets miniapp_imgui_vulkan)
install(FILES ${imgui_references} DESTINATION imgui COMPONENT support)
install(FILES "${imgui_source}/LICENSE.txt" DESTINATION licenses RENAME imgui.txt COMPONENT support)
