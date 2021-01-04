package lesson3.serial;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;

public class ObjectReader {

    public static void main(String[] args) {
        // РУГАЕТСЯ:
        // java.io.StreamCorruptedException: invalid stream header: EFBFBDEF

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("src/main/resources/lib/student.txt"));){
            Student student = (Student) ois.readObject();
            student.printInfo();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

//        String file = "src/main/resources/lib/student.txt";
//        try {
//            FileInputStream fileInputStream = new FileInputStream(file);
//            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
//            Student student = (Student) objectInputStream.readObject();
//            student.printInfo();
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        }

    }


}
