/*
 * This file is part of Map Explorer.
 * 
 * Copyright © 2005-2007 Christoph Breitkopf
 *
 * This software is provided 'as-is', without any express or implied warranty.
 * In no event will the authors be held liable for any damages arising from the
 * use of this software.
 *
 * Permission is granted to anyone to use this software for any purpose, including
 * commercial applications, and to alter it and redistribute it freely, subject to
 * the following restrictions:
 *
 *   1. The origin of this software must not be misrepresented; you must not claim
 *      that you wrote the original software. If you use this software in a product,
 *      an acknowledgment in the product documentation would be appreciated but is
 *      not required.
 *
 *   2. Altered source versions must be plainly marked as such, and must not be
 *      misrepresented as being the original software.
 *
 *   3. This notice may not be removed or altered from any source distribution.
 */

package de.bokeh.ddm.mapexplorer;

import java.util.Map;
import java.util.HashMap;

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
    
    /** Zone of Death.<p>Must make a morale check if hit by a melee attack. */
    ZONE_OF_DEATH,
    
    /** Haunted.<p>-2 to all saves. */
    HAUNTED,
    
    /** Risky terrain. */
    RISKY,
    
    /** A pit or chasm. */
    PIT,
    
    /** Steep slope. */
    STEEP_SLOPE,
    
    /** Lava. */
    LAVA,
    
    /** Smoke.<p>Blocks LOS. */
    SMOKE,
    
    /** Forest.<p>Blocks LOS. */
    FOREST,
    
    /** Teleporter. */
    TELEPORTER,
    
    /** Waterfall. */
    WATERFALL,
    
    /** Start area A. */
    START_A,
    
    /** Start Area B. */
    START_B,
    
    /** Exit A. */
    EXIT_A,
    
    /** Exit B. */
    EXIT_B,
    
    /** Victory Area A. */
    VICTORY_A,
    
    /** Victory Area B. */
    VICTORY_B,
    
    /** Temporary wall terrain created using an Elemental Wall or other means. */
    ELEMENTAL_WALL;
    
    static final Map<String, MapFeature> tagNames = new HashMap<String, MapFeature>();
    static {
	tagNames.put("difficult", DIFFICULT);
	tagNames.put("smoke", SMOKE);
	tagNames.put("forest", FOREST);
	tagNames.put("pit", PIT);
	tagNames.put("spikestones", SPIKE_STONES);
	tagNames.put("lava", LAVA);
	tagNames.put("risky", RISKY);
	tagNames.put("magic", SACRED_CIRCLE);
	tagNames.put("summoning", SUMMONING_CIRCLE);
	tagNames.put("statue", STATUE);
	tagNames.put("bloodrock", BLOOD_ROCK);
	tagNames.put("haunted", HAUNTED);
	tagNames.put("teleporter", TELEPORTER);
	tagNames.put("elementel_wall", ELEMENTAL_WALL);
	tagNames.put("steep_slope", STEEP_SLOPE);
	tagNames.put("death_zone", ZONE_OF_DEATH);
	tagNames.put("waterfall", WATERFALL);
    }
    
    
    
    public static MapFeature valueOfTag(String tag) {
	MapFeature f = tagNames.get(tag);
	if (f == null)
	    throw new IllegalArgumentException("unknown tag for MapFeature: " + tag);
	return f;
    }
    
}
