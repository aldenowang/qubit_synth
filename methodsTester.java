
public class methodsTester {
	public static void main(String[] args) {
	    // Instance fields (variables belonging to each object)
		
	    ZOmega x = new ZOmega(1, 0, 0, 0);
	    ZOmega y = new ZOmega(0, 0, 0, 0);

	    x.printZOmega();
	    long k = 0;
	    cVector vec = new cVector(x,y,k);

	    vec.printCVector();
	    
	    
		//DOmega x = new DOmega(1, 2, 0, 1, 0, 1, -1, 2);
		//DOmega y = new DOmega(0, 2, 0, 1, -1, 2, 1, 2);
		//cVector vec = Runner.convertToZOmega(x, y);
	    /*
		ZOmega x = new ZOmega(-7, 0, -8, -11);
		ZOmega y = new ZOmega(-13, 22, 4, 11);
		cVector vec = new cVector(x, y, 20);
	*/
	    
	    
	    
	    
	    vec.applyHGate();
	    vec.applyTGate(1);
	    vec.applyTGate(1);
	    vec.applyHGate();
	    vec.applyTGate(3);
	    vec.applyTGate(3);
	    vec.applyHGate();
	    vec.applyTGate(3);
	    vec.applyHGate();

	    vec.applyTGate(2);
	    vec.applyHGate();
	    vec.applyTGate(2);
	    vec.applyHGate();
	    vec.applyTGate(3);
	    vec.applyHGate();
	    vec.applyTGate(3);
	    vec.applyHGate();
	    vec.applyTGate(1);

	    vec.applyHGate();
	    vec.applyTGate(3);
	    vec.applyHGate();
	    vec.applyTGate(3);
	    vec.applyHGate();
	    vec.applyTGate(3);
	    vec.applyHGate();
	    vec.applyTGate(2);  
	    vec.applyHGate();
	    vec.applyTGate(1);
	    vec.applyHGate();
	    vec.applyTGate(2);  
	    vec.applyHGate();
	    vec.applyTGate(4);
	    
	    vec.applyTGate(3);
	    vec.applyHGate();
	    vec.applyTGate(1);
	    vec.applyHGate();
	    vec.applyTGate(3);
	    vec.applyHGate();
	    vec.applyTGate(1);
	    vec.applyHGate();
	    vec.applyTGate(1);
	    vec.applyHGate();
	    vec.applyTGate(3);
	    vec.applyHGate();
	    vec.applyTGate(1);
	    vec.applyHGate();
	    vec.applyTGate(3);
	    vec.applyHGate();
	    vec.applyTGate(3);
	    vec.applyHGate();
	    vec.applyTGate(3);
	    vec.applyHGate();
	    vec.applyTGate(2);
	    
	    /*
	    vec.applyHGate();
	    vec.applyTGate(1);
	    vec.applyHGate();
	    vec.applyTGate(3);
	    vec.applyHGate();
	    vec.applyTGate(1);
	    vec.applyHGate();
	    vec.applyTGate(3);
	    vec.applyHGate();
	    vec.applyTGate(3);
	    vec.applyHGate();
	    vec.applyTGate(3);
	    vec.applyHGate();
	    */
	    System.out.println("actual thing here:");
	    vec.printCVector();

	    cVector b = reduceTo2(vec);
	    b.printCVector();
	   
	    
	   
	    
	   
	        
	    }
	
		
	public static cVector reduceTo2(cVector c) {
			String gateSeq = "";
				
			while (c.getK() > 0) {
				for (int i = 0; i < 4; i++) {
					for (int j = 0; j < 4; j++) {
						cVector temp = new cVector(c.getX(), c.getY(), c.getK());
						temp.applyTGate(i);
						temp.applyHGate();
						temp.applyTGate(j);
						temp.applyHGate();
						
						if (temp.getK() < c.getK()) {
							c = new cVector(temp.getX(), temp.getY(), temp.getK());
							System.out.println(c.getK());
							gateSeq += "HT†^" + i + "HT†^" + j;
							break;
						}
					}
				}
				
			}
			if (c.getX().isZero() && !c.getY().isZero()) {
				c.applyHGate();
				c.applyTGate(4);
				c.applyHGate();
				gateSeq = gateSeq + "HT†^4H";
			}


			
				
			
			System.out.println("Gate Sequence:");
			System.out.println(gateSeq);
			return c;
	}
	
}
