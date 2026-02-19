
public class U2Matrix {
	
	
	
	private DOmega a;
	private DOmega b;
	private DOmega c;
	private DOmega d;
    
    public U2Matrix(DOmega a, DOmega c) {
    	// Store the first column (a, c)
    	this.a = a;
    	this.c = c;
        // Compute the second column (b, d) = (-c*, a*)
        this.b = (c.conjugate()).scalarMult(-1);
        this.d = a.conjugate();
        
    }
    public U2Matrix(DOmega a, DOmega b, DOmega c, DOmega d) {
    	this.a = a;
       	this.b = b;
    	this.c = c;
    	this.d = d;
    }
    
    
    public boolean isUnitary() {
    	
    	DOmega tempA = a;
    	DOmega tempB = b;
    	DOmega tempC = c;
    	DOmega tempD = d;
    	
        DOmega aConj = tempA.conjugate();
        DOmega bConj = tempB.conjugate();
        DOmega cConj = tempC.conjugate();
        DOmega dConj = tempD.conjugate();
        
        /*
        System.out.println("printing dagger");
        aConj.printDOmega();
        cConj.printDOmega();
        bConj.printDOmega();
        dConj.printDOmega();
      	*/
        
        U2Matrix dagger = new U2Matrix(aConj, cConj, bConj, dConj);
        
        U2Matrix identity = matrixMultiplication(this, dagger);
        if (identity.getA().equals(DOmega.ONE) && identity.getB().equals(DOmega.ZERO) && identity.getC().equals(DOmega.ZERO) && identity.getD().equals(DOmega.ONE)){
        	return true;
        }
        return false;
      
  
    }
    public U2Matrix matrixMultiplication(U2Matrix left, U2Matrix right){ //shld be correct (deepseek)
    	DOmega a = left.getA().multiplication(right.getA())
                .add(left.getB().multiplication(right.getC()));
    	
    	DOmega b = left.getA().multiplication(right.getB())
                .add(left.getB().multiplication(right.getD()));
    	
    	DOmega c = left.getC().multiplication(right.getA())
                .add(left.getD().multiplication(right.getC()));
    	
    	DOmega d = left.getC().multiplication(right.getB())
                .add(left.getD().multiplication(right.getD()));
    	
    	return new U2Matrix(a, b, c, d); 
    }
    
    
    public DOmega getA() {
    	return a;
    }
    public DOmega getB() {
    	return b;
    }
    public DOmega getC() {
    	return c;
    }
    public DOmega getD() {
    	return d;
    }
    
   
}
