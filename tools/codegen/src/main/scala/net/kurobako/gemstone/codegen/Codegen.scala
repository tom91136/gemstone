package net.kurobako.gemstone.codegen

import cats.syntax.all.*
import com.monovore.decline.Command
import com.monovore.decline.Opts
import java.nio.file.Path
import net.kurobako.gemstone.*
import net.kurobako.gemstone.string.*

object Codegen {
  enum Request {
    case Prepare(root: Path, bundles: Vector[String])
    case Generate(root: Path)
    case CheckEnvironment(root: Path)
  }

  private val root = Opts.option[String]("root", "Repository root").map(Path.of(_).toAbsolutePath.normalize)

  private val command = Command("codegen", "Prepare pinned references and regenerate checked-in sources")(
    Opts.subcommand("prepare", "Prepare reference bundles and print their directories")(
      (root, Opts.arguments[String]("bundle").map(_.toList.toVector)).mapN(Request.Prepare(_, _))
    ).orElse(Opts.subcommand("generate", "Regenerate every checked-in source")(root.map(Request.Generate(_))))
      .orElse(Opts.subcommand("check-environment", "Check environment outputs and names")(
        root.map(Request.CheckEnvironment(_))
      ))
  )

  def main(arguments: Array[String]): Unit = CodegenCommand.run(command, arguments) {
    case Request.Prepare(root, names) =>
      names.foreach { name =>
        val bundle = References.Bundles.find(_.name == name)
          .getOrElse(throw IllegalArgumentException(s"unknown reference bundle $name"))
        Console.out.println(prepare(root, bundle))
      }
    case Request.Generate(root)         => generate(root)
    case Request.CheckEnvironment(root) => GenerateEnvironment.main(Array(s"$root", "--check"))
  }

  // Mirrors `offlineMode` in build.sbt.
  private def offline: Boolean = sys.env.get(environment.Offline).exists { value =>
    value.toLowerCaseRoot match {
      case "1" | "true" | "on"   => true
      case "0" | "false" | "off" => false
      case _                     => throw IllegalArgumentException(s"${environment.Offline} must be a boolean: $value")
    }
  }

  private def prepare(root: Path, bundle: References.Bundle): Path =
    References.prepare(bundle, root.resolve(".cache/references"), root.resolve("target/references"), offline)

  private def generate(root: Path): Unit = {
    def path(relative: String): String = s"${root.resolve(relative)}"
    val references                     = root.resolve("target/references")
    GenerateEnvironment.main(Array(s"$root", "--write"))
    ImageFormats.main(Array(path("core/src/generated/main/scala")))

    val api      = prepare(root, References.API)
    val manifest = s"${references.resolve("official-api-headers.json")}"
    GenerateOfficialAPIManifest.main(Array("--clang", "clang", s"$api", manifest))
    GenerateNativeBindings.main(Array(path("native/generated"), manifest))
    GenerateExecutionRPC.main(Array(path("native/generated/rpc")))

    val ptx = s"${prepare(root, References.PTX).resolve("nvidia/ptx/index.html")}"
    GeneratePTXISA.main(Array(
      "instructions",
      ptx,
      path("isa/ptx/src/generated/main/scala"),
      References.PTXISAVersion
    ))
    GeneratePTXISA.main(Array(
      "tests",
      ptx,
      path("isa/ptx/src/generated/test/scala"),
      References.PTXISAVersion
    ))
    GenerateMMA884Oracle.main(Array(
      path("isa/ptx/src/generated/test/scala"),
      path("isa/ptx/src/generated/test/resources"),
      path("conformance/unified/src/cuda")
    ))

    val tools = prepare(root, References.SASSTools)
    val sass  = references.resolve("sass")
    ExtractSASS.main(
      (Vector("--bundle", s"${tools.resolve("MANIFEST.tsv")}", "--output", s"$sass") ++
        References.Nvdisasm.flatMap((generation, version, _, digest) =>
          Vector(
            s"--$generation",
            s"${tools.resolve(generation).resolve(s"usr/local/cuda-$version/bin/nvdisasm")}",
            s"--$generation-sha",
            digest
          )
        )).toArray
    )
    GenerateSASSISA.main(Array(
      s"$sass",
      path("isa/sass/src/generated/main/scala"),
      path("isa/sass/src/generated/main/resources")
    ))

    val tablegen = References.tablegen(root.resolve(".cache/references"), references, offline)
    val amdgpu   = s"${prepare(root, References.AMDGPU)}"
    GenerateAMDGPUISA.main(Array(
      "--python",
      "python3",
      "--tablegen",
      s"$tablegen",
      amdgpu,
      path("isa/amdgpu/src/generated/main/scala"),
      path("isa/amdgpu/src/generated/main/resources"),
      path("native/generated")
    ))
    GenerateAMDGPUOracle.main(Array(amdgpu, path("isa/amdgpu/src/generated/test/resources")))

    val spirv   = prepare(root, References.SPIRV)
    val unified = spirv.resolve("khronos/spirv-headers/include/spirv/unified1")
    val grammar = s"${unified.resolve("spirv.core.grammar.json")}"
    GenerateSPIRVISA.main(Array(
      "instructions",
      grammar,
      s"$unified",
      path("isa/spirv/src/generated/main/scala"),
      path("native/generated/spirv")
    ))
  }
}
