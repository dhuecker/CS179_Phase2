package vapor_code_gen;

import visitor.*;
import syntaxtree.*;
import syntax_checker.*;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

public class VaporGenVisitor<R,A> implements GJVisitor<R,A>  {

    public GenerateVapor genvap;

    ClassBook currentClassBook = null;
    String currentClass = "";
    String currentMethod = "";

    int tempCounter = 0;
    int labelCounter = 0;

    List<String> funcCallTemps = new ArrayList<>();
    List<MethodsBook> funcCallTypes = new ArrayList<>();

    List<String> randIds = new ArrayList<>();
    List<String> randTypes = new ArrayList<>();

    static String trueString = "1";
    static String falseString = "0";

    String methodname(MethodDeclaration m) {
        return m.f2.f0.toString();
    }

    String classname(MainClass mc) {
        return mc.f1.f0.toString();
    }

    String classname(ClassDeclaration c) {
        return c.f1.f0.toString();
    }

    String classname(ClassExtendsDeclaration c) {
        return c.f1.f0.toString();
    }

 

    String createTemp() {
        String var = "t." + tempCounter;
        tempCounter++;
        return var;
    }

    String createLabel() {
        String var = "l." + labelCounter;
        labelCounter++;
        return var;
    }

    ClassRecordKeeper findRecord(String cName) {
        Iterator<ClassRecordKeeper> tempIt = J2V.classRecordKeepers.iterator();
        while (tempIt.hasNext()) {
            ClassRecordKeeper currentRec = tempIt.next();
            if (currentRec.cname.equals(cName)) {
                return currentRec;
            }
        }
        return null;
    }

    int getMethodOffset(String cName, String mName) {
        return findRecord(cName).getMethodOffset(mName);
    }

    String getMethodLabel(String cName, String mName) {
        return findRecord(cName).getMethodLabel(getMethodOffset(cName, mName));
    }

    List<String> getMethodArgs(String cName, String mName) {
        List<String> tempArgs = new ArrayList<>();

        ClassBook currClass = (ClassBook) Typecheck.symbolTable.get(Symbol.symbol(cName));
        MethodsBook currMethod = (MethodsBook) currClass.methods.get(Symbol.symbol(mName));

        for (int a = 0; a < currMethod.params.size(); a++) {
            tempArgs.add(currMethod.params.get(a));
        }

        return tempArgs;
    }
    
    public R visit(NodeList x, A arg) {
        R _ret=null;
        int _count=0;
        for ( Enumeration<Node> a = x.elements(); a.hasMoreElements(); ) {
            a.nextElement().accept(this,arg);
            _count++;
        }
        return _ret;
    }

    public R visit(NodeListOptional n, A argu) {
        if ( n.present() ) {
            R _ret=null;
            int _count=0;
            for ( Enumeration<Node> e = n.elements(); e.hasMoreElements(); ) {
                e.nextElement().accept(this,argu);
                _count++;
            }
            return _ret;
        }
        else
            return null;
    }

    public R visit(NodeOptional n, A argu) {
        if ( n.present() )
            return n.node.accept(this,argu);
        else
            return null;
    }

    public R visit(NodeSequence n, A argu) {
        R _ret=null;
        int _count=0;
        for ( Enumeration<Node> e = n.elements(); e.hasMoreElements(); ) {
            e.nextElement().accept(this,argu);
            _count++;
        }
        return _ret;
    }

    public R visit(NodeToken n, A argu) { return null; }

    //
    // User-generated visitor methods below
    //

    /**
     * f0 -> MainClass()
     * f1 -> ( TypeDeclaration() )*
     * f2 -> <EOF>
     */
    public R visit(Goal n, A argu) {
        R _ret=null;
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        n.f2.accept(this, argu);
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
    public R visit(MainClass n, A argu) {
        R _ret=null;

        genvap.addLine("func Main()");
        genvap.increaseIndent();

        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        n.f3.accept(this, argu);
        n.f4.accept(this, argu);
        n.f5.accept(this, argu);
        n.f6.accept(this, argu);
        n.f7.accept(this, argu);
        n.f8.accept(this, argu);
        n.f9.accept(this, argu);
        n.f10.accept(this, argu);
        n.f11.accept(this, argu);
        n.f12.accept(this, argu);
        n.f13.accept(this, argu);
        n.f14.accept(this, argu);
        n.f15.accept(this, argu);
        n.f16.accept(this, argu);
        n.f17.accept(this, argu);

        genvap.addLine("ret");
        genvap.descreaseIndent();
        genvap.addLine("");

        return _ret;
    }

    /**
     * f0 -> ClassDeclaration()
     *       | ClassExtendsDeclaration()
     */
    public R visit(TypeDeclaration x, A arg) {
        R _ret=null;
        x.f0.accept(this, arg);
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
    public R visit(ClassDeclaration n, A argu) {
        R _ret=null;

        currentClassBook = (ClassBook) Typecheck.symbolTable.get(Symbol.symbol(classname(n)));
        currentClass = classname(n);
        currentMethod = null;

        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        n.f3.accept(this, argu);
        n.f4.accept(this, argu);
        n.f5.accept(this, argu);
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
    public R visit(ClassExtendsDeclaration n, A argu) {
        R _ret=null;

        currentClassBook = (ClassBook) Typecheck.symbolTable.get(Symbol.symbol(classname(n)));
        currentClass = classname(n);
        currentMethod = null;

        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        n.f3.accept(this, argu);
        n.f4.accept(this, argu);
        n.f5.accept(this, argu);
        n.f6.accept(this, argu);
        n.f7.accept(this, argu);
        return _ret;
    }

    /**
     * f0 -> Type()
     * f1 -> Identifier()
     * f2 -> ";"
     */
    public R visit(VarDeclaration n, A argu) {
        R _ret=null;
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        n.f2.accept(this, argu);

        if (n.f0.f0.choice instanceof Identifier) {
            randIds.add(n.f1.f0.toString());
            randTypes.add(((Identifier) n.f0.f0.choice).f0.toString());
        }

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
    public R visit(MethodDeclaration n, A argu) {
        R _ret=null;
        currentMethod = methodname(n);
        List<String> tempArgs = getMethodArgs(currentClass, currentMethod);

        String TempargString = "";
        Iterator<String> it = tempArgs.iterator();
        while(it.hasNext()) {
            TempargString += " " + it.next();
        }
        genvap.addLine("func " + getMethodLabel(currentClass, currentMethod) + "(this" + TempargString + ")");

        genvap.increaseIndent();

        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        n.f3.accept(this, argu);
        n.f4.accept(this, argu);
        n.f5.accept(this, argu);
        n.f6.accept(this, argu);
        n.f7.accept(this, argu);
        n.f8.accept(this, argu);
        n.f9.accept(this, argu);
        String retExpress = (String) n.f10.accept(this, argu);
        n.f11.accept(this, argu);
        n.f12.accept(this, argu);

        if (findRecord(currentClass) != null) {
            int fieldOffset = findRecord(currentClass).getFieldOffset((String) retExpress);
            if (fieldOffset != -1) {
                String temp = createTemp();
                genvap.addLine(temp + " = [this + " + (fieldOffset * 4) + "]");
                retExpress = temp;
            }
        }
        genvap.addLine("ret " + retExpress);
        genvap.descreaseIndent();
        genvap.addLine("");

        return _ret;
    }

    /**
     * f0 -> FormalParameter()
     * f1 -> ( FormalParameterRest() )*
     */
    public R visit(FormalParameterList n, A argu) {
        R _ret=null;
        _ret = n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        return _ret;
    }

    /**
     * f0 -> Type()
     * f1 -> Identifier()
     */
    public R visit(FormalParameter n, A argu) {
        R _ret=null;
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);

        _ret = (R) n.f1.f0.toString();

        return _ret;
    }

    /**
     * f0 -> ","
     * f1 -> FormalParameter()
     */
    public R visit(FormalParameterRest n, A argu) {
        R _ret=null;
        n.f0.accept(this, argu);
        _ret = n.f1.accept(this, argu);
        return _ret;
    }

    /**
     * f0 -> ArrayType()
     *       | BooleanType()
     *       | IntegerType()
     *       | Identifier()
     */
    public R visit(Type n, A argu) {
        R _ret=null;
        _ret = n.f0.accept(this, argu);
        return _ret;
    }

    /**
     * f0 -> "int"
     * f1 -> "["
     * f2 -> "]"
     */
    public R visit(ArrayType n, A argu) {
        R _ret=null;
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        return _ret;
    }

    /**
     * f0 -> "boolean"
     */
    public R visit(BooleanType n, A argu) {
        R _ret=null;
        n.f0.accept(this, argu);
        return _ret;
    }

    /**
     * f0 -> "int"
     */
    public R visit(IntegerType n, A argu) {
        R _ret=null;
        n.f0.accept(this, argu);
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
    public R visit(Statement n, A argu) {
        R _ret=null;
        n.f0.accept(this, argu);
        return _ret;
    }

    /**
     * f0 -> "{"
     * f1 -> ( Statement() )*
     * f2 -> "}"
     */
    public R visit(Block n, A argu) {
        R _ret=null;
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        return _ret;
    }

    /**
     * f0 -> Identifier()
     * f1 -> "="
     * f2 -> Expression()
     * f3 -> ";"
     */
    public R visit(AssignmentStatement n, A argu) {
        R _ret=null;
        String idName = (String) n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        String expressVal = (String) n.f2.accept(this, argu);
        n.f3.accept(this, argu);

        if (expressVal.contains("::")) {
            int div = expressVal.indexOf("::");
            String allocType = expressVal.substring(0, div);
            String varName = expressVal.substring(div + 2);
            expressVal = varName;
        }

        if (findRecord(currentClass) != null) {
            int fieldOffset = findRecord(currentClass).getFieldOffset(idName);
            if (fieldOffset != -1) {
                idName = "[this + " + (fieldOffset * 4) + "]";
            }
        }

        if (findRecord(currentClass) != null) {
            int fieldOffset = findRecord(currentClass).getFieldOffset(expressVal);
            if (fieldOffset != -1) {
                expressVal = "[this + " + (fieldOffset * 4) + "]";
            }
        }

        genvap.addLine(idName + " = " + expressVal);

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
    public R visit(ArrayAssignmentStatement n, A argu) {
        R _ret=null;
        String baseAddress = (String) n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        String offsetTemp = (String)n.f2.accept(this, argu);
        n.f3.accept(this, argu);
        n.f4.accept(this, argu);
        String assignTemp = (String) n.f5.accept(this, argu);
        n.f6.accept(this, argu);

        if (findRecord(currentClass) != null) {
            int fieldOffset = findRecord(currentClass).getFieldOffset(baseAddress);
            if (fieldOffset != -1) {
                String temp = createTemp();
                genvap.addLine(temp + " = Add(this " + (fieldOffset * 4) + ")");
                genvap.addLine(temp + " = [" + temp + "]");
                baseAddress = temp;
            }
        }

        String alignedOffset = createTemp();
        genvap.addLine(alignedOffset + " = MulS(" + offsetTemp + " 4)");
        String ArrayIndex = createTemp();
        genvap.addLine(ArrayIndex + " = Add(" + baseAddress + " " + alignedOffset + ")");
        genvap.addLine(ArrayIndex + " = Add(" + ArrayIndex + " 4)");
        genvap.addLine("[" + ArrayIndex + "] = " + assignTemp);

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
    public R visit(IfStatement n, A argu) {
        R _ret=null;

        String elseLabel = createLabel();
        String endIf = createLabel();

        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        String boolRes = (String) n.f2.accept(this, argu);
        n.f3.accept(this, argu);

        if (findRecord(currentClass) != null) {
            int fieldOffset = findRecord(currentClass).getFieldOffset(boolRes);
            if (fieldOffset != -1) {
                String Temp_RHS = createTemp();
                genvap.addLine(Temp_RHS + " = [this + " + (fieldOffset * 4) + "]");
                boolRes = Temp_RHS;
            }
        }

        genvap.addLine("if0 " + boolRes + " goto " + ":" + elseLabel);
        genvap.increaseIndent();
        n.f4.accept(this, argu);
        genvap.addLine("goto :" + endIf);
        genvap.descreaseIndent();
        genvap.addLine(elseLabel + ":");
        genvap.increaseIndent();
        n.f5.accept(this, argu);
        n.f6.accept(this, argu);
        genvap.descreaseIndent();
        genvap.addLine(endIf + ":");

        return _ret;
    }

    /**
     * f0 -> "while"
     * f1 -> "("
     * f2 -> Expression()
     * f3 -> ")"
     * f4 -> Statement()
     */
    public R visit(WhileStatement n, A argu) {
        R _ret=null;

        String tempLabel = createLabel();
        String doneLabel = createLabel();

        genvap.addLine(tempLabel + ":");

        genvap.increaseIndent();

        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        String express = (String) n.f2.accept(this, argu);

        if (findRecord(currentClass) != null) {
            int fieldOffset = findRecord(currentClass).getFieldOffset(express);
            if (fieldOffset != -1) {
                String Temp_RHS = createTemp();
                genvap.addLine(Temp_RHS + " = [this + " + (fieldOffset * 4) + "]");
                express = Temp_RHS;
            }
        }

        genvap.addLine("if0 " + express + " goto " + ":" + doneLabel);
        n.f3.accept(this, argu);
        n.f4.accept(this, argu);
        genvap.descreaseIndent();
        genvap.addLine("goto :" + tempLabel);
        genvap.addLine(doneLabel + ":");

        return _ret;
    }

    /**
     * f0 -> "System.out.println"
     * f1 -> "("
     * f2 -> Expression()
     * f3 -> ")"
     * f4 -> ";"
     */
    public R visit(PrintStatement n, A argu) {
        R _ret=null;
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        R pVal = n.f2.accept(this, argu);
        n.f3.accept(this, argu);
        n.f4.accept(this, argu);

        if (findRecord(currentClass) != null) {
            int fieldOffset = findRecord(currentClass).getFieldOffset((String)pVal);
            if (fieldOffset != -1) {
                String Temp_RHS = createTemp();
                genvap.addLine(Temp_RHS + " = [this + " + (fieldOffset * 4) + "]");
                pVal = (R) Temp_RHS;
            }
        }

        genvap.addLine("PrintIntS(" + pVal + ")");

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
    public R visit(Expression n, A argu) {
        R _ret=null;
        _ret = n.f0.accept(this, argu);
        return _ret;
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "&&"
     * f2 -> PrimaryExpression()
     */
    public R visit(AndExpression n, A argu) {
        R _ret=null;
        R RHS = n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        R LHS = n.f2.accept(this, argu);

        String tempA = createTemp();
        String tempB = createTemp();
        String resultTemp = createTemp();

        if (findRecord(currentClass) != null) {
            int fieldOffset = findRecord(currentClass).getFieldOffset((String)RHS);
            if (fieldOffset != -1) {
                String Temp_RHS = createTemp();
                genvap.addLine(Temp_RHS + " = [this + " + (fieldOffset * 4) + "]");
                RHS = (R) Temp_RHS;
            }
        }

        if (findRecord(currentClass) != null) {
            int fieldOffset = findRecord(currentClass).getFieldOffset((String)LHS);
            if (fieldOffset != -1) {
                String temp_lhs = createTemp();
                genvap.addLine(temp_lhs + " = [this + " + (fieldOffset * 4) + "]");
                LHS = (R) temp_lhs;
            }
        }

        genvap.addLine(tempA + " = Eq(1 " + RHS + ")");
        genvap.addLine(tempB + " = Eq(1 " + LHS + ")");
        genvap.addLine(resultTemp + " = Eq(" + tempA + " " + tempB + ")");

        _ret = (R) resultTemp;

        return _ret;
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "<"
     * f2 -> PrimaryExpression()
     */
    public R visit(CompareExpression n, A argu) {
        R _ret=null;
        R RHS = n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        R LHS = n.f2.accept(this, argu);

        if (findRecord(currentClass) != null) {
            int fieldOffset = findRecord(currentClass).getFieldOffset((String)RHS);
            if (fieldOffset != -1) {
                String Temp_RHS = createTemp();
                genvap.addLine(Temp_RHS + " = [this + " + (fieldOffset * 4) + "]");
                RHS = (R) Temp_RHS;
            }
        }

        if (findRecord(currentClass) != null) {
            int fieldOffset = findRecord(currentClass).getFieldOffset((String)LHS);
            if (fieldOffset != -1) {
                String Temp_LHS = createTemp();
                genvap.addLine(Temp_LHS + " = [this + " + (fieldOffset * 4) + "]");
                LHS = (R) Temp_LHS;
            }
        }

        String resultTemp = createTemp();
        genvap.addLine(resultTemp + " = LtS(" + RHS + " " + LHS + ")");

        _ret = (R) resultTemp;

        return _ret;
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "+"
     * f2 -> PrimaryExpression()
     */
    public R visit(PlusExpression n, A argu) {
        R _ret=null;
        R RHS = n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        R LHS = n.f2.accept(this, argu);


        if (findRecord(currentClass) != null) {
            int fieldOffset = findRecord(currentClass).getFieldOffset((String)RHS);
            if (fieldOffset != -1) {
                String Temp_RHS = createTemp();
                genvap.addLine(Temp_RHS + " = [this + " + (fieldOffset * 4) + "]");
                RHS = (R) Temp_RHS;
            }
        }

        if (findRecord(currentClass) != null) {
            int fieldOffset = findRecord(currentClass).getFieldOffset((String)LHS);
            if (fieldOffset != -1) {
                String Temp_LHS = createTemp();
                genvap.addLine(Temp_LHS + " = [this + " + (fieldOffset * 4) + "]");
                LHS = (R) Temp_LHS;
            }
        }

        String resultTemp = createTemp();
        genvap.addLine(resultTemp + " = " + "Add(" + RHS + " " + LHS + ")");

        _ret = (R) resultTemp;

        return _ret;
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "-"
     * f2 -> PrimaryExpression()
     */
    public R visit(MinusExpression n, A argu) {
        R _ret=null;
        R RHS = n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        R LHS = n.f2.accept(this, argu);

        if (findRecord(currentClass) != null) {
            int fieldOffset = findRecord(currentClass).getFieldOffset((String)RHS);
            if (fieldOffset != -1) {
                String Temp_RHS = createTemp();
                genvap.addLine(Temp_RHS + " = [this + " + (fieldOffset * 4) + "]");
                RHS = (R) Temp_RHS;
            }
        }

        if (findRecord(currentClass) != null) {
            int fieldOffset = findRecord(currentClass).getFieldOffset((String)LHS);
            if (fieldOffset != -1) {
                String Temp_LHS = createTemp();
                genvap.addLine(Temp_LHS + " = [this + " + (fieldOffset * 4) + "]");
                LHS = (R) Temp_LHS;
            }
        }

        String resultTemp = createTemp();
        genvap.addLine(resultTemp + " = " + "Sub(" + RHS + " " + LHS + ")");

        _ret = (R) resultTemp;

        return _ret;
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "*"
     * f2 -> PrimaryExpression()
     */
    public R visit(TimesExpression n, A argu) {
        R _ret=null;
        R RHS = n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        R LHS = n.f2.accept(this, argu);

        if (findRecord(currentClass) != null) {
            int fieldOffset = findRecord(currentClass).getFieldOffset((String)RHS);
            if (fieldOffset != -1) {
                String Temp_RHS = createTemp();
                genvap.addLine(Temp_RHS + " = [this + " + (fieldOffset * 4) + "]");
                RHS = (R) Temp_RHS;
            }
        }

        if (findRecord(currentClass) != null) {
            int fieldOffset = findRecord(currentClass).getFieldOffset((String)LHS);
            if (fieldOffset != -1) {
                String Temp_LHS = createTemp();
                genvap.addLine(Temp_LHS + " = [this + " + (fieldOffset * 4) + "]");
                LHS = (R) Temp_LHS;
            }
        }

        String resultTemp = createTemp();
        genvap.addLine(resultTemp + " = " + "MulS(" + RHS + " " + LHS + ")");

        _ret = (R) resultTemp;

        return _ret;
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "["
     * f2 -> PrimaryExpression()
     * f3 -> "]"
     */
    public R visit(ArrayLookup n, A argu) {
        R _ret=null;
        String ptrTemp = (String) n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        String offsetTemp = (String) n.f2.accept(this, argu);
        n.f3.accept(this, argu);

        // TODO: Check if the value of [<val>] is gte 0
        String resultTemp = createTemp();
        String alignedOffset = createTemp();
        String tempL1 = createLabel();
        String tempL2 = createLabel();
        genvap.addLine(alignedOffset + " = " + offsetTemp);
        // alignedOffset is now the index
        String baseAddress = createTemp();
        if (findRecord(currentClass) != null) {
            int fieldOffset = findRecord(currentClass).getFieldOffset(ptrTemp);
            if (fieldOffset != -1) {
                String temp = createTemp();
                genvap.addLine(temp + " = [this + " + (fieldOffset * 4) + "]");
                ptrTemp = temp;
            }
        }

        genvap.addLine(baseAddress + " = [" + ptrTemp + "]");

        genvap.addLine("ok = LtS(" + alignedOffset + " " + baseAddress + ")");
        genvap.addLine("if ok goto :" + tempL1);
        genvap.addLine("Error(\"array index out of bounds\")");
        genvap.addLine(tempL1+":");
        genvap.addLine("ok = LtS(-1 " + offsetTemp + ")");
        genvap.addLine("if ok goto :" + tempL2);
        genvap.addLine("Error(\"array index out of bounds\")");
        genvap.addLine(tempL2 + ":");
        genvap.addLine(alignedOffset + " = MulS(" + offsetTemp + " 4)");
        genvap.addLine(alignedOffset + " = Add(" + ptrTemp + " " + alignedOffset + ")");
        genvap.addLine(alignedOffset + " = Add(" + alignedOffset + " 4)");
        genvap.addLine(resultTemp + " = [" + alignedOffset + "]");

        _ret = (R) resultTemp;

        return _ret;
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "."
     * f2 -> "length"
     */
    public R visit(ArrayLength n, A argu) {
        R _ret=null;
        String arrTemp = (String) n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        n.f2.accept(this, argu);

        String resultTemp = createTemp();

        genvap.addLine(resultTemp + " = [" + arrTemp + "]");

        _ret = (R) resultTemp;

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
    public R visit(MessageSend n, A argu) {
        R _ret=null;

        // TODO: Handle when funcOwner is not a "new <Class>" statement
        R funcOwnerTemp = n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        n.f3.accept(this, argu);
        String argsTemp = (String) n.f4.accept(this, argu);
        n.f5.accept(this, argu);

        String vTableBaseTemp = createTemp();
        String resultTemp = createTemp();
        String mName = n.f2.f0.toString();

        // Clean up args
        if (argsTemp != null) {
            String[] argTokens = argsTemp.split(" ", 100);
            StringBuilder fixed = new StringBuilder();
            for (String argToken : argTokens) {
                if (findRecord(currentClass) != null) {
                    int fieldOffset = findRecord(currentClass).getFieldOffset(argToken);
                    if (fieldOffset != -1) {
                        String temp = createTemp();
                        genvap.addLine(temp + " = [this + " + (fieldOffset * 4) + "]");
                        fixed.append(temp).append(" ");
                    } else {
                        fixed.append(argToken).append(" ");
                    }
                } else {
                    fixed.append(argToken).append(" ");
                }
            }

            argsTemp = fixed.toString();
        }

        // Primary expression was an allocation
        // <Type>::<name>
        if (((String) funcOwnerTemp).contains("::")) {
            int div = ((String) funcOwnerTemp).indexOf("::");

            String allocType = ((String) funcOwnerTemp).substring(0, div);
            String vName = ((String) funcOwnerTemp).substring(div + 2);

            int offset = getMethodOffset(allocType, mName);

            genvap.addLine(vTableBaseTemp + " = [" + vName + "]"); // Get
            genvap.addLine(vTableBaseTemp + " = [" + vTableBaseTemp + "]");
            genvap.addLine(vTableBaseTemp + " = [" + vTableBaseTemp + " + " + (offset*4) + "]");
            if (argsTemp != null)
                genvap.addLine(resultTemp + " = call " + vTableBaseTemp + "(" + vName + " " + argsTemp + ")");
            else
                genvap.addLine(resultTemp + " = call " + vTableBaseTemp + "(" + vName + ")");
        } else {
            if (funcOwnerTemp.equals("this")) {
                int offset = getMethodOffset(currentClass, mName);

                String funcPtr = createTemp();
                genvap.addLine(funcPtr + " = [this]");
                genvap.addLine(funcPtr + " = [" + funcPtr + "]");
                genvap.addLine(funcPtr + " = [" + funcPtr + " + " + offset * 4 +  "]");
                if (argsTemp != null)
                    genvap.addLine(resultTemp + " = call " + funcPtr + "(this " + argsTemp + ")");
                else
                    genvap.addLine(resultTemp + " = call " + funcPtr + "(this)");
            } else if (funcCallTemps.contains(funcOwnerTemp)) {
                // Nested function call
                int index = funcCallTemps.indexOf(funcOwnerTemp);
                String classReturned = funcCallTypes.get(index).getClassReturned();

                int offset = getMethodOffset(classReturned, mName);

                genvap.addLine("vt = [" + funcOwnerTemp + "]");
                genvap.addLine("vt = [vt]");
                genvap.addLine("f = [vt + " + (offset*4) + "]");

                if (argsTemp != null)
                    genvap.addLine(resultTemp + " = call f(" + funcOwnerTemp + " " + argsTemp + ")");
                else
                    genvap.addLine(resultTemp + " = call f(" + funcOwnerTemp + ")");

            } else {
                // Fetch type of funcOwner
                ClassBook cbook = (ClassBook) Typecheck.symbolTable.get(Symbol.symbol(currentClass));
                MethodsBook mbook = (MethodsBook) cbook.methods.get(Symbol.symbol(mName));
                String type = cbook.getIdType((String) funcOwnerTemp, currentMethod);

                // FuncOwner returns a regular variable instead
                if (type == null) {
                    if (randIds.contains(funcOwnerTemp)) {
                        type = randTypes.get(randIds.indexOf(funcOwnerTemp));
                    }
                }

                if (type != null) {
                    int offset = getMethodOffset(type, mName);

                    String x = (String) funcOwnerTemp;
                    if (findRecord(currentClass) != null) {
                        int fieldOffset = findRecord(currentClass).getFieldOffset((String) funcOwnerTemp);
                        if (fieldOffset != -1) {
                            String temp = createTemp();
                            genvap.addLine(temp + " = [this + " + (fieldOffset * 4) + "]");
                            x = temp;
                        }
                    }

                    genvap.addLine("vt = [" + x + "]");
                    genvap.addLine("vt = [vt]");
                    genvap.addLine("f = [vt + " + (offset*4) + "]");

                    if (argsTemp != null)
                        genvap.addLine(resultTemp + " = call f(" + x + " " + argsTemp + ")");
                    else
                        genvap.addLine(resultTemp + " = call f(" + x + ")");

                    funcCallTemps.add(resultTemp);
                    funcCallTypes.add(mbook);
                }
            }
        }

        _ret = (R) resultTemp;

        return _ret;
    }

    /**
     * f0 -> Expression()
     * f1 -> ( ExpressionRest() )*
     */
    public R visit(ExpressionList n, A argu) {
        R _ret=null;
        String express = (String) n.f0.accept(this, argu);
        n.f1.accept(this, argu);

        String restEX = "";

        if (n.f1.present()) {
            for (int a = 0; a < n.f1.size(); a++) {
                String x = (String) n.f1.elementAt(a).accept(this, argu);
                restEX = restEX + " " + x;
            }
        }

        _ret = (R) (express + restEX);

        return _ret;
    }

    /**
     * f0 -> ","
     * f1 -> Expression()
     */
    public R visit(ExpressionRest n, A argu) {
        R _ret=null;
        n.f0.accept(this, argu);
        String express = (String) n.f1.accept(this, argu);
        _ret = (R) express;
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
    public R visit(PrimaryExpression n, A argu) {
        R _ret=null;
        _ret = n.f0.accept(this, argu);

        return _ret;
    }

    /**
     * f0 -> <INTEGER_LITERAL>
     */
    public R visit(IntegerLiteral n, A argu) {
        R _ret=null;
        n.f0.accept(this, argu);
        _ret = (R)n.f0.toString();
        return _ret;
    }

    /**
     * f0 -> "true"
     */
    public R visit(TrueLiteral n, A argu) {
        R _ret=null;
        n.f0.accept(this, argu);
        _ret = (R) trueString;

        return _ret;
    }

    /**
     * f0 -> "false"
     */
    public R visit(FalseLiteral n, A argu) {
        R _ret=null;
        n.f0.accept(this, argu);
        _ret = (R) falseString;

        return _ret;
    }

    /**
     * f0 -> <IDENTIFIER>
     */
    public R visit(Identifier n, A argu) {
        R _ret=null;
        n.f0.accept(this, argu);
        String idNameTemp = n.f0.toString();
        _ret = (R) idNameTemp;

        return _ret;
    }

    /**
     * f0 -> "this"
     */
    public R visit(ThisExpression n, A argu) {
        R _ret=null;
        n.f0.accept(this, argu);

        _ret = (R) "this";

        return _ret;
    }

    /**
     * f0 -> "new"
     * f1 -> "int"
     * f2 -> "["
     * f3 -> Expression()
     * f4 -> "]"
     */
    public R visit(ArrayAllocationExpression n, A argu) {
        R _ret=null;
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        R sizeStrTemp = n.f3.accept(this, argu);
        n.f4.accept(this, argu);

        String arrName = createTemp();
        String sizeOffsetTemp = createTemp();

        if (findRecord(currentClass) != null) {
            int fieldOffset = findRecord(currentClass).getFieldOffset((String)sizeStrTemp);
            if (fieldOffset != -1) {
                String Temp_RHS = createTemp();
                genvap.addLine(Temp_RHS + " = [this + " + (fieldOffset * 4) + "]");
                sizeStrTemp = (R) Temp_RHS;
            }
        }

        genvap.addLine(sizeOffsetTemp  + " = MulS(" + sizeStrTemp + " 4)");
        genvap.addLine(sizeOffsetTemp + " = Add(" + sizeOffsetTemp + " 4)");
        genvap.addLine(arrName + " = HeapAllocZ(" + sizeOffsetTemp + ")");

        // Store the size (in index) of the array in the base of the array
        genvap.addLine("[" + arrName + "] = " + sizeStrTemp);

        _ret = (R) arrName;

        return _ret;
    }

    /**
     * f0 -> "new"
     * f1 -> Identifier()
     * f2 -> "("
     * f3 -> ")"
     */
    public R visit(AllocationExpression n, A argu) {
        R _ret=null;
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        n.f3.accept(this, argu);

        String cStringTemp = n.f1.f0.toString();
        String objStringTemp = createTemp();
        ClassRecordKeeper recordTemp = findRecord(cStringTemp);

        genvap.addLine(objStringTemp + " = HeapAllocZ(" + (recordTemp.getSize()) + ")");
        genvap.addLine("[" + objStringTemp + "] = " + ":" + cStringTemp);

        _ret = (R) (cStringTemp + "::" + objStringTemp);

        return _ret;
    }

    /**
     * f0 -> "!"
     * f1 -> Expression()
     */
    public R visit(NotExpression n, A argu) {
        R _ret=null;
        n.f0.accept(this, argu);
        R valueTemp = n.f1.accept(this, argu);

        String resultTemp = createTemp();
        String labelTemp = createLabel();
        String exit = createLabel();

        genvap.addLine(resultTemp + " = 0");
        genvap.addLine("ok = Eq(0 " + valueTemp + ")");
        genvap.addLine("if ok goto :"+labelTemp);
        genvap.addLine(resultTemp + " = 0");
        genvap.addLine("goto :" +exit);
        genvap.addLine(labelTemp +":");
        genvap.addLine(resultTemp + " = 1");
        genvap.addLine(exit + ":");

        _ret = (R) resultTemp;

        return _ret;
    }

    /**
     * f0 -> "("
     * f1 -> Expression()
     * f2 -> ")"
     */
    public R visit(BracketExpression n, A argu) {
        R _ret=null;
        n.f0.accept(this, argu);
        _ret = n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        return _ret;
    }
}
