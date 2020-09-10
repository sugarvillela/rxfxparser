package toksource.interfaces;

import toksource.Base_TextSource;

public interface ChangeListener {
    void onTextSourceChange(ITextStatus textStatus, ChangeNotifier caller);
}
