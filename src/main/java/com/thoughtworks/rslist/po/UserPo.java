package com.thoughtworks.rslist.po;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.util.List;

@Entity
@Table(name = "user")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserPo {
    @Id
    @GeneratedValue
    private int id;
    private String name;
    private String gender;
    private int age;
    private String email;
    private String phone;
    private int voteNum = 10;

    @JsonIgnore
    @OneToMany(cascade = CascadeType.REMOVE,mappedBy = "userPo")
    private List<RsEventPo> rsEventPos;





    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
