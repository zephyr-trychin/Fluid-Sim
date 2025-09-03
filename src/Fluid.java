import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;


public class Fluid extends JPanel {


    int width = 500;
    int height = 500;
    int[] ARGB;
    boolean paused = false;
    boolean drawDensity = true;
    double radius = 0.25;

    ArrayList<Particle> allParticles = new ArrayList<Particle>();
    Bucket[][] buckets = new Bucket[10][10];



    static Fluid me = new Fluid();

    double upperBound = 0.9;
    double lowerBound = 0.1;
    double leftBound = 0.1;
    double rightBound = 0.9;


    BufferedImage rgb_image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);


    public static void main(String[] args) throws IOException {


        System.out.print("Fluid.main( ");
        for (int i = 0; i < args.length; i++) {
            System.out.print(args[i] + ", ");
        }
        System.out.println(")");
        me = new Fluid();
        int w = me.width;
        int h = me.height;
        me.init(w, h);


        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(w, h);
        frame.add(me);
        frame.setVisible(true);


        Thread animThread = new Thread() {
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(5);

                        if (!me.paused) {
                            me.update(5);
                        }

                        /*for (int i = 0; i < me.buckets.length; i++) {
                            for (int j = 0; j < me.buckets[0].length; j++) {
                                me.buckets[j][i].clear();
                            }
                        }*/

                        if(me.drawDensity) {
                            for (Particle particle : me.allParticles) {
                                double test = me.calcVector(particle, me.radius);
                            }
                        }

                        frame.repaint();
                    } catch (Exception e) {


                    }
                }
            }
        };
        animThread.start();


        frame.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
            }


            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {


                }
            }


            @Override
            public void keyReleased(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_SPACE:
                        me.allParticles.clear();
                        for (int j = 0; j < 500; j++) {
                            me.allParticles.add(new Particle(
                                    (Math.random() * 0.8) + 0.1,
                                    (Math.random() * 0.8) + 0.1,
                                    0,
                                    Math.random() * 0.002 - 0.001,
                                    Math.random() * 0.002 - 0.001,
                                    0,
                                    0
                            ));
                        }
                        break;
                    case KeyEvent.VK_P:
                        me.paused = !me.paused;
                    case KeyEvent.VK_D:
                        me.drawDensity = !me.drawDensity;
                }
            }
        });


    }


    public void addParticles(double x1, double y1, double x2, double y2, int xParticles, int yParticles) {
        for (int i = 0; i < xParticles; i++) {
            for (int j = 0; j < yParticles; j++) {
                allParticles.add(new Particle(
                        ((x2 - x1) / (double)xParticles) * (double)i + x1,
                        ((y2 - y1) / (double)yParticles) * (double)j + y1,
                        0.0,
                        0.001,
                        0.0,
                        0.0,
                        0.0
                ));
            }
        }
    }


    public void init(int w, int h) {
        width = w;
        height = h;
        ARGB = new int[width * height];
        int i = 0;
        for (int y = 0; y < height; y++) {
            int red = (y * 255) / (height - 1);
            for (int x = 0; x < width; x++) {
                int green = (x * 255) / (width - 1);
                int blue = 128;
                ARGB[i++] = (red << 16) | (green << 8) | blue;
            }
        }

        for (int k = 0; k < me.buckets.length; k++) {
            for (int j = 0; j < me.buckets[0].length; j++) {
                me.buckets[j][k] = new Bucket();
            }
        }

        addParticles(0.1, 0.3, 0.9, 0.7, 10, 10);


    }


    public static Fluid get() {
        return me;
    }


    public void paint(Graphics g) {


        g.setColor(Color.white);
        g.clearRect(0, 0, this.getWidth(), this.getHeight());
        g.setColor(Color.black);


        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                ARGB[i * width + j] = 0xffffff;
            }
        }

        drawParticles(3, g);


        rgb_image.setRGB(0, 0, width, height, ARGB, 0, width);
        g.drawImage(rgb_image, 0, 0, null);
    }


    public void drawParticles(double size, Graphics g) {
        for (Particle particle : allParticles) {
            drawBox(
                    (int) (particle.pos.vec[0] * me.width),
                    (int) (particle.pos.vec[1] * me.height),
                    (int) size,
                    (int) size,
                    rgbToInt(0, 0, Math.min((int)(particle.den), 255))
            );
        }
    }


    public void drawBox(int x, int y, int width, int height, int col) {
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                ARGB[((y + j) - height / 2) * me.width + ((x + i) - width / 2)] = col;
            }
        }
    }


    public void update(double deltaTime) {
        for (Particle particle : allParticles) {
            keepInBounds(particle, 0.95);
            particle.dir.vec[1] += 0.00001;
            particle.next(deltaTime);
        }
    }


    public void keepInBounds(Particle particle, double elasticity) {
        if (particle.pos.vec[0] < leftBound) {
            particle.pos.vec[0] = leftBound;
            particle.dir.vec[0] = particle.dir.vec[0] * -elasticity;
        }
        if (particle.pos.vec[0] > rightBound) {
            particle.pos.vec[0] = rightBound;
            particle.dir.vec[0] = particle.dir.vec[0] * -elasticity;
        }
        if (particle.pos.vec[1] < lowerBound) {
            particle.pos.vec[1] = lowerBound;
            particle.dir.vec[1] = particle.dir.vec[1] * -elasticity;
        }
        if (particle.pos.vec[1] > upperBound) {
            particle.pos.vec[1] = upperBound;
            particle.dir.vec[1] = particle.dir.vec[1] * -elasticity;
        }
    }

    public double calcVector(Particle particle0, double r) {
        double tempX = 0;
        double tempY = 0;
        Vec tempVec = new Vec(0, 0, 0);
        for (Particle particle1 : allParticles) {
            tempX = (particle0.pos.vec[0] - particle1.pos.vec[0]);
            tempX = tempX * tempX;
            tempY = (particle0.pos.vec[1] - particle1.pos.vec[1]);
            tempY = tempY * tempY;

            if (tempX + tempY <= r*r) {
                tempX = particle1.pos.vec[0] - particle0.pos.vec[0];
                tempY = particle1.pos.vec[1] - particle0.pos.vec[1];
                tempVec.set(tempX,tempY,0);
                //Particle.smoothingKernel(tempVec.length(), r, false, true);
            }
        }
        return 0;
    }

    public double calcDensity (double x, double y, double r){
        double tempX;
        double tempY;
        for (Particle particle : allParticles) {
            tempX = (x - particle.pos.vec[0]);
            tempX = tempX * tempX;
            tempY = (y - particle.pos.vec[1]);
            tempY = tempY * tempY;
            if (tempX + tempY <= r*r) {
                //Particle.smoothingKernel(Math.sqrt(tempX + tempY));
            }
        }
    }

    public int rgbToInt (int r, int g, int b) {
        return (r<<16) | (g<<8) | b;
    }
}
