package net.kurobako.gemstone.vm

import net.kurobako.gemstone.constraint.*

object MachineUnit {

  /** What a unit is doing. */
  enum State derives CanEqual {

    /** The unit is issuing instructions. */
    case Running

    /** The unit is parked, waiting for the machine to release it. */
    case Blocked(reason: Reason)

    /** The unit has finished; it issues nothing more. */
    case Retired
  }

  /** Why a unit is parked. */
  enum Reason derives CanEqual {

    /** Waiting at a barrier for its participants to arrive. */
    case Barrier(id: String)

    /** Waiting for the lanes of its own unit to arrive at a collective. */
    case Members

    /** Waiting for memory to become available. */
    case Memory

    /** Waiting for another workgroup of the cluster. */
    case Cluster(id: String)

    /** Waiting for a collective to resolve. */
    case Collective(id: String)
  }
}

/** One unit of execution: the lanes a backend runs together. NVIDIA runs a cohort of lanes, AMDGPU a wave, SPIR-V an
  * invocation. A timeline compares units by the lanes they carry, as the hardware would, and by what they are doing.
  */
case class MachineUnit(
    /** The workgroup the unit belongs to. */
    workgroup: LaunchGeometry.Coordinates,
    /** The unit's ordinal within its workgroup, which the backend names. */
    ordinal: Int >= 0,
    /** How many lanes the unit runs. */
    lanes: Int >= 0,
    /** What the unit is doing. */
    state: MachineUnit.State
) derives CanEqual
