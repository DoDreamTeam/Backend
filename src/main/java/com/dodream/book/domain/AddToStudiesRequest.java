package com.dodream.book.domain;

import java.util.List;
import lombok.Data;

@Data
public class AddToStudiesRequest {
    List<Long> studyIds;
}
