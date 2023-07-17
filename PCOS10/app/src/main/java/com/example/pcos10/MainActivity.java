package com.example.pcos10;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;

import com.example.pcos10.databinding.ActivityMainBinding;
import com.example.pcos10.ml.Model;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private MinMaxScalerLoader scalerLoader;
    private MinMaxScaler scaler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        scalerLoader = new MinMaxScalerLoader(getAssets());
        scaler = scalerLoader.loadScaler(); // Load MinMaxScaler

        Button detectButton = binding.button;
        detectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Collect user input
                float weight, height, waist, hip;

                // Validate weight field
                if (!binding.weight.getText().toString().isEmpty()) {
                    weight = Float.parseFloat(binding.weight.getText().toString());
                } else {
                    Toast.makeText(MainActivity.this, "Please enter weight", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Validate height field
                if (!binding.height.getText().toString().isEmpty()) {
                    height = Float.parseFloat(binding.height.getText().toString());
                } else {
                    Toast.makeText(MainActivity.this, "Please enter height", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Validate waist field
                if (!binding.waist.getText().toString().isEmpty()) {
                    waist = Float.parseFloat(binding.waist.getText().toString());
                } else {
                    Toast.makeText(MainActivity.this, "Please enter waist measurement", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Validate hip field
                if (!binding.hip.getText().toString().isEmpty()) {
                    hip = Float.parseFloat(binding.hip.getText().toString());
                } else {
                    Toast.makeText(MainActivity.this, "Please enter hip measurement", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Calculate BMI
                float bmi = calculateBMI(weight, height);

                // Calculate WHR
                float whr = calculateWHR(waist, hip);

                // Update the input list with BMI, WHR, and other input values
                // Update the input list with BMI, WHR, and other input values
                List<Float> userInputList = new ArrayList<>();
                userInputList.add(Float.parseFloat(binding.age.getText().toString()));
                userInputList.add(weight);
                userInputList.add(height);
                userInputList.add(bmi);
                userInputList.add(Float.parseFloat(binding.bloodgroup.getText().toString()));
                userInputList.add(Float.parseFloat(binding.cycle.getText().toString()));
                userInputList.add(Float.parseFloat(binding.cyclelength.getText().toString()));
                userInputList.add(Float.parseFloat(binding.marriagestatus.getText().toString()));
                userInputList.add(Float.parseFloat(binding.pregnant.getText().toString()));
                userInputList.add(Float.parseFloat(binding.aborptions.getText().toString()));
                userInputList.add(hip);
                userInputList.add(waist);
                userInputList.add(whr);
                userInputList.add(Float.parseFloat(binding.weightgain.getText().toString()));
                userInputList.add(Float.parseFloat(binding.hairgrowth.getText().toString()));
                userInputList.add(Float.parseFloat(binding.skindarkening.getText().toString()));
                userInputList.add(Float.parseFloat(binding.hairloss.getText().toString()));
                userInputList.add(Float.parseFloat(binding.pimples.getText().toString()));
                userInputList.add(Float.parseFloat(binding.fastfood.getText().toString()));
                userInputList.add(Float.parseFloat(binding.regexercise.getText().toString()));

                // Check if all user inputs are zeros
                boolean allZeros = userInputList.stream().allMatch(value -> value == 0.0f);

                if (allZeros) {
                    // Handle the case where all inputs are zeros
                    Toast.makeText(MainActivity.this, "Please enter valid numeric values", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        if (scaler != null) {
                            // Normalize user input using the loaded MinMaxScaler
                            float[] userInputArray = new float[userInputList.size()];
                            for (int i = 0; i < userInputList.size(); i++) {
                                userInputArray[i] = userInputList.get(i);
                            }
                            float[] normalizedInputArray = scaler.transform(userInputArray);

                            ByteBuffer byteBuffer = ByteBuffer.allocate(4 * normalizedInputArray.length); // Allocate space for float values (4 bytes each)

                            // Update byte buffer with normalized user inputs
                            for (float value : normalizedInputArray) {
                                byteBuffer.putFloat(value);
                            }

                            Model model = Model.newInstance(MainActivity.this);

                            // Create inputs for reference
                            TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, normalizedInputArray.length}, DataType.FLOAT32);
                            inputFeature0.loadBuffer(byteBuffer);

                            // Run model inference and get result
                            Model.Outputs outputs = model.process(inputFeature0);
                            TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();

                            // Get the binary output value (0 or 1)
                            float outputValue = outputFeature0.getFloatValue(0);

                            // Determine the class label based on the output value
                            String classLabel = outputValue >= 0.5 ? "PCOS DETECTED" : "NEGATIVE";

                            // Display the class label
                            binding.textView.setText("RESULT: " + classLabel);

                            clearAlllFields();

                            // Release model resources if no longer used
                            model.close();
                        } else {
                            Toast.makeText(MainActivity.this, "Failed to load MinMaxScaler", Toast.LENGTH_SHORT).show();
                        }
                    } catch (NumberFormatException e) {
                        // Handle the case where the input is not a valid number
                        Toast.makeText(MainActivity.this, "Please enter valid numeric values", Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
    }

    private void clearAlllFields() {
        // Clear input fields
        binding.age.getText().clear();
        binding.weight.getText().clear();
        binding.height.getText().clear();
        binding.bloodgroup.getText().clear();
        binding.cycle.getText().clear();
        binding.cyclelength.getText().clear();
        binding.marriagestatus.getText().clear();
        binding.pregnant.getText().clear();
        binding.aborptions.getText().clear();
        binding.hip.getText().clear();
        binding.waist.getText().clear();
        binding.weightgain.getText().clear();
        binding.hairgrowth.getText().clear();
        binding.skindarkening.getText().clear();
        binding.hairloss.getText().clear();
        binding.pimples.getText().clear();
        binding.fastfood.getText().clear();
        binding.regexercise.getText().clear();
    }

    private float calculateBMI(float weight, float height) {
        return weight / (height * height);
    }

    private float calculateWHR(float waist, float hip) {
        return waist / hip;
    }
}
