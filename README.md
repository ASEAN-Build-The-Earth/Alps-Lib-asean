# ASEAN Lib
This is the ASEAN BTE implementation of AlpsLib package.
> To use the latest version of AlpsLib, you need to add the following dependencies to your pom.xml. You can find a list of all dependencies here:
> </br>https://mvn.alps-bte.com/#browse/browse:alps-lib

## Jitpack
Add jitpack repository to your `pom.xml` to access our modules.
```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```
### AseanLib-Hologram
Implemented DecentHologram Plugin
Includes DecentHologramDisplay wrapper class which can be used to create custom holograms.
```xml
<repositories>
    <!-- Decent Hologram Repository -->
    <repository>
        <id>codemc-repo</id>
        <url>https://repo.codemc.io/repository/maven-public/</url>
    </repository>
</repositories>
```
```xml
<dependencies>
    <!-- ASEAN Lib Hologram -->
    <dependency>
        <groupId>com.github.ASEAN-Build-The-Earth.Alps-Lib-asean</groupId>
        <artifactId>aseanlib-hologram</artifactId>
        <version>1.1.6</version>
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
    <!-- ASEAN Lib NPC -->
    <dependency>
        <groupId>com.github.ASEAN-Build-The-Earth.Alps-Lib-asean</groupId>
        <artifactId>aseanlib-npc</artifactId>
        <version>1.1.6</version>
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
## Github Package
The package is hosted in Github Package therefore an access key is required to authorize the package endpoints. contact us if you want to use it.

```xml
<repositories>
    <repository>
        <id>aseanbte-repo</id>
        <url>https://maven.pkg.github.com/asean-build-the-earth/</url>
    </repository>
</repositories>
```

```xml
<dependencies>
    <dependency>
        <groupId>com.github.ASEAN-Build-The-Earth.Alps-Lib-asean</groupId>
        <artifactId>aseanlib-hologram</artifactId>
        <version>1.1.6</version>
    </dependency>
    <dependency>
        <groupId>com.aseanbte.aseanlib</groupId>
        <artifactId>aseanlib-npc</artifactId>
        <version>1.1.6</version>
    </dependency>
</dependencies>
```

---
## Unimplemented
The other module has no addition from alpslib package and can be ignored.

