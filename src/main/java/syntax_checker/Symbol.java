package syntax_checker;

import java.util.Dictionary;
import java.util.Hashtable;

public class Symbol {
    private String name;
    private Symbol(String n) {

        name=n;
    }
    private static Dictionary dict = new Hashtable();

    public String toString() {

        return name;
    }

    public static Symbol symbol(String x) {
        String temp1 = x.intern();
        Symbol temp2 = (Symbol) dict.get(temp1);
        if (temp2 == null) {
            temp2 = new Symbol(temp1);
            dict.put(temp1, temp2);
        }

        return temp2;
    }

    public boolean lt(Symbol LHS) {
        return this.hashCode() < LHS.hashCode();
    }

    public boolean gt(Symbol LHS) {
        return this.hashCode() > LHS.hashCode();
    }

    public boolean eq(Symbol LHS) {
        return this.hashCode() == LHS.hashCode();
    }

    public boolean lte(Symbol LHS)
    {
        return this.lt(LHS) || this.eq(LHS);
    }

    public boolean gte(Symbol LHS) {

        return this.gt(LHS) || this.eq(LHS);
    }
}
