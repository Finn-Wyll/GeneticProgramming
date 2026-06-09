package gp.core.service;

import gp.core.dto.Dataset;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Service
public class DatasetService {

    public Dataset loadDataset(InputStream inputStream, int labelIndex) throws Exception {
        List<double[]> featureRows = new ArrayList<>();
        List<Integer> labels = new ArrayList<>();
        List<String> variableNames = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            String header = br.readLine();
            if (header != null) {
                String[] parts = header.split(",");
                for (int i = 0; i < parts.length; i++) {
                    if (i != labelIndex) {
                        variableNames.add(parts[i].trim());
                    }
                }
            }

            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(",");
                
                double[] features = new double[parts.length - 1];
                int featureIdx = 0;
                for (int i = 0; i < parts.length; i++) {
                    if (i == labelIndex) {
                        labels.add(Integer.valueOf(parts[i].trim()));
                    } else {
                        features[featureIdx++] = Double.parseDouble(parts[i].trim());
                    }
                }
                featureRows.add(features);
            }
        }

        double[][] featureArray = featureRows.toArray(new double[0][0]);
        int[] labelArray = labels.stream().mapToInt(i -> i).toArray();

        return new Dataset(featureArray, labelArray, variableNames);
    }
}
