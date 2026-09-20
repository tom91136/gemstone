package net.kurobako.gemstone.launcher

/** Platform eligibility is separate from whether a native provider has been built for that platform. */
private[gemstone] enum RuntimePlatform {
  case Linux, Windows, Other
}

private[gemstone] object RuntimePlatform {
  // OS identifiers use ASCII; avoid locale state (and unsupported Native Locale APIs).
  def fromOSName(name: String): RuntimePlatform = name.map { character =>
    if (character >= 'A' && character <= 'Z') (character + ('a' - 'A')).toChar else character
  } match {
    case value if value.startsWith("windows") => RuntimePlatform.Windows
    case value if value == "linux"            => RuntimePlatform.Linux
    case _                                    => RuntimePlatform.Other
  }

  def current: RuntimePlatform = fromOSName(sys.props.getOrElse("os.name", ""))
}
