package io.github.kosmx.bendylib;

/**
 * Different workarounds to fix shared mod incompatibilities
 * If needed, I advise using {@link WorkaroundEnum#VanillaDraw}.
 * That is the most stable in any modded environment.
 */
public enum WorkaroundEnum {
    ExportQuads, VanillaDraw, None;
}
