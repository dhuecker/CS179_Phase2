package vapor_code_gen;

import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

public class  GenerateVapor {

    List<String> Vapbuffer;
    int intentLevel;

    public GenerateVapor() {
        Vapbuffer = new ArrayList<>();
        intentLevel = 0;
    }

    public void setupTables(List<ClassRecordKeeper> classRecordKeepers) {
        Iterator<ClassRecordKeeper> temp = classRecordKeepers.listIterator();
        while (temp.hasNext()) {
            initClassRecord(temp.next());
        }
    }

    public void printBuffer() {

        addLine("");

        Iterator<String> temp = Vapbuffer.iterator();
        while (temp.hasNext()) {
            System.out.println(temp.next());
        }
    }

    public void addLine(String l) {
        String prefix = "";
        for (int a = 0; a < intentLevel * 2; a++)
            prefix += " ";

        Vapbuffer.add(prefix + l);
    }

    public void increaseIndent() {
        intentLevel++;
    }

    public void descreaseIndent() {
        if (intentLevel != 0)
            intentLevel--;
    }

    void initClassRecord(ClassRecordKeeper x) {
        initVTable(x.v_table);

        // Mutable data for class fields
        if (x.cname.equals("Main"))
            addLine("const Main_Class");
        else
            addLine("const " + x.cname);
        increaseIndent();
        addLine(":" + x.v_table.name); // v_table pointer
        descreaseIndent();
        addLine("");
    }

    void initVTable(VTable vtab) {
        addLine("const " + vtab.name);

        increaseIndent();

        // Function pointers
        Iterator<String> funcTemp = vtab.functions.iterator();
        while (funcTemp.hasNext()) {
            String funcName = funcTemp.next();
            if (funcName.contains("main")) {
                addLine(":Main");
            } else {
                addLine(":" + funcName);
            }
        }

        descreaseIndent();
        addLine("");
    }
}
