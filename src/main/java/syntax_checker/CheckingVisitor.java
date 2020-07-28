package syntax_checker;

import syntaxtree.*;
import visitor.GJNoArguVisitor;

import java.util.Enumeration;

public class
CheckingVisitor<R> implements GJNoArguVisitor<R> {

    public static String IntTypeStr = "INT_TYPE";
    public static String BoolTypeStr = "BOOL_TYPE";
    public static String ArrayTypeStr = "ARRAY_TYPE";

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
        int n = pl.f1.size();
        if (n == 0) {
            return true;
        } else {
            FormalParameter param_one;
            FormalParameter param_two;
            for (int i = -1; i < n; i++) {
                for (int j = -1; j < n; j++) {
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
        for (int i = 0; i < root.f1.size(); i++) {
            TypeDeclaration td = (TypeDeclaration) root.f1.elementAt(i);

            String currName;
            if (td.f0.which == 0) {
                currName = classname((ClassDeclaration)td.f0.choice);
            } else {
                currName = classname((ClassExtendsDeclaration)td.f0.choice);
            }

            if (classname.equals(currName)) {
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
        NodeChoice superclass = findClass(c.f3.f0.toString());
        NodeListOptional scFields;
        if (superclass.which == 0) {
            scFields = (NodeListOptional)fields((ClassDeclaration)superclass.choice);
        } else {
            scFields = (NodeListOptional)fields((ClassExtendsDeclaration)superclass.choice);
        }

        // List which contains a typeEnv of C*CS
        NodeListOptional typeEnv = new NodeListOptional();
        // Add class' elements to the list
        for (int i = 0; i < c.f5.size(); i++) {
            typeEnv.addNode(c.f5.elementAt(i));
        }

        // Add superclass' elements to the list
        for (int i = 0; i < scFields.size(); i++) {
            typeEnv.addNode(scFields.elementAt(i));
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
        MethodType a = methodtype(c, id_m);
        MethodType b = methodtype(sc, id_m);
        if (methodtype(sc, id_m) != null && a.equals(b))
            return true;

        return false;
    }

    public boolean isSubType(String target, String id) {
        ClassBook curr = (ClassBook) symbolTable.get(Symbol.symbol(id));
        if (curr == null)
            return false;

        if (curr.parent != null && curr.parent.equals(target)) {
            return true;
        }

        while (curr.parent != null) {
            if (curr.parent.equals(target))
                return true;
            curr = (ClassBook) symbolTable.get(Symbol.symbol(curr.parent));
        }

        return false;
    }

    //
    // Auto class visitors--probably don't need to be overridden.
    //
    public R visit(NodeList n) {
        R _ret=null;
        int _count=0;
        for (Enumeration<Node> e = n.elements(); e.hasMoreElements(); ) {
            e.nextElement().accept(this);
            _count++;
        }
        return _ret;
    }

    public R visit(NodeListOptional n) {
        if ( n.present() ) {
            R _ret=null;
            int _count=0;
            for ( Enumeration<Node> e = n.elements(); e.hasMoreElements(); ) {
                e.nextElement().accept(this);
                _count++;
            }
            return _ret;
        }
        else
            return null;
    }

    public R visit(NodeOptional n) {
        if ( n.present() )
            return n.node.accept(this);
        else
            return null;
    }

    public R visit(NodeSequence n) {
        R _ret=null;
        int _count=0;
        for ( Enumeration<Node> e = n.elements(); e.hasMoreElements(); ) {
            e.nextElement().accept(this);
            _count++;
        }
        return _ret;
    }

    public R visit(NodeToken n) { return null; }

    //
    // User-generated visitor methods below
    //

    /**
     * f0 -> MainClass()
     * f1 -> ( TypeDeclaration() )*
     * f2 -> <EOF>
     */
    public R visit(Goal n) {
        R _ret=null;
        n.f0.accept(this);
        n.f1.accept(this);
        n.f2.accept(this);
        return _ret;
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
    public R visit(MainClass n) {
        R _ret=null;
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
        return _ret;
    }

    /**
     * f0 -> ClassDeclaration()
     *       | ClassExtendsDeclaration()
     */
    public R visit(TypeDeclaration n) {
        R _ret=null;
        n.f0.accept(this);
        return _ret;
    }

    /**
     * f0 -> "class"
     * f1 -> Identifier()
     * f2 -> "{"
     * f3 -> ( VarDeclaration() )*
     * f4 -> ( MethodDeclaration() )*
     * f5 -> "}"
     */
    public R visit(ClassDeclaration n) {
        R _ret=null;

        currentClass = (ClassBook) symbolTable.get(Symbol.symbol(classname(n)));

        n.f0.accept(this);
        n.f1.accept(this);
        n.f2.accept(this);
        n.f3.accept(this);
        n.f4.accept(this);
        n.f5.accept(this);
        return _ret;
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
    public R visit(ClassExtendsDeclaration n) {
        R _ret=null;

        n.f0.accept(this);
        n.f1.accept(this);
        n.f2.accept(this);
        n.f3.accept(this);
        n.f4.accept(this);
        n.f5.accept(this);
        n.f6.accept(this);
        n.f7.accept(this);
        return _ret;
    }

    /**
     * f0 -> Type()
     * f1 -> Identifier()
     * f2 -> ";"
     */
    public R visit(VarDeclaration n) {
        R _ret=null;
        n.f0.accept(this);
        n.f1.accept(this);
        n.f2.accept(this);
        return _ret;
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
    public R visit(MethodDeclaration n) {
        R _ret=null;

        currentMethod = (MethodsBook) currentClass.methods.get(Symbol.symbol(methodname(n)));

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
        String itemType = (String) n.f10.accept(this);
        n.f11.accept(this);
        n.f12.accept(this);

        if (n.f1.f0.choice instanceof IntegerType)
            _ret = (R)IntTypeStr;
        if (n.f1.f0.choice instanceof BooleanType)
            _ret = (R)BoolTypeStr;
        if (n.f1.f0.choice instanceof ArrayType)
            _ret = (R)ArrayTypeStr;
        if (n.f1.f0.choice instanceof Identifier) {
            _ret = (R)((ClassTypeBook) currentMethod.type).classname;
        }

        if (!((R)itemType).equals(_ret))
            TypeError();

        if (!distinct(n.f4))
            TypeError();

        return _ret;
    }

    /**
     * f0 -> FormalParameter()
     * f1 -> ( FormalParameterRest() )*
     */
    public R visit(FormalParameterList n) {
        R _ret=null;
        n.f0.accept(this);
        n.f1.accept(this);
        return _ret;
    }

    /**
     * f0 -> Type()
     * f1 -> Identifier()
     */
    public R visit(FormalParameter n) {
        R _ret=null;
        n.f0.accept(this);
        _ret = n.f1.accept(this);

        return _ret;
    }

    /**
     * f0 -> ","
     * f1 -> FormalParameter()
     */
    public R visit(FormalParameterRest n) {
        R _ret=null;
        n.f0.accept(this);
        n.f1.accept(this);
        return _ret;
    }

    /**
     * f0 -> ArrayType()
     *       | BooleanType()
     *       | IntegerType()
     *       | Identifier()
     */
    public R visit(Type n) {
        R _ret=null;
        n.f0.accept(this);
        return _ret;
    }

    /**
     * f0 -> "int"
     * f1 -> "["
     * f2 -> "]"
     */
    public R visit(ArrayType n) {
        R _ret=null;
        n.f0.accept(this);
        n.f1.accept(this);
        n.f2.accept(this);
        return _ret;
    }

    /**
     * f0 -> "boolean"
     */
    public R visit(BooleanType n) {
        R _ret=null;
        n.f0.accept(this);
        return _ret;
    }

    /**
     * f0 -> "int"
     */
    public R visit(IntegerType n) {
        R _ret=null;
        n.f0.accept(this);
        return _ret;
    }

    /**
     * f0 -> Block()
     *       | AssignmentStatement()
     *       | ArrayAssignmentStatement()
     *       | IfStatement()
     *       | WhileStatement()
     *       | PrintStatement()
     */
    public R visit(Statement n) {
        R _ret=null;
        n.f0.accept(this);
        return _ret;
    }

    /**
     * f0 -> "{"
     * f1 -> ( Statement() )*
     * f2 -> "}"
     */
    public R visit(Block n) {
        R _ret=null;
        n.f0.accept(this);
        n.f1.accept(this);
        n.f2.accept(this);
        return _ret;
    }

    /**
     * f0 -> Identifier()
     * f1 -> "="
     * f2 -> Expression()
     * f3 -> ";"
     */
    public R visit(AssignmentStatement n) {
        R _ret=null;
        n.f0.accept(this);
        n.f1.accept(this);
        String expType = (String) n.f2.accept(this);
        n.f3.accept(this);

        //check here
        Book tempMId = null;
        if (currentMethod != null)
            tempMId = currentMethod.Items.get(Symbol.symbol(n.f0.f0.toString()));
        Book tempCId = currentClass.Items.get(Symbol.symbol(n.f0.f0.toString()));

        if (tempMId == null && tempCId == null) {
            TypeError();
        }

        Book temp = (tempMId != null) ? tempMId : tempCId;
        String idType = "";
        if (temp instanceof IntBook) {
            idType = IntTypeStr;
        }

        if (temp instanceof BoolBook) {
            idType = BoolTypeStr;
        }

        if (temp instanceof ArrayBook) {
            idType = ArrayTypeStr;
        }

        if (temp instanceof ClassBook) {
            idType = ((ClassBook) temp).classname;
        }

        if (expType == null || (!expType.equals(idType) && !isSubType(idType, expType))) {

            TypeError();
        }

        return _ret;
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
    public R visit(ArrayAssignmentStatement n) {
        R _ret=null;
        R id = n.f0.accept(this);
        n.f1.accept(this);
        R exp1 = n.f2.accept(this);
        n.f3.accept(this);
        n.f4.accept(this);
        R exp2 = n.f5.accept(this);
        n.f6.accept(this);

        if (!id.equals(ArrayTypeStr))
            TypeError();

        if (!exp1.equals(IntTypeStr))
            TypeError();

        if (!exp2.equals(IntTypeStr))
            TypeError();

        return _ret;
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
    public R visit(IfStatement n) {
        R _ret=null;
        n.f0.accept(this);
        n.f1.accept(this);
        R express = n.f2.accept(this);
        n.f3.accept(this);
        n.f4.accept(this);
        n.f5.accept(this);
        n.f6.accept(this);

        if (!express.equals(BoolTypeStr))
            TypeError();

        return _ret;
    }

    /**
     * f0 -> "while"
     * f1 -> "("
     * f2 -> Expression()
     * f3 -> ")"
     * f4 -> Statement()
     */
    public R visit(WhileStatement n) {
        R _ret=null;
        n.f0.accept(this);
        n.f1.accept(this);
        R express = n.f2.accept(this);
        n.f3.accept(this);
        n.f4.accept(this);

        if (!express.equals(BoolTypeStr))
            TypeError();

        return _ret;
    }

    /**
     * f0 -> "System.out.println"
     * f1 -> "("
     * f2 -> Expression()
     * f3 -> ")"
     * f4 -> ";"
     */
    public R visit(PrintStatement n) {
        R _ret=null;
        n.f0.accept(this);
        n.f1.accept(this);
        R express = n.f2.accept(this);
        n.f3.accept(this);
        n.f4.accept(this);

        if (express == null || !express.equals(IntTypeStr))
            TypeError();

        return _ret;
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
    public R visit(Expression n) {
        R _ret=null;
        _ret = n.f0.accept(this);
        return _ret;
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "&&"
     * f2 -> PrimaryExpression()
     */
    public R visit(AndExpression n) {
        R _ret=null;
        R RHS = n.f0.accept(this);
        n.f1.accept(this);
        R LHS = n.f2.accept(this);

        if (!RHS.equals(LHS) || !RHS.equals(BoolTypeStr) || !LHS.equals(BoolTypeStr)) {
            TypeError();
        }

        _ret = RHS;
        return _ret;
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "<"
     * f2 -> PrimaryExpression()
     */
    public R visit(CompareExpression n) {
        R _ret=null;
        R rhs = n.f0.accept(this);
        n.f1.accept(this);
        R lhs = n.f2.accept(this);

        if (!rhs.equals(lhs) || !rhs.equals(IntTypeStr) || !lhs.equals(IntTypeStr)) {
            TypeError();
        }

        _ret = (R) BoolTypeStr;

        return _ret;
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "+"
     * f2 -> PrimaryExpression()
     */
    public R visit(PlusExpression n) {
        R _ret=null;
        R RHS = n.f0.accept(this);
        n.f1.accept(this);
        R LHS = n.f2.accept(this);

        if (!RHS.equals(LHS) || !RHS.equals(IntTypeStr) || !LHS.equals(IntTypeStr)) {
            TypeError();
        }

        _ret = RHS;
        return _ret;
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "-"
     * f2 -> PrimaryExpression()
     */
    public R visit(MinusExpression n) {
        R _ret=null;
        R rhs = n.f0.accept(this);
        n.f1.accept(this);
        R lhs = n.f2.accept(this);

        if (!rhs.equals(lhs) || !rhs.equals(IntTypeStr) || !lhs.equals(IntTypeStr)) {
            TypeError();
        }

        _ret = rhs;

        return _ret;
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "*"
     * f2 -> PrimaryExpression()
     */
    public R visit(TimesExpression n) {
        R _ret=null;
        R rhs = n.f0.accept(this);
        n.f1.accept(this);
        R lhs = n.f2.accept(this);

        if (!rhs.equals(lhs) || !rhs.equals(IntTypeStr) || !lhs.equals(IntTypeStr)) {
            TypeError();
        }

        _ret = rhs;

        return _ret;
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "["
     * f2 -> PrimaryExpression()
     * f3 -> "]"
     */
    public R visit(ArrayLookup n) {
        R _ret=null;
        R arr_express = n.f0.accept(this);
        n.f1.accept(this);
        R index_express = n.f2.accept(this);
        n.f3.accept(this);

        if (!arr_express.equals(ArrayTypeStr) || !index_express.equals(IntTypeStr))
            TypeError();

        _ret = (R)IntTypeStr;

        return _ret;
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "."
     * f2 -> "length"
     */
    public R visit(ArrayLength n) {
        R _ret=null;
        n.f0.accept(this);
        n.f1.accept(this);
        n.f2.accept(this);

        _ret = (R)IntTypeStr;

        return _ret;
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "."
     * f2 -> Identifier()
     * f3 -> "("
     * f4 -> ( ExpressionList() )?
     * f5 -> ")"
     */
    public R visit(MessageSend n) {
        R _ret=null;
        String temp = (String) n.f0.accept(this);
        n.f1.accept(this);
        n.f2.accept(this);
        n.f3.accept(this);
        n.f4.accept(this);
        n.f5.accept(this);

        if (temp == null) {
            TypeError();
            return null;
        }

        // Does method exist
        ClassBook cbook = (ClassBook) symbolTable.get(Symbol.symbol(temp));
        MethodsBook mbook = (MethodsBook) cbook.methods.get(Symbol.symbol(n.f2.f0.toString()));

        // Check
        if (mbook == null) {
            ClassBook tempCb = cbook;
            while (tempCb != null) {
                MethodsBook tempMb = (MethodsBook) tempCb.methods.get(Symbol.symbol(n.f2.f0.toString()));

                if (tempMb != null) {
                    mbook = tempMb;
                    break;
                }

                tempCb = (ClassBook) symbolTable.get(Symbol.symbol(tempCb.parent));
            }

            if (tempCb == null) {
                TypeError();
                return null;
            }
        }

        if (mbook == null) {
            TypeError();
            return null;
        }

        if (n.f4.present()) {

            if (mbook.paramNum != ((ExpressionList) n.f4.node).f1.size()) {
                TypeError();
            }

            // Are the variables in the expressions the expected types?
            if (!((ExpressionList) n.f4.node).f0.accept(this).equals(mbook.pTypes.get(0)) && mbook.paramNum != 0) {
                TypeError();
            }

            for (int i = 0; i < ((ExpressionList) n.f4.node).f1.size(); i++) {
                String currExpType = (String)((ExpressionList) n.f4.node).f1.elementAt(i).accept(this);
                if (!currExpType.equals(mbook.pTypes.get(i+1))
                        && !isSubType(mbook.pTypes.get(i+1), currExpType)
                ) {

                    TypeError();
                }
            }
        }

        if (mbook.type instanceof IntBook) {
            _ret = (R) IntTypeStr;
        }

        if (mbook.type instanceof BoolBook) {
            _ret = (R) BoolTypeStr;
        }

        if (mbook.type instanceof ArrayBook) {
            _ret = (R) ArrayTypeStr;
        }

        if (mbook.type instanceof ClassTypeBook) {
            _ret = (R)((ClassTypeBook) mbook.type).classname;
        }

        return _ret;
    }

    /**
     * f0 -> Expression()
     * f1 -> ( ExpressionRest() )*
     */
    public R visit(ExpressionList n) {
        R _ret=null;
        n.f0.accept(this);
        n.f1.accept(this);
        return _ret;
    }

    /**
     * f0 -> ","
     * f1 -> Expression()
     */
    public R visit(ExpressionRest n) {
        R _ret=null;
        n.f0.accept(this);
        R express = n.f1.accept(this);

        _ret = express;

        return _ret;
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
    public R visit(PrimaryExpression n) {
        R _ret=null;
        _ret = n.f0.accept(this);
        return _ret;
    }

    /**
     * f0 -> <INTEGER_LITERAL>
     */
    public R visit(IntegerLiteral n) {
        R _ret=null;
        n.f0.accept(this);

        _ret = (R)IntTypeStr;

        return _ret;
    }

    /**
     * f0 -> "true"
     */
    public R visit(TrueLiteral n) {
        R _ret=null;
        n.f0.accept(this);

        _ret = (R)BoolTypeStr;

        return _ret;
    }

    /**
     * f0 -> "false"
     */
    public R visit(FalseLiteral n) {
        R _ret=null;
        n.f0.accept(this);

        _ret = (R)BoolTypeStr;

        return _ret;
    }

    /**
     * f0 -> <IDENTIFIER>
     */
    public R visit(Identifier n) {
        R _ret=null;
        n.f0.accept(this);

        Book idBook = null;

        // The current method takes precedence over the current class
        if (currentMethod != null) {
            Book temp = currentMethod.Items.get(Symbol.symbol(n.f0.toString()));
            if (temp != null) idBook = temp;
        }

        if (currentClass != null && idBook == null) {
            Book temp = currentClass.Items.get(Symbol.symbol(n.f0.toString()));
            if (temp != null) idBook = temp;
        }

        if (idBook instanceof IntBook) {
            _ret = (R)IntTypeStr;
        }

        if (idBook instanceof BoolBook) {
            _ret = (R)BoolTypeStr;
        }

        if (idBook instanceof ArrayBook) {
            _ret = (R)ArrayTypeStr;
        }

        if (idBook instanceof ClassTypeBook) {
            _ret = (R)((ClassTypeBook) idBook).classname;
        }

        if (idBook instanceof ClassBook) {
            _ret = (R)((ClassBook) idBook).classname;
        }

        return _ret;
    }

    /**
     * f0 -> "this"
     */
    public R visit(ThisExpression n) {
        R _ret=null;
        n.f0.accept(this);

        String currCname = currentClass.classname;

        if (symbolTable.get(Symbol.symbol(currCname)) == null)
            TypeError();

        _ret = (R)currCname;

        return _ret;
    }

    /**
     * f0 -> "new"
     * f1 -> "int"
     * f2 -> "["
     * f3 -> Expression()
     * f4 -> "]"
     */
    public R visit(ArrayAllocationExpression n) {
        R _ret=null;
        n.f0.accept(this);
        n.f1.accept(this);
        n.f2.accept(this);
        n.f3.accept(this);
        n.f4.accept(this);

        _ret = (R)ArrayTypeStr;

        return _ret;
    }

    /**
     * f0 -> "new"
     * f1 -> Identifier()
     * f2 -> "("
     * f3 -> ")"
     */
    public R visit(AllocationExpression n) {
        R _ret=null;
        n.f0.accept(this);
        n.f1.accept(this);
        n.f2.accept(this);
        n.f3.accept(this);

        ClassBook tempClassb = (ClassBook) symbolTable.get(Symbol.symbol(n.f1.f0.toString()));
        if (tempClassb == null) {
            TypeError();

            return null;
        }

        _ret = (R)tempClassb.classname;

        return _ret;
    }

    /**
     * f0 -> "!"
     * f1 -> Expression()
     */
    public R visit(NotExpression n) {
        R _ret=null;
        n.f0.accept(this);
        _ret = n.f1.accept(this);

        if (!_ret.equals(BoolTypeStr))
            TypeError();

        return _ret;
    }

    /**
     * f0 -> "("
     * f1 -> Expression()
     * f2 -> ")"
     */
    public R visit(BracketExpression n) {
        R _ret=null;
        n.f0.accept(this);
        _ret = n.f1.accept(this);
        n.f2.accept(this);
        return _ret;
    }
}
