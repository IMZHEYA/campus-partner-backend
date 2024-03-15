package com.example.CampusPartnerBackend.common;

import lombok.Data;

import java.io.Serializable;
@Data
public class pageRequest implements Serializable {


    private int pageNum;

    private int pageSize;

}
