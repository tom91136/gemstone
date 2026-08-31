#include "../provider.hpp"

#include "unified_suite/adapters/onedpl_algorithms.hpp"
#include "unified_suite/algorithms.hpp"

namespace unified_suite::program {

std::vector<Case> algorithm_f64_cases(const Configuration &configuration) {
  return algorithms::cases_for<adapters::OneDPLAlgorithms, ScalarType::f64>(configuration);
}

} // namespace unified_suite::program
