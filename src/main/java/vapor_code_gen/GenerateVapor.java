package vapor_code_gen;

import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

public class  GenerateVapor {

    List<String> Vapbuffer;
    int inLevel;

    public GenerateVapor() {
        Vapbuffer = new ArrayList<>();
        inLevel = 0;
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
        for (int a = 0; a < inLevel * 2; a++)
            prefix += " ";
        Vapbuffer.add(prefix + l);
    }

    public void increaseIndent() {
        inLevel++;
    }

    public void descreaseIndent() {
        if (inLevel != 0)
            inLevel--;
    }

    void initClassRecord(ClassRecordKeeper x) {
        initVTable(x.vTab);

        if (x.cname.equals("Main"))
            addLine("const Main_Class");
        else
            addLine("const " + x.cname);
        increaseIndent();
        addLine(":" + x.vTab.Vname); // vTab pointer
        descreaseIndent();
        addLine("");
    }

    void initVTable(VTable vtab) {
        addLine("const " + vtab.Vname);

        increaseIndent();

        Iterator<String> funcTemp = vtab.functions.iterator();
        while (funcTemp.hasNext()) {
            String fName = funcTemp.next();
            if (fName.contains("main")) {
                addLine(":Main");
            } else {
                addLine(":" + fName);
            }
        }

        descreaseIndent();
        addLine("");
    }
}
