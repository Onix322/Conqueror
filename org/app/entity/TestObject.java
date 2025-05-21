package org.app.entity;

import java.util.Objects;

public class TestObject {

    private String hello;
    private Integer number;

    public TestObject() {
    }

    public TestObject(String hello, Integer number) {
        this.hello = hello;
        this.number = number;
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
