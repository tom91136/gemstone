#!/usr/bin/env python3
"""Export the pinned ACO Python model to a deterministic, deliberately tiny interchange format."""

import importlib.util
import pathlib
import sys


def load(path):
    spec = importlib.util.spec_from_file_location("gemstone_pinned_aco_opcodes", path)
    if spec is None or spec.loader is None:
        raise RuntimeError("cannot load pinned ACO opcode source")
    module = importlib.util.module_from_spec(spec)
    spec.loader.exec_module(module)
    return module


def operand(value, is_input, is_output):
    kind = value.base_type.name.removeprefix("aco_base_type_")
    return ",".join(
        (
            kind,
            str(value.bit_size),
            str(value.num_components),
            "1" if is_input else "0",
            "1" if is_output else "0",
            "1" if value.fixed_reg.name != "not_fixed" else "0",
        )
    )


def main(argv):
    if len(argv) != 2:
        raise SystemExit("usage: export_aco.py <aco_opcodes.py>")
    module = load(pathlib.Path(argv[1]))
    generations = ("gfx6", "gfx7", "gfx8", "gfx9")
    for name in sorted(module.instructions):
        instruction = module.instructions[name]
        if instruction.format.name.startswith("PSEUDO"):
            continue
        operands = [operand(value, False, True) for value in instruction.definitions]
        operands.extend(operand(value, True, False) for value in instruction.operands)
        for generation in generations:
            opcode = getattr(instruction.op, generation)
            if opcode >= 0:
                print(
                    "\t".join(
                        (
                            generation.upper(),
                            name,
                            instruction.format.name,
                            str(opcode),
                            instruction.cls.value,
                            ";".join(operands),
                            "mesa-aco",
                        )
                    )
                )


if __name__ == "__main__":
    main(sys.argv)
