package com.example.bbmanager.ui.contact

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.*
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.bbmanager.R
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import com.example.bbmanager.Contact
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ContactFragment : Fragment() {

    // ViewModel을 초기화
    private val viewModel: ContactViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true) // 메뉴 사용 설정
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_contact, container, false)
        // 동적 UI를 추가할 컨테이너
        val containerLayout: LinearLayout = view.findViewById(R.id.containerLayout)

        val fabChat = requireActivity().findViewById<FloatingActionButton>(R.id.fab_chat)

        // res/raw/contacts.json에서 데이터를 로드
        viewModel.loadContacts(requireContext())


        // ViewModel의 LiveData를 관찰하여 UI 업데이트
        viewModel.contactGroups.observe(viewLifecycleOwner, Observer { groups ->
            containerLayout.removeAllViews()

            for (group in groups) {
                // 그룹 이름 추가
                val groupTitle = TextView(requireContext()).apply {
                    text = group.groupName
                    textSize = 24f
                    setPadding(16, 32, 16, 32)
                }
                containerLayout.addView(groupTitle)

                // 각 그룹의 연락처 추가
                for (contact in group.contacts) {
                    val cardView = createContactCard(inflater, containerLayout, contact)
                    containerLayout.addView(cardView)
                }
            }
        })

        return view
    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.contact_add_menu, menu) // 메뉴 리소스 파일 연결
    }

    private fun createContactCard(inflater: LayoutInflater, container: ViewGroup, contact: Contact): View {
        val cardView = inflater.inflate(R.layout.contact_card, container, false)
        //Log.d("ContactFragment", "cardView: $cardView, logoImageView: $ImageView")

        // Bind the views
        val titleTextView = cardView.findViewById<TextView>(R.id.titleTextView)
        val phoneNumberTextView = cardView.findViewById<TextView>(R.id.phoneNumberTextView)
        val logoImageView = cardView.findViewById<ImageView>(R.id.logoImageView)
        val callImageView = cardView.findViewById<ImageView>(R.id.callImageView)


        Log.d("ContactFragment", "Contact img: ${contact.img}")
        //val resourceId = imageMapping[contact.img]
        val resourceId = requireContext().resources.getIdentifier(contact.img, "drawable", requireContext().packageName)
        logoImageView.setImageResource(resourceId)

        logoImageView.setOnClickListener{
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(contact.website))
            startActivity(browserIntent)
        }


        callImageView.setOnClickListener {
            val phoneNumber = contact.phone.trim() // 공백 제거
            val cleanedPhoneNumber = phoneNumber.replace(Regex("[^\\d]"), "")
            if (cleanedPhoneNumber.matches(Regex("\\d+"))) { // 숫자로만 이루어진 문자열인지 확인
                val intent = Intent(Intent.ACTION_DIAL).apply {
                    data = Uri.parse("tel:$phoneNumber")
                }
                startActivity(intent)
            } else {
                Toast.makeText(context, "유효하지 않은 전화번호입니다.", Toast.LENGTH_SHORT).show()
            }
        }





        // Set text data
        titleTextView.text = contact.name
        phoneNumberTextView.text = contact.phone
        //val resourceId = requireContext().resources.getIdentifier(contact.img, "drawable", requireContext().packageName)
        //logoImageView.setImageResource(resourceId)

        return cardView
    }


}
