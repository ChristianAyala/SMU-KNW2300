package rxtxrobot;

import com.phidgets.InterfaceKitPhidget;
import com.phidgets.PhidgetException;

/**
 *
 * @author raikia
 */
public class USBSensor extends SerialCommunication
{
        private InterfaceKitPhidget inter;

        public USBSensor()
        {
                try
                {
                        inter = new InterfaceKitPhidget();
                }
                catch (PhidgetException ex)
                {
                        this.getErrStream().println("ERROR: Error creating InterfaceKitPhidget: " + ex.getMessage());
                }
        }

        @Override
        public boolean isConnected()
        {
                try
                {
                        return inter.isAttached();
                }
                catch (Exception e)
                {
                        return false;
                }
        }

        @Override
        public void close()
        {
                throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void connect()
        {
                connect(0);
        }

        public void connect(int serial)
        {
                this.getOutStream().print("Connecting to the USB Sensor");
                try
                {
                        if (serial == 0)
                        {
                                this.getOutStream().println("...");
                                inter.openAny();
                        }
                        else
                        {
                                this.getOutStream().println(" with serial " + serial + "...");
                                inter.open(serial);
                        }
                }
                catch (Exception e)
                {
                        this.getErrStream().println("ERROR: Opening InterfaceKitPhidget error: " + e.getMessage());
                }
                this.getOutStream().println("Waiting for sensor to be attached...");
                try
                {
                        for (int x = 0; x < 50; ++x)
                        {
                                if (inter.isAttached())
                                        break;
                                this.sleep(500);
                        }
                        if (!inter.isAttached())
                        {
                                this.getErrStream().println("ERROR: Could not find the USB sensor in time.  Check that you are using the correct serial number!");
                                System.exit(1);
                        }
                }
                catch (Exception e)
                {
                        this.getErrStream().println("ERROR: Could not check if the device is attached! " + e.getMessage());
                }
                try
                {
                        this.getOutStream().println("Sensor attached! (serial # " + inter.getSerialNumber() + ")");
                }
                catch (Exception e)
                {
                }
        }

        public AnalogPin getAnalogPin(int index)
        {
                if (!isConnected())
                {
                        this.getErrStream().println("ERROR: No USB Sensor is connected");
                        return null;
                }
                try
                {
                        if (index >= inter.getSensorCount())
                        {
                                this.getErrStream().println("ERROR: Invalid index of AnalogPin: " + index);
                                return null;
                        }
                }
                catch (Exception e)
                {
                        return null;
                }
                try
                {
                        return new AnalogPin(index, inter.getSensorRawValue(index));
                }
                catch (Exception e)
                {
                        this.getErrStream().println("ERROR: Could not get the real sensor data! " + e.getMessage());
                        return null;
                }
        }

        public DigitalPin getDigitalInputPin(int index)
        {
                if (!isConnected())
                {
                        this.getErrStream().println("ERROR: No USB Sensor is connected");
                        return null;
                }
                try
                {
                        if (index >= inter.getInputCount())
                        {
                                this.getErrStream().println("ERROR: Invalid index of DigitalInputPin: " + index);
                                return null;
                        }
                }
                catch (Exception e)
                {
                        return null;
                }
                try
                {
                        return new DigitalPin(index, inter.getInputState(index)?1:0);
                }
                catch (Exception e)
                {
                        this.getErrStream().println("ERROR: Could not get the real sensor data! " + e.getMessage());
                        return null;
                }
        }

        public DigitalPin getDigitalOutputPin(int index)
        {
                if (!isConnected())
                {
                        this.getErrStream().println("ERROR: No USB Sensor is connected");
                        return null;
                }
                try
                {
                        if (index >= inter.getOutputCount())
                        {
                                this.getErrStream().println("ERROR: Invalid index of DigitalOutputPin: " + index);
                                return null;
                        }
                }
                catch (Exception e)
                {
                        return null;
                }
                try
                {
                        return new DigitalPin(index, inter.getOutputState(index)?1:0);
                }
                catch (Exception e)
                {
                        this.getErrStream().println("ERROR: Could not get the real sensor data! " + e.getMessage());
                        return null;
                }
        }

        public void setDigitalOutputPin(int index, int value)
        {
                if (!isConnected())
                {
                        this.getErrStream().println("ERROR: No USB Sensor is connected");
                        return;
                }
                try
                {
                        if (index >= inter.getOutputCount())
                        {
                                this.getErrStream().println("ERROR: Invalid index of DigitalOutputPin: " + index);
                                return;
                        }
                }
                catch (Exception e)
                {
                        return;
                }
                try
                {
                        inter.setOutputState(index, value==1);
                }
                catch (Exception e)
                {
                        this.getErrStream().println("ERROR: Could not set the sensor data! " + e.getMessage());
                }
        }
}
