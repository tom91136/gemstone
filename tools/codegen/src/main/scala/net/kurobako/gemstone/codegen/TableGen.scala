package net.kurobako.gemstone.codegen

import java.nio.file.Path
import scala.collection.mutable
import scala.sys.process.Process
import scala.sys.process.ProcessLogger

/** Evaluate TableGen inheritance and expressions before selecting records for generation. */
object TableGen {
  private val Export = """import json
import re
import subprocess
import sys

selection = json.loads(sys.argv[1])
field = re.compile(r"  (?:field )?(?:\{[^{}]*\}|[^\s{}]+) (\w+)(?: = (.*))?$")
token = re.compile(r"\s*(-?\d+|\?|\w+|\"[^\"]*\"|[{}\[\],])")


def parse(text):
    if text is None:
        raise ValueError("unsupported multi-line value")
    tokens = []
    position = 0
    while position < len(text):
        match = token.match(text, position)
        if not match:
            raise ValueError(f"unsupported value: {text}")
        tokens.append(match.group(1))
        position = match.end()
    tokens.reverse()

    def value():
        head = tokens.pop()
        if head in "{[":
            close = "}" if head == "{" else "]"
            items = []
            while tokens[-1] != close:
                items.append(value())
                if tokens[-1] == ",":
                    tokens.pop()
            tokens.pop()
            return items[::-1] if head == "{" else items
        if head == "?":
            return None
        if head.startswith('"'):
            return head[1:-1]
        if re.fullmatch(r"-?\d+", head):
            return int(head)
        if re.fullmatch(r"\w+", head):
            return {"def": head, "kind": "def", "printable": head}
        raise ValueError(f"unsupported value: {text}")

    result = value()
    if tokens:
        raise ValueError(f"unsupported value: {text}")
    return result


def records(stream):
    for line in stream:
        yield line
        if " = [{" in line and not line.endswith("}];\n"):
            for line in stream:
                if line.endswith("}];\n"):
                    break


classes = set()
result = {kind: {} for kind in selection}
process = subprocess.Popen(sys.argv[2:], stdout=subprocess.PIPE, text=True)
lines = records(process.stdout)
for line in lines:
    if line.startswith("class "):
        classes.add(re.match(r"class ([^\s<]+)", line).group(1))
    if not line.startswith("def "):
        continue
    header = re.fullmatch(r"def (\S+) \{(?:\t//(.*))?\n", line)
    if not header:
        raise ValueError(f"unsupported record: {line}")
    kinds = [kind for kind in (header.group(2) or "").split() if kind in selection]
    if not kinds:
        continue
    values = {}
    for line in lines:
        if line == "}\n":
            break
        match = field.match(line.rstrip("\n"))
        if not match:
            raise ValueError(f"unsupported field in {header.group(1)}: {line}")
        text = match.group(2) or ""
        values[match.group(1)] = text[:-1] if text.endswith(";") else None
    for kind in kinds:
        result[kind][header.group(1)] = {name: parse(values[name]) for name in selection[kind]}
if process.wait() != 0:
    raise subprocess.CalledProcessError(process.returncode, sys.argv[2:])
missing = set(selection) - classes
if missing:
    raise ValueError(f"unknown classes: {sorted(missing)}")
json.dump(result, sys.stdout, sort_keys=True)
"""

  def records(
      executable: Path,
      source: Path,
      includes: Vector[Path],
      selection: Map[String, Vector[String]],
      python: String
  ): ujson.Value = {
    val command = Vector(python, "-B", "-c", Export, ujson.write(selection), executable.toString, "--print-records") ++
      includes.flatMap(path => Vector("-I", path.toString)) :+ source.toString
    val output = mutable.StringBuilder()
    val status = Process(command).!(ProcessLogger(line => output.append(line), Console.err.println(_)))
    require(status == 0, s"TableGen export failed with exit code $status")
    ujson.read(output.result())
  }
}
