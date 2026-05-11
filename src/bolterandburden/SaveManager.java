/**
 * Author:      Shane Potts
 * Project:     Bolter & Burden: The Traitor's Throne
 * Class:       CSC 1061 Computer Science II - Java, FRCC Spring 2026
 * Description: Serializes and deserializes full warband state to a plain-text
 *              save file so a campaign can be paused and resumed across sessions.
 *              Uses a custom key:value / block format instead of Java's
 *              Serializable interface, keeping the output human-readable and the
 *              implementation fully transparent. Each Unit is written as a
 *              UNIT...END_UNIT block containing all persistent fields: stats,
 *              wargear, XP, rank, morale, corruption, routing/possession flags,
 *              and the full ability list. Abilities are encoded as
 *              Type|field|field... pipe-delimited lines. Both the player warband
 *              and the shared possessed warband are saved so daemonic possession
 *              consequences carry forward correctly after a resume.
 * Inputs:      Warband objects (player + possessed) and nextAct index via save();
 *              reads from "bolterandburden.save" in working directory via load()
 * Outputs:     Writes/reads "bolterandburden.save"; status messages to System.out
 *
 * Change Log:
 *   2026-04-08  Shane Potts  Initial creation - Phase 3 persistent save/load;
 *                             custom key:value / block format (not Java Serializable);
 *                             save() writes UNIT...END_UNIT blocks with all
 *                             persistent fields (stats, wargear, XP, rank, morale,
 *                             corruption, routing/possession, abilities);
 *                             load() reconstructs both warbands + nextAct index;
 *                             encodeAbility() / decodeAbility() use pipe-delimited
 *                             strings; supports both Format v1 and v2 BattleTrait
 *                             encoding (with and without trigger field)
 *   2026-04-26  Shane Potts  Phase 3 complete - no further changes this phase
 */

package bolterandburden;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SaveManager {

    private static final String SAVE_FILE         = "bolterandburden.save";
    private static final int    FORMAT_VERSION    = 1;
    private static final String SECTION_PLAYER    = "--- PLAYER UNITS ---";
    private static final String SECTION_POSSESSED = "--- POSSESSED UNITS ---";
    private static final String SECTION_END       = "--- END ---";

    private SaveManager() {}

    // -------------------------------------------------------------------------
    // Save Data Container
    // -------------------------------------------------------------------------

    /**
     * Holds the reconstructed campaign state returned by load().
     */
    public static class SaveData {
        public final Warband playerWarband;
        public final Warband possessedWarband;
        public final int     nextAct;

        public SaveData(Warband playerWarband, Warband possessedWarband, int nextAct) {
            this.playerWarband    = playerWarband;
            this.possessedWarband = possessedWarband;
            this.nextAct          = nextAct;
        }
    }

    // -------------------------------------------------------------------------
    // Public API
    // -------------------------------------------------------------------------

    /**
     * Returns true if a save file exists in the working directory.
     *
     * @return true if "bolterandburden.save" is present
     */
    public static boolean saveExists() {
        return new File(SAVE_FILE).exists();
    }

    /**
     * Deletes the save file if it exists. Called after a campaign completes
     * normally so a fresh run starts a new game.
     */
    public static void deleteSave() {
        File f = new File(SAVE_FILE);
        if (f.exists()) f.delete();
    }

    /**
     * Serializes both warbands and the next act index to the save file. Each
     * Unit is written as a UNIT...END_UNIT block with all persistent fields.
     * Called automatically after each act completes.
     *
     * @param playerWarband    The player's warband (current state)
     * @param possessedWarband The accumulated possessed-unit roster
     * @param nextAct          1-based act number to resume from (2 = begin Act 2)
     */
    public static void save(Warband playerWarband, Warband possessedWarband, int nextAct) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(SAVE_FILE))) {

            // Header block
            pw.println("FORMAT_VERSION:"         + FORMAT_VERSION);
            pw.println("NEXT_ACT:"               + nextAct);
            pw.println("PLAYER_WARBAND:"         + playerWarband.getWarbandName());
            pw.println("PLAYER_FACTION:"         + playerWarband.getFaction().getFactionName());
            pw.println("PLAYER_FACTION_CHAOS:"   + playerWarband.getFaction().isChaosAligned());
            pw.println("POSSESSED_WARBAND:"      + possessedWarband.getWarbandName());
            pw.println("POSSESSED_FACTION:"      + possessedWarband.getFaction().getFactionName());
            pw.println("POSSESSED_FACTION_CHAOS:" + possessedWarband.getFaction().isChaosAligned());

            // Player unit blocks
            pw.println(SECTION_PLAYER);
            for (Unit u : playerWarband.getRosterSnapshot()) {
                writeUnit(pw, u);
            }

            // Possessed unit blocks
            pw.println(SECTION_POSSESSED);
            for (Unit u : possessedWarband.getRosterSnapshot()) {
                writeUnit(pw, u);
            }

            pw.println(SECTION_END);
            System.out.println(Color.c("  [SAVE] Campaign saved. Resume with 'Continue' next session.",
                    Color.CYAN));

        } catch (IOException e) {
            System.out.println("  [SAVE] Error writing save file: " + e.getMessage());
        }
    }

    /**
     * Deserializes both warbands and the next act index from the save file.
     * Returns null and prints an error if the file cannot be read.
     *
     * @return Populated SaveData, or null on failure
     */
    public static SaveData load() {
        try (BufferedReader br = new BufferedReader(new FileReader(SAVE_FILE))) {

            Map<String, String>              header          = new HashMap<>();
            ArrayList<Map<String, String>>   playerBlocks    = new ArrayList<>();
            ArrayList<Map<String, String>>   possessedBlocks = new ArrayList<>();

            String                section      = "HEADER";
            Map<String, String>   currentBlock = null;
            String                line;

            while ((line = br.readLine()) != null) {
                line = line.trim();

                if (line.equals(SECTION_PLAYER)) {
                    section = "PLAYER";
                    continue;
                }
                if (line.equals(SECTION_POSSESSED)) {
                    section = "POSSESSED";
                    continue;
                }
                if (line.equals(SECTION_END)) {
                    break;
                }
                if (line.equals("UNIT")) {
                    currentBlock = new HashMap<>();
                    continue;
                }
                if (line.equals("END_UNIT")) {
                    if (currentBlock != null) {
                        if (section.equals("PLAYER")) playerBlocks.add(currentBlock);
                        else                          possessedBlocks.add(currentBlock);
                        currentBlock = null;
                    }
                    continue;
                }

                int colonIdx = line.indexOf(':');
                if (colonIdx < 0) continue;

                String key = line.substring(0, colonIdx);
                String val = line.substring(colonIdx + 1);

                if (section.equals("HEADER")) {
                    header.put(key, val);
                } else if (currentBlock != null) {
                    // ABILITY lines can repeat; accumulate them newline-separated
                    if (key.equals("ABILITY")) {
                        String existing = currentBlock.get("ABILITIES");
                        currentBlock.put("ABILITIES",
                                existing == null ? val : existing + "\n" + val);
                    } else {
                        currentBlock.put(key, val);
                    }
                }
            }

            // Reconstruct faction objects
            int nextAct = intVal(header, "NEXT_ACT", 1);

            Faction playerFaction = new Faction(
                    header.getOrDefault("PLAYER_FACTION", "Unknown"),
                    Boolean.parseBoolean(header.getOrDefault("PLAYER_FACTION_CHAOS", "false")));
            Warband playerWarband = new Warband(
                    header.getOrDefault("PLAYER_WARBAND", "Player Warband"), playerFaction);

            Faction possessedFaction = new Faction(
                    header.getOrDefault("POSSESSED_FACTION", "Chaos Daemons"),
                    Boolean.parseBoolean(header.getOrDefault("POSSESSED_FACTION_CHAOS", "true")));
            Warband possessedWarband = new Warband(
                    header.getOrDefault("POSSESSED_WARBAND", "The Possessed"), possessedFaction);

            for (Map<String, String> block : playerBlocks) {
                Unit u = reconstructUnit(block);
                if (u != null) playerWarband.addUnit(u);
            }
            for (Map<String, String> block : possessedBlocks) {
                Unit u = reconstructUnit(block);
                if (u != null) possessedWarband.addUnit(u);
            }

            return new SaveData(playerWarband, possessedWarband, nextAct);

        } catch (IOException e) {
            System.out.println("  [LOAD] Error reading save file: " + e.getMessage());
            return null;
        }
    }

    // -------------------------------------------------------------------------
    // Write helpers
    // -------------------------------------------------------------------------

    /**
     * Writes one unit as a UNIT...END_UNIT block. Shared fields come first,
     * then type-specific subtype fields, then equipment, then abilities.
     *
     * @param pw The PrintWriter targeting the open save file
     * @param u  The unit to serialize
     */
    private static void writeUnit(PrintWriter pw, Unit u) {
        pw.println("UNIT");
        pw.println("TYPE:"       + u.getClass().getSimpleName());
        pw.println("NAME:"       + u.getName());
        pw.println("FACTION:"    + u.getFaction());
        pw.println("WOUNDS:"     + u.getStats().getWounds());
        pw.println("TOUGHNESS:"  + u.getStats().getToughness());
        pw.println("SAVE:"       + u.getStats().getSave());
        pw.println("SPEED:"      + u.getStats().getSpeed());
        pw.println("ATTACKS:"    + u.getStats().getAttacks());
        pw.println("ATTACK_MOD:" + u.getWargear().getAttackModifier());
        pw.println("XP:"         + u.getExperiencePoints());
        pw.println("MORALE:"     + u.getMoraleLevel());
        pw.println("CORRUPTION:" + u.getCorruptionLevel());
        pw.println("RANK:"       + u.getUnitRank());
        pw.println("ROUTING:"    + u.isRouting());
        pw.println("POSSESSED:"  + u.isPossessed());

        // Type-specific subtype fields
        if (u instanceof Infantry) {
            Infantry inf = (Infantry) u;
            pw.println("SUBTYPE_ROLE:"    + inf.getSquadRole());
            pw.println("SUBTYPE_VETERAN:" + inf.isVeteran());
        } else if (u instanceof Vehicle) {
            Vehicle v = (Vehicle) u;
            pw.println("SUBTYPE_VEHICLE_TYPE:" + v.getVehicleType());
            pw.println("SUBTYPE_HULL_POINTS:"  + v.getHullPoints());
        } else if (u instanceof Knight) {
            Knight k = (Knight) u;
            pw.println("SUBTYPE_KNIGHT_TITLE:" + k.getKnightTitle());
            pw.println("SUBTYPE_PILOT_BOUND:"  + k.isPilotBound());
        } else if (u instanceof Psyker) {
            Psyker p = (Psyker) u;
            pw.println("SUBTYPE_DISCIPLINE:"   + p.getDisciplineName());
            pw.println("SUBTYPE_WARP_CHARGE:"  + p.getWarpCharge());
        }

        // Equipment list (pipe-separated to avoid comma ambiguity in item names)
        ArrayList<String> gear = u.getWargear().getEquipmentList();
        pw.println("EQUIPMENT:" + (gear.isEmpty() ? "" : String.join("|", gear)));

        // Abilities (one ABILITY: line per entry)
        for (Ability a : u.getAbilities()) {
            pw.println("ABILITY:" + encodeAbility(a));
        }

        pw.println("END_UNIT");
    }

    /**
     * Encodes an Ability to a pipe-delimited string. Format varies by type:
     * PsychicPower  → PsychicPower|name|desc|damageValue
     * SorceryRitual → SorceryRitual|name|desc|damageValue|corruptionCost
     * BattleTrait   → BattleTrait|name|desc|statTarget|modifier
     *
     * @param a The ability to encode
     * @return Pipe-delimited string representation
     */
    private static String encodeAbility(Ability a) {
        if (a instanceof PsychicPower) {
            PsychicPower pp = (PsychicPower) a;
            return "PsychicPower|" + pp.getAbilityName() + "|"
                    + pp.getDescription() + "|" + pp.getDamageValue();
        }
        if (a instanceof SorceryRitual) {
            SorceryRitual sr = (SorceryRitual) a;
            return "SorceryRitual|" + sr.getAbilityName() + "|"
                    + sr.getDescription() + "|" + sr.getDamageValue()
                    + "|" + sr.getCasterCorruptionCost();
        }
        if (a instanceof BattleTrait) {
            BattleTrait bt = (BattleTrait) a;
            return "BattleTrait|" + bt.getAbilityName() + "|"
                    + bt.getDescription() + "|" + bt.getStatTarget()
                    + "|" + bt.getModifier() + "|" + bt.getTrigger();
        }
        // Fallback: base Ability fields only
        return "Unknown|" + a.getAbilityName() + "|" + a.getDescription();
    }

    // -------------------------------------------------------------------------
    // Read helpers
    // -------------------------------------------------------------------------

    /**
     * Reconstructs a Unit from a parsed key:value block. Builds the StatBlock
     * and Wargear, constructs the appropriate subclass, then restores all
     * persistent state via quiet setters.
     *
     * @param b Map of key → value strings parsed from a UNIT block
     * @return The reconstructed Unit, or null if TYPE is unrecognized
     */
    private static Unit reconstructUnit(Map<String, String> b) {
        String type    = b.getOrDefault("TYPE", "");
        String name    = b.getOrDefault("NAME", "Unknown");
        String faction = b.getOrDefault("FACTION", "Unknown");

        StatBlock stats = new StatBlock(
                intVal(b, "WOUNDS",    1),
                intVal(b, "TOUGHNESS", 4),
                intVal(b, "SAVE",      4),
                intVal(b, "SPEED",     6),
                intVal(b, "ATTACKS",   1));

        Wargear wargear = new Wargear();
        wargear.setAttackModifier(intVal(b, "ATTACK_MOD", 0));

        String equipStr = b.getOrDefault("EQUIPMENT", "");
        if (!equipStr.isEmpty()) {
            for (String item : equipStr.split("\\|")) {
                wargear.addEquipment(item);
            }
        }

        Unit unit;
        switch (type) {
            case "Infantry":
                unit = new Infantry(name, faction, stats, wargear,
                        b.getOrDefault("SUBTYPE_ROLE", "Tactical"),
                        Boolean.parseBoolean(b.getOrDefault("SUBTYPE_VETERAN", "false")));
                break;
            case "Vehicle":
                unit = new Vehicle(name, faction, stats, wargear,
                        b.getOrDefault("SUBTYPE_VEHICLE_TYPE", "Transport"),
                        intVal(b, "SUBTYPE_HULL_POINTS", intVal(b, "WOUNDS", 1)));
                break;
            case "Knight":
                unit = new Knight(name, faction, stats, wargear,
                        b.getOrDefault("SUBTYPE_KNIGHT_TITLE", "Despoiler"),
                        Boolean.parseBoolean(b.getOrDefault("SUBTYPE_PILOT_BOUND", "false")));
                break;
            case "Psyker":
                unit = new Psyker(name, faction, stats, wargear,
                        b.getOrDefault("SUBTYPE_DISCIPLINE", "Unknown"),
                        intVal(b, "SUBTYPE_WARP_CHARGE", 2));
                break;
            default:
                System.out.println("  [LOAD] Unknown unit type '" + type + "' — skipping.");
                return null;
        }

        // Restore persistent state via quiet setters (no narrative side-effects)
        unit.setExperiencePoints(intVal(b, "XP", 0));
        unit.setUnitRank(intVal(b, "RANK", 1));
        unit.setMoraleLevel(intVal(b, "MORALE", 10));
        unit.setCorruptionLevel(intVal(b, "CORRUPTION", 0));
        if (Boolean.parseBoolean(b.getOrDefault("ROUTING", "false"))) {
            unit.setRouting(true);
        }
        if (Boolean.parseBoolean(b.getOrDefault("POSSESSED", "false"))) {
            unit.setDaemonPossessed(true);
        }

        // Restore abilities
        String abilityData = b.getOrDefault("ABILITIES", "");
        if (!abilityData.isEmpty()) {
            for (String encoded : abilityData.split("\n")) {
                Ability a = decodeAbility(encoded.trim());
                if (a != null) unit.addAbility(a);
            }
        }

        return unit;
    }

    /**
     * Decodes a pipe-delimited ability string back into an Ability instance.
     *
     * @param encoded Pipe-delimited string from encodeAbility()
     * @return The reconstructed Ability, or null if unrecognized
     */
    private static Ability decodeAbility(String encoded) {
        String[] parts = encoded.split("\\|", -1);
        if (parts.length < 2) return null;
        String kind = parts[0];
        try {
            switch (kind) {
                case "PsychicPower":
                    if (parts.length >= 4)
                        return new PsychicPower(parts[1], parts[2], Integer.parseInt(parts[3]));
                    break;
                case "SorceryRitual":
                    if (parts.length >= 5)
                        return new SorceryRitual(parts[1], parts[2],
                                Integer.parseInt(parts[3]), Integer.parseInt(parts[4]));
                    break;
                case "BattleTrait":
                    // Format v1: BattleTrait|name|desc|statTarget|modifier
                    // Format v2: BattleTrait|name|desc|statTarget|modifier|trigger
                    if (parts.length >= 6)
                        return new BattleTrait(parts[1], parts[2],
                                parts[3], Integer.parseInt(parts[4]), parts[5]);
                    else if (parts.length >= 5)
                        return new BattleTrait(parts[1], parts[2],
                                parts[3], Integer.parseInt(parts[4])); // defaults to "passive"
                    break;
            }
        } catch (NumberFormatException e) {
            System.out.println("  [LOAD] Malformed ability entry: " + encoded);
        }
        return null;
    }

    /**
     * Parses an integer from the block map, returning defaultVal on failure.
     *
     * @param b          The key:value map for the current UNIT block
     * @param key        The key to look up
     * @param defaultVal Fallback value if key is missing or unparseable
     * @return Parsed integer or defaultVal
     */
    private static int intVal(Map<String, String> b, String key, int defaultVal) {
        try {
            return Integer.parseInt(b.getOrDefault(key, String.valueOf(defaultVal)));
        } catch (NumberFormatException e) {
            return defaultVal;
        }
    }
}

/*
 * Change Log:
 *   2026-04-08  Shane Potts  Initial creation - Phase 3 persistent save/load;
 *                             custom key:value / block format (not Java Serializable);
 *                             save() writes UNIT...END_UNIT blocks with all
 *                             persistent fields (stats, wargear, XP, rank, morale,
 *                             corruption, routing/possession, abilities);
 *                             load() reconstructs both warbands + nextAct index;
 *                             encodeAbility() / decodeAbility() use pipe-delimited
 *                             strings; supports both Format v1 and v2 BattleTrait
 *                             encoding (with and without trigger field)
 *   2026-04-26  Shane Potts  Phase 3 complete - no further changes this phase
 */
