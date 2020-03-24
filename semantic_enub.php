abstract class ENUB { // Boolean Enumerations
    const verb = 1;
    const noun = 2;
    const adjective = 4;
    const adverb = 8;
    const linking = 16;
    const article = 32;
    const determiner = 64;
    const begin = 128;
    const middle = 512;
    const end = 1024;
    const lion = 2048;
    const tiger = 8192;
    const zebra = 16384;
    const bear = 32768;
    const deer = 65536;
    const dog = 131072;
    const cat = 262144;
 
    public function getGroupName($enum){ // group name
        if( 64>= $enum && $enum >= 1 ){
            return 'POS';
        } // end if
        if( 1024>= $enum && $enum >= 128 ){
            return 'STATE';
        } // end if
        if( 262144>= $enum && $enum >= 2048 ){
            return 'ANIMAL';
        } // end if
        return '';
    } // end function
} // end class
