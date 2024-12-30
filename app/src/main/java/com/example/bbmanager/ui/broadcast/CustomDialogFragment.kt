package com.example.bbmanager.ui.broadcast

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bbmanager.R
import com.example.bbmanager.adapters.MessageAdapter
import com.example.bbmanager.data.GPTRequest
import com.example.bbmanager.data.GPTResponse
import com.example.bbmanager.data.network.RetrofitClient
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import retrofit2.Call
import retrofit2.Callback
import androidx.core.view.WindowInsetsCompat
import com.example.bbmanager.data.Message
import com.example.bbmanager.data.network.ChatMessageAdapter
import retrofit2.Response
import com.example.bbmanager.data.network.ChatMessage


class CustomDialogFragment : BottomSheetDialogFragment() {

    private lateinit var gestureDetector: GestureDetector

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.custom_dialog, container, false)

        val layoutInput: View = view.findViewById(R.id.message_input_layout) // 입력창 레이아웃
        val recyclerView: RecyclerView = view.findViewById(R.id.recycler_view_messages)
        val editTextMessage: EditText = view.findViewById(R.id.edit_text_message)
        val sendButton: Button = view.findViewById(R.id.button_send)

        //val messageAdapter = MessageAdapter()
        val messageAdapter = ChatMessageAdapter()
        recyclerView.adapter = messageAdapter
        recyclerView.layoutManager = LinearLayoutManager(context)




        ViewCompat.setOnApplyWindowInsetsListener(view) { _, insets ->
            Log.d("KeyboardTest", "Insets received: ${insets.toString()}")
            val imeHeight = insets.getInsets(WindowInsetsCompat.Type.ime()).bottom
            Log.d("KeyboardTest", "IME Height: $imeHeight")
            val params = layoutInput.layoutParams as ConstraintLayout.LayoutParams
            params.bottomMargin = imeHeight
            layoutInput.layoutParams = params
            Log.d("KeyboardTest", "Bottom margin set to: ${params.bottomMargin}")
            insets
        }


        sendButton.setOnClickListener {
            val userMessage = editTextMessage.text.toString().trim()

            if (userMessage.isNotEmpty()) {
                // 사용자 메시지를 화면에 추가
                messageAdapter.addMessage(ChatMessage(userMessage, true))
                recyclerView.scrollToPosition(messageAdapter.itemCount - 1)
                editTextMessage.text.clear()

                // GPT API 요청 객체 생성
                val gptRequest = GPTRequest(
                    model = "gpt-3.5-turbo",
                    messages = listOf(
                        Message(role = "system", content = "You are a helpful assistant."),
                        Message(role = "user", content = userMessage)
                    )
                )

                // Retrofit을 사용하여 GPT API 호출
                RetrofitClient.apiService.getGPTResponse(gptRequest).enqueue(object : Callback<GPTResponse> {
                    override fun onResponse(call: Call<GPTResponse>, response: Response<GPTResponse>) {
                        try {
                            if (response.isSuccessful) {
                                // GPT 응답을 화면에 추가
                                val reply = response.body()?.choices?.firstOrNull()?.message?.content ?: "No response"
                                messageAdapter.addMessage(ChatMessage(reply, false))
                                recyclerView.scrollToPosition(messageAdapter.itemCount - 1)
                                Log.d("NetworkTest", "Response: $reply")
                            } else {
                                val errorMessage = "Error: ${response.message()}"
                                messageAdapter.addMessage(ChatMessage(errorMessage, false))
                                recyclerView.scrollToPosition(messageAdapter.itemCount - 1)
                                Log.e("NetworkTest", "Error: $errorMessage")
                            }
                        } catch (e: Exception) {
                            val exceptionMessage = "Parsing error: ${e.localizedMessage}"
                            messageAdapter.addMessage(ChatMessage(exceptionMessage, false))
                            Log.e("NetworkTest", exceptionMessage, e)
                        }
                    }

                    override fun onFailure(call: Call<GPTResponse>, t: Throwable) {
                        val networkError = "Network error: ${t.localizedMessage}"
                        messageAdapter.addMessage(ChatMessage(networkError, false))
                        recyclerView.scrollToPosition(messageAdapter.itemCount - 1)
                        Log.e("NetworkTest", networkError, t)
                    }
                })
            }
        }

        return view
    }



    override fun onStart() {
        super.onStart()
        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        // BottomSheetDialog의 높이 설정
        val dialog = dialog as? BottomSheetDialog
        val bottomSheet = dialog?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
        bottomSheet?.let {
            val behavior = BottomSheetBehavior.from(it)
            val displayMetrics = resources.displayMetrics
            val height = (displayMetrics.heightPixels * 0.8).toInt() // 화면 높이의 80%
            it.layoutParams.height = height
            it.requestLayout()
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
        //dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)
    }



}


private fun <T> Call<T>.enqueue(callback: Callback<T>) {
    try {
        this.enqueue(callback)
    } catch (e: Exception) {
        Log.e("NetworkTest", "Enqueue error: ${e.message}")
        callback.onFailure(this, e)
    }
}







