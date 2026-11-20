package net.kurobako.gemstone.isa.check

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.amdgpu.GlobalMemory
import net.kurobako.gemstone.isa.nvidia.Machine as NvidiaMachine
import net.kurobako.gemstone.semantics.Space
import net.kurobako.gemstone.semantics.render.*

/** Each ISA's loads and stores of global memory as the machine's plain accesses of [[Space.Global]]: whole bytes by
  * 64-bit address, little-endian, which [[Rejection]] admits and a solver encodes as one array both sequences of a
  * proof share. Every other access of memory stays as its ISA renders it, and so stays refused, as does a definition
  * that both loads and stores, an update in place that other lanes may race with, and a SASS definition of more than
  * one access, which execution aligns as one where the definition renders one per element.
  */
object GlobalAccess {
  private val amdgpu = classOf[GlobalMemory].getName
  private val nvidia = NvidiaMachine.memoryContext._1
  private val Sized  = "(load|store)(8|16|32|64)".r
  private val global = RenderMachine.space(Space.Global)

  private def bytes(tpe: Type): Boolean = tpe match {
    case Type.Integer(width, false) => width % 8 == 0
    case _                          => false
  }

  private def load(tpe: Type, address: Term): Term = Term.context(
    RenderMachine.Machine,
    MachineCall("load", Vec(tpe), Vec((global, None), (plain(address), Some(RenderMachine.Word)))),
    Some(tpe)
  )

  private def store(tpe: Type, address: Term, value: Term): Term = Term.context(
    RenderMachine.Machine,
    MachineCall(
      "store",
      Vec(tpe),
      Vec((global, None), (plain(address), Some(RenderMachine.Word)), (plain(value), Some(tpe)))
    ),
    None,
    effect = true
  )

  private def width(bytes: Term): Term = bytes match {
    case Term.Call(_, Vec(literal @ Term.Literal(_))) => literal
    case other                                        => other
  }

  private def plain(term: Term): Term = term match {
    case Term.Context(contextType = `amdgpu`, method = Sized("load", width), arguments = Vec(address)) =>
      load(Type.unsigned(width.toInt.unsafe[Int in 1 ~ 64]), address)
    case Term.Context(contextType = `amdgpu`, method = Sized("store", width), arguments = Vec(address, value)) =>
      store(Type.unsigned(width.toInt.unsafe[Int in 1 ~ 64]), address, value)
    case Term.Context(contextType = `nvidia`, method = "loadGlobal", typeArguments = Vec(tpe), arguments = Vec(address))
        if bytes(tpe) => load(tpe, address)
    case Term.Context(
          contextType = `nvidia`,
          method = "storeGlobal",
          typeArguments = Vec(tpe),
          arguments = Vec(address, value)
        ) if bytes(tpe) => store(tpe, address, value)
    case Term.Context(
          contextType = `nvidia`,
          method = check @ ("alignLoadGlobal" | "alignStoreGlobal"),
          arguments = Vec(address, bytes)
        ) =>
      Term.context(
        RenderMachine.Machine,
        MachineCall(
          check.stripSuffix("Global"),
          Vec.empty,
          Vec((global, None), (plain(address), Some(RenderMachine.Word)), (width(bytes), None))
        ),
        None,
        effect = true
      )
    case other => other.mapChildren(plain(_))
  }

  private def terms(definition: Definition): Vec[Term] =
    (definition.requirements ++ definition.body.map(_.term) ++ definition.results.map(_.value))
      .flatMap(Rejection.terms(_))

  private def machine(method: String)(term: Term): Boolean = term match {
    case Term.Context(contextType = owner, method = called) =>
      owner == RenderMachine.Machine.contextType && called == method
    case _ => false
  }

  /** `definition` with its global accesses plain, and the contexts it then reaches, or as it is where they are not
    * accesses a proof can take.
    */
  def plain(definition: Definition): Definition = {
    val requirements = definition.requirements.map(plain(_))
    val body         = definition.body.map(statement => statement.copy(term = plain(statement.term)))
    val results      = definition.results.map((name, tpe, value) => (name, tpe, plain(value)))
    val reached      = (requirements ++ body.map(_.term) ++ results.map(_._3)).flatMap(_.contexts).distinct
    val memories     = Set(amdgpu, nvidia, RenderMachine.Machine.contextType)
    val known        = definition.contexts.map(context => (context.name, context.tpe))
    val made         = definition.copy(
      contexts = known.filter(context => !memories(context._2) || reached.contains(context)) ++
        reached.filter(context => memories(context._2) && !known.contains(context)),
      requirements = requirements,
      body = body,
      results = results
    )
    if (!(loads(made) && terms(made).exists(machine("store")))) made else definition
  }

  /** Whether `definition` loads or stores memory through the machine's plain accesses. */
  def accesses(definition: Definition): Boolean =
    definition.contexts.exists(_.tpe == RenderMachine.Machine.contextType)

  /** Whether `definition` loads memory. */
  def loads(definition: Definition): Boolean = terms(definition).exists(machine("load"))
}
