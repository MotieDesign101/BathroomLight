package de.lukeslog.lichtimbad;

import java.io.IOException;

/**
 * Created by lukas on 30.08.14.
 */
public class Runner implements Runnable
{
    Thread d;
    int counter=0;
    boolean currentValue=false;
    Licht licht;
    boolean soft=false;

    public Runner()
    {
        licht = new Licht();
        d = new Thread(this);
        d.run();
    }

    @Override
    public void run()
    {

        while(true)
        {
            try
            {
                currentValue = getCurrentValue();
                //System.out.println(currentValue);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            if(currentValue && (!Licht.status || soft))
            {
                counter=0;
                soft=false;
                System.out.println("turnon");
                licht.turnOn();
            }
            if(!currentValue && Licht.status)
            {
                counter++;
                if(counter%8==0 && counter>0 && counter<305)
                {
                    licht.turnonSoft();
                    soft = true;
                    System.out.println("-10");
                }
                if(counter==1200)
                {
                    System.out.println("turnoff");
                    licht.turnOff();
                    counter=0;
                    soft=false;
                }
            }
            //System.out.println(currentValue);
            sleep();
        }
    }

    private void sleep()
    {
        try
        {
            Thread.sleep(250);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    private boolean getCurrentValue() throws IOException
    {
        return HomeMaticMotionDetectorParser.parseXMLFile();
    }
}
