package com.socct.log;

import java.lang.annotation.ElementType;

public class Person implements Cloneable {


    public String name;
    public String age;

    @Override
    protected Person clone() {
        try {
            return (Person) super.clone();
        } catch (Exception e) {
            return null;
        }

    }
}
