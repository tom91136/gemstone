package net.kurobako.gemstone.launcher

/** Environment variable names shared across the launcher, server, and native facades. Keep in sync with
  * `native/include/gemstone/environment.hpp`.
  */
object EnvironmentNames {
  val Trace                    = "GEMSTONE_TRACE"
  val SystemPaging             = "GEMSTONE_SYSTEM_PAGING"
  val TargetArch               = "GEMSTONE_TARGET_ARCH"
  val Profile                  = "GEMSTONE_PROFILE"
  val Record                   = "GEMSTONE_RECORD"
  val RpcClient                = "GEMSTONE_RPC_CLIENT"
  val RpcPort                  = "GEMSTONE_RPC_PORT"
  val RpcTimeoutSeconds        = "GEMSTONE_RPC_TIMEOUT_SECONDS"
  val ExecutionParallelism     = "GEMSTONE_EXECUTION_PARALLELISM"
  val ExecutionMaxInstructions = "GEMSTONE_EXECUTION_MAX_INSTRUCTIONS"
  val ExecutionQuantum         = "GEMSTONE_EXECUTION_QUANTUM"
  val NativeDirectory          = "GEMSTONE_NATIVE_DIRECTORY"
  val NativeTestDirectory      = "GEMSTONE_NATIVE_TEST_DIRECTORY"
  val TestLibrary              = "GEMSTONE_TEST_LIBRARY"
  val BenchmarkJfr             = "GEMSTONE_BENCHMARK_JFR"

  /** Mirrors `gemstone::traceEnabled`: `GEMSTONE_TRACE` is a comma-separated list of categories, `all` or the bare
    * scope enables every category within it.
    */
  def traceEnabled(environment: Map[String, String], scope: String, category: String = ""): Boolean =
    environment.get(Trace).exists { value =>
      val qualified = if (category.isEmpty) scope else s"$scope.$category"
      value.split(',').exists { token =>
        val trimmed = token.trim
        trimmed == "all" || trimmed == scope || trimmed == qualified
      }
    }
}
