package WriteREad;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Timestamp;
import java.util.Calendar;

import static WriteREad.MyMqtt.DEFAULT_TOPIC;

public class Infoquarter extends Thread{
    @Override
    public void run()  {


        try {
            MyMqtt app = new MyMqtt();
            Calendar calendar = Calendar.getInstance();
            calendar.set(2022,7,27,0,0);
            while (true) {

                Calendar c=Calendar.getInstance();
                c.getTime();
                if(calendar.before(c)) {
                    Timestamp t1 = new Timestamp(calendar.getTime().getTime()); //endDate
                    calendar.add(Calendar.DAY_OF_MONTH, -4);
                    Timestamp t2 = new Timestamp(calendar.getTime().getTime());//startdate
                    System.out.println("End Date:"+t1.toString());
                    JSONArray jsonArray = getResponse(t2.toString(), t1.toString(),"ora");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        app.runClient();
                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                        JSONObject newjson = new JSONObject().put("timestamp", jsonObject.get("date")).put("energiaq", jsonObject.get("value"));

                        String message = new String(newjson.toString());
                        System.out.println(message);
                        app.sendMessage("training/Silviu/SolarEdge/day", message);

                        Thread.sleep(200);
                    }
                    calendar.add(Calendar.DAY_OF_MONTH,8);



                }else {
                    Timestamp t1 = new Timestamp(c.getTime().getTime()); //endDate
                    c.add(c.DAY_OF_MONTH, -1);
                    Timestamp t2 = new Timestamp(c.getTime().getTime());//startDate
                    System.out.println("End date:"+t1.toString()+" Start date:"+t2.toString());
                    JSONArray jsonArray = getResponse(t2.toString(), t1.toString(), "ora");

                    for (int i = 0; i < jsonArray.length(); i++) {
                        app.runClient();

                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                        JSONObject newjson = new JSONObject().put("timestamp", jsonObject.get("date")).put("energiaq", jsonObject.get("value"));

                        String message = new String(newjson.toString());
                        System.out.println(message);
                        app.sendMessage("training/Silviu/SolarEdge/day", message);

                        Thread.sleep(200);
                    }
                    Thread.sleep(900000);
                }





            }

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        catch (IOException | MqttException e) {
            throw new RuntimeException(e);
        } finally {
            //app.stopClient();
        }


    }

    public static JSONArray getResponse(String startDate, String endDate,String time) throws IOException {
        JSONArray jsonArray2 = null;
        String api_key = "";
        String site = "";
        String url;
        if(time.equals("day")){

            url = "https://monitoringapi.solaredge.com/site/"+site+"/energy?timeUnit=DAY&endDate=" + endDate + "&startDate=" + startDate + "&api_key="+api_key;}
        else {

            url="https://monitoringapi.solaredge.com/site/"+site+"/power?startTime="+startDate.substring(0,10)+"%20"+startDate.substring(11,19)+"&endTime="+endDate.substring(0,10)+"%20"+endDate.substring(11,19)+"&api_key="+api_key";
        }
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
            response.append(inputLine + '\n');
        }
        response.append("]");
        in.close();
        System.out.println(response.toString());
        JSONArray jsonArray = new JSONArray(response.toString());

        JSONObject jsObject = jsonArray.getJSONObject(0);//tot obiectul
        jsonArray2 = new JSONArray(jsObject.getJSONObject("power").getJSONArray("values"));


        return jsonArray2;
    }

}

