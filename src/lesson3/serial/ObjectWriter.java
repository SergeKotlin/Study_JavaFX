package lesson3.serial;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class ObjectWriter {

    public static void main(String[] args) {
        Student student = new Student(13, "Martin", new Book());
        student.printInfo();

        try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("src/main/resources/lib/student.txt"))) {
//            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("src/main/resources/lib/student.txt"));
            oos.writeObject(student);
//            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
