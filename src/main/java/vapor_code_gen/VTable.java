package vapor_code_gen;

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

public class VTable {

    String Vname;
    public List<String> functions;

    public VTable(String Vname) {
        this.Vname = Vname;
        functions = new ArrayList<>();
    }

    public void addFunction(String label) {
        functions.add(label);
    }

    public int getFunctionOffset(String key) {
        int counterTemp = 0;
        Iterator<String> x = functions.iterator();
        while(x.hasNext()) {
            String currentTemp = x.next();

            int subTemp = currentTemp.indexOf("_" + key);
            if (subTemp != -1)
            if (currentTemp.substring(subTemp).equals("_" + key))
                return counterTemp;

            counterTemp++;
        }
        return -1;
    }

    public String getFunctionLabel(int i) {
        return functions.get(i);
    }
}
