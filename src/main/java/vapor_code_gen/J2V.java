package vapor_code_gen;

import syntax_checker.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

public class J2V {

    public static List<ClassRecordKeeper> classRecordKeepers;

    static GenerateVapor genv = new GenerateVapor();

    public static void generateCode() {


        if (!Typecheck.typeChecking()) {
            // Program is not valid
            // Do not proceed further
        } else {

            ClassGraph tempCG = new ClassGraph();
            Iterator<String> classIt = Typecheck.sTable.getItems().iterator();
            while (classIt.hasNext()) {
                String currentClassname = classIt.next();
                ClassBook currentClassTemp = (ClassBook) Typecheck.sTable.get(Symbol.symbol(currentClassname));

                tempCG.addEdge(currentClassname, currentClassTemp.parent);
            }

            classRecordKeepers = new ArrayList<>();

            Iterator<String> cTempIt = tempCG.topologicalSort().iterator();
            while(cTempIt.hasNext()) {
                String currentClassname = cTempIt.next();

                ClassBook currentClassTemp = (ClassBook) Typecheck.sTable.get(Symbol.symbol(currentClassname));

                ClassRecordKeeper currentRecordTemp = new ClassRecordKeeper(currentClassname);
                classRecordKeepers.add(currentRecordTemp);

                if (currentClassTemp.parent != null) {
                    currentRecordTemp.copyFieldsFrom(findClassRecord(currentClassTemp.parent));
                }


                Iterator<String> fieldsIt = currentClassTemp.Items.getItems().iterator();
                while (fieldsIt.hasNext()) {
                    currentRecordTemp.addField(fieldsIt.next());
                }

                Iterator<String> methodsIt = currentClassTemp.methods.getItems().iterator();
                while (methodsIt.hasNext()) {
                    String currentMethod = methodsIt.next();
                    String fName = currentClassname + "_" + currentMethod;

                    currentRecordTemp.v_table.addFunction(fName);
                }
            }

            genv.setupTables(classRecordKeepers);
            VaporGenVisitor<String, String> vapVis = new VaporGenVisitor<>();
            vapVis.genvap = genv;

            try {
                Typecheck.root.accept(vapVis, "");
            } catch (Exception e) {
                System.out.println("ERROR: " + e);
                e.printStackTrace();
            }

            genv.printBuffer();
        }
    }

    public static void main(String args[]) {
        generateCode();
    }

    static ClassRecordKeeper findClassRecord(String name) {
        Iterator<ClassRecordKeeper> tempIt = classRecordKeepers.iterator();
        while (tempIt.hasNext()) {
            ClassRecordKeeper current = tempIt.next();
            if (current.cname.equals(name))
                return current;
        }

        return null;
    }

    static void inspectCRK() {

        Iterator<ClassRecordKeeper> ItTemp = classRecordKeepers.iterator();
        while (ItTemp.hasNext()) {
            ClassRecordKeeper current = ItTemp.next();
            System.out.println(current.cname);

            System.out.println("FIELDS");

            Iterator<String> fieldsIt = current.fields.iterator();
            while (fieldsIt.hasNext()) {
                System.out.println("     " + fieldsIt.next());
            }

            System.out.println("METHODS");

            Iterator<String> methodsIt = current.v_table.functions.iterator();
            while (methodsIt.hasNext()) {
                System.out.println("     " + methodsIt.next());
            }
        }
    }
}
