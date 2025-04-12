package tests.jsonServiceTest;

import java.util.List;

public class TestJsonServicePerson {
    private String name;
    private Integer age;
    private Double height;
    private Boolean isProgrammer;
    private TestJsonServiceAddress address;
    private List<String> languagesSpoken;
    private Object certificate;

    public TestJsonServicePerson(String name, Integer age, Double height, Boolean isProgrammer, TestJsonServiceAddress address, List<String> languagesSpoken, Object certificate) {
        this.name = name;
        this.age = age;
        this.height = height;
        this.isProgrammer = isProgrammer;
        this.address = address;
        this.languagesSpoken = languagesSpoken;
        this.certificate = certificate;
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

    public Double getHeight() {
        return height;
    }

    public void setHeight(Double height) {
        this.height = height;
    }

    public Boolean getProgrammer() {
        return isProgrammer;
    }

    public void setProgrammer(Boolean programmer) {
        isProgrammer = programmer;
    }

    public TestJsonServiceAddress getAddress() {
        return address;
    }

    public void setAddress(TestJsonServiceAddress address) {
        this.address = address;
    }

    public List<String> getLanguagesSpoken() {
        return languagesSpoken;
    }

    public void setLanguagesSpoken(List<String> languagesSpoken) {
        this.languagesSpoken = languagesSpoken;
    }

    public Object getCertificate() {
        return certificate;
    }

    public void setCertificate(Object certificate) {
        this.certificate = certificate;
    }
}
