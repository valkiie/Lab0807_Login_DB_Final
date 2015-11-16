package com.example.richard.lab0807_login_db_final;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    private ProgressDialog pDialog;
    JSONParser jParser = new JSONParser();
    ArrayList<HashMap<String, String>> empresaList;
    private static String url_all_empresas =
            "http://valkie.pe.hu/laboratorio8/get_users.php";
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_PRODUCTS = "TBL_USER";
    private static final String TAG_ID = "id";
    private static final String TAG_NOMBRE = "name";
    private static final String TAG_DESCRIPCION = "password";
    JSONArray products = null;
    ListView lista;


    class LoadAllProducts extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            pDialog = new ProgressDialog(MainActivity.this);
//            pDialog.setMessage("Cargando comercios. Por favor espere...");
//            pDialog.setIndeterminate(false);
//            pDialog.setCancelable(false);
//            pDialog.show();
        }
        protected String doInBackground(String... args) {
            List params = new ArrayList();
            JSONObject json =
                    jParser.makeHttpRequest(url_all_empresas, "GET", params);
            Log.d("All Products: ", json.toString());
            try {
                int success = json.getInt(TAG_SUCCESS);
                if (success == 1) {
                    products=json.getJSONArray(TAG_PRODUCTS);
                    for (int i = 0; i < products.length(); i++) {
                        JSONObject c = products.getJSONObject(i);
                        String id = c.getString(TAG_ID);
                        String name = c.getString(TAG_NOMBRE);
                        String descripcion = c.getString(TAG_DESCRIPCION);

                        HashMap map = new HashMap();
                        map.put(TAG_ID, id);
                        map.put(TAG_NOMBRE, name);
                        map.put(TAG_DESCRIPCION, descripcion);
                        empresaList.add(map);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public void login(View view) throws JSONException {
        EditText name = (EditText) findViewById(R.id.editText);
        EditText password = (EditText) findViewById(R.id.editText2);
        Context context = getApplicationContext();
        String encryptedPass="";
        int duration = Toast.LENGTH_SHORT;
//        Log.e("LOG", name.getText().toString());
//        Log.e("LOG", password.getText().toString());
//        Log.e("LOG", empresaList.get(0).toString());
//        Log.e("LOG", empresaList.get(0).get("name"));
        try{
            encryptedPass=(SHA1(password.getText().toString()));
            Log.e("LOG", encryptedPass);
        } catch (Exception e) {
            e.printStackTrace();
        }
        for(int i=0;i<empresaList.size();i++){
            if(name.getText().toString().equals(empresaList.get(i).get("name"))){
                if(encryptedPass.equals(empresaList.get(i).get("password"))){
                    CharSequence text = "Bienvenido " + name.getText().toString();
                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }
                else{
                    CharSequence text = "Contraseña incorrecta ";
                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }
            }
        }


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        empresaList = new ArrayList<HashMap<String, String>>();
        new LoadAllProducts().execute();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
//    public static byte[] SHA1(String x) throws Exception {
//        java.security.MessageDigest d = null;
//        d = java.security.MessageDigest.getInstance("SHA-1");
//        d.reset();
//        d.update(x.getBytes("UTF-8"));
//        return d.digest();
//    }
        private String convertToHex(byte[] data) {
            StringBuilder buf = new StringBuilder();
            for (byte b : data) {
                int halfbyte = (b >>> 4) & 0x0F;
                int two_halfs = 0;
                do {
                    buf.append((0 <= halfbyte) && (halfbyte <= 9) ? (char) ('0' + halfbyte) : (char) ('a' + (halfbyte - 10)));
                    halfbyte = b & 0x0F;
                } while (two_halfs++ < 1);
            }
            return buf.toString();
        }

        public String SHA1(String text) throws NoSuchAlgorithmException, UnsupportedEncodingException {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            md.update(text.getBytes("iso-8859-1"), 0, text.length());
            byte[] sha1hash = md.digest();
            return convertToHex(sha1hash);
        }

}
