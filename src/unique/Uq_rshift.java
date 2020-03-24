package unique;

import commons.BIT;

/**
 *
 * @author Dave Swanson
 */
public class Uq_rshift extends Uq_repeater{
    //protected int c, inci, strPad; initi, 
//    protected int fini;
//    protected int rpoint, bpoint;
    protected boolean errOnInit, errOnResume;
    
    public Uq_rshift(){
        this.name="Uq_rshift constructor";
        this.initi = this.rpoint = 1;
        this.fini  = this.bpoint = 1<<31;
        this.strPad=4;              // use strPad for format spacers on bin disp
        this.errOnInit = true;      // 
        this.errOnResume = false;
    }
    @Override
    public int n(){//increment and get
        //System.out.println( this.name+": A: "+this.c );
        if( this.errOnResume ){
            this.c = this.rpoint;
            this.errOnResume=false;
            //System.out.println( this.name+": found errOnResume: "+this.c );
        }
        else if( this.errOnInit ){
            this.c = this.initi;
            this.errOnInit=false;
            //System.out.println( this.name+": found errOnInit: "+this.c );
        }
        else{
            this.c <<= this.inci;
        }
        //System.out.println( this.name+": B: "+this.c );
        return this.c;
    }
    @Override
    public String s(){//increment and get
        return BIT.str( n(),this.strPad );
    }
    @Override
    public String sPrev(){//increment and get
        return BIT.str( n(),this.strPad );
    }
    /* Repeater */
    @Override
    public void setResumePoint( int resumePoint ){
        this.rpoint = resumePoint;
        //System.out.println( this.name+": setResumePoint: "+resumePoint );
    }    
    @Override
    public boolean signal(){
        int next = this.c << this.inci;
        return 
            (this.bpoint<=next && this.bpoint >0) || 
            ( next==0 && !this.errOnInit && !this.errOnResume );
    }
    @Override
    public void resume(){
        this.c = this.rpoint>>this.inci;
        if( this.c==0 ){
            this.errOnResume=true;
        }
        //System.out.println( this.name+": resume: "+this.c+": err: "+this.errOnResume  );
    }
    /* iterator */
    @Override
    public void setInitial( int init ){
        //System.out.println( this.name+": setInitial: "+init );
        this.initi = init;
        rewind();
        
    }
    /* iterator */
    @Override
    public boolean hasNext() { 
        int next = this.c << this.inci;
        return 
            ( this.fini>=next || this.fini <=0 ) && 
            ( next!=0 || this.errOnInit || this.errOnResume );
    }
    @Override
    public void rewind(){//reset incrementer to initial value
        this.c = this.initi>>this.inci;
        if( this.c==0 ){
            this.errOnInit=true;
        }
    }
}
