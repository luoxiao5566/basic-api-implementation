package com.thoughtworks.rslist.po;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    @JsonIgnore
    private UserPo userPo;

    @ManyToOne
    @JsonIgnore
    private RsEventPo rsEventPo;
}
