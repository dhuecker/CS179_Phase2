package vapor_code_gen;

public class ClassGraphNode {
    public String name;

 
    public boolean tMark;
    public boolean pMark;

    public ClassGraphNode(String name) {
        this.name = name;

        tMark = false;
        pMark = false;
    }

    public boolean equals(String lhs) {
        return this.name == lhs;
    }

    public void giveTempMark() {
        tMark = true;
    }

    public void takeTempMark() {
        tMark = false;
    }

    public void givePermMark() {
        pMark = true;
    }
}
