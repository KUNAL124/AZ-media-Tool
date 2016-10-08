package com.example.naruto.az_media_tool.Adapters;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.naruto.az_media_tool.DatabaseHelper.DbBitmapUtility;
import com.example.naruto.az_media_tool.Extras.Ent_item;
import com.example.naruto.az_media_tool.Main.VideoPlayer;
import com.example.naruto.az_media_tool.R;
import com.snappydb.DB;
import com.snappydb.DBFactory;
import com.snappydb.SnappydbException;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Naruto on 7/17/2016.
 */
public class Ent_List_Adapter extends RecyclerView.Adapter<Ent_List_Adapter.ViewHolderEntList> {
    private LayoutInflater layoutInflater;
    Context context;
    Activity activity;
    private static int diff;
    private ArrayList<Ent_item> Ent_list = new ArrayList<Ent_item>();
    SeekBar seek_bar;
    TextView name_audio;
    ImageButton play_button;
    ImageButton pause_button;
    MediaPlayer player;
    TextView text_shown;
    Handler seekHandler = new Handler();
    DB snappydb;
    public Ent_List_Adapter(Context context, int differ, Activity activity) {
        this.context = context;
        diff = differ;
        this.activity = activity;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public ViewHolderEntList onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        if (diff == 0) {
            view = layoutInflater.inflate(R.layout.custom_audio_list_row, parent, false);
        } else {
            view = layoutInflater.inflate(R.layout.custom_row, parent, false);
        }

        ViewHolderEntList viewHolderWeatherForcast = new ViewHolderEntList(view, new ViewHolderEntList.ViewHolderInterface() {
            @Override
            public void showDetails(View v, int position) {
                try {
                    snappydb=DBFactory.open(context);
                } catch (SnappydbException e) {
                    e.printStackTrace();
                }
                Ent_item new_item = Ent_list.get(position);
                if ((diff == 0 && new_item.getType() == 1) || (diff == 1 && new_item.getType() == 1)) {
                    File f = new_item.getFile();
                    String path=new_item.getFile().getAbsolutePath();
                    if(diff==0){
                        try {
                            ArrayList<String> current=snappydb.getObject("Audio_current_location",ArrayList.class);
                            ArrayList<String> back=snappydb.getObject("Audio_back_location",ArrayList.class);
                            back.add(0,current.get(0));
                            current.add(0,path);
                            Log.v("filepath:",""+path);
                            snappydb.put("Audio_current_location",current);
                            snappydb.put("Audio_back_location",back);
                        } catch (SnappydbException e) {
                            e.printStackTrace();
                        }
                    }
                    if(diff==1){
                        try {
                            ArrayList<String> current=snappydb.getObject("Video_current_location",ArrayList.class);
                            ArrayList<String> back=snappydb.getObject("Video_back_location",ArrayList.class);
                            back.add(0,current.get(0));
                            current.add(0,path);
                            Log.v("filepath:",""+path);
                            snappydb.put("Video_current_location",current);
                            snappydb.put("Video_back_location",back);
                        } catch (SnappydbException e) {
                            e.printStackTrace();
                        }
                    }
                    Ent_list.clear();
                    find_files(f);
                    notifyDataSetChanged();
                } else {
                    if (diff == 0) {
                        //context.startActivity(new Intent(context, AudioPlay.class).putExtra("Uri", new_item.getFile().toString()));
                        show_dialog(Uri.parse(new_item.getFile().toString()), new_item.getFilename());
                    } else {
                        context.startActivity(new Intent(context, VideoPlayer.class).putExtra("videofile",new_item.getFile().toString()).putExtra("name",new_item.getFilename().replace(".mp4","")));
                    }
                }
            }
        });
        return viewHolderWeatherForcast;

    }

    public void setEnt_list(ArrayList<Ent_item> ent_list) {
        Ent_list = ent_list;
        notifyItemRangeChanged(0, Ent_list.size());
    }




    @Override
    public void onBindViewHolder(ViewHolderEntList holder, int position) {
        Ent_item new_item = Ent_list.get(position);
        if (diff == 1 && new_item.getType() == 0) {
            holder.filename.setText(new_item.getFilename());
            holder.duration.setText(new_item.getDuration());
            Log.v("filename mp4", "" + new_item.getFilename());
            if (new_item.getBitmapThumbnail() == null) {
                holder.thumbnail.setImageResource(R.drawable.noimage);
            } else holder.thumbnail.setImageBitmap(new_item.getBitmapThumbnail());
        } else {
            if (diff == 1 && new_item.getType() == 1) {
                Log.v("filename", "" + new_item.getFilename());
                holder.filename.setText(new_item.getFilename());
                holder.duration.setText("");
                holder.thumbnail.setImageResource(R.drawable.ic_folder);
            } else {
                holder.audio_filename.setText(new_item.getFilename());
                if (new_item.getType() == 1) {
                    holder.Audio_image.setImageResource(R.drawable.ic_folder);
                } else {
                    holder.Audio_image.setImageResource(R.drawable.audio_icon);
                }
            }
        }
    }


    @Override
    public int getItemCount() {
        return Ent_list.size();
    }

static class ViewHolderEntList extends RecyclerView.ViewHolder implements View.OnClickListener {
    //define all the widgets ids
    ImageView thumbnail;
    private ViewHolderInterface myclickhandler;
    TextView filename;
    TextView duration;
    TextView audio_filename;
    ImageView Audio_image;
    public ViewHolderEntList(View itemView, ViewHolderInterface m) {
        super(itemView);
        if (diff == 1) {
            thumbnail = (ImageView) itemView.findViewById(R.id.Bit_image);
            duration = (TextView) itemView.findViewById(R.id.length);
            filename = (TextView) itemView.findViewById(R.id.filename);
        } else {
            audio_filename = (TextView) itemView.findViewById(R.id.audio_file_name);
            Audio_image = (ImageView) itemView.findViewById(R.id.Audio_image);
        }
        myclickhandler = m;
        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        myclickhandler.showDetails(v, getAdapterPosition());
    }

    interface ViewHolderInterface {
        public void showDetails(View v, int position);
    }

}

    public void find_files(File root) {
        DB snappydb = null;
        try {
            snappydb = DBFactory.open(context);
        } catch (SnappydbException e) {
            e.printStackTrace();
        }
        File f = root;
        File[] files = f.listFiles();
        for (File singleFile : files) {
            Ent_item ent_item = new Ent_item();
            if (singleFile.isDirectory()) {
                if (have_media_file(singleFile)) {
                    ent_item.setType(1);
                    ent_item.setFilename(singleFile.getName());
                    ent_item.setFile(singleFile);
                    Ent_list.add(ent_item);
                }
            } else {
                String filename = singleFile.getName();
                if (diff == 1) {
                    if (filename.endsWith(".mp4")||filename.endsWith(".mkv")||filename.endsWith(".webm")||filename.endsWith(".avi")||filename.endsWith(".3gp")) {
                        byte[] image;
                        String duration;
                        try {
                            image = snappydb.getBytes(singleFile.getName() + " image");
                            duration = snappydb.get(singleFile.getName() + " duration");
                            ent_item.setBitmapThumbnail(DbBitmapUtility.getImage(image));
                            ent_item.setDuration(duration);
                            ent_item.setFile(singleFile);
                            ent_item.setType(0);
                            ent_item.setFilepath(singleFile.getAbsolutePath());
                            ent_item.setFilename(singleFile.getName());
                            Ent_list.add(ent_item);
                        } catch (SnappydbException e) {

                        }
                    }
                } else {
                    if (filename.endsWith(".mp3")) {
                        ent_item.setFile(singleFile);
                        ent_item.setType(0);
                        ent_item.setFilepath(singleFile.getAbsolutePath());
                        ent_item.setFilename(singleFile.getName());
                        Ent_list.add(ent_item);
                    }


                }
            }
        }

    }

    private boolean have_media_file(File singleFile) {
        File f = singleFile;
        File[] files = f.listFiles();
        for (File singlefile : files) {
            if (singlefile.isDirectory()) {
                if (have_media_file(singlefile))
                    return true;
            } else {
                if (diff == 1) {
                    if (singlefile.getName().endsWith(".mp4")||singlefile.getName().endsWith(".mkv")||singlefile.getName().endsWith(".avi")||singlefile.getName().endsWith(".webm")||singlefile.getName().endsWith(".3gp")) {
                        return true;
                    }
                } else {
                    if (singlefile.getName().endsWith(".mp3")) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    void show_dialog(Uri uri, String name) {
        final boolean[] complete = {false};
        final boolean[] paused = {false};
        final Dialog settingsDialog = new Dialog(activity);
        settingsDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        settingsDialog.getWindow().setBackgroundDrawableResource(R.color.relative_trans);
        View view = ((AppCompatActivity) activity).getLayoutInflater().inflate(R.layout.ausio_play, null);
        seek_bar = (SeekBar) view.findViewById(R.id.audio_track);
        name_audio = (TextView) view.findViewById(R.id.audio_name);
        play_button = (ImageButton) view.findViewById(R.id.pause);
        play_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (paused[0] == false) {
                    player.pause();
                    paused[0] = true;
                    play_button.setImageResource(R.drawable.play);
                } else {
                    if (paused[0] == true && complete[0] != true) {
                        player.start();
                        paused[0] = false;
                        seekUpdation();
                        play_button.setImageResource(R.drawable.pause);
                    } else {
                        player.seekTo(0);
                        player.start();
                        paused[0] = false;
                        seekUpdation();
                        play_button.setImageResource(R.drawable.pause);
                    }
                }
            }
        });
        player = MediaPlayer.create(context, uri);
        seek_bar.setMax(player.getDuration());
        player.start();
        name_audio.setText(name);
        seekUpdation();
        settingsDialog.setContentView(view);
        settingsDialog.show();
        seek_bar.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                player.seekTo(seek_bar.getProgress());
                seekUpdation();
                return true;
            }
        });
        seek_bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser)
                    player.seekTo(progress);
                if (complete[0] && fromUser) {
                    {
                        player.start();
                        player.seekTo(progress);
                        complete[0] = false;
                        seekUpdation();
                        paused[0] = false;
                        play_button.setImageResource(R.drawable.pause);
                    }

                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        settingsDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                player.stop();
            }
        });
        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                complete[0] = true;
                seek_bar.setProgress(seek_bar.getMax());
                paused[0] = true;
                play_button.setImageResource(R.drawable.play);
            }
        });
    }

    Runnable run = new Runnable() {

        @Override
        public void run() {
            seekUpdation();
        }
    };

    public void seekUpdation() {

        seek_bar.setProgress(player.getCurrentPosition());
        seekHandler.postDelayed(run, 1000);
    }
}