import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.AlphaComposite;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class CanvasPanel extends JPanel implements ActionListener
{
   private Timer clock, updateFps, output;
   private double count = 0;
   private Model satelite, earth;
   private Camera cam;
   private Vector position;
   private Vector[] earthSystem;
   private AlphaComposite composite;
   private long currentTime, lastTime;
   private int fps, maxFps, minFps;
   private int[] avgFps;
   private int fpsIndex;
   private ProjectionWindow parent;
  
   public CanvasPanel(ProjectionWindow parentIn) {
      parent = parentIn;
      
      setBackground(Color.BLACK);
      
      fps = 1000;
      maxFps = 0;
      minFps = fps;
      
      avgFps = new int[60];
      fpsIndex = 0;
      
      currentTime = 0;
      lastTime = 0;
      
      clock = new Timer(1000 / fps, this);
      clock.setCoalesce(true);
      updateFps = new Timer(150, parent);
      updateFps.setActionCommand("fps timer");
      output = new Timer(5000, this);
      //output.start();
      
      satelite = new Model();
      earth = new Model();
      position = new Vector(0, 400, 0);
      
      int inclination = 20;
      
      earthSystem = new Vector[3];
      earthSystem[1] = new Vector(0, 1, 0);
      earthSystem[2] = (new Vector(Math.sin(inclination * Matrix3x3.TORAD), 0, Math.cos(inclination * Matrix3x3.TORAD))).normalize();
      earthSystem[0] = earthSystem[1].cross(earthSystem[2]);
   }
   
   public void paint(Graphics g) {
      super.paint(g);
      Graphics2D g2D = (Graphics2D) g;
      
      earth.setDrawStack();
      satelite.setDrawStack();
      Surface.drawAll(g2D);
   }
  
   public void actionPerformed(ActionEvent event) {
      if (event.getSource() == clock){
         step();
      } 
      else if (event.getSource() == output) {
         /*
         double avg = 0;
         for (int i = 0; i < avgFps.length; i++) {
             avg += avgFps[i];
         }
         avg /= avgFps.length;
         */
         System.out.println("Max FPS: " + maxFps);
         //System.out.println("Avg FPS: " + avg);
         System.out.println("Min FPS: " + minFps + "\n");
         
         maxFps = 0;
         minFps = fps;
      }
   }
   
   private void step() {
      repaint();
      satelite.addYRotation(count * 1.5);
      satelite.addZRotation(count * 1);
      earth.addZRotation(-0.25 * count);
      
      Matrix3x3 rotate = Matrix3x3.rotateZ(count * 1.5);
      
      Matrix3x3 transform = new Matrix3x3(earthSystem[0], earthSystem[1], earthSystem[2]);
      transform = transform.getTranspose();
      
      satelite.setPosition(transform.multiply(rotate.multiply(position)));
      //Surface.setLightSource(satelite.getPosition());
      
      count += (60 / (double)fps);
      
      currentTime = System.nanoTime();
      fps = (int)(1000000000 / (currentTime - lastTime));
      
      if (fps > maxFps) {
         maxFps = fps;
      } 
      else if (fps < minFps){
         minFps = fps;
      }
      
      avgFps[fpsIndex] = fps;
      fpsIndex++; 
      if (fpsIndex == avgFps.length) {
         fpsIndex = 0;
      }
      
      lastTime = System.nanoTime();
      //clock.stop();
   }
   
   public void load() {
      clock.stop();
      updateFps.stop();
      satelite = new Model(cam);
      satelite.setPosition(position);
      satelite.readFile("input\\rocket5.dat");
      satelite.scale(0.125);
      earth = new Model(cam);
      earth.readFile("input\\sphereGen2.dat");
      //earth.setCoordinateSystem(new Vector(0, 1, 0), new Vector(0, 0, 1), new Vector(1, 0, 0));
      
      Vector xAxis = satelite.getPosition().normalize().scale(-1);
      Vector yAxis = earthSystem[2];
          
      satelite.setCoordinateSystem(xAxis, yAxis, xAxis.cross(yAxis));
      //satelite.isLightingCorrected(false);
      //Surface.setLightSource(satelite.getPosition());
      lastTime = System.nanoTime();
      clock.start();
      updateFps.start();
   }
   
   public void setCamera(Camera newCam) {
      cam = newCam;
      satelite.setCamera(cam);
      earth.setCamera(cam);
      Surface.setCamera(cam);
   }
   
   public Timer getFPSUpdater() {
      return updateFps;
   }
   
   public int getFps() {
      double avg = 0;
      for (int i = 0; i < avgFps.length; i++) {
         avg += avgFps[i];
      }
      avg /= avgFps.length;
      
      return (int)avg;
      
      //return fps;
   }
}
