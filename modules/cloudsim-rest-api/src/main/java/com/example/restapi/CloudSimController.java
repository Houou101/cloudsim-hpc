package com.example.restapi;

import com.example.mongodb.MongoDBManager;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
public class CloudSimController {

    private final MongoDBManager awsManager;
    private final MongoDBManager azureManager;

    @Autowired
    public CloudSimController(
            @Qualifier("awsManager") MongoDBManager awsManager,
            @Qualifier("azureManager") MongoDBManager azureManager) {
        this.awsManager = awsManager;
        this.azureManager = azureManager;
    }

    @GetMapping("/simulate/aws")
    public List<Map<String, Object>> simulateAWS(@RequestParam long cloudletLength) {
        return runSimulation(awsManager, cloudletLength);
    }

    @GetMapping("/simulate/azure")
    public List<Map<String, Object>> simulateAzure(@RequestParam long cloudletLength) {
        return runSimulation(azureManager, cloudletLength);
    }

    private List<Map<String, Object>> runSimulation(MongoDBManager manager, long cloudletLength) {
        List<Map<String, Object>> results = new ArrayList<>();
        List<Document> instances = manager.getValidInstances();

        for (Document instance : instances) {
            // Sichere Feldabrufe für Azure und AWS
            String name = getStringValue(instance, "Name der Größe", "InstanceType");
            Double vcpus = getDoubleValue(instance, "vCPUs (Anzahl)", "vCPUs");
            Double frequency = getDoubleValue(instance, "Spitzenfrequenz für alle Kerne (GHz)", "BaseClockSpeedGhz");

            // Überspringe Instanzen, bei denen wichtige Felder fehlen
            if (name == null || vcpus == null || frequency == null || frequency == 0.0) {
                System.out.println("Ungültige Instanz übersprungen: " + instance.toJson());
                continue;
            }

            // Berechne die Ausführungszeit
            double executionTime = cloudletLength / (vcpus * frequency);

            // Ergebnis speichern
            Map<String, Object> result = new HashMap<>();
            result.put("instance_name", name);
            result.put("execution_time", executionTime);
            results.add(result);
        }
        return results;
    }

    private String getStringValue(Document doc, String... keys) {
        for (String key : keys) {
            if (doc.containsKey(key) && doc.getString(key) != null) {
                return doc.getString(key);
            }
        }
        return null;
    }

    private Double getDoubleValue(Document doc, String... keys) {
        for (String key : keys) {
            if (doc.containsKey(key)) {
                Object value = doc.get(key);
                if (value instanceof Number) {
                    return ((Number) value).doubleValue();
                }
            }
        }
        return null;
    }

}
