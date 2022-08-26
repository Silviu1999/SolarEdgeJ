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

public class Power extends Thread{
    @Override
    public void run() {

        try {
            Calendar calendar = Calendar.getInstance();
            calendar.set(2021, 9, 21, 0, 0);
            while (true) {

                Calendar c = Calendar.getInstance();
                c.getTime();
                if (calendar.before(c)) {
                    Timestamp t1 = new Timestamp(calendar.getTime().getTime()); //endDate
                    calendar.add(Calendar.DAY_OF_MONTH, -1);
                    Timestamp t2 = new Timestamp(calendar.getTime().getTime());//startdate
                    System.out.println("End Date:" + t1.toString());
                    getResponse(t2.toString(), t1.toString());

                    calendar.add(Calendar.DAY_OF_MONTH, 2);


                } else {
                    Timestamp t1 = new Timestamp(c.getTime().getTime()); //endDate
                    c.add(c.DAY_OF_MONTH, -1);
                    Timestamp t2 = new Timestamp(c.getTime().getTime());//startDate
                    getResponse(t2.toString(), t1.toString());


                    Thread.sleep(900000);
                }


            }
        } catch (MqttException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }




    public static void getResponse(String startDate, String endDate ) throws IOException, InterruptedException, MqttException {
        MyMqtt app = new MyMqtt();
        JSONArray jsonArray2 = null;
        String api_key = "C2HLR38KUMXL6HOY3VE0F2BNGZSEF1XF";
        String site = "1745524";
        String url;
        url="https://monitoringapi.solaredge.com/site/1745524/powerDetails?startTime="+startDate.substring(0,10)+"%20"+startDate.substring(11,19)+"&endTime="+endDate.substring(0,10)+"%20"+endDate.substring(11,19)+"&api_key=C2HLR38KUMXL6HOY3VE0F2BNGZSEF1XF";
        System.out.println("URL:"+url);
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
        System.out.println("-----------------------");
        System.out.println(jsObject.getJSONObject("powerDetails").getJSONArray("meters").length());
        for (int i=0;i<jsObject.getJSONObject("powerDetails").getJSONArray("meters").length();i++) {
            System.out.println(jsObject.getJSONObject("powerDetails").getJSONArray("meters").getJSONObject(i).get("type"));
            jsonArray2=new JSONArray(jsObject.getJSONObject("powerDetails").getJSONArray("meters")).getJSONObject(i).getJSONArray("values");
            sendMqtt(jsonArray2,jsObject.getJSONObject("powerDetails").getJSONArray("meters").getJSONObject(i).get("type").toString());
        }
        System.out.println("-----------------------");

        System.out.println("-----------------------------------------------------------");
        System.out.println(jsonArray2);



    }

    public static  void sendMqtt(JSONArray jsonArray2,String meters) throws MqttException, InterruptedException {
        MyMqtt app = new MyMqtt();
        for (int i = 0; i < jsonArray2.length(); i++) {
            app.runClient();

            JSONObject jsonObject = jsonArray2.getJSONObject(i);
            JSONObject newjson=null;

            try {
                newjson = new JSONObject().put("timestamp", jsonObject.get("date")).put(meters, jsonObject.get("value"));
            }catch (Exception e){
                System.out.println("--------------");
                System.out.println("Nu e  value ");
                System.out.println("--------------");
                newjson = new JSONObject().put("timestamp", jsonObject.get("date")).put(meters, 0);
            }

            String message = new String(newjson.toString());
            System.out.println(message);
            app.sendMessage("training/Silviu/SolarEdge/meters", message);

            Thread.sleep(200);
        }


    }

}

