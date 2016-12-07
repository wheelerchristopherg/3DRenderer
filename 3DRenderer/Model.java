import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.awt.Graphics;
import java.awt.Color;

public class Model
{
   private Camera cam;
   private Vector[] points;
   private Vector[] transformedPoints;
   private int numbPoints;
   private Surface[] surfaces;
   private int numbSurfaces;
   private Vector position;
   private double scalar;
   private Vector[] axes;
   private RotationType[] rotationStack;
   private Vector[] worldPoints;
   
   public Model() {
      this(null);
   }
   
   public Model(Camera camIn) {
      cam = camIn;
      points = new Vector[50];
      transformedPoints = new Vector[50];
      worldPoints = new Vector[50];
      numbPoints = 0;
      surfaces = new Surface[5];
      numbSurfaces = 0;
      position = new Vector();
      scalar = 1;
      axes = new Vector[3];
      axes[0] = new Vector(1, 0, 0);
      axes[1] = new Vector(0, 1, 0);
      axes[2] = new Vector(0, 0, 1);
      rotationStack = new RotationType[0];
   }
   
   public void readFile(String fileName) {
      points = new Vector[50];
      numbPoints = 0;
      surfaces = new Surface[5];
      numbSurfaces = 0;
      try {
         Scanner fileScanner = new Scanner(new File(fileName));
         String line = fileScanner.nextLine();
         Scanner lineScanner = new Scanner(line);
         
         //reads in points from the given file
         while (!line.equals("")) {
            lineScanner.useDelimiter(", ");
            double x = Double.parseDouble(lineScanner.next());
            double y = Double.parseDouble(lineScanner.next());
            double z = Double.parseDouble(lineScanner.next());
            
            Vector newPoint = new Vector(x, y, z);
            addPoint(newPoint);
            
            line = fileScanner.nextLine();
            lineScanner = new Scanner(line);
         }
         
         line = fileScanner.nextLine();
         lineScanner = new Scanner(line);
         
         //reads in surface values
         while (!line.equals("")) {
            lineScanner.useDelimiter(", ");
            
            Surface newSurface = new Surface();
            addSurface(newSurface);
            
            while (lineScanner.hasNext()) {
               int index = Integer.parseInt(lineScanner.next());
               newSurface.add(transformedPoints[index], 
                             worldPoints[index]);
            }
            
            line = fileScanner.nextLine();
            lineScanner = new Scanner(line);
         }
         
         //reads in color values.
         for (int i = 0; i < numbSurfaces; i++) {
            line = fileScanner.nextLine();
            
            if (line.trim().equals("")) {
               break;
            }
            
            lineScanner = new Scanner(line);
            lineScanner.useDelimiter(", ");
            
            int red = Integer.parseInt(lineScanner.next());
            int green = Integer.parseInt(lineScanner.next());
            int blue = Integer.parseInt(lineScanner.next());
            surfaces[i].setFillColor(new Color(red, green, blue));
         }
         
         fileScanner.close();
      }
      catch (IOException e) {
         System.out.println("Invalid File");
      }
      catch (NumberFormatException e) {
         System.out.println(e);
      }
      catch (IllegalArgumentException e) {
         System.out.println(e);
      }
   }
   
   public void addXRotation(double angleIn) {
      addRotation(new RotationType(RotationType.X, angleIn));
   }
   
   public void addYRotation(double angleIn) {
      addRotation(new RotationType(RotationType.Y, angleIn));
   }
   
   public void addZRotation(double angleIn) {
      addRotation(new RotationType(RotationType.Z, angleIn));
   }
   
   public void isLightingCorrected(boolean value) {
      for (int i = 0; i < numbSurfaces; i++) {
         surfaces[i].setLightingCorrected(value);
      }
   }
   
   private void addRotation(RotationType rot) {
      RotationType[] temp = rotationStack;
      rotationStack = new RotationType[temp.length + 1];
      for (int i = 0; i < temp.length; i++) {
         rotationStack[i] = temp[i];
      }
      rotationStack[temp.length] = rot;
   }
   
   public boolean scale(double scalarIn) {
      if (Math.abs(scalarIn) > 0) {
         scalar = Math.abs(scalarIn);
         return true;
      }
      return false;
   }
   
   public void setPosition(Vector positionIn) {
      position = positionIn;
   }
   
   public boolean setCoordinateSystem(Vector xAxis, Vector yAxis, Vector zAxis) {
      if (xAxis.dot(yAxis) == 0) {
         if (xAxis.cross(yAxis).equals(zAxis)) {
            axes[0].setFromVector(xAxis);
            axes[1].setFromVector(yAxis);
            axes[2].setFromVector(zAxis);
            return true;
         }
      }
      return false;
   }
   
   public Vector getPosition() {
      return position;
   }
   
   public void addPoint(Vector pointIn) {
      if (numbPoints == points.length) {
         Vector[] temp = points;
         Vector[] tempTransformed = transformedPoints;
         points = new Vector[numbPoints + 50];
         transformedPoints = new Vector[numbPoints + 50];
         worldPoints = new Vector[numbPoints + 50];
         for (int i = 0; i < numbPoints; i++) {
            points[i] = temp[i];
            transformedPoints[i] = new Vector();
            worldPoints[i] = new Vector();
         }
      }
      points[numbPoints] = pointIn;
      transformedPoints[numbPoints] = new Vector();
      worldPoints[numbPoints] = new Vector();
      numbPoints++;
      
   }
   
   public void addSurface(Surface surfaceIn) {
      if (numbSurfaces == surfaces.length){
         Surface[] temp = surfaces;
         surfaces = new Surface[numbSurfaces + 5];
         for (int i = 0; i < numbSurfaces; i++) {
            surfaces[i] = temp[i];
         }
      }
      surfaces[numbSurfaces] = surfaceIn;
      numbSurfaces++;
   }
   
   public void setDrawStack() {
      performTransformations();
      for (int i = 0; i < numbSurfaces; i++) {
         surfaces[i].addToDrawStack();
      }
   }
   
   public void setCamera(Camera camIn) {
      cam = camIn;
   }
   
   public void performTransformations() {
      Matrix3x3 transformToScreen = new Matrix3x3(cam.getRight(), cam.getDown(), cam.getNormal());
      
      transformToScreen = transformToScreen.getTranspose().invert();
      
      Matrix3x3 rotate = Matrix3x3.identityMatrix();
      
      for (int i = 0; i < rotationStack.length; i++) {
         rotate = rotate.multiply(rotationStack[i].getRotation());
      }
      rotationStack = new RotationType[0];
      
      Matrix3x3 systemTransform = (new Matrix3x3(axes[0], axes[1], axes[2])).getTranspose();
      
      
      Thread[] threads = new Thread[1];
      
      //long time = System.nanoTime();
      
      for (int i = 0; i < threads.length; i++) {
         threads[i] = new Thread(new PointTransform(i, threads.length, rotate, systemTransform, transformToScreen));
         threads[i].start();
      }
      
      try {
         for (Thread thread : threads) {
            thread.join();
         }
      } 
      catch (InterruptedException e) {
         System.out.println("Interrupted");
      }
      
      //System.out.println((System.nanoTime() - time));
   }
   
   private class PointTransform implements Runnable
   {
      //Vector currentPoint;
      private int initialIndex;
      private int indexIncrement;
      private Matrix3x3 rotate;
      private Matrix3x3 systemTransform;
      private Matrix3x3 transformToScreen;
      
      public PointTransform(int initial, int increment, Matrix3x3 rotateIn, Matrix3x3 systemTransformIn, Matrix3x3 transformToScreenIn) {
         initialIndex = initial;
         indexIncrement = increment;
         rotate = rotateIn;
         systemTransform = systemTransformIn;
         transformToScreen = transformToScreenIn;
      }
      
      public void run() {
         for (int i = initialIndex; i < numbPoints; i += indexIncrement) {
            transformedPoints[i].setFromVector(rotate.multiply(points[i].scale(scalar)));
            transformedPoints[i].setFromVector(systemTransform.multiply(transformedPoints[i]));
            worldPoints[i].setFromVector(transformedPoints[i]);
            transformedPoints[i].setFromVector(transformedPoints[i].add(position).subtract(cam.getLocation()));
            transformedPoints[i].setFromVector(cam.originOffset(cam.traceToCamera(transformToScreen.multiply(transformedPoints[i]))));
         }
      }
   }
   
   private class RotationType
   {
      private int type;
      private double angle;
      private Vector translation;
      
      public static final int X = 0;
      public static final int Y = 1;
      public static final int Z = 2;
      public static final int TRANSLATION = 3;
      
      public RotationType(int typeIn, double angleIn) {
         type = typeIn; 
         angle = angleIn;
         
         if (angle >= 360) {
            angle -= 360;
         } 
         else if (angle < 0) {
            angle += 360;
         }
         
         if (type < 0 || type > 2) {
            type = 0;
         }
      }
      
      public RotationType(double deltaXIn, double deltaYIn, double deltaZIn) {
         type = TRANSLATION;
         translation = new Vector(deltaXIn, deltaYIn, deltaZIn);
      }
      
      public RotationType(Vector translationIn) {
         type = TRANSLATION;
         translation.setFromVector(translationIn);
      }
      
      public Matrix3x3 getRotation() {
         if (type == X) {
            return Matrix3x3.rotateX(angle);
         } 
         else if (type == Y) {
            return Matrix3x3.rotateY(angle);
         } 
         else if (type == Z) {
            return Matrix3x3.rotateZ(angle);
         } else {
            return null;
         }
      }
      
      private Vector performTransformaition(Vector pointIn) {
         if (type == TRANSLATION) {
            return pointIn.add(translation);
         } 
         else {
            return getRotation().multiply(pointIn);
         }
      }
   }
}