package org.entities;

import org.utils.entityManager.Entity;

public class TestObject implements Entity<TestObject> {

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
    public Class<TestObject> classOf() {
        return TestObject.class;
    }

    @Override
    public TestObject get(){
        return this;
    }
}
