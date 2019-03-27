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
//    =======
//    private lateinit var viewModel: EditorViewModel
//
//    val userKey: Array<String> by lazy { resources.getStringArray(R.array.user_keys) }
//
//    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
//        return inflater.inflate(R.layout.editor_fragment, container, false)
//    }
//
//    override fun onActivityCreated(savedInstanceState: Bundle?) {
//        super.onActivityCreated(savedInstanceState)
//
//        val adapter =
//                ArrayAdapter.createFromResource(requireContext(), R.array.user_names, android.R.layout.simple_spinner_item)
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//        user_spinner.setAdapter(adapter)
//        user_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//            override fun onNothingSelected(parent: AdapterView<*>?) {
//
//            }
//
//            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
//                viewModel.userId = userKey[position]
//            }
//        }
//
//        // 이미지 첨부는 어떻게 해야하는 것일까?
//
//        viewModel = ViewModelProviders.of(this, Injection.provideViewModelFactory()).get(EditorViewModel::class.java).apply {
//            postImageUrl.observe(this@EditorFragment, Observer {
//                it?.run { post_image.load(this) }
//            })
//
//        }
//
//
//
//        editor_write_post_button.setOnClickListener {
//            val titleText = title_edit_text.text
//            val contentText = context_edit_text.text
//            if (titleText.isBlank() || contentText.isBlank()) {
//                Toast.makeText(context, "내용을 입력해주세요.", Toast.LENGTH_SHORT).show()
//                return@setOnClickListener
//            }
//
//            val bitmap = post_image.getTag(R.id.icon) as? Bitmap
//            viewModel.createPost(contentText.toString(), null, titleText.toString(), bitmap) {
//                activity?.finish()
//            }
//        }
//
//        editor_image_post_button.setOnClickListener {
//            it?.run {
//                viewModel.getPostImage(post_image)
//            }
//        }
//    }
//
//    //todo super class
//    private inline fun <reified T : BaseViewModel> getViewModel(): T {
//        val viewModelFactory = Injection.provideViewModelFactory()
//        return ViewModelProviders.of(this, viewModelFactory).get(T::class.java)
//    }
//
//    >>>>>>> develop
}
