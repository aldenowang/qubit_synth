
public class cVector {
	
	private ZOmega [] col = new ZOmega[2];
	private long k;
	
	public cVector(ZOmega x, ZOmega y, long k) {
		
		col[0] = x;
		col[1] = y;
		this.k = k;
	}
	
	private static ZOmega negative(ZOmega z){
	    long[] c = z.getCoeffs();
	    return new ZOmega(-c[0], -c[1], -c[2], -c[3]);
	}
	
	
	public void applyHGate() {
		/*
		 ZOmega new0 = col[0].add(col[1]);         // x + y
		 ZOmega new1 = col[0].add(negative(col[1])); // x - y
		 col[0] = new0;
		 col[1] = new1;
		 
		 ZOmega tempX = col[0];
		 ZOmega tempY = col[1];
		 this.k += 2;
		*/
		ZOmega new0 = col[0].add(col[1]);
		ZOmega new1 = col[0].add(negative(col[1]));
		col[0] = new0;
		col[1] = new1;
		
		ZOmega tempX = col[0];
		ZOmega tempY = col[1];
		
		long xSDE = tempX.factorOutAllDeltas()[4];
		long ySDE = tempY.factorOutAllDeltas()[4];
		long minDrop = Math.min(xSDE, ySDE);
		
		col[0] = Tester.reduceExactly(tempX, minDrop);
		col[1] = Tester.reduceExactly(tempY, minDrop);
		this.k = this.k + (2 - minDrop);
		 
		 
	}
 	
	
	
	public void applyTGate(int power) {
		ZOmega w;
		switch (power) {
		case 0: w = new ZOmega(1, 0, 0, 0); break;
		case 1: w = new ZOmega(0, 1, 0, 0); break;
		case 2: w = new ZOmega(0, 0, 1, 0); break;
		case 3: w = new ZOmega(0, 0, 0, 1); break;
		case 4: w = new ZOmega(-1, 0, 0, 0); break;
		case 5: w = new ZOmega(0, -1, 0, 0); break;
		case 6: w = new ZOmega(0, 0, -1, 0); break;
		case 7: w = new ZOmega(0, 0, 0, -1); break;
		default: w = new ZOmega(1, 0, 0, 0); 
		}
		ZOmega temp = new ZOmega(col[1]);
		col[1] = temp.multiplication(w);
	}
	

	
	public void incrementK(long n) {
		k += n;
	}
	
	public long getK() {
		return k;
	}
	public ZOmega[] getCol() {
		return col;
	}
	public ZOmega getX() {
		return col[0];
	}
	public ZOmega getY() {
		return col[1];
	}
	public void setX(ZOmega x) {
		col[0] = x;
	}
	public void setY(ZOmega y) {
		col[1] = y;
	}
	
	
	public void printCVector() {
		System.out.println(col[0] + " " + col[1] + " sde = " + k);
	}
	
	
	
}
