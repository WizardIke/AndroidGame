package wizardike.assignment3.networking;

/**
 * Created by Isaac on 25/01/2017.
 */
public class NetworkMessageTypes {
    public static final int startGame = (byte)0;
    public static final int connectionLost = (byte)1;
    public static final int gameObject = (byte)2;

    public static final int castSpell = (byte)0;
    public static final int raiseSkeleton = (byte)1;
    public static final int raiseSkeletons = (byte)7;
    public static final int summonSkeletonHorde = (byte)8;
    public static final int fireBolt = (byte)12;

    public static final int setAwesomeness = (byte)2;
    public static final int setX = (byte)3;
    public static final int setY = (byte)4;
    public static final int die = (byte)5;
    public static final int setName = (byte)6;
    public static final int setHealth = (byte)9;
    public static final int setPosition = (byte)10;
    public static final int explode = (byte)11;
    public static final int activate = 12;
    public static final int activateSuccessful = 13;
}
