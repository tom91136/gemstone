#include "../provider.hpp"

#include "unified_suite/adapters/onedpl_async.hpp"
#include "unified_suite/async_cases.hpp"

namespace unified_suite::program {

std::vector<Case> async_cases(const Configuration &configuration) {
  return unified_suite::async_cases::cases<adapters::OneDPLAsync>(configuration);
}

} // namespace unified_suite::program
