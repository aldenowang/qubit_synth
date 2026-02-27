import java.util.ArrayList;

public class Runner {
	
	public static void main(String[] args) {
		
		        System.out.println("Enter the first column of a U(2) matrix (second column will be determined automatically):");
		        System.out.println("a   b");
		        System.out.println("c   d");
		       
		        // Input first column elements
		        System.out.println("Enter the element in the top-left part of the matrix in the ring D[w]");
		        DOmega a = readDOmega(); 
		        System.out.println("");
		        System.out.println("Enter element c (bottom-left, complex number):");
		        DOmega c = readDOmega();
		        
		        
		        U2Matrix matrix = new U2Matrix(a, c);
		        System.out.println("\nGenerated U(2) matrix:");
		        System.out.print("a: ");   matrix.getA().printDOmega(); System.out.println("");
		        System.out.print("b: ");   matrix.getB().printDOmega(); System.out.println("");
		        System.out.print("c: ");   matrix.getC().printDOmega(); System.out.println("");
		        System.out.print("d: ");   matrix.getD().printDOmega(); System.out.println("");
		        System.out.println("");
		        
		        if (matrix.isUnitary()) {
		        	 System.out.println("Matrix is unitary: " + matrix.isUnitary());
		        	 System.out.println("");
		        	 cVector co = convertToZOmega(matrix.getA(), matrix.getC());
				     cVector temp = reduceColumnVector(co);
				     
				     if (temp.getK() == 0) {
				    	 System.out.println("Reduced column vector in Z[w]:");
					     temp.getX().printZOmega();
					     temp.getY().printZOmega();
				     } else {
				    	 System.out.println("Reduced column vector up to SDE 1:");
					     temp.getX().printZOmega();
					     temp.getY().printZOmega();
				     }
				    
		        }
		        else {
			        System.out.println("Must enter a valid unitary matrix");
		        }
		       
		        
		       
          }	
		
		
	
	public static cVector convertToZOmega(DOmega x, DOmega y) {
					
				DOmega delta = new DOmega(1, 1, 1, 1, 0, 1, 0, 1);
				int sde = 0;
				while (ZOmega.isInRing(x) == false || ZOmega.isInRing(y) == false) {
					DOmega tempX = x ;
					DOmega tempY = y;
						x = tempX.multiplication(delta);
						y = tempY.multiplication(delta);
						sde++;
				}
				System.out.println("Initial SDE is: " + sde);
				ZOmega newX = convertDToZ(x);
				ZOmega newY = convertDToZ(y);
				cVector c = new cVector(newX, newY, sde);
				return c;
				
			}
			
			private static boolean allEven(long[] t) {
			    return (t[0] & 1) == 0 && (t[1] & 1) == 0 && (t[2] & 1) == 0 && (t[3] & 1) == 0;
			}

			private static ZOmega convertDToZ(DOmega d) {
				int a = d.getNumerators()[0]/d.getDenominators()[0];
				int b = d.getNumerators()[1]/d.getDenominators()[1];
				int c = d.getNumerators()[2]/d.getDenominators()[2];
				int e = d.getNumerators()[3]/d.getDenominators()[3];
				return new ZOmega(a, b, c, e);

				
			}
			
			public static cVector reduceColumnVector(cVector c) {
				String gateSeq = "";
					
				while (c.getK() > 1) {
					for (int i = 0; i < 4; i++) {
						for (int j = 0; j < 4; j++) {
							cVector temp = new cVector(c.getX(), c.getY(), c.getK());
							temp.applyTGate(i);
							temp.applyHGate();
							temp.applyTGate(j);
							temp.applyHGate();
							
							if (temp.getK() < c.getK()) {
								c = new cVector(temp.getX(), temp.getY(), temp.getK());
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
				
				if (c.getK() == 1 && c.getX().isDivByDelta() && c.getY().isDivByDelta()) {
					 ZOmega x = c.getX();
					 ZOmega y = c.getY();
					 c.setX(x.divideByDelta(x));
				     c.setY(y.divideByDelta(y));
				     c.incrementK(-1);

				}
				System.out.println("Final SDE is: " + c.getK());
			    System.out.println();
				System.out.println("Gate Sequence:");
				System.out.println(gateSeq);
				System.out.println();
				return c;
		}
		
	
		    private static DOmega readDOmega() {
		    	System.out.println("The general form of a number in D[w] is a + bw + cw^2 + dw^3");
		    	System.out.println("Where w = e^ipi/4");
		    	System.out.println("And a, b, c, d are elements of the dyadic rational numbers, which can be written as z/2^k, where z and k are integers");
		    	System.out.println("If you don't want a specific omega power just enter 0 as the numerator");
		    	System.out.println("");
		    	System.out.println("If you enter a non-integer it will be rounded DOWN to the nearest integer");
		    	
		    	
		    	boolean one = false;
		    	boolean two = false;
		    	boolean three = false;
		    	boolean four = false;
		    	
		    	//a
		    	System.out.println("Enter a's numerator");
		        int num1 = TextIO.getInt(); //will round down
		        int denom1;
		        TextIO.getln();
		    	do {
		    		System.out.println("Enter a's denominator");
			    	denom1 = TextIO.getInt();
			        TextIO.getln();
			        one = checkDenom(denom1);
		    	} while (!one);
		    	
		    	//b
		    	System.out.println("Enter b's numerator");
		        int num2 = TextIO.getInt();
		        int denom2;
		        TextIO.getln();
		    	do {
			    	System.out.println("Enter b's denominator");
			    	denom2 = TextIO.getInt();
			        TextIO.getln();
			        two = checkDenom(denom2);
		    	} while (!two);
		    	
		    	//c
		    	System.out.println("Enter c's numerator");
		        int num3 = TextIO.getInt();
		        int denom3;
		        TextIO.getln();
		        do {
		        	System.out.println("Enter c's denominator");
			        denom3 = TextIO.getInt();
			        TextIO.getln();
			        three = checkDenom(denom3);
		        } while (!three);
		        
		        //d
		    	System.out.println("Enter d's numerator");
		        int num4 = TextIO.getInt();
		        int denom4;
		        TextIO.getln();
		        do {
		        	System.out.println("Enter d's denominator");
			        denom4 = TextIO.getInt();
			        TextIO.getln();
			        four = checkDenom(denom4);
		        } while (!four);
		        
		    	
		        DOmega input = new DOmega(num1, denom1, num2, denom2, num3, denom3, num4, denom4);
		        //input.printDOmega();
		        return input;
		    }
		    
		    private static boolean checkDenom(int n) {
		    	if (n <= 0 || Integer.bitCount(n) != 1) {
			    	 System.out.println("Invalid denominator entry, must be an integer power of 2");
			    	 return false;
		    	}
		    	return true;
		    }
		    	    		    		
		    
	}
