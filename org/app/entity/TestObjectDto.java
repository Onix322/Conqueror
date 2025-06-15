package org.app.entity;

public class TestObjectDto {

    private Integer age;
    private Integer number;

    public TestObjectDto() {
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "TestObjectDto{" +
                "age=" + age +
                '}';
    }
}
