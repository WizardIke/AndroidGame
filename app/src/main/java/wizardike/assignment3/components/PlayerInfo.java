package wizardike.assignment3.components;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import wizardike.assignment3.Engine;
import wizardike.assignment3.entities.Player;
import wizardike.assignment3.levels.Level;

public class PlayerInfo {
    private Date lastPlayedData;
    private int playerClass;
    private int playerRace;
    private String name;
    private int level;

    public PlayerInfo(int playerClass, int playerRace, String name, int level) {
        lastPlayedData = Calendar.getInstance().getTime();
        this.playerClass = playerClass;
        this.playerRace = playerRace;
        this.name = name;
        this.level = level;
    }

    public PlayerInfo(DataInputStream save) throws IOException {
        lastPlayedData = new Date(save.readLong());
        playerClass = save.readInt();
        playerRace = save.readInt();
        int nameLength = save.readInt();
        byte[] bytes = new byte[nameLength];
        int bytesRead = save.read(bytes);
        if(bytesRead != nameLength) {
            throw new IOException("Failed reading name");
        }
        this.name = new String(bytes);
        this.level = save.readInt();
    }

    public void save(DataOutputStream save) throws IOException {
        save.writeLong(Calendar.getInstance().getTime().getTime());
        save.writeInt(playerClass);
        save.writeInt(playerRace);
        byte[] nameBytes = name.getBytes();
        save.writeInt(nameBytes.length);
        save.write(nameBytes);
        save.writeInt(level);
    }

    public int createPlayer(Engine engine, Level level) {
        return Player.create(engine, level);
    }

    public Date getLastPlayedData() {
        return lastPlayedData;
    }

    public int getPlayerClass() {
        return playerClass;
    }

    public int getPlayerRace() {
        return playerRace;
    }

    public String getName() {
        return name;
    }

    public int getLevel() {
        return level;
    }
}
