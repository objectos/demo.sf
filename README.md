# SFOW (Single-File Objectos Way)

Source code for the "Single-File Objectos Way" series.

## How to Run

To run the latest iteration of the application, you'll need:

- JDK 23 or later.

Then, in a work directory, run the following commands:

```
wget -q https://repo.maven.apache.org/maven2/br/com/objectos/objectos.way/0.2.2/objectos.way-0.2.2.jar
wget -q https://raw.githubusercontent.com/objectos/demo.sfow/refs/heads/main/main/Start.java
java -p objectos.way-0.2.2.jar --add-modules objectos.way --enable-preview Start.java
```

Finally, browse to `http://localhost:8080`.

Alternatively, you can run a previous iteration of the application by following the instructions on the relevant section below.

## Iterations

This section lists all the iterations of the application in reverse chronological order.

### SFOW #001: Single-Path Text-Response Web Server 

Configures and starts the web server:

- Browse to `http://localhost:8080` and it responds with "It Works!".
- Browse to any other path and it responds with "Not Found".

Requires JDK 23 or later.
Instructions to run:

```
wget -q https://repo.maven.apache.org/maven2/br/com/objectos/objectos.way/0.2.2/objectos.way-0.2.2.jar
wget -q https://raw.githubusercontent.com/objectos/demo.sfow/refs/tags/v001/main/Start.java
java -p objectos.way-0.2.2.jar --add-modules objectos.way --enable-preview Start.java
```

After the server starts, browse to `http://localhost:8080`.

## License

Copyright (C) 2025 [Objectos Software LTDA](https://www.objectos.com.br)

Licensed under the Apache License, Version 2.0.
