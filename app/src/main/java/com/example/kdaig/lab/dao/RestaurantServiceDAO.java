package com.example.kdaig.lab.dao;

import android.util.Log;

import com.example.kdaig.lab.model.ClassRestaurant;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class RestaurantServiceDAO {
    private static final String BASE_HOST ="http://192.168.1.3/api/restaurant/";

    // getall restaurant
    public List<ClassRestaurant> getAll(){
        InputStream is=null;
        String response=null;

        try {
            //http client
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpEntity httpEntity=null;
            HttpResponse httpResponse=null;

            HttpGet httpGet = new HttpGet("http://192.168.1.3/api/restaurant/getall");
            httpResponse = httpClient.execute(httpGet);
            httpEntity =httpResponse.getEntity();
            is = httpEntity.getContent();
        }
        catch (Exception ex){
            Log.d("My error",ex.toString());
        }
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is,"UTF-8"),8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line=reader.readLine()) != null){
                sb.append(line+"\n");
            }
            is.close();
            response = sb.toString();
        }catch (Exception e){
            Log.e("Buffer Error","Error:"+ e.toString());
        }

        try {
            //convert string response into json object
            JSONArray jsonArray = new JSONArray(response);

            ArrayList<ClassRestaurant> restaurantArrayList = new ArrayList<>();

            for (int i =0; i< jsonArray.length(); i++){
                JSONObject jsonItem = jsonArray.getJSONObject(i);
                ClassRestaurant classRestaurant = new ClassRestaurant();
                classRestaurant.setId(jsonItem.getString("id"));
                classRestaurant.setName(jsonItem.getString("name"));
                classRestaurant.setAddress(jsonItem.getString("address"));
                classRestaurant.setType(jsonItem.getString("type"));

                restaurantArrayList.add(classRestaurant);
            }
            return restaurantArrayList;
        } catch (Exception ex) {
            Log.e("Data","Response:" + response);
            Log.e("Json Error","Error:" + ex.toString());
            return new ArrayList<ClassRestaurant>();
        }
    }

    // get restaurant by id
    public ClassRestaurant getById (String id){
        InputStream is = null;
        String response = null;

        try {
            //http client
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpEntity httpEntity = null;
            HttpResponse httpResponse = null;

            HttpGet httpGet = new HttpGet("http://192.168.1.3/api/restaurant/getbyid/" + id);
            httpResponse = httpClient.execute(httpGet);
            httpEntity = httpResponse.getEntity();
            is = httpEntity.getContent();
        } catch (Exception ex) {
            Log.d("My error", ex.toString());
        }

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();
            response = sb.toString();
        } catch (Exception e) {
            Log.e("Buffer Error", "Error:" + e.toString());
        }

        try {
            //convert string response into json object
            JSONObject jsonObject = new JSONObject(response);

            ClassRestaurant classRestaurant = new ClassRestaurant();
            classRestaurant.setId(jsonObject.getString("id"));
            classRestaurant.setName(jsonObject.getString("name"));
            classRestaurant.setAddress(jsonObject.getString("address"));
            classRestaurant.setType(jsonObject.getString("type"));

            return classRestaurant;
        }
        catch (Exception ex) {
            Log.e("Data", "Response:" + response);
            Log.e("Json Error", "Error:" + ex.toString());
            return new ClassRestaurant();
        }
    }

    // create restaurant
    public  void create(ClassRestaurant classRestaurant)throws IOException, JSONException {
        JSONObject jsonParam = new JSONObject();
        jsonParam.put("id",classRestaurant.getId());
        jsonParam.put("name",classRestaurant.getName());
        jsonParam.put("address",classRestaurant.getAddress());
        jsonParam.put("type",classRestaurant.getType());

        String jsonString =jsonParam.toString();

        try {
            //http client
            DefaultHttpClient httpClient =new DefaultHttpClient();
            HttpEntity httpEntity=null;
            HttpResponse httpResponse=null;

            //kiem tra lai method la post hat get
            HttpPost httpPost = new HttpPost("http://192.168.1.3/api/restaurant/create");
            //cau hinh header cho phep gui du lieu json
            httpPost.setHeader("Accept","application/json");
            httpPost.setHeader("Content-type","application/json");

            //dnh kem data vao requestBody
            httpPost.setEntity(new StringEntity(jsonString));

            //thuc thu goi dich vu rest
            httpResponse = httpClient.execute(httpPost);
        }
        catch (Exception ex){
            Log.e("Buffer Error","Error:"+ex.toString());
        }
    }

    // update restaurant
    public void update(ClassRestaurant classRestaurant) throws  IOException, JSONException{
        JSONObject jsonParam = new JSONObject();
        jsonParam.put("id",classRestaurant.getId());
        jsonParam.put("name",classRestaurant.getName());
        jsonParam.put("address",classRestaurant.getAddress());
        jsonParam.put("type",classRestaurant.getType());

        String jsonString = jsonParam.toString();
        try {
            //1.create http client
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpEntity httpEntity = null;
            HttpResponse httpResponse = null;

            //ktra loai metod la put
            HttpPut httpPut = new HttpPut("http://192.168.1.3/api/restaurant/update/"+classRestaurant.getId());

            httpPut.setHeader("Accept","application/json");
            httpPut.setHeader("Content-type","application/json");

            httpPut.setEntity(new StringEntity(jsonString));

            httpResponse = httpClient.execute(httpPut);
        }
        catch (Exception ex){
            Log.e("Buffer Error","Error:"+ex.getLocalizedMessage());
            Log.e("Info","Data:"+jsonString);
            Log.e("Info","URL:"+BASE_HOST+"update/"+classRestaurant.getId());
        }
    }
    // delete restaurant
    public boolean delete(String id){
        InputStream is = null;
        String response = null;

        try {
            //http client
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpEntity httpEntity = null;
            HttpResponse httpResponse = null;

            HttpDelete httpDelete = new HttpDelete("http://192.168.1.3/api/restaurant/delete/"+id);
            httpResponse = httpClient.execute(httpDelete);
            httpEntity = httpResponse.getEntity();
            is = httpEntity.getContent();
            return  true;
        }
        catch (Exception ex){
            Log.d("My error",ex.toString());
            return  false;
        }
    }
}
