package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.isa.sass.*
import net.kurobako.gemstone.rpc.*
import net.kurobako.gemstone.server.backend.{Cubin as Backend, *}
import net.kurobako.gemstone.vm.*
import scala.util.Using

/** Minimal fixture builder retained for the server's constant-alias binding integration test. */
object CubinBackendFixture {
  val encoding: String = "sass-decoded-integration-test"
  case class Fixture(module: Cubin.Module, functions: Vec[DecodedFunction])

  def lower(
      module: Cubin.Module,
      kernel: Cubin.Kernel,
      functions: Vec[DecodedFunction]
  ): Either[String, Fixture] = VMCompiler.lower(module, kernel, functions).map(_ => Fixture(module, functions))

  class Fixtures extends RuntimeBackend {
    @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
    private var fixtures                   = Vec.empty[Fixture]
    override val encoding: String          = CubinBackendFixture.encoding
    def image(fixture: Fixture): Vec[Byte] = {
      val index = fixtures.size
      fixtures = fixtures :+ fixture
      LittleEndian.toVec(index.toLong, 4)
    }
    override def load(image: Vec[Byte]): Either[ExecutionFailure, RuntimeBackend.Program] = {
      val index   = image.zipWithIndex.foldLeft(0)((value, entry) => value | ((entry._1 & 255) << (entry._2 * 8)))
      val fixture = fixtures(index)
      Backend().loadDecoded(fixture.module, fixture.functions)
    }
  }

  def session[A](body: (ExecutionSession, Fixtures) => A): A = {
    val fixtures = Fixtures()
    Using.resource(ExecutionSession(fixtures :: Backends.default().programs))(service => body(service, fixtures))
  }
}
