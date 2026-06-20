package model;

import java.util.HashSet;
import java.util.Set;

/**
 * Abstract class representing a Character in the simulation.
 * Showcases Abstraction, Encapsulation, Constructor Overloading, and Collections framework usage.
 */
public abstract class Character {
    private String name;
    private int health;
    private int maxHealth;
    private int attackPower;
    private int defense;
    private String role;
    private Set<String> abilities;

    /**
     * Primary constructor with common fields.
     */
    public Character(String name, int health, int attackPower, int defense, String role) {
        this.name = name;
        this.health = health;
        this.maxHealth = health;
        this.attackPower = attackPower;
        this.defense = defense;
        this.role = role;
        this.abilities = new HashSet<>();
    }

    /**
     * Overloaded constructor to initialize with predefined abilities.
     */
    public Character(String name, int health, int attackPower, int defense, String role, Set<String> abilities) {
        this(name, health, attackPower, defense, role);
        if (abilities != null) {
            this.abilities.addAll(abilities);
        }
    }

    // Abstract methods representing polymorphism
    public abstract void attack(Character opponent);
    public abstract void specialMove(Character opponent);

    /**
     * Inflicts damage on the character, taking their defense rating into account.
     * Guaranteed to deal at least 1 point of damage if hit.
     * @param damage Raw incoming damage
     */
    public void takeDamage(int damage) {
        int mitigatedDamage = damage - this.defense;
        int finalDamage = Math.max(1, mitigatedDamage);
        this.health = Math.max(0, this.health - finalDamage);
        System.out.println("  -> " + name + " takes " + finalDamage + " damage (Defense mitigated: " + Math.max(0, mitigatedDamage) + "). HP remaining: " + health + "/" + maxHealth);
    }

    /**
     * Adds a unique ability to the character's list of tracked abilities.
     * Showcases HashSet ability tracking to avoid duplicate capabilities.
     * @param ability The ability name
     * @return true if added, false if already present
     */
    public boolean addAbility(String ability) {
        if (ability == null || ability.trim().isEmpty()) {
            return false;
        }
        return this.abilities.add(ability.trim());
    }

    public boolean isAlive() {
        return this.health > 0;
    }

    public void resetHealth() {
        this.health = this.maxHealth;
    }

    // Getters and Setters representing Encapsulation
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getHealth() { return health; }
    public void setHealth(int health) { this.health = Math.max(0, health); }

    public int getMaxHealth() { return maxHealth; }
    public void setMaxHealth(int maxHealth) { this.maxHealth = maxHealth; }

    public int getAttackPower() { return attackPower; }
    public void setAttackPower(int attackPower) { this.attackPower = attackPower; }

    public int getDefense() { return defense; }
    public void setDefense(int defense) { this.defense = defense; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public Set<String> getAbilities() { return new HashSet<>(abilities); }
    public void setAbilities(Set<String> abilities) { this.abilities = new HashSet<>(abilities); }

    @Override
    public String toString() {
        return String.format("[%s] Name: %s | HP: %d/%d | ATK: %d | DEF: %d | Abilities: %s",
                role, name, health, maxHealth, attackPower, defense, abilities);
    }
}
