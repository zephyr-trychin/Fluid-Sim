public class Particle {
    public Vec pos = new Vec();
    public Vec dir = new Vec();
    double den;


    public Particle() {

    }

    public Particle(double xPos, double yPos, double zPos, double xDir, double yDir, double zDir, double density) {
        pos.vec[0] = xPos;
        pos.vec[1] = yPos;
        pos.vec[2] = zPos;


        dir.vec[0] = xDir;
        dir.vec[1] = yDir;
        dir.vec[2] = zDir;

        den = density;
    }


    public void next(double deltaTime) {
        pos.vec[0] += dir.vec[0] * deltaTime;
        pos.vec[1] += dir.vec[1] * deltaTime;
        pos.vec[1] += dir.vec[2] * deltaTime;
    }

    public static double smoothingKernel(double x1, double r, boolean smooth, boolean derivative) {
        double x;
        if (x1 < 0) {
            x = -x1;
        } else {
            x = x1;
        }

        double temp;
        if (smooth) {
            if (derivative) {
                temp = (r*r) - (x*x);
                temp = temp * temp;
                temp = temp * x * 24;
                temp = temp / (Math.PI*r*r*r*r*r*r*r*r);
                return temp;
            } else {
                temp = (r*r) - (x*x);
                temp = temp * temp * temp;
                temp = temp * 4.0;
                temp = temp / (Math.PI*r*r*r*r*r*r*r*r);
                return temp;
            }
        } else {
            if (derivative) {
                temp = (r) - (x);
                temp = temp * temp;
                temp = temp * x * 24;
                temp = temp / (x*r*r*r*r*r);
                return temp;
            } else {
                temp = (r) - (x);
                temp = temp * temp * temp;
                temp = temp * 8.0;
                temp = temp / (r*r*r*r*r);
                return temp;
            }
        }
    }
}
