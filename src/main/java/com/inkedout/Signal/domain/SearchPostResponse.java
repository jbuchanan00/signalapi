package com.inkedout.Signal.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SearchPostResponse {
    private static final Logger log = LoggerFactory.getLogger(SearchPostResponse.class);
    public Post post;
    public User user;
    public Shop shop;
    public String source;

    @Override
    public boolean equals(Object o){
        log.info("Param {} Obj {}", o, this.user.id);
        if (o == null || getClass() != o.getClass()) return false;

        SearchPostResponse dummyUser = (SearchPostResponse) o;
        return dummyUser.user.id.equals(this.user.id);
    }
}
