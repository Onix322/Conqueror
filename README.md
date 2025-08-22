# Conqueror Framework & Build Tool

Conqueror is a self-built Java ecosystem composed of:

- a **modular web framework**, inspired by Spring but fully decoupled,
- a **custom build tool** designed to replace Maven for personal projects.

This project demonstrates a deep understanding of HTTP processing, DI, reflection, JSON/XML parsing, and Maven dependency resolution.

---

## 🛠 How to Run
1. Install Java 
2. Install JDK and add jdk's bin directory to OS's env variable PATH
> E.G. linux: sudo apt install openjdk-21-jre-headless
> open the installation dir (e.g. $HOME/.jdks/openjdk-21-jre-headless/bin)
> copy path
> open $HOME/.bashrc and add "export PATH=<your path>" (e.g.: export PATH=/home/<username>/.jdks/openjdk-21-jre-headless/bin)
2. **Clone the repo:**


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
│   │   └───console
│   │           Console.java
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
> Any parameters should be objects witch are registered in the ApplicationContext.\ 
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

```bash
git clone <repo-url>
```
1.5. **Add `build_tool/target/libs`**
   - Add to your IDE classpath (e.g., IntelliJ IDEA). This is crucial for runtime dependency resolution.
   - Alternatively, you can run the build tool from the command line.
   - If you want to run the build tool from the command line, ensure you have Java installed and set up correctly.
   - If you don't use an IDE you can run the build tool using the following command: 

   - ```bash
      java -cp build_tool/target/libs Main
      ```
2. **Configure:**

   - Edit `build_tool/config.properties` for local settings and framework settings

3. **Build & Run:**
   - use  
```bash
javac Main.java
java -cp build_tool/target/libs Main
```

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

Open-source license. Free for personal and educational use.

---

For feedback or technical questions: [GitHub Issues] or reach out via direct message.

---

> *"Don't just use tools. Understand them. Then build your own."* — Alex, Creator of Conqueror

