package org.app.entity;

public class TestObjectDto {
    private Integer id;
    private Integer age;

    public TestObjectDto() {
    }


    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "TestObjectDto{" +
                "id=" + id +
                ", age=" + age +
                '}';
    }
}
