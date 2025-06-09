package org.app.entity;

import org.server.processors.context.annotations.entity.Column;
import org.server.processors.context.annotations.entity.Entity;

@Entity(name = "test_object")
public class TestObject {

    @Column(name = "id", primary = true, autoIncrement = true, nullable = false, idColumn = true)
    private Integer id;
    @Column(name = "name")
    private String name;
    @Column(name = "number")
    private Integer number;
    @Column(name = "age")
    private Integer age;

    public TestObject() {
    }

    public TestObject(Integer id, String name, Integer number, Integer age) {
        this.id = id;
        this.name = name;
        this.number = number;
        this.age = age;
    }

    @Override
    public String toString() {
        return "TestObject{" +
                "id=" + id +
                ", hello='" + name + '\'' +
                ", number=" + number +
                ", age=" + age +
                '}';
    }
}
