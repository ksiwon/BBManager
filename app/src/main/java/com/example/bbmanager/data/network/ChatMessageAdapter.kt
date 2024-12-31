package com.example.bbmanager.data.network

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.bbmanager.R

class ChatMessageAdapter : RecyclerView.Adapter<ChatMessageAdapter.ViewHolder>() {

    // 메시지 데이터를 저장할 리스트
    private val messages = mutableListOf<ChatMessage>()

    // RecyclerView에 새로운 메시지를 추가하는 메서드
    fun addMessage(message: ChatMessage) {
        messages.add(message)
        notifyItemInserted(messages.size - 1) // 데이터 변경 알림
    }

    // ViewHolder를 생성하는 메서드
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_message, parent, false) // item_message.xml 레이아웃 사용
        return ViewHolder(view)
    }

    // ViewHolder와 데이터를 바인딩하는 메서드
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val message = messages[position]
        holder.bind(message)
    }

    // 데이터 리스트 크기 반환
    override fun getItemCount(): Int = messages.size

    // ViewHolder 클래스 정의
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textMessage: TextView = itemView.findViewById(R.id.text_message) // item_message.xml의 TextView
        private val messageContainer: ConstraintLayout = itemView.findViewById(R.id.message_container) // 메시지 컨테이너

        fun bind(message: ChatMessage) {
            textMessage.text = message.content
            textMessage.setBackgroundResource(
                if (message.isUser) R.drawable.message_send_box // 사용자 메시지 배경
                else R.drawable.message_receive_box             // GPT 메시지 배경
            )

            // 메시지 정렬 설정
            val params = messageContainer.layoutParams as ConstraintLayout.LayoutParams
            if (message.isUser) {
                // 사용자 메시지: 오른쪽 정렬
                params.startToStart = ConstraintLayout.LayoutParams.UNSET
                params.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
                textMessage.gravity = Gravity.END
            } else {
                // GPT 메시지: 왼쪽 정렬
                params.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                params.endToEnd = ConstraintLayout.LayoutParams.UNSET
                textMessage.gravity = Gravity.START
            }
            messageContainer.layoutParams = params

            textMessage.textAlignment = View.TEXT_ALIGNMENT_GRAVITY
        }

    }
}
