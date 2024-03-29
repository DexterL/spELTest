package org.example;

public class Entity {
    private String ID;
    private String name;

    public Entity() {
        super();
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Entity(String ID, String name) {
        this.ID = ID;
        this.name = name;
    }
}
