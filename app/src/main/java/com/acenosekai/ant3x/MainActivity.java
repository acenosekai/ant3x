package com.acenosekai.ant3x;


import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.LayoutInflaterCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Toast;

import com.acenosekai.ant3x.factory.BaseFragment;
import com.acenosekai.ant3x.factory.MediaReader;
import com.acenosekai.ant3x.factory.MusicScanner;
import com.acenosekai.ant3x.fragment.LibraryFragment;
import com.acenosekai.ant3x.fragment.OpeningFragment;
import com.acenosekai.ant3x.model.Song;
import com.mikepenz.iconics.context.IconicsContextWrapper;
import com.mikepenz.iconics.context.IconicsLayoutInflater;

import java.io.File;
import java.util.Date;

import io.realm.Realm;

public class MainActivity extends AppCompatActivity {
    private FragmentManager fm;

    public void changePage(BaseFragment fragment) {
        if (fragment != null) {
            FragmentTransaction transaction = fm.beginTransaction();

            transaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            transaction.replace(R.id.base_container, fragment);
            transaction.commitAllowingStateLoss();
        }
    }

    public void backTo(BaseFragment fragment) {
        this.backBehavior = () -> changePage(fragment);
    }

    public void t(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(IconicsContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LayoutInflaterCompat.setFactory(getLayoutInflater(), new IconicsLayoutInflater(getDelegate()));
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
        setContentView(R.layout.activity_main);
        this.fm = getSupportFragmentManager();
        long startApp = System.currentTimeMillis();
        changePage(new OpeningFragment());

        //load song

        final Date scanTime = new Date();
//        if(!Playback.getInstance().isPlaying()) {
        new MusicScanner(new MusicScanner.OnScan() {
            @Override
            public void fileAdded(File file) {
                final Realm[] realm = {Realm.getInstance(App.realmConfig())};
                realm[0].executeTransaction(r -> {
                    Song s = r.where(Song.class).equalTo("directory", file.getPath()).findFirst();
                    if (s == null) {
                        Song song = r.createObject(Song.class, file.getPath());
                        song.setLastWatch(scanTime);
                    } else {
                        s.setLastWatch(scanTime);
                    }
                });
                realm[0].close();
                new MediaReader(file).initialize();

            }

            @Override
            public void complete() {
                Realm realm = Realm.getInstance(App.realmConfig());
                realm.executeTransaction(r -> {
                    r.where(Song.class).notEqualTo("lastWatch", scanTime).findAll().deleteAllFromRealm();

                });
                realm.close();
                changePage(new LibraryFragment());
                App.i("time elapsed = " + (System.currentTimeMillis() - startApp));
            }

            @Override
            public void error(Exception e) {

            }
        }).scan(Environment.getExternalStorageDirectory());
//        }else{
//            changePage(new LibraryFragment());
//        }
    }

    public void commandService(String com) {
        Intent startIntent = new Intent(this, ForegroundService.class);
        startIntent.setAction(com);
        startService(startIntent);
    }

    private Runnable backBehavior;

    public void setBackBehavior(Runnable backBehavior) {
        this.backBehavior = backBehavior;
    }

    @Override
    public void onBackPressed() {
        backBehavior.run();
    }


}
