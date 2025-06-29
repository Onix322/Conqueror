package test.singletonAnnotationTest.stiu;

import test.singletonAnnotationTest.TestSingleton;

@TestSingleton
public class SingletonClassTest {
    private String name = "Class test Singleton din stiu";
    public SingletonClassTest(test.singletonAnnotationTest.SingletonClassTest singletonClassTest) {
    }
    @Override
    public String toString() {
        return "SingletonClassTest{" +
                "name='" + name + '\'' +
                '}';
    }
}
