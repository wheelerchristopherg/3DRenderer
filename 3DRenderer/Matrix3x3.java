import java.text.DecimalFormat;

public class Matrix3x3
{
   //top level array is rows, second level is columbs
   private double[][] matrix = new double[3][3];
   
   public static final double TORAD = Math.PI / 180;
   
   /**
       Creates a 3x3 matrix out of 3 vectors using each vector as a columb.
       @param column1 Vector
       @param column2 Vector
       @param column3 Vector
   */
   public Matrix3x3(Vector column1, Vector column2, Vector column3) {
      matrix[0] = column1.toArray();
      matrix[1] = column2.toArray();
      matrix[2] = column3.toArray();
   }
   
   public Matrix3x3(double[][] matrixArray) throws IllegalArgumentException {
      try {
         for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
               matrix[i][j] = matrixArray[i][j];
            }
         }
      } 
      catch(ArrayIndexOutOfBoundsException e) {
         throw new IllegalArgumentException("Should be a 3x3 array.");
      }
   }
   
   public Matrix3x3() {
      for (int i = 0; i < 3; i++) {
         for (int j = 0; j < 3; j++) {
            matrix[i][j] = 0;
         }
      }
   }
   
   public boolean editElement(int row, int columb, double newValue) {
      if ((row < 3) && (columb < 3) && (row >= 0) && (columb >= 0)) {
         matrix[row][columb] = newValue;
         return true;
      }
      return false;
   }
   
   public double getElement(int row, int columb) throws IllegalArgumentException {
      if ((row < 3) && (columb < 3) && (row >= 0) && (columb >= 0)) {
         return matrix[row][columb];
      } 
      else {
         throw new IllegalArgumentException("Values must be less than 3 and greater than or equal to 0.");
      }
   }
   
   /**
       Returns a String representation of the matrix.
       @return String visual representation of the matrix
   */
   public String toString() {
      DecimalFormat f = new DecimalFormat("0.00000");
      String output = "";
      for (int i = 0; i < 3; i++) {
         output += "\n| ";
         for (int j = 0; j < 3; j++) {
            output += f.format(matrix[j][i]) + " ";
         }
         output += "|";
      }
      output += "\n";
      return output;
   }
   
   public Matrix3x3 invert() {
      Matrix3x3 inverted = new Matrix3x3();
      
      inverted.matrix[0][0] = determinant2x2(matrix[1][1], matrix[1][2], matrix[2][1], matrix[2][2]);
      inverted.matrix[0][1] = determinant2x2(matrix[0][2], matrix[0][1], matrix[2][2], matrix[2][1]);
      inverted.matrix[0][2] = determinant2x2(matrix[0][1], matrix[0][2], matrix[1][1], matrix[1][2]);
      inverted.matrix[1][0] = determinant2x2(matrix[1][2], matrix[1][0], matrix[2][2], matrix[2][0]);
      inverted.matrix[1][1] = determinant2x2(matrix[0][0], matrix[0][2], matrix[2][0], matrix[2][2]);
      inverted.matrix[1][2] = determinant2x2(matrix[0][2], matrix[0][0], matrix[1][2], matrix[1][0]);
      inverted.matrix[2][0] = determinant2x2(matrix[1][0], matrix[1][1], matrix[2][0], matrix[2][1]);
      inverted.matrix[2][1] = determinant2x2(matrix[0][1], matrix[0][0], matrix[2][1], matrix[2][0]);
      inverted.matrix[2][2] = determinant2x2(matrix[0][0], matrix[0][1], matrix[1][0], matrix[1][1]);
      
      try {
         inverted = inverted.scale(1 / getDeterminate());
      }
      catch (Exception e) {
         System.out.println(e);
      }
      
      return inverted;
   }
   
   public static double determinant2x2(double a, double b, double c, double d) {
      return (a * d) - (b * c);
   }
   
   public Matrix3x3 getTranspose() {
      Matrix3x3 transpose = this.clone();
      
      for (int i = 0; i < 3; i++) {
         for (int j = 0; j < 3; j++) {
            if (i != j) {
               transpose.matrix[i][j] = matrix[j][i];
            }
         }
      }
      
      return transpose;
   }
   
   public double getDeterminate() {
      double a = matrix[0][0] * determinant2x2(matrix[1][1], matrix[1][2], matrix[2][1], matrix[2][2]);
      double b = matrix[0][1] * determinant2x2(matrix[1][0], matrix[1][2], matrix[2][0], matrix[2][2]);
      double c = matrix[0][2] * determinant2x2(matrix[1][0], matrix[1][1], matrix[2][0], matrix[2][1]);
      
      return a - b + c;
   }
   
   public Matrix3x3 clone() {
      Matrix3x3 output = null;
      try {
         output = new Matrix3x3(matrix.clone());
      }
      catch (IllegalArgumentException e) {
         System.out.println(e);
      }
      
      return output;
   }
   
   public Matrix3x3 scale(double scalar) {
      Matrix3x3 output = clone();
      for (int i = 0; i < 3; i++) {
         for (int j = 0; j < 3; j++) {
            output.matrix[i][j] = matrix[i][j] * scalar;
         }
      }
      
      return output;
   }
   
   public Vector multiply(Vector v) {
      double[] output = new double[3];
      
      for (int i = 0; i < 3; i++) {
         output[i] = v.getXD() * matrix[i][0];
         output[i] += v.getYD() * matrix[i][1];
         output[i] += v.getZD() * matrix[i][2];
      }
      
      return new Vector(output[0], output[1], output[2]);
   }
   
   public Matrix3x3 multiply(Matrix3x3 m) {
      Matrix3x3 output = new Matrix3x3();
      for (int i = 0; i < 3; i++) {
         for (int j = 0; j < 3; j++) {
            output.matrix[i][j] = matrix[i][0] * m.matrix[0][j] + matrix[i][1] * m.matrix[1][j] + matrix[i][2] * m.matrix[2][j];
         }
      }
      
      return output;
   }
   
   public static Matrix3x3 identityMatrix() {
      return new Matrix3x3(new Vector(1, 0, 0), new Vector(0, 1, 0), new Vector(0, 0, 1));
   }
   
   public static Matrix3x3 rotateX(double angle) {
      return new Matrix3x3(new Vector(1, 0, 0), 
                          new Vector(0, Math.cos(angle * TORAD), Math.sin(angle * TORAD)),
                          new Vector(0, -Math.sin(angle * TORAD), Math.cos(angle * TORAD)));
      
   }
   
   public static Matrix3x3 rotateY(double angle) {
      return new Matrix3x3(new Vector(Math.cos(angle * TORAD), 0, Math.sin(angle * TORAD)),
                          new Vector(0, 1, 0),
                          new Vector(-Math.sin(angle * TORAD), 0, Math.cos(angle * TORAD)));
   }
   
   public static Matrix3x3 rotateZ(double angle) {
      return new Matrix3x3(new Vector(Math.cos(angle * TORAD), -Math.sin(angle * TORAD), 0),
                          new Vector(Math.sin(angle * TORAD), Math.cos(angle * TORAD), 0),
                          new Vector(0, 0, 1));
   }
}