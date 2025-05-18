package test.singletonAnnotationTest;

@Singleton
public class SingletonClassTest {
    private String name = "Class test Singleton";
    public SingletonClassTest() {;
    }
    @Override
    public String toString() {
        return "SingletonClassTest{" +
                "name='" + name + '\'' +
                '}';
    }
}
