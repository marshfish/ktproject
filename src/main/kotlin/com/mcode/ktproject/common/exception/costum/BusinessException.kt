package com.mcode.ktproject.common.exception.costum

import java.lang.RuntimeException

class BusinessException(var  code: Int,var desc: String) : RuntimeException(desc) {

}
