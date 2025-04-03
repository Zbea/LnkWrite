package com.bll.lnkwrite.net

import java.io.Serializable


class BaseResult<T> : Serializable {
    var msg: String=""
    var error: String=""
    var code = 0
    var data: T? = null
}