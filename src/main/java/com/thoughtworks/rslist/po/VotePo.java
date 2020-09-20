package com.thoughtworks.rslist.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import java.time.LocalDateTime;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "vote")

public class VotePo {
    @Id
    @GeneratedValue
    private int id;

    private String localDateTime;

    protected int num;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserPo userPo;

    @ManyToOne
    @JoinColumn(name = "rs_event_id")
    private RsEventPo rsEventPo;
}
