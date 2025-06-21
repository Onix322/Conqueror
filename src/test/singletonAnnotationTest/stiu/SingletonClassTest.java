package src.test.singletonAnnotationTest.stiu;

import src.test.singletonAnnotationTest.TestSingleton;

@TestSingleton
public class SingletonClassTest {
    private String name = "Class test Singleton din stiu";
    public SingletonClassTest(src.test.singletonAnnotationTest.SingletonClassTest singletonClassTest) {
    }
    @Override
    public String toString() {
        return "SingletonClassTest{" +
                "name='" + name + '\'' +
                '}';
    }
}
