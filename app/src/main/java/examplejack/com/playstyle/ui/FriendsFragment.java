package examplejack.com.playstyle.ui;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.List;

import examplejack.com.playstyle.adapters.UserAdapter;
import examplejack.com.playstyle.utils.ParseConstants;
import examplejack.com.playstyle.R;

/**
 * Created by jack on 29-8-2015.
 */
public class FriendsFragment extends Fragment {

    public static final String TAG = FriendsFragment.class.getSimpleName();
    public static final String ARG_PAGE = "ARG_PAGE";

    protected List<ParseUser> mFriends;
    protected ParseRelation<ParseUser> mFriendsRelation;
    protected ParseUser mCurrentUser;
    protected GridView mGridView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.user_grid, container, false);

        mGridView = (GridView)view.findViewById(R.id.friendsGrid);

        TextView emptyTextView = (TextView)view.findViewById(android.R.id.empty);
        mGridView.setEmptyView(emptyTextView);

        return view;

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
                        UserAdapter adapter = new UserAdapter(getActivity(), mFriends);
                        mGridView.setAdapter(adapter);
                    }
                    else {
                        ((UserAdapter) mGridView.getAdapter()).refill(mFriends);
                    }
                } else {
                    Log.e(TAG, e.getMessage());
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage(e.getMessage())
                            .setTitle(getString(R.string.error_title))
                            .setPositiveButton(android.R.string.ok, null); //null want geen extra actie

                    AlertDialog dialog = builder.create();
                    dialog.show();
                }


            }
        });

    }
    public static FriendsFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        FriendsFragment fragment = new FriendsFragment();
        fragment.setArguments(args);
        return fragment;
    }
}
