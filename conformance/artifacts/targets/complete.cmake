if(NOT DEFINED CONFORMANCE_BACKENDS)
  set(CONFORMANCE_BACKENDS cuda hip hsa opencl sycl levelzero)
endif()
if(NOT DEFINED CUDA_ARCHITECTURES)
  set(CUDA_ARCHITECTURES
    sm_35  # Kepler (CUDA 11.8): GeForce GTX 780 [Ti], GTX Titan, Tesla K{20,40}
    sm_50  # Maxwell (CUDA 11.8): GeForce GTX 750 [Ti], Quadro K{1,2}200, Tesla M10
    sm_60  # Pascal (Datacenter, CUDA 11.8): Tesla P100, Quadro GP100
    sm_70  # Volta (CUDA 11.8): Tesla V100, Titan V, Quadro GV100

    sm_75  # Turing: GeForce RTX 20{6..8}0 [Ti/Super], GTX 16{5,6}0 [Ti/Super], Tesla T4, Quadro RTX {4..8}000
    sm_80  # Ampere (Datacenter): A100 [40GB/80GB], A30
    sm_86  # Ampere (Consumer/Pro): GeForce RTX 30{5..9}0 [Ti], RTX A{2..6}000
    sm_87  # Ampere (Edge/Embedded): Jetson AGX Orin, Orin {NX,Nano}, DRIVE Orin
    sm_89  # Ada Lovelace: GeForce RTX 40{6..9}0 [Ti/Super], RTX {4..6}000 Ada, L4, L40[S]
    sm_90  # Hopper: H100, H200, H20, GH200 Grace Hopper
    sm_100 # Blackwell (Datacenter): B100, B200, GB200 NVL
    sm_103 # Blackwell (Workstation/Mobile): GB10
    sm_120 # Blackwell (Consumer): GeForce RTX 50{7..9}0 [Ti]
  )
endif()
if(NOT DEFINED AMDGPU_ARCHITECTURES)
  set(AMDGPU_ARCHITECTURES
    # --- Classic GCN 4 & 5 ---
    gfx803  # Polaris 10/11/12 & Fiji: Radeon RX {4,5}{6..9}0, RX 550, R9 Fury/Nano, Pro WX {4,5,7}100
    gfx900  # Vega 10: Radeon RX Vega {56,64}, Frontier Edition, Radeon Pro WX 9100
    gfx906  # Vega 20: Radeon VII, Instinct MI{50,60} (Packed dot-product DL-ops)
    gfx90c  # Renoir/Cezanne/Barcelo: Ryzen {4,5}000 {G,GE,U,H,HS} APUs (Vega {6..8})

    # --- CDNA Datacenter / HPC / AI ---
    gfx908  # CDNA 1: Instinct MI100 (MFMA v1 Matrix Cores)
    gfx90a  # CDNA 2: Instinct MI{210,250,250X} (Full-rate FP64 MFMA)
    gfx942  # CDNA 3: Instinct MI300{A,X} (Unified APU Memory, FP8 Transformer Engine)
    gfx950  # CDNA 4: Instinct MI350 series

    # --- RDNA 1 & Semi-Custom Mining / Console ---
    gfx1010 # Navi 10: Radeon RX 5{6,7}00 [XT] (Wave32 introduction)
    gfx1011 # Navi 12: AMD BC-160 Mining Card (8GB HBM2), Radeon Pro 5600M
    gfx1012 # Navi 14: Radeon RX 5500 [XT], Pro 5500M
    gfx1013 # Cyan Skillfish: AMD BC-250 Mining Card (PlayStation 5 Oberon APU silicon, 16GB GDDR6)

    # --- RDNA 2 ---
    gfx1030 # Navi 21: Radeon RX {68,69}{0,5}0 XT
    gfx1031 # Navi 22: Radeon RX 67{0,5}0 [XT]
    gfx1032 # Navi 23: Radeon RX 66{0,5}0 [XT]
    gfx1033 # Van Gogh / Aerith: Valve Steam Deck APU
    gfx1034 # Navi 24: Radeon RX 6{4,5}00 [XT]
    gfx1035 # Rembrandt: Ryzen 6000 {U,H,HS} APUs (Radeon {66,68}0M)
    gfx1036 # Raphael / Granite Ridge: Ryzen {7,9}000 desktop CPU iGPU

    # --- RDNA 3 & 3.5 ---
    gfx1100 # Navi 31: Radeon RX 7900 {GRE,XT,XTX} (Dual-issue Wave32, WMMA)
    gfx1101 # Navi 32: Radeon RX 7{7,8}00 XT
    gfx1102 # Navi 33: Radeon RX 7600 [XT]
    gfx1103 # Phoenix / Hawk Point: Ryzen {7,8}040 APUs (Radeon {76,78}0M, ROG Ally, Legion Go)
    gfx1150 # Strix Point: Ryzen AI 300 series APUs (Radeon {88,89}0M)
    gfx1151 # Strix Halo: Ryzen AI Max 300 series APUs (40 CUs)

    # --- RDNA 4 ---
    gfx1200 # Navi 48: Radeon RX 8800 series
    gfx1201 # Navi 44: Radeon RX 8600 series
  )
endif()
