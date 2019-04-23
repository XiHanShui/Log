package com.socct.log;

public class Test {

    public static void main(String[] args) {
        Person person = new Person();
        Person person1 = person;

        Person clone = person.clone();

        System.out.println(person==person1);



    }
}
