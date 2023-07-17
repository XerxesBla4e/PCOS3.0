package com.example.pcos10;

public class MinMaxScaler {
    private float[] min;
    private float[] max;
    private float[] scale;

    public MinMaxScaler(float[] min, float[] max, float[] scale) {
        this.min = min;
        this.max = max;
        this.scale = scale;
    }

    public float[] transform(float[] data) {
        float[] transformedData = new float[data.length];
        for (int i = 0; i < data.length; i++) {
            float normalizedValue = (data[i] - min[i]) / (max[i] - min[i]) * scale[i];
            transformedData[i] = normalizedValue;
        }
        return transformedData;
    }
}

