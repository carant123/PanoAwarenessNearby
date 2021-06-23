package com.huawei.panoramawarenessnearby.nearbyservice.nearby

import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.huawei.panoramawarenessnearby.R
import com.huawei.panoramawarenessnearby.databinding.MessageBinding
import java.util.*

class ChatAdapter : RecyclerView.Adapter<ChatAdapter.ChatVH>() {

    private var items: ArrayList<MessageBean?>? = ArrayList()

    fun setItems(items: ArrayList<MessageBean?>?) {
        this.items = items
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatVH {
        val inflater = LayoutInflater.from(parent.context)
        val binding: MessageBinding = MessageBinding.inflate(inflater, parent, false)
        return ChatVH(binding)
    }


    override fun onBindViewHolder(holder: ChatVH, position: Int) {
        holder.bind(items!![position])
    }

    override fun getItemCount(): Int {
        return if (items != null) items!!.size else 0
    }

    class ChatVH(binding: MessageBinding) : RecyclerView.ViewHolder(binding.getRoot()) {
        private val binding: MessageBinding = binding
        fun bind(item: MessageBean?) {
            binding.setItem(item)
            if (item!!.isSend()) {
                val params = FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT
                )
                params.gravity = Gravity.END
                val container: ConstraintLayout = binding.container
                container.background = ContextCompat.getDrawable(
                    binding.getRoot().getContext(), R.drawable.purple_rec
                )
                container.layoutParams = params

                //binding.container.foregroundGravity
            }
        }

    }
}