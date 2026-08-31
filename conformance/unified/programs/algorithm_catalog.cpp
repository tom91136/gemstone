#include "provider.hpp"

#include "unified_suite/algorithms.hpp"

#include <iterator>

namespace unified_suite::program {
namespace {

void append(std::vector<Case> &result, std::vector<Case> cases) {
  result.insert(result.end(), std::make_move_iterator(cases.begin()), std::make_move_iterator(cases.end()));
}

} // namespace

std::vector<Case> algorithm_cases(const Configuration &configuration) {
  std::vector<Case> result;
  append(result, algorithm_i8_cases(configuration));
  append(result, algorithm_i32_cases(configuration));
  append(result, algorithm_i64_cases(configuration));
  append(result, algorithms::f16_cases(configuration));
  append(result, algorithm_f32_cases(configuration));
  append(result, algorithm_f64_cases(configuration));
  return result;
}

} // namespace unified_suite::program
