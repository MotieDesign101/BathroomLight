package de.lukeslog.lichtimbad;

import de.jaetzold.philips.hue.HueBridge;
import de.jaetzold.philips.hue.HueLightBulb;

import java.util.Collection;
import java.util.List;

/**
 * Created by lukas on 30.08.14.
 */
public class Licht
{
    public static String BRIDGEUSERNAME = "552627b33010930f275b72ab1c7be258";
    List<HueBridge> bridges;
    Collection<HueLightBulb> lights=null;
    HueLightBulb bathroomlight = null;
    public static boolean status=false;

    public Licht()
    {
        connectToHueLights();
    }

    private void connectToHueLights()
    {
        new Thread(new Runnable()
        {
            @SuppressWarnings("unchecked")
            public void run()
            {
                bridges = HueBridge.discover();
                for(HueBridge bridge : bridges)
                {
                    bridge.setUsername(BRIDGEUSERNAME);
                    if(bridge.authenticate(true))
                    {
                        System.out.println("Access granted. username: " + bridge.getUsername());
                        lights = (Collection<HueLightBulb>) bridge.getLights();
                        System.out.println("Available LightBulbs: " + lights.size());
                        for (HueLightBulb bulb : lights)
                        {
                            System.out.println(bulb.toString());
                            if(bulb.getName().equals("Bathroom"))
                            {
                                bathroomlight=bulb;
                            }
                        }
                        System.out.println("");
                    }
                    else
                    {
                        System.out.println("Authentication failed.");
                    }
                }
            }
        }).start();
    }

    public void turnOn()
    {
        status=true;
        lightsetTo(true, 255, 0);
    }

    public void turnonSoft()
    {
        System.out.println("->");
        try
        {
            if (bathroomlight.getBrightness() > 20)
            {
                System.out.println("dimming....");
                bathroomlight.setBrightness(bathroomlight.getBrightness() - 15);
            }
            else
            {
                System.out.println("turnoff via turnofslow....");
                bathroomlight.setOn(false);
            }
        }
        catch(Exception e)
        {
            System.out.println("turning the light down slowly no longer an option for some reason.");
        }
    }

    public void turnOff()
    {
        status=false;
        lightsetTo(false, 0, 0);
    }

    private void lightsetTo(final boolean b, final int n, final int f)
    {
        new Thread(new Runnable()
        {
            @SuppressWarnings("unchecked")
            public void run()
            {
                if(lights!=null)
                {
                    if (b)
                    {
                        if (bathroomlight != null)
                        {
                            setHueColor(bathroomlight, n, n, n);
                        } else
                        {
                            for (HueLightBulb bulb : lights)
                            {
                                if (bulb.getName().equals("Bathroom"))
                                {
                                    try
                                    {
                                        System.out.println(bulb.toString());
                                        setHueColor(bulb, n, n, n);
                                    } catch (Exception e)
                                    {
                                        System.out.println(e.getMessage());
                                    }
                                }
                            }
                        }
                    }
                    else
                    {
                        if (bathroomlight != null)
                        {
                            bathroomlight.setOn(b);
                        } else
                        {
                            for (HueLightBulb bulb : lights)
                            {
                                if (bulb.getName().equals("Bathroom"))
                                {
                                    try
                                    {
                                        System.out.println(bulb.toString());
                                        bulb.setOn(b);
                                    }
                                    catch (Exception e)
                                    {
                                        System.out.println(e.getMessage());
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }).start();
    }

    public static void setHueColor(final HueLightBulb bulb, double r, double g, double b)
    {
        //method from http://www.everyhue.com/vanilla/discussion/166/hue-rgb-to-hsv-algorithm/p1
        //r = (float(rInt) / 255)
        r=r/255.0;
        //g = (float(gInt) / 255)
        g=g/255.0;
        //b = (float(bInt) / 255)
        b=b/255.0;

        if (r > 0.04045)
        {
            r = Math.pow(((r + 0.055) / 1.055), 2.4);
        }
        else
        {
            r = r / 12.92;
        }
        if (g > 0.04045)
        {
            g = Math.pow(((g + 0.055) / 1.055), 2.4);
        }
        else
        {
            g = g / 12.92;
        }
        if (b > 0.04045)
        {
            b = Math.pow(((b + 0.055) / 1.055), 2.4);
        }
        else
        {
            b = b / 12.92;
        }

        r = r * 100;
        g = g * 100;
        b = b * 100;

        //Observer = 2deg, Illuminant = D65
        //These are tristimulus values
        //X from 0 to 95.047
        //Y from 0 to 100.000
        //Z from 0 to 108.883
        double X = r * 0.4124 + g * 0.3576 + b * 0.1805;
        double Y = r * 0.2126 + g * 0.7152 + b * 0.0722;
        double Z = r * 0.0193 + g * 0.1192 + b * 0.9505;

        //Compute xyY
        double sum = X + Y + Z;
        double chroma_x = 0;
        double chroma_y = 0;
        if (sum > 0)
        {
            chroma_x = X / (X + Y + Z); //x
            chroma_y = Y / (X + Y + Z); //y
        }
        final double ch_x =chroma_x;
        final double ch_y = chroma_y;
        //int brightness = (int)(Math.floor(Y / 100 *254)); //luminosity, Y
        new Thread(new Runnable()
        {
            public void run()
            {
                try
                {

                    //Log.d(TAG, "1");
                    bulb.setOn(true);
                    //Log.d(TAG, "12");
                    //bulb.setBrightness(0);
                    //Log.d(TAG, "3");
                    bulb.setCieXY(ch_x , ch_y);
                    //Log.d(TAG, "4");
                    bulb.setBrightness(255);
                }
                catch(Exception e)
                {
                    System.out.println("there was an error when setting the lightbulb");
                }
            }
        }).start();
    }
}
