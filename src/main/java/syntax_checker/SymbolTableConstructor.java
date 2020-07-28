package syntax_checker;

import visitor.Visitor;
import syntaxtree.*;
import java.util.*;

public class SymbolTableConstructor implements Visitor {

  public Goal root;
  public SymbolTable symbolTable;

  ClassBook currentClass = null;
  MethodsBook currentMethod = null;

  public boolean errorFound = false;

  public void TypeError() {
    errorFound = true;
  }

  //
  // Helper functions as defined in the MiniJava Type System
  //
  public String idName(Identifier id) {
    return id.f0.toString();
  }

  public String classname(MainClass mc) {
    return mc.f1.f0.toString();
  }

  public String classname(ClassDeclaration c) {
    return c.f1.f0.toString();
  }

  public String classname(ClassExtendsDeclaration c) {
    return c.f1.f0.toString();
  }

  public String methodname(MethodDeclaration m) {
    return m.f2.f0.toString();
  }

  public boolean distinct(NodeOptional no) {
    // Has no parameters
    if (!no.present()) {
      return true;
    }

    FormalParameterList pl = (FormalParameterList)no.node;
    // If f1 is empty -> Always distinct (i.e. one parameter)
    int temp = pl.f1.size();
    if (temp == 0) {
      return true;
    } else {
      FormalParameter param_one;
      FormalParameter param_two;
      for (int i = -1; i < temp; i++) {
        for (int j = -1; j < temp; j++) {
          if (i == -1) {
            param_one = pl.f0;
          } else {
            param_one = ((FormalParameterRest)pl.f1.elementAt(i)).f1;
          }
          if (j == -1) {
            param_two = pl.f0;
          } else {
            param_two = ((FormalParameterRest)pl.f1.elementAt(j)).f1;
          }

          if (
            param_one.f1.f0.toString().equals(param_two.f1.f0.toString())
            && i != j
          ) {
            return false;
          }
        }
      }
    }

    return true;
  }

  public NodeChoice findClass(String classname) {
    for (int a = 0; a < root.f1.size(); a++) {
      TypeDeclaration td = (TypeDeclaration) root.f1.elementAt(a);

      String currentN;
      if (td.f0.which == 0) {
        currentN = classname((ClassDeclaration)td.f0.choice);
      } else {
        currentN = classname((ClassExtendsDeclaration)td.f0.choice);
      }

      if (classname.equals(currentN)) {
        return td.f0;
      }
    }

    return null;
  }

  public NodeListOptional fields(ClassDeclaration c) { 
    return c.f3; 
  }


  public NodeListOptional fields(ClassExtendsDeclaration c) {
    // Find the superclass
    NodeChoice superC = findClass(c.f3.f0.toString());
    NodeListOptional superF;
    if (superC.which == 0) {
      superF = (NodeListOptional)fields((ClassDeclaration)superC.choice);
    } else {
      superF = (NodeListOptional)fields((ClassExtendsDeclaration)superC.choice);
    }

    NodeListOptional typeEnv = new NodeListOptional();
    // Add class' elements to the list
    for (int a = 0; a < c.f5.size(); a++) {
      typeEnv.addNode(c.f5.elementAt(a));
    }

    // Add superclass' elements to the list
    for (int a = 0; a < superF.size(); a++) {
      typeEnv.addNode(superF.elementAt(a));
    }
    
    return typeEnv;
  }

  public MethodType methodtype(String id, String id_m) {
    NodeChoice targetClass = findClass(id);
    if (targetClass.which == 0) {
      // Regular class
      ClassDeclaration cd = (ClassDeclaration)targetClass.choice;
      for (int i = 0; i < cd.f4.size(); i++) {
        MethodDeclaration curr = (MethodDeclaration)cd.f4.elementAt(i);

        if (methodname(curr).equals(id_m)) {
          return new MethodType(curr.f1, curr.f4);
        }
      }

    } else {
      // Extends class
      ClassExtendsDeclaration cd = (ClassExtendsDeclaration)targetClass.choice;
      for (int i = 0; i < cd.f6.size(); i++) {
        MethodDeclaration curr = (MethodDeclaration)cd.f6.elementAt(i);

        if (methodname(curr).equals(id_m)) {
          return new MethodType(curr.f1, curr.f4);
        }
      }

      return methodtype(cd.f3.f0.toString(), id_m);
    }

    return null;
  }

  public boolean noOverloading(String c, String sc, String id_m) {
    MethodType one = methodtype(c, id_m);
    MethodType two = methodtype(sc, id_m);
    if (methodtype(sc, id_m) != null && one.equals(two))
      return true;

    return false;
  }

  //
  // Auto class visitors--probably don't need to be overridden.
  //
  public void visit(NodeList n) {
    for ( Enumeration<Node> e = n.elements(); e.hasMoreElements(); )
      e.nextElement().accept(this);
  }

  public void visit(NodeListOptional n) {
    if ( n.present() )
      for ( Enumeration<Node> e = n.elements(); e.hasMoreElements(); )
          e.nextElement().accept(this);
  }

  public void visit(NodeOptional n) {
    if ( n.present() )
      n.node.accept(this);
  }

  public void visit(NodeSequence n) {
    for ( Enumeration<Node> e = n.elements(); e.hasMoreElements(); )
      e.nextElement().accept(this);
  }

  public void visit(NodeToken n) { }

  //
  // User-generated visitor methods below
  //

  /**
  * f0 -> MainClass()
  * f1 -> ( TypeDeclaration() )*
  * f2 -> <EOF>
  */
  public void visit(Goal n) {
    n.f0.accept(this);
    n.f1.accept(this);
    n.f2.accept(this);
  }

  /**
  * f0 -> "class"
  * f1 -> Identifier()
  * f2 -> "{"
  * f3 -> "public"
  * f4 -> "static"
  * f5 -> "void"
  * f6 -> "main"
  * f7 -> "("
  * f8 -> "String"
  * f9 -> "["
  * f10 -> "]"
  * f11 -> Identifier()
  * f12 -> ")"
  * f13 -> "{"
  * f14 -> ( VarDeclaration() )*
  * f15 -> ( Statement() )*
  * f16 -> "}"
  * f17 -> "}"
  */
  public void visit(MainClass n) {
    ClassBook tempbook = new ClassBook(classname(n));
    currentClass = tempbook;

    n.f0.accept(this);
    n.f1.accept(this);
    n.f2.accept(this);
    n.f3.accept(this);
    n.f4.accept(this);
    n.f5.accept(this);
    n.f6.accept(this);
    n.f7.accept(this);
    n.f8.accept(this);
    n.f9.accept(this);
    n.f10.accept(this);
    n.f11.accept(this);
    n.f12.accept(this);
    n.f13.accept(this);
    n.f14.accept(this);
    n.f15.accept(this);
    n.f16.accept(this);
    n.f17.accept(this);

    tempbook.methods.put(Symbol.symbol("main"), new MethodsBook());

    symbolTable.put(Symbol.symbol(classname(n)), tempbook);
    currentMethod = null;
  }

  /**
  * f0 -> ClassDeclaration()
  *       | ClassExtendsDeclaration()
  */
  public void visit(TypeDeclaration n) {
    n.f0.accept(this);
  }

  /**
  * f0 -> "class"
  * f1 -> Identifier()
  * f2 -> "{"
  * f3 -> ( VarDeclaration() )*
  * f4 -> ( MethodDeclaration() )*
  * f5 -> "}"
  */
  public void visit(ClassDeclaration n) {
    ClassBook tempbook = new ClassBook(classname(n));
    currentClass = tempbook;

    n.f0.accept(this);
    n.f1.accept(this);
    n.f2.accept(this);
    n.f3.accept(this);
    n.f4.accept(this);
    n.f5.accept(this);

    symbolTable.put(Symbol.symbol(classname(n)), tempbook);
    currentMethod = null;
  }

  /**
  * f0 -> "class"
  * f1 -> Identifier()
  * f2 -> "extends"
  * f3 -> Identifier()
  * f4 -> "{"
  * f5 -> ( VarDeclaration() )*
  * f6 -> ( MethodDeclaration() )*
  * f7 -> "}"
  */
  public void visit(ClassExtendsDeclaration n) {
    ClassBook tempbook = new ClassBook(classname(n));
    currentClass = tempbook;

    n.f0.accept(this);
    n.f1.accept(this);
    n.f2.accept(this);
    n.f3.accept(this);
    n.f4.accept(this);
    n.f5.accept(this);
    n.f6.accept(this);
    n.f7.accept(this);

    tempbook.parent = n.f3.f0.toString();
    symbolTable.put(Symbol.symbol(classname(n)), tempbook);
    currentMethod = null;
  }

  /**
  * f0 -> Type()
  * f1 -> Identifier()
  * f2 -> ";"
  */
  public void visit(VarDeclaration n) {
    n.f0.accept(this);
    n.f1.accept(this);
    n.f2.accept(this);

    if (currentMethod == null) {
      if (currentClass.Items.alreadyEx(Symbol.symbol(idName(n.f1))))
        TypeError();

      if (n.f0.f0.choice instanceof IntegerType)
        currentClass.Items.put(Symbol.symbol(idName(n.f1)), new IntBook());
      if (n.f0.f0.choice instanceof BooleanType)
        currentClass.Items.put(Symbol.symbol(idName(n.f1)), new BoolBook());
      if (n.f0.f0.choice instanceof ArrayType)
        currentClass.Items.put(Symbol.symbol(idName(n.f1)), new ArrayBook());
      if (n.f0.f0.choice instanceof Identifier)
        currentClass.Items.put(Symbol.symbol(idName(n.f1)), new ClassBook(((Identifier) n.f0.f0.choice).f0.toString()));
    } else {
      if (currentMethod.Items.alreadyEx(Symbol.symbol(idName(n.f1))))
        TypeError();

      if (n.f0.f0.choice instanceof IntegerType)
        currentMethod.Items.put(Symbol.symbol(idName(n.f1)), new IntBook());
      if (n.f0.f0.choice instanceof BooleanType)
        currentMethod.Items.put(Symbol.symbol(idName(n.f1)), new BoolBook());
      if (n.f0.f0.choice instanceof ArrayType)
        currentMethod.Items.put(Symbol.symbol(idName(n.f1)), new ArrayBook());
      if (n.f0.f0.choice instanceof Identifier)
        currentMethod.Items.put(Symbol.symbol(idName(n.f1)), new ClassBook(((Identifier) n.f0.f0.choice).f0.toString()));
    }
  }

  /**
  * f0 -> "public"
  * f1 -> Type()
  * f2 -> Identifier()
  * f3 -> "("
  * f4 -> ( FormalParameterList() )?
  * f5 -> ")"
  * f6 -> "{"
  * f7 -> ( VarDeclaration() )*
  * f8 -> ( Statement() )*
  * f9 -> "return"
  * f10 -> Expression()
  * f11 -> ";"
  * f12 -> "}"
  */
  public void visit(MethodDeclaration n) {

    MethodsBook tempM = new MethodsBook();
    currentMethod = tempM;

    if (n.f1.f0.choice instanceof IntegerType)
      tempM.type = new IntBook();
    if (n.f1.f0.choice instanceof BooleanType)
      tempM.type = new BoolBook();
    if (n.f1.f0.choice instanceof ArrayType)
      tempM.type = new ArrayBook();
    if (n.f1.f0.choice instanceof Identifier) {

      tempM.type = new ClassTypeBook();
      ((ClassTypeBook) tempM.type).classname = idName((Identifier) n.f1.f0.choice);
    }

    n.f0.accept(this);
    n.f1.accept(this);
    n.f2.accept(this);
    n.f3.accept(this);
    n.f4.accept(this);
    n.f5.accept(this);
    n.f6.accept(this);
    n.f7.accept(this);
    n.f8.accept(this);
    n.f9.accept(this);
    n.f10.accept(this);
    n.f11.accept(this);
    n.f12.accept(this);

    if (n.f4.present()) {
      tempM.paramNum = ((FormalParameterList)(n.f4).node).f1.size();
    } else {
      tempM.paramNum = 0;
    }

    currentClass.methods.put(Symbol.symbol(methodname(n)), tempM);
  }

  /**
  * f0 -> FormalParameter()
  * f1 -> ( FormalParameterRest() )*
  */
  public void visit(FormalParameterList n) {
    n.f0.accept(this);
    n.f1.accept(this);
  }

  /**
  * f0 -> Type()
  * f1 -> Identifier()
  */
  public void visit(FormalParameter n) {
    n.f0.accept(this);
    n.f1.accept(this);

    if (currentMethod == null) {
      if (currentClass.Items.alreadyEx(Symbol.symbol(idName(n.f1))))
        TypeError();

      if (n.f0.f0.choice instanceof IntegerType)
        currentClass.Items.put(Symbol.symbol(idName(n.f1)), new IntBook());
      if (n.f0.f0.choice instanceof BooleanType)
        currentClass.Items.put(Symbol.symbol(idName(n.f1)), new BoolBook());
      if (n.f0.f0.choice instanceof ArrayType)
        currentClass.Items.put(Symbol.symbol(idName(n.f1)), new ArrayBook());
      if (n.f0.f0.choice instanceof Identifier)
        currentClass.Items.put(Symbol.symbol(idName(n.f1)), new ClassBook(((Identifier) n.f0.f0.choice).f0.toString()));
    } else {
      if (currentMethod.Items.alreadyEx(Symbol.symbol(idName(n.f1))))
        TypeError();

      if (n.f0.f0.choice instanceof IntegerType) {
        currentMethod.Items.put(Symbol.symbol(idName(n.f1)), new IntBook());
        currentMethod.pTypes.add(CheckingVisitor.IntTypeStr);
        currentMethod.params.add(idName(n.f1));
      }
      if (n.f0.f0.choice instanceof BooleanType) {
        currentMethod.Items.put(Symbol.symbol(idName(n.f1)), new BoolBook());
        currentMethod.pTypes.add(CheckingVisitor.BoolTypeStr);
        currentMethod.params.add(idName(n.f1));
      }
      if (n.f0.f0.choice instanceof ArrayType) {
        currentMethod.Items.put(Symbol.symbol(idName(n.f1)), new ArrayBook());
        currentMethod.pTypes.add(CheckingVisitor.ArrayTypeStr);
        currentMethod.params.add(idName(n.f1));
      }
      if (n.f0.f0.choice instanceof Identifier) {
        currentMethod.Items.put(Symbol.symbol(idName(n.f1)), new ClassBook(((Identifier) n.f0.f0.choice).f0.toString()));
        currentMethod.pTypes.add(((Identifier) n.f0.f0.choice).f0.toString());
        currentMethod.params.add(idName(n.f1));
      }
    }
  }

  /**
    * f0 -> ","
    * f1 -> FormalParameter()
    */
  public void visit(FormalParameterRest n) {
    n.f0.accept(this);
    n.f1.accept(this);
  }

  /**
  * f0 -> ArrayType()
  *       | BooleanType()
  *       | IntegerType()
  *       | Identifier()
  */
  public void visit(Type n) {
      n.f0.accept(this);
  }

  /**
  * f0 -> "int"
  * f1 -> "["
  * f2 -> "]"
  */
  public void visit(ArrayType n) {
    n.f0.accept(this);
    n.f1.accept(this);
    n.f2.accept(this);
  }

  /**
  * f0 -> "boolean"
  */
  public void visit(BooleanType n) {
    n.f0.accept(this);
  }

  /**
  * f0 -> "int"
  */
  public void visit(IntegerType n) {
    n.f0.accept(this);
  }

  /**
  * f0 -> Block()
  *       | AssignmentStatement()
  *       | ArrayAssignmentStatement()
  *       | IfStatement()
  *       | WhileStatement()
  *       | PrintStatement()
  */
  public void visit(Statement n) {
    n.f0.accept(this);
  }

  /**
  * f0 -> "{"
  * f1 -> ( Statement() )*
  * f2 -> "}"
  */
  public void visit(Block n) {
    n.f0.accept(this);
    n.f1.accept(this);
    n.f2.accept(this);
  }

  /**
  * f0 -> Identifier()
  * f1 -> "="
  * f2 -> Expression()
  * f3 -> ";"
  */
  public void visit(AssignmentStatement n) {
    n.f0.accept(this);
    n.f1.accept(this);
    n.f2.accept(this);
    n.f3.accept(this);
  }

  /**
  * f0 -> Identifier()
  * f1 -> "["
  * f2 -> Expression()
  * f3 -> "]"
  * f4 -> "="
  * f5 -> Expression()
  * f6 -> ";"
  */
  public void visit(ArrayAssignmentStatement n) {
    n.f0.accept(this);
    n.f1.accept(this);
    n.f2.accept(this);
    n.f3.accept(this);
    n.f4.accept(this);
    n.f5.accept(this);
    n.f6.accept(this);
  }

  /**
  * f0 -> "if"
  * f1 -> "("
  * f2 -> Expression()
  * f3 -> ")"
  * f4 -> Statement()
  * f5 -> "else"
  * f6 -> Statement()
  */
  public void visit(IfStatement n) {
    n.f0.accept(this);
    n.f1.accept(this);
    n.f2.accept(this);
    n.f3.accept(this);
    n.f4.accept(this);
    n.f5.accept(this);
    n.f6.accept(this);
  }

  /**
  * f0 -> "while"
  * f1 -> "("
  * f2 -> Expression()
  * f3 -> ")"
  * f4 -> Statement()
  */
  public void visit(WhileStatement n) {
    n.f0.accept(this);
    n.f1.accept(this);
    n.f2.accept(this);
    n.f3.accept(this);
    n.f4.accept(this);
  }

  /**
  * f0 -> "System.out.println"
  * f1 -> "("
  * f2 -> Expression()
  * f3 -> ")"
  * f4 -> ";"
  */
  public void visit(PrintStatement n) {
    n.f0.accept(this);
    n.f1.accept(this);
    n.f2.accept(this);
    n.f3.accept(this);
    n.f4.accept(this);
  }

  /**
  * f0 -> AndExpression()
  *       | CompareExpression()
  *       | PlusExpression()
  *       | MinusExpression()
  *       | TimesExpression()
  *       | ArrayLookup()
  *       | ArrayLength()
  *       | MessageSend()
  *       | PrimaryExpression()
  */
  public void visit(Expression n) {
    n.f0.accept(this);
  }

  /**
  * f0 -> PrimaryExpression()
  * f1 -> "&&"
  * f2 -> PrimaryExpression()
  */
  public void visit(AndExpression n) {
    n.f0.accept(this);
    n.f1.accept(this);
    n.f2.accept(this);
  }

  /**
  * f0 -> PrimaryExpression()
  * f1 -> "<"
  * f2 -> PrimaryExpression()
  */
  public void visit(CompareExpression n) {
    n.f0.accept(this);
    n.f1.accept(this);
    n.f2.accept(this);
  }

  /**
  * f0 -> PrimaryExpression()
  * f1 -> "+"
  * f2 -> PrimaryExpression()
  */
  public void visit(PlusExpression n) {
    n.f0.accept(this);
    n.f1.accept(this);
    n.f2.accept(this);
  }

  /**
  * f0 -> PrimaryExpression()
  * f1 -> "-"
  * f2 -> PrimaryExpression()
  */
  public void visit(MinusExpression n) {
      n.f0.accept(this);
      n.f1.accept(this);
      n.f2.accept(this);
  }

  /**
  * f0 -> PrimaryExpression()
  * f1 -> "*"
  * f2 -> PrimaryExpression()
  */
  public void visit(TimesExpression n) {
    n.f0.accept(this);
    n.f1.accept(this);
    n.f2.accept(this);
  }

  /**
  * f0 -> PrimaryExpression()
  * f1 -> "["
  * f2 -> PrimaryExpression()
  * f3 -> "]"
  */
  public void visit(ArrayLookup n) {
    n.f0.accept(this);
    n.f1.accept(this);
    n.f2.accept(this);
    n.f3.accept(this);
  }

  /**
  * f0 -> PrimaryExpression()
  * f1 -> "."
  * f2 -> "length"
  */
  public void visit(ArrayLength n) {
    n.f0.accept(this);
    n.f1.accept(this);
    n.f2.accept(this);
  }

  /**
  * f0 -> PrimaryExpression()
  * f1 -> "."
  * f2 -> Identifier()
  * f3 -> "("
  * f4 -> ( ExpressionList() )?
  * f5 -> ")"
  */
  public void visit(MessageSend n) {
    n.f0.accept(this);
    n.f1.accept(this);
    n.f2.accept(this);
    n.f3.accept(this);
    n.f4.accept(this);
    n.f5.accept(this);
  }

  /**
  * f0 -> Expression()
  * f1 -> ( ExpressionRest() )*
  */
  public void visit(ExpressionList n) {
    n.f0.accept(this);
    n.f1.accept(this);
  }

  /**
  * f0 -> ","
  * f1 -> Expression()
  */
  public void visit(ExpressionRest n) {
    n.f0.accept(this);
    n.f1.accept(this);
  }

  /**
  * f0 -> IntegerLiteral()
  *       | TrueLiteral()
  *       | FalseLiteral()
  *       | Identifier()
  *       | ThisExpression()
  *       | ArrayAllocationExpression()
  *       | AllocationExpression()
  *       | NotExpression()
  *       | BracketExpression()
  */
  public void visit(PrimaryExpression n) {
    n.f0.accept(this);
  }

  /**
  * f0 -> <INTEGER_LITERAL>
  */
  public void visit(IntegerLiteral n) {
    n.f0.accept(this);
  }

  /**
  * f0 -> "true"
  */
  public void visit(TrueLiteral n) {
    n.f0.accept(this);
  }

  /**
  * f0 -> "false"
  */
  public void visit(FalseLiteral n) {
    n.f0.accept(this);
  }

  /**
  * f0 -> <IDENTIFIER>
  */
  public void visit(Identifier n) {
    n.f0.accept(this);
  }

  /**
  * f0 -> "this"
  */
  public void visit(ThisExpression n) {
    n.f0.accept(this);
  }

  /**
  * f0 -> "new"
  * f1 -> "int"
  * f2 -> "["
  * f3 -> Expression()
  * f4 -> "]"
  */
  public void visit(ArrayAllocationExpression n) {
    n.f0.accept(this);
    n.f1.accept(this);
    n.f2.accept(this);
    n.f3.accept(this);
    n.f4.accept(this);
  }

  /**
  * f0 -> "new"
  * f1 -> Identifier()
  * f2 -> "("
  * f3 -> ")"
  */
  public void visit(AllocationExpression n) {
    n.f0.accept(this);
    n.f1.accept(this);
    n.f2.accept(this);
    n.f3.accept(this);
  }

  /**
  * f0 -> "!"
  * f1 -> Expression()
  */
  public void visit(NotExpression n) {
    n.f0.accept(this);
    n.f1.accept(this);
  }

  /**
  * f0 -> "("
  * f1 -> Expression()
  * f2 -> ")"
  */
  public void visit(BracketExpression n) {
    n.f0.accept(this);
    n.f1.accept(this);
    n.f2.accept(this);
  }
}
