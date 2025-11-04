import java.util.ArrayList;

public class Runner {
	private static String gateString = "";
	//filler general method will edit later to match my class
	public static void main(String[] args) {
		        ZOmega DELTA = new ZOmega(1, 1, 0, 0);
		        System.out.println("Enter the first column of a U(2) matrix (second column will be determined automatically):");
		       
		        // Input first column elements
		        System.out.println("Enter the element in the top-left part of the matrix in the ring D[w]");
		        DOmega a = readDOmega(); 
		        System.out.println("");
		        System.out.println("Enter element b (bottom-left, complex number):");
		        DOmega c = readDOmega();
		        
		        
		        U2Matrix matrix = new U2Matrix(a, c);
		        System.out.println("\nGenerated U(2) matrix:");
		        matrix.getA().printDOmega();
		        matrix.getB().printDOmega();
		        matrix.getC().printDOmega();
		        matrix.getD().printDOmega();
		        System.out.println("");
				
		        boolean isUnitary = matrix.isUnitary();
		        System.out.println("Matrix is unitary: " + isUnitary);
		        
		        cVector co = convertToZOmega(matrix.getA(), matrix.getC());
		        String gateString = "";
		        
		        while (co.getK() > 0) {
		        	//int [] sdeReduction = new int[8];
		        	long sdeBefore = new ZOmega(co.getX()).factorOutAllDeltas()[4];
	        		int bestR = -1;
	        		long bestDrop = 0;
	        		int maxT = 0;
	        		
		        	for (int i = 0; i < 8; i++) { //8 possible values
		        		cVector temp = new cVector(new ZOmega(co.getX()), new ZOmega(co.getY()), co.getK());
		        		temp.applyTGate(i);
		        		temp.applyHGate();
		        		//check failing case
		        		long drop;
		        		//System.out.println(temp.getX().isZero());
		        		if (temp.getX().isZero() == true) {
		        			continue;
		        		} else {
			        		drop = temp.getX().factorOutAllDeltas()[4];
		        		}
		        		if (drop > bestDrop) {
		        			bestDrop = drop;
		        			maxT = i;
		        		}
		        
		        		  
		        	}

		        	
		        	System.out.println();
		        	/*
		        	System.out.println("sdeBefore = " + sdeBefore);
		        	System.out.println("bestDrop = " + bestDrop);
		        	System.out.println("maxT = " + maxT);
					*/
		        	co.applyTGate(maxT);
		        	co.applyHGate();
		        	if (bestDrop <= 0) {
		        		//throw new RuntimeException("No SDE progress ts step; check arithmetic");
		        	}
		        	
		        	ZOmega tempX = new ZOmega(co.getX());
		        	ZOmega tempY = new ZOmega(co.getY());
		        	long [] xInfo = tempX.factorOutAllDeltas();
		        	long [] yInfo = tempY.factorOutAllDeltas();
		        	
		        	long sdeDrop = Math.min(xInfo[4], yInfo[4]);
		        	
		            ZOmega temp2X = new ZOmega(co.getX());
		            ZOmega temp2Y = new ZOmega(co.getY());
		            co.setX(reduceExactly(temp2X, sdeDrop));
		            co.setY(reduceExactly(temp2Y, sdeDrop));
		        	
		        	System.out.println("sde after gate sequence: " + co.getK());
		        	co.incrementK(-sdeDrop);
		        	
		        	if (maxT == 0) {
		        		gateString = "H " + gateString;
		        		break;
		        	}
		        	else {
			        	gateString = "H" + "T^" + maxT + " " + gateString;
		        	}
		        	//gateString = gateString + maxT + "^T" + "H ";
		        	
		        	System.out.println("sde taken out is: " + xInfo[4]);
		        	System.out.println("sde is: " + co.getK());

		        }
		       
		        System.out.println("Unreduced gate sequence: " + gateString);
		        gateString = reduceGateSequence(gateString);
		        System.out.println("Reduced gate sequence: " + gateString);
		      }
	
	private static String reduceGateSequence(String gateString) {
	    gateString = gateString.replace(" ", ""); // remove spaces if there are

	    //remove all "T^0" 
	    for (int i = 0; i + 3 <= gateString.length(); ) {
	        if (gateString.startsWith("T^0", i)) {
	            gateString = gateString.substring(0, i) + gateString.substring(i + 3);
	            if (i > 0) {
	            	i--; 
	            }
	        } else {
	            i++;
	        }
	    }

	    //cancel HH
	    boolean changed = true;
	    while (changed) {
	        changed = false;
	        for (int i = 0; i + 2 <= gateString.length(); ) {
	            if (gateString.startsWith("HH", i)) {
	                gateString = gateString.substring(0, i) + gateString.substring(i + 2);
	                changed = true;
	                if (i > 0) i--; 
	            } else {
	                i++;
	            }
	        }
	    }

	    // remove all ^
	    for (int i = 0; i < gateString.length(); ) {
	        if (gateString.charAt(i) == '^') {
	            gateString = gateString.substring(0, i) + gateString.substring(i + 1);
	        } else {
	            i++;
	        }
	    }

	    //"HT5T5...T1" (all T's between H's).
	    // store T segments between H's
	    java.util.ArrayList<String> tSegments = new java.util.ArrayList<>();
	    for (int i = 0; i < gateString.length(); ) {
	        if (gateString.charAt(i) != 'H') {
	        	i++; continue; 
	        } 
	        
	        i++; 
	        int start = i;
	        while (i < gateString.length() && gateString.charAt(i) != 'H') i++;
	        if (i > start) {
	            tSegments.add(gateString.substring(start, i));
	        }
	    }

	    //combine T segment like terms and reduce by mod 8
	    StringBuilder fGateString = new StringBuilder();
	    for (int j = 0; j < tSegments.size(); j++) {
	        String seg = tSegments.get(j);
	        int sum = 0;
	        for (int g = 0; g < seg.length(); g++) {
	            char ch = seg.charAt(g);
	            if (Character.isDigit(ch)) sum += (ch - '0');
	        }
	        sum %= 8; 
	        if (sum == 0) {
	        	continue; 
	        }
	        
	        if (fGateString.length() > 0) {
	        	fGateString.append(' ');
	        }
	        fGateString.append('H').append("T^").append(sum);
	    }

	    return fGateString.toString();
	}
	
	
		
		
		private static String reverseStr(String str) {
			 return new StringBuilder(str).reverse().toString();
		}
		
			public static cVector convertToZOmega(DOmega x, DOmega y) {
					
				DOmega delta = new DOmega(1, 1, 1, 1, 0, 1, 0, 1);
				int sde = 0;
				System.out.println("");
				System.out.println("are the entries in ZOmega before delta multiplication?: " + ZOmega.isInRing(x));
				while (ZOmega.isInRing(x) == false || ZOmega.isInRing(y) == false) {
					DOmega tempX = x ;
					DOmega tempY = y;
						x = tempX.multiplication(delta);
						y = tempY.multiplication(delta);
						sde++;
				}
				System.out.println("are the entires in ZOmega after delta multiplicaiton: " + ZOmega.isInRing(x));
				System.out.println("sde is: " + sde);
				ZOmega newX = convertDToZ(x);
				ZOmega newY = convertDToZ(y);
				cVector c = new cVector(newX, newY, sde);
				return c;
				
			}
			
			private static boolean allEven(long[] t) {
			    return (t[0] & 1) == 0 && (t[1] & 1) == 0 && (t[2] & 1) == 0 && (t[3] & 1) == 0;
			}

			/**
			 * Contract:
			 *   Caller guarantees that z actually has SDE >= d 
			 */
			private static ZOmega reduceExactly(ZOmega z, long d) {
			    if (d < 0) throw new IllegalArgumentException("d must be >= 0");
			    long[] t = z.getCoeffs().clone();

			    long m = d / 4;
			    long r = d % 4;

			    for (int i = 0; i < m; i++) {
			        if (!allEven(t)) {
			            throw new RuntimeException("Expected all-even before /2 (group-of-4 part). "
			                    + "d=" + d + " m=" + m + " r=" + r);
			        }
			        t[0] /= 2; t[1] /= 2; t[2] /= 2; t[3] /= 2;
			    }
			    
			    if (r > 0) {
			    	
			    	
			        long need = 4 - r; 
			        for (int k = 0; k < need; k++) {
			        	ZOmega temp = new ZOmega(t[0], t[1], t[2], t[3]);
			        	ZOmega DELTA = new ZOmega(1, 1, 0, 0);
			        	ZOmega newT = temp.multiplication(DELTA);
			            t[0] = newT.getCoeffs()[0];
			            t[1] = newT.getCoeffs()[1];
			            t[2] = newT.getCoeffs()[2];
			            t[3] = newT.getCoeffs()[3];
			        }
			        if (!allEven(t)) {
			            throw new RuntimeException("Expected all-even before the final /2");
			        }
			        t[0] /= 2; t[1] /= 2; t[2] /= 2; t[3] /= 2;
			        
			    }
				
			    return new ZOmega(t[0], t[1], t[2], t[3]);
			}
			
		
			
			private static ZOmega convertDToZ(DOmega d) {
				int a = d.getNumerators()[0]/d.getDenominators()[0];
				int b = d.getNumerators()[1]/d.getDenominators()[1];
				int c = d.getNumerators()[2]/d.getDenominators()[2];
				int e = d.getNumerators()[3]/d.getDenominators()[3];
				return new ZOmega(a, b, c, e);

				
			}
		    private static DOmega readDOmega() {
		    	System.out.println("The general form of a number in D[w] is a + bw + cw^2 + dw^3");
		    	System.out.println("Where w = e^ipi/4");
		    	System.out.println("And a, b, c, d are elements of the dyadic rational numbers, which can be written as z/2^k, where z and k are integers");
		    	System.out.println("If you don't want a specific omega just enter 0 as the numerator");
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
		        input.printDOmega();
		        return input;
		    }
		    
		    private static boolean checkDenom(int n) {
		    	if (n <= 0 || Integer.bitCount(n) != 1) {
			    	 System.out.println("Invalid denominator entry, must be an integer power of 2");
			    	 return false;
		    	}
		    	return true;
		    }
		    
		    public static cVector randomU2Generator() {
				DOmega a = new DOmega(1, 1, 0, 1, 0, 1, 0, 1);
				DOmega c = new DOmega(0, 1, 0, 1, 0, 1, 0, 1);
				cVector identity = Runner.convertToZOmega(a, c);
				
				String gateString = "";
				int gateNum = (int)(Math.random()*20 + 5);
				
				for (int i = 0; i < gateNum; i++) {
					identity.applyHGate();
					int luckyT = (int)(Math.random()*8);
					identity.applyTGate(luckyT);
					
					gateString = gateString + "HT^" + luckyT;
				}
				
				System.out.println(gateString);
				return identity;
				
			}
		    		
		    		
		    
}
