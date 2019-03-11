package com.teamrx.rxtargram.detail

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.teamrx.rxtargram.R
import com.teamrx.rxtargram.base.BaseViewModel
import com.teamrx.rxtargram.comment.CommentActivity
import com.teamrx.rxtargram.editor.EditorActivity
import com.teamrx.rxtargram.inject.Injection
import com.teamrx.rxtargram.model.Post
import kotlinx.android.synthetic.main.fragment_detail_view.*

class DetailViewFragment : Fragment(), OptionClickListener {

    private lateinit var detailViewModel: DetailViewModel
    private lateinit var adapter: PostRecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.detail_view, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.write -> startActivity(Intent(requireContext(), EditorActivity::class.java)).run { true }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_detail_view, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // 컴포넌트 리스너
        setupRecyclerView()

        // 활성화 되었을 때 데이터를 다시 로드 하기 위해 뷰모델 observe
        setupViewModel()
    }

    private fun setupRecyclerView() {
        adapter = PostRecyclerViewAdapter(requireContext(), this)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.adapter = adapter
    }

    private fun setupViewModel() {
        detailViewModel = getViewModel()
        detailViewModel.getPosts().observe(this, Observer { posts ->
            updateUI(posts)
        })

        detailViewModel.loadPosts()

    }

    private fun updateUI(posts: List<Post>) {
        adapter.setPostDatas(posts)
    }

    private inline fun <reified T : BaseViewModel> getViewModel(): T {
        val viewModelFactory = Injection.provideViewModelFactory()
        return ViewModelProviders.of(this, viewModelFactory).get(T::class.java)
    }

    companion object {
        fun newInstance() = DetailViewFragment()
    }

    @Suppress("EnumEntryName", "UNUSED_PARAMETER", "SpellCheckingInspection", "ClassName")
    enum class ePOPUP(@StringRes resid: kotlin.Int) {
        done(R.string.done),
        share(R.string.share),
        copy_link(R.string.copy_link),
        save(R.string.save),
        modify(R.string.modify),
        delete(R.string.delete);
    }

    override fun onOptionClick(post: Post?) {
        val context = requireContext()
        AlertDialog.Builder(context)
                .setAdapter(ArrayAdapter(context, android.R.layout.simple_list_item_1, ePOPUP.values()))
                { dlg, which ->
                    Toast.makeText(context, "selected ${(dlg as AlertDialog).listView.getItemAtPosition(which)}", Toast.LENGTH_SHORT).show()
                    when (which) {
                        3 -> startModifyActivity(post)
                    }
                }.show()

        AlertDialog.Builder(context).setItems(resources.getStringArray(R.array.post_option)) { dlg, which ->
            Toast.makeText(context, "selected ${(dlg as AlertDialog).listView.getItemAtPosition(which)}", Toast.LENGTH_SHORT).show()
            when (which) {
                3 -> startModifyActivity(post)
            }
        }.show()
    }

    override fun onCommentClick(post_id: String) {
        // comment 목록보기 화면
        val activity = activity ?: return
        CommentActivity.startActivity(activity, post_id)
    }

    private fun startModifyActivity(post: Post?) {
        val intent = Intent(activity?.applicationContext, ModifyActivity::class.java)
        intent.putExtra("post", post)

        activity?.startActivity(intent)
    }
}
