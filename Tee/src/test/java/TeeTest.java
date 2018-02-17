import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.util.Scanner;

import static org.junit.Assert.*;

public class TeeTest {
    String data = "Hello, World!";
    @Before
    public void clearFile() {
        try (PrintWriter writer = new PrintWriter(new File("a.txt"))) {
            writer.print("");
            writer.flush();
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    @Test
    public void testTeeSimple() throws Exception {
        System.setIn(new ByteArrayInputStream(data.getBytes()));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        System.setOut(new PrintStream(baos));
        Tee.main(new String[]{"a.txt"});

        assertEquals(data, baos.toString().substring(0, data.length()));

        Scanner scanner = new Scanner(new FileInputStream(new File("a.txt")));
        assertEquals(data, scanner.nextLine());
    }

    @Test
    public void testTee() throws Exception {
        System.setIn(new ByteArrayInputStream(data.getBytes()));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        System.setOut(new PrintStream(baos));
        Tee.main(new String[]{"a.txt"});

        System.setIn(new ByteArrayInputStream(data.getBytes()));
        Tee.main(new String[]{"--append", "a.txt"});

        assertEquals(30, baos.size());

        Scanner scanner = new Scanner(new FileInputStream(new File("a.txt")));
        assertEquals(data, scanner.nextLine());
    }
}