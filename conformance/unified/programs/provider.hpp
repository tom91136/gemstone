#ifndef UNIFIED_SUITE_PROGRAMS_PROVIDER_HPP
#define UNIFIED_SUITE_PROGRAMS_PROVIDER_HPP

#include "unified_suite/config.hpp"
#include "unified_suite/result.hpp"

#include <vector>

namespace unified_suite::program {

std::vector<Case> algorithm_cases(const Configuration &configuration);
std::vector<Case> algorithm_i8_cases(const Configuration &configuration);
std::vector<Case> algorithm_i32_cases(const Configuration &configuration);
std::vector<Case> algorithm_i64_cases(const Configuration &configuration);
std::vector<Case> algorithm_f32_cases(const Configuration &configuration);
std::vector<Case> algorithm_f64_cases(const Configuration &configuration);
std::vector<Case> blas_cases(const Configuration &configuration);
std::vector<Case> edge_case_cases(const Configuration &configuration);
std::vector<Case> stress_cases(const Configuration &configuration);

} // namespace unified_suite::program

#endif
