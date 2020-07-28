package syntax_checker;

import java.util.List;

public class ClassBook extends Book {

    public String classname;
    public SymbolTable methods;
    public String parent;

    public ClassBook(String c) {
        classname = c;
        methods = new SymbolTable();
    }

    public String getIdType(String id, String currMethod) {
        List<String> params = myItems.getItems();
        if (params.contains(id)) {
            ClassBook cb = (ClassBook) myItems.get(Symbol.symbol(id));
            return cb.classname;
        }
        List<String> methodNames = methods.getItems();
        if (methodNames.contains(currMethod)) {
            MethodsBook mb = (MethodsBook) methods.get(Symbol.symbol(currMethod));

            if (mb.myItems.get(Symbol.symbol(id)) != null)
                return ((ClassBook) mb.myItems.get(Symbol.symbol(id))).classname;
            else {
                for (int i = 0; i < mb.params.size(); i++) {
                    if (mb.params.get(i).equals(id)) {
                        return mb.paramTypes.get(i);
                    }
                }
            }

        }
        return null;
    }
}
