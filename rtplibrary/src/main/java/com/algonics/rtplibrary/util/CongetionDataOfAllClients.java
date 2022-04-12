package com.algonics.rtplibrary.util;

import com.algonics.rtsp.utils.CongetionDataRequest;

import java.util.ArrayList;
import java.util.List;

public class CongetionDataOfAllClients {
    public List<CongetionDataRequest> congetionDataList;
    public String timeStamp;
    public CongetionDataOfAllClients(){
        congetionDataList = new ArrayList<CongetionDataRequest>();
    }
}
