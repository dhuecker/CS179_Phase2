package syntax_checker;

import java.util.List;

public class ClassBook extends Book {

    public String classname;
    public SymbolTable methods;
    public String parent;

    public ClassBook(String cname) {
        classname = cname;
        methods = new SymbolTable();
    }

    //added getIdType for phase2

    public String getIdType(String tempId, String currentMethod) {
        List<String> params = Items.getItems();
        if (params.contains(tempId)) {
            ClassBook cbook = (ClassBook) Items.get(Symbol.symbol(tempId));
            return cbook.classname;
        }
        List<String> mNames = methods.getItems();
        if (mNames.contains(currentMethod)) {
            MethodsBook mbook = (MethodsBook) methods.get(Symbol.symbol(currentMethod));

            if (mbook.Items.get(Symbol.symbol(tempId)) != null)
                return ((ClassBook) mbook.Items.get(Symbol.symbol(tempId))).classname;
            else {
                for (int a = 0; a < mbook.params.size(); a++) {
                    if (mbook.params.get(a).equals(tempId)) {
                        return mbook.pTypes.get(a);
                    }
                }
            }

        }
        return null;
    }
}
