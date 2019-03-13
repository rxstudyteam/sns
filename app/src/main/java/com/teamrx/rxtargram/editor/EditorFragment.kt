package com.teamrx.rxtargram.editor

import android.graphics.Bitmap
import android.os.Bundle
import android.util.check
import android.view.*
import androidx.lifecycle.Observer
import com.teamrx.rxtargram.R
import com.teamrx.rxtargram.base.AppFragment
import com.teamrx.rxtargram.databinding.EditorFragmentBinding
import com.teamrx.rxtargram.util.setGlide
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EditorFragment : AppFragment() {
    private lateinit var bb: EditorFragmentBinding

    companion object {
        fun newInstance() = EditorFragment()
    }

    private val viewModel by lazy { getViewModel<EditorViewModel>() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.editor, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.save -> postWrite().run { true }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View = EditorFragmentBinding.inflate(inflater).also { bb = it }.root

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.postImageUrl.observe(this, Observer { url -> bb.postImage.setGlide(url) })
        bb.editorImagePostButton.setOnClickListener { viewModel.getPostImage(requireContext()) }

//        Intent().apply {
//
////            action = ""
////            extras.putChar("sdfsdf") = "sdfsfds"
//        }.also {
//            startActivity( it)
//        }

    }

    private fun postWrite() {
        if (!check())
            return
        postWrite(bb.titleEditText.text.toString(), bb.contextEditText.text.toString(), bb.postImage.getTag(R.id.bitmap) as? Bitmap)
    }

    private fun postWrite(title: String, context: String, image: Bitmap?) = CoroutineScope(Dispatchers.Main).launch {
        showProgress()
        try {
            viewModel.createPost(title, context, image)
            requireActivity().finish()
        } finally {
            dismissProgress()
        }
    }

    private fun check() = bb.titleEditText.check() && bb.contextEditText.check()
}
