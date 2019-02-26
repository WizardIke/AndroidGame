package wizardike.assignment3;

import android.net.Uri;
import android.util.Log;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class SaveFileManager {
    private static final String TAG = "SaveFileManager";

    public static void createSave(Uri saveFileUri, String name, int playerClass, int race) throws IOException {
        File saveFile = new File(saveFileUri.getPath());
        DataOutputStream save = new DataOutputStream(new FileOutputStream(saveFile));
        save.writeInt(0); //The state of the save is hasn't been played yet
        new PlayerInfo(playerClass, race, name, 1).save(save);
        save.writeInt(0); //Start the game on dungeon level 1 i.e level with id of 0
        save.writeInt(0); //Zero levels have save data generated for them
        save.close();
    }

    public static void deleteSave(Uri saveFileUri) {
        File saveFile = new File(saveFileUri.getPath());
        final boolean succeeded = saveFile.delete();
        if(!succeeded) {
            Log.e(TAG, "Deleted a save file that doesn't exist");
        }
    }
}
