package com.teamrx.rxtargram.editor

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.teamrx.rxtargram.R
import kotlinx.android.synthetic.main.editor_fragment.*

class EditorFragment : Fragment() {

    companion object {
        fun newInstance() = EditorFragment()
    }

    private lateinit var viewModel: EditorViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.editor_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(EditorViewModel::class.java)
        editor_write_post_button.setOnClickListener {
            viewModel.createPost("2nE", null, "첫 번째 api test", "내용은 별거 없지 뭐")
        }
    }

}
