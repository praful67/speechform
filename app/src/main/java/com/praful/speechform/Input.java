package com.praful.speechform;

public class Input {
    String id;
    String tagname;

    public String getTagname() {
        return tagname;
    }

    public void setTagname(String tagname) {
        this.tagname = tagname;
    }

    public Input(String id, String name, String type, String placeholder, String label, String tagname) {
        this.id = id;
        this.tagname = tagname;
        this.name = name;
        this.label = label;
        this.type = type;
        this.placeholder = placeholder;
    }

    String name;
    String label;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Input() {
    }

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


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPlaceholder() {
        return placeholder;
    }

    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
    }

    String type;
    String placeholder;
}
