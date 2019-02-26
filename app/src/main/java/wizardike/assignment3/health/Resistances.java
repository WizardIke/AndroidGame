package wizardike.assignment3.health;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by Isaac on 14/12/2016.
 */
public class Resistances {
    private final static int numResistances = 7;
    public enum Type {
        fire(0),
        cold(1),
        lightning(2),
        arcane(3),
        bludgeoning(4),
        piecing(5),
        slashing(6);

        public final int value;
        Type(int value) {
            this.value = value;
        }
    }
    public float[] resistances = new float[numResistances];

    public float get(int index){
        return resistances[index];
    }

    public float get(Type type){
        return resistances[type.value];
    }

    public void set(float value, int index){
        resistances[index] = value;
    }

    public void set(float value, Type type){
        resistances[type.value] = value;
    }

    public Resistances(float fire, float cold, float lightning, float arcane, float bludgeoning, float piecing,
                       float slashing) {
        resistances[Type.fire.value] = fire;
        resistances[Type.cold.value] = cold;
        resistances[Type.lightning.value] = lightning;
        resistances[Type.arcane.value] = arcane;
        resistances[Type.bludgeoning.value] = bludgeoning;
        resistances[Type.piecing.value] = piecing;
        resistances[Type.slashing.value] = slashing;
    }

    public Resistances(DataInputStream saveData) throws IOException {
        for(int i = 0; i < resistances.length; ++i) {
            resistances[i] = saveData.readFloat();
        }
    }

    public void save(DataOutputStream saveData) throws IOException {
        for(float resistance : resistances) {
            saveData.writeFloat(resistance);
        }
    }
}
