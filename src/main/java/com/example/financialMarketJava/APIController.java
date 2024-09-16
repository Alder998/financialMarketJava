package com.example.financialMarketJava;

// Service Class to Handle SQL data (when implemented)

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;

public class APIController {
	
    @Autowired
    private DataClass data;

    // TODO: Delete
    @Transactional
    public String displayStatus(String status) throws Exception {
        return data.displayStatus(status);
    }

}

