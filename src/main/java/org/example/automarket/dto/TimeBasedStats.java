package org.example.automarket.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TimeBasedStats {
    private long total;
    private long approved;
    private long pending;
    private long rejected;
    private long sold;
}
