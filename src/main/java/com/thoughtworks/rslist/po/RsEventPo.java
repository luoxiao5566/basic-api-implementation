package com.thoughtworks.rslist.po;

import com.thoughtworks.rslist.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.Valid;
import java.util.List;

@Entity
@Data
@Table(name = "rsEvent")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RsEventPo {
    @Id
    @GeneratedValue
    private int id;
    private String eventName;
    private String keyWord;
    private int voteNum;
    @Valid
    @ManyToOne
    private UserPo userPo;

    @OneToMany
    private List<VotePo> votePos;

}
