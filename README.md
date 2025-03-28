### Starten der Anwendung

Um die REST-API zu starten, führe folgende Datei aus:

```
modules/cloudsim-rest-api/src/main/java/com/example/restapi/Application.java
```

### Beispielanfragen

Sobald der Server läuft, lassen sich Anfragen wie folgt stellen:

- Für AWS-Simulation:
  ```
  http://localhost:8080/simulate/aws?cloudletLength=2000
  ```
- Für Azure-Simulation:
  ```
  http://localhost:8080/simulate/azure?cloudletLength=2000
  ```

