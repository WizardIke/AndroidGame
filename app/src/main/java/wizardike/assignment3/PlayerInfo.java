package wizardike.assignment3;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import wizardike.assignment3.assemblies.EntityLoadedCallback;
import wizardike.assignment3.assemblies.FireMagePlayer;
import wizardike.assignment3.assemblies.NecromancerPlayer;
import wizardike.assignment3.assemblies.Player;
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

    public void createPlayer(Engine engine, Level level, EntityLoadedCallback callback) {
        if(playerClass == 0) {
            FireMagePlayer.create(engine, level, 0.0f, 0.0f, callback);
        } else if(playerClass == 1) {
            NecromancerPlayer.create(engine, level, 0.0f, 0.0f, callback);
        } else {
            throw new UnsupportedOperationException("Invalid player class: " + playerClass);
        }
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
