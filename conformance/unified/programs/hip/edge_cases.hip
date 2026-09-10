#include "../provider.hpp"

#include "unified_suite/adapters/rocblas.hpp"
#include "unified_suite/adapters/thrust_edge_cases.hpp"
#include "unified_suite/device_function_cases.hpp"
#include "unified_suite/edge_cases.hpp"

namespace unified_suite::program {

std::vector<Case> edge_case_cases(const Configuration &configuration) {
  auto result = edge_cases::cases<adapters::ThrustEdgeCases, adapters::Rocblas>(configuration);
  result.push_back(gpu_calls::device_function_case<false>());
  result.push_back(gpu_calls::device_function_case<true>());
  return result;
}

} // namespace unified_suite::program
