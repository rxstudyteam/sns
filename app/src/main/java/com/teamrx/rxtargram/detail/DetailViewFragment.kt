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
    private lateinit var observer: Observer<List<Post>>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
            = inflater.inflate(R.layout.fragment_detail_view, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        adapter = PostRecyclerViewAdapter(requireContext())
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.adapter = adapter

        if(!::observer.isInitialized) {
            observer = Observer { posts ->
                println("detail viewmodel observe..")
                updateUI(posts)
            }
        }

        detailViewModel = getViewModel()
        detailViewModel.getPosts().observe(requireActivity(), observer)
    }

    private fun updateUI(posts: List<Post>) {
        adapter.addPosts(posts)
    }

    private inline fun <reified T : BaseViewModel> getViewModel(): T {
        val viewModelFactory = Injection.provideViewModelFactory()
        return ViewModelProviders.of(this, viewModelFactory).get(T::class.java)
    }

    companion object {
        fun newInstance() = DetailViewFragment()
    }

}
