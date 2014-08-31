package de.lukeslog.lichtimbad;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * Created by lukas on 30.08.14.
 */
public class HomeMaticMotionDetectorParser
{
    //get the values for the coffe machine
    //submit them to the records class
    public static boolean parseXMLFile() throws IOException
    {
        return readValueFromURL("http://192.168.1.107/addons/xmlapi/statelist.cgi");
    }

    private static boolean readValueFromURL(String url) throws IOException {
        URL oracle = new URL(url);
        BufferedReader in = new BufferedReader(
                new InputStreamReader(oracle.openStream()));

        String inputLine;
        String filetext="";
        while ((inputLine = in.readLine()) != null) {
            //System.out.println(inputLine);
            //System.out.println();
            filetext=filetext+"\n"+inputLine;
        }
        filetext=filetext.replace(">", ">\n");
        filetext=filetext.replace("/>", "/>\n");
        //System.out.println(filetext);
        String[] lines = filetext.split("\n");
        boolean result=false;
        for(String line : lines)
        {
            //System.out.println(line);

            if(line.contains("BidCos-RF.KEQ0972591:1.MOTION"))
            {
                //System.out.println(line);
                String[] vv = line.split("'");
                String oldv="";
                for(String v : vv)
                {
                    if(oldv.equals(" value="))
                    {
                        if(v.equals("false"))
                        {
                            result=false;
                        }
                        else
                        {
                            result=true;
                        }
                    }
                    oldv=v;
                }
            }

        }
        in.close();
        //System.out.println(result);
        return result;
    }

}
