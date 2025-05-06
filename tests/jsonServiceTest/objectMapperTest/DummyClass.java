package tests.jsonServiceTest.objectMapperTest;

public class DummyClass {
    private String name;
    private Integer age;

    public DummyClass(){}

    public DummyClass(String name, Integer age) {
        this.name = name;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "DummyClass{" +
                "name='" + name + '\'' +
                ", age=" + age +
                '}';
    }
}
