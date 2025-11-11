
public class Runner2 {

	public static void main(String[] args) {
    // Instance fields (variables belonging to each object)
    ZOmega x = new ZOmega(1, 0, 0, 0);
    ZOmega y = new ZOmega(0, 0, 0, 0);

    x.printZOmega();
    long k =0;

    cVector vec = new cVector(x,y,k);

    vec.printCVector();

    vec.applyHGate();
    vec.printCVector();

    vec.applyTGate(1);
    vec.applyTGate(1);
    vec.printCVector();

    vec.applyHGate();
    vec.printCVector();
        
    }

}