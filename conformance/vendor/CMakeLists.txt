set(conformance_vendor_targets)
include("${CMAKE_CURRENT_SOURCE_DIR}/${CONFORMANCE_BACKEND}.cmake")
add_custom_target(conformance_vendor_artifact DEPENDS ${conformance_vendor_targets})
