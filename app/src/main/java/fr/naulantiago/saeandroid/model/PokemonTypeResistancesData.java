package fr.naulantiago.saeandroid.model;

public class PokemonTypeResistancesData
{
    private enum DamageRelation
    {
        VULNERABLE,
        NEUTRAL,
        RESISTANT,
        TWICE_RESISTANT;
    }

    private final PokemonTypeData types;
    private final double damageMultiplier;
    private DamageRelation damageRelation = DamageRelation.NEUTRAL;

    public PokemonTypeResistancesData(final PokemonTypeData types,
                                      final double damageMultiplier) {
        this.types = types;
        this.damageMultiplier = damageMultiplier;

        if (this.damageMultiplier == 2.) {
            damageRelation = DamageRelation.VULNERABLE;
        } else if (this.damageMultiplier == 1.) {
            damageRelation = DamageRelation.NEUTRAL;
        } else if (this.damageMultiplier == .5) {
            damageRelation = DamageRelation.RESISTANT;
        } else if (this.damageMultiplier == .25) {
            damageRelation = DamageRelation.TWICE_RESISTANT;
        }
    }

    public PokemonTypeData getTypes() {
        return types;
    }

    public double getDamageMultiplier() {
        return damageMultiplier;
    }

    public DamageRelation getDamageRelation() {
        return damageRelation;
    }
}
