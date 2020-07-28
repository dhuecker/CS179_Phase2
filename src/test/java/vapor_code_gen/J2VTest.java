package vapor_code_gen;

import org.junit.Test;

import java.io.*;

import static org.junit.Assert.*;

public class J2VTest {

    void passFileToMain(String name) throws IOException {
        String[] args = null;
        final InputStream ogIn = System.in;
        try {
            final FileInputStream tempIn = new FileInputStream(new File("src/test/resources/part2_input/" + name));
            System.setIn(tempIn);
            J2V.generateCode();
            tempIn.close();
        } finally {
            System.setIn(ogIn);
        }
    }

    String testFile(String name) throws IOException {
        // Setup
        final PrintStream ogOut = System.out;
        final ByteArrayOutputStream tempOut = new ByteArrayOutputStream();
        System.setOut(new PrintStream(tempOut));

        passFileToMain(name);

        // Clean up
        System.setOut(ogOut);
        tempOut.close();

        return tempOut.toString();
    }

    //Test cases below for each input file given for phase 2
    @Test
    public void addTest() throws IOException {
        // PASSES
       assertEquals("", testFile("Add.java"));
    }

    @Test
    public void binaryTree() throws IOException {
        // PASSES
        assertEquals("", testFile("BinaryTree.java"));
    }

    @Test
    public void bubbleSort() throws IOException {
        // PASSES
        assertEquals("", testFile("BubbleSort.java"));
    }

    @Test
    public void callTest() throws IOException {
        // PASSES
        assertEquals("", testFile("Call.java"));
    }

    @Test
    public void factorialTest() throws IOException {
        // PASSES
        assertEquals("", testFile("Factorial.java"));
    }

    @Test
    public void linearSearchTest() throws IOException {
        // PASSES
        assertEquals("", testFile("LinearSearch.java"));
    }

    @Test
    public void linkedListTest() throws IOException {
        // PASSES
        assertEquals("", testFile("LinkedList.java"));
    }

    @Test
    public void moreThan4() throws IOException {
        // PASSES
        assertEquals("", testFile("MoreThan4.java"));
    }

    @Test
    public void outOfBounds() throws IOException {
        // PASSES
        assertEquals("", testFile("OutOfBounds.error"));
    }

    @Test
    public void printLiteral() throws IOException {
        // PASSES
        assertEquals("", testFile("PrintLiteral.java"));
    }

    @Test
    public void quickSort() throws IOException {
        // PASSES
        assertEquals("", testFile("QuickSort.java"));
    }

    @Test
    public void treeVisitor() throws IOException {
        // PASSES
        assertEquals("", testFile("TreeVisitor.java"));
    }

    @Test
    public void varsTest() throws IOException {
        // PASSES
        assertEquals("", testFile("Vars.java"));
    }

}
