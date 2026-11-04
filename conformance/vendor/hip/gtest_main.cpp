#include <gtest/gtest.h>

int main(int argc, char **argv) {
#ifdef CONFORMANCE_GTEST_FILTER
  GTEST_FLAG_SET(filter, CONFORMANCE_GTEST_FILTER);
#endif
  testing::InitGoogleTest(&argc, argv);
  return RUN_ALL_TESTS();
}
