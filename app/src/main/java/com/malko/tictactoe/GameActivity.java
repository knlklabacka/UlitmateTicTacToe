package com.malko.tictactoe;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;

public class GameActivity extends Activity {
   public static final String KEY_RESTORE = "key_restore";
   public static final String PREF_RESTORE = "pref_restore";
   private GameFragment mGameFragment;
   private MediaPlayer mMediaPlayer;
   private Handler mHandler = new Handler();

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_game);
      // Restore game here...
      mGameFragment = (GameFragment) getFragmentManager()
            .findFragmentById(R.id.fragment_game);
      boolean restore = getIntent().getBooleanExtra(KEY_RESTORE, false);
      if (restore) {
         String gameData = getPreferences(MODE_PRIVATE)
               .getString(PREF_RESTORE, null);
         if (gameData != null) {
            mGameFragment.putState(gameData);
         }
      }
      Log.d("UT3", "restore = " + restore);
   }

   @Override
   protected void onResume(){
      super.onResume();
      mMediaPlayer = MediaPlayer.create(this, R.raw.frankum_loop001e);
      mMediaPlayer.setLooping(true);
      mMediaPlayer.start();
   }

   public void startThinking(){
      View thinkView = findViewById(R.id.thinking);
      thinkView.setVisibility(View.VISIBLE);
   }

   public void stopThinking(){
      View thinkView = findViewById(R.id.thinking);
      thinkView.setVisibility(View.GONE);
   }

   public void restartGame() {
      mGameFragment.restartGame();
   }

   public void reportWinner(final Tile.Owner winner) {
      AlertDialog.Builder builder = new AlertDialog.Builder(this);
      if(mMediaPlayer != null && mMediaPlayer.isPlaying()){
         mMediaPlayer.stop();
         mMediaPlayer.reset();
         mMediaPlayer.release();
      }

      builder.setMessage(getString(R.string.declare_winner, winner));
      builder.setCancelable(false);
      builder.setPositiveButton(R.string.ok_label,
            new DialogInterface.OnClickListener() {
               @Override
               public void onClick(DialogInterface dialogInterface, int i) {
                  finish();
               }
            });
      final Dialog dialog = builder.create();
      mHandler.postDelayed(new Runnable() {
         @Override
         public void run() {
            mMediaPlayer = mMediaPlayer.create(GameActivity.this, winner == Tile.Owner.X ? R.raw.oldedgar_winner : winner == Tile.Owner.O ? R.raw.notr_loser : R.raw.department64_draw);
            mMediaPlayer.start();
            dialog.show();
         }
      }, 500);

      // Reset the board to the initial position
      mGameFragment.initGame();
   }

   @Override
   protected void onPause() {
      super.onPause();
      mHandler.removeCallbacks(null);
      mMediaPlayer.stop();
      mMediaPlayer.reset();
      mMediaPlayer.release();
      String gameData = mGameFragment.getState();
      getPreferences(MODE_PRIVATE).edit()
            .putString(PREF_RESTORE, gameData)
            .commit();
     // Log.d("UT3", "state = " + gameData);
   }
}
