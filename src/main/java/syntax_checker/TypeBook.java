package syntax_checker;

public class TypeBook extends Book {
    public TypeBook() {

    }
}

class IntBook extends TypeBook {

}

class BoolBook extends TypeBook {

}

class ArrayBook extends TypeBook {

}

class ClassTypeBinder extends TypeBook {
    String classname;
}
