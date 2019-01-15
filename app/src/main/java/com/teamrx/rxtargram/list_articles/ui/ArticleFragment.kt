package com.teamrx.rxtargram.list_articles.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.teamrx.rxtargram.R
import com.teamrx.rxtargram.base.AppFragment

class ArticleFragment : AppFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_article, null)

        return view
    }
}