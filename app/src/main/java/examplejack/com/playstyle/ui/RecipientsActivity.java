package examplejack.com.playstyle.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

import examplejack.com.playstyle.adapters.UserAdapter;
import examplejack.com.playstyle.utils.FileHelper;
import examplejack.com.playstyle.utils.ParseConstants;
import examplejack.com.playstyle.R;

public class RecipientsActivity extends Activity {

    public static final String TAG = RecipientsActivity.class.getSimpleName();
    //public static final String ARG_PAGE = "ARG_PAGE";

    protected List<ParseUser> mFriends;
    protected ParseRelation<ParseUser> mFriendsRelation;
    protected ParseUser mCurrentUser;
    protected MenuItem mSendMenuItem; //We willen een menuitem kunnen benaderen
    protected Uri mMediaUri;
    protected String mFileType;
    protected GridView mGridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_grid);

        mGridView = (GridView)findViewById(R.id.friendsGrid);
        mGridView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        mGridView.setOnItemClickListener(mOnItemClickListener);

        TextView emptyTextView = (TextView)findViewById(android.R.id.empty);
        mGridView.setEmptyView(emptyTextView);

        //In de oncreate wordt de intent data meegegeven (Uri)
        mMediaUri = getIntent().getData();
        mFileType = getIntent().getExtras().getString(ParseConstants.KEY_FILE_TYPE);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_recipients, menu);
        mSendMenuItem = menu.getItem(0); //We hebben maar 1 item dus positie 0

        return true;
    }
    @Override //Wordt blijkbaar iedere keer ververst als pagina getoond wordt.
    public void onResume() {
        super.onResume();

        mCurrentUser = ParseUser.getCurrentUser();
        mFriendsRelation = mCurrentUser.getRelation(ParseConstants.KEY_FRIENDS_RELATION);
        ParseQuery<ParseUser> query = mFriendsRelation.getQuery();
        query.addAscendingOrder(ParseConstants.KEY_USERNAME);
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> friends, ParseException e) {

                if (e == null) {

                    mFriends = friends;

                    //Adapt users to simple list (we hebben een adapter nodig
                    String[] usernames = new String[mFriends.size()];
                    int i = 0;
                    for (ParseUser user : mFriends) {
                        usernames[i] = user.getUsername();
                        i++;
                    }
                    if (mGridView.getAdapter() == null) {
                        UserAdapter adapter = new UserAdapter(RecipientsActivity.this, mFriends);
                        mGridView.setAdapter(adapter);
                    }
                    else {
                        ((UserAdapter) mGridView.getAdapter()).refill(mFriends);
                    }
                } else {
                    Log.e(TAG, e.getMessage());
                    AlertDialog.Builder builder = new AlertDialog.Builder(RecipientsActivity.this);
                    builder.setMessage(e.getMessage())
                            .setTitle(getString(R.string.error_title))
                            .setPositiveButton(android.R.string.ok, null); //null want geen extra actie

                    AlertDialog dialog = builder.create();
                    dialog.show();
                }


            }
        });

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
        if (id == R.id.action_send){
            ParseObject message = createMessage();

            if (message == null) {
                //There was an error
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(R.string.error_selecting_file)
                        .setTitle(R.string.error_selecting_file_title)
                        .setPositiveButton(android.R.string.ok,null);

                AlertDialog dialog = builder.create();
                dialog.show();
            }
            else {
                send(message);
                finish(); //Ga terug naar bovenliggende activiteit in de stack
            }

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected ParseObject createMessage() {
        ParseObject message = new ParseObject(ParseConstants.CLASS_MESSAGES);
        message.put(ParseConstants.KEY_SENDER_ID,ParseUser.getCurrentUser().getObjectId());
        message.put(ParseConstants.KEY_SENDER_NAME,ParseUser.getCurrentUser().getUsername());
        message.put(ParseConstants.KEY_REDIPIENT_IDS,getRecipientsIds());
        message.put(ParseConstants.KEY_FILE_TYPE,mFileType);

        //Parse heeft een byte array nodig dat we moeten maken
        byte[] fileBytes = FileHelper.getByteArrayFromFile(this, mMediaUri);

        if (fileBytes == null) {
            return null;
        }
        else {
            if (mFileType.equals(ParseConstants.TYPE_IMAGE)) {
                fileBytes = FileHelper.reduceImageForUpload(fileBytes);
            }

            String fileName = FileHelper.getFileName(this,mMediaUri,mFileType);
            ParseFile file = new ParseFile(fileName,fileBytes);
            message.put(ParseConstants.KEY_FILE,file);
            return message;
        }
    }

    protected ArrayList<String> getRecipientsIds(){
        ArrayList<String> recipientIds = new ArrayList<String>(); //Beginnen met lege lijst
        for (int i = 0; i < mGridView.getCount(); i++){
            if (mGridView.isItemChecked(i)){
                recipientIds.add(mFriends.get(i).getObjectId());
            }
        }
        return recipientIds;
    }



    protected  void send(ParseObject message) {
        message.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    //Succes
                    Toast.makeText(RecipientsActivity.this, R.string.succes_message, Toast.LENGTH_LONG).show();
                }
                else {
                    //There was an error
                    AlertDialog.Builder builder = new AlertDialog.Builder(RecipientsActivity.this);
                    builder.setMessage(R.string.error_sending_message)
                            .setTitle(R.string.error_selecting_file_title)
                            .setPositiveButton(android.R.string.ok,null);

                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });

    }

    protected AdapterView.OnItemClickListener mOnItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
            if (mGridView.getCheckedItemCount()> 0) {
                mSendMenuItem.setVisible(true);
            }
            else {
                mSendMenuItem.setVisible(false);
            }

            ImageView checkImageView = (ImageView)view.findViewById(R.id.checkImageView);

            if (mGridView.isItemChecked(position)){
                //add recipient
                checkImageView.setVisibility(View.VISIBLE);

            }
            else {
                //remove recipient
                checkImageView.setVisibility(View.INVISIBLE);
            }
        }
    };
}













