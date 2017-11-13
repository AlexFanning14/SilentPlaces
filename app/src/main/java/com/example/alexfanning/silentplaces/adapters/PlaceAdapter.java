package com.example.alexfanning.silentplaces.adapters;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.alexfanning.silentplaces.R;
import com.example.alexfanning.silentplaces.SilentPlace;
import com.example.alexfanning.silentplaces.provider.PlaceContract;
import com.google.android.gms.location.places.PlaceBuffer;

/**
 * Created by alex.fanning on 23/10/2017.
 */

public class PlaceAdapter extends RecyclerView.Adapter<PlaceAdapter.PlaceViewHolder> {
    private SilentPlace[] mPlaces;
    private Context mContext;
    private final ListItemClickListener mOnClickListener;

    public interface ListItemClickListener{
        void onListItemClick(SilentPlace place);
    }

    public PlaceAdapter(SilentPlace[] _places, Context _context,ListItemClickListener listener){
        mPlaces= _places;
        mContext = _context;
        mOnClickListener = listener;
    }



    @Override
    public PlaceAdapter.PlaceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layoutIdForGridItem = R.layout.placeitem;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(layoutIdForGridItem,parent,false);
        PlaceViewHolder viewHolder = new PlaceViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(PlaceAdapter.PlaceViewHolder holder, int position) {
        holder.bind(mPlaces[position]);
    }

    @Override
    public int getItemCount() {
        return mPlaces.length;
    }

    class PlaceViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView mTvDescrip;
        private Spinner mSpinner;
        boolean initialDisplay = true;

        public PlaceViewHolder(View itemView){
            super(itemView);

            mTvDescrip = (TextView)itemView.findViewById(R.id.tv_place_item);
            mSpinner = (Spinner)itemView.findViewById(R.id.spinner_options);

            mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                    if (!initialDisplay){
                        SilentPlace sp = mPlaces[getAdapterPosition()];


                        ContentValues cv = new ContentValues();
                        cv.put(PlaceContract.PlaceEntry.COLUMN_PLACE_ID, sp.get_id());
                        cv.put(PlaceContract.PlaceEntry.COLUMN_DESCRIPTION, sp.getDescription());
                        cv.put(PlaceContract.PlaceEntry.COLUMN_SILENT_MODE, position);

                        Uri uri = PlaceContract.PlaceEntry.CONTENT_URI;
                        uri = uri.buildUpon().appendPath(sp.get_id()).build();

                       mContext.getContentResolver().update(uri,cv,null,null);

                    }else{
                        initialDisplay = false;
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });


        }

        void bind(SilentPlace sp){
            mTvDescrip.setText(sp.getDescription());
            populateSpinner();
            mSpinner.setSelection(sp.getSilentMode());
        }

        private void populateSpinner(){
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(mContext,R.array.options_array,R.layout.support_simple_spinner_dropdown_item );
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            mSpinner.setAdapter(adapter);



        }

        @Override
        public void onClick(View view) {
            mOnClickListener.onListItemClick(mPlaces[getAdapterPosition()]);
        }
    }

}
