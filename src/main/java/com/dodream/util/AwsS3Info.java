package com.dodream.util;

import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class AwsS3Info {

    private String uuidString;
    private String profileName;
    public AwsS3Info() {
    }
}
