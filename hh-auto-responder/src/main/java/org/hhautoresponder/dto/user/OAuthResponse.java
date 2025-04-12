package org.hhautoresponder.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class OAuthResponse {

    @JsonProperty ("access_token")
    private String accessToken;

    @JsonProperty ("token_type")
    private String tokenType;

    @JsonProperty ("expires_in")
    private Integer expiresIn;

    @JsonProperty ("refresh_token")
    private String refreshToken;
}