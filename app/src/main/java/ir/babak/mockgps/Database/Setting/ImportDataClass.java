package ir.babak.mockgps.Database.Setting;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Type;
import java.net.URI;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import ir.babak.mockgps.Database.Entitiy.PeriodEntitiy;
import ir.babak.mockgps.Database.Entitiy.PositionsEntitiy;
import ir.babak.mockgps.R;

public class ImportDataClass {

    private Activity activity;

    public ImportDataClass(Activity activity) {
        this.activity = activity;
    }

    public void ChoiceFileDialog() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        activity.startActivityForResult(intent, activity.getResources().getInteger(R.integer.RESULT_LOAD_IMAGE));
    }

    private List<PositionsEntitiy> getAllPositions(String jsonString) {
        Gson gson = new Gson();
        Type listType = new TypeToken<ArrayList<PositionsEntitiy>>() {
        }.getType();
        return gson.fromJson(jsonString, listType);
    }


    public List<PositionsEntitiy> ReadFile(Uri strFileUri) {

        try {
            InputStream inputStream = activity.getContentResolver().openInputStream(strFileUri);
            BufferedReader r = new BufferedReader(new InputStreamReader(inputStream));
            String mLine;
            StringBuilder text = new StringBuilder();
            while ((mLine = r.readLine()) != null) {
                text.append(mLine);
            }

            return getAllPositions(text.toString());


        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


}
