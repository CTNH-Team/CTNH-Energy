# CTNH-Energy

[![Build](https://github.com/CTNH-Team/CTNH-Energy/actions/workflows/build.yml/badge.svg?branch=dev)](https://github.com/CTNH-Team/CTNH-Energy/actions/workflows/build.yml)

Core mod of the Applied Energistics for the modpack Create: New Horizon (CTNH).

## Building

This mod should be built under [CTNH-Team/CTNH-Modules](https://github.com/CTNH-Team/CTNH-Modules) repository using Gradle.

```shell
$ git clone --recursive https://github.com/CTNH-Team/CTNH-Modules.git 
$ cd CTNH-Modules   # And you may need to update the submodules manually
$ ./gradlew :modules:CTNH-Energy:build            # To build the mod .jar
$ ./gradlew :modules:CTNH-Energy:runData          # To generate data
$ ./gradlew :modules:CTNH-Energy:spotlessCheck    # To check code formatting
$ ...
```

Nightly builds are available on the [Actions](https://github.com/CTNH-Team/CTNH-Energy/actions/workflows/build.yml) page.

## License

All code is licensed under the [GNU LGPL v3 License](https://www.gnu.org/licenses/lgpl-3.0.en.html).

All artwork (images, textures, models, animations, etc.) is licensed under the [Creative Commons Attribution-NonCommercial 4.0 International License](http://creativecommons.org/licenses/by-nc/4.0/), unless stated otherwise.

`textures/block/casings/assembler_matrix_frame.png` and `textures/block/casings/assembler_matrix_frame_ctm.png` are derivative work based on textures from ExpandedAE and are subject to the GNU LGPL v3 license of ExpandedAE. 
 
## Credits

### Reference

- The implementation of smart blocking mode of pattern provider referenced [ExpandedAE](https://github.com/ko-lja/expandedae)  
- The implementation of Quantum Computer referenced [AdvancedAE](https://github.com/pedroksl/AdvancedAE)
- The implementation of AppEU referenced [Applied Flux](https://github.com/GlodBlock/ExtendedAE/tree/appflux/1.21.1-neoforge)

### Dependents

- Applied Energistics 2  
- GregTech-Modern
- The implementation of ME Advanced Pattern Buffer is based on [ProgrammedCircuitCard](https://github.com/yuuki1293/ProgrammedCircuitCard)  
