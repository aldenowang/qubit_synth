
public class methodsTester {
	//15994428 + 22619537w + 15994428w^2 + 0w^3
	//9369319 + 6625109w + 0w^2 + -6625109w^3
	//sde is: 1
	
	//107578520350 + 0w + -107578520350w^2 + -152139002499w^3
	//-107578520350 + -152139002499w + -107578520350w^2 + 0w^3
	//sde is: 2
	
	
	//test reduce exactly cuz it be breaking sometimes and not factoring out anything at sde = 2 and 1
	
	public static void main(String [] args) {
		ZOmega x1 = new ZOmega(15994428, 22619537, 15994428, 0);
		ZOmega y1 = new ZOmega(9369319, 6625109, 0, -6625109);
		cVector sde1 = new cVector(x1, y1, 1);
		
		ZOmega x2 = new ZOmega(107578520350L, 0, -107578520350L, -152139002499L);
		ZOmega y2 = new ZOmega(-107578520350L, -152139002499L, -107578520350L, 0);
		cVector sde2 = new cVector(x2, y2, 2);
		
		
	}
	
}
