import java.text.DecimalFormat;

/**
    This class is 3D vector class with all the basic functions to describe and manipulate vectors.
    @author Christopher Wheeler
    @version 1.0
*/
public class Vector
{
   private double x = 0;
   private double y = 0;
   private double z = 0;
   
   /**
       Empty constructor. Sets X, Y, and Z to 0.
   */
   public Vector(){
      this(0,0,0);
   }
   
   /**
       Builds a 2D vector by only requiring X and Y coordinates in integer form.
       Sets Z to 0.
       @param a int initial x value
       @param b int initial y value
   */
   public Vector(int a, int b){
      this((double)a ,(double)b);
   }
   
   /**
       Builds a 3D vector by accepting 3 integer values.
       @param a int initial x value
       @param b int initial y value
       @param c int initial z value
   */
   public Vector(int a, int b, int c){
      this((double)a,(double)b,(double)c);
   }
   
   /**
       Builds a 2D vector with the given double values.
       @param a double initial x value
       @param b double initial y value
       
   */
   public Vector(double a, double b){
      this(a,b,0.0);
   }

   /**
       Builds a 3D vector by accepting 3 double values.
       @param a double initial x value
       @param b double initial y value
       @param c double initial z value
   */
   public Vector(double a, double b, double c){
      x = a;
      y = b;
      z = c;
   }
   
   /**
       Returns the value of X as an integer.
       @return int X
   */
   public int getX(){
      return (int)x;
   }
   
   /**
       Returns the value of Y as an integer.
       @return int Y
   */
   public int getY(){
      return (int)y;
   }
   
   /**
       Returns the value of Z as an integer.
       @return int Z
   */
   public int getZ(){
      return (int)z;
   }
   
   /**
       Returns the value of X as a double.
       @return double X
   */
   public double getXD(){
      return x;
   }
   
   /**
       Returns the value of Y as a double.
       @return double Y
   */
   public double getYD(){
      return y;
   }
   
   /**
       Returns the value of Z as a double.
       @return double Z
   */
   public double getZD(){
      return z;
   }
   
   /**
       Returns an array containing all values of the vector.
       @return double[] vector values
   */
   public double[] toArray() {
      double[] array = {x, y, z};
      return array;
   }
   
   /**
       Returns a vector with a magnitude of 1 in the same direction as the current vector.
       @return Vector normalized vector
   */
   public Vector normalize(){
      return new Vector(x/this.magnitude(),y/this.magnitude(),z/this.magnitude());
   }
   
   /**
       Returns the magnitude of the current vector.
       @return double magnitude
   */
   public double magnitude() {
      return Math.sqrt((x * x) + (y * y) + (z * z));
   }
   
   /**
       Returns the sum of the current vector and an additional vector.
       @param a vector to add to current vector
       @return Vector sum
   */
   public Vector add(Vector a) {
      return new Vector(x + a.x, y + a.y, z + a.z);
   }
   
   /**
       Subtracts the given vector from the current vector.
       @param a vector to subtract
       @return Vector current vector - a
   */
   public Vector subtract(Vector a) {
      return new Vector(x - a.x, y - a.y, z - a.z);
   }
   
   /**
      Returns the current vector scaled the given amount.
       @param scalar double
       @return Vector scaled vector 
   */
   public Vector scale(double scalar) {
      return new Vector(x * scalar, y * scalar, z * scalar);
   }
   
   /**
       Returns the dot product between the current vector and a.
       @param Vector a
       @return double dot product
   */
   public double dot(Vector a){
      return (x * a.x) + (y * a.y) + (z * a.z);
   }
   
   /**
       Returns a string representation of the current vector.
       @return String
   */
   public String toString(){
      DecimalFormat f = new DecimalFormat("0.00000");
      return "\n| " + f.format(x) + " |\n| " + f.format(y) + " |\n| " + f.format(z) + " |\n";
   }
   
   /**
       Returns the cross product between the current vector and b.
       @param Vector b
       @return Vector cross product
   */
   public Vector cross(Vector b){
      return new Vector((y * b.z) - (z * b.y), (z * b.x) - (x * b.z), (x * b.y) - (y * b.x));
   }
   
   /**
       Sets x, y, and z to the values of the given vector. Useful if this Vector is being used 
       as a reference type that is read from multiple objects or methods.
       @param input Vector to copy values from
   */
   public void setFromVector(Vector input) {
      x = input.x;
      y = input.y;
      z = input.z;
   }
   
   /**
       Tests if the given Vector v has the same x, y, and z components as the current Vector.
       @param v Vector object to check for equality
       @return true when equal, false otherwise
   */
   public boolean equals(Vector v) {
      if ((x == v.x) && (y == v.y) && (z == v.z)) {
         return true;
      }
      return false;
   }
}
