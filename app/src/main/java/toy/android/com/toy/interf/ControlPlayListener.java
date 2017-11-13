package toy.android.com.toy.interf;

/**
 * Created by Android on 2017/9/20.
 */

public interface ControlPlayListener {
    void play(String url);
    void pause();
    void stop();
    void loop();
    void seekto(int progress);
    void continueplay();

}
