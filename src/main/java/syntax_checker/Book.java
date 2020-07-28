package syntax_checker;

public abstract class Book {

    public SymbolTable myItems;

    public Book() {
        myItems = new SymbolTable();
    }

    public boolean addSymbol(Symbol key, Book b) {
        if (myItems.get(key) == null) {
            myItems.put(key, b);
            return true;
        }
        return false;
    }
}
