package syntax_checker;

public abstract class Book {

    public SymbolTable Items;

    public Book() {
        Items = new SymbolTable();
    }

    public boolean addSymbol(Symbol key, Book tempb) {
        if (Items.get(key) == null) {
            Items.put(key, tempb);
            return true;
        }
        return false;
    }
}
