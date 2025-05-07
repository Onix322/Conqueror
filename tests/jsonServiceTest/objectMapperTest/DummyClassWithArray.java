package tests.jsonServiceTest.objectMapperTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DummyClassWithArray{
    private String name;
    private Integer age;
    private List<String> strings;

    public DummyClassWithArray() {
    }

    public DummyClassWithArray(String name, Integer age, List<String> strings) {
        this.name = name;
        this.age = age;
        this.strings = strings;
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

    public List<String> getStrings() {
        return strings;
    }

    public void setStrings(List<String> strings) {
        this.strings = strings;
    }

    @Override
    public String toString() {
        return "DummyClassWithArray{" +
                "name='" + name + '\'' +
                ", age=" + age +
                ", strings=" + strings +
                '}';
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;
        DummyClassWithArray that = (DummyClassWithArray) object;
        return Objects.equals(getName(), that.getName()) && Objects.equals(getAge(), that.getAge()) && Objects.equals(getStrings(), that.getStrings());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getAge(), getStrings());
    }
}
