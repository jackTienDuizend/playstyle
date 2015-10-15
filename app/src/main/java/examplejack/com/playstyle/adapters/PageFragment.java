package examplejack.com.playstyle.adapters;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.parse.ParseUser;

/**
 * Created by jack on 23-8-2015.
 */
public class PageFragment extends Fragment {
    public static final String ARG_PAGE = "ARG_PAGE";

    private int mPage;

   /*
   public static PageFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        PageFragment fragment = new PageFragment();
        fragment.setArguments(args);
        return fragment;
    }
*/


 /*   @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = null;
        switch (mPage) {
            case 1: //Pagina 0
                view = inflater.inflate(R.layout.fragment_inbox, container, false);
                this.setHasOptionsMenu(true);
                return view;

            case 2: //Pagina 1
                view = inflater.inflate(R.layout.user_grid, container, false);
                this.setHasOptionsMenu(true);
                return view;



        }

        return view;
    }
*/


}