#!/usr/bin/env python3
import gzip
import importlib.util
from pathlib import Path
import tempfile
import unittest
from unittest import mock

module = importlib.util.spec_from_file_location('provenance', Path(__file__).with_name('runtime_provenance.py'))
p = importlib.util.module_from_spec(module); module.loader.exec_module(p)

class Provenance(unittest.TestCase):
    def test_direct_and_hsa_routes_require_actual_bindings_not_only_counts(self):
        with tempfile.TemporaryDirectory() as directory:
            root=Path(directory); hip=root/'libamdhip64.so';hsa=root/'libhsa-runtime64.so';wrong=root/'vendor-other.so'
            for path in (hip,hsa,wrong):path.write_bytes(path.name.encode())
            for route in ('hip','hip-hsa'):
                spec=dict(runtime_route=dict(id=route,hip_library=str(hip),hsa_library=str(hsa)),gemstone=dict(directory=directory),
                          authenticated_requests=dict(cuda=0,execution=12),attestation=dict(required=True))
                with mock.patch.object(p.sys,'platform','linux'): env=p.prepare(spec,'gemstone',root,'case')
                log=Path(env['LD_DEBUG_OUTPUT']+'.123')
                log.write_text(f"123: binding file /app [0] to {hip} [0]: normal symbol `hipMalloc' [hip_4.2]\n"+
                               f"123: binding file {hip} [0] to {hsa} [0]: normal symbol `hsa_init' [ROCR_1]\n")
                evidence=p.collect(spec,'gemstone',root,'case');p.validate(evidence)
                self.assertTrue(evidence['verified']);self.assertEqual(len(evidence['libraries']['hip']['sha256']),64)
                log.write_text(f"123: binding file /app [0] to {wrong} [0]: normal symbol `hipMalloc' [hip_4.2]\n")
                with self.assertRaises(RuntimeError):p.validate(p.collect(spec,'gemstone',root,'case'))
                log.unlink()
                with self.assertRaises(RuntimeError):p.validate(p.collect(spec,'gemstone',root,'case'))
    def test_archived_loader_evidence_is_lossless(self):
        with tempfile.TemporaryDirectory() as directory:
            path = Path(directory) / 'case.loader.123'
            raw = b"binding evidence\n" * 10000
            path.write_bytes(raw)
            evidence = dict(evidence_files=[str(path)])
            p.archive(evidence)
            self.assertFalse(path.exists())
            archive = Path(evidence['evidence_files'][0])
            self.assertEqual(gzip.decompress(archive.read_bytes()), raw)
            self.assertLess(archive.stat().st_size, len(raw) // 10)

    def test_successful_device_route_is_qualified_once_per_command(self):
        with tempfile.TemporaryDirectory() as directory:
            root=Path(directory);hip=root/'hip.so';hip.write_bytes(b'hip')
            def specification():
                return dict(runtime_route=dict(id='hip',hip_library=str(hip)),
                            gemstone=dict(directory=directory,command=['unique-program']),
                            authenticated_requests=dict(cuda=0,execution=1),attestation=dict(required=True))
            first=specification()
            with mock.patch.object(p.sys,'platform','linux'):env=p.prepare(first,'gemstone',root/'cases','first')
            log=Path(env['LD_DEBUG_OUTPUT']+'.1')
            log.write_text(f"1: binding file /app [0] to {hip} [0]: normal symbol `hipMalloc'\n")
            evidence=p.collect(first,'gemstone',root/'cases','first');p.archive(evidence)
            second=specification()
            with mock.patch.object(p.sys,'platform','linux'):
                self.assertEqual(p.prepare(second,'gemstone',root/'cases','second'),{})
            cached=p.collect(second,'gemstone',root/'cases','second')
            self.assertTrue(cached['verified']);self.assertEqual(cached['evidence_files'],evidence['evidence_files'])

    def test_shared_oracle_is_not_requalified_and_windows_is_not_claimed(self):
        self.assertEqual(p.prepare({},'oracle',Path('.'),'case'),{})
        self.assertIsNone(p.collect({},'oracle',Path('.'),'case'))
        with mock.patch.object(p.sys,'platform','win32'):
            with self.assertRaises(RuntimeError):p.prepare(dict(runtime_route=dict(id='hip')),'gemstone',Path('.'),'case')
    def test_empty_work_accepts_loaded_libraries_but_never_a_conflicting_binding(self):
        with tempfile.TemporaryDirectory() as directory:
            root=Path(directory);hip=root/'hip.so';hip.write_bytes(b'hip');wrong=root/'other.so';wrong.write_bytes(b'other')
            spec=dict(runtime_route=dict(id='hip',hip_library=str(hip)),gemstone=dict(directory=directory),
                      authenticated_requests=dict(cuda=0,execution=0),attestation=dict(required=False))
            with mock.patch.object(p.sys,'platform','linux'):env=p.prepare(spec,'gemstone',root,'case')
            log=Path(env['LD_DEBUG_OUTPUT']+'.1');log.write_text(f'1: calling init: {hip}\n')
            p.validate(p.collect(spec,'gemstone',root,'case'))
            log.write_text(log.read_text()+f"1: binding file /app [0] to {wrong} [0]: normal symbol `hipMalloc'\n")
            with self.assertRaises(RuntimeError):p.validate(p.collect(spec,'gemstone',root,'case'))
if __name__=='__main__':unittest.main()
