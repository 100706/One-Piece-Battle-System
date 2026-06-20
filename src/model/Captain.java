package model;

import java.util.Set;

/**
 * Representing a Captain character in the battle system.
 * Showcases Inheritance, Interface Implementation, and Method Overriding.
 */
public class Captain extends Character implements HakiUser {
    private String crewName;

    /**
     * Constructor using default ability set.
     */
    public Captain(String name, int health, int attackPower, int defense, String crewName) {
        super(name, health, attackPower, defense, "Captain");
        this.crewName = crewName;
        addAbility("Conqueror's Haki");
        addAbility("Leadership Aura");
    }

    /**
     * Overloaded constructor with predefined abilities.
     */
    public Captain(String name, int health, int attackPower, int defense, String crewName, Set<String> abilities) {
        super(name, health, attackPower, defense, "Captain", abilities);
        this.crewName = crewName;
        addAbility("Conqueror's Haki");
        addAbility("Leadership Aura");
    }

    @Override
    public void attack(Character opponent) {
        System.out.println(getName() + " (Captain) launches a commanding melee strike on " + opponent.getName() + "!");
        // Normal attack deals base attack power as damage
        opponent.takeDamage(getAttackPower());
    }

    @Override
    public void specialMove(Character opponent) {
        System.out.println(getName() + " (Captain) unleashes their Ultimate Special Move: Gomu Gomu no Elephant Gun!");
        // Special move deals 1.5x damage
        int specialDamage = (int) (getAttackPower() * 1.5);
        opponent.takeDamage(specialDamage);
    }

    @Override
    public void useHaki(Character opponent) {
        System.out.println(getName() + " activates Conqueror's Haki, overwhelming " + opponent.getName() + "'s willpower!");
        // Haki bypasses opponent's defense entirely (deals raw damage directly)
        int hakiDamage = getAttackPower();
        int opponentDefense = opponent.getDefense();
        // Since takeDamage subtracts defense, we temporarily offset it or deal it directly by adding defense to damage.
        opponent.takeDamage(hakiDamage + opponentDefense);
    }

    public String getCrewName() { return crewName; }
    public void setCrewName(String crewName) { this.crewName = crewName; }

    @Override
    public String toString() {
        return super.toString() + String.format(" | Crew: %s", crewName);
    }
}
