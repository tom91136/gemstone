package net.kurobako.gemstone.server.execution

import net.kurobako.gemstone.vm.Event
import net.kurobako.gemstone.vm.Observer
import net.kurobako.gemstone.vm.Scheduler

final case class MachineServices(observer: Observer[Event], scheduler: Scheduler)
