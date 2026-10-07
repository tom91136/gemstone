#include <cstdlib>
#include <filesystem>
#include <iterator>

#include <CL/cl.h>

namespace {

auto threads() { return std::distance(std::filesystem::directory_iterator("/proc/self/task"), {}); }

void CL_CALLBACK waitForGate(cl_event, cl_int, void *gate) {
  auto event = static_cast<cl_event>(gate);
  (void)clWaitForEvents(1, &event);
}

cl_context context = nullptr;
cl_device_id device = nullptr;
cl_event late = nullptr;

// Layered runtimes register teardown after loading the ICD but before its first submission, and release their objects
// from it.
void teardown() {
  cl_int error = CL_SUCCESS;
  auto queue = clCreateCommandQueueWithProperties(context, device, nullptr, &error);
  cl_event queued = nullptr;
  const bool released = queue && clEnqueueMarkerWithWaitList(queue, 1, &late, &queued) == CL_SUCCESS
                        && clSetUserEventStatus(late, CL_COMPLETE) == CL_SUCCESS && clWaitForEvents(1, &queued) == CL_SUCCESS
                        && clReleaseEvent(queued) == CL_SUCCESS && clReleaseEvent(late) == CL_SUCCESS
                        && clReleaseCommandQueue(queue) == CL_SUCCESS;
  if (!released) std::_Exit(EXIT_FAILURE);
}

} // namespace

int main() {
  if (std::atexit(teardown) != 0) return EXIT_FAILURE;
  // ICD loaders load every installed ICD, including in processes that never use this one.
  if (threads() != 1) return EXIT_FAILURE;
  cl_platform_id platform = nullptr;
  if (clGetPlatformIDs(1, &platform, nullptr) != CL_SUCCESS) return EXIT_FAILURE;
  if (clGetDeviceIDs(platform, CL_DEVICE_TYPE_GPU, 1, &device, nullptr) != CL_SUCCESS) return EXIT_FAILURE;
  cl_int error = CL_SUCCESS;
  context = clCreateContext(nullptr, 1, &device, nullptr, nullptr, &error);
  if (!context || error != CL_SUCCESS) return EXIT_FAILURE;
  auto queue = clCreateCommandQueueWithProperties(context, device, nullptr, &error);
  if (!queue || error != CL_SUCCESS) return EXIT_FAILURE;
  auto start = clCreateUserEvent(context, &error);
  auto gate = clCreateUserEvent(context, &error);
  late = clCreateUserEvent(context, &error);
  if (!start || !gate || !late) return EXIT_FAILURE;
  cl_event marker = nullptr;
  if (clEnqueueMarkerWithWaitList(queue, 1, &start, &marker) != CL_SUCCESS) return EXIT_FAILURE;
  // The callback runs on a queue worker and blocks on an event the host never completes.
  if (clSetEventCallback(marker, CL_COMPLETE, waitForGate, gate) != CL_SUCCESS) return EXIT_FAILURE;
  if (clSetUserEventStatus(start, CL_COMPLETE) != CL_SUCCESS) return EXIT_FAILURE;
  return EXIT_SUCCESS;
}
