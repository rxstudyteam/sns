package com.teamrx.rxtargram.detail


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.teamrx.rxtargram.R
import com.teamrx.rxtargram.base.BaseViewModel
import com.teamrx.rxtargram.inject.Injection
import com.teamrx.rxtargram.model.Post
import kotlinx.android.synthetic.main.fragment_detail_view.*

class DetailViewFragment : Fragment() {

    private lateinit var detailViewModel: DetailViewModel
    private lateinit var adapter: PostRecyclerViewAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
            = inflater.inflate(R.layout.fragment_detail_view, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // 컴포넌트 리스너
        context?.let { adapter = PostRecyclerViewAdapter(it) }
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.adapter = adapter
    }

    override fun onResume() {
        super.onResume()

        // 활성화 되었을 때 데이터를 다시 로드 하기 위해 뷰모델 observe
        detailViewModel = getViewModel()
        detailViewModel.getPosts().observe(this, Observer { posts ->
            updateUI(posts)
        })
    }

    private fun updateUI(posts: List<Post>) {
        adapter.addPosts(posts)
    }

    inline fun <reified T : BaseViewModel> getViewModel(): T {
        val viewModelFactory = Injection.provideViewModelFactory()
        return ViewModelProviders.of(this, viewModelFactory).get(T::class.java)
    }

    companion object {
        private var INSTANCE: DetailViewFragment? = null
        fun getInstance() = INSTANCE ?: synchronized(DetailViewFragment::class.java) {
            INSTANCE ?: DetailViewFragment().also { INSTANCE = it }
        }
    }

}
