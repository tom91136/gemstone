package net.kurobako.gemstone.launcher

private[gemstone] final case class LaunchPlan(
    apis: Set[ComputeAPI],
    environment: Environment
)
