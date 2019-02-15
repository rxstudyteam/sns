package com.teamrx.rxtargram.util

import com.teamrx.rxtargram.base.AppApplication

/**
 * Created by Rell on 2019. 1. 22..
 */

fun getString(id: Int): String = AppApplication.context.resources.getString(id)

fun getStringArray(id: Int): MutableList<String> = AppApplication.context.resources.getStringArray(id).toMutableList()
