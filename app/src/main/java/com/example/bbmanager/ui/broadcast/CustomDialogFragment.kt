package com.example.bbmanager.ui.broadcast

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bbmanager.R
import com.example.bbmanager.adapters.MessageAdapter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class CustomDialogFragment : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.custom_dialog, container, false)

        val recyclerView: RecyclerView = view.findViewById(R.id.recycler_view_messages)
        val editTextMessage: EditText = view.findViewById(R.id.edit_text_message)
        val sendButton: Button = view.findViewById(R.id.button_send)

        val messageAdapter = MessageAdapter()
        recyclerView.adapter = messageAdapter
        recyclerView.layoutManager = LinearLayoutManager(context)

        sendButton.setOnClickListener {
            val message = editTextMessage.text.toString()
            if (message.isNotBlank()) {
                messageAdapter.addMessage(message) // 메시지 추가
                recyclerView.scrollToPosition(messageAdapter.itemCount - 1) // 최신 메시지로 스크롤
                editTextMessage.text.clear() // 입력창 초기화
            }
        }
        fun hideKeyboard(context: Context, view: View) {
            val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
        }


        view.setOnTouchListener { _, _ ->
            hideKeyboard(requireContext(), view) // 키보드 숨김
            false // 추가 동작 없음
        }

        // 닫기 버튼 이벤트 처리
        view.findViewById<Button>(R.id.close_button).setOnClickListener {
            dismiss() // 팝업 닫기
        }

        return view
    }



    override fun onStart() {
        super.onStart()

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
        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)
    }
    private fun hideKeyboard(context: Context, view: View) {
        val inputMethodManager =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }


}
