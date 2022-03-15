package com.borham.simplecrud;

public class DatabaseFormat {
    private int id;
    private String name;
    private String age;
    private String city;

    public DatabaseFormat(String name, String city, String age) {
        this.name = name;
        this.age = age;
        this.city = city;
    }

    public DatabaseFormat(int id, String name, String city, String age) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.city = city;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
