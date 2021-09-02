package com.small.biznez.ResponsePojo;

import lombok.Data;

@Data
public class ResponsePojo<T> {
    String message;
    Boolean success=true;
    T data;
    int status=200;
}
