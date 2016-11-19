# doag-demo-javascript
Diese Repository beinhaltet Teil der 3 der Demo.

Dabei ist der Services für das präsentieren der View im wesentlichen ein einfacher HTTP-Server (mit Vert.x) der die Inhalte im Verzeichnis resources/webroot über die Adresse localhost:8080 bereitstellt.
Das ist aus dem Grund nötig, als dass der Code ohne CORS Beschwerden seitens des Browsers abläuft. Würde man den Code z.B. für ein Smartphone vorbereiten, so könnte man auf den HTTP-Server an dieser Stelle verzichten.

## Demo-Starten
- Auschecken
- mvn clean install aufrufen
- java -jar target/...-fat.jar ausführen
- Über localhost:8080 ist die UI zu sehen.
- Login ist über Username: peter und Passwort: pan möglich.
