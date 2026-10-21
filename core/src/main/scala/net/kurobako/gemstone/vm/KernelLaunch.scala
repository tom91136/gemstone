package net.kurobako.gemstone.vm

/** A kernel task, the context it runs against, and the workgroups that schedule its work-groups. */
case class KernelLaunch(task: KernelTask, context: TaskContext, workgroups: Workgroups)
