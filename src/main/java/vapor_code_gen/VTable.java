package vapor_code_gen;

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

public class VTable {

    String name;
    public List<String> functions;

    public VTable(String name) {
        this.name = name;
        functions = new ArrayList<>();
    }

    public void addFunction(String label) {
        functions.add(label);
    }

    public int getFunctionOffset(String key) {
        int counter = 0;
        Iterator<String> x = functions.iterator();
        while(x.hasNext()) {
            String currentTemp = x.next();

            int subTemp = currentTemp.indexOf("_" + key);
            if (subTemp != -1)
            if (currentTemp.substring(subTemp).equals("_" + key))
                return counter;

            counter++;
        }
        return -1;
    }

    public String getFunctionLabel(int i) {
        return functions.get(i);
    }
}
