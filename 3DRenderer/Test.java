public class Test
{
    public static void main(String[] args) {
        Matrix3x3 m1 = new Matrix3x3(new Vector(17, 5, -9), new Vector(7, 5, 1), new Vector(-9, -4, 20));
        Matrix3x3 m2 = new Matrix3x3(new Vector(5, -7, 6), new Vector(15, -1, -9), new Vector(4, 6, 13));
        
        System.out.println(m1);
        System.out.println(m2);
        System.out.println(m1.multiply(m2));
        System.out.println(m2.multiply(m1));
        System.out.println(m1.multiply(m2).invert());
        System.out.println(m2.multiply(m1).invert());
    }
}