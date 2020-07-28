package vapor_code_gen;

import syntax_checker.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

public class J2V {

    public static List<ClassRecordKeeper> classRecordKeepers;

    static GenerateVapor genv = new GenerateVapor();

    public static void generateCode() {


        if (!Typecheck.typeCheck()) {
            // Program is not valid
            // Do not proceed further
        } else {
            // Construct a class graph
            ClassGraph tempCG = new ClassGraph();
            Iterator<String> newClasses = Typecheck.symbolTable.getItems().iterator();
            while (newClasses.hasNext()) {
                String currentClassname = newClasses.next();
                ClassBook currentClassTemp = (ClassBook) Typecheck.symbolTable.get(Symbol.symbol(currentClassname));

                tempCG.addEdge(currentClassname, currentClassTemp.parent);
            }

            // Construct class records
            classRecordKeepers = new ArrayList<>();

            // Topologically sort class graph
            Iterator<String> classesTemp = tempCG.topologicalSort().iterator();
            while(classesTemp.hasNext()) {
                String currentClassname = classesTemp.next();

                ClassBook currentClassTemp = (ClassBook) Typecheck.symbolTable.get(Symbol.symbol(currentClassname));

                ClassRecordKeeper currentRecordTemp = new ClassRecordKeeper(currentClassname);
                classRecordKeepers.add(currentRecordTemp);

                // Fields from parent classes
                // If topo-sort works, then the parent's CR should already contain the data
                if (currentClassTemp.parent != null) {
                    currentRecordTemp.copyFieldsFrom(findClassRecord(currentClassTemp.parent));
                }

                // Explicit fields
                Iterator<String> fields = currentClassTemp.Items.getItems().iterator();
                while (fields.hasNext()) {
                    currentRecordTemp.addField(fields.next());
                }

                Iterator<String> methods = currentClassTemp.methods.getItems().iterator();
                while (methods.hasNext()) {
                    String currentMethod = methods.next();
                    String fName = currentClassname + "_" + currentMethod;

                    currentRecordTemp.v_table.addFunction(fName);
                }
            }

            // Puts the v_tables and class records into a text buffer
            genv.setupTables(classRecordKeepers);

            // Get ready for another round of visitors
            VaporGenVisitor<String, String> vapVis = new VaporGenVisitor<>();
            vapVis.genvap = genv; // Pass the code buffer to the VaporVisitor
            try {
                //Goal root = parser.Goal();
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
        Iterator<ClassRecordKeeper> tempIterator = classRecordKeepers.iterator();
        while (tempIterator.hasNext()) {
            ClassRecordKeeper current = tempIterator.next();
            if (current.cname.equals(name))
                return current;
        }

        return null;
    }

    static void inspectCRK() {
        // Inspect ClassRecords
        Iterator<ClassRecordKeeper> IteratorTemp = classRecordKeepers.iterator();
        while (IteratorTemp.hasNext()) {
            ClassRecordKeeper current = IteratorTemp.next();
            System.out.println(current.cname);

            System.out.println("FIELDS");

            Iterator<String> fields = current.fields.iterator();
            while (fields.hasNext()) {
                System.out.println("     " + fields.next());
            }

            System.out.println("METHODS");

            Iterator<String> methods = current.v_table.functions.iterator();
            while (methods.hasNext()) {
                System.out.println("     " + methods.next());
            }
        }
    }
}
