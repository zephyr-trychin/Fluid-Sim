import java.util.ArrayList;

public class Bucket {
    ArrayList<Particle> particles = new ArrayList<Particle>();

    public Bucket () {

    }

    public void add (Particle particle) {
        particles.add(particle);
    }

    public void clear () {
        particles.clear();
    }

}
