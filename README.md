# ASEAN Lib
This is the ASEAN BTE implementation of AlpsLib package.
> To use the latest version of AlpsLib, you need to add the following dependencies to your pom.xml. You can find a list of all dependencies here:
> </br>https://mvn.alps-bte.com/#browse/browse:alps-lib

## Modules
The package is hosted in Github Package therefore an access key is required to authorize the package endpoints.


Repository
```xml
<repositories>
    <repository>
        <id>aseanbte-repo</id>
        <url>https://maven.pkg.github.com/asean-build-the-earth/</url>
    </repository>
</repositories>
```

Replace ```latest``` with the version of the module you want to use. You can find a list of all versions by clicking the link above.

### AseanLib-Hologram
Implemented DecentHologram Plugin
Includes an abstract DecentHologramDisplay which can be used to create custom holograms.
```xml
<repositories>
    <!-- Decent Hologram -->
    <repository>
        <id>jitpack</id>
        <url>https://jitpack.io/</url>
    </repository>
    <repository>
        <id>codemc-repo</id>
        <url>https://repo.codemc.io/repository/maven-public/</url>
    </repository>
</repositories>
```
```xml
<dependencies>
    <!-- Decent Hologram Display Class-->
    <dependency>
        <groupId>com.aseanbte.aseanlib</groupId>
        <artifactId>aseanlib-hologram</artifactId>
        <version>latest</version>
        <scope>compile</scope>
    </dependency>
    <!-- Decent Hologram -->
    <dependency>
        <groupId>com.github.decentsoftware-eu</groupId>
        <artifactId>decentholograms</artifactId>
        <version>2.8.6</version>
        <scope>provided</scope>
    </dependency>
</dependencies>
```

### AseanLib-NPC
Fancy NPC plugin manager class with Decent Hologram as npc name-tag.
```xml
<dependencies>
    <dependency>
      <groupId>com.aseanbte.aseanlib</groupId>
      <artifactId>aseanlib-npc</artifactId>
      <version>1.1.5</version>
    </dependency>
    <!-- FancyNpcs Plugin -->
    <dependency>
        <groupId>de.oliver</groupId>
        <artifactId>FancyNpcs</artifactId>
        <version>2.0.5</version>
        <scope>provided</scope>
    </dependency>
</dependencies>
```
---
## Unimplemented
The other module has no addition from alpslib package and can be ignored.

