package compile.interfaces;

import compile.implitem.Base_StackItem;

public interface IStackItem {
    void push( Base_StackItem nuTop );
    void pop();

    Base_StackItem getAbove();
    Base_StackItem getBelow();

    void onPush();   // call immediately after push/activate
    void onPop();    // call just before pop/deactivate
}
