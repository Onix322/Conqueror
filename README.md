# Conqueror Framework & Build Tool

Conqueror is a self-built Java ecosystem composed of:

- a **modular web framework**, inspired by Spring but fully decoupled,
- a **custom build tool** designed to replace Maven for personal projects.

This project demonstrates deep understanding of HTTP processing, DI, reflection, JSON/XML parsing, and Maven dependency resolution.

---

## 🧠 Architecture Overview

```
conqueror/
├── Main.java                  // Application entry point
├── configuration/            // Global configuration
├── loader/                   // Build tool (downloading, parsing POMs, version resolving)
├── src/com/
│   ├── app/                  // Example CRUD application
│   ├── config/hibernate/     // Optional Hibernate integration
│   ├── server/
│   │   ├── annotations/      // DI, controller, mapping annotations
│   │   ├── database/         // Persistence interfaces
│   │   ├── exceptions/       // Custom exception system
│   │   ├── handlers/         // Data transformation and routing
│   │   ├── httpServer/       // Custom HTTP server
│   │   ├── logger/           // Custom logging system
│   │   ├── managers/         // Controller and error managers
│   │   ├── metadata/         // Routing and reflection metadata
│   │   ├── parsers/          // JSON custom parser/mapper
│   │   ├── processors/       // Annotation/meta processors
│   │   └── process/          // Core application context
├── test/                     // Unit tests for framework modules
```

---

## 🚀 Key Features

### ✅ Framework

- Custom-built HTTP server
- Annotation-based routing (`@GetMapping`, `@PostMapping`, etc.)
- Custom Dependency Injection (DI) container
- Handcrafted JSON parser with no external libraries
- Optional ORM integration (Hibernate)
- Exception management system
- Minimal configuration, high extensibility

> 🔧 To use an ORM (like Hibernate), install it separately and configure it easily in the `src/com/config` folder using `@ComponentConfig` and `@ForceInstance` annotations.

### ✅ Build Tool

- Downloads dependencies from Maven Central
- Parses POM files using SAX
- Resolves latest version within intervals
- Handles transitive dependencies and exclusions
- Automatically generates download URLs for artifacts
- Stores all resolved dependencies in `target/classes`

> ⚠️ To include dependencies at runtime, you must manually add `target/classes` to your **project classpath**. In IntelliJ: `Project Structure > Modules > Dependencies > + Add Folder`.

---

## 🛠 How to Run

1. **Clone the repo:**

```bash
git clone <repo-url>
cd conqueror
```
1.5. **Add `target/classes`**
   - Add to your IDE classpath (e.g., IntelliJ IDEA). This is crucial for runtime dependency resolution.

2. **Configure:**

   - Edit `config.properties` for local settings

3. **Build & Run:**

```bash
javac Main.java
java Main
```

> Add your custom entities and controllers inside `src/com/app/`

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
target/
├── classes/                 # Contains all downloaded dependencies (JARs)
├── metadata/                # Stores resolved POM and dependency metadata
```

You must ensure `target/classes` is part of your runtime classpath.

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

