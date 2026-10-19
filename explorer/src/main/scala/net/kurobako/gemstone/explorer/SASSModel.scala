package net.kurobako.gemstone.explorer

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.catalogue.*
import net.kurobako.gemstone.isa.sass.*
import net.kurobako.gemstone.isa.sass.asm.*
import net.kurobako.gemstone.semantics.render.*
import scala.annotation.tailrec
import scala.collection.mutable

/** SASS forms, fields and register vocabularies come from the recovered NVIDIA disassembler tables of each target. */
object SASSModel extends Model {
  lazy val targets: Vec[String] =
    Vec.from((35 to 130).flatMap(sm => Target.parse(s"sm_$sm").toOption.map(target => s"sm_${target.sm}")))
  val anyTarget: Boolean            = false
  def defaultTarget: Option[String] = targets.find(_ == "sm_90").orElse(targets.lastOption)

  private def target(name: Option[String]): Either[String, Target] =
    name.orElse(defaultTarget).toRight("no SASS target selected").flatMap(Target.parse)

  private val cache = mutable.Map.empty[String, Vec[View.Entry]]

  def entries(name: Option[String]): Vec[View.Entry] = target(name).fold(
    _ => Vec.empty,
    target =>
      cache.getOrElseUpdate(
        target.name, {
          val database = target.database
          database.forms
            .flatMap(form => form.opcodes.map(_.name.takeWhile(_ != '.')).distinct.map(_ -> form))
            .groupMap(_._1)(_._2).toVec.sortBy(_._1)
            .map { (mnemonic, forms) =>
              val sorted = forms.sortBy(_.name)
              View.Entry(
                Language.SASS,
                mnemonic,
                sorted.map(_.name),
                () => sorted.map(this.form(database, _)),
                Some(s"sm_${target.sm}")
              )
            }
        }
      )
  )

  /** An instance's target, its text, the operand each register or predicate spelling the text uses binds, the bindings
    * of its operands in operand order, the offset field of each constant operand with the offset it reads and how it
    * writes its form.
    */
  type Instance = (
      sm: Int,
      text: String,
      operands: Map[String, String],
      order: Vec[String],
      constants: Vec[(binding: String, offset: Long)],
      written: Written
  )

  /** The target, form, opcode and the text of each of the form's declared fields, in order, that an instance writes its
    * form with.
    */
  case class Written(sm: Int, form: String, opcode: String, texts: Vec[String])

  private object Spelling {

    /** Where a constant operand's text has reached: its bank number or register, its offset, or a register indexing it.
      */
    enum Bank derives CanEqual {
      case Outside, Number, Register, Offset, Indexed
    }
  }

  /** How an instance spells its operands: the text of each operand field, the operand each register or predicate
    * spelling binds, the operands' bindings in order and its constant operands' offset fields.
    */
  private case class Spelling(
      text: Map[NVDisasm.Syntax.Field, String],
      names: Map[String, String],
      order: Vec[String],
      constants: Vec[(binding: String, offset: Long)],
      count: Int,
      predicates: Int,
      bank: Spelling.Bank,
      zero: Boolean = false
  ) derives CanEqual {
    def spell(field: NVDisasm.Syntax.Field, value: String): Spelling = copy(text = text.updated(field, value))

    def register(field: NVDisasm.Syntax.Field, prefix: String): Spelling = {
      val base = count * 4
      spell(field, s"$prefix$base").copy(
        names = names ++ (0 until 4).map(offset =>
          s"$prefix${base + offset}" -> (if (offset == 0) field.binding else s"${field.binding}+$offset")
        ),
        order = order :+ field.binding,
        count = count + 1,
        bank = bank match {
          case Spelling.Bank.Register => Spelling.Bank.Offset
          case Spelling.Bank.Offset   => Spelling.Bank.Indexed
          case other                  => other
        }
      )
    }

    /** `field` moved past every register spelt so far, to the first multiple of `count`. */
    def aligned(field: NVDisasm.Syntax.Field, count: Int): Option[Spelling] = text.get(field).collect {
      case Numbered(prefix, index) if index.toInt % count != 0 =>
        val used = names.keys.collect { case Numbered(`prefix`, other) => other.toInt + 1 }.maxOption.getOrElse(0)
        val base = used.alignUp(count)
        copy(
          text = text.updated(field, s"$prefix$base"),
          names = names.filterNot((_, binding) => binding.takeWhile(_ != '+') == field.binding) ++ (0 until count).map(
            offset => s"$prefix${base + offset}" -> (if (offset == 0) field.binding else s"${field.binding}+$offset")
          )
        )
    }
  }

  private val Variants  = 32
  private val Same      = "(?s).*\\b(\\w+) and (\\w+) must be the same.*".r
  private val Matching  = "(?s).*\\b(\\w+) needs to be equal to (\\w+)\\b.*".r
  private val Pair      = "(?s).*\\b(\\w+) should be 2n and (\\w+) should be \\w+\\+1\\b.*".r
  private val Adjacent  = "(?s).*requires (\\w+) and (\\w+) to be vector-consecutive.*".r
  private val Odd       = "(?s).*requires (\\w+) to be \\S+ vector-aligned \\(odd\\).*".r
  private val Combined  = "(?s).*Invalid combination of (\\w+), (\\w+)\\b(?!,).*".r
  private val Ignored   = "(?s).*requires that (\\w+)==RZ.*".r
  private val Aligned   = "(?s).*\\b(\\w+) and (\\w+) need to be vector-aligned.*".r
  private val Ambiguous = "(?s)ambiguous .*".r
  private val Unicast   = "(?s).*: Invalid instruction combination".r
  private val Negative  = "(?s).*if (\\w+) is non negative.*".r
  private val Zero      = "(?s).*if (\\w+) is not RZ.*".r
  private val TiedLast  = "(?s).*register must be the same as last input register.*".r
  private val Tied      = "(?s).*register must be the same as input register (\\d+).*".r
  private val Numbered  = "(U?R)(\\d+)".r

  /** Mnemonics whose plain immediate is an absolute code address. */
  private val AbsoluteTargets = Set("JMP", "JCAL", "CALL")

  /** Indirect transfers whose register and offset the disassembler separates with a space, and whose offset, written
    * out, tells the encodings with and without one apart.
    */
  private val Indirect = Set("RET", "CALL", "BRX", "BRXU", "JMX", "JMXU")

  /** `text` with the space between an indirect transfer's operands written as the comma assembly text expects. */
  private def separated(text: String): String = text.split(" ", 2) match {
    case Array(opcode, operands) if Indirect(opcode.takeWhile(_ != '.')) && !operands.contains(',') =>
      s"$opcode ${operands.trim.split("\\s+").mkString(", ")}"
    case _ => text
  }

  /** The operand text of a form's instances: registers four apart so no pair or quad overlaps the next, predicates
    * numbered in order, `0x1` immediates and constants of bank 3 at offsets `0x10`, `0x110` and on in operand order.
    * Operands with a default keep it. Code addresses are `0x20`, the final instruction of the function an instance
    * lowers in.
    */
  private def spelling(
      database: NVDisasm.Database,
      form: NVDisasm.Form,
      fields: Vec[NVDisasm.Syntax.Field],
      mnemonic: String
  ): Either[String, Spelling] =
    fields.foldLeft[Either[String, Spelling]](
      Right(Spelling(Map.empty, Map.empty, Vec.empty, Vec.empty, 0, 0, Spelling.Bank.Outside))
    ) { (spelt, field) =>
      spelt.flatMap { state =>
        def symbol(preferred: String*): Either[String, Spelling] = {
          val names = database.vocabulary(field).map(_.name)
          val known = names.toSet
          preferred.find(known.contains).orElse(names.headOption)
            .toRight(s"no spelling for operand ${field.kind}:${field.binding}").map(state.spell(field, _))
        }
        field.category match {
          case _ if field.binding == "Pg" || field.binding == "UPg" => Right(state)
          case _ if field.kind.startsWith("CBU_STATE")              => symbol("B1")
          case NVDisasm.FieldKind.ConstantBank                      =>
            symbol("c").map(_.copy(bank = Spelling.Bank.Number))
          case _ if field.kind == "CX"               => symbol("cx").map(_.copy(bank = Spelling.Bank.Register))
          case NVDisasm.FieldKind.DescriptorRegister => symbol("desc")
          case _ if field.kind == "TMA"              => symbol("desc")
          case kind if !kind.isOperand               => Right(state)
          case NVDisasm.FieldKind.ZeroRegister | NVDisasm.FieldKind.ZeroUniformRegister =>
            symbol("RZ", "URZ").map(_.copy(zero = true))
          case NVDisasm.FieldKind.UniformRegister | NVDisasm.FieldKind.NonZeroUniformRegister =>
            Right(state.register(field, "UR"))
          case kind if kind.isRegister                                   => Right(state.register(field, "R"))
          case kind if kind.isPredicateOperand && field.default.nonEmpty => Right(state)
          case kind if kind.isPredicateOperand                           =>
            val prefix = if (kind == NVDisasm.FieldKind.UniformPredicate) "UP" else "P"
            val name   = s"$prefix${state.predicates}"
            Right(state.spell(field, name).copy(
              names = state.names.updated(name, field.binding),
              order = state.order :+ field.binding,
              predicates = state.predicates + 1
            ))
          case NVDisasm.FieldKind.Immediate | NVDisasm.FieldKind.SignedImmediate
              if state.bank == Spelling.Bank.Number =>
            Right(state.spell(field, s"0x${InstructionSemantics.ConstantBank}").copy(bank = Spelling.Bank.Offset))
          case NVDisasm.FieldKind.Immediate | NVDisasm.FieldKind.SignedImmediate
              if state.bank == Spelling.Bank.Offset =>
            val binding = form.encodedBinding(field)
            val offset  = 0x10L + 0x100L * state.constants.size
            Right(state.spell(field, s"0x${offset.toHexString}").copy(
              order = state.order :+ binding,
              constants = state.constants :+ (binding, offset),
              bank = Spelling.Bank.Outside
            ))
          case NVDisasm.FieldKind.Immediate | NVDisasm.FieldKind.SignedImmediate
              if (field.default.nonEmpty && Indirect(mnemonic) && mnemonic.endsWith("U")) || mnemonic == "RET" =>
            Right(state.spell(field, "0x0").copy(bank = Spelling.Bank.Outside))
          // an address of a zero register alone still spells its offset: `[RZ+0x10]`
          case NVDisasm.FieldKind.Immediate | NVDisasm.FieldKind.SignedImmediate
              if field.default.nonEmpty && state.zero =>
            Right(state.spell(field, "0x10").copy(bank = Spelling.Bank.Outside, zero = false))
          case NVDisasm.FieldKind.Immediate | NVDisasm.FieldKind.SignedImmediate if field.default.nonEmpty =>
            Right(state.copy(bank = Spelling.Bank.Outside))
          case NVDisasm.FieldKind.Immediate | NVDisasm.FieldKind.SignedImmediate =>
            Right(state.spell(field, if (AbsoluteTargets(mnemonic)) "0x20" else "0x1").copy(bank =
              Spelling.Bank.Outside
            ))
          case NVDisasm.FieldKind.RelativeImmediate                       => Right(state.spell(field, "0x20"))
          case kind if kind.isFloatingImmediate && field.default.nonEmpty => Right(state)
          case kind if kind.isFloatingImmediate                           => Right(state.spell(field, "1"))
          // CS2R and CS2UR read the counters, as a pair unless `.32`
          case NVDisasm.FieldKind.SpecialRegister if form.opcodes.exists(_.name.startsWith("CS2")) =>
            symbol("SR_CLOCKLO", "SR_LANEID")
          case NVDisasm.FieldKind.SpecialRegister               => symbol("SR_LANEID")
          case NVDisasm.FieldKind.PredicateRegister             => symbol("PR")
          case kind if kind.isOperand && field.default.nonEmpty => Right(state)
          case _                                                => symbol()
        }
      }.map(next =>
        if (
          next.zero && field.category.isOperand &&
          !PartialFunction.cond(field.category) {
            case NVDisasm.FieldKind.ZeroRegister | NVDisasm.FieldKind.ZeroUniformRegister => true
          }
        )
          next.copy(zero = false)
        else next
      )
    }

  def immediate(field: NVDisasm.Syntax.Field): Boolean =
    field.category == NVDisasm.FieldKind.Immediate || field.category == NVDisasm.FieldKind.SignedImmediate

  /** The immediate operand a failed condition sets, and the value it requires, where the condition equates one with a
    * number, as `.16864 with 8-bit data requires id2 == 0` does.
    */
  private def forced(form: NVDisasm.Form, fields: Vec[NVDisasm.Syntax.Field], failure: String)
      : Option[(NVDisasm.Syntax.Field, String)] = {
    import ConditionExpression.*
    @tailrec
    def equated(expression: ConditionExpression): Option[(String, BigInt)] = expression match {
      case Binary(BinaryOperator.Equal, Field(name, None), Number(value)) => Some((name, value))
      case Binary(BinaryOperator.Implies, _, consequence)                 => equated(consequence)
      case _                                                              => None
    }
    form.conditions.filter(condition => failure.endsWith(condition.message)).flatMap(condition =>
      equated(condition.expression)
    ).flatMap((name, value) =>
      fields.find(field => field.binding == name && immediate(field)).map(_ -> s"0x${value.toString(16)}")
    ).headOption
  }

  /** Every instance of a form, under `mnemonic`: every opcode spelling and value of each required modifier together,
    * with one optional modifier where the encoding accepts none of those alone, then each value of each optional
    * modifier alone, as far as the encoding accepts them and up to a bound; or why none can be written.
    */
  def instances(name: Option[String], mnemonic: String, formName: String): Either[String, Vec[Instance]] = for {
    target <- target(name)
    database = target.database
    form <- database.byForm.get(formName).toRight(s"no form $formName")
    fields = NVDisasm.Syntax.fields(form.syntax)
    spelt <- spelling(database, form, fields, mnemonic)
    opcodes  = form.opcodes.map(_.name).filter(_.takeWhile(_ != '.') == mnemonic).distinct
    choices  = SASSModifiers.of(database, fields)
    required = choices.filter(_.required)
    found <- {
      def attempt(operands: Spelling): Either[Vec[String], Vec[(String, Written)]] = {
        val failures = mutable.LinkedHashSet.empty[String]
        val written  = mutable.HashMap.empty[(String, Map[NVDisasm.Syntax.Field, String]), Option[(String, Written)]]
        // a combination whose encoding forces an immediate, such as a sparsity selector of zero, takes that value
        def render(
            opcode: String,
            chosen: Map[NVDisasm.Syntax.Field, String],
            operands: Spelling
        ): Option[(String, Written)] = {
          // a condition-code test is written on the CC operand, which otherwise keeps its default
          def spell(field: NVDisasm.Syntax.Field) = operands.text.get(field).orElse(chosen.get(field)).orElse(
            Option.when(field.kind == "CC" && field.default.nonEmpty && chosen.keys.exists(_.kind == "Test"))("CC")
          )
          FormatParser.render(form, opcode, spell(_)).flatMap(FormatParser.parse(database, form, 0, _))
            .flatMap(_ => FormatParser.render(form, opcode, spell(_), braced = false))
            .fold(
              failure =>
                forced(form, fields, failure).filterNot((field, value) => operands.text.get(field).contains(value))
                  .flatMap((field, value) => render(opcode, chosen, operands.spell(field, value)))
                  .orElse {
                    failures += failure
                    None
                  },
              found =>
                Some((found, Written(target.sm, formName, opcode, fields.map(spell(_).getOrElse("")))))
            )
        }
        def text(opcode: String, chosen: Map[NVDisasm.Syntax.Field, String]): Option[(String, Written)] =
          written.getOrElseUpdate((opcode, chosen), render(opcode, chosen, operands))
        val product = LazyList.from(opcodes).flatMap(opcode =>
          required.foldLeft(LazyList(Map.empty[NVDisasm.Syntax.Field, String]))((partial, choice) =>
            partial.flatMap(chosen => choice.values.map(value => chosen.updated(choice.field, value)))
          ).map(opcode -> _)
        )
        // where no combination of the required modifiers is legal alone, each takes one optional modifier as well
        val optional = choices.filterNot(_.required)
        val combined = product.take(SASSModifiers.Attempts)
          .flatMap((opcode, chosen) => text(opcode, chosen).map(found => (opcode, chosen, found))).take(
            Variants
          ).toVec match {
          case Vec() =>
            product.take(SASSModifiers.Attempts).flatMap((opcode, chosen) =>
              optional.view.flatMap(choice =>
                choice.values.view.map(chosen.updated(choice.field, _))
                  .flatMap(extended => text(opcode, extended).map(found => (opcode, extended, found)))
              ).headOption
            ).take(Variants).toVec
          case found => found
        }
        val alone = combined.headOption.toVec.flatMap { (opcode, chosen, _) =>
          opcodes.flatMap(other => text(other, chosen)) ++ choices.flatMap(choice =>
            choice.values.flatMap(value => text(opcode, chosen.updated(choice.field, value)))
          )
        }
        val found = (alone ++ combined.map(_._3)).distinctBy(_._1).take(Variants * 2)
        Either.cond(found.nonEmpty, found, Vec.from(failures))
      }
      // An accumulating form writes the register one of its inputs reads.
      // Inputs count every written operand after the destination, immediates included.
      def tie(
          current: Spelling,
          input: Vec[NVDisasm.Syntax.Field] => Option[NVDisasm.Syntax.Field]
      ): Option[Spelling] = {
        val written = fields.filter(field => field.category.isOperand && current.text.contains(field))
        for {
          destination <- written.find(_.category.isRegister)
          tied        <- input(written.dropWhile(_ != destination).drop(1)).filter(_.category.isRegister)
          text        <- current.text.get(destination)
        } yield current.spell(tied, text)
      }
      // Some encodings constrain operands: two must be the same register, or one must be negated or zero.
      def adjusted(current: Spelling, failures: Vec[String]): Vec[Spelling] = {
        def respell(binding: String)(change: String => String) =
          fields.find(_.binding == binding).flatMap(field => current.text.get(field).map(text => (field, change(text))))
        def same(first: String, second: String) = fields.find(_.binding == first).flatMap(current.text.get).map(text =>
          fields.filter(_.binding == second).foldLeft(current)(_.spell(_, text))
        )
        // the register after `binding`'s, for operands that must form a pair
        def next(binding: String, target: String) =
          fields.find(_.binding == binding).flatMap(current.text.get).collect {
            case Numbered(bank, index) => s"$bank${index.toInt + 1}"
          }.flatMap(text => fields.find(_.binding == target).map(current.spell(_, text)))
        lazy val unspelt =
          fields.filter(field => immediate(field) && field.default.nonEmpty && !current.text.contains(field))
        failures.collectFirst {
          case Same(first, second)     => same(first, second).toVec
          case Matching(first, second) => same(first, second).toVec
          // the tcgen05 products take the instruction descriptor in the register after the metadata address
          case Combined("URe", "URh")  => next("URe", "URh").toVec
          case Combined(first, second) => tabled(current, first, second) ++ next(second, first).toVec
          // UTCBAR names a multicast mask only under .MULTICAST
          case Unicast() if mnemonic == "UTCBAR" => respell("URc")(_ => "URZ").map(current.spell(_, _)).toVec
          case Pair(first, second)               => next(first, second).toVec
          case Adjacent(first, second)           => next(first, second).toVec
          case Odd(binding)                      => next("Rb", binding).orElse(next(binding, binding)).toVec
          case Aligned(first, second)            => next(first, second).toVec
          // an operand left at its default can read as either of two encodings: spell its immediates out
          case Ambiguous() =>
            Vec(unspelt.foldLeft(current)(_.spell(_, "0x1"))).filter(_ != current)
          case Negative(binding) => respell(binding)(text => s"-$text").map(current.spell(_, _)).toVec
          case Zero(binding)     =>
            respell(binding)(text => s"${text.takeWhile(_ == '-')}RZ").map(current.spell(_, _)).toVec
          case Ignored(binding) => respell(binding)(_ => "RZ").map(current.spell(_, _)).toVec
          case TiedLast()       => tie(current, inputs => inputs.findLast(_.category.isRegister)).toVec
          case Tied(index)      => tie(current, _.lift(index.toInt - 1)).toVec
          // a register group the encoding aligns, such as the eight-register lane mask of a 2CTA product
          case failure if alignment(failure).nonEmpty =>
            alignment(failure).flatMap((binding, count) =>
              fields.find(_.binding == binding).flatMap(current.aligned(_, count))
            ).toVec
        }.getOrElse(Vec.empty)
      }
      def alignment(failure: String): Option[(String, Int)] =
        form.conditions.filter(condition => failure.endsWith(condition.message)).flatMap(condition =>
          modulus(condition.expression)
        ).headOption
      // a register pair the encoding takes from a table of legal combinations: the row for either register
      def tabled(current: Spelling, first: String, second: String): Vec[Spelling] = {
        val rows = form.conditions.map(_.expression).collectFirst {
          case ConditionExpression.Call(
                BuiltinCall.Defined,
                Vec(
                  ConditionExpression.Call(
                    BuiltinCall.Table(table),
                    Vec(
                      ConditionExpression.Field(`first`, None),
                      ConditionExpression.Field(`second`, None)
                    )
                  )
                )
              ) => table
        }.flatMap(database.byTable.get).fold(Vec.empty[NVDisasm.Row])(_.rows)
        val pair                                    = Vec(first, second)
        def anchored(anchor: Int): Option[Spelling] = for {
          bound         <- fields.find(_.binding == pair(anchor)).flatMap(current.text.get)
          (bank, index) <- bound match {
            case Numbered(bank, index) => Some((bank, BigInt(index)))
            case _                     => None
          }
          row   <- rows.find(_.inputs.lift(anchor).contains(index))
          other <- row.inputs.lift(1 - anchor)
          field <- fields.find(_.binding == pair(1 - anchor))
        } yield current.spell(field, s"$bank$other")
        Vec(anchored(0), anchored(1)).flatten
      }
      // the first adjustment that leads to instances, or the failures of the deepest spelling the first one leads to
      def search(current: Spelling, rounds: Int): Either[Vec[String], (Spelling, Vec[(String, Written)])] =
        attempt(current).map(current -> _).left.flatMap { failures =>
          val tried = LazyList.from(if (rounds > 0) adjusted(current, failures) else Vec.empty[Spelling])
            .map(search(_, rounds - 1))
          tried.collectFirst { case found @ Right(_) => found }.orElse(tried.headOption).getOrElse(Left(failures))
        }
      search(spelt, 3).left.map(_.headOption.getOrElse(s"$formName has no opcode under $mnemonic"))
    }
  } yield found._2.map((text, written) =>
    (target.sm, text, found._1.names, found._1.order, found._1.constants, written)
  )

  /** The register and count of a condition that the register's index, URZ counting as one past it, is a multiple of the
    * count.
    */
  @tailrec
  private def modulus(expression: ConditionExpression): Option[(String, Int)] = {
    import BinaryOperator.*
    import ConditionExpression.*
    expression match {
      case Binary(Equal, Binary(Remainder, Binary(Add, Field(name, None), _), Number(count)), Number(zero))
          if zero == 0 && count > 1 =>
        Some(name -> count.toInt)
      case Binary(Implies, _, consequence) => modulus(consequence)
      case _                               => None
    }
  }

  private def render(expression: Expression): String = expression match {
    case Expression.Constant(value, _)                               => value.toString
    case Expression.Reference(name, attribute, _)                    => NVDisasm.bindingKey(name, attribute)
    case Expression.Scale(value, factor)                             => s"scale(${render(value)}, $factor)"
    case Expression.Multiply(value, factor)                          => s"${render(value)} * $factor"
    case Expression.Identity(first, second)                          => s"identity(${render(first)}, ${render(second)})"
    case Expression.FixedFloat(value, format)                        => s"${render(value)} as $format"
    case Expression.Relaxed(value)                                   => render(value)
    case Expression.FloatConvert(value = value, selector = selector) => s"float(${render(value)}, $selector)"
    case Expression.Lookup(name, arguments) => s"$name(${arguments.map(render(_)).mkString(", ")})"
  }

  @tailrec
  private def label(expression: Expression): String = expression match {
    case Expression.Constant(_, _)                => "const"
    case Expression.Reference(name, attribute, _) => NVDisasm.bindingKey(name, attribute)
    case Expression.Relaxed(value)                => label(value)
    case Expression.Scale(value, _)               => label(value)
    case Expression.Multiply(value, _)            => label(value)
    case Expression.FixedFloat(value, _)          => label(value)
    case Expression.FloatConvert(value = value)   => label(value)
    case Expression.Identity(first, _)            => label(first)
    case Expression.Lookup(name, _)               => name
  }

  private val Whitespace = "\\s+".r

  private def tokens(nodes: Vec[NVDisasm.Syntax]): Vec[View.Token] = nodes.flatMap {
    case NVDisasm.Syntax.Text(raw)    => Vec(View.Token.Text(Whitespace.replaceAllIn(raw, " ")))
    case NVDisasm.Syntax.Literal(raw) => Vec(View.Token.Text(raw))
    case field: NVDisasm.Syntax.Field =>
      val default = field.default.fold("")(value => s"($value)")
      Vec(View.Token.Slot(s"${field.kind}$default:${field.binding}", field.category.toString))
    case NVDisasm.Syntax.Group(open, close, children) => (View.Token.Text(open) +: tokens(children)) :+
        View.Token.Text(close)
    case NVDisasm.Syntax.Optional(marker, children) =>
      (View.Token.Text(s"[${marker.getOrElse("")}") +: tokens(children)) :+ View.Token.Text("]")
  }

  private def form(database: NVDisasm.Database, form: NVDisasm.Form): View.Form = {
    val fields   = NVDisasm.Syntax.fields(form.syntax, includeMarkedOptionals = true)
    val operands = fields.filter(_.category.isOperand).map(field =>
      View.OperandSlot(field.binding, field.kind, field.default.fold("")(value => s"default $value"))
    )
    val choices = fields.filterNot(_.category.isOperand).flatMap(field =>
      Option.when(database.vocabulary(field).nonEmpty)(
        View.Choice(s"${field.kind}:${field.binding}", database.vocabulary(field).map(_.name).distinct)
      )
    )
    val opcodes = form.opcodes.map(opcode => s"${opcode.name} = 0x${opcode.value.toString(16)}").distinct
    val opcode  = form.opcodes.map(_.value).distinct match {
      case Vec(value) =>
        form.opcodeFields.map(field => View.deposit(field.positions, value)).foldLeft(Map.empty[Int, Boolean])(_ ++ _)
      case _ => Map.empty[Int, Boolean]
    }
    val constants = form.fields.collect {
      case NVDisasm.Assignment(_, positions, Expression.Constant(value, _)) =>
        View.deposit(positions, value)
    }.foldLeft(Map.empty[Int, Boolean])(_ ++ _)
    View.Form(
      form.name,
      tokens(form.syntax),
      operands,
      choices,
      Vec("Opcode" -> opcodes.mkString(", ")) ++ Option.when(form.alternate)("Alternate" -> "yes").toVec,
      form.conditions.map(_.message).distinct,
      Some(View.Encoding(
        database.width,
        form.fields.map(field =>
          View.Field(label(field.expression), field.positions, s"${field.name} = ${render(field.expression)}")
        ),
        constants ++ opcode
      ))
    )
  }

  private val files = Vec(
    "Register"         -> "General purpose registers (R), 32 bits per thread; RZ reads as zero.",
    "UniformRegister"  -> "Uniform registers (UR), one value per warp; URZ reads as zero.",
    "Predicate"        -> "Predicate registers; PT is constant true.",
    "UniformPredicate" -> "Uniform predicate registers; UPT is constant true.",
    "BD"               -> "Convergence barrier registers (B).",
    "SpecialRegister"  -> "Special registers read with S2R and CS2R."
  )

  def registers(name: Option[String]): Vec[View.RegisterFile] = target(name).fold(
    _ => Vec.empty,
    target => {
      val database = target.database
      files.flatMap { (vocabulary, description) =>
        database.byVocabulary.get(vocabulary).map { symbols =>
          val named = symbols.values.filterNot(symbol =>
            vocabulary == "SpecialRegister" && symbol.name.startsWith("SR") && symbol.name.drop(2).forall(_.isDigit)
          )
          View.RegisterFile(
            vocabulary,
            description,
            named.sortBy(symbol => (symbol.value, symbol.name)).map(symbol =>
              View.Register(symbol.name, Some(symbol.value))
            )
          )
        }
      }
    }
  )

  val binary: Boolean = true

  def assemble(source: String, name: Option[String]): Either[String, String] = for {
    target  <- target(name)
    program <- Parser.parseProgram(source).left.map(_.message)
    bytes   <- Assembler(target).encode(program).left.map(_.message)
  } yield Hex.render(bytes)

  def disassemble(bytes: Vec[Byte], name: Option[String]): Either[String, String] = for {
    target  <- target(name)
    program <- Disassembler(target).decode(bytes).left.map(_.message)
    text    <- Printer.print(program).left.map(_.message)
  } yield text

  val assemblyExample: String = "IADD3 R7, R7, 0x1, RZ;\nEXIT;\n"
  val binaryExample: String   = "/* 0x000000000000794d */ /* 0x000fea0003800000 */\n"

  /** The definition an instance of the form lowers to, under the operand table's names in its operand order. */
  /** A field's value as the instance encodes it, or a term over the offset fields, which are parameters. */
  private enum Partial {
    case Known(value: BigInt)
    case Symbolic(term: Term, boolean: Boolean)
  }

  /** `expression` with every reference but those to `parameters` evaluated under `bindings`, or why it cannot be. */
  private def partial(
      database: NVDisasm.Database,
      bindings: Map[String, BigInt],
      parameters: Set[String],
      expression: ConditionExpression
  ): Either[String, Partial] = {
    import BinaryOperator.*
    import ConditionExpression.*
    def references(expression: ConditionExpression): Boolean = expression match {
      case Field(name, attribute)          => parameters(NVDisasm.bindingKey(name, attribute))
      case Unary(_, value)                 => references(value)
      case Binary(_, left, right)          => references(left) || references(right)
      case Call(_, arguments)              => arguments.exists(references(_))
      case Conditional(condition, yes, no) => references(condition) || references(yes) || references(no)
      case _                               => false
    }
    def term(value: Partial): Term = value match {
      case Partial.Known(known)       => Term.Literal(known.toLong)
      case Partial.Symbolic(found, _) => found
    }
    def truth(value: Partial): Term = value match {
      case Partial.Symbolic(found, false) => Term.Binary("!=", found, Term.Literal(0L))
      case other                          => term(other)
    }
    def go(expression: ConditionExpression): Either[String, Partial] =
      if (!references(expression)) Legality.evaluate(database, expression, bindings).map(Partial.Known(_))
      else expression match {
        case Field(name, attribute) =>
          Right(Partial.Symbolic(Term.Variable(NVDisasm.bindingKey(name, attribute)), false))
        case Unary(UnaryOperator.LogicalNot, value) =>
          go(value).map(found => Partial.Symbolic(Term.Unary("~", truth(found)), true))
        case Binary(operator @ (LogicalAnd | LogicalOr | Implies), left, right) =>
          go(left).flatMap {
            case Partial.Known(known) =>
              operator match {
                case LogicalAnd if known == 0 => Right(Partial.Known(0))
                case LogicalOr if known != 0  => Right(Partial.Known(1))
                case Implies if known == 0    => Right(Partial.Known(1))
                case _                        => go(right)
              }
            case symbolic =>
              go(right).map {
                case Partial.Known(known) =>
                  operator match {
                    case LogicalAnd => if (known == 0) Partial.Known(0) else symbolic
                    case LogicalOr  => if (known != 0) Partial.Known(1) else symbolic
                    case _          =>
                      if (known != 0) Partial.Known(1) else Partial.Symbolic(Term.Unary("~", truth(symbolic)), true)
                  }
                case other =>
                  operator match {
                    case LogicalAnd => Partial.Symbolic(Term.Binary("&", truth(symbolic), truth(other)), true)
                    case LogicalOr  => Partial.Symbolic(Term.Binary("|", truth(symbolic), truth(other)), true)
                    case _          =>
                      Partial.Symbolic(Term.Binary("|", Term.Unary("~", truth(symbolic)), truth(other)), true)
                  }
              }
          }
        case Binary(operator, left, right) =>
          for {
            l     <- go(left)
            r     <- go(right)
            found <- operator match {
              case Equal        => Right(Term.Binary("==", term(l), term(r)) -> true)
              case NotEqual     => Right(Term.Binary("!=", term(l), term(r)) -> true)
              case Less         => Right(Term.Binary("<s", term(l), term(r)) -> true)
              case LessEqual    => Right(Term.Binary("<=s", term(l), term(r)) -> true)
              case Greater      => Right(Term.Binary("<s", term(r), term(l)) -> true)
              case GreaterEqual => Right(Term.Binary("<=s", term(r), term(l)) -> true)
              case Add          => Right(Term.Binary("+", term(l), term(r)) -> false)
              case Subtract     => Right(Term.Binary("-", term(l), term(r)) -> false)
              case Multiply     => Right(Term.Binary("*", term(l), term(r)) -> false)
              case BitwiseAnd   => Right(Term.Binary("&", term(l), term(r)) -> false)
              case BitwiseOr    => Right(Term.Binary("|", term(l), term(r)) -> false)
              case BitwiseXor   => Right(Term.Binary("^", term(l), term(r)) -> false)
              case Remainder    => Right(Term.Binary("%s", term(l), term(r)) -> false)
              case Divide       => Right(Term.Binary("/s", term(l), term(r)) -> false)
              case ShiftLeft    => Right(Term.Binary("<<", term(l), term(r)) -> false)
              case ShiftRight   => Right(Term.Binary(">>s", term(l), term(r)) -> false)
              case other        => Left(s"unsupported condition operator $other")
            }
          } yield Partial.Symbolic(found._1, found._2)
        case Conditional(condition, whenTrue, whenFalse) =>
          go(condition).flatMap {
            case Partial.Known(known) => go(if (known != 0) whenTrue else whenFalse)
            case _                    => Left("unsupported condition on an operand field")
          }
        case other => Left(s"unsupported condition $other")
      }
    go(expression)
  }

  /** The values `field` holds where `requirement` holds of it alone: a union of intervals within `low` to `high`. */
  private def intervals(requirement: Term, field: String, low: Long, high: Long): Option[Vec[(Long, Long)]] = {
    def complement(found: Vec[(Long, Long)]): Vec[(Long, Long)] = {
      val bounded = found :+ (high + 1, high + 1)
      val starts  = bounded.scanLeft(low)((next, interval) => math.max(next, interval._2 + 1))
      bounded.zip(starts).collect { case ((from, _), next) if from > next => (next, from - 1) }
    }
    def union(
        left: Vec[(Long, Long)],
        right: Vec[(Long, Long)]
    ): Vec[(Long, Long)] = {
      val merged = mutable.ArrayBuffer.empty[(Long, Long)]
      (left ++ right).sortBy(_._1).foreach { interval =>
        merged.lastOption match {
          case Some((from, to)) if interval._1 <= to + 1 =>
            merged(merged.length - 1) = (from, math.max(to, interval._2))
          case _ => merged.append(interval)
        }
      }
      Vec.from(merged)
    }
    def clip(from: Long, to: Long): Vec[(Long, Long)] =
      Option.when(math.max(from, low) <= math.min(to, high))((math.max(from, low), math.min(to, high))).toVec
    requirement match {
      case Term.Binary("<s", Term.Variable(`field`), Term.Literal(bound))  => Some(clip(low, bound - 1))
      case Term.Binary("<s", Term.Literal(bound), Term.Variable(`field`))  => Some(clip(bound + 1, high))
      case Term.Binary("<=s", Term.Variable(`field`), Term.Literal(bound)) => Some(clip(low, bound))
      case Term.Binary("<=s", Term.Literal(bound), Term.Variable(`field`)) => Some(clip(bound, high))
      case Term.Binary("==", Term.Variable(`field`), Term.Literal(value))  => Some(clip(value, value))
      case Term.Binary("!=", Term.Variable(`field`), Term.Literal(value))  => Some(complement(clip(value, value)))
      case Term.Binary("&", left, right)                                   =>
        for {
          l <- intervals(left, field, low, high)
          r <- intervals(right, field, low, high)
        } yield complement(union(complement(l), complement(r)))
      case Term.Binary("|", left, right) =>
        for {
          l <- intervals(left, field, low, high)
          r <- intervals(right, field, low, high)
        } yield union(l, r)
      case Term.Unary("~", value) => intervals(value, field, low, high).map(complement(_))
      case _                      => None
    }
  }

  /** The type of an offset field: an integer of its syntax width, narrowed to the values its encoding holds. */
  private def fieldType(form: NVDisasm.Form, binding: String): Option[(base: Type, low: Long, high: Long)] =
    form.declared.find(field => form.encodedBinding(field) == binding).flatMap { field =>
      field.arguments.flatMap(_.takeWhile(_.isDigit).toIntOption).filter(width => width > 0 && width <= 64).map {
        width =>
          val signed      = field.category == NVDisasm.FieldKind.SignedImmediate
          val (low, high) =
            if (signed) (-(1L << (width - 1)), (1L << (width - 1)) - 1L)
            else (0L, if (width == 64) Long.MaxValue else (1L << width) - 1L)
          val encoded = form.fields.collect {
            case NVDisasm.Assignment(_, positions, Expression.Reference(`binding`, None, _)) =>
              (0L, (1L << positions.size) - 1L)
            case NVDisasm.Assignment(
                  _,
                  positions,
                  Expression.Scale(Expression.Reference(`binding`, None, _), factor)
                ) =>
              (0L, ((1L << positions.size) - 1L) * factor.toLong)
          }
          (
            Type.Integer(width.unsafe[Int in 1 ~ 64], signed),
            encoded.map(_._1).foldLeft(low)(math.max),
            encoded.map(_._2).foldLeft(high)(math.min)
          )
      }
    }

  /** The type of each of an instance's offset fields and the requirements its encoding places on them: a condition that
    * bounds one field alone narrows its type, and the others, evaluated for every other field the instance fixes, must
    * hold. Conditions on the fixed fields alone hold for the instance, such as each register's range. An instance the
    * encoding tables do not parse reads its constants at fixed offsets.
    */
  private def encoding(
      database: NVDisasm.Database,
      form: NVDisasm.Form,
      text: String,
      fields: Vec[String]
  ): (types: Map[String, Type], requirements: Vec[Term]) =
    if (fields.isEmpty) (Map.empty, Vec.empty)
    else FormatParser.parse(database, form, 0, text).map(Legality.completeBindings(form, _)).fold(
      _ => (Map.empty, Vec.empty),
      { bindings =>
        val parameters = fields.toSet
        val domains    = fields.flatMap(field => fieldType(form, field).map(field -> _)).toMap
        val conditions = form.conditions.flatMap(condition =>
          partial(database, bindings -- parameters, parameters, condition.expression).toOption.collect {
            case Partial.Symbolic(term, _) => term
          }
        )
        val (bounds, requirements) = conditions.partition(condition =>
          domains.exists((field, domain) => intervals(condition, field, domain.low, domain.high).nonEmpty)
        )
        val types = domains.map { (field, domain) =>
          val allowed = bounds.flatMap(intervals(_, field, domain.low, domain.high)).foldLeft(
            Vec((domain.low, domain.high))
          )((current, bound) =>
            current.flatMap((from, to) =>
              bound.flatMap((low, high) =>
                Option.when(math.max(from, low) <= math.min(to, high))((math.max(from, low), math.min(to, high)))
              )
            )
          )
          field -> Type.Ranged(domain.base, allowed.map((low, high) => (low, high)))
        }
        (types, requirements)
      }
    )

  /** The definition an instance lowers to, under the operand table's names in its operand order, with its constant
    * operands' offsets as parameters.
    */
  def definition(instance: Instance, mnemonic: String, formName: String): Either[Unrendered, Definition] = {
    val (sm, instruction, operands, order, constants, _) = instance
    for {
      found <- (for {
        target   <- sm.refine[Int >= 35].left.map(_ => s"no SASS target sm_$sm")
        selected <- Target.parse(s"sm_$sm")
        form     <- selected.database.byForm.get(formName).toRight(s"no form $formName")
        program  <- Parser.parseProgram(s"${separated(instruction)};").left.map(_.message)
        parsed   <- program.statements.collectFirst { case Program.Statement.Operation(operation, _, _) => operation }
          .toRight(s"$instruction is not an instruction")
      } yield (target, selected, form, parsed)).left.map(Unrendered.Rejected(_))
      (target, selected, form, parsed) = found
      fields                           = encoding(selected.database, form, instruction, constants.map(_.binding))
      definition <- InstructionSemantics.describe(
        mnemonic,
        target,
        parsed,
        operands,
        constants.flatMap((binding, offset) => fields.types.get(binding).map(tpe => offset -> (binding, tpe))).toMap,
        order,
        fields.requirements
      )
    } yield {
      // an offset the definition does not read, as where a comparison ignores its sources, constrains nothing
      val parameters = definition.parameters.map(_.name).toSet
      definition.copy(requirements = definition.requirements.filter(mentions(_).subsetOf(parameters)))
    }
  }

  private def mentions(term: Term): Set[String] = term match {
    case Term.Variable(name)                         => Set(name)
    case Term.Unary(_, value)                        => mentions(value)
    case Term.Binary(_, left, right)                 => mentions(left) ++ mentions(right)
    case Term.Call(_, arguments)                     => arguments.toSet.flatMap(mentions(_))
    case Term.Select(condition, whenTrue, whenFalse) =>
      mentions(condition) ++ mentions(whenTrue) ++ mentions(whenFalse)
    case _ => Set.empty
  }

  protected def render(entry: View.Entry, form: View.Form): Either[Unavailable, Vec[Described]] = {
    val results = instances(entry.target, entry.mnemonic, form.name).map(
      _.map(instance => instance -> definition(instance, entry.mnemonic, form.name))
    )
    val defined = results.toOption.toVec.flatten.flatMap((instance, result) =>
      result.toOption.map(Described(instance.text, _))
    )
    Either.cond(
      defined.nonEmpty,
      defined,
      SASSReasons.unavailable(
        entry,
        form,
        target(entry.target).toOption.map(_.database),
        results.map(_.flatMap(_._2.left.toOption))
      )
    )
  }
}
