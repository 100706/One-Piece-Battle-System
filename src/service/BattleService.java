package service;

import exception.InvalidBattleException;
import model.Character;
import model.HakiUser;
import model.SpecialAbility;
import java.util.ArrayList;
import java.util.List;

/**
 * Service managing battles and combat simulations.
 * Showcases Exception Handling, Polymorphism, and Collections (ArrayList).
 */
public class BattleService {
    private final List<String> history = new ArrayList<>();

    /**
     * Executes a turn-based battle between two characters until one is defeated.
     * Incorporates critical hits, random damage scaling, and special skills.
     * @param c1 Combatant 1
     * @param c2 Combatant 2
     * @return The winning character
     * @throws InvalidBattleException if one or both characters are invalid or dead
     */
    public Character startBattle(Character c1, Character c2) throws InvalidBattleException {
        if (c1 == null || c2 == null) {
            throw new InvalidBattleException("Battle cannot start: One or both combatants are null.");
        }
        if (c1.getName().equalsIgnoreCase(c2.getName())) {
            throw new InvalidBattleException("Battle cannot start: A character cannot fight themselves (" + c1.getName() + ").");
        }
        if (!c1.isAlive()) {
            throw new InvalidBattleException("Battle cannot start: " + c1.getName() + " is already defeated.");
        }
        if (!c2.isAlive()) {
            throw new InvalidBattleException("Battle cannot start: " + c2.getName() + " is already defeated.");
        }

        // Reset health for a fair combat simulation
        c1.resetHealth();
        c2.resetHealth();

        List<String> currentBattleLogs = new ArrayList<>();
        logAndPrint("\n================ BATTLE SIMULATION ================", currentBattleLogs);
        logAndPrint(c1.getName() + " vs " + c2.getName(), currentBattleLogs);
        logAndPrint("===================================================", currentBattleLogs);

        Character attacker = c1;
        Character defender = c2;
        int round = 1;

        while (c1.isAlive() && c2.isAlive()) {
            logAndPrint("\n[Round " + round + "]", currentBattleLogs);

            // Temporarily compute random multiplier and critical hit
            int originalAtk = attacker.getAttackPower();
            boolean isCritical = Math.random() < 0.15; // 15% critical hit chance
            double randomMultiplier = 0.85 + Math.random() * 0.30; // 85% to 115% scaling
            
            double multiplier = randomMultiplier;
            if (isCritical) {
                multiplier *= 1.5; // 1.5x critical damage
                logAndPrint("  💥 CRITICAL HIT! " + attacker.getName() + " finds a weak spot!", currentBattleLogs);
            }

            int tempAtk = Math.max(1, (int) (originalAtk * multiplier));
            attacker.setAttackPower(tempAtk);

            // Determine move type
            double moveChance = Math.random();
            if (moveChance < 0.50) {
                // 50% normal attack
                logAndPrint("  " + attacker.getName() + " uses a basic attack.", currentBattleLogs);
                attacker.attack(defender);
            } else if (moveChance < 0.80) {
                // 30% special move
                logAndPrint("  " + attacker.getName() + " uses a Special Move!", currentBattleLogs);
                attacker.specialMove(defender);
            } else {
                // 20% Interface Ability (Haki or SpecialAbility)
                if (attacker instanceof HakiUser) {
                    logAndPrint("  " + attacker.getName() + " commands the power of Haki!", currentBattleLogs);
                    ((HakiUser) attacker).useHaki(defender);
                } else if (attacker instanceof SpecialAbility) {
                    logAndPrint("  " + attacker.getName() + " triggers an Awakened Ability!", currentBattleLogs);
                    ((SpecialAbility) attacker).activateSpecialAbility(defender);
                } else {
                    // Fallback to normal attack
                    logAndPrint("  " + attacker.getName() + " uses a basic attack.", currentBattleLogs);
                    attacker.attack(defender);
                }
            }

            // Restore original attack power
            attacker.setAttackPower(originalAtk);

            // Check if defender survived
            if (!defender.isAlive()) {
                logAndPrint("\n☠️ " + defender.getName() + " has been defeated!", currentBattleLogs);
                break;
            }

            // Swap roles
            Character temp = attacker;
            attacker = defender;
            defender = temp;
            round++;
        }

        Character winner = calculateWinner(c1, c2);
        logAndPrint("\n🏆 WINNER: " + winner.getName() + " (Remaining HP: " + winner.getHealth() + ")", currentBattleLogs);
        logAndPrint("===================================================\n", currentBattleLogs);

        // Save the summary of the battle to the global history
        String summary = String.format("Match: %s vs %s | Winner: %s (HP left: %d) | Rounds: %d",
                c1.getName(), c2.getName(), winner.getName(), winner.getHealth(), round);
        history.add(summary);

        // Also add the entire round-by-round log to the history archive
        history.addAll(currentBattleLogs);

        return winner;
    }

    /**
     * Calculates the surviving winner of the match.
     * @param c1 Combatant 1
     * @param c2 Combatant 2
     * @return The surviving character
     */
    public Character calculateWinner(Character c1, Character c2) {
        if (c1.isAlive()) {
            return c1;
        } else {
            return c2;
        }
    }

    /**
     * Prints and returns history logs.
     * @return List of history strings
     */
    public List<String> battleHistory() {
        System.out.println("\n=== Battle History Logs ===");
        if (history.isEmpty()) {
            System.out.println(" No battle history found in current session.");
        } else {
            for (String record : history) {
                System.out.println(record);
            }
        }
        return new ArrayList<>(history);
    }

    private void logAndPrint(String message, List<String> logs) {
        System.out.println(message);
        logs.add(message);
    }

    /**
     * Clears session battle history.
     */
    public void clearHistory() {
        history.clear();
    }
}
