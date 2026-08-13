package tech.luckyblock.mcmod.ctnhenergy.api;

/** Pattern metadata used to carry a GregTech programmed-circuit configuration. */
public interface ICircuitPattern {

    int NO_CIRCUIT = -1;

    void CE$setCircuitNumber(int number);

    int CE$getCircuitNumber();
}
