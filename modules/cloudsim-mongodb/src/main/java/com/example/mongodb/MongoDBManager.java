package com.example.mongodb;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

public class MongoDBManager {
    private final MongoCollection<Document> collection;

    // Konstruktor, der eine MongoCollection injiziert bekommt
    public MongoDBManager(MongoCollection<Document> collection) {
        this.collection = collection;
    }

    public List<Document> getValidInstances() {
        List<Document> validInstances = new ArrayList<>();
        try (MongoCursor<Document> cursor = collection.find().iterator()) {
            while (cursor.hasNext()) {
                Document instance = cursor.next();
                if (isValidInstance(instance)) {
                    validInstances.add(instance);
                }
            }
        }
        return validInstances;
    }

    private boolean isValidInstance(Document instance) {
        // Azure-spezifische Felder
        String[] azureFields = {"Name der Größe", "vCPUs (Anzahl)", "Spitzenfrequenz für alle Kerne (GHz)"};

        // AWS-spezifische Felder
        String[] awsFields = {"InstanceType", "vCPUs", "BaseClockSpeedGhz"};

        // Prüfen für Azure-Felder
        boolean validAzure = true;
        for (String field : azureFields) {
            if (!instance.containsKey(field) || instance.get(field) == null) {
                validAzure = false;
                break;
            }
        }

        // Prüfen für AWS-Felder
        boolean validAWS = true;
        for (String field : awsFields) {
            if (!instance.containsKey(field) || instance.get(field) == null) {
                validAWS = false;
                break;
            }
        }

        // Instanz ist gültig, wenn sie entweder Azure- oder AWS-Felder erfüllt
        return validAzure || validAWS;
    }

}
