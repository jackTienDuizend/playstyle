package examplejack.com.playstyle.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import examplejack.com.playstyle.adapters.MessageAdapter;
import examplejack.com.playstyle.utils.ParseConstants;
import examplejack.com.playstyle.R;

/**
 * Created by jack on 24-8-2015.
 */
public class InboxFragment extends ListFragment {

    public static final String ARG_PAGE = "ARG_PAGE";
    protected List<ParseObject> mMessages;
    protected SwipeRefreshLayout mSwipeRefreshLayout;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_inbox, container, false);

        mSwipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(mOnRefreshListener);
        mSwipeRefreshLayout.setColorSchemeColors(
                R.color.swipeRefresh1,
                R.color.swipeRefresh2,
                R.color.swipeRefresh3,
                R.color.swipeRefresh4);
        return view;

    }


    @Override
    public void onResume() {
        super.onResume();

        retrieveMessages();


    }

    private void retrieveMessages() {
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(ParseConstants.CLASS_MESSAGES);
        query.whereEqualTo(ParseConstants.KEY_REDIPIENT_IDS, ParseUser.getCurrentUser().getObjectId());
        query.addDescendingOrder(ParseConstants.KEY_CREATED_AT);

        //Uitoeren van de query


        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> messages, ParseException e) {

                if (mSwipeRefreshLayout.isRefreshing()){
                    mSwipeRefreshLayout.setRefreshing(false);
                }

                if (e == null) {
                    //We found messages
                    mMessages = messages;

                    String[] usernames = new String[mMessages.size()];
                    int i = 0;
                    for (ParseObject message : mMessages) {
                        usernames[i] = message.getString(ParseConstants.KEY_SENDER_NAME);
                        i++;
                    }
                    if (getListView().getAdapter() == null) {
                        MessageAdapter adapter = new MessageAdapter(
                                getListView().getContext(),
                                mMessages);
                        setListAdapter(adapter);
                    }
                    else {
                        //refill the adapter
                        ((MessageAdapter)getListView().getAdapter()).refill(mMessages);
                    }
                }
                else {
                    //Bummer
                }
            }
        });
    }

    public static InboxFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        InboxFragment fragment = new InboxFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        ParseObject message = mMessages.get(position);
        String messageType = message.getString(ParseConstants.KEY_FILE_TYPE);
        ParseFile file = message.getParseFile(ParseConstants.KEY_FILE);
        Uri fileUri = Uri.parse(file.getUrl()); //We hebben een uri nodig

        if (messageType.equals(ParseConstants.TYPE_IMAGE)){
            //Show the image
            Intent intent = new Intent(getActivity(),ViewImageActivity.class);
            intent.setData(fileUri);
            startActivity(intent);
        }
        else {
            //Show video
            Intent intent = new Intent(Intent.ACTION_VIEW,fileUri);
            intent.setDataAndType(fileUri,"video/*");
            startActivity(intent);

        }

        //Hier hebben ze het bericht al gezien
        //Nu gaan we verwijderen.
        List<String> ids = message.getList(ParseConstants.KEY_REDIPIENT_IDS);

        if (ids.size() == 1){
            //Last recipient we can deletethe message
            message.deleteInBackground(); //We gaan er voor het gemak van uit dat het deleten goed gaat

        }
        else {
            //We delete the reciipient and save
            ids.remove(ParseUser.getCurrentUser().getObjectId()); //Lokaal

            ArrayList<String> idsToRemove = new ArrayList<String>();
            idsToRemove.add(ParseUser.getCurrentUser().getObjectId());

            message.removeAll(ParseConstants.KEY_REDIPIENT_IDS,idsToRemove);
            message.saveInBackground();
        }

    }

    protected SwipeRefreshLayout.OnRefreshListener mOnRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            retrieveMessages();
        }
    };
}
