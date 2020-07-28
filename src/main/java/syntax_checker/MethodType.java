package syntax_checker;

import syntaxtree.*;

public class MethodType {
  public Type rType;
  public NodeOptional argList;

  public MethodType(Type returnType, NodeOptional argumentList) {
    this.rType = returnType;
    this.argList = argumentList;
  }

  public boolean equals(MethodType RHS) {
    if (this.rType.f0.which == RHS.rType.f0.which) {
      if (!this.argList.present() && !RHS.argList.present()) {
        return true;
      } else {
        if (
          this.argList.present() && RHS.argList.present() &&
          ((FormalParameterList)this.argList.node).f1.size() == ((FormalParameterList)RHS.argList.node).f1.size()
        ) 
          return true;   
      }
    }
    return false;
  }
}
