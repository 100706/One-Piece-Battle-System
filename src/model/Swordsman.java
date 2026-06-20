package model;

import java.util.Set;

/**
 * Representing a Swordsman character in the battle system.
 * Showcases Inheritance, Interface Implementation, and Method Overriding.
 */
public class Swordsman extends Character implements SpecialAbility {
    private int swordCount;

    /**
     * Constructor using default ability set.
     */
    public Swordsman(String name, int health, int attackPower, int defense, int swordCount) {
        super(name, health, attackPower, defense, "Swordsman");
        this.swordCount = swordCount;
        addAbility("Three-Sword Style");
        addAbility("Blade Deflection");
    }

    /**
     * Overloaded constructor with predefined abilities.
     */
    public Swordsman(String name, int health, int attackPower, int defense, int swordCount, Set<String> abilities) {
        super(name, health, attackPower, defense, "Swordsman", abilities);
        this.swordCount = swordCount;
        addAbility("Three-Sword Style");
        addAbility("Blade Deflection");
    }

    @Override
    public void attack(Character opponent) {
        System.out.println(getName() + " slashes " + opponent.getName() + " using a " + swordCount + "-sword style technique!");
        opponent.takeDamage(getAttackPower());
    }

    @Override
    public void specialMove(Character opponent) {
        System.out.println(getName() + " unleashes their Ultimate Swordsmanship: Santoryu Ougi: Sanzen Sekai!");
        int specialDamage = (int) (getAttackPower() * 1.6);
        opponent.takeDamage(specialDamage);
    }

    @Override
    public void activateSpecialAbility(Character opponent) {
        System.out.println(getName() + " activates their Special Ability: Nine Sword Style: Ashura, raising their blade power!");
        // Deals 1.3x damage and boosts their attack power for this fight
        int abilityDamage = (int) (getAttackPower() * 1.3);
        setAttackPower(getAttackPower() + 2); // Permanent ATK boost for this fight!
        System.out.println("  -> " + getName() + "'s Attack Power permanent boost! Now ATK is: " + getAttackPower());
        opponent.takeDamage(abilityDamage);
    }

    public int getSwordCount() { return swordCount; }
    public void setSwordCount(int swordCount) { this.swordCount = swordCount; }

    @Override
    public String toString() {
        return super.toString() + String.format(" | Swords: %d", swordCount);
    }
}
