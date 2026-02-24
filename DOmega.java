import java.util.Arrays;

public class DOmega {
	// General form (a_num/a_denom) + (b_num/b_denom)w + (c_num/c_denom)w^2 + (d_num/d_denom)w^3
    private final int[] numerators;
    private final int[] denominators;
    
    public static final DOmega ONE = new DOmega(1, 1, 0, 1, 0, 1, 0, 1);
    public static final DOmega ZERO = new DOmega(0, 1, 0, 1, 0, 1, 0, 1);

    
    public DOmega(int a_num, int a_denom, 
                  int b_num, int b_denom,
                  int c_num, int c_denom,
                  int d_num, int d_denom) {
        
        if (!isPowerOfTwo(a_denom) || !isPowerOfTwo(b_denom) || 
            !isPowerOfTwo(c_denom) || !isPowerOfTwo(d_denom)) {
            throw new IllegalArgumentException("All denominators must be powers of 2");
        }
                
        numerators = new int[4];
        denominators = new int[4];
        
        numerators[0] = a_num;
        denominators[0] = a_denom; //no w
        numerators[1] = b_num;
        denominators[1] = b_denom; //w
        numerators[2] = c_num;
        denominators[2] = c_denom; //w^2
        numerators[3] = d_num;
        denominators[3] = d_denom; //w^3
        
        normalizeFractions();
       
    }
    
  
    
    public int[] getNumerators() {
        return numerators.clone();
    }
    
    public int[] getDenominators() {
        return denominators.clone();
    }
    
    public DOmega add(DOmega other) {
        int a_denom = lcm(denominators[0], other.denominators[0]);
        int b_denom = lcm(denominators[1], other.denominators[1]);
        int c_denom = lcm(denominators[2], other.denominators[2]);
        int d_denom = lcm(denominators[3], other.denominators[3]);
        
        int a_num = numerators[0] * (a_denom / denominators[0]) + other.numerators[0] * (a_denom / other.denominators[0]); //scale ts by value multiplied by to get to lcm
        int b_num = numerators[1] * (b_denom / denominators[1]) + other.numerators[1] * (b_denom / other.denominators[1]);
        int c_num = numerators[2] * (c_denom / denominators[2]) + other.numerators[2] * (c_denom / other.denominators[2]);
        int d_num = numerators[3] * (d_denom / denominators[3]) + other.numerators[3] * (d_denom / other.denominators[3]);
                   
        return new DOmega(a_num, a_denom, b_num, b_denom, c_num, c_denom, d_num, d_denom);
    }
    private int[] addFractions(int num1, int denom1, int num2, int denom2) {
        int commonDenom = lcm(denom1, denom2);
        int scaledNum1 = num1 * (commonDenom / denom1);
        int scaledNum2 = num2 * (commonDenom / denom2);
        
        int resultNum = scaledNum1 + scaledNum2;
        
        // Simplify
        int g = gcd(Math.abs(resultNum), commonDenom);
        resultNum /= g;
        commonDenom /= g;
        
        // make denominator positive
        if (commonDenom < 0) {
            resultNum = -resultNum;
            commonDenom = -commonDenom;
        }
        
        return new int[] { resultNum, commonDenom };
    }
    

    
    public DOmega scalarMult(int s) {
        return new DOmega(numerators[0] * s, denominators[0], numerators[1] * s, denominators[1], numerators[2] * s, denominators[2], numerators[3] * s, denominators[3]);
    }

    public DOmega conjugate() {
    	
    	return new DOmega(
    	         numerators[0], denominators[0],
    	        -numerators[3], denominators[3],
    	        -numerators[2], denominators[2],
    	        -numerators[1], denominators[1]
    	    );
	
    }
    
    public DOmega multiplication(DOmega other) {
    	 int[] product_num   = new int[4];
    	 int[] product_denom = new int[4];
    	 product_denom[0] = 1;
    	 product_denom[1] = 1;
    	 product_denom[2] = 1;
    	 product_denom[3] = 1;
    	    for (int i = 0; i < 4; i++) {
    	        for (int j = 0; j < 4; j++) {
    	            int tNum   = numerators[i]   * other.numerators[j];
    	            int tDenom = denominators[i] * other.denominators[j];

    	            int[] arr = normalizeFractions(tNum, tDenom);
    	            int num   = arr[0];
    	            int denom = arr[1];

    	            int k = i + j;
    	            
    	            if (k >= 4) {
    	            	k -= 4;
    	            	num = num * -1;
    	            }
    	            int [] sum = addFractions(product_num[k], product_denom[k], num, denom);
    	            product_num[k] = sum[0];
    	            product_denom[k] = sum[1];
    	   
    	          
    	        }
    	    }

    	    return new DOmega(product_num[0], product_denom[0], product_num[1], product_denom[1], product_num[2], product_denom[2], product_num[3], product_denom[3]);
    }
    
    public boolean equals(DOmega other) {
    	boolean aEq = false;
    	boolean bEq = false;
    	boolean cEq = false;
    	boolean dEq = false;
    	int [] oNums = other.getNumerators();
    	int [] oDenoms = other.getDenominators();
    	
    	if (numerators[0] == oNums[0] && denominators[0] == oDenoms[0]) {
    		aEq = true;
    	}
    	if (numerators[1] == oNums[1] && denominators[1] == oDenoms[1]) {
    		bEq = true;
    	}
    	if (numerators[2] == oNums[2] && denominators[2] == oDenoms[2]) {
    		cEq = true;
    	}
    	if (numerators[3] == oNums[3] && denominators[3] == oDenoms[3]) {
    		dEq = true;
    	}
    	
    	if (aEq && bEq && cEq && dEq) {
    		return true;
    	}
    	return false;
    }
    
    public boolean containedInRing() {
        return true;
    }
    
 
    
    public boolean isOne() {
    	 if (numerators[0] == 1 && numerators[1] == 0 && numerators[2] == 0 && numerators[3] == 0) {
         	return true;
         }
         return false;
    }
    public boolean isZero() {
    	 if (numerators[0] == 0 && numerators[1] == 0 && numerators[2] == 0 && numerators[3] == 0) {
          	return true;
          }
          return false;
    }
    
    //helper methods
    private static boolean isPowerOfTwo(int n) {
        return n > 0 && (n & (n - 1)) == 0;  //if one bit is set to 1 than it is a power of 2
    }
    
    private int lcm(int a, int b) { //12, 10 -> (120)/2 = 60 true
        return Math.abs(a * b) / gcd(a, b);
    }
    
    private void normalizeFractions() {
        for (int i = 0; i < 4; i++) {
            if (numerators[i] == 0) {
                denominators[i] = 1;
                continue;
            }
            
            int gcd = gcd(Math.abs(numerators[i]), denominators[i]);
            numerators[i] /= gcd;
            denominators[i] /= gcd;
            
            if (!isPowerOfTwo(denominators[i])) {
                throw new ArithmeticException("Fraction simplification error");
            }
        }
    }
    
    private int [] normalizeFractions(int num, int denom){
        int g = gcd(Math.abs(num), Math.abs(denom));
        num /= g;
        denom /= g;

        // normalize sign so denom always positive
        if (denom < 0) {
            num   = -num;
            denom = -denom;
        }

        return new int[]{ num, denom };
    }
    
    private int gcd(int a, int b) {
        if (b == 0) {
        	return a;
        }
       return gcd(b, a%b);
    }
    

    public void printDOmega() {
    	for (int i = 0; i < 4; i++) {
    		System.out.print(" (" + numerators[i] + "/" + denominators[i] + ")w^" + i + " ");
    		if (i != 3) {
    			System.out.print("+"); 		
    		}
    	}
    }
}
