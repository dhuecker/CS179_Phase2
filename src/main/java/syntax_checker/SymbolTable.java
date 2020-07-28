package syntax_checker;

import java.util.List;
import java.util.ArrayList;

class HashBucket {
    Symbol key;
    Book chain;
    HashBucket another;

    HashBucket(Symbol a, Book b, HashBucket c) {
        key = a;
        chain = b;
        another = c;
    }
}

class HashT {
    final int MAX = 256;
    HashBucket table[] = new HashBucket[MAX];

    private int hash(Symbol k) {
        String s = k.toString();
        int h = 0;
        for (int i = 0; i < s.length(); i++)
            h = h*65599+s.charAt(i);
        return h;
    }

    void insert(Symbol s, Book b) {
        int index=hash(s)%MAX;
        index = (index < 0) ? index*-1 : index;
        table[index] = new HashBucket(s, b, table[index]);
    }

    Book lookup(Symbol s) {
        int index=hash(s)%MAX;
        index = (index < 0) ? index*-1 : index;
        for (HashBucket b = table[index]; b != null; b = b.another) {
            if (s.eq(b.key)) {
                return b.chain;
            }
        }

        return null;
    }

    void pop(Symbol s) {
        int index = hash(s)%MAX;
        table[index] = table[index].another;
    }

    public void print() {
        for (int i = 0; i < table.length; i++) {
            HashBucket current = table[i];
            while (current != null) {
                System.out.println(current.key.toString());
                current = current.another;
            }
        }
    }

    public List<String> getAllItems() {
        List<String> temp = new ArrayList<>();
        for (int i = 0; i < table.length; i++) {
            HashBucket current = table[i];
            while (current != null) {
                temp.add(current.key.toString());
                current = current.another;
            }
        }
        return temp;
    }
}

public class SymbolTable {

    HashT hashTable;

    public SymbolTable() {
        hashTable   = new HashT();
    }

    public void put(Symbol key, Book value) {
        hashTable.insert(key, value);
    }

    public Book get(Symbol key) {
        return hashTable.lookup(key);
    }

    public boolean alreadyEx(Symbol key) {
        return !(get(key) == null);
    }

    public void print() {
        hashTable.print();
    }

    public List<String> getItems() {
        return hashTable.getAllItems();
    }
}
