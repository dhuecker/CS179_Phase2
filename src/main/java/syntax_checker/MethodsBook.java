package syntax_checker;

import java.util.ArrayList;
import java.util.List;

public class MethodsBook extends Book {

    public TypeBook type;
    public int paramNum;
    public List<String> pTypes;
    public List<String> params;

    public MethodsBook() {
        pTypes = new ArrayList<>();
        params = new ArrayList<>();
    }

    public String getClassReturned() {
        if (type instanceof ClassTypeBook)
            return ((ClassTypeBook) type).classname;
        return null;
    }
}
