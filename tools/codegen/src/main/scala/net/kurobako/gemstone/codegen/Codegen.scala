package net.kurobako.gemstone.codegen

import cats.syntax.all.*
import com.monovore.decline.Command
import com.monovore.decline.Opts
import java.nio.file.Path
import net.kurobako.gemstone.*

object Codegen {
  case class Request(root: Path, action: Action)

  enum Action derives CanEqual {
    case Prepare(bundles: Vector[String])
    case Generate
    case CheckEnvironment
  }

  private val rootOption = Opts.option[String]("root", "Repository root").map(Path.of(_).toAbsolutePath.normalize)

  private val command = Command("codegen", "Prepare pinned references and regenerate checked-in sources")(
    Opts.subcommand("prepare", "Prepare reference bundles and print their directories")(
      (rootOption, Opts.arguments[String]("bundle").map(_.toList.toVector))
        .mapN((root, bundles) => Request(root, Action.Prepare(bundles)))
    ).orElse(Opts.subcommand("generate", "Regenerate every checked-in source")(
      rootOption.map(Request(_, Action.Generate))
    )).orElse(Opts.subcommand("check-environment", "Check environment outputs and names")(
      rootOption.map(Request(_, Action.CheckEnvironment))
    ))
  )

  def main(arguments: Array[String]): Unit = CodegenCommand.run(command, arguments) {
    case Request(root, Action.Prepare(names)) =>
      names.foreach { name =>
        val bundle = References.Bundles.find(_.name == name)
          .getOrElse(throw IllegalArgumentException(s"unknown reference bundle $name"))
        Console.out.println(prepare(root, bundle))
      }
    case Request(root, Action.Generate)         => generate(root)
    case Request(root, Action.CheckEnvironment) => GenerateEnvironment.main(Array(s"$root", "--check"))
  }

  // Mirrors `BuildEnvironment.offline` in the sbt build.
  private def offline: Boolean =
    configuration.enabled(
      sys.env,
      environment.OfflineOn
    ).fold(message => throw IllegalArgumentException(message), identity)

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
      path("isa/ptx-asm/src/generated/main/scala"),
      References.PTXISAVersion,
      path("isa/ptx-asm/src/generated/main/resources")
    ))
    GeneratePTXISA.main(Array(
      "tests",
      ptx,
      path("isa/ptx-asm/src/generated/test/scala"),
      References.PTXISAVersion
    ))
    GenerateMMA884Oracle.main(Array(
      path("isa/ptx-vm/src/generated/test/scala"),
      path("isa/ptx-vm/src/generated/test/resources"),
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
      path("isa/sass-asm/src/generated/main/scala"),
      path("isa/sass-asm/src/generated/main/resources")
    ))

    val tablegen = References.tablegen(root.resolve(".cache/references"), references, offline)
    val amdgpu   = s"${prepare(root, References.AMDGPU)}"
    GenerateAMDGPUISA.main(Array(
      "--python",
      "python3",
      "--tablegen",
      s"$tablegen",
      amdgpu,
      path("isa/amdgpu-asm/src/generated/main/scala"),
      path("isa/amdgpu-asm/src/generated/main/resources"),
      path("native/generated")
    ))
    GenerateAMDGPUOracle.main(Array(amdgpu, path("isa/amdgpu-asm/src/generated/test/resources")))
    GenerateAMDGPUDomains.main(Array(
      amdgpu,
      path("isa/amdgpu-asm/src/generated/main/scala"),
      path("isa/amdgpu-asm/src/generated/main/resources"),
      path("isa/amdgpu-asm/src/generated/test/resources/net/kurobako/gemstone/isa/amdgpu/llvm-oracle")
    ))

    val spirv   = prepare(root, References.SPIRV)
    val unified = spirv.resolve("khronos/spirv-headers/include/spirv/unified1")
    val grammar = s"${unified.resolve("spirv.core.grammar.json")}"
    GenerateSPIRVISA.main(Array(
      "instructions",
      grammar,
      s"$unified",
      path("isa/spirv-asm/src/generated/main/scala"),
      path("native/generated/spirv")
    ))
  }
}
