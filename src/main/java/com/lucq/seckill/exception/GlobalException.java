package com.lucq.seckill.exception;

import com.lucq.seckill.result.CodeMsg;

public class GlobalException extends RuntimeException {
    private static final long SerialVersionUID = 1L;

    private CodeMsg codeMsg;

    public GlobalException(CodeMsg codeMsg) {
        super(codeMsg.toString());
        this.codeMsg = codeMsg;
    }

    public CodeMsg getCodeMsg() {
        return codeMsg;
    }
}
