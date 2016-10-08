package com.example.naruto.az_media_tool.Fragments;


import android.media.MediaMetadataRetriever;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.naruto.az_media_tool.Adapters.Ent_List_Adapter;
import com.example.naruto.az_media_tool.DatabaseHelper.DbBitmapUtility;
import com.example.naruto.az_media_tool.Extras.Ent_item;
import com.example.naruto.az_media_tool.R;
import com.snappydb.DB;
import com.snappydb.DBFactory;
import com.snappydb.SnappydbException;

import java.io.File;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Video_list#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Video_list extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private ArrayList<Ent_item> ent_item_list = new ArrayList<>();
    private ArrayList<File> mvideos = new ArrayList<>();
    private RecyclerView ENT_LIST;
    private Ent_List_Adapter ent_list_adapter;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private DB snappydb = null;

    public Video_list() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Video_list.
     */
    // TODO: Rename and change types and number of parameters
    public static Video_list newInstance(String param1, String param2) {
        Video_list fragment = new Video_list();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        try {
            snappydb = DBFactory.open(getContext());
        } catch (SnappydbException e) {
            e.printStackTrace();
        }
        ArrayList<String> current=new ArrayList<>();
        ArrayList<String> back=new ArrayList<>();
        current.add("/storage/sdcard1/");
        try {
            snappydb.put("Video_current_location",current);
            snappydb.put("Video_back_location",back);
        } catch (SnappydbException e) {
            e.printStackTrace();
        }

        FetchAudioList fetchlist = new FetchAudioList();
        fetchlist.execute();
        setHasOptionsMenu(true);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.location_up_down,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();
        ArrayList<String> back = null;
        ArrayList<String> current = null;
        try {
            current = snappydb.getObject("Video_current_location", ArrayList.class);
            back=snappydb.getObject("Video_back_location",ArrayList.class);

        } catch (SnappydbException e) {
            e.printStackTrace();
        }
        if(id==R.id.backup){

            if(current.size()!=1&&back.size()!=0){
                Log.v("current:",""+current.get(0));
                Log.v("back:",""+back.get(0));
                current.remove(0);
                current.add(0,back.get(0));
                back.remove(0);

                try {
                    snappydb.put("Video_current_location",current);
                    snappydb.put("Video_back_location",back);
                } catch (SnappydbException e) {
                    e.printStackTrace();
                }

                FetchAudioList fetchAudioList=new FetchAudioList();
                fetchAudioList.execute();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_audio__list, container, false);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Videos");
        ENT_LIST = (RecyclerView) view.findViewById(R.id.ENT_List);
        ENT_LIST.setLayoutManager(new GridLayoutManager(this.getActivity(),3));
        ent_list_adapter = new Ent_List_Adapter(getContext(), 1,getActivity());
        ENT_LIST.setAdapter(ent_list_adapter);
        return view;
    }

    public class FetchAudioList extends AsyncTask<Void, Void, ArrayList<Ent_item>> {

        ArrayList<Ent_item> ent_item_lists = new ArrayList<>();
        MediaMetadataRetriever retriver = new MediaMetadataRetriever();
        ArrayList<String> filelock;
        @Override
        protected ArrayList<Ent_item> doInBackground(Void... params) {
            try {
                filelock=snappydb.getObject("Video_current_location",ArrayList.class);
                Log.v("hello:",""+filelock.get(0));
            } catch (SnappydbException e) {
                e.printStackTrace();
            }
            File f = new File(filelock.get(0));
            find_files(f);
            return ent_item_lists;
        }
        public void find_files(File root) {
            try {
                snappydb = DBFactory.open(getContext());
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
                        ent_item_lists.add(ent_item);
                    }
                } else {
                    String filename = singleFile.getName();
                    if (filename.endsWith(".mp4")||filename.endsWith(".mkv")||filename.endsWith(".avi")||filename.endsWith(".3gp")||filename.endsWith(".webm")) {
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
                            ent_item_lists.add(ent_item);
                        } catch (SnappydbException e) {

                        }
                    }

                }
            }

        }

        private boolean have_media_file(File singleFile) {
            File f=singleFile;
            File[] files = f.listFiles();
            for(File singlefile:files){
                if(singlefile.isDirectory()){
                    if(have_media_file(singlefile))
                        return true;
                }else{
                    if(singlefile.getName().endsWith(".mp4")||singlefile.getName().endsWith(".mkv")||singlefile.getName().endsWith(".3gp")||singlefile.getName().endsWith(".avi")||singlefile.getName().endsWith(".webm")){
                        return true;
                    }
                }
            }
            return false;
        }

        @Override
        protected void onPostExecute(ArrayList<Ent_item> ent_items) {
            super.onPostExecute(ent_items);
            ent_list_adapter.notifyDataSetChanged();
            ent_list_adapter.setEnt_list(ent_items);
        }
    }
}
