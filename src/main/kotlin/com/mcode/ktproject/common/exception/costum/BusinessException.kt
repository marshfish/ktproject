package com.mcode.ktproject.common.exception.costum

import java.lang.RuntimeException

class BusinessException(var desc: String,var  code: Int) : RuntimeException() {

}