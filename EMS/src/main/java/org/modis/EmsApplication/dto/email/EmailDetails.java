package org.modis.EmsApplication.dto.email;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EmailDetails {
    private String sender;
    private String recipient;
    private String msgBody;
    private String subject;
}
