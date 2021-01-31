package lesson3;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;

public class Main {
    public static void main(String[] args) throws IOException {
//        File file = new File("src/main/resources/lib");
        File file = new File("src/main/resources/lib/index.html");
        File fileHarryPotter = new File("src/main/resources/lib/hp.txt");

//        checkDir(file);
//        testFileMethods(file);
//        fileWriter();
//        fileInputStream(fileHarryPotter);
//        inputStreamReader(fileHarryPotter);
//        inputStreamReader2(fileHarryPotter);
//        bufferedReader(fileHarryPotter);
//     Для несколько источников - коллекция из стримов:
        sequenceStream();
    }

    private static void checkDir(File file) {
        String[] str1 = file.list(((dir, name) -> name.startsWith("test")));
        String[] str2 = file.list(((dir, name) -> name.endsWith(".txt")));

        for (String s : str1) {
            System.out.println(s);
        }
        System.out.println();
        for (String s : str2) {
            System.out.println(s);
        }
    }

    private static void testFileMethods(File file) throws IOException {
        /*if (file.exists()) {
            file.createNewFile();
        } else {
            file.deleteOnExit(); // Удаление при выходе
        }*/

        File file1 = new File("src/main/resources/lib/test1.txt");
        System.out.println(file1.length());
        File file2 = new File("src/main/resources/lib/test2.txt");
        System.out.println("(потому что русский 2мя Байтами)");
        System.out.println(file2.length());
        System.out.println(file.canWrite());
        System.out.println(file.lastModified());
        System.out.println(new Date(file.lastModified()));

        System.out.println(file.isDirectory());
        System.out.println(file.isFile());

        System.out.println(file);
        System.out.println(file.getAbsoluteFile());
        System.out.println(file.getPath());
        System.out.println(file.getName());

        String[] ex = file.getName().split("\\.");
        if (ex.length > 1) {
            System.out.println(ex[ex.length - 1]);
        }
    }

    private static void fileWriter() {
        File file = new File("src/main/resources/lib/test1.txt");

        try (FileWriter writer = new FileWriter(file, true)) {
//        try {
//            FileWriter writer = new FileWriter(file, true);
            writer.write("\nHello World!");
//            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void fileInputStream(File file) {
        long start = System.currentTimeMillis();
        try (FileInputStream in = new FileInputStream(file)){
            int n;
            while ( (n = in.read()) != -1) {
                System.out.println((char) n);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(System.currentTimeMillis() - start);
    }

    private static void inputStreamReader(File file) {
        long start = System.currentTimeMillis();
        try (var in = new InputStreamReader(new FileInputStream(file), "UTF-8")){
            int n;
            while ( (n = in.read()) != -1) {
                System.out.println((char) n);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(System.currentTimeMillis() - start);
    }

    private static void inputStreamReader2(File file) {
        long start = System.currentTimeMillis();
//        try (var in = new InputStreamReader(new FileInputStream(file), "UTF-8")){
        // <-- для символов. А нус теперь блоками подаётся
        try (var in = new FileInputStream(file)){
            int n;
            byte[] byteArray = new byte[4096]; //Мин кластер в файловой системе 4КБ
            while ( (n = in.read(byteArray)) != -1) {
//                System.out.println((char) n);
                System.out.println(new String(byteArray));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(System.currentTimeMillis() - start);
    }

    private static void bufferedReader(File file) {
        long start = System.currentTimeMillis();

        try {
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);

            String strLine;

            while ((strLine = br.readLine()) != null) {
                System.out.println(strLine);
            }

            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println(System.currentTimeMillis() - start);
    }

    private static void sequenceStream() {
        ArrayList<InputStream> inputStreams = new ArrayList<>();
        try {
            inputStreams.add(new FileInputStream( "src/main/resources/lib/hp.txt"));
            inputStreams.add(new FileInputStream( "src/main/resources/lib/test2.txt"));
            inputStreams.add(new FileInputStream( "src/main/resources/lib/test1.txt"));

            SequenceInputStream in = new SequenceInputStream(Collections.enumeration(inputStreams));
            // Чтобы убрать кракозябры, как было в методе выше - нужно добавть оболочку new InputStreamReader,
            // который работает исключительно с UTF-8
            // Ещё можно работать с zip-архивами.. ZipInputStream

            int n;
            while ( (n = in.read()) != -1) {
                System.out.print((char) n);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        //1:29:00 ещё сериализацию добавить со студентами
    }
}
