import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Tester {
	
	//infinite looping right now
	
	public static void main(String[] args) {
	
		String gateString = "";
		int loopLength = (int)(Math.random()*30 + 20);
		boolean firstLoop = true;
		ZOmega one = new ZOmega(1, 0, 0, 0);
		ZOmega zero = new ZOmega(0, 0, 0, 0);
		cVector tester = new cVector(one, zero, 0);
		
		for (int i = 0; i < loopLength; i++) {
			int hOrT = (int)(Math.random() * 2);
			if (hOrT == 0 || firstLoop == true) {
				firstLoop = false;
				tester.applyHGate();
				gateString = gateString + "H";
			}
			else {
				int tPower = (int)(Math.random() * 8);
				tester.applyTGate(tPower);
				gateString = gateString + "T^" + tPower + " ";
			}
			
		}
		 
		
		tester.getX().printZOmega();
		tester.getY().printZOmega();
		System.out.println("sde is: " + tester.getK());
		System.out.println("");
		System.out.println("actual gate string is: " + gateString);
		//String reducedGateString = reduceGateSequence(gateString);
		//System.out.println("reduced act string is: " + reducedGateString);
		
		/*
		String testedGateString = "";	
		long dropX = tester.getX().factorOutAllDeltas()[4];
		long dropY = tester.getY().factorOutAllDeltas()[4];
		long minDrop = Math.min(dropX, dropY);
		
		ZOmega temporaryX = new ZOmega(tester.getX());
		ZOmega temporaryY = new ZOmega(tester.getY());
		
		tester.setX(reduceExactly(temporaryX, minDrop));
		tester.setY(reduceExactly(temporaryY, minDrop));
		tester.incrementK(-minDrop);
		*/
		
		int iter = 0;
		final int MAX_ITERS = 50;
		while (tester.getK() > 0 && iter++ < MAX_ITERS) {
	
			if (tester.getK() <= 2) {
				tester.getX().printZOmega();
				tester.getY().printZOmega();
			}
			
			long bestDrop = -1;
			int maxT = 0;

			for (int i = 0; i < 4; i++) {
			    cVector temp = new cVector(new ZOmega(tester.getX()), new ZOmega(tester.getY()), tester.getK());
			    long sdeBefore = temp.getK();
			    temp.applyTGate(i);
			    temp.applyHGate();
			    
			    if (temp.getX().isZero()) {
			    	continue;
			    }
			    
			    long [] xInfo = temp.getX().factorOutAllDeltas();
			    long [] yInfo = temp.getY().factorOutAllDeltas();
			    long sdeAfter = temp.getK();

			    if (sdeAfter > sdeBefore) {
			    	gateString = gateString + "H" + "T^" + i;
			    	tester = new cVector(temp.getX(), temp.getY(), temp.getK());
			    	break;
			    }
  	
			  
			}
			
		}

		
		System.out.println();
		//System.out.println("unreduced gate string is: " + testedGateString);
		//String reducedString = reduceGateSequence(testedGateString);
		//System.out.println("reduced gate string is: " + reducedString);
		 
		 
	        
	   }
		
		
	private static String reduceGateSequence(String s) {
		 if (s == null) return "";
		    s = s.trim().replaceAll("\\s+", " ");

		    // 1) tokenize into ["H", "T^k", ...]
		    java.util.List<String> toks = new java.util.ArrayList<>();
		    java.util.regex.Matcher m = java.util.regex.Pattern
		        .compile("(H|T\\^[0-9]+)")
		        .matcher(s);
		    while (m.find()) toks.add(m.group());

		    // 2) single pass: combine adjacent T's, cancel HH, drop T^0
		    java.util.List<String> out = new java.util.ArrayList<>();
		    for (String tok : toks) {
		        if (tok.charAt(0) == 'T') {
		            int k = Integer.parseInt(tok.substring(2)); // after "T^"
		            if (!out.isEmpty() && out.get(out.size()-1).startsWith("T^")) {
		                int prev = Integer.parseInt(out.get(out.size()-1).substring(2));
		                int sum = (prev + k) & 7; // mod 8
		                if (sum == 0) {
		                    out.remove(out.size()-1); // T^0 disappears
		                } else {
		                    out.set(out.size()-1, "T^" + sum);
		                }
		            } else {
		                if ((k & 7) != 0) out.add("T^" + (k & 7));
		            }
		        } else { // 'H'
		            if (!out.isEmpty() && out.get(out.size()-1).equals("H")) {
		                out.remove(out.size()-1); // cancel HH
		            } else {
		                out.add("H");
		            }
		        }
		    }

		    // 3) join
		    return String.join("", out);
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
				ZOmega newX = testernvertDToZ(x);
				ZOmega newY = testernvertDToZ(y);
				cVector c = new cVector(newX, newY, sde);
				return c;
				
			}
			
		private static boolean allEven(long[] t) {
		    return (t[0] & 1L) == 0 && (t[1] & 1L) == 0 && (t[2] & 1L) == 0 && (t[3] & 1L) == 0;
		}

			
			public static ZOmega reduceExactly(ZOmega z, long d) {
			    if (d < 0) {
				    	throw new IllegalArgumentException("d must be >= 0");
				    }
				    
			    long[] t = z.getCoeffs().clone();
			    long m = d / 4;
			    long r = d % 4; //remainder
				    
			    for (long i = 0; i < m; i++) {
				        if (!allEven(t)) {
				            throw new RuntimeException("Expected all-even before /2 (group-of-4 part).");
				        }
				    
			        t[0] /= 2; t[1] /= 2; t[2] /= 2; t[3] /= 2;
				    }
				    if (r > 0) {
			        long need = 4 - r; 
			        for (long k = 0; k < need; k++) {
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
			
		
		
			
			private static ZOmega testernvertDToZ(DOmega d) {
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
	
	


	