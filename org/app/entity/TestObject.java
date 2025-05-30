package org.app.entity;

import org.server.httpServer.HttpMethod;
import org.server.processors.context.annotations.entity.Column;
import org.server.processors.context.annotations.entity.Entity;

import java.util.List;
import java.util.Objects;

@Entity(name = "test_object")
public class TestObject {

    @Column(name = "hello")
    private String hello;
    @Column(name = "number")
    private Integer number;
    @Column(name = "method")
    private List<HttpMethod> method;

    public TestObject() {
    }

    public TestObject(String hello, Integer number, List<HttpMethod> method) {
        this.hello = hello;
        this.number = number;
        this.method = method;
    }

    public String getHello() {
        return hello;
    }

    public void setHello(String hello) {
        this.hello = hello;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    @Override
    public String toString() {
        return "TestObject{" +
                "hello='" + hello + '\'' +
                ", number=" + number +
                '}';
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;
        TestObject that = (TestObject) object;
        return Objects.equals(getHello(), that.getHello()) && Objects.equals(getNumber(), that.getNumber());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getHello(), getNumber());
    }
}
