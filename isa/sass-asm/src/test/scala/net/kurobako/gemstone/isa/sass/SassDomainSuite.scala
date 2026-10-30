package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.isa.catalogue.*
import net.kurobako.gemstone.isa.sass.asm.*
import net.kurobako.gemstone.testing.Suite
import scala.collection.mutable
import scala.concurrent.duration.*

class SassDomainSuite extends Suite {
  // walks the operand domains of every distinct SASS table
  override def baseTimeout: FiniteDuration = 60.seconds

  private val tables: Vec[Target] = Vec.from(
    (35 to 130).flatMap(sm => Target.parse(sm.toString).toOption).groupBy(_.database.digest).values.map(_.minBy(_.sm))
      .toVector.sortBy(_.sm.raw)
  )

  private def forms(target: Target, mnemonic: String): Vec[NVDisasm.Form] = target.database.forms.filter(form =>
    !form.alternate && form.opcodes.exists(_.name.takeWhile(_ != '.') == mnemonic)
  )

  private def domain(target: Target, mnemonic: String, binding: String): FieldDomain =
    forms(target, mnemonic).flatMap(form => SassDomains.fields(target.database, form))
      .collectFirst { case (name, found) if name == binding => found }
      .getOrElse(fail(s"${target.name} $mnemonic has no $binding field"))

  private def bytes(text: String, target: Target): Array[Byte] =
    Assembler.assemble(text, target).fold(error => fail(s"${target.name} $text: $error"), identity)

  private def printed(code: Array[Byte], target: Target): String =
    InstructionDecoder.decode(0, code, target).fold(
      fail(_),
      decoded => Printer.print(decoded.operation).fold(fail(_), identity)
    )

  private def parsed(
      target: Target,
      mnemonic: String,
      text: String
  ): Vec[(NVDisasm.Form, Either[String, Map[String, BigInt]])] =
    forms(target, mnemonic).filter(_.opcodes.forall(_.name == mnemonic))
      .map(form => form -> FormatParser.parse(target.database, form, 0, text))

  private def limitsRegisters(expression: ConditionExpression): Boolean = expression match {
    case ConditionExpression.Parameter(name)        => name.endsWith("REG_COUNT")
    case ConditionExpression.Binary(_, left, right) => limitsRegisters(left) || limitsRegisters(right)
    case _                                          => false
  }

  private def refused(form: NVDisasm.Form, message: String): Boolean =
    form.conditions.exists(condition => message == s"illegal ${form.name}: ${condition.message}")

  test("the tables describe sixteen target descriptions") {
    assertEquals(tables.size, 16)
  }

  test("register bank capacities come from the largest vocabulary of each bank") {
    assertEquals(
      Vec(RegisterBank.R, RegisterBank.UR, RegisterBank.B, RegisterBank.SB).map(RegisterBank.capacity),
      Vec(Some(256), Some(256), Some(64), Some(6))
    )
    assertEquals(RegisterBank.capacity(RegisterBank.fromSpelling("gsb")), None)
  }

  test("every domain value is a value of the vocabulary of its field kind") {
    val checked = mutable.HashSet.empty[(String, FieldDomain)]
    tables.foreach { target =>
      val database     = target.database
      val vocabularies = database.byVocabulary.map { (label, vocabulary) =>
        label -> vocabulary.values.map(symbol => symbol.name -> symbol.value).toSet
      }
      database.forms.foreach { form =>
        SassDomains.fields(database, form).foreach { (binding, domain) =>
          vocabularies.get(domain.label).foreach { known =>
            if (checked.add(database.digest -> domain))
              domain.values.foreach(value =>
                assert(known(value), s"${target.name} ${form.name}.$binding: ${value._1} is not in ${domain.label}")
              )
          }
        }
      }
    }
  }

  private def registerBounds(form: NVDisasm.Form): Map[String, Vec[ConditionExpression.Binary]] =
    form.conditions.collect {
      case NVDisasm.Condition(
            bound @ ConditionExpression.Binary(
              BinaryOperator.LogicalOr,
              ConditionExpression.Binary(BinaryOperator.Equal, ConditionExpression.Field(binding, None), _),
              rest
            ),
            _,
            _
          ) if limitsRegisters(rest) => binding -> bound
    }.groupMap(_._1)(_._2)

  test("a register bound in the legality conditions is the bound of its domain") {
    val checked = mutable.HashSet.empty[(String, String, FieldDomain, Vec[ConditionExpression.Binary])]
    tables.foreach { target =>
      val database = target.database
      database.forms.foreach { form =>
        registerBounds(form).foreach { (binding, bounds) =>
          SassDomains.field(database, form, binding).filter(_.bank.nonEmpty).foreach { domain =>
            if (checked.add((database.digest, binding, domain, bounds))) {
              def legal(code: Int): Vec[Either[String, BigInt]] = {
                val bindings = Map(binding -> BigInt(code))
                bounds.map(bound => Legality.evaluate(database, bound, bindings))
              }
              val where = s"${target.name} ${form.name}.$binding ${domain.describe}"
              (0 to 255).filter(domain.contains(_)).foreach(code =>
                assert(legal(code).forall(_ != Right(BigInt(0))), s"$where=$code")
              )
              val top = domain.runs.collect { case DomainRun.Registers(_, first, last, base) =>
                base + last - first
              }.max
              if (top < 254 && !domain.contains(top + 1) && legal(top + 1).forall(_.isRight))
                assert(
                  legal(top + 1).contains(Right(BigInt(0))) || bounds.exists(bound =>
                    Legality.evaluate(database, bound.left, Map(binding -> BigInt(top + 1))) == Right(BigInt(1))
                  ),
                  s"$where above $top"
                )
            }
          }
        }
      }
    }
  }

  test("every general register of S2R round trips in every table") {
    tables.foreach { target =>
      val registers = domain(target, "S2R", "Rd")
      assertEquals(
        registers.values.map(_._2).distinct.size,
        target.database.parameters("MAX_REG_COUNT").toInt + 1,
        target.name
      )
      registers.values.collect {
        case (name, _) if name == "RZ" || name.drop(1).toIntOption.exists(_.toString == name.drop(1)) => name
      }.foreach { name =>
        val text = s"S2R $name, SR_LANEID;"
        assertEquals(printed(bytes(text, target), target), text, target.name)
      }
    }
  }

  tables.foreach { target =>
    test(s"every special register code of S2R in ${target.name} has a round trip spelling or a refusing condition") {
      val database    = target.database
      val s2rForms    = forms(target, "S2R")
      val parsedForms = s2rForms.filter(_.opcodes.forall(_.name == "S2R"))
      val specials    = s2rForms.flatMap(form => SassDomains.fields(database, form))
        .collectFirst { case (_, found) if found.label == "SpecialRegister" => found }
        .getOrElse(fail(s"${target.name} S2R has no special register")).values
      specials.groupBy(_._2).foreach { (code, spellings) =>
        val encoded = spellings.flatMap((name, _) => Assembler.assemble(s"S2R R1, $name;", target).toOption)
        if (encoded.isEmpty)
          spellings.foreach { (name, _) =>
            parsedForms.foreach { form =>
              val result = FormatParser.parse(database, form, 0, s"S2R R1, $name")
              assert(result.left.exists(refused(form, _)), s"${target.name} ${form.name} $name (code $code): $result")
            }
          }
        encoded.foreach(found => assertEquals(bytes(printed(found, target), target).toSeq, found.toSeq, target.name))
      }
    }
  }

  test("a special register the table also names outside the SR spelling prints as SR<n>") {
    val target = Targets.target(35)
    assert(domain(target, "S2R", "SR").values.contains(("SM_SHADER_TYPE", BigInt(20))))
    assertEquals(printed(bytes("S2R R1, SR20;", target), target), "S2R R1, SR20;")
  }

  test("CS2R prefers the zero-register alias over its numeric selector") {
    val aliased = tables.filter { target =>
      forms(target, "CS2R").flatMap(form => SassDomains.fields(target.database, form))
        .exists { (_, found) =>
          found.label == "SpecialRegister" && found.values.contains(("SRZ", BigInt(255))) &&
          found.values.contains(("SR255", BigInt(255)))
        }
    }
    assertEquals(aliased.size, 11)
    aliased.foreach { target =>
      val text = "CS2R R4, SRZ;"
      assertEquals(printed(bytes(text, target), target), text, target.name)
    }
  }

  test("every convergence barrier of BSYNC round trips and the next one is rejected") {
    val modern = tables.filter(target => target.database.byVocabulary.contains("BD"))
    assertEquals(modern.size, 11)
    modern.foreach { target =>
      val barriers = domain(target, "BSYNC", "barReg")
      assertEquals(
        barriers.runs,
        Vec[DomainRun](DomainRun.Registers(bank = "B", first = 0, last = 15, code = 0)),
        target.name
      )
      barriers.values.foreach { (name, _) =>
        val text = s"BSYNC $name;"
        assertEquals(printed(bytes(text, target), target), text, target.name)
      }
      val written = parsed(target, "BSYNC", "BSYNC B0").collect { case (form, Right(_)) => form }
      assert(written.nonEmpty, target.name)
      written.foreach(form =>
        assertEquals(
          FormatParser.parse(target.database, form, 0, "BSYNC B16"),
          Left(s"barReg=16 is outside the BD domain of ${form.name}: B0..B15"),
          target.name
        )
      )
    }
  }

  test("register operands beyond the legality bound are refused by the conditions of the form") {
    val bounded = tables.filter { target =>
      val top = domain(target, "S2R", "Rd").runs.collect { case DomainRun.Registers(_, first, last, base) =>
        base + last - first
      }.max
      top < 254 && {
        parsed(target, "S2R", s"S2R R${top + 1}, SR_LANEID").foreach((form, result) =>
          assert(result.left.exists(refused(form, _)), s"${target.name} ${form.name}: $result")
        )
        true
      }
    }
    assertEquals(bounded.size, 11)
  }

  test("uniform registers stop below the uniform register limit of the table") {
    tables.filter(_.database.parameters.contains("MAX_UNIFORM_REG_COUNT")).foreach { target =>
      val limit   = target.database.parameters("MAX_UNIFORM_REG_COUNT").toInt
      val uniform = target.database.forms.view.flatMap(form => SassDomains.fields(target.database, form)).collect {
        case (_, found) if found.label == "UniformRegister" => found.runs
      }.toSeq
      assert(
        uniform.exists(runs =>
          runs.headOption.contains(DomainRun.Registers("UR", 0, limit - 1, 0)) &&
            runs.lastOption.exists { case DomainRun.Named("URZ", _) => true; case _ => false }
        ),
        s"${target.name}: ${uniform.distinct}"
      )
    }
  }
}
