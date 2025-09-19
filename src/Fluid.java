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
    double radius = 1;
    double densityConst = 0.1;
    double pressureConst = 0.00005;
    double xGrav = 0;
    double yGrav = 0;
    int frameXPos;
    int frameYPos;
    int frameWidth;
    int frameHeight;
    int deltaXPos;
    int deltaYPos;


    PointerInfo pointerInfo = MouseInfo.getPointerInfo();


    Point mouseLocation = pointerInfo.getLocation();


    ArrayList<Particle> allParticles = new ArrayList<Particle>();
    Bucket[][] buckets = new Bucket[10][10];


    static Fluid me = new Fluid();


    double upperBound = 9.0;
    double lowerBound = 1.0;
    double leftBound = 1.0;
    double rightBound = 9.0;


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


        me.frameXPos = frame.getLocation().x;
        me.frameYPos = frame.getLocation().y;


        Thread animThread = new Thread() {
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(5);


                        /*
                         * for (int i = 0; i < me.buckets.length; i++) {
                         * for (int j = 0; j < me.buckets[0].length; j++) {
                         * me.buckets[j][i].clear();
                         * }
                         * }
                         */


                        if (!me.paused) {
                            if (me.drawDensity) {


                                for (Particle particle : me.allParticles) {


                                    Particle tempParticle = new Particle();
                                    tempParticle.next(7);


                                    particle.den = me.calcDensity(particle.pos.vec[0], particle.pos.vec[1],
                                            me.radius);


                                    me.calcVector(particle, me.radius);
                                }
                            }


                            me.deltaXPos = frame.getLocation().x - me.frameXPos;
                            me.deltaYPos = frame.getLocation().y - me.frameYPos;


                            me.deltaXPos = Math.min(me.deltaXPos, 500);
                            me.deltaYPos = Math.min(me.deltaYPos, 500);


                            me.deltaYPos = frame.getLocation().y - me.frameYPos;


                            me.frameXPos = frame.getLocation().x;
                            me.frameYPos = frame.getLocation().y;

                            me.frameHeight = frame.getHeight();
                            me.frameWidth = frame.getWidth();


                            me.upperBound = ((me.frameHeight / 50.0) - 1.0);
                            me.lowerBound = 1.0;
                            me.leftBound = 1.0;
                            me.rightBound = ((me.frameWidth / 50.0) - 1.0);


                            me.update(7);


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
                        me.addParticles(3, 3, 7, 7, 20, 20);
                        break;
                    case KeyEvent.VK_P:
                        me.paused = !me.paused;
                        break;
                    case KeyEvent.VK_D:
                        me.drawDensity = !me.drawDensity;
                        break;
                    case KeyEvent.VK_UP:
                        for (Particle particle : me.allParticles) {
                            particle.dir.vec[1] -= 0.01;
                        }
                        break;
                    case KeyEvent.VK_DOWN:
                        for (Particle particle : me.allParticles) {
                            particle.dir.vec[1] += 0.01;
                        }
                        break;
                    case KeyEvent.VK_RIGHT:
                        for (Particle particle : me.allParticles) {
                            particle.dir.vec[0] += 0.01;
                        }
                        break;
                    case KeyEvent.VK_LEFT:
                        for (Particle particle : me.allParticles) {
                            particle.dir.vec[0] -= 0.01;
                        }
                        break;
                    case KeyEvent.VK_V:
                        me.xGrav = -0.0005;
                        me.yGrav = 0.0005;
                        break;
                    case KeyEvent.VK_B:
                        me.xGrav = 0;
                        me.yGrav = 0.0005;
                        break;
                    case KeyEvent.VK_N:
                        me.xGrav = 0.0005;
                        me.yGrav = 0.0005;
                        break;
                    case KeyEvent.VK_F:
                        me.xGrav = -0.0005;
                        me.yGrav = 0;
                        break;
                    case KeyEvent.VK_G:
                        me.xGrav = 0;
                        me.yGrav = 0;
                        break;
                    case KeyEvent.VK_H:
                        me.xGrav = 0.0005;
                        me.yGrav = 0;
                        break;
                    case KeyEvent.VK_R:
                        me.xGrav = -0.0005;
                        me.yGrav = -0.0005;
                        break;
                    case KeyEvent.VK_T:
                        me.xGrav = 0;
                        me.yGrav = -0.0005;
                        break;
                    case KeyEvent.VK_Y:
                        me.xGrav = 0.0005;
                        me.yGrav = -0.0005;
                        break;
                }
            }
        });


    }


    public void addParticles(double x1, double y1, double x2, double y2, int xParticles, int yParticles) {
        for (int i = 0; i < xParticles; i++) {
            for (int j = 0; j < yParticles; j++) {
                allParticles.add(new Particle(
                        ((x2 - x1) / (double) xParticles) * (double) i + x1,
                        ((y2 - y1) / (double) yParticles) * (double) j + y1,
                        0.0,
                        0.0,
                        0.0,
                        0.0,
                        0.0));
            }
        }
    }


    public void init(int w, int h) {
        width = w;
        height = h;
        ARGB = new int[5000 * 5000];
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


        addParticles(1, 3, 9, 7, 20, 20);


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
                // double tempDen = me.calcDensity(j, i, me.radius);
                // tempDen /= 1;
                // rgbToInt((int) (255 - tempDen), 0, (int) (tempDen));
                ARGB[i * width + j] = 0xfffffff;
            }
        }


        drawParticles(5, g);


        rgb_image.setRGB(0, 0, width, height, ARGB, 0, width);
        g.drawImage(rgb_image, 0, 0, null);
    }


    public void drawParticles(double size, Graphics g) {
        int col;
        double tempCol;
        double tempDen;
        for (Particle particle : allParticles) {
            // tempDen = particle.den / densityConst;
            /*
             * if (tempDen < 255) {
             * col = rgbToInt(0, 0, (int) tempDen);
             * } else if (tempDen < 512) {
             * tempDen = (particle.den / densityConst) - 255;
             * col = rgbToInt((int) (tempDen), 0, (255 - (int) tempDen));
             * } else {
             * tempDen = (particle.den / densityConst) - 511;
             * if (tempDen > 255) {
             * tempDen = 255;
             * }
             * col = rgbToInt(255, (int) (tempDen * 0.8), (int) (tempDen * 0.8));
             * }
             */
            tempCol = Math
                    .sqrt((particle.dir.vec[0] * particle.dir.vec[0]) + (particle.dir.vec[1] * particle.dir.vec[1]));
            col = (int) (tempCol * 7000);
            col = rgbToInt(col, 0, 255 - col);


            drawBox(
                    (int) (particle.pos.vec[0] * (me.width / 10)),
                    (int) (particle.pos.vec[1] * (me.height / 10)),
                    (int) size,
                    (int) size,
                    col);
        }
    }


    public void drawBox(int x, int y, int w, int h, int col) {
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                ARGB[((y + j) - h / 2) * me.width + ((x + i) - w / 2)] = col;
            }
        }
    }


    public void update(double deltaTime) {


        double tempX = 0;
        double tempY = 0;
        double temp;
        double tempTheta = 0;
        double tempAdd = 0;
        int xSign;
        int ySign;
        Vec tempVec = new Vec(0.0, 0.0, 0.0);
        double r = me.radius;


        for (Particle particle : allParticles) {
            particle.next(deltaTime);
            keepInBounds(particle, 0.95);
            particle.dir.vec[0] += xGrav - (deltaXPos / 10000.0);
            particle.dir.vec[1] += yGrav - (deltaYPos / 10000.0);
            particle.dir.vec[0] *= 0.99;
            particle.dir.vec[1] *= 0.99;


            tempX = (particle.pos.vec[0] - mouseLocation.getX());
            tempX = tempX * tempX;
            tempY = (particle.pos.vec[1] - mouseLocation.getY());
            tempY = tempY * tempY;


            if (tempX + tempY <= r * r) {


                tempX = particle.pos.vec[0] - mouseLocation.getX();
                if (tempX < 0) {
                    tempX = -tempX;
                    xSign = 1;
                } else {
                    xSign = -1;
                }


                tempY = particle.pos.vec[1] - mouseLocation.getY();
                if (tempY < 0) {
                    tempY = -tempY;
                    ySign = 1;
                } else {
                    ySign = -1;
                }


                temp = Particle.smoothingKernel(Math.sqrt(tempX + tempY), r, false, true);


                if (tempX == 0) {
                    tempVec.vec[1] = tempVec.vec[1] + (temp * me.pressureConst);
                } else if (tempY == 0) {
                    tempVec.vec[0] = tempVec.vec[0] + (temp * me.pressureConst);
                } else {


                    tempTheta = Math.atan(tempY / tempX);


                    tempAdd = temp;
                    tempAdd *= me.pressureConst;


                    tempVec.vec[0] = tempVec.vec[0] + (tempAdd * xSign * Math.cos(tempTheta));
                    tempVec.vec[1] = tempVec.vec[1] + (tempAdd * ySign * Math.sin(tempTheta));
                    particle.dir.vec[0] -= (tempAdd * xSign * Math.cos(tempTheta));
                    particle.dir.vec[1] -= (tempAdd * ySign * Math.sin(tempTheta));
                }
            }
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


    public void calcVector(Particle particle0, double r) {
        double tempX = 0;
        double tempY = 0;
        double temp;
        double tempTheta = 0;
        double tempAdd = 0;
        int xSign;
        int ySign;
        Vec tempVec = new Vec(0.0, 0.0, 0.0);
        for (Particle particle1 : me.allParticles) {
            if (particle0 != particle1) {
                tempX = (particle0.pos.vec[0] - particle1.pos.vec[0]);
                tempX = tempX * tempX;
                tempY = (particle0.pos.vec[1] - particle1.pos.vec[1]);
                tempY = tempY * tempY;


                if (tempX + tempY <= r * r) {


                    tempX = particle1.pos.vec[0] - particle0.pos.vec[0];
                    if (tempX < 0) {
                        tempX = -tempX;
                        xSign = 1;
                    } else {
                        xSign = -1;
                    }


                    tempY = particle1.pos.vec[1] - particle0.pos.vec[1];
                    if (tempY < 0) {
                        tempY = -tempY;
                        ySign = 1;
                    } else {
                        ySign = -1;
                    }


                    temp = Particle.smoothingKernel(Math.sqrt(tempX + tempY), r, false, true);


                    if (tempX == 0) {
                        if (particle0.pos.vec[1] < particle1.pos.vec[1]) {
                            tempVec.vec[1] = tempVec.vec[1] - (temp * me.pressureConst);
                        } else {
                            tempVec.vec[1] = tempVec.vec[1] + (temp * me.pressureConst);
                        }
                    } else {


                        if (tempY == 0) {
                            if (particle0.pos.vec[0] < particle1.pos.vec[0]) {
                                tempVec.vec[0] = tempVec.vec[0] - (temp * me.pressureConst);
                            } else {
                                tempVec.vec[0] = tempVec.vec[0] + (temp * me.pressureConst);
                            }
                        } else {


                            tempTheta = Math.atan(tempY / tempX);


                            tempAdd = temp;
                            tempAdd *= me.pressureConst;


                            tempVec.vec[0] = tempVec.vec[0] + (tempAdd * xSign * Math.cos(tempTheta));
                            tempVec.vec[1] = tempVec.vec[1] + (tempAdd * ySign * Math.sin(tempTheta));
                            particle1.dir.vec[0] -= (tempAdd * xSign * Math.cos(tempTheta)) / (2);
                            particle1.dir.vec[1] -= (tempAdd * ySign * Math.sin(tempTheta)) / (2);
                        }
                    }
                }
            }
        }
        particle0.dir.vec[0] += tempVec.vec[0] / 2;
        particle0.dir.vec[1] += tempVec.vec[1] / 2;
    }


    public double calcDensity(double x, double y, double r) {
        double tempX;
        double tempY;
        double temp = 0;
        for (Particle particle : allParticles) {
            tempX = (x - particle.pos.vec[0]);
            tempX = tempX * tempX;
            tempY = (y - particle.pos.vec[1]);
            tempY = tempY * tempY;
            if (tempX + tempY <= r * r) {
                temp += (double) Particle.smoothingKernel(Math.sqrt(tempX + tempY), r, true, false);
            }
        }
        return temp;
    }


    public int rgbToInt(int r, int g, int b) {
        return (r << 16) | (g << 8) | b;
    }
}
