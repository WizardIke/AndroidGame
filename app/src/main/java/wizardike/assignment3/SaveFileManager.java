package wizardike.assignment3;

import android.net.Uri;
import android.util.Log;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import wizardike.assignment3.entities.SurfaceLevel;
import wizardike.assignment3.entities.World;

public class SaveFileManager {
    private static final String TAG = "SaveFileManager";

    public static void createSave(Uri saveFileUri) throws IOException {
        File saveFile = new File(saveFileUri.getPath());
        DataOutputStream save = new DataOutputStream(new FileOutputStream(saveFile));
        save.writeInt(SurfaceLevel.id); //version of starting level to use
        save.writeInt(1); //number of levels in save
        save.writeInt(SurfaceLevel.saveLength()); //length of level data
        SurfaceLevel.generateSave(save);
    }

    public static void deleteSave(Uri saveFileUri) {
        File saveFile = new File(saveFileUri.getPath());
        final boolean succeeded = saveFile.delete();
        if(!succeeded) {
            Log.e(TAG, "Deleted a save file that doesn't exist");
        }
    }
}
