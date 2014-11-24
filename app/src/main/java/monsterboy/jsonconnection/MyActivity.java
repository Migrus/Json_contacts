package monsterboy.jsonconnection;

import android.app.Activity;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;


public class MyActivity extends ListActivity {
    private static final String DEBUG_TAG = "DEBUG_TAG";

    private RadioGroup radioGroup;
    private RadioButton sound, vibration, silent;


    private ProgressDialog pDialog;

    // JSON Node names
    private static final String TAG_CONTACTS = "studenti";
    private static final String TAG_ID = "id";
    private static final String TAG_NAME = "jmeno";
    private static final String TAG_SURNAME = "prijmeni";

    // contacts JSONArray
    JSONArray contacts = null;

    // Hashmap for ListView
    ArrayList<HashMap<String, String>> contactList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        //isConnect();
        triggerDownload("http://www.csita.cz/sklad/studenti.json");

        contactList = new ArrayList<HashMap<String, String>>();
        ListView lv = getListView();

        // Listview on item click listener
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // getting values from selected ListItem
                String name = ((TextView) view.findViewById(R.id.name))
                        .getText().toString();
                String cost = ((TextView) view.findViewById(R.id.surname))
                        .getText().toString();
                String description = ((TextView) view.findViewById(R.id.id))
                        .getText().toString();

                // Starting single contact activity
                Intent in = new Intent(getApplicationContext(),
                        SingleContactActivity.class);
                in.putExtra(TAG_NAME, name);
                in.putExtra(TAG_SURNAME, cost);
                in.putExtra(TAG_ID, description);
                startActivity(in);

            }
        });

        //on change radio group
        radioGroup = (RadioGroup) findViewById(R.id.sort);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // find which radio button is selected
                if(checkedId == R.id.radioSurname) {

                    Collections.sort(contactList, new Comparator<HashMap< String,String >>() {
                        @Override
                        public int compare(HashMap<String, String> first, HashMap<String, String> second) {
                            //return obj1.name.compareToIgnoreCase(obj2.name);
                            String firstValue = first.get(TAG_SURNAME);
                            String secondValue = second.get(TAG_SURNAME);
                            return firstValue.compareTo(secondValue);
                        }
                    });
                    ListAdapter adapter = new SimpleAdapter(
                            MyActivity.this, contactList,
                            R.layout.list_item, new String[] { TAG_NAME, TAG_SURNAME,
                            TAG_ID }, new int[] { R.id.name,
                            R.id.surname, R.id.id });

                    setListAdapter(adapter);
                }
                else if(checkedId == R.id.radioName) {

                    Collections.sort(contactList, new Comparator<HashMap< String,String >>() {
                        @Override
                        public int compare(HashMap<String, String> first, HashMap<String, String> second) {
                            //return obj1.name.compareToIgnoreCase(obj2.name);
                            String firstValue = first.get(TAG_NAME);
                            String secondValue = second.get(TAG_NAME);
                            return firstValue.compareTo(secondValue);
                        }
                    });
                    ListAdapter adapter = new SimpleAdapter(
                            MyActivity.this, contactList,
                            R.layout.list_item, new String[] { TAG_NAME, TAG_SURNAME,
                            TAG_ID }, new int[] { R.id.name,
                            R.id.surname, R.id.id });

                    setListAdapter(adapter);
                }
                else {

                    Collections.sort(contactList, new Comparator<HashMap< String,String >>() {
                        @Override
                        public int compare(HashMap<String, String> first, HashMap<String, String> second) {
                            //return obj1.name.compareToIgnoreCase(obj2.name);
                            String firstValue = first.get(TAG_ID);
                            String secondValue = second.get(TAG_ID);
                            return firstValue.compareTo(secondValue);
                        }
                    });
                    ListAdapter adapter = new SimpleAdapter(
                            MyActivity.this, contactList,
                            R.layout.list_item, new String[] { TAG_NAME, TAG_SURNAME,
                            TAG_ID }, new int[] { R.id.name,
                            R.id.surname, R.id.id });

                    setListAdapter(adapter);
                }
            }

        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean isConnect()
    {
        boolean connected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            connected = true;
            this.showToast("Připojeno");
        }
        else {
            connected = false;
            this.showToast("Nepřipojeno");
        }
        return  connected;
    }

    public void showToast(CharSequence text) {
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    // When user clicks button, calls AsyncTask.
    // Before attempting to fetch the URL, makes sure that there is a network connection.
    public void triggerDownload(String stringUrl) {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            new DownloadWebpageTask().execute(stringUrl);
        } else {
            showToast("Připojení k internetu není k dispozici!");
        }
    }

    // Uses AsyncTask to create a task away from the main UI thread. This task takes a
    // URL string and uses it to create an HttpUrlConnection. Once the connection
    // has been established, the AsyncTask downloads the contents of the webpage as
    // an InputStream. Finally, the InputStream is converted into a string, which is
    // displayed in the UI by the AsyncTask's onPostExecute method.
    private class DownloadWebpageTask extends AsyncTask<String, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(MyActivity.this);
            pDialog.setMessage("Načítám data...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(String... urls) {

            String jsonStr = null;
            // params comes from the execute() call: params[0] is the url.
            try {
                jsonStr = downloadUrl(urls[0]);
            } catch (IOException e) {
                return null;
            }

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    // Getting JSON Array node
                    contacts = jsonObj.getJSONArray(TAG_CONTACTS);

                    // looping through All Contacts
                    for (int i = 0; i < contacts.length(); i++) {
                        JSONObject c = contacts.getJSONObject(i);

                        String id = c.getString(TAG_ID);
                        String name = c.getString(TAG_NAME);
                        String email = c.getString(TAG_SURNAME);

                        // tmp hashmap for single contact
                        HashMap<String, String> contact = new HashMap<String, String>();

                        // adding each child node to HashMap key => value
                        contact.put(TAG_ID, id);
                        contact.put(TAG_NAME, name);
                        contact.put(TAG_SURNAME, email);

                        // adding contact to contact list
                        contactList.add(contact);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
            }
            return null;
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();

            /**
             * Updating parsed JSON data into ListView
             * */
            ListAdapter adapter = new SimpleAdapter(
                    MyActivity.this, contactList,
                    R.layout.list_item, new String[] { TAG_NAME, TAG_SURNAME,
                    TAG_ID }, new int[] { R.id.name,
                    R.id.surname, R.id.id });

            setListAdapter(adapter);
            //showToast(result);
        }
    }

    // Given a URL, establishes an HttpUrlConnection and retrieves
    // the web page content as a InputStream, which it returns as
    // a string.
    private String downloadUrl(String myurl) throws IOException {
        InputStream is = null;
        // Only display the first 500 characters of the retrieved
        // web page content.
        int len = 500;

        try {
            URL url = new URL(myurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            // Starts the query
            conn.connect();
            int response = conn.getResponseCode();
            Log.d(DEBUG_TAG, "The response is: " + response);
            is = conn.getInputStream();

            // Convert the InputStream into a string
            String contentAsString = readIt(is, len);
            return contentAsString;

            // Makes sure that the InputStream is closed after the app is
            // finished using it.
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    // Reads an InputStream and converts it to a String.
    public String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException {
        Reader reader = null;
        reader = new InputStreamReader(stream, "UTF-8");
        char[] buffer = new char[len];
        reader.read(buffer);
        return new String(buffer);
    }
}
