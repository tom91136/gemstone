package net.kurobako.gemstone.launcher

private[gemstone] object Launch {
  private val RemovedOverrides = Set(
    "CONFORMANCE_DEVICE",
    "GEMSTONE_RECORD",
    "HSA_ENABLE_DTIF",
    "HSA_TOOLS_LIB",
    "OCL_ICD_VENDORS",
    "OCL_ICD_FILENAMES",
    "ONEAPI_DEVICE_SELECTOR",
    "SYCL_DEVICE_FILTER",
    "ZE_ENABLE_ALT_DRIVERS"
  )

  def plan(
      apis: Set[ComputeAPI],
      layout: RuntimeLayout,
      endpoint: Endpoint,
      inherited: Map[String, String]
  ): Either[LaunchError, LaunchPlan] =
    if (apis.isEmpty) Left(LaunchError("at least one compute API must be enabled"))
    else {
      val runtime = layout.absolute
      val preload = ComputeAPI.values.view.filter(apis).collect {
        case api @ (ComputeAPI.CUDA | ComputeAPI.HIP | ComputeAPI.HSA | ComputeAPI.OpenCL) => runtime.library(api)
      }.toVector
      val directories          = preload.flatMap(path => Option(path.getParent)).distinct
      val inheritedPreload     = inherited.get("LD_PRELOAD").filter(_.nonEmpty).toVector
      val inheritedLibraryPath = inherited.get("LD_LIBRARY_PATH").filter(_.nonEmpty).toVector
      val common               = Map(
        "GEMSTONE_RPC_PORT"   -> s"${endpoint.port.value}",
        "GEMSTONE_RPC_CLIENT" -> s"${endpoint.client.value}"
      )
      val dynamic           = Map.newBuilder[String, String]
      val conformanceTarget = for {
        backend      <- inherited.get("CONFORMANCE_TARGET_BACKEND")
        architecture <- inherited.get("CONFORMANCE_TARGET_ARCHITECTURE").filter(_.nonEmpty)
      } yield backend -> architecture
      conformanceTarget.foreach {
        case ("cuda", architecture)
            if apis(ComputeAPI.CUDA) && !inherited.contains("GEMSTONE_CUDA_TARGET") &&
              !inherited.contains("GEMSTONE_CUDA_TARGET_ARCH") =>
          dynamic += "GEMSTONE_CUDA_TARGET" -> architecture
        case ("hip" | "hsa", architecture)
            if (apis(ComputeAPI.HIP) || apis(ComputeAPI.HSA)) && !inherited.contains("GEMSTONE_AMDGPU_TARGET") =>
          dynamic += "GEMSTONE_AMDGPU_TARGET" -> architecture
        case _ =>
      }
      if (preload.nonEmpty) dynamic += "LD_PRELOAD" -> (preload.map(path => s"$path") ++ inheritedPreload).mkString(":")
      if (directories.nonEmpty)
        dynamic += "LD_LIBRARY_PATH" -> (directories.map(path => s"$path") ++ inheritedLibraryPath).mkString(":")
      if (apis(ComputeAPI.OpenCL))
        dynamic += "OCL_ICD_FILENAMES" -> s"${runtime.library(ComputeAPI.OpenCL)}"
      if (apis(ComputeAPI.LevelZero)) {
        dynamic += "ZE_ENABLE_ALT_DRIVERS"        -> s"${runtime.library(ComputeAPI.LevelZero)}"
        dynamic += "ZE_ENABLE_LOADER_DEBUG_TRACE" -> "0"
      }
      Right(LaunchPlan(apis, Environment(common ++ dynamic.result(), RemovedOverrides)))
    }
}
