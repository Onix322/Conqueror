# Example of how to create a custom app.

### Creating an entity
- first, we need to create a `custom object` with a `custom annotation` that will be used in the application.
- The annotation must be added in App.java file for the application to recognize it.
- The custom object (entity) is annotated with your custom annotation, which indicates is part of the context application.

#### 1. Creating Custom Annotation
```java
public @interface CustomAnnotationEntity {
    // This annotation can be used to mark classes as entities in the application.
}
```
#### 2. Adding the custom annotation to the application context
```java
public class App {
    public static void main(String[] args) {
        // implementation of the application context
        applicationContext.registerAnnotation(CustomAnnotationEntity.class);
        //... the rest of the application initialization
    }
}
```

#### 3. Creating the custom object with the custom annotation
```java
@CustomAnnotationEntity
public class TestObject {
  //... the rest of the class implementation
}
```

### Creating a controller for a custom object
- This example demonstrates how to create a controller for a custom object using the `@Controller` annotation, 
  which is part of the application's framework for handling HTTP requests.
- The controller will handle various HTTP methods such as GET, POST, PUT, and DELETE to manage the lifecycle of the custom object.
- those methods will return a `ResponseSuccessful` object, which encapsulates the HTTP status, message, and data.
- if error occurs, it will return a `ResponseError` object.
- The controller will use a service class to perform the actual business logic.
- Another important aspect is that the controller is annotated with `@Controller` and the path is specified as `/test-object`.
- this path is mandatory to access the controller's endpoints.
- All methods annotations for the controller are defined using `@GetMapping`, `@PostMapping`, `@PutMapping`, `@DeleteMapping` and `@PatchMapping` annotations, 
  which map the HTTP requests to the respective methods in the controller.

```java
@Controller("/test-object")
public class TestObjectController {

    private final TestObjectService TEST_OBJECT_SERVICE;

    private TestObjectController(TestObjectService testObjectService) {
        this.TEST_OBJECT_SERVICE = testObjectService;
    }

    // Note the {integer} is a variable 
    // In mapping annotations variables are declared by: { + variable type + }, and are used in the order of insertion.
    @GetMapping("/get/{integer}")
    public ResponseSuccessful<TestObject> getById(Integer id) {
        return ResponseSuccessful.<TestObject>builder()
                .setHttpStatus(HttpStatus.OK.getCode())
                .setMessage(HttpStatus.OK.getMessage())
                .setData(this.TEST_OBJECT_SERVICE.findById(id))
                .build();
    }
    @GetMapping
    public ResponseSuccessful<List<TestObject>> getAll() {
        return ResponseSuccessful.<List<TestObject>>builder()
                .setHttpStatus(HttpStatus.OK.getCode())
                .setMessage(HttpStatus.OK.getMessage())
                .setData(this.TEST_OBJECT_SERVICE.findAll())
                .build();
    }

    @DeleteMapping("/{integer}")
    public ResponseSuccessful<Boolean> remove(Integer id) {
        return ResponseSuccessful.<Boolean>builder()
                .setHttpStatus(HttpStatus.OK.getCode())
                .setMessage(HttpStatus.OK.getMessage())
                .setData(this.TEST_OBJECT_SERVICE.remove(id))
                .build();
    }

    @PostMapping
    public ResponseSuccessful<TestObject> create(@RequestBody TestObject object) {
        return ResponseSuccessful.<TestObject>builder()
                .setHttpStatus(HttpStatus.OK.getCode())
                .setMessage(HttpStatus.OK.getMessage())
                .setData(this.TEST_OBJECT_SERVICE.create(object))
                .build();
    }

    @PutMapping("/{integer}")
    public ResponseSuccessful<TestObject> update(@RequestBody TestObject object, Integer id) {
        return ResponseSuccessful.<TestObject>builder()
                .setHttpStatus(HttpStatus.OK.getCode())
                .setMessage(HttpStatus.OK.getMessage())
                .setData(this.TEST_OBJECT_SERVICE.update(object, id))
                .build();
    }
}
```

### Creating a service for the custom object
- The service class is responsible for the business logic of the application.
- It interacts with the repository to perform CRUD operations on the custom object.
- The service class is annotated with `@Service`, which indicates that it is a service component in the application.

```java
@Service
public class TestObjectService {

    private final TestObjectRepository TEST_OBJECT_REPOSITORY;

    public TestObjectService(TestObjectRepository testObjectRepository) {
        this.TEST_OBJECT_REPOSITORY = testObjectRepository;
    }

    public TestObject findById(Integer id) {
        return this.TEST_OBJECT_REPOSITORY.findById(id)
                .orElseThrow(() -> new NotFoundException("TestObject not found with id: " + id));
    }

    public List<TestObject> findAll() {
        return this.TEST_OBJECT_REPOSITORY.findAll();
    }

    public boolean remove(Integer id) {
        return this.TEST_OBJECT_REPOSITORY.removeById(id);
    }

    public TestObject create(TestObject object) {
        return this.TEST_OBJECT_REPOSITORY.save(object);
    }

    public TestObject update(TestObject object, Integer id) {
        if (!this.TEST_OBJECT_REPOSITORY.existsById(id)) {
            throw new NotFoundException("TestObject not found with id: " + id);
        }
        object.setId(id);
        return this.TEST_OBJECT_REPOSITORY.save(object);
    }
}
```

#### Creating a repository for the custom object
- The repository class is responsible for data access and persistence.
- It extends a generic repository class, which provides methods for CRUD operations and is annotated with `@Component`.

```java
@Component
public class RepositoryTestObject extends RepositoryHibernate<TestObject, Integer> {
    public RepositoryTestObject(SessionFactoryImpl sessionFactory) {
        super(sessionFactory);
    }
}
```
