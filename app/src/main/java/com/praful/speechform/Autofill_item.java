package com.praful.speechform;

public class Autofill_item {
    Input input;
    String key,value;

    public Autofill_item() {
    }

    public Input getInput() {
        return input;
    }

    public void setInput(Input input) {
        this.input = input;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Autofill_item(Input input, String key, String value) {
        this.input = input;
        this.key = key;
        this.value = value;
    }
}
