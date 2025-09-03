public class Vec {
    double[] vec = new double[4];
    public Vec() {
        vec[3] = 1.0;
    }


    public Vec (double x, double y, double z) {
        vec[0] = x;
        vec[1] = y;
        vec[2] = z;
        vec[3] = 1.0;
    }


    public void set (double x, double y, double z) {
        vec[0] = x;
        vec[1] = y;
        vec[2] = z;
        vec[3] = 1.0;
    }
    public static double dotProd(Vec in1, Vec in2) {
        return (in1.vec[0]*in2.vec[0])+
                (in1.vec[1]*in2.vec[1])+
                (in1.vec[2]*in2.vec[2]);
    }


    public static Vec crossProd(Vec in1, Vec in2){
        Vec cross = new Vec();
        cross.vec[0] = (in1.vec[1] * in2.vec[2]) - (in1.vec[2] * in2.vec[1]);
        cross.vec[1] = (in1.vec[2] * in2.vec[0]) - (in1.vec[0] * in2.vec[2]);
        cross.vec[2] = (in1.vec[0] * in2.vec[1]) - (in1.vec[1] * in2.vec[0]);
        return cross;
    }


    public Vec normalize () {
        double length = Math.sqrt(
                vec[0]*vec[0]+
                        vec[1]*vec[1]+
                        vec[2]*vec[2]
        );
        return new Vec(
                vec[0]/length,
                vec[1]/length,
                vec[2]/length
        );
    }

    public double length () {
        return Math.sqrt(
                vec[0]*vec[0]+
                        vec[1]*vec[1]+
                        vec[2]*vec[2]);
    }
}
