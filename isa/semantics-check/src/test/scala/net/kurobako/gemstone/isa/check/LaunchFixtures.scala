package net.kurobako.gemstone.isa.check

import net.kurobako.gemstone.collection.*

object LaunchFixtures {
  def arguments(text: String): Vec[LaunchArgument] =
    LaunchArgument.parse(text).fold(error => throw IllegalArgumentException(error), identity)
}
