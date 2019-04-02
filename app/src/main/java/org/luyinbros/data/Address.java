package org.luyinbros.data;

import java.util.List;

public class Address {
    private String id;
    private String name;
    private List<Address> child;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Address> getChild() {
        return child;
    }

    public void setChild(List<Address> child) {
        this.child = child;
    }
}
