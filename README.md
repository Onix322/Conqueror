# Conqueror Framework & Build Tool

Conqueror is a self-built Java ecosystem composed of:

- a **modular web framework**, inspired by Spring but fully decoupled,
- a **custom build tool** designed to replace Maven for personal projects.

This project demonstrates a deep understanding of HTTP processing, DI, reflection, JSON/XML parsing, and Maven dependency resolution.

---

## 🧠 Architecture Overview
```
conqueror
│   conqueror.iml
│   Main.java
│   README.md
│
├───build_tool
│   │   Loader.java
│   │   pom.xml
│   │
│   ├───target
│   │   │   classpath.txt
│   │   │
│   │   └───classes
│   └───utilities
│       │   ArtifactValidator.java
│       │   ClassPathLoader.java
│       │   Downloader.java
│       │   JarResolver.java
│       │   UrlAccessor.java
│       │
│       ├───linkGenerator
│       │   │   LinkGenerator.java
│       │   │
│       │   └───link
│       │           Link.java
│       │           LinkExtension.java
│       │           VersionedLink.java
│       │
│       ├───pomReader
│       │   │   PomReader.java
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
└───framework
    ├───src
    │   │   App.java
    │   │
    │   ├───com
    │   │   ├───app
    │   │   │   │   APP_EXAMPLE.md
    │   │   │   │
    │   │   │   ├───controller
    │   │   │   ├───entity
    │   │   │   ├───repository
    │   │   │   └───service
    │   │   └───app_config
    │   │           EXAMPLE_CONFIG.md
    │   │
    │   └───server
    │       ├───annotations
    │       │   ├───component
    │       │   │   │   Component.java
    │       │   │   │   ComponentEntity.java
    │       │   │   │
    │       │   │   └───configuration
    │       │   │           ComponentConfig.java
    │       │   │           ForceInstance.java
    │       │   │
    │       │   └───controller
    │       │       │   Controller.java
    │       │       │
    │       │       └───mapping
    │       │           │   Mapping.java
    │       │           │
    │       │           ├───methods
    │       │           │       DeleteMapping.java
    │       │           │       GetMapping.java
    │       │           │       PatchMethod.java
    │       │           │       PostMapping.java
    │       │           │       PutMapping.java
    │       │           │
    │       │           └───parameters
    │       │                   RequestBody.java
    │       │
    │       ├───database
    │       │       Persistence.java
    │       │
    │       ├───environment
    │       │       Environment.java
    │       │
    │       ├───exceptions
    │       │       AnnotationException.java
    │       │       CircularDependencyException.java
    │       │       ConfigPropertyNonExistent.java
    │       │       ConnectionException.java
    │       │       DuplicateMappingMethod.java
    │       │       HttpProcessFailed.java
    │       │       HttpStartLineIncorrect.java
    │       │       IllegalClassException.java
    │       │       IncompatibleTypeChangeException.java
    │       │       JsonNotValid.java
    │       │       JsonPropertyFormatError.java
    │       │       MissingHttpStartLine.java
    │       │       NoCompatibleHttpVersionFound.java
    │       │       NoEmptyConstructorFound.java
    │       │       NoEntityMatchesJson.java
    │       │       NoSuchEntity.java
    │       │       NoSuchJsonPropertyError.java
    │       │       SchemaModeNotSupported.java
    │       │
    │       ├───handlers
    │       │       RouteHandler.java
    │       │       TransformationHandler.java
    │       │
    │       ├───httpServer
    │       │   │   HttpServer.java
    │       │   │   HttpServerImpl.java
    │       │   │
    │       │   └───utils
    │       │       │   HttpVersion.java
    │       │       │
    │       │       ├───httpMethod
    │       │       │       BodyRequirement.java
    │       │       │       HttpMethod.java
    │       │       │
    │       │       ├───request
    │       │       │   ├───httpRequest
    │       │       │   │       HttpRequest.java
    │       │       │   │       HttpRequestBuilder.java
    │       │       │   │
    │       │       │   ├───httpRequestBody
    │       │       │   │       HttpRequestBody.java
    │       │       │   │
    │       │       │   ├───httpRequestHeader
    │       │       │   │       HttpRequestHeader.java
    │       │       │   │       HttpRequestHeaderFactory.java
    │       │       │   │
    │       │       │   └───httpRequestStartLine
    │       │       │           HttpRequestStartLine.java
    │       │       │           HttpRequestStartLineFactory.java
    │       │       │
    │       │       ├───response
    │       │       │   │   HttpConnectionType.java
    │       │       │   │   HttpStatus.java
    │       │       │   │
    │       │       │   ├───httpResponse
    │       │       │   │       HttpResponse.java
    │       │       │   │       HttpResponseFactory.java
    │       │       │   │
    │       │       │   ├───httpResponseBody
    │       │       │   │       HttpResponseBody.java
    │       │       │   │
    │       │       │   ├───httpResponseHeaders
    │       │       │   │       HttpResponseHeader.java
    │       │       │   │
    │       │       │   └───httpResponseStartLine
    │       │       │           HttpResponseStartLine.java
    │       │       │
    │       │       ├───responseEntity
    │       │       │       ResponseFailed.java
    │       │       │       ResponseSuccessful.java
    │       │       │
    │       │       └───route
    │       │               ControllerRoute.java
    │       │               MethodRoute.java
    │       │               PathVariable.java
    │       │
    │       ├───logger
    │       │       Logger.java
    │       │
    │       ├───managers
    │       │   │   ExceptionManager.java
    │       │   │
    │       │   └───controllerManager
    │       │           ControllerManager.java
    │       │           ControllerManagerImpl.java
    │       │
    │       ├───metadata
    │       │       ControllerMetaData.java
    │       │       MetaData.java
    │       │       MethodMetaData.java
    │       │       RouteMetaData.java
    │       │
    │       ├───parsers
    │       │   ├───json
    │       │   │   │   JsonService.java
    │       │   │   │   JsonServiceImpl.java
    │       │   │   │
    │       │   │   └───utils
    │       │   │       ├───coordinate
    │       │   │       │       Coordinate.java
    │       │   │       │
    │       │   │       ├───formatter
    │       │   │       │       JsonFormat.java
    │       │   │       │       JsonFormatedString.java
    │       │   │       │
    │       │   │       ├───mapper
    │       │   │       │       JsonMapper.java
    │       │   │       │       ObjectMapper.java
    │       │   │       │
    │       │   │       ├───navigator
    │       │   │       │       JsonNavigator.java
    │       │   │       │
    │       │   │       ├───parser
    │       │   │       │       JsonParser.java
    │       │   │       │       Parser.java
    │       │   │       │
    │       │   │       ├───properties
    │       │   │       │       JsonKey.java
    │       │   │       │       JsonKeyValue.java
    │       │   │       │       JsonProperty.java
    │       │   │       │       JsonValue.java
    │       │   │       │
    │       │   │       ├───types
    │       │   │       │       JsonArray.java
    │       │   │       │       JsonIterator.java
    │       │   │       │       JsonObject.java
    │       │   │       │       JsonType.java
    │       │   │       │
    │       │   │       └───validator
    │       │   │               JsonValidator.java
    │       │   │
    │       │   └───primitiveParser
    │       │           PrimitiveParser.java
    │       │
    │       └───processors
    │           ├───context
    │           │       ApplicationContext.java
    │           │
    │           ├───metadata
    │           │       ControllerMetaDataProcessor.java
    │           │       MetaDataProcessor.java
    │           │       MethodMetaDataProcessor.java
    │           │
    │           └───route
    │                   RouteProcessor.java
    │
    └───test

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

> 🔧 To use an ORM (like Hibernate), install it separately and configure it easily in the `framework/src/com/config` folder using `@ComponentConfig` and `@ForceInstance` annotations.

### ✅ Build Tool

- Downloads dependencies from Maven Central
- Parses POM files using SAX
- Resolves latest version within intervals
- Handles transitive dependencies and exclusions
- Automatically generates download URLs for artifacts
- Stores all resolved dependencies in `build_tool/target/classes`

> ⚠️ To include dependencies at runtime, you must manually add `build_tool/target/classes` to your **project classpath**. 
> E.g.: In IntelliJ: `Project Structure > Modules > Dependencies > + Add Folder`.

---

## 🛠 How to Run

1. **Clone the repo:**

```bash

git clone <repo-url>
cd conqueror
```
1.5. **Add `build_tool/target/classes`**
   - Add to your IDE classpath (e.g., IntelliJ IDEA). This is crucial for runtime dependency resolution.
   - Alternatively, you can run the build tool from the command line.
   - If you want to run the build tool from the command line, ensure you have Java installed and set up correctly.
   - You can run the build tool using the following command:
   - ```bash
      java -cp build_tool/target/classes Main
      ```
2. **Configure:**

   - Edit `build_tool/config.properties` for local settings

3. **Build & Run:**
   - use  
```bash

javac Main.java
java -cp build_tool/target/classes Main
```

> Add your custom entities and controllers inside `framework/src/com/app/`

## 🔮 Potential Extensions

- Spring Boot bridge module
- CLI interface for managing dependencies
- `.jar` export with auto-bootstrap logic
- Web-based runtime dashboard
- Cron-like job scheduler

---

## 📦 Build Tool Output

After dependency resolution, the following structure is created:

```
build_tool
│   
└──target
  │   classpath.txt # Stores classpath full -java -cp command
  │
  └───classes       # Dependencies are downloaded here
```

You must ensure `build_tool/target/classes` is part of your runtime classpath.

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

