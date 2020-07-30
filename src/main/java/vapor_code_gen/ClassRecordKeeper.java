package vapor_code_gen;

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

public class ClassRecordKeeper {

    public String cname;
    public List<String> fields;
    public VTable vTab;

    public ClassRecordKeeper(String classname) {
        this.cname = classname;
        fields = new ArrayList<>();
        vTab = new VTable(classname + "_vtable");
    }

    public void copyFieldsFrom(ClassRecordKeeper x) {
        for (int a = 0; a < x.fields.size(); a++) {
            this.fields.add(x.fields.get(a));
        }
    }

    public void addField(String name) {
        fields.add(name);
    }


    public int getFieldOffset(String field) {
        int tempOff = 1;
        Iterator<String> fieldIt = fields.iterator();
        while (fieldIt.hasNext()) {
            if (fieldIt.next().equals(field))
                return tempOff;
            tempOff++;
        }

        return -1;
    }

    public int getMethodOffset(String method) {
        return vTab.getFunctionOffset(method);
    }

    public String getMethodLabel(int i) {
        return vTab.getFunctionLabel(i);
    }

    public int getSize() {
        return (fields.size() * 4) + 4;
    }
}
