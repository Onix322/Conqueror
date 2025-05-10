package test.jsonServiceTest.objectMapperTest;

import java.util.Objects;

public class DummyClassWithObject {
    private String name;
    private Integer age;
    private Boolean isProgrammer;
    private DummyClass dummyClass;
    private DummyClassWithArray object;

    public DummyClassWithObject(){
    }

    public DummyClassWithObject(String name, Integer age, Boolean isProgrammer, DummyClass dummyClass, DummyClassWithArray object) {
        this.name = name;
        this.age = age;
        this.isProgrammer = isProgrammer;
        this.dummyClass = dummyClass;
        this.object = object;
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

    public Boolean getProgrammer() {
        return isProgrammer;
    }

    public void setProgrammer(Boolean programmer) {
        isProgrammer = programmer;
    }

    public DummyClass getDummyClass() {
        return dummyClass;
    }

    public void setDummyClass(DummyClass dummyClass) {
        this.dummyClass = dummyClass;
    }

    public DummyClassWithArray getObject() {
        return object;
    }

    public void setObject(DummyClassWithArray object) {
        this.object = object;
    }

    @Override
    public boolean equals(Object object1) {
        if (object1 == null || getClass() != object1.getClass()) return false;
        DummyClassWithObject that = (DummyClassWithObject) object1;
        return Objects.equals(getName(), that.getName()) && Objects.equals(getAge(), that.getAge()) && Objects.equals(isProgrammer, that.isProgrammer) && Objects.equals(getDummyClass(), that.getDummyClass()) && Objects.equals(getObject(), that.getObject());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getAge(), isProgrammer, getDummyClass(), getObject());
    }

    @Override
    public String toString() {
        return "DummyClassWithObject{" +
                "name='" + name + '\'' +
                ", age=" + age +
                ", isProgrammer=" + isProgrammer +
                ", dummyClass=" + dummyClass +
                ", object=" + object +
                '}';
    }
}
