package com.example.restapi;

import com.example.mongodb.MongoDBManager;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MongoDBConfig {

    // Atlas Connection String
    private static final String MONGO_URI =
            "mongodb+srv://SimulationUser:9pIqIgC95YaBsQTg@azurepricetracking.2oiyy.mongodb.net/?retryWrites=true&w=majority&appName=AzurePriceTracking";

    // AWS Database and Collection
    private static final String AWS_DATABASE = "AWSInstancesDB";
    private static final String AWS_COLLECTION = "AWSInstancesCollection";

    // Azure Database and Collection
    private static final String AZURE_DATABASE = "AzureInstancesDB";
    private static final String AZURE_COLLECTION = "AzureInstancesCollection";

    @Bean
    public MongoClient mongoClient() {
        return MongoClients.create(MONGO_URI);
    }

    // AWS Collection
    @Bean(name = "awsCollection")
    public MongoCollection<Document> awsCollection(MongoClient mongoClient) {
        MongoDatabase database = mongoClient.getDatabase(AWS_DATABASE);
        return database.getCollection(AWS_COLLECTION);
    }

    // Azure Collection
    @Bean(name = "azureCollection")
    public MongoCollection<Document> azureCollection(MongoClient mongoClient) {
        MongoDatabase database = mongoClient.getDatabase(AZURE_DATABASE);
        return database.getCollection(AZURE_COLLECTION);
    }

    // MongoDBManager für AWS
    @Bean(name = "awsManager")
    public MongoDBManager awsManager(@Qualifier("awsCollection") MongoCollection<Document> collection) {
        return new MongoDBManager(collection);
    }

    // MongoDBManager für Azure
    @Bean(name = "azureManager")
    public MongoDBManager azureManager(@Qualifier("azureCollection") MongoCollection<Document> collection) {
        return new MongoDBManager(collection);
    }
}
