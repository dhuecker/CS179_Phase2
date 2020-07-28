package syntax_checker;

import syntaxtree.*;
import parser.*;

import java.util.ArrayList;
import java.util.List;

public class Typecheck {
  public static SymbolTable sTable;

  // Take input here
  public static MiniJavaParser parser = new MiniJavaParser(System.in);
  public static Goal root;

  public static boolean typeChecking() {
    // Set up data below
    sTable = new SymbolTable();
    SymbolTableConstructor oneVisitor = new SymbolTableConstructor();
    CheckingVisitor<String> twoVisitor = new CheckingVisitor<>();

    parser.ReInit(System.in);
//try block below
    try {
      root = parser.Goal();

      // First pass; Give the visitor data it needs
      oneVisitor.root = root;
      oneVisitor.symbolTable = sTable;

      // Construct symbol table
      root.accept(oneVisitor);


        // Second pass
        twoVisitor.root = root;
        twoVisitor.symbolTable = sTable;

        // Type check based off items stored in the symbol table
       root.accept(twoVisitor);

    } catch (Exception e) {
      System.out.println("ERROR: " + e);
      e.printStackTrace();
    }

    // If the program makes it this far, it is correct
    if (!oneVisitor.errorFound && !twoVisitor.errorFound) {
      System.out.println("Program type checked successfully");
      return true;
    }
    else {
      System.out.println("Type error");
      return false;
    }
  }
}

