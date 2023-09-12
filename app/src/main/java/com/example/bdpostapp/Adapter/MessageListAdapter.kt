package com.example.bdpostapp.Adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.AnimationDrawable
import android.util.Log
import android.view.*
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.bdpostapp.Base.MainApplication
import com.example.bdpostapp.Entity.GsonBean.Receiving.Message
import com.example.bdpostapp.Global.Constant
import com.example.bdpostapp.R
import com.example.bdpostapp.Utils.AudioTrackUtil
import com.example.bdpostapp.Utils.FilePathUtil
import com.example.bdpostapp.Utils.TaoUtil
import java.text.MessageFormat
import java.util.*

// 消息列表适配器
class MessageListAdapter(context: Context, list: MutableList<Message>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val TAG = "MessageListAdapter"
    var mContext: Context = context
    private var mList: MutableList<Message> = list

    private var mLayoutInflater: LayoutInflater = LayoutInflater.from(mContext)

    private val dataMap: TreeMap<String, Int> = TreeMap<String, Int>()  // 存放消息的索引 [主键:index]

    // glide 图片加载选项
    private var options: RequestOptions = RequestOptions()
        .centerCrop()
        .placeholder(R.mipmap.error_bg)  // 加载成功之前占位图
        .error(R.mipmap.error_bg)  // 加载错误之后的错误图;
    private var options2: RequestOptions = RequestOptions()
        .centerCrop()
        .placeholder(R.mipmap.vector_gaode)  // 加载成功之前占位图
        .error(R.mipmap.vector_gaode)  // 加载错误之后的错误图;
    init {

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
//        Log.e(TAG, "onCreateViewHolder: 消息类型"+ viewType)
        when (viewType) {
            // 文本消息
            Constant.TEXT_MESSAGE -> {
                return ReceiveTextViewHolder(
                    mLayoutInflater.inflate(
                        R.layout.receive_msg_text,
                        parent,
                        false
                    )
                )
            }
            // 语音消息
            Constant.VOICE_MESSAGE -> {
                return ReceiveVideoHolder(
                    mLayoutInflater.inflate(
                        R.layout.receive_msg_voice,
                        parent,
                        false
                    )
                )
            }
            // 图片消息
            Constant.PICTURE_MESSAGE -> {
                return ReceivePicHolder(
                    mLayoutInflater.inflate(
                        R.layout.receive_msg_pic,
                        parent,
                        false
                    )
                )
            }
            // SOS消息
            Constant.SOS_MESSAGE -> {
                return ReceiveSosHolder(
                    mLayoutInflater.inflate(
                        R.layout.receive_msg_sos,
                        parent,
                        false
                    )
                )
            }
            // OK消息
            Constant.OK_MESSAGE -> {
                return ReceiveSafetyHolder(
                    mLayoutInflater.inflate(
                        R.layout.receive_msg_ok,
                        parent,
                        false
                    )
                )
            }
        }
        Log.e(TAG, "onCreateViewHolder: 返回默认 ViewHolder")
        // 默认文本
        return ReceiveTextViewHolder(mLayoutInflater.inflate(R.layout.receive_msg_text, parent, false))
    }



    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        var msg: Message = mList[position]
        when (msg.chatType.name) {
            "TEXT" -> {
                val receiveTextViewHolder = holder as ReceiveTextViewHolder
                receiveTextViewHolder.content.text = msg.content  // 消息内容
                receiveTextViewHolder.sendTime.text = msg.chatTimeStr  // 时间
                msg.loc?.let { val isLoc=setLng(msg.loc.lng.toString(),msg.loc.lat.toString(),receiveTextViewHolder.posTV)}  // 是否显示位置
            }
            "VOICE" -> {
                val receiveVideoHolder = holder as ReceiveVideoHolder
                receiveVideoHolder.sendTime.text = msg.chatTimeStr  // 时间
                receiveVideoHolder.secondsTV.text = MessageFormat.format("{0}″", msg.voiceInfo.sec)  // 秒数
                msg.voiceInfo.enhanceStatus?.let {

                    if(it.name.equals("NOT")){ receiveVideoHolder.isEnhance.visibility = View.GONE; }else{receiveVideoHolder.isEnhance.visibility = View.VISIBLE;}
                }
                // 点击播放
                receiveVideoHolder.audioPlayLL.setOnClickListener {
                    var filePath = FilePathUtil.getVoiceFile()+msg.id + "RECEIVE.wav";
                    playVoice(filePath, receiveVideoHolder.audioPlayImg, position)
                }
            }
            "IMAGE" -> {
                val receivePicHolder = holder as ReceivePicHolder
                receivePicHolder.sendTime.text = msg.chatTimeStr  // 时间
                receivePicHolder.receiveProTv.text="图片大小:"+msg.imageInfo.size+"KB,已接收"+msg.imageInfo.curTotal +"/"+msg.imageInfo.total+"包"
                var url = Constant.BASE_URL + Constant.FILE + msg.imageInfo.fileId
                Glide.with(mContext).load(url).apply(options).into(receivePicHolder.pictureIV)  // 载入图片
            }
            "ALARM" -> {
                val receiveSosHolder = holder as ReceiveSosHolder
//                var url = Constant.BASE_URL + Constant.FILE + msg.imageInfo.fileId
//                Glide.with(mContext).load(url).apply(options2).into(receiveSosHolder.mapView)  // 载入图片
                receiveSosHolder.sendTime?.text = msg.chatTimeStr  // 时间
                // 判断这条报警消息是语音还是文本，现在只有文本
                if(true){
                    // 文本
                    receiveSosHolder.sosInfo?.text=msg.alarmInfo.content
                    receiveSosHolder.sosInfo?.visibility=View.VISIBLE
                    receiveSosHolder.audioPlayLL?.visibility=View.GONE
                    receiveSosHolder.mapView?.visibility=View.GONE
                }else{
                    // 语音
                }
                msg.loc?.let { setLng(msg.loc.lng.toString(),msg.loc.lat.toString(),receiveSosHolder.posTV) }

            }
            "OK" -> {
                val receiveSafetyHolder = holder as ReceiveSafetyHolder
//                var url = Constant.BASE_URL + Constant.FILE + msg.imageInfo.fileId
//                Glide.with(mContext).load(url).apply(options2).into(receiveSafetyHolder.mapView)
                receiveSafetyHolder.sendTime?.text = msg.chatTimeStr  // 时间
                // 判断这条报警消息是语音还是文本，现在只有文本
                if(true){
                    // 文本
                    receiveSafetyHolder.sosInfo?.text=msg.content
                    receiveSafetyHolder.sosInfo?.visibility=View.VISIBLE
                    receiveSafetyHolder.audioPlayLL?.visibility=View.GONE
                    receiveSafetyHolder.mapView?.visibility=View.GONE
                }else{
                    // 语音
                }
                msg.loc?.let {setLng(msg.loc.lng.toString(),msg.loc.lat.toString(),receiveSafetyHolder.posTV)}


            }
        }
    }
    @SuppressLint("SetTextI18n")
    private fun setLng(lng:String?, lat:String?, tv:TextView):Boolean{
        lng?.let {ln->
            lat?.let{la->
                tv.visibility=View.VISIBLE
                if(ln.isEmpty()||la.isEmpty()||ln=="0"||la=="0"){
                    tv.text = "位置" + 0 + "E " + 0 + "N"
                }else{
                    tv.text ="位置" + TaoUtil.changeToDFM(ln.toDouble()) + "E " + TaoUtil.changeToDFM(la.toDouble()) + "N"
                }
                tv.setOnClickListener {
                    if (::onItemClickListener.isInitialized) {
                        onItemClickListener.onItemMapClickListener(it,ln,la)
                    }
                }
                return true
            }
        }
        tv.visibility=View.GONE
        return false
    }


    override fun getItemViewType(position: Int): Int {
        // 判断消息类型
        when(mList[position].chatType.name){
            "TEXT" -> return Constant.TEXT_MESSAGE
            "VOICE" -> return Constant.VOICE_MESSAGE
            "IMAGE" -> return Constant.PICTURE_MESSAGE
            "ALARM" -> return Constant.SOS_MESSAGE
            "OK" -> return Constant.OK_MESSAGE
        }
        return Constant.TEXT_MESSAGE
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }




    @SuppressLint("NotifyDataSetChanged")
    fun notifyData(list: MutableList<Message>) {
        mList = list
        setDataMap()
        notifyDataSetChanged()
    }

    // 接受前一页的数据：把前一页的数据放到前面再加上原有的数据
    fun notifyDataBefore(new_list: MutableList<Message>) {
        var old_list = mutableListOf<Message>();
        old_list.addAll(new_list)
        old_list.addAll(mList)
        mList.clear()
        mList = old_list
        setDataMap()
        notifyDataSetChanged()
    }

    // 改变接收图片状态
    fun notifyReceivedImg(userMsg: Message){
        val pos = dataMap[userMsg.id]
        if(pos==null){
            // 如果列表里面没有代表是新消息：插入
            notifyInsert(userMsg)
        }else{
            // 改变单个
            notifyItemChanged(pos, 0)
        }
    }
    //单条插入刷新
    fun notifyInsert(userMsg: Message):Int {
        mList.also {
            it.add(userMsg)
            val currentIndex=it.size-1
            notifyItemInserted(currentIndex)  // 新增之后 size+1 这时修改最后一条
            dataMap[userMsg.id] = currentIndex  // 添加索引列表
            return currentIndex
        }
    }

    //删除刷新
    fun removeData(userMsg: Message) {
        mList.also {
            dataMap[userMsg.id]?.also { pos ->
                it.removeAt(pos)
                dataMap.remove(userMsg.id)
                notifyItemRemoved(pos)
                notifyItemRangeChanged(pos, it.size)  // 注意要修改范围
            }
        }
    }


    // 根据id获取userMessage
    fun getDataMap(id:String): Message?{
        mList.also {
            dataMap[id]?.also { pos->
                it[pos].also { msg->
                    return msg
                }
            }
            return null
        }
    }

    // 设置消息id对应的索引列表
    fun setDataMap(){
        val size = mList.size
        for (i in 0 until size) {
            dataMap[mList[i].id] = i
        }
    }
    // 获取所有消息数据
    fun getDataList() : MutableList<Message>{
        return mList
    }

    fun clear() {
        mList.clear()
        dataMap.clear()
        notifyDataSetChanged();
    }

    // 滚动到最底部
    fun scrollToBottom() {
        val lastIndex = itemCount - 1
        if (lastIndex >= 0) {
            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            layoutManager.scrollToPositionWithOffset(lastIndex, 0)
        }
    }

    // 设置 RecyclerView
    private lateinit var recyclerView: RecyclerView
    fun setRecyclerView(recyclerView: RecyclerView) {
        this.recyclerView = recyclerView
    }


    // 播放语音
    private var anim: AnimationDrawable? = null
    private var currentVoiceIndex = -1
    private fun playVoice(path: String?, imageView: ImageView, position: Int) {
        if (TaoUtil.isFastClick(1000)) {
            MainApplication.getInstance().showToast("点击过快",0)
            return
        }
        if (AudioTrackUtil.getInstance().isStart) {
            AudioTrackUtil.getInstance().stopPlay()
            if (currentVoiceIndex == position) {
                currentVoiceIndex = -1
                return
            }
        }
        currentVoiceIndex = position
        AudioTrackUtil.getInstance().startPlay(path, object : AudioTrackUtil.PlayListener {
            override fun start() {
                anim = imageView.background as AnimationDrawable
                anim?.start()
            }

            override fun stop() {
                anim?.also {
                    it.selectDrawable(0)
                    it.stop()
                }
            }
        })
    }

    //接收item
    internal class ReceiveTextViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var sendTime:TextView = itemView.findViewById(R.id.send_time)
        var content:TextView = itemView.findViewById(R.id.message_content)
        var posTV:TextView = itemView.findViewById(R.id.posTV)
    }
    internal class ReceiveVideoHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var sendTime:TextView = itemView.findViewById(R.id.send_time)
        var audioPlayLL:LinearLayout = itemView.findViewById(R.id.audioPlayLL)
        var audioPlayImg:ImageView = itemView.findViewById(R.id.audioPlayImg)
        var secondsTV:TextView = itemView.findViewById(R.id.secondsTV)
        var isEnhance:TextView = itemView.findViewById(R.id.isEnhance)
    }
    internal class ReceivePicHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var sendTime:TextView = itemView.findViewById(R.id.send_time)
        var pictureIV:ImageView = itemView.findViewById(R.id.pictureIV)
        var receiveProTv:TextView = itemView.findViewById(R.id.receiveProTv)
    }
    internal class ReceiveSosHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var sendTime:TextView?=null
        var posTV:TextView
        var sosInfo:TextView?=null
        var mapView:ImageView
        var audioPlayLL:LinearLayout?=null
        var secondsTV:TextView?=null
        var audioPlayImg:ImageView?=null
        var msgLL:LinearLayout?=null
        init {
            sendTime=itemView.findViewById(R.id.send_time)
            sosInfo=itemView.findViewById(R.id.sosInfo)
            posTV=itemView.findViewById(R.id.posTV)
            mapView=itemView.findViewById(R.id.mapView)
            audioPlayLL=itemView.findViewById(R.id.audioPlayLL)
            audioPlayLL=itemView.findViewById(R.id.audioPlayLL)
            secondsTV=itemView.findViewById(R.id.secondsTV)
            audioPlayImg=itemView.findViewById(R.id.audioPlayImg)
            msgLL=itemView.findViewById(R.id.msgLL)
        }
    }
    internal class ReceiveSafetyHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var sendTime:TextView?=null
        var posTV:TextView
        var sosInfo:TextView?=null
        var mapView:ImageView
        var audioPlayLL:LinearLayout?=null
        var secondsTV:TextView?=null
        var audioPlayImg:ImageView?=null
        var msgLL:LinearLayout?=null
        init {
            sendTime=itemView.findViewById(R.id.send_time)
            sosInfo=itemView.findViewById(R.id.sosInfo)
            posTV=itemView.findViewById(R.id.posTV)
            mapView=itemView.findViewById(R.id.mapView)
            audioPlayLL=itemView.findViewById(R.id.audioPlayLL)
            audioPlayLL=itemView.findViewById(R.id.audioPlayLL)
            secondsTV=itemView.findViewById(R.id.secondsTV)
            audioPlayImg=itemView.findViewById(R.id.audioPlayImg)
            msgLL=itemView.findViewById(R.id.msgLL)
        }
    }

// 接口（没用到） --------------------------------------------------------------
    fun setOnItemClickListener(listener: OnItemClickListener) {
        onItemClickListener = listener
    }
    private lateinit var onItemClickListener: OnItemClickListener
    interface OnItemClickListener {
        //地图点击事件
        fun onItemMapClickListener(view: View?, longitude: String?, latitude: String?)
        fun viewPic(path: String?,originalPath: String?)
    }


}

