package src.test.singletonAnnotationTest;

@TestSingleton
public class AnotherTestClass {
    private String name = "Class test AnotherTestClass";
    private SingletonClassTest singletonClassTest;
    public AnotherTestClass(SingletonClassTest singletonClassTest) {
        this.singletonClassTest = singletonClassTest;
    }

    @Override
    public String toString() {
        return "AnotherTestClass{" +
                "name='" + singletonClassTest.toString() + '\'' +
                '}';
    }
}
