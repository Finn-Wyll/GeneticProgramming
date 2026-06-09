package gp.algorithms.gp.api;

import gp.algorithms.gp.service.GPService;
import gp.core.dto.ClassificationResult;
import gp.core.dto.Dataset;
import gp.core.service.DatasetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/gp")
@Tag(name = "Genetic Programming", description = "Endpoints for Genetic Programming training and classification")
public class GPController {

    @Autowired
    private GPService gpService;

    @Autowired
    private DatasetService datasetService;

    @PostMapping(value = "/train", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Train a Genetic Programming model", 
               description = "Upload training and test CSV files along with hyperparameters to train a GP model.")
    public ClassificationResult train(
            @Parameter(description = "Training CSV file", required = true)
            @RequestParam("trainFile") MultipartFile trainFile,
            
            @Parameter(description = "Test CSV file", required = true)
            @RequestParam("testFile") MultipartFile testFile,
            
            @ModelAttribute GPRequestDTO request) throws Exception {

        Dataset trainData = datasetService.loadDataset(trainFile.getInputStream(), 0);
        Dataset testData = datasetService.loadDataset(testFile.getInputStream(), 0);

        Map<String, Object> params = new HashMap<>();
        params.put("modelType", request.getModelType());
        params.put("populationSize", request.getPopulationSize());
        params.put("maxGenerations", request.getMaxGenerations());
        params.put("crossoverRate", request.getCrossoverRate());
        params.put("mutationRate", request.getMutationRate());
        params.put("maxDepth", request.getMaxDepth());
        params.put("mutationDepth", request.getMutationDepth());
        params.put("tournamentSize", request.getTournamentSize());
        params.put("seed", request.getSeed());
        params.put("variableNames", trainData.getVariableNames());

        return gpService.train(
                trainData.getFeatures(), trainData.getLabels(),
                testData.getFeatures(), testData.getLabels(),
                params);
    }
}
