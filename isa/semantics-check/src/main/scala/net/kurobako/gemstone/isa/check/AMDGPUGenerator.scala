package net.kurobako.gemstone.isa.check

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.explorer.*
import net.kurobako.gemstone.isa.amdgpu.*
import net.kurobako.gemstone.semantics.render.*
import scala.collection.mutable

/** The AMDGPU instances the explorer describes on GFX8, GCN5 and RDNA2, which run wave64 and wave32, on CDNA2, CDNA4,
  * RDNA3, RDNA3.5 and RDNA4, which add the MFMA and WMMA matrix instructions and their own conversions, on GFX6 and
  * GFX7, whose legacy opcodes, VOP2 encodings, ADDR64 buffer addressing and FLAT the later ones dropped, and the matrix
  * instructions alone of CDNA1 and CDNA3: the first instance of each mnemonic, then each instance whose definition
  * differs from every one checked before in more than its names and numbers, its operands named by their role, or for a
  * buffer or image instruction, in the accesses and conversions it makes on a descriptor layout. A matrix instruction's
  * shapes differ only in its layout tables, so of each target's element formats, kinds of modifier and register file
  * only the smallest shape is checked; where even that shape is deep, as SMFMAC's, SWMMAC's and gfx950's f8f6f4 are,
  * once per element formats whatever its modifiers, register file and target. Left out are the scratch accesses, which
  * fault past the private segment for most inputs, except where a buffer access reaches it through a null descriptor.
  */
object AMDGPUGenerator {
  val targets: Vec[String] =
    Vec("gfx906", "gfx1030", "gfx90a", "gfx1100", "gfx803", "gfx1151", "gfx950", "gfx1201", "gfx601", "gfx700")

  /** Targets whose matrix instructions alone are checked. */
  val matrixTargets: Vec[String] = Vec("gfx908", "gfx942")

  /** Targets whose dual-issue pairs alone are checked: gfx1250's VOPD3 halves read pairs, three sources and NEG. */
  val dualTargets: Vec[String] = Vec("gfx1250")

  private val TargetSet       = targets.toSet
  private val MatrixTargetSet = matrixTargets.toSet
  private val DualTargetSet   = dualTargets.toSet

  private val Matrix   = "v_s?(mfma|wmma|mfmac|wmmac)_.*".r
  private val Dual     = "v_dual_.*".r
  private val Resource = "(t?buffer|image|s_buffer)_.*".r

  // the modifiers that change where a resource access lands or what it returns, not cache policy
  private val Addressing = "offen|idxen|addr64|lds|tfe|glc|sc0|th:TH_ATOMIC_RETURN|d16|a16|r128|da|unorm|dim:.+".r
  private val Owner      = ".*\\.(BufferResource|ImageResource|TexelFormat)".r

  /** A resource instance's kind: the descriptor layout `target` reads, the machine operations and resource functions
    * its definition calls, as often as it calls them, and the modifiers that address it, so each access width,
    * addressing, range check and conversion shows once per layout whatever operation combines the values, which other
    * memories' instructions check.
    */
  private def resource(target: String, instance: String, definition: Definition): String = {
    def calls(term: Term): Vec[String] = term match {
      case Term.Context(context = context, method = method, arguments = arguments) =>
        s"$context.$method" +: arguments.flatMap(calls(_))
      case Term.Primitive(owner, method, arguments, _, _) =>
        Option.when(Owner.matches(owner))(s"$owner.$method").toVec ++ arguments.flatMap(calls(_))
      case Term.Unary(_, value)            => calls(value)
      case Term.Binary(_, left, right)     => calls(left) ++ calls(right)
      case Term.Call(_, arguments)         => arguments.flatMap(calls(_))
      case Term.Select(condition, yes, no) => calls(condition) ++ calls(yes) ++ calls(no)
      case _                               => Vec.empty
    }
    val called = definition.body.flatMap(statement => calls(statement.term)) ++
      definition.results.flatMap(result => calls(result.value))
    val modifiers = instance.split(' ').toVec.drop(1).filter(Addressing.matches(_))
    val layout    = target.refine[Target].toOption.fold("")(found => ResourceGeneration.of(found.isa).toString)
    s"$layout ${called.sorted.mkString(" ")} ${modifiers.mkString(" ")}"
  }

  /** The longest definition emitted as one method, in characters. The compiler computes stack frames for every
    * instruction of a method over all its locals, so the deepest matrix shapes, such as SMFMAC's and SWMMAC's of K 64
    * and gfx950's f8f6f4 of K 128, split into a method per result.
    */
  private val Whole = 17000

  private val F8F6F4Registers = Vec(8, 8, 6, 6, 4)
  private val Dimensions      = raw"_\d+x\d+x\d+(_\d+b)?".r
  private val Modifier        = "(cbsz|abid|blgp|neg|neg_lo|neg_hi|op_sel|op_sel_hi|index_key|clamp)(:.*)?".r

  /** A matrix instance's kind: its encoding family (fp8 and bf8 are FNUZ before gfx950), mnemonic without dimensions
    * and with fp8 and bf8 alike, the width of A's elements where CBSZ picks their format, else whether it takes other
    * modifiers, and, for the matrix core, whether it names accumulation registers.
    */
  private def matrixKind(target: String, instance: String): String = {
    val words    = instance.split(' ').toVec
    val mnemonic = words.head
    val family   = target match {
      case "gfx950"                        => "cdna4"
      case cdna if cdna.startsWith("gfx9") => "cdna"
      case rdna                            => rdna.take(5)
    }
    val selective = mnemonic.endsWith("_f8f6f4")
    val formats   = words.tail.collectFirst {
      case word @ Modifier("cbsz", _) if selective => F8F6F4Registers(word.last - '0')
    }.getOrElse(8)
    val modified = !selective && words.tail.exists {
      case Modifier(_, _) => true
      case _              => false
    }
    val banked = mnemonic.startsWith("v_mfma") && instance.contains(" a[")
    s"$family ${Dimensions.replaceAllIn(mnemonic, "").replace("bf8", "f8").replace("fp8", "f8")} $formats $modified $banked"
  }

  /** Whether any context a definition calls satisfies `found`. */
  private def anywhere(definition: Definition)(found: Term.Context => Boolean): Boolean = {
    def uses(term: Term): Boolean = term match {
      case context @ Term.Context(arguments = arguments) => found(context) || arguments.exists(uses(_))
      case Term.Primitive(arguments = arguments)         => arguments.exists(uses(_))
      case Term.Unary(_, value)                          => uses(value)
      case Term.Binary(_, left, right)                   => uses(left) || uses(right)
      case Term.Call(_, arguments)                       => arguments.exists(uses(_))
      case Term.Select(condition, yes, no)               => uses(condition) || uses(yes) || uses(no)
      case _                                             => false
    }
    definition.body.exists(statement => uses(statement.term)) || definition.results.exists(result => uses(result.value))
  }

  // buffer accesses reach private memory only through a null descriptor
  private def faulting(definition: Definition): Boolean =
    anywhere(definition)(_.contextType.endsWith(".PrivateMemory")) &&
      !anywhere(definition)(_.contextType.endsWith(".GlobalMemory"))

  private val Routed = "quad_perm|row_|dpp8|bank_mask".r

  private val Relative = Set("vgpr", "vgprAt", "writeVgpr")

  private val Word = "0x[0-9a-fA-F]+L?|[0-9]+L?|[A-Za-z_][A-Za-z0-9_]*".r

  private val TypeWidths = raw"\[[0-9, ]+\]".r

  // whole names and numbers erased but for the operand and result types, the body's type-argument widths and the
  // operands' roles, their names less any index, so each SDWA selection or modifier wrapping any operation shows once
  // per width and operand encoding; DPP instances by their spelling, and the relative moves' by their mnemonic too, so
  // each control and modifier shows once per target, and once per relative move
  private def structure(target: String, instance: String, definition: Definition): String =
    if (Routed.findFirstIn(instance).isEmpty) {
      val operands = (definition.parameters.map(_.name) ++ definition.results.map(_.name)).toSet
      val types    = (definition.parameters.map(_.tpe) ++ definition.results.map(_.tpe)).map(tpe =>
        ScalaPrinter.show(tpe.unranged)
      )
      val text = ScalaBackend.show(definition)
      types.mkString(",") + TypeWidths.findAllIn(text).mkString + Word.replaceAllIn(
        text,
        word =>
          if (word.matched.head.isDigit) "#" else if (operands(word.matched)) word.matched.filterNot(_.isDigit) else "x"
      )
    } else {
      val moved = if (anywhere(definition)(context => Relative(context.method))) instance.takeWhile(!_.isWhitespace)
      else ""
      s"$target${instance.dropWhile(!_.isWhitespace).replaceAll("[vsa]\\[?[0-9]+(:[0-9]+)?\\]?|0x[0-9a-f]+|[0-9]+", "r")}$moved"
    }

  private type Spelt = (described: Described, operands: Map[String, String])

  private def spelt(target: String, found: Spelt): Case =
    Case(found.described.instance, Some(target), found.described.definition, Lookup.Spellings(found.operands))

  private def described(included: String => Boolean) = (targets ++ matrixTargets ++ dualTargets).distinct.flatMap(
    target =>
      AMDGPUModel.entries(Some(target)).view
        .filter(entry =>
          included(entry.mnemonic) &&
            (TargetSet(target) ||
              (MatrixTargetSet(target) && Matrix.matches(entry.mnemonic)) ||
              (DualTargetSet(target) && Dual.matches(entry.mnemonic)))
        )
        .flatMap(entry => entry.forms.map(form => (target, entry.mnemonic, AMDGPUModel.spelt(entry, form))))
        .toVec
  )

  private val Number = raw"\b[0-9]+\b".r

  private def matrices(described: Vec[(String, String, Vec[Spelt])]): Vec[Case] = {
    val kept = Vec.newBuilder[Case]
    val seen = mutable.Set.empty[String]
    described.collect {
      case (target, mnemonic, found) if Matrix.matches(mnemonic) => found.map(spelt(target, _))
    }.flatten.groupBy(value => matrixKind(value.target.getOrElse(""), value.instance)).toVec.sortBy(_._1).map(
      (_, values) => values.minBy(value => ScalaBackend.show(value.definition).length)
    ).foreach { value =>
      val text = ScalaBackend.show(value.definition)
      if (text.length <= Whole) kept += value
      else {
        // a deep shape once per element formats and once per definition up to its numbers: its modifiers, register
        // files and the targets sharing it read other lanes and registers through the code the shallower shapes check
        val keys = Set(
          matrixKind(value.target.getOrElse(""), value.instance).split(' ').dropRight(2).mkString(" "),
          Number.replaceAllIn(text, "#")
        )
        if (!keys.exists(seen)) {
          val _ = seen ++= keys
          kept += value
        }
      }
    }
    kept.result()
  }

  private def cases(included: String => Boolean): Vec[Case] = {
    val found   = described(included)
    val kept    = Vec.newBuilder[Case]
    val checked = mutable.Set.empty[String]
    val shown   = mutable.Set.empty[String]
    found.filterNot((_, mnemonic, _) => Matrix.matches(mnemonic)).foreach { (target, mnemonic, instances) =>
      instances.foreach { candidate =>
        val value = spelt(target, candidate)
        val kind  =
          if (Resource.matches(mnemonic)) resource(target, value.instance, value.definition)
          else structure(target, value.instance, value.definition)
        if ((checked(mnemonic) && shown(kind)) || faulting(value.definition)) shown += kind
        else {
          val _ = kept += value
          val _ = checked += mnemonic
          shown += kind
        }
      }
    }
    kept.result() ++ matrices(found)
  }

  /** The cases of the matrix instructions, of the buffer and image instructions, and of the rest, each compiled on its
    * own as together they outgrow the build's heap.
    */
  val matrix: Generator   = Part("AMDGPUMatrix", Matrix.matches(_))
  val resource: Generator = Part("AMDGPUResource", Resource.matches(_))
  val rest: Generator     = Part("AMDGPU", mnemonic => !Matrix.matches(mnemonic) && !Resource.matches(mnemonic))

  private class Part(prefix: String, included: String => Boolean) extends Generator {
    def sources: Vec[(name: String, source: String)] =
      Emit.sources(prefix, "AMDGPUSuite", cases(included), Whole)
  }
}
