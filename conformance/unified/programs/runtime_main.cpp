#include "provider.hpp"
#include "unified_suite/runner.hpp"
int main(int argc, char **argv) {
  const auto empty = [](const unified_suite::Configuration &) { return std::vector<unified_suite::Case>{}; };
  return unified_suite::main(argc, argv,
                             {empty, empty, unified_suite::program::edge_case_cases, empty, empty});
}
