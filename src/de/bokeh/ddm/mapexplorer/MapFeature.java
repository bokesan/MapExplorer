package de.bokeh.ddm.mapexplorer;

/**
 * Map features.
 * @author Christoph Breitkopf
 */
public enum MapFeature {

    /** Difficult terrain. */
    DIFFICULT,
    
    /** Statue. */
    STATUE,
    
    /** Sacred Circle.<p>Meelee and ranged attacks are +2 and
        damage is considered magic.  */
    SACRED_CIRCLE,
    
    /** Summoning Circle. */
    SUMMONING_CIRCLE,
    
    /** Spike Stones. */
    SPIKE_STONES,
    
    /** Blood Rock.<p>Melee attacks score critical hits on a natural 19 or 20. */
    BLOOD_ROCK,
    
    /** Haunted.<p>-2 to all saves. */
    HAUNTED,
    
    /** Risky terrain. */
    RISKY,
    
    PIT,
    LAVA,
    
    /** Smoke.<p>Blocks LOS. */
    SMOKE,
    
    START_A,
    START_B,
    
    /** Exit A. */
    EXIT_A,
    /** Exit B. */
    EXIT_B,
    
    /** Victory Area A. */
    VICTORY_A,
    
    /** Victory Area B. */
    VICTORY_B,
    
}
