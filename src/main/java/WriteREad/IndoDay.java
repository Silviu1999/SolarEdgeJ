
package WriteREad;

import com.fasterxml.jackson.databind.JsonNode;
import jdk.swing.interop.SwingInterOpUtils;
import netscape.javascript.JSObject;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.image.TileObserver;
import java.beans.XMLDecoder;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.sql.Timestamp;
import java.util.Calendar;

import static WriteREad.MyMqtt.DEFAULT_TOPIC;

 class InfoDay extends Thread{
    @Override
    public void run() {
        MyMqtt app = null;


        try {
            app = new MyMqtt();
            while (true) {
                Calendar calendar = Calendar.getInstance();
                calendar.getTime();
                //calendar.add(Calendar.DAY_OF_MONTH,-1);
                Timestamp t1 = new Timestamp(calendar.getTime().getTime()); //endDate
                calendar.add(Calendar.DAY_OF_MONTH, -30);
                Timestamp t2 = new Timestamp(calendar.getTime().getTime());//startDate
                String startDate = t2.toString().substring(0, 10);
                String endDate = t1.toString().substring(0, 10);
                System.out.println(startDate + " " + endDate);

                JSONArray jsonArray = getResponse(startDate, endDate);

                for (int i = 0; i < jsonArray.length(); i++) {
                    app.runClient();
                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    JSONObject newjson = new JSONObject().put("timestamp", jsonObject.get("date")).put("valoar", jsonObject.get("value"));

                    String message = new String(newjson.toString());
                    System.out.println(message);
                    app.sendMessage(DEFAULT_TOPIC, message);

                    Thread.sleep(200);
                }
                Thread.sleep(43200000);
            }

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        catch (MqttException e) {
            throw new RuntimeException(e);


        }
    }

    public  static JSONArray  getResponse (String startDate,String endDate) {
        JSONArray jsonArray2=null;
        try {
            String api_key = "";
            String site = "";
            String url="https://monitoringapi.solaredge.com/site/"+site+"/energy?timeUnit=DAY&endDate="+endDate+"&startDate="+startDate+"&api_key="+api_key;
            URL obj;
            obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            int responseCode = con.getResponseCode();
            System.out.println("Response Code: " + responseCode);
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();
            response.append("[");
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine+'\n');
            }
            response.append("]");
            in.close();
            System.out.println(response.toString());
            JSONArray jsonArray=new JSONArray(response.toString());
            //System.out.println(jsonArray.length());

            JSONObject jsObject=jsonArray.getJSONObject(0);//tot obiectul
            jsonArray2=new JSONArray(jsObject.getJSONObject("energy").getJSONArray("values"));



        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        finally {
            return jsonArray2;
        }

    }

}
