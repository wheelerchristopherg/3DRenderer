import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.awt.AlphaComposite;
import java.awt.image.BufferedImage;
import java.awt.RenderingHints;

/**
    This class is used to store information about an individual surface,
    including its color, lighting corrected color, points with respect to the 3D
    world, and points with respect to the 2D surface of the screen. It's also used
    to determine the order in which the surfaces are drawn to the screen.
*/
public class Surface
{
   private Vector[] points;
   private Vector[] worldPoints;
   private int numbPoints;
   private Color fillColor;
   private Color lightCorrectedFill;
   private double depth;
   private boolean colorCorrected;
   
   private static Surface[] drawOrder = new Surface[50];
   private static boolean wireframe = false;
   private static int visibleSurfaces = 0;
   private static Vector[] lightSource = new Vector[1];
   private static Camera cam = null;
   private static int numbLights = 0;
   
   public Surface() {
      points = new Vector[5];
      worldPoints = new Vector[5];
      numbPoints = 0;
      fillColor = Color.WHITE;
      depth = 0;
      colorCorrected = true;
   }
   
   /**
       Sets the global reference for the camera object.
   */
   public static void setCamera(Camera camIn) {
      cam = camIn;
   }
   
   /**
       Global switch for wireframe whether to display as wire frame
       or filled surfaces.
   */
   public static void setWireframe(boolean state) {
      wireframe = state;
   }
   
   /**
       Adds a light source to the system.
   */
   public static void addLightSource(Vector source) {
      if (numbLights >= lightSource.length) {
         Vector[] temp = lightSource;
         lightSource = new Vector[numbLights + 2];
         for (int i = 0; i < temp.length; i++) {
            lightSource[i] = temp[i];
         }
      }
      lightSource[numbLights] = source;
      numbLights++;
   }
   
   /**
       Determines whether or not this individual surface is lit.
   */
   public void setLightingCorrected(boolean value) {
      colorCorrected = value;
   }
   
   /**
       Adds the references of each point that make up the surface,
       both in world coordinates and coordinates relative to the camera.
   */
   public void add(Vector pointIn, Vector worldPointIn) {
      if (numbPoints == points.length) {
         Vector[] temp = points;
         Vector[] temp1 = worldPoints;
         points = new Vector[numbPoints + 50];
         worldPoints = new Vector[numbPoints + 50];
         for (int i = 0; i < numbPoints; i++) {
            points[i] = temp[i];
            worldPoints[i] = temp1[i];
         }
      }
      points[numbPoints] = pointIn;
      worldPoints[numbPoints] = worldPointIn;
      numbPoints++;
   }
   
   /**
       Sets the color of the surface.
   */
   public void setFillColor(Color colorIn) {
      fillColor = colorIn;
      lightCorrectedFill = fillColor;
   }
   
   /**
       Adds the surface to the global draw stack in the order of closest to farthest
       from the screen.
   */
   public void addToDrawStack() {
      //checks if the surface is visible
      if (isVisible()) {
         //determines whether or not to make use of the lighting system.
         //only does so if the wireframe diagram switch is off, the light
         //corrected switch is on, and a light source exists. Otherwise,
         //the surface colors are not changed.
         if ((numbLights > 0) && (!wireframe) && colorCorrected) {
            setLighting();
         }
         
         //determines where to place the surface in the draw stack.
         if (drawOrder.length < visibleSurfaces + 1) {
            Surface[] temp = drawOrder;
            drawOrder = new Surface[visibleSurfaces + 50];
            for (int i = 0; i < visibleSurfaces; i++) {
               drawOrder[i] = temp[i];
            }
            //System.out.println("Expand array: " + drawOrder.length);
         }
         
         int left = 0;
         int right = visibleSurfaces - 1;
         int insertIndex = 0;
         if (right > left) {
            insertIndex = left + ((right - left) / 2);
            double otherDepth = drawOrder[insertIndex].getDepth();
            while ((otherDepth != depth) && (left != right)) {
               if (depth < otherDepth) {
                  right = (insertIndex - 1 >= left) ? insertIndex - 1 : left;
               }
               if (depth > otherDepth) {
                  left = (insertIndex + 1 <= right) ? insertIndex + 1 : right;
               }
               insertIndex = left + ((right - left) / 2);
               otherDepth = drawOrder[insertIndex].getDepth();
            }
         }
            
         visibleSurfaces++;
         //drawOrder[visibleSurfaces - 1] = null;
         for (int i = visibleSurfaces - 1;(i > insertIndex) && (i > 0); i--) {
            drawOrder[i] = drawOrder[i - 1];
         }
         drawOrder[insertIndex] = this;
      }
   }
   
   /**
       Calculates the shading of the surface given the light source locations
   */
   private void setLighting() {
      Vector line1 = worldPoints[1].subtract(worldPoints[0]);
      
      Vector line2 = worldPoints[2].subtract(worldPoints[1]);
      
      Vector surfaceNormal = line1.cross(line2).normalize();
      
      Vector sum = new Vector();
      
      for (int i = 0; i < numbPoints; i++) {
         sum.add(worldPoints[i]);
      }
      
      Vector avg = sum.scale(1 / numbPoints);
      
      double lightingPercentage = 0;
      
      for (int i = 0; i < numbLights; i++) {
         Vector lightNormal = avg.subtract(lightSource[i]).normalize();
         double dot = surfaceNormal.dot(lightNormal);
         
         if (-dot > 0) {
            lightingPercentage += -dot;
         }
      }
      
      if (lightingPercentage < 0) {
         lightingPercentage = 0;
      } 
      else if (lightingPercentage > 1) {
         lightingPercentage = 1;
      }
      
      int red = (int)(fillColor.getRed() * lightingPercentage);
      int green = (int)(fillColor.getGreen() * lightingPercentage);
      int blue = (int)(fillColor.getBlue() * lightingPercentage);
      
      lightCorrectedFill = new Color(red, green, blue);
   }
   
   public static void drawAll(Graphics2D g) {
      //System.out.println(visibleSurfaces);
      BufferedImage bufferedImage = new BufferedImage(cam.getWidth(), cam.getHeight(), BufferedImage.TYPE_INT_ARGB);
      Graphics2D gbi = bufferedImage.createGraphics();
      //gbi.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      gbi.setComposite(AlphaComposite.getInstance(AlphaComposite.DST_OVER));
      for (int i = 0; i < visibleSurfaces; i++) {
      //for (int i = visibleSurfaces - 1; i >= 0; i--) {
         drawOrder[i].draw(gbi);
      }
      
      g.drawImage(bufferedImage, null, 0, 0);
      
      visibleSurfaces = 0;
   }
   
   public void draw(Graphics2D g) {
      Path2D p = new Path2D.Double();
      
      p.moveTo(points[0].getXD(), points[0].getYD());
      for (int i = 0; i < numbPoints; i++) {
         p.lineTo(points[i].getXD(), points[i].getYD());
      }
      p.lineTo(points[0].getXD(), points[0].getYD());
      
      if (!wireframe) {
         g.setColor(lightCorrectedFill);
         g.fill(p);
      } 
      else {
         g.setColor(Color.BLACK);
         g.draw(p);
      }
      
   }
   
   private boolean isVisible() {
      
      Vector line1 = points[1].subtract(points[0]);
      
      Vector line2 = points[2].subtract(points[1]);
      
      Vector surfaceNormal = line1.cross(line2).normalize();
      
      double dot = surfaceNormal.dot(new Vector(0, 0, 1));
      boolean visible = false;
      double sum = 0;
      
      //long time = System.nanoTime();
      if (dot < 0 || wireframe) {
         for (int i = 0; i < numbPoints; i++) {
            if ((points[i].getZ() >= 0) &&
               (points[i].getX() > 0) &&
               (points[i].getX() < cam.getWidth()) &&
               (points[i].getY() > 0) &&
               (points[i].getY() < cam.getHeight())) {
               visible = true;
            }
            sum += points[i].getZD();
         }
      }
      depth = sum / numbPoints;
      
      if (depth < 0) {
         visible = false;
      }
      
      //System.out.println("visibility: " + (System.nanoTime() - time));
      
      return visible;
   }
   
   public double getDepth() {
      return depth;
   }
   
}
