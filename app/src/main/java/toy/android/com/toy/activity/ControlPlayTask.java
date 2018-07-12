package toy.android.com.toy.activity;

import android.os.AsyncTask;

/**
 * Created by Android on 2017/9/20.
 */
@Deprecated
public class ControlPlayTask extends AsyncTask<String,Integer,Boolean> {
    @Override
    protected void onPreExecute() {
        super.onPreExecute();

    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onCancelled(Boolean aBoolean) {
        super.onCancelled(aBoolean);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }

    @Override
    protected Boolean doInBackground(String... params) {
        return null;
    }
}
