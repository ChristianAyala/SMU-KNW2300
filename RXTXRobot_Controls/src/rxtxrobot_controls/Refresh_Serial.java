package rxtxrobot_controls;

public class Refresh_Serial implements Runnable
{
    private MainWindow mw;
    private boolean running;
    public Refresh_Serial(MainWindow w)
    {
        mw = w;
        running = false;
    }

    @Override
    public void run()
    {
        running = true;
        while (running)
        {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException ex) {
            }
            mw.updatePortList();
        }
    }
    public void stop()
    {
        running = false;
    }
}
