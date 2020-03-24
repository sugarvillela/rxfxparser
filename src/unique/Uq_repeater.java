package unique;
/*
    //protected int c, strPad; 
    //protected int inci, initi, fini;
*/

/**
 *
 * @author Dave Swanson
 */
public class Uq_repeater extends Unique{
    protected int rpoint, bpoint;
    
    public Uq_repeater(){
        this.name="Uq_repeater constructor";
        this.initi = this.rpoint = -1;
        this.fini  = this.bpoint = 0x40000000;
    }
    /* Repeater */
    public void setResumePoint( int resumePoint ){
        this.rpoint = resumePoint-this.inci;
        //System.out.println( this.name+": setResumePoint: "+resumePoint );
    }
    public void setBreakPoint( int breakPoint ){
        this.bpoint = breakPoint;
        //System.out.println( this.name+": setBreakPoint: "+breakPoint );
    }
    public boolean signal(){
        return ( this.c + this.inci ) > this.bpoint;
    }
    public void resume(){
        this.c = this.rpoint;
    }
}
