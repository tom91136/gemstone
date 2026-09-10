#!/usr/bin/env python3
"""Exercise both container wrappers with recorded Docker/Podman arguments, without an SDK."""
import importlib.util
import json
import os
from pathlib import Path
import subprocess
import shutil
import tempfile
import unittest

ROOT = Path(__file__).resolve().parents[2]


class ContainerContract(unittest.TestCase):
    def test_openmp_target_configuration_activates_upstream_regions(self):
        script = (ROOT / "conformance/miniapps/tools/container-build.sh").read_text()
        block = script.split('      if [[ "$model" == omp-target ]]; then\n', 1)[1].split('      else\n', 1)[0]
        for architecture in ("gfx1030", "gfx1150"):
            shell = 'cmake_args=()\nhip_target_architecture=' + architecture + '\n' + block
            shell += '\nprintf "%s\\n" "${cmake_args[@]}"\n'
            result = subprocess.run(["bash", "-s"], input=shell, capture_output=True, text=True)
            self.assertEqual(result.returncode, 0, result.stderr)
            arguments = result.stdout.splitlines()
            self.assertIn("-DOFFLOAD=ON", arguments)
            flags = next(x for x in arguments if x.startswith("-DOFFLOAD_FLAGS="))
            self.assertIn("-fopenmp-targets=amdgcn-amd-amdhsa", flags)
            self.assertIn("-march=" + architecture, flags)
        model_block = script.split('  build_model="$model"\n', 1)[1].split('  case "$app:$model" in', 2)[1]
        for app, expected in (("babelstream", "omp"), ("minibude", "omp"),
                              ("cloverleaf", "omp-target"), ("tealeaf", "omp")):
            shell = f'app={app}\nmodel=omp-target\nbuild_model="$model"\ncase "$app:$model" in' + model_block
            shell += '\nprintf "%s" "$build_model"\n'
            result = subprocess.run(["bash", "-s"], input=shell, capture_output=True, text=True)
            self.assertEqual(result.returncode, 0, result.stderr)
            self.assertEqual(result.stdout, expected)

    def test_hip_stdpar_retains_managed_interposition_without_exported_stl_overrides(self):
        script = (ROOT / "conformance/miniapps/tools/container-build.sh").read_text()
        block = script.split('        if [[ "$model" == std-indices ]]; then\n', 1)[1].split('\n        fi\n        cmake_args+=(', 1)[0]
        for architecture in ("gfx1030", "gfx900"):
            shell = 'hip_app_extra_flags=-O2\nhip_target_architecture=' + architecture + '\n' + block
            shell += '\nprintf "%s" "$hip_app_extra_flags"\n'
            result = subprocess.run(["bash", "-s"], input=shell, capture_output=True, text=True)
            self.assertEqual(result.returncode, 0, result.stderr)
            flags = result.stdout.split()
            self.assertIn("--hipstdpar", flags)
            self.assertIn("-U__HIPSTDPAR_INTERPOSE_ALLOC_V1__", flags)
            self.assertNotIn("-U__HIPSTDPAR_INTERPOSE_ALLOC__", flags)
            self.assertIn("-fvisibility=hidden", flags)
            self.assertIn("-fvisibility-inlines-hidden", flags)
            self.assertIn("-fno-sized-deallocation", flags)
            self.assertIn("--hipstdpar-interpose-alloc", flags)

    def test_openmp_target_audit_rejects_initialization_without_offload(self):
        spec = importlib.util.spec_from_file_location("openmp_audit", ROOT / "conformance/miniapps/tools/verify-openmp-target.py")
        audit = importlib.util.module_from_spec(spec)
        spec.loader.exec_module(audit)
        flags = "amdclang++ -fopenmp=libomp -fopenmp-targets=amdgcn-amd-amdhsa -march=gfx1030 -c source.cpp"
        sections = "  [ 3] .llvm.offloading LLVM_OFFLOADING 0002002e8 0002e8 0009f0 00 A 0 0 8\n"
        empty_entries = sections + "  [11] llvm_offload_entries PROGBITS 000201fa0 001fa0 000000 00 A 0 0 8\n"
        entries = sections + "  [11] llvm_offload_entries PROGBITS 000201fa0 001fa0 000310 00 A 0 0 8\n"
        initialization = " U __tgt_register_lib@VERS1.0\n U __tgt_unregister_lib@VERS1.0\n"
        with self.assertRaisesRegex(ValueError, "source definitions"):
            audit.audit("babelstream", [{"command": flags}], entries, initialization, "gfx1030")
        command = [{"command": flags + " -DOMP_TARGET_GPU"}]
        with self.assertRaisesRegex(ValueError, "nonempty OpenMP target entries"):
            audit.audit("babelstream", command, empty_entries, initialization, "gfx1030")
        with self.assertRaisesRegex(ValueError, "target kernel launch"):
            audit.audit("babelstream", command, entries, initialization + " U __tgt_target_data_begin_mapper@VERS1.0\n", "gfx1030")
        result = audit.audit("babelstream", command, entries, initialization + " U __tgt_target_kernel@VERS1.0\n", "gfx1030")
        self.assertEqual(result["target_launch_symbols"], ["__tgt_target_kernel"])
        self.assertGreater(result["offload_entries_bytes"], 0)

    def test_non_kokkos_models_do_not_require_a_kokkos_architecture(self):
        script = (ROOT / "conformance/miniapps/tools/container-build.sh").read_text()
        prefix = script.split("<<'CONTAINER_SCRIPT'\n", 1)[1].split('\nif [[ "$backend" == sycl', 1)[0]
        result = subprocess.run(["bash", "-s", "--", "opencl", "sm_75", "gfx900", "babelstream:ocl"],
                                input=prefix, capture_output=True, text=True)
        self.assertEqual(result.returncode, 0, result.stderr)
        result = subprocess.run(["bash", "-s", "--", "opencl", "sm_75", "gfx900", "babelstream:kokkos"],
                                input=prefix, capture_output=True, text=True)
        self.assertNotEqual(result.returncode, 0)
        self.assertIn("no architecture mapping", result.stderr)

    def test_both_builders_leave_the_cpp_abi_runtime_to_the_host(self):
        for mode in (0, 1):
            with self.subTest(bundle_system_loaders=mode):
                check = (ROOT / "conformance/tools/runtime-bundle.sh").read_text()
                check += f"\nbundle_system_loaders={mode}\n"
                check += "\n".join(f"is_host_abi_library '{library}' || exit 1" for library in
                    ("libstdc++.so.6", "libstdc++.so.6.0.33", "libgcc_s.so.1"))
                check += "\nif is_host_abi_library libsycl.so.9; then exit 1; fi\n"
                check += ("if is_host_abi_library libOpenCL.so.1; then exit 1; fi\n" if mode else
                          "is_host_abi_library libOpenCL.so.1 || exit 1\n")
                result = subprocess.run(["bash", "-s"], input=check, capture_output=True, text=True)
                self.assertEqual(result.returncode, 0, result.stderr)

    def test_retired_matrix_overrides_fail_before_container_work(self):
        for variable in ("UNIFIED_SUITE_CTEST_SHARDS", "UNIFIED_SUITE_SYCL_ALGORITHM_SIZES", "UNIFIED_SUITE_SYCL_EDGE_BLAS_SIZES"):
            with self.subTest(variable=variable), tempfile.TemporaryDirectory() as directory:
                environment = {k: v for k, v in os.environ.items() if not k.startswith("UNIFIED_SUITE_")}
                environment[variable] = "1"
                result = subprocess.run(["bash", str(ROOT / "conformance/unified/tools/container-build.sh"), "host", directory],
                                        env=environment, capture_output=True, text=True)
                self.assertNotEqual(result.returncode, 0)
                self.assertIn(variable + " is retired", result.stderr)

    def test_shared_build_contract(self):
        for engine in ("docker", "podman"):
            for suite, backend in (("unified", "host"), ("unified", "cuda"), ("unified", "hip"), ("unified", "opencl"), ("unified", "hsa"), ("unified", "sycl"), ("miniapps", "opencl")):
                with self.subTest(engine=engine, suite=suite), tempfile.TemporaryDirectory() as directory:
                    work = Path(directory)
                    mock = work / engine
                    log = work / "calls.jsonl"
                    mock.write_text(
                        "#!/usr/bin/env python3\n"
                        "import json, os, sys\n"
                        "from pathlib import Path\n"
                        "with open(os.environ['CONTAINER_TEST_LOG'], 'a') as log:\n"
                        "    log.write(json.dumps(sys.argv[1:]) + '\\n')\n"
                        "if sys.argv[1] == 'build' and '--iidfile' in sys.argv:\n"
                        "    Path(sys.argv[sys.argv.index('--iidfile')+1]).write_text('sha256:aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa\\n')\n"
                        "if sys.argv[1] == 'run':\n"
                        "    mount = next(a for a in sys.argv if a.startswith('type=bind,src=') and a.endswith(',dst=/out,rw'))\n"
                        "    output = Path(mount[len('type=bind,src='):-len(',dst=/out,rw')]) / 'bin'\n"
                        "    output.mkdir(parents=True, exist_ok=True)\n"
                        "    backend = os.environ['CONTAINER_TEST_BACKEND']\n"
                        "    runner = output / ('unified_suite_' + backend)\n"
                        "    runner.touch()\n"
                        "    runner.chmod(0o755)\n"
                    )
                    mock.chmod(0o755)
                    environment = {key: value for key, value in os.environ.items()
                                   if not key.startswith("GEMSTONE_")}
                    environment.update(PATH=f"{work}:{environment['PATH']}",
                                       GEMSTONE_CONTAINER_ENGINE=engine,
                                       CMAKE_BUILD_PARALLEL_LEVEL="3",
                                       CONTAINER_TEST_LOG=str(log), CONTAINER_TEST_BACKEND=backend,
                                       UNIFIED_SUITE_ALGORITHM_SIZES="4096,16384")
                    inherited = work / "unrelated-data"
                    inherited.mkdir()
                    (inherited / "keep").write_text("preserve")
                    environment["artifact_staging"] = str(inherited)
                    output = work / "artifact with spaces"
                    command = ["bash", str(ROOT / f"conformance/{suite}/tools/container-build.sh"), backend]
                    if suite == "miniapps":
                        command += [str(work), str(output), "babelstream:ocl"]
                    else:
                        command += [str(output)]
                    result = subprocess.run(command, env=environment, capture_output=True, text=True)
                    self.assertEqual(result.returncode, 0, result.stderr)
                    self.assertTrue((inherited / "keep").is_file())
                    calls = [json.loads(line) for line in log.read_text().splitlines()]
                    build = next(call for call in calls if call[0] == "build")
                    run = next(call for call in calls if call[0] == "run")
                    self.assertEqual(Path(build[-1]).resolve(), ROOT / "conformance/containers")
                    self.assertIn("sha256:aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa", run)  # A mutable tag may point elsewhere after build.
                    self.assertIn("--file", build)  # Docker must not infer a Dockerfile from Containerfile.
                    self.assertIn("CMAKE_BUILD_PARALLEL_LEVEL=3", run)
                    if suite == "unified":
                        self.assertIn("-DUNIFIED_SUITE_ALGORITHM_SIZES=4096,16384", run)
                    self.assertIn(f"type=bind,src={output},dst=/out,rw", run)
                    self.assertIn("none", run)
                    self.assertTrue(any("dst=/gemstone-tools,ro" in arg for arg in run))
                    self.assertEqual("--userns=keep-id" in run, engine == "podman")
                    self.assertIn(f"INSTALL_OPENCL={int(backend == 'opencl')}", build)

                    # Invalid concurrency must fail before creating or running a builder.
                    log.unlink()
                    shutil.rmtree(output)
                    environment["CMAKE_BUILD_PARALLEL_LEVEL"] = "0"
                    result = subprocess.run(command, env=environment, capture_output=True, text=True)
                    self.assertNotEqual(result.returncode, 0)
                    self.assertIn("must be a positive integer", result.stderr)
                    self.assertFalse(log.exists())


if __name__ == "__main__":
    unittest.main()
