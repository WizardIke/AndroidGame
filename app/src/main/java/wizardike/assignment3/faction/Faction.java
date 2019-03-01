package wizardike.assignment3.faction;

/**
 * Created by Isaac on 18/01/2017.
 */
public enum Faction {
    Necromancer((byte)1),
    Mage((byte)0);
    public final byte value;

    Faction(final byte faction){
        this.value = faction;
    }

    public boolean isAlly(Faction other) {
        return other != null && (other.value == Necromancer.value && this.value == Necromancer.value ||
                other.value == Mage.value && this.value == Mage.value);
    }

    public boolean isEnemy(Faction other){
        return other != null && (other.value == Necromancer.value && this.value == Mage.value ||
                other.value == Mage.value && this.value == Necromancer.value);
    }

    public static Faction toFaction(final int faction) {
        switch(faction){
            case 0 :
                return Faction.Mage;
            case 1:
                return Faction.Necromancer;
            default:
                return null;
        }
    }
}
