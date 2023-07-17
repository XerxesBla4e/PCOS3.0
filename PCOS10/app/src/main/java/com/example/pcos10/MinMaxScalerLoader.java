package com.example.pcos10;

import android.content.res.AssetManager;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MinMaxScalerLoader {
    private AssetManager assetManager;

    public MinMaxScalerLoader(AssetManager assetManager) {
        this.assetManager = assetManager;
    }

    public MinMaxScaler loadScaler() {
        try {
            InputStream inputStream = assetManager.open("min_max_scaler.json");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder jsonContent = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                jsonContent.append(line);
            }
            bufferedReader.close();
            inputStream.close();

            Gson gson = new Gson();
            MinMaxScalerParams scalerParams = gson.fromJson(jsonContent.toString(), MinMaxScalerParams.class);

            MinMaxScaler scaler = new MinMaxScaler(scalerParams.data_min_, scalerParams.data_max_, scalerParams.scale_);
            return scaler;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static class MinMaxScalerParams {
        float[] scale_;
        float[] min_;
        float[] data_min_;
        float[] data_max_;
        float[] data_range_;
    }
}
