package com.herrrau.crawlspace;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class ActivityGameItemsAdapter extends RecyclerView.Adapter<ActivityGameItemsAdapter.ViewHolder> {

    private String[] types;
    private ArrayList<Integer> typeNumbers;
    private ArrayList<Integer> typeAmounts;
    private String[] amounts;
    static ActivityGame activityGame;
    boolean experiment = false; //wird automatisch gesetzt

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;
        private final ImageButton imageButton;
        private final LinearLayout layout;

        public ViewHolder(View view) {
            super(view);
            textView = (TextView) view.findViewById(R.id.textViewNumberItem);
            textView.setTextColor(activityGame.getResources().getColor(R.color.textViewItems));
            imageButton = (ImageButton) view.findViewById(R.id.imageButtonItem);
            layout = (LinearLayout) view.findViewById(R.id.layoutItem);
        }

        public TextView getTextView() {
            return textView;
        }

        public ImageButton getImageButton() {
            return imageButton;
        }

        public LinearLayout getLayout() {
            return layout;
        }
    }

    public ActivityGameItemsAdapter(ActivityGame activityGame, String[] types, String[] amounts) {
        this.types = types;
        this.amounts = amounts;
        this.activityGame = activityGame;
        experiment = false;
    }

    public ActivityGameItemsAdapter(ActivityGame activityGame, ArrayList<Integer> typeNumbers, ArrayList<Integer> amounts) {
        this.typeNumbers = typeNumbers;
        this.typeAmounts = amounts;
        this.activityGame = activityGame;
        experiment = true;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.items_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        if (!experiment) {
            int temp = position;
            if (types[temp].equals("")) {
                //0 items of any kind
                viewHolder.getImageButton().setVisibility(View.GONE);
                viewHolder.getTextView().setText("");
                return;
            }
            ItemType it = ItemType.values()[Integer.parseInt(types[temp])];
            //int id = new FactoryObject().getID(it.toString().toLowerCase());
            int id = new FactoryObject().getID(it.getFileName());
            //image
            viewHolder.getImageButton().setBackgroundResource(id);
            //desc + amount
            //viewHolder.getTextView().setAllCaps(true); // nur zum Testen
            viewHolder.getTextView().setText(it.getName().substring(0, 3) + "\n" + amounts[temp]);
            //listener
            viewHolder.getImageButton().setOnClickListener(new View.OnClickListener() {
                public void onClick(android.view.View v) {
                    activityGame.localController.tryItem(Integer.parseInt(types[temp]));
                }
            });
        } else {
            int temp = position;
            ItemType it = ItemType.values()[typeNumbers.get(temp)];
            //int id = new FactoryObject().getID(it.toString().toLowerCase());
            int id = new FactoryObject().getID(it.getFileName());
            viewHolder.getImageButton().setBackgroundResource(id);
            viewHolder.getTextView().setText(" " + it.getName().substring(0, 2) + " " + typeAmounts.get(temp));
            viewHolder.getLayout().setOnClickListener(new View.OnClickListener() {
                public void onClick(android.view.View v) {
                    activityGame.localController.tryItem(typeNumbers.get(temp));
                }
            });

        }
    }

    @Override
    public int getItemCount() {
        return types.length;
    }
}
