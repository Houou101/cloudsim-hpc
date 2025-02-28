package com.example.restapi;


/**
 * Klasse, um das Ergebnis der Simulation zu speichern.
 */
public class SimulationResult {
    private String status;
    private String message;
    private long executionTime;

    // Konstruktor
    public SimulationResult(String status, String message, long executionTime) {
        this.status = status;
        this.message = message;
        this.executionTime = executionTime;
    }

    // Getter und Setter
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getExecutionTime() {
        return executionTime;
    }

    public void setExecutionTime(long executionTime) {
        this.executionTime = executionTime;
    }
}
