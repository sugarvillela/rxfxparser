package toksource.interfaces;

import interfaces.ILifeCycle;

/**For text source (line-by-line)
 * Use file and list implementations
 * 
 * For token source (word-by-word)
 * Use TokenSource Word/Line iterator; set output to word, line or a strategy/state
 * 
 * @author Dave Swanson
 */
public interface ITextSource extends ITextStatus{
    void rewind();
    String next();
    boolean hasNext();
    boolean hasData();

//    void setLineGetter();
//    void setWordGetter();
//    boolean isLineGetter();
//    boolean isWordGetter();
    

}
