package com.example.bdpostapp.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.bdpostapp.Entity.GsonBean.Receiving.Group;
import com.example.bdpostapp.Entity.GsonBean.Receiving.Terminal;
import com.example.bdpostapp.R;
import com.example.bdpostapp.Utils.DataUtil;
import com.example.bdpostapp.Utils.MapMarkerUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExpandableListAdapter extends BaseExpandableListAdapter {

    private String TAG = "ExpandableListAdapter";
    private Context context;
    private List<String> groupList;
    private HashMap<String, List<String>> childMap;

    private List<Group> origin_list_data;  // 源数据
    private List<Group> show_list_data = new ArrayList<>();  // 展示的列表数据

    public ExpandableListAdapter(Context context, List<Group> list_data) {
        this.context = context;
        this.origin_list_data = list_data;
        this.show_list_data.clear();
        this.show_list_data.addAll(origin_list_data);
    }

    @Override
    public int getGroupCount() {
        return show_list_data.size();
    }
    @Override
    public Object getGroup(int groupPosition) {
        return show_list_data.get(groupPosition);
    }
    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        Group group = (Group) getGroup(groupPosition);

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.group_item, null);
        }

        TextView groupTextView = convertView.findViewById(R.id.group_text);
        groupTextView.setText(group.name);
        TextView deviceCount = convertView.findViewById(R.id.device_count);
        if(group.terminals!=null){
            deviceCount.setText(group.terminals.size()+"");
        }
//        if(group.terminals==null||group.terminals.size()==0){convertView.setSelected(false);}
        return convertView;
    }
    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        List<Terminal> terminals = show_list_data.get(groupPosition).terminals;
        if(terminals==null || terminals.size()==0){
            return 0;
        }else {
            return terminals.size();
        }
    }
    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return show_list_data.get(groupPosition).terminals.get(childPosition);
    }
    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }
    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        Terminal terminal = (Terminal) getChild(groupPosition,childPosition);
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.child_item, null);
        }

        // 图标
        try{
            ImageView icon = convertView.findViewById(R.id.icon);
            String scope = terminal.scope;
            String status = terminal.status;
            if (MapMarkerUtil.resourceMap.containsKey(scope)) {
                Map<String, Integer> statusMap = MapMarkerUtil.resourceMap.get(scope);
                if (statusMap.containsKey(status)) {
                    icon.setBackgroundResource(statusMap.get(status));
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        // 名称
        try{
            TextView childTextView = convertView.findViewById(R.id.child_text);
            String remark = terminal.remark;
            String addr = terminal.addr;
            if(remark!=null && !remark.equals("")){
                childTextView.setText(remark+ " ("+addr+")");
            }else {
                childTextView.setText(addr);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        // 时间
        try{
            long timeStamp = terminal.lastCommTime;
            TextView time = convertView.findViewById(R.id.time);
            if(timeStamp>0){time.setText(DataUtil.stamp2Date(timeStamp));}
            else {
                timeStamp = Math.abs(timeStamp);
                time.setText(show_last_time(timeStamp));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        // 点击事件
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(onItemClickListener!=null){
                    if(terminal.status.equals(MapMarkerUtil.NOT)) {
                        onItemClickListener.onItemClickListener("NOT");
                    }else {
                        onItemClickListener.onItemClickListener(terminal.addr);
                    }

                }
            }
        });

        return convertView;

    }
    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
//        return getChildrenCount(groupPosition) > 0;
        return true;
    }

    public void setData(List<Group> list_data){
        this.origin_list_data = list_data;
        this.show_list_data.clear();
        this.show_list_data.addAll(origin_list_data);
        notifyDataSetChanged();
    }

    public void searchData(String searchText) {
        Log.e(TAG, "searchData: "+searchText );
        show_list_data.clear(); // 清空显示数据
        Log.e(TAG, "ori: "+origin_list_data.size()+" show:"+show_list_data.size() );
        if(searchText.equals("")){
            show_list_data.addAll(origin_list_data);
        }else {
            for (Group group : origin_list_data) {
                if(group.terminals.size()==0){continue;}
                Group filteredGroup = new Group();
                filteredGroup.name = group.name;
                filteredGroup.terminals = new ArrayList<>();
                for (Terminal terminal : group.terminals) {
                    if (terminal.addr.contains(searchText) || (terminal.remark!=null && terminal.remark.contains(searchText))) {
                        filteredGroup.terminals.add(terminal);
                    }
                }
                show_list_data.add(filteredGroup);
            }
        }

        notifyDataSetChanged(); // 更新适配器数据
    }

    public String show_last_time(long time){

        long time_1 = (System.currentTimeMillis() - time) / 1000;// / 60;  // xxx分钟
        if(time_1 <= 60){
            return "刚刚";
        }else if(time_1 > 60 && time_1 < 3600){
            return time_1/60 + "分钟前";
        }

        else if(time_1 >= 3600 && time_1 < 86400){
            long a = time_1/3600;
            return a+"小时前";
        }else if(time_1 >= 86400 && time_1 < 2592000){
            long b = time_1/86400;
            return b+"天前";
        }else if(time_1 >= 2592000 && time_1 < 31104000){
            long c = time_1/2592000;
            return c+"个月前";
        }else if(time_1 >= 31104000){
            return "一年前";
        }
        return "time";
    }

// 接口 -*---------------------------------------------
    public interface onItemClickListener{
        void onItemClickListener(String addr);
    }
    public onItemClickListener onItemClickListener;
    public void setOnItemClickListener(onItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }

}
