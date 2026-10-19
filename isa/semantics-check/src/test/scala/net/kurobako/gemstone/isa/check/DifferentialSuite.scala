package net.kurobako.gemstone.isa.check

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.semantics.render.*
import net.kurobako.gemstone.testing.*
import scala.util.Failure
import scala.util.Random
import scala.util.Try

/** Checks each definition against executing its instance on edge-biased random inputs: results must agree bit for bit,
  * except that any NaN matches any other, and so must the machine state.
  */
abstract class DifferentialSuite[H, L <: Lookup](reference: Reference[H, L], runs: Int = 64) extends munit.FunSuite {
  def check(
      instance: String,
      target: Option[String],
      parameters: Vec[(String, Type)],
      results: Vec[(String, Type)],
      lookup: L
  )(definition: (Vec[Long], H) => Vec[Long]): Unit =
    test(s"${target.fold("")(name => s"$name: ")}$instance") {
      val random = Random(instance.hashCode.toLong)
      val names  = parameters.map(_._1)
      (0 until runs).foreach { run =>
        val sampled   = parameters.map((_, tpe) => Edges.sample(tpe, random))
        val inputs    = reference.inputs(target, instance, lookup, parameters, sampled, random)
        val expected  = reference.harness(run.toLong)
        val obtained  = reference.harness(run.toLong)
        val executed  = reference.execute(target, instance, lookup, parameters, inputs, results, expected)
        val evaluated = Try(definition(inputs, obtained))
        (executed, evaluated) match {
          // inputs outside the instruction's domain, which execution and the definition both reject
          case (Left(_), Failure(_: IllegalArgumentException)) => ()
          case (Left(reason), _)                               => fail(s"$instance does not execute: $reason")
          case (Right(values), _)                              =>
            assertEquals(evaluated.get.size, values.size, "result count")
            lazy val shown = names.zip(inputs.map(value => s"0x${value.toHexString}")).mkString(", ")
            values.zip(evaluated.get).zip(results).foreach { case ((want, got), (name, tpe)) =>
              if (!Edges.same(tpe)(want, got))
                fail(s"$name: executed 0x${want.toHexString}, definition 0x${got.toHexString}; inputs $shown")
            }
            assertEquals(reference.state(obtained), reference.state(expected), s"machine state, inputs $inputs")
        }
      }
    }
}
