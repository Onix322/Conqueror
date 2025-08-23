# Conqueror Framework & Build Tool

Conqueror is a self-built Java ecosystem composed of:

- a **modular web framework**, inspired by Spring but fully decoupled,
- a **custom build tool** designed to replace Maven for personal projects.

This project demonstrates a deep understanding of HTTP processing, DI, reflection, JSON/XML parsing, and Maven dependency resolution.

---

## 🛠 Quick Start

#### 1. **Install Java**  
   **NOTE:** After installing Java, if you don’t use an IDE to run the app, make sure you have `JAVA_HOME` set in your OS environment. Basic Java installation **does not always set it automatically**.

#### 2. **Set `JAVA_HOME`**:
   **NOTE:** If you use an IDE to run the source code (e.g. IntelliJ IDEA) you may not need to do those steps. But to be sure everything runs properly is **recommended** follow all of them. 

- Windows
```bash
    1. Install a JDK (e.g., Java 24) from [Oracle](https://www.oracle.com/java/technologies/javase-downloads.html) or OpenJDK.
    
    # Set JAVA_HOME environment variable (if not set already)
    2. Open **Control Panel → System → Advanced system settings → Environment Variables**
    
    3. Add a new **System variable**:
       - Variable name: `JAVA_HOME`
       - Variable value: e.g., `C:\Program Files\Java\jdk-24`
       
    # Add it in PATH (if not yet)
    4. Edit the `Path` variable and add: `%JAVA_HOME%\bin`
    
    5. Open a new Command Prompt and verify:
       - echo %JAVA_HOME%
       - java -version
```

- Linux (Ubuntu/Debian)
```bash
    sudo apt update
    sudo apt install openjdk-24-jdk -y
    
    # Set JAVA_HOME and update PATH persistently in your shell config (~/.bashrc or ~/.zshrc)
    echo 'export JAVA_HOME=/usr/lib/jvm/java-24-openjdk-amd64' >> ~/.bashrc
    echo 'export PATH=$JAVA_HOME/bin:$PATH' >> ~/.bashrc
    
    # Apply changes immediately
    source ~/.bashrc
    
    # Verify
    echo $JAVA_HOME
    java -version
```
    
- macOS
```bash
    # Install Java (e.g., via Homebrew or official JDK)
    brew install openjdk@24
    
    # Set JAVA_HOME persistently
    echo 'export JAVA_HOME=$(/usr/libexec/java_home)' >> ~/.zshrc
    echo 'export PATH=$JAVA_HOME/bin:$PATH' >> ~/.zshrc
    
    # Apply changes immediately
    source ~/.zshrc
    
    # Verify
    echo $JAVA_HOME
    java -version
```

#### 3. **Clone the repository**
```bash
    git clone <repo-url>
```

#### 4. **Set up project in IDE**
- Mark the main directory as **source root** (if is not yet).
- Add `build_tool/target/libs` as **Library** and include it in **Modules**.

#### 5. **Start the application (IDE)**
- Run the main class or script
- Type `help` in the app to see all available commands

#### 5.1 **Build & Run (CLI)**
```bash
    javac Main.java
    java -cp build_tool/target/libs Main
```

## Start after compilation

### 1. Compile with commands:
```bash
  build
  jar
```

### 2. Run app:
#### Windows

- You must create .bat file to run it
- Use the following script:

```bash
    @echo off
    java -cp "app.jar;classes" framework.src.App -boot
    pause
```
#### Linux / macOS

- You must create .sh file to run it
- Use the following script:
```bash
    #!/bin/bash
    java -cp "app.jar:classes" framework.src.App -boot
    read -p "Press [Enter] key to continue..."
```
- Make it executable:
```bash
    chmod +x run.sh
```

## 🧠 Architecture Overview
```
CONQUEROR
│   .gitignore
│   conqueror.iml
│   Main.java
│   README.md
│
├───build_tool
│   │   deps.xml
│   │
│   ├───cli
│   │   │   InterfaceCLI.java
│   │   │
│   │   ├───command
│   │   │       BuildCommand.java
│   │   │       Command.java
│   │   │       CommandRegistry.java
│   │   │       CommandResult.java
│   │   │       HelpCommand.java
│   │   │       InitCommand.java
│   │   │       JarCommand.java
│   │   │       NoCommand.java
│   │   │       QuitCommand.java
│   │   │       RunCommand.java
│   │   │       StartCommand.java
│   │   │       StatusCommand.java
│   │   │       StopCommand.java
│   │   │
│   │   ├───console
│   │   │       Console.java
│   │   │   
│   │   └───process
│   │           JavaProcessManager.java
│   │
│   ├───target
│   │   └───libs
│   └───utilities
│       │   ArtifactValidator.java
│       │   Downloader.java
│       │   JarResolver.java
│       │   Loader.java
│       │   UrlAccessor.java
│       │
│       ├───depsReader
│       │   │   DepsReader.java
│       │   │
│       │   ├───handlers
│       │   │       XMLHandler.java
│       │   │       XMLHandlerFactory.java
│       │   │
│       │   └───supportedTagsClasses
│       │       │   TagElement.java
│       │       │
│       │       └───artifact
│       │           │   Artifact.java
│       │           │   VersionedArtifact.java
│       │           │
│       │           ├───dependency
│       │           │       Dependencies.java
│       │           │       Dependency.java
│       │           │       DependencyManagement.java
│       │           │
│       │           ├───exclusion
│       │           │       Exclusion.java
│       │           │       Exclusions.java
│       │           │
│       │           ├───parent
│       │           │       Parent.java
│       │           │
│       │           └───xml
│       │               │   XMLParsed.java
│       │               │
│       │               ├───metadata
│       │               │       Metadata.java
│       │               │       Versioning.java
│       │               │       Versions.java
│       │               │
│       │               └───project
│       │                       Project.java
│       │
│       ├───linkGenerator
│       │   │   LinkGenerator.java
│       │   │
│       │   └───link
│       │           Link.java
│       │           LinkExtension.java
│       │           VersionedLink.java
│       │
│       └───version
│           │   FixedVersion.java
│           │   IntervalVersion.java
│           │   Version.java
│           │
│           └───versionHandler
│                   DefaultProperties.java
│                   VersionHandler.java
│                   VersionHandlerContract.java
│                   VersionIntervalDirection.java
│                   VersionParser.java
│
├───configuration
│       config.properties
│       Configuration.java
│       ConfigurationImpl.java
│
├───framework
│   ├───src
│   │   │   App.java
│   │   │
│   │   ├───boot
│   │   │       Boot.java
│   │   │
│   │   ├───com
│   │   │   ├───app
│   │   │   │   │   APP_EXAMPLE.md
│   │   │   │   │
│   │   │   │   ├───controller
│   │   │   │   ├───entity
│   │   │   │   ├───repository
│   │   │   │   └───service
│   │   │   └───app_config
│   │   │           EXAMPLE_CONFIG.md
│   │   │
│   │   └───server
│   │       ├───annotations
│   │       │   ├───component
│   │       │   │   │   Component.java
│   │       │   │   │   ComponentEntity.java
│   │       │   │   │
│   │       │   │   └───configuration
│   │       │   │           ComponentConfig.java
│   │       │   │           ForceInstance.java
│   │       │   │
│   │       │   └───controller
│   │       │       │   Controller.java
│   │       │       │
│   │       │       └───mapping
│   │       │           │   Mapping.java
│   │       │           │
│   │       │           ├───methods
│   │       │           │       DeleteMapping.java
│   │       │           │       GetMapping.java
│   │       │           │       PatchMethod.java
│   │       │           │       PostMapping.java
│   │       │           │       PutMapping.java
│   │       │           │
│   │       │           └───parameters
│   │       │                   RequestBody.java
│   │       │
│   │       ├───database
│   │       │       Persistence.java
│   │       │
│   │       ├───environment
│   │       │       Environment.java
│   │       │
│   │       ├───exceptions
│   │       │       AnnotationException.java
│   │       │       CircularDependencyException.java
│   │       │       ConfigPropertyNonExistent.java
│   │       │       ConnectionException.java
│   │       │       DuplicateMappingMethod.java
│   │       │       HttpProcessFailed.java
│   │       │       HttpStartLineIncorrect.java
│   │       │       IllegalClassException.java
│   │       │       IncompatibleTypeChangeException.java
│   │       │       JsonNotValid.java
│   │       │       JsonPropertyFormatError.java
│   │       │       MissingHttpStartLine.java
│   │       │       NoCompatibleHttpVersionFound.java
│   │       │       NoEmptyConstructorFound.java
│   │       │       NoEntityMatchesJson.java
│   │       │       NoSuchEntity.java
│   │       │       NoSuchJsonPropertyError.java
│   │       │       SchemaModeNotSupported.java
│   │       │
│   │       ├───handlers
│   │       │       RouteHandler.java
│   │       │       TransformationHandler.java
│   │       │
│   │       ├───httpServer
│   │       │   │   HttpServer.java
│   │       │   │   HttpServerImpl.java
│   │       │   │
│   │       │   └───utils
│   │       │       │   HttpVersion.java
│   │       │       │
│   │       │       ├───httpMethod
│   │       │       │       BodyRequirement.java
│   │       │       │       HttpMethod.java
│   │       │       │
│   │       │       ├───request
│   │       │       │   ├───httpRequest
│   │       │       │   │       HttpRequest.java
│   │       │       │   │       HttpRequestBuilder.java
│   │       │       │   │
│   │       │       │   ├───httpRequestBody
│   │       │       │   │       HttpRequestBody.java
│   │       │       │   │
│   │       │       │   ├───httpRequestHeader
│   │       │       │   │       HttpRequestHeader.java
│   │       │       │   │       HttpRequestHeaderFactory.java
│   │       │       │   │
│   │       │       │   └───httpRequestStartLine
│   │       │       │           HttpRequestStartLine.java
│   │       │       │           HttpRequestStartLineFactory.java
│   │       │       │
│   │       │       ├───response
│   │       │       │   │   HttpConnectionType.java
│   │       │       │   │   HttpStatus.java
│   │       │       │   │
│   │       │       │   ├───httpResponse
│   │       │       │   │       HttpResponse.java
│   │       │       │   │       HttpResponseFactory.java
│   │       │       │   │
│   │       │       │   ├───httpResponseBody
│   │       │       │   │       HttpResponseBody.java
│   │       │       │   │
│   │       │       │   ├───httpResponseHeaders
│   │       │       │   │       HttpResponseHeader.java
│   │       │       │   │
│   │       │       │   └───httpResponseStartLine
│   │       │       │           HttpResponseStartLine.java
│   │       │       │
│   │       │       ├───responseEntity
│   │       │       │       ResponseFailed.java
│   │       │       │       ResponseSuccessful.java
│   │       │       │
│   │       │       └───route
│   │       │               ControllerRoute.java
│   │       │               MethodRoute.java
│   │       │               PathVariable.java
│   │       │
│   │       ├───logger
│   │       │       Logger.java
│   │       │
│   │       ├───managers
│   │       │   │   ExceptionManager.java
│   │       │   │
│   │       │   └───controllerManager
│   │       │           ControllerManager.java
│   │       │           ControllerManagerImpl.java
│   │       │
│   │       ├───metadata
│   │       │       ControllerMetaData.java
│   │       │       MetaData.java
│   │       │       MethodMetaData.java
│   │       │       RouteMetaData.java
│   │       │
│   │       ├───parsers
│   │       │   ├───json
│   │       │   │   │   JsonService.java
│   │       │   │   │   JsonServiceImpl.java
│   │       │   │   │
│   │       │   │   └───utils
│   │       │   │       ├───coordinate
│   │       │   │       │       Coordinate.java
│   │       │   │       │
│   │       │   │       ├───formatter
│   │       │   │       │       JsonFormat.java
│   │       │   │       │       JsonFormatedString.java
│   │       │   │       │
│   │       │   │       ├───mapper
│   │       │   │       │       JsonMapper.java
│   │       │   │       │       ObjectMapper.java
│   │       │   │       │
│   │       │   │       ├───navigator
│   │       │   │       │       JsonNavigator.java
│   │       │   │       │
│   │       │   │       ├───parser
│   │       │   │       │       JsonParser.java
│   │       │   │       │       Parser.java
│   │       │   │       │
│   │       │   │       ├───properties
│   │       │   │       │       JsonKey.java
│   │       │   │       │       JsonKeyValue.java
│   │       │   │       │       JsonProperty.java
│   │       │   │       │       JsonValue.java
│   │       │   │       │
│   │       │   │       ├───types
│   │       │   │       │       JsonArray.java
│   │       │   │       │       JsonIterator.java
│   │       │   │       │       JsonObject.java
│   │       │   │       │       JsonType.java
│   │       │   │       │
│   │       │   │       └───validator
│   │       │   │               JsonValidator.java
│   │       │   │
│   │       │   └───primitiveParser
│   │       │           PrimitiveParser.java
│   │       │
│   │       └───processors
│   │           ├───context
│   │           │       ApplicationContext.java
│   │           │
│   │           ├───metadata
│   │           │       ControllerMetaDataProcessor.java
│   │           │       MetaDataProcessor.java
│   │           │       MethodMetaDataProcessor.java
│   │           │
│   │           └───route
│   │                   RouteProcessor.java
│   │
│   └───test
└───result
    ├───app
    └───jars

```
---

## 🚀 Key Features

### ✅ Framework

- Custom-built HTTP server
- Annotation-based routing (`@GetMapping`, `@PostMapping`, etc.)
- Custom Dependency Injection (DI) container
- Handcrafted JSON parser with no external libraries
- Optional ORM integration (e.g: Hibernate)
- Exception management system
- Minimal configuration, high extensibility

> 🔧 To use an ORM (e.g. Hibernate), or any other class as a component from ApplicationContext, install it as a dependency in deps.xml and configure it easily in the `framework/src/com/config` directory \
> by creating a configuration class (e.g. HibernateConfig) using `@ComponentConfig` over the class and `@ForceInstance` over the method annotations. \
> Any parameters should be objects which are registered in the ApplicationContext.\ 
> The class (HibernateConfig) should return the instance of the respective class. \
> If you want an Annotation to be recognized by the ApplicationContext, you have to register it in App.java. (E.g. @Entity from Hibernate).

### ✅ Build Tool

- Downloads dependencies from Maven Central
- Parses POM files using SAX
- Resolves latest version within intervals
- Handles transitive dependencies and exclusions
- Automatically generates download URLs for artifacts
- Stores all resolved dependencies in `build_tool/target/libs`

> ⚠️ To include dependencies at runtime, you must manually add `build_tool/target/libs` to your **project classpath**. 
> E.g.: In IntelliJ: `Project Structure > Modules > Dependencies > + Add Folder`.

---

> Add your custom entities and controllers inside `framework/src/com/app/`
> Add your custom app configuration (e.g. HibernateConfig) and controllers inside `framework/src/com/app/`

## 📦 Build Tool Output

After dependency resolution, the following structure is created:

```
build_tool
│   
└──target
  │   classpath.txt # Stores classpath full -java -cp command
  │
  └───libs       # Dependencies are downloaded here
```

You must ensure `build_tool/target/libs` is part of your runtime classpath.
> E.g.: In IntelliJ: `Project Structure > Modules > Dependencies > + Add Folder`. \
> E.g.: No IDE: `java -cp build_tool/target/libs Main`


---

## 🙌 Author

Developed entirely by **Alexandru Dobos (Onix322)**, software engineer focused on custom Java architectures, tooling, and performance-centric backend development.

---

## 📄 License

Open-source. Free for whoever wants to use it.

---

For feedback or technical questions: [GitHub Issues] or reach out via direct message.

---