package src.com.app.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "test_object")
public class TestObject {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
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