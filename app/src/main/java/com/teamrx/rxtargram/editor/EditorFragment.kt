package com.teamrx.rxtargram.editor

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.lifecycle.Observer
import com.teamrx.rxtargram.R
import com.teamrx.rxtargram.base.BaseViewModel
import com.teamrx.rxtargram.inject.Injection
import com.teamrx.rxtargram.profile.load
import kotlinx.android.synthetic.main.editor_fragment.*

class EditorFragment : Fragment() {

    companion object {
        fun newInstance() = EditorFragment()
    }

    private lateinit var viewModel: EditorViewModel

    val userKey: Array<String> by lazy { resources.getStringArray(R.array.user_keys) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.editor_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


        val adapter =
            ArrayAdapter.createFromResource(requireContext(), R.array.user_names, android.R.layout.simple_spinner_item)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        user_spinner.setAdapter(adapter)
        user_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                viewModel.userId = userKey[position]
            }
        }

        // 이미지 첨부는 어떻게 해야하는 것일까?

        viewModel =
            ViewModelProviders.of(this, Injection.provideViewModelFactory()).get(EditorViewModel::class.java).apply {
                postImageUrl.observe(this@EditorFragment, Observer {
                    it?.run { post_image.load(this) }
                })

            }



        editor_write_post_button.setOnClickListener {
            val titleText = title_edit_text.text
            val contentText = context_edit_text.text
            if (titleText.isBlank() || contentText.isBlank()) {
                return@setOnClickListener
            }
            viewModel.createPost(contentText.toString(), null, titleText.toString())
        }

        editor_image_post_button.setOnClickListener {
            it?.run {
                viewModel.getPostImage(this)
            }
        }
    }

    //todo super class
    private inline fun <reified T : BaseViewModel> getViewModel(): T {
        val viewModelFactory = Injection.provideViewModelFactory()
        return ViewModelProviders.of(this, viewModelFactory).get(T::class.java)
    }

}
