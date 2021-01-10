package lesson3.serial;

import java.io.Serializable;

public class Student extends Human implements Serializable {
    // Сериализация - представление объекта в двоичной формате
    private int id;
    private String name;
    // transient - не будет сериализоваться
    private transient Book book;

    Student(int id, String name, Book book) {
        this.id = id;
        this.name = name;
        this.book = book;
    }

    public void printInfo() {
        System.out.println("Id: " + id);
        System.out.println("Name: " + name);
        System.out.println("Book: " + String.valueOf(book));
    }
}
