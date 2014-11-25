package monsterboy.jsonconnection;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;


public class SingleContactActivity extends Activity {

    // JSON node keys
    private static final String TAG_NAME = "jmeno";
    private static final String TAG_SURNAME = "prijmeni";
    private static final String TAG_ID = "id";
    private static final String TAG_EMAIL = "email";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_contact);

        // getting intent data
        Intent in = getIntent();

        // Get JSON values from previous intent
        String name = in.getStringExtra(TAG_NAME);
        String surname = in.getStringExtra(TAG_SURNAME);
        String id = in.getStringExtra(TAG_ID);
        String email = in.getStringExtra(TAG_EMAIL);

        // Displaying all values on the screen
        TextView lblName = (TextView) findViewById(R.id.name_label);
        TextView lblSurname = (TextView) findViewById(R.id.surname_label);
        TextView lblId = (TextView) findViewById(R.id.id_label);
        TextView lblEmail = (TextView) findViewById(R.id.email_label);

        lblName.setText(name);
        lblSurname.setText(surname);
        lblId.setText(id);
        lblEmail.setText(email);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.single_contact, menu);
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

}
