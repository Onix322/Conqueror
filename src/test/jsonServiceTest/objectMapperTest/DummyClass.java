package src.test.jsonServiceTest.objectMapperTest;

import java.util.Objects;

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

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;
        DummyClass that = (DummyClass) object;
        return Objects.equals(getName(), that.getName()) && Objects.equals(getAge(), that.getAge());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getAge());
    }
}
