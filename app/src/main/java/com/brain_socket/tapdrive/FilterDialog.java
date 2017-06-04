package com.brain_socket.tapdrive;

import android.app.Dialog;
import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Albert on 4/5/17.
 */
public class FilterDialog extends Dialog {
    private Context context;
    private FilterDiagCallback callback;
    private ArrayList<ArrayList<String>> filters;
    private FiltersContainerAdapter filtersContainerAdapter;
    LayoutInflater inflater;

    public FilterDialog(Context context,FilterDiagCallback callback) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.diag_filter);
        this.context = context;
        this.callback = callback;
        init();
    }

    private void init(){
        inflater = LayoutInflater.from(context);
        initFilters();
        RecyclerView rvFilters = (RecyclerView) findViewById(R.id.rvFilters);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false);
        filtersContainerAdapter = new FiltersContainerAdapter();
        rvFilters.setLayoutManager(linearLayoutManager);
        rvFilters.setAdapter(filtersContainerAdapter);
    }

    private void initFilters(){
        filters = new ArrayList<ArrayList<String>>();

        ArrayList<String> filters1 = new ArrayList<>();
        filters1.add("a1");
        filters1.add("a2");
        filters1.add("a3");
        filters.add(filters1);

        ArrayList<String> filters2 = new ArrayList<>();
        filters2.add("b1");
        filters2.add("b2");
        filters2.add("b3");
        filters.add(filters2);

        ArrayList<String> filters3 = new ArrayList<>();
        filters3.add("c1");
        filters3.add("c2");
        filters3.add("c3");
        filters.add(filters3);

    }


    public interface FilterDiagCallback{
        void onFilterSelected(int price);
    }

    public class ChildFiltersAdapter extends RecyclerView.Adapter<ChildFilterViewHolder> {
        private ArrayList<String> filterComponents;

        public ChildFiltersAdapter(ArrayList<String> filterComponents){
            this.filterComponents = filterComponents;
        }

        @Override
        public ChildFilterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View root = inflater.inflate(R.layout.item_child_filter, parent, false);
            ChildFilterViewHolder viewHolder = new ChildFilterViewHolder(root);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ChildFilterViewHolder holder, int position) {
            try{
                if(filterComponents != null){
                    holder.tvFilterName.setText(filterComponents.get(position));
                }
            }catch (Exception ex){
                ex.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            if(filterComponents == null)
                return 0;
            return filters.size();
        }
    }

    public class FiltersContainerAdapter extends RecyclerView.Adapter<FilterContainerViewHolder> {

        @Override
        public FilterContainerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View root = inflater.inflate(R.layout.row_filter,parent,false);
            FilterContainerViewHolder viewHolder = new FilterContainerViewHolder(root);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(FilterContainerViewHolder holder, int position) {
            try{
                ArrayList<String> filterComponents = filters.get(position);
                holder.rvFilters.setLayoutManager(new LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false));
                ChildFiltersAdapter childFiltersAdapter = new ChildFiltersAdapter(filterComponents);
                holder.rvFilters.setAdapter(childFiltersAdapter);
            }catch (Exception ex){
                ex.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            if(filters != null)
                return filters.size();
            return 0;
        }

        public void updateAdapter(){
            if(filters != null){
                notifyDataSetChanged();
            }
        }
    }

    public class FilterContainerViewHolder extends RecyclerView.ViewHolder{
        public RecyclerView rvFilters;

        public FilterContainerViewHolder(View itemView) {
            super(itemView);
            rvFilters = (RecyclerView) itemView.findViewById(R.id.rvFilters);
        }
    }

    public class ChildFilterViewHolder extends RecyclerView.ViewHolder {
        private TextView tvFilterName;
        private View root;

        public ChildFilterViewHolder(View itemView) {
            super(itemView);
            root = itemView;
            tvFilterName = (TextView) root.findViewById(R.id.tvFilterName);
        }
    }
}
