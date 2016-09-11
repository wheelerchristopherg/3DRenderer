public class Camera
{
   private Vector down;
   private Vector right;
   private Vector normal;
   private Vector location;
   private double perspectivePoint;
   private int width;
   private int height;
   private Vector screenOrigin;
   
   public Camera(int widthIn, int heightIn) {
      width = widthIn;
      height = heightIn;
      perspectivePoint = 500;
      down = new Vector(0, 0, -1);
      right = new Vector(-1, 0, 0);
      normal = right.cross(down);
      location = new Vector(0, 250, 0);
      screenOrigin = new Vector(width / 2, height / 2);
      
   }
   
   public void setPerspectivePoint(int distance) {
      perspectivePoint = distance;
   }
   
   public void setLocation(Vector locationIn) {
      location = locationIn;
   }
   
   public void setDown(Vector downIn) {
      down = downIn;
   }
   
   public void setRight(Vector rightIn) {
      right = rightIn;
   }
   
   public Vector traceToCamera(Vector pointIn) {
      double x = (pointIn.getXD() * perspectivePoint) / (perspectivePoint + pointIn.getZD());
      double y = (pointIn.getYD() * perspectivePoint) / (perspectivePoint + pointIn.getZD());
      
      return new Vector(x, y, pointIn.getZD());
   }
   
   public Vector getNormal() {
      normal = right.cross(down);
      return normal;
   }
   
   public Vector getDown() {
      return down;
   }
   
   public Vector getRight() {
      return right;
   }
   
   public Vector originOffset(Vector pointIn) {
      return pointIn.add(screenOrigin);
   }
   
   public Vector getLocation() {
      return location;
   }
   
   public int getWidth() {
      return width;
   }
   
   public int getHeight() {
      return height;
   }
}