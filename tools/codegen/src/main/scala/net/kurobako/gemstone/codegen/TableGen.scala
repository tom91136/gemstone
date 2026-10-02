package net.kurobako.gemstone.codegen

import java.nio.file.Path
import scala.util.Using

/** Evaluate TableGen inheritance and expressions before selecting records for generation. */
object TableGen {
  private val Export = """import json
import subprocess
import sys
import tempfile

selection = json.loads(sys.argv[1])
with tempfile.TemporaryFile(mode="w+") as output:
    subprocess.run(sys.argv[2:], stdout=output, check=True)
    output.seek(0)
    records = json.load(output)
result = {}
for kind, fields in selection.items():
    names = records["!instanceof"][kind]
    result[kind] = {name: {field: records[name][field] for field in fields} for name in names}
json.dump(result, sys.stdout, sort_keys=True)
"""

  def records(
      executable: Path,
      source: Path,
      includes: Vector[Path],
      selection: Map[String, Vector[String]],
      python: String
  ): ujson.Value = {
    val command = Vector(python, "-B", "-c", Export, ujson.write(selection), executable.toString, "--dump-json") ++
      includes.flatMap(path => Vector("-I", path.toString)) :+ source.toString
    val process = ProcessBuilder(command*).redirectError(ProcessBuilder.Redirect.INHERIT).start()
    try {
      val output = Using.resource(scala.io.Source.fromInputStream(process.getInputStream, "UTF-8"))(_.mkString)
      val status = process.waitFor()
      require(status == 0, s"TableGen export failed with exit code $status")
      ujson.read(output)
    } finally process.destroy()
  }
}
