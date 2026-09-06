#include "unified_suite/adapters/host_async.hpp"
#include "unified_suite/async_cases.hpp"
#include "unified_suite/hip_async_cases.hpp"

#include <set>
#include <string>

int main() {
  static_assert(std::size(unified_suite::async_cases::operations) == 7);
  static_assert(std::size(unified_suite::async_cases::operation_specs) ==
                std::size(unified_suite::async_cases::operations));

  unified_suite::Configuration configuration;
  configuration.edge_case_sizes = {0, 1, 7, 33};
  configuration.edge_algorithm_types = {unified_suite::ScalarType::i32, unified_suite::ScalarType::f32};
  const auto cases = unified_suite::async_cases::cases<unified_suite::adapters::HostAsync>(configuration);
  const auto expected = std::size(unified_suite::async_cases::operations) *
                        configuration.edge_case_sizes.size() * configuration.edge_algorithm_types.size();
  if (cases.size() != expected)
    return 1;

  std::set<std::string> names;
  for (const auto &test_case : cases) {
    if (!names.insert(test_case.name).second || test_case.name.rfind("async/", 0) != 0)
      return 2;
    const auto result = test_case.execute();
    if (result.status != unified_suite::Status::pass)
      return 3;
  }

  static_assert(std::size(unified_suite::hip_async_cases::operations) == 6);
  static_assert(std::size(unified_suite::hip_async_cases::operation_specs) ==
                std::size(unified_suite::hip_async_cases::operations));
  const auto hip_cases =
      unified_suite::hip_async_cases::cases<unified_suite::adapters::HostAsync>(configuration);
  const auto expected_hip = std::size(unified_suite::hip_async_cases::operations) *
                            configuration.edge_case_sizes.size() * configuration.edge_algorithm_types.size();
  if (hip_cases.size() != expected_hip)
    return 4;
  for (const auto &test_case : hip_cases) {
    if (!names.insert(test_case.name).second || test_case.name.rfind("async/hip/", 0) != 0)
      return 5;
    const auto result = test_case.execute();
    if (result.status != unified_suite::Status::pass)
      return 6;
  }
  return 0;
}
