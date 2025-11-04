
public class ZOmega {
	
	//a + bw + cw^2 + dw^3  (if w > 3, just simplify)
	//where w = e^ipi/4
	
	//w^4 = -1
	//w^5 = -w
	//w^6 = -w^2
	//w^7 = -w^3
	//w^8 = 1
	private long [] coeff;
	private static final ZOmega DELTA = new ZOmega(1, 1, 0, 0);

	
	public ZOmega() { //for making a delta
		coeff = new long[4];
		coeff[0] = 1;
		coeff[1] = 1;
		coeff[2] = 0;
		coeff[3] = 0;
	}
	
	public ZOmega(ZOmega other) { //for copying fr
	    long[] c = other.getCoeffs();
	    this.coeff = new long[]{c[0], c[1], c[2], c[3]};
	}
	
	public ZOmega(long a, long b, long c, long d) {
		coeff = new long[4];
		coeff[0] = a;
		coeff[1] = b;
		coeff[2] = c;
		coeff[3] = d;
	}
	
	public long[] getCoeffs() {
		return this.coeff;
	}
	 public ZOmega add(ZOmega other) {
	        return new ZOmega(
	            coeff[0] + other.coeff[0],
	            coeff[1] + other.coeff[1],
	            coeff[2] + other.coeff[2],
	            coeff[3] + other.coeff[3]
	        );
	    }
	    
	    public ZOmega multiplication(ZOmega other) { //0 -> 1, 1 -> w, 2 -> w^2, 3 -> w^3, 4 -> w^4, 5 -> w^5, 6 -> w^6
	    	long [] unSimplified = new long[7];
	    	for (int i = 0; i < 4; i++) {
	    		for (int j = 0; j < 4; j++) {
	    			unSimplified[i + j] += coeff[i] * other.coeff[j];
	    		}
	    	}
	    	long [] zOmega = new long[4];
	    	zOmega[0] = unSimplified[0] - unSimplified[4];
	    	zOmega[1] = unSimplified[1] - unSimplified[5];
	    	zOmega[2] = unSimplified[2] - unSimplified[6];
	    	zOmega[3] = unSimplified[3];
	    	return new ZOmega(zOmega[0], zOmega[1], zOmega[2], zOmega[3]);
	    }
	    
	    private static boolean allEven(long a, long b, long c, long d) {
	        return ( (a & 1) | (b & 1) | (c & 1) | (d & 1) ) == 0;
	    }
	    
	    public void mult(ZOmega other) { //0 -> 1, 1 -> w, 2 -> w^2, 3 -> w^3, 4 -> w^4, 5 -> w^5, 6 -> w^6
	    	long [] unSimplified = new long[7];
	    	for (int i = 0; i < 4; i++) {
	    		for (int j = 0; j < 4; j++) {
	    			unSimplified[i + j] += coeff[i] * other.coeff[j];
	    		}
	    	}
	    	long [] zOmega = new long[4];
	    	zOmega[0] = unSimplified[0] - unSimplified[4];
	    	zOmega[1] = unSimplified[1] - unSimplified[5];
	    	zOmega[2] = unSimplified[2] - unSimplified[6];
	    	zOmega[3] = unSimplified[3];
	    	coeff[0] = zOmega[0];
	    	coeff[1] = zOmega[1];
	    	coeff[2] = zOmega[2];
	    	coeff[3] = zOmega[3];

	    }
	    
	    
	    public long [] factorOutAllDeltas() {  	//immutable
		     long m = 0;
		     long sdeFOut = 0;
		     ZOmega delta = new ZOmega(1, 1, 0, 0);
		     long [] tempC = new long[4];
		     tempC[0] = coeff[0];
		     tempC[1] = coeff[1];
		     tempC[2] = coeff[2];
		     tempC[3] = coeff[3];
		     
		     if (tempC[0] == 0 && tempC[1] == 0 && tempC[2] == 0 && tempC[3] == 0) {
		    	 long [] allZero = new long[5];
		    	 allZero[0] = 0;
		    	 allZero[1] = 0;
		    	 allZero[2] = 0;
		    	 allZero[3] = 0;
		    	 allZero[4] = 0;
		    	 return allZero;
		     }

		     
		     while (tempC[0] % 2 == 0 && tempC[1] % 2 == 0 && tempC[2] % 2 == 0 && tempC[3] % 2 == 0) {
		    	 tempC[0] /= 2;
		    	 tempC[1] /= 2;
		    	 tempC[2] /= 2;
		    	 tempC[3] /= 2;
		    	 m++;
		     }
		     
		     long t = 0; 
		     ZOmega temp = new ZOmega(tempC[0], tempC[1], tempC[2], tempC[3]);
		     while (temp.coeff[0] %2 != 0 || temp.coeff[1] %2 != 0 || temp.coeff[2] %2 != 0  || temp.coeff[3] %2 != 0 ) { //chawnged this loop
		    	 temp = temp.multiplication(delta);
		    	 t++;

		    	 //this.prlongZOmega();
		     }
		     long r = (4 - (t % 4)) % 4; //how many deltas u can factor out
		     if (r != 0) {
		    	 temp.coeff[0] /= 2;
		    	 temp.coeff[1] /= 2;
		    	 temp.coeff[2] /= 2;
		    	 temp.coeff[3] /= 2;
		     }
		     
		     sdeFOut = (4 * m) + r;
		     long [] info = new long[5];
		     info[0] = temp.coeff[0];
		     info[1] = temp.coeff[1];
		     info[2] = temp.coeff[2];
		     info[3] = temp.coeff[3];
		     info[4] = sdeFOut;

		    return info;	     
	    }
	    
	    public boolean isDivByDelta() {
	        long a = coeff[0];
	        long b = coeff[1];
	        long c = coeff[2];
	        long d = coeff[3];

	        return (a - b + c - d) == 0;
	    }
	    
	   
	    public static boolean isInRing(DOmega d) {
	    	int nums[] = d.getNumerators();
	    	int denoms[] = d.getDenominators();
	    	for (int i = 0; i < 4; i++) {
	    		if (isOne(denoms[i]) == false) {
	    			return false;
	    		}
	    	}
	    	return true;
	    	
	    }
	    private static boolean isOne(double num) {
	    	if (num == 1 || num == -1) {
	    		return true;
	    	}
	    	return false;
	    }
	    
	    private static boolean isInteger(double number) {
	        return number == Math.floor(number); 
	    }
	    
	    @Override
	    public String toString() {
	        StringBuilder sb = new StringBuilder();
	        sb.append(coeff[0]);
	        
	        for (int i = 1; i < 4; i++) {
	            if (coeff[i] != 0) {
	                if (coeff[i] > 0) sb.append("+");
	                sb.append(coeff[i]).append("w");
	                if (i > 1) sb.append("^").append(i);
	            }
	        }
	        
	        return sb.toString();
	    }
	    
	    public boolean isZero() {
		    return coeff[0] == 0 &&
		           coeff[1] == 0 &&
		           coeff[2] == 0 &&
		           coeff[3] == 0;
		}
	    @Override
	    public boolean equals(Object o) {
	        if (this == o) {
	        	return true;
	        }
	        if (!(o instanceof ZOmega)) {
	        	return false;
	        }
	        ZOmega other = (ZOmega) o;
	        long[] a = this.coeff; 
	        long [] b = other.coeff;
	        return a[0]==b[0] && a[1]==b[1] && a[2]==b[2] && a[3]==b[3];
	    }

	    public void printZOmega() {
	    	System.out.println(coeff[0] +" + " + coeff[1] + "w + " + coeff[2] + "w^2 + " + coeff[3] + "w^3");
	    }
}
