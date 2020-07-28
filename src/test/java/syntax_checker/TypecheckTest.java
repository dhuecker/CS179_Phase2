package syntax_checker;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import java.io.*;
import java.util.Scanner;

import static org.junit.Assert.*;


@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TypecheckTest {
    void passFileToMain(String name) throws IOException {
        String[] args = null;
        final InputStream og = System.in;
        try {
            final FileInputStream finput = new FileInputStream(new File("src/test/resources/input_files/" + name));
            System.setIn(finput);
            Typecheck.typeCheck();
            finput.close();
        } finally {
            System.setIn(og);
        }
    }

    String testFile(String name) throws IOException {
        // Setup
        final PrintStream ogOut = System.out;
        final ByteArrayOutputStream myOut = new ByteArrayOutputStream();
        System.setOut(new PrintStream(myOut));

        passFileToMain(name);
        System.setOut(ogOut);
        myOut.close();

        return myOut.toString();
    }

    @Test
    public void basic() throws IOException {
        assertEquals(testFile("Basic.java"), "Program type checked successfully\n");
    }

    @Test
    public void basicError() throws IOException {
        assertEquals(testFile("Basic-error.java"), "Type error\n");
    }

    @Test
    public void binaryTree() throws IOException {
        assertEquals(testFile("BinaryTree.java"), "Program type checked successfully\n");
    }

    @Test
    public void binaryTreeError() throws IOException {
        assertEquals(testFile("BinaryTree-error.java"), "Type error\n");
    }

    @Test
    public void bubbleSort() throws IOException {
        assertEquals(testFile("BubbleSort.java"), "Program type checked successfully\n");
    }

    @Test
    public void bubbleSortError() throws IOException {
        assertEquals(testFile("BubbleSort-error.java"), "Type error\n");
    }

    @Test
    public void factorial() throws IOException {
        assertEquals(testFile("Factorial.java"), "Program type checked successfully\n");
    }

    @Test
    public void factorialError() throws IOException {
        assertEquals(testFile("Factorial-error.java"), "Type error\n");
    }

    @Test
    public void linearSearch() throws IOException {
        assertEquals(testFile("LinearSearch.java"), "Program type checked successfully\n");
    }

    @Test
    public void linearSearchError() throws IOException {
        assertEquals(testFile("LinearSearch-error.java"), "Type error\n");
    }

    @Test
    public void linkedList() throws IOException {
        assertEquals(testFile("LinkedList.java"), "Program type checked successfully\n");
    }

    @Test
    public void LinkedListError() throws IOException {
        assertEquals(testFile("LinkedList-error.java"), "Type error\n");
    }

    @Test
    public void moreThan() throws IOException {
        assertEquals(testFile("MoreThan4.java"), "Program type checked successfully\n");
    }

    @Test
    public void moreThanError() throws IOException {
        assertEquals(testFile("MoreThan4-error.java"), "Type error\n");
    }

    @Test
    public void quickSort() throws IOException {
        assertEquals(testFile("QuickSort.java"), "Program type checked successfully\n");
    }

    @Test
    public void quickSortError() throws IOException {
        assertEquals(testFile("QuickSort-error.java"), "Type error\n");
    }

    @Test
    public void treeVisitor() throws IOException {
        assertEquals(testFile("TreeVisitor.java"), "Program type checked successfully\n");
    }

    @Test
    public void treeVisitorError() throws IOException {
        assertEquals(testFile("TreeVisitor-error.java"), "Type error\n");
    }

}
