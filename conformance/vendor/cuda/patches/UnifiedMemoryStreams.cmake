patch_exact("${source_root}.staging/UnifiedMemoryStreams.cu"
  [==[#include <algorithm>]==]
  [==[#include <algorithm>
#include <cmath>]==])
patch_exact("${source_root}.staging/UnifiedMemoryStreams.cu"
  [==[// Free TaskList]==]
  [==[for (auto &t : TaskList)
    for (unsigned int i = 0; i < t.size; i++) {
      double row = 0., col = 0.;
      for (unsigned int j = 0; j < t.size; j++) {
        row += t.data[i * t.size + j] * t.vector[j];
        col += t.data[j * t.size + i] * t.vector[j];
      }
      double tol = 1e-9 * t.size;
      if (!(std::abs(t.result[i] - row) <= tol || std::abs(t.result[i] - col) <= tol)) exit(EXIT_FAILURE);
    }

  // Free TaskList]==])
