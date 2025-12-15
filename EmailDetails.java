package com.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EmailDetails {

    private List<String> recipient;
    private String msgBody;
    private String subject;
    private String attachment;
}