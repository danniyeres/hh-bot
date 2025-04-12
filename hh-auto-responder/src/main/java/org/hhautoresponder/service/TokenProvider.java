package org.hhautoresponder.service;

public interface TokenProvider {
    String getValidAccessToken(Long userId);
}