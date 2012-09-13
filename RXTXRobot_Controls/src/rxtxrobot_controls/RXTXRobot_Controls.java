package rxtxrobot_controls;


public class RXTXRobot_Controls
{
    public static void main(String[] args)
    {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                 (new MainWindow()).setVisible(true);
            }
            
        });
    }
}
