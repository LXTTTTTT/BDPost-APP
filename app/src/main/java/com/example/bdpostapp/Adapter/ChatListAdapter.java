package com.example.bdpostapp.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bdpostapp.Entity.GsonBean.Receiving.Chat;
import com.example.bdpostapp.R;

import java.util.ArrayList;
import java.util.List;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.MyViewHolder>{

    private String TAG = "ChatListAdapter";
    Context my_context;
    List<Chat> chat_list = new ArrayList<>();  // 用到的操作名称

    public ChatListAdapter(Context context, onItemClickListener onItemClickListener){
        my_context = context;
//        this.chat_list = MainApplication.getInstance().dataViewModel.getChat_list().getValue();
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(my_context).inflate(R.layout.contact_msg_item, parent, false);
        ChatListAdapter.MyViewHolder holder = new ChatListAdapter.MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Chat chat = chat_list.get(position);
        try {
            if(chat.remark!=null && !chat.remark.equals("")){
                holder.remark.setText(chat.remark);
//                if(chat.addr!=null){holder.remark.setText(chat.remark + " (" + chat.addr + ")");}
//                else {holder.remark.setText(chat.remark);}
            }else {
                if(chat.addr!=null){holder.remark.setText(chat.addr);}
            }

            holder.msg_time.setText(chat.chatTimeStr);
            if(chat.chatType.name.equals("TEXT")){
                holder.last_msg.setText("文本消息");
                holder.last_msg.setTextColor(my_context.getColor(R.color.black));
                if(chat.content!=null){holder.last_msg.setText(chat.content);}
            }else if(chat.chatType.name.equals("IMAGE")){
                holder.last_msg.setText("图片消息");
                holder.last_msg.setTextColor(my_context.getColor(R.color.black));
            }else if(chat.chatType.name.equals("VOICE")){
                holder.last_msg.setText("语音消息");
                holder.last_msg.setTextColor(my_context.getColor(R.color.black));
            }else if(chat.chatType.name.equals("ALARM")){
                holder.last_msg.setText("SOS消息");
                holder.last_msg.setTextColor(my_context.getColor(R.color.red_1));
            }else if(chat.chatType.name.equals("OK")){
                holder.last_msg.setText("报平安消息");
                holder.last_msg.setTextColor(my_context.getColor(R.color.green_1));
            }else {
                holder.last_msg.setText("新消息");
                holder.last_msg.setTextColor(my_context.getColor(R.color.black));
            }

            if(chat.unreadNum>0){
                holder.unread_msg.setVisibility(View.VISIBLE);
                holder.unread_msg.setText(chat.unreadNum+"");
            }else {
                holder.unread_msg.setVisibility(View.GONE);
            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    Log.e(TAG, "onClick: 点击" );
                    if(onItemClickListener!=null){onItemClickListener.onItemClickListener(chat.addr, chat.remark);}
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return chat_list.size();
    }


    public void upDateList(List<Chat> chat_list){
        this.chat_list.clear();
        this.chat_list = chat_list;
        notifyDataSetChanged();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder{

        private TextView remark;
        private TextView msg_time;
        private TextView last_msg;
        private TextView unread_msg;

        public MyViewHolder(View itemView) {
            super(itemView);
            remark = itemView.findViewById(R.id.remark);
            msg_time = itemView.findViewById(R.id.msg_time);
            last_msg = itemView.findViewById(R.id.last_msg);
            unread_msg = itemView.findViewById(R.id.unread_msg);

        }

    }

    // 接口 ----------------------------------------------------------
    public interface onItemClickListener{
        void onItemClickListener(String addr,String remark);
    }
    public onItemClickListener onItemClickListener;
    public void setOnItemClickListener(onItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }


}
