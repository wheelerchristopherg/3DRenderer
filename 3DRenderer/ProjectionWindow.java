import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JCheckBox;
import javax.swing.SwingUtilities;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import java.awt.GridLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Color;
import java.text.DecimalFormat;

public class ProjectionWindow extends JFrame implements ActionListener, ItemListener, KeyListener
{
   private JPanel primaryPanel, lowerPanel;
   private CanvasPanel canvas;
  
   private JLabel fpsCounter;
   private JCheckBox wireframeCheck;
  
   private DecimalFormat format;
  
   private Camera cam;
  
   public ProjectionWindow() {
      super("3D Projection");
      setDefaultCloseOperation(EXIT_ON_CLOSE);
      addKeyListener(this);
      setFocusable(true);
      primaryPanel = new JPanel();
      primaryPanel.setPreferredSize(new Dimension(800, 540));
      lowerPanel = new JPanel();
      lowerPanel.setPreferredSize(new Dimension(800, 30));
      canvas = new CanvasPanel(this);
      canvas.setPreferredSize(new Dimension(800, 500));
      cam = new Camera(800, 500);
    
      //cam.setLocation(new Vector(0, 0, 1000));
      //cam.setRight(new Vector(-1, 0, 0));
      //cam.setDown(new Vector(0, 1, 0));
    
      cam.setLocation((new Vector(0, 500, 250)).scale(2));
      cam.setRight((new Vector(-1, 0, 0)));
      cam.setDown(cam.getLocation().normalize().scale(-1).cross(cam.getRight()));
    
      cam.setPerspectivePoint(1000);
    
      canvas.setCamera(cam);
    
      Surface.addLightSource(new Vector(1000, 500, 0));
      Surface.setCamera(cam);
      
      format = new DecimalFormat("##0");
      fpsCounter = new JLabel("FPS: 0");
      fpsCounter.setMinimumSize(new Dimension(50, 20));
      fpsCounter.setPreferredSize(new Dimension(50, 20));
      fpsCounter.setMaximumSize(new Dimension(50, 20));
      wireframeCheck = new JCheckBox("Wireframe");
      wireframeCheck.setMnemonic(KeyEvent.VK_W);
      wireframeCheck.addItemListener(this);
   
      lowerPanel.add(fpsCounter);
      lowerPanel.add(wireframeCheck);
   
      primaryPanel.add(canvas);
      primaryPanel.add(lowerPanel);
   
      getContentPane().add(primaryPanel);
      pack();
      this.requestFocusInWindow();
      setVisible(true);
      canvas.load();
   }
  
   public void actionPerformed(ActionEvent e) {
      if(e.getActionCommand().equals("fps timer")) {
         fpsCounter.setText("FPS: " + format.format(canvas.getFps()));
      }
   }
  
   public void itemStateChanged(ItemEvent e) {
      if (e.getSource() == wireframeCheck) {
         if (e.getStateChange() == ItemEvent.SELECTED) {
            Surface.setWireframe(true);
            canvas.setBackground(Color.WHITE);
         } 
         else {
            Surface.setWireframe(false);
            canvas.setBackground(Color.BLACK);
         }
         this.requestFocusInWindow();
      }
   }
   
   public void keyReleased(KeyEvent e) {
      if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
         System.exit(0);
      }
   }
   
   public void keyPressed(KeyEvent e) {}
   
   public void keyTyped(KeyEvent e) {}
 
   public static void main(String[] args) {
      SwingUtilities.invokeLater(
         new Runnable() {
            public void run() {
               new ProjectionWindow();
            }
         });
   }
}
