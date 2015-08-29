package examplejack.com.playstyle;

import android.app.Application;

import com.parse.Parse;

/**
 * Created by jack on 16-8-2015.
 * Dir is het entry poin van de app omdat we Appplication extenden
 */
public class RibbitApplication extends Application {
    @Override
    public void onCreate(){
        super.onCreate();
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "7KU2GkMiSvHpCD3d7vDLeRyHGEjE0rJp8EOMNW5E", "1ub2SSSFD8X9jkpjLyg62H223h82hMPNMaipOQ6R");
/*
        ParseObject testObject = new ParseObject("TestObject");
        testObject.put("foo", "bar");
        testObject.saveInBackground();
*/
    }

}
