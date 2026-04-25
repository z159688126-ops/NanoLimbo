name: Build XServer Jar
on: [push, workflow_dispatch]

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      
      - name: Set up JDK 17
        uses: actions/setup-java@v3
        with:
          java-version: '17'
          distribution: 'temurin'
          
      - name: Build with Maven/Gradle or Javac
        run: |
          mkdir -p build/ua/nanit/limbo
          javac src/main/java/ua/nanit/limbo/NanoLimbo.java -d build/
          cd build
          echo "Main-Class: ua.nanit.limbo.NanoLimbo" > manifest.txt
          jar cvfm server.jar manifest.txt ua/nanit/limbo/*.class
          
      - name: Upload Artifact
        uses: actions/upload-artifact@v3
        with:
          name: xserver-fixed-jar
          path: build/server.jar
