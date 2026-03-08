package com.blog.service;

import javax.servlet.http.HttpServletRequest;

public interface FeedService {

    String buildSitemap(HttpServletRequest request);

    String buildRss(HttpServletRequest request);
}
