package model;

import java.util.Set;

/**
 * Representing a Devil Fruit User character in the battle system.
 * Showcases Inheritance, Interface Implementation, and Method Overriding.
 */
public class DevilFruitUser extends Character implements SpecialAbility {
    private String devilFruitName;
    private String devilFruitType; // Paramecia, Zoan, Logia

    /**
     * Constructor using default ability set.
     */
    public DevilFruitUser(String name, int health, int attackPower, int defense, String devilFruitName, String devilFruitType) {
        super(name, health, attackPower, defense, "DevilFruitUser");
        this.devilFruitName = devilFruitName;
        this.devilFruitType = devilFruitType;
        addAbility("Devil Fruit Control");
        addAbility(devilFruitName + " Powers");
    }

    /**
     * Overloaded constructor with predefined abilities.
     */
    public DevilFruitUser(String name, int health, int attackPower, int defense, String devilFruitName, String devilFruitType, Set<String> abilities) {
        super(name, health, attackPower, defense, "DevilFruitUser", abilities);
        this.devilFruitName = devilFruitName;
        this.devilFruitType = devilFruitType;
        addAbility("Devil Fruit Control");
        addAbility(devilFruitName + " Powers");
    }

    @Override
    public void attack(Character opponent) {
        System.out.println(getName() + " uses their " + devilFruitName + " (" + devilFruitType + ") basic attack on " + opponent.getName() + "!");
        opponent.takeDamage(getAttackPower());
    }

    @Override
    public void specialMove(Character opponent) {
        System.out.println(getName() + " unleashes their Devil Fruit Special Move: Room - Gamma Knife on " + opponent.getName() + "!");
        int specialDamage = (int) (getAttackPower() * 1.7);
        opponent.takeDamage(specialDamage);
    }

    @Override
    public void activateSpecialAbility(Character opponent) {
        System.out.println(getName() + " triggers Devil Fruit Awakening: Kroom / Anesthesia!");
        // Awakened strike ignores defense and deals double damage, but drains user health slightly as a trade-off
        int awakeningDamage = getAttackPower() * 2;
        int selfDrain = 5;
        setHealth(getHealth() - selfDrain);
        System.out.println("  -> " + getName() + " suffers " + selfDrain + " HP fatigue drain from awakening. HP is now: " + getHealth());
        opponent.takeDamage(awakeningDamage + opponent.getDefense());
    }

    public String getDevilFruitName() { return devilFruitName; }
    public void setDevilFruitName(String devilFruitName) { this.devilFruitName = devilFruitName; }

    public String getDevilFruitType() { return devilFruitType; }
    public void setDevilFruitType(String devilFruitType) { this.devilFruitType = devilFruitType; }

    @Override
    public String toString() {
        return super.toString() + String.format(" | Fruit: %s (%s)", devilFruitName, devilFruitType);
    }
}
