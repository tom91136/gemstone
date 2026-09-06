#include "../provider.hpp"

#include "unified_suite/adapters/thrust_async.hpp"
#include "unified_suite/async_cases.hpp"
#include "unified_suite/hip_async_cases.hpp"

namespace unified_suite::program {

std::vector<Case> async_cases(const Configuration &configuration) {
  auto result = unified_suite::async_cases::cases<adapters::ThrustAsync>(configuration);
  auto hip_scan = unified_suite::hip_async_cases::cases<adapters::ThrustAsync>(configuration);
  result.insert(result.end(), hip_scan.begin(), hip_scan.end());
  return result;
}

} // namespace unified_suite::program
