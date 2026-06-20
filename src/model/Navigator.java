package model;

import java.util.Set;

/**
 * Representing a Navigator character in the battle system.
 * Showcases Inheritance, Interface Implementation, and Method Overriding.
 */
public class Navigator extends Character implements SpecialAbility {
    private String navigationTool;

    /**
     * Constructor using default ability set.
     */
    public Navigator(String name, int health, int attackPower, int defense, String navigationTool) {
        super(name, health, attackPower, defense, "Navigator");
        this.navigationTool = navigationTool;
        addAbility("Weather Forecasting");
        addAbility("Cartography");
    }

    /**
     * Overloaded constructor with predefined abilities.
     */
    public Navigator(String name, int health, int attackPower, int defense, String navigationTool, Set<String> abilities) {
        super(name, health, attackPower, defense, "Navigator", abilities);
        this.navigationTool = navigationTool;
        addAbility("Weather Forecasting");
        addAbility("Cartography");
    }

    @Override
    public void attack(Character opponent) {
        System.out.println(getName() + " strikes " + opponent.getName() + " with their " + navigationTool + "!");
        opponent.takeDamage(getAttackPower());
    }

    @Override
    public void specialMove(Character opponent) {
        System.out.println(getName() + " calls down a lightning bolt: Thunderbolt Tempo on " + opponent.getName() + "!");
        int specialDamage = (int) (getAttackPower() * 1.4);
        opponent.takeDamage(specialDamage);
    }

    @Override
    public void activateSpecialAbility(Character opponent) {
        System.out.println(getName() + " activates Mirage Tempo, confusing the enemy and reinforcing defense!");
        // Boosts defense and deals minor shock damage
        setDefense(getDefense() + 5);
        System.out.println("  -> " + getName() + "'s Defense boosted! Now DEF is: " + getDefense());
        opponent.takeDamage(getAttackPower() - 2);
    }

    public String getNavigationTool() { return navigationTool; }
    public void setNavigationTool(String navigationTool) { this.navigationTool = navigationTool; }

    @Override
    public String toString() {
        return super.toString() + String.format(" | Tool: %s", navigationTool);
    }
}
