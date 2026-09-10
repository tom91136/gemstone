package net.kurobako.gemstone.launcher

import net.kurobako.gemstone.Col

/** Platform eligibility is separate from whether a native provider has been built for that platform. */
enum RuntimePlatform {
  case Linux, Windows, Other
}

object RuntimePlatform {
  // OS identifiers use ASCII; avoid locale state (and unsupported Native Locale APIs).
  def fromOSName(name: String): RuntimePlatform = name.map { character =>
    if (character >= 'A' && character <= 'Z') (character + ('a' - 'A')).toChar else character
  } match {
    case value if value.startsWith("windows") => RuntimePlatform.Windows
    case value if value == "linux"            => RuntimePlatform.Linux
    case _                                    => RuntimePlatform.Other
  }
  def current: RuntimePlatform = fromOSName(sys.props("os.name"))
}

/** HIP is an application API. Only the Linux ROCm vendor route requires the HSA boundary. Windows HIP uses a different
  * vendor backend; route eligibility does not claim Windows provider qualification.
  */
enum HIPRuntimeRoute(val id: String, val requiresHSA: Boolean) {
  case GemstoneHIP    extends HIPRuntimeRoute("hip", false)
  case VendorHIPOnHSA extends HIPRuntimeRoute("hip-hsa", true)
}

object HIPRuntimeRoute {
  def parse(value: String): Either[LaunchError, HIPRuntimeRoute] =
    values.find(_.id == value).toRight(LaunchError(s"unsupported HIP runtime route: $value"))

  def supported(platform: RuntimePlatform): Col[HIPRuntimeRoute] = platform match {
    case RuntimePlatform.Linux => Col(GemstoneHIP, VendorHIPOnHSA)
    case _                     => Col(GemstoneHIP)
  }
}
